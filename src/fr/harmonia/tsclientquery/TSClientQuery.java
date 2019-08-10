package fr.harmonia.tsclientquery;

import static fr.harmonia.tsclientquery.exception.QueryException.ERROR_ID_COMMAND_NOT_FOUND;
import static fr.harmonia.tsclientquery.exception.QueryException.ERROR_ID_INSUFFICIENT_CLIENT_PERMISSIONS;
import static fr.harmonia.tsclientquery.exception.QueryException.ERROR_ID_INVALID_PARAMETER;
import static fr.harmonia.tsclientquery.exception.QueryException.ERROR_ID_NOT_CONNECTED;
import static fr.harmonia.tsclientquery.exception.QueryException.ERROR_ID_OK;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import fr.harmonia.tsclientquery.answer.Answer;
import fr.harmonia.tsclientquery.answer.BanAnswer;
import fr.harmonia.tsclientquery.answer.ChannelClientListAnswer;
import fr.harmonia.tsclientquery.answer.ChannelConnectInfoAnswer;
import fr.harmonia.tsclientquery.answer.ErrorAnswer;
import fr.harmonia.tsclientquery.answer.MultipleBanAnswer;
import fr.harmonia.tsclientquery.answer.RequireRegisterAnswer;
import fr.harmonia.tsclientquery.answer.WhoAmIAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;
import fr.harmonia.tsclientquery.event.Handler;
import fr.harmonia.tsclientquery.exception.AlreadyStartedException;
import fr.harmonia.tsclientquery.exception.CommandNotFoundException;
import fr.harmonia.tsclientquery.exception.InsufficientClientPermissionsQueryException;
import fr.harmonia.tsclientquery.exception.InvalidParameterQueryException;
import fr.harmonia.tsclientquery.exception.MessageTooLongException;
import fr.harmonia.tsclientquery.exception.NotConnectedQueryException;
import fr.harmonia.tsclientquery.exception.QueryException;
import fr.harmonia.tsclientquery.exception.UnRegisterQueryException;
import fr.harmonia.tsclientquery.exception.UnStartedClientException;
import fr.harmonia.tsclientquery.exception.WrongAuthKeyException;
import fr.harmonia.tsclientquery.query.AuthQuery;
import fr.harmonia.tsclientquery.query.BanAddQuery;
import fr.harmonia.tsclientquery.query.BanAddQuery.BanAddQueryBuilder;
import fr.harmonia.tsclientquery.query.BanClientQuery;
import fr.harmonia.tsclientquery.query.BanClientQuery.BanClientQueryBuilder;
import fr.harmonia.tsclientquery.query.BanDel;
import fr.harmonia.tsclientquery.query.BanDelAllQuery;
import fr.harmonia.tsclientquery.query.ChannelAddPermQuery;
import fr.harmonia.tsclientquery.query.ChannelClientAddPermQuery;
import fr.harmonia.tsclientquery.query.ChannelClientListQuery;
import fr.harmonia.tsclientquery.query.ChannelConnectInfoQuery;
import fr.harmonia.tsclientquery.query.ChannelCreateQuery;
import fr.harmonia.tsclientquery.query.ChannelCreateQuery.ChannelProperty;
import fr.harmonia.tsclientquery.query.ClientPokeQuery;
import fr.harmonia.tsclientquery.query.HelpQuery;
import fr.harmonia.tsclientquery.query.Query;
import fr.harmonia.tsclientquery.query.QueryClientNotifyRegister;
import fr.harmonia.tsclientquery.query.QueryClientNotifyUnregister;
import fr.harmonia.tsclientquery.query.SendTextMessageQuery;
import fr.harmonia.tsclientquery.query.UseQuery;
import fr.harmonia.tsclientquery.query.WhoAmIQuery;

public class TSClientQuery {
	/**
	 * the max length of a message
	 */
	public static final int MAX_MESSAGE_LENGTH = 8192;
	/**
	 * the max length of a message
	 */
	public static final int MAX_POKE_LENGTH = 100;

	/**
	 * decode with base64
	 * 
	 * @param s what to decode
	 * @return the decoded string
	 */
	public static String decodeBase64(String s) {
		return new String(Base64.getDecoder().decode(s));
	}

	/**
	 * decode a string query
	 * 
	 * @param str string to decode
	 * @return the decoded string
	 */
	public static String decodeQueryStringParameter(String str) {
		return str.replace("\\a", String.valueOf((char) 7)).replace("\\v", String.valueOf((char) 11))
				.replace("\\r", "\r").replace("\\p", "|").replace("\\f", "\f").replace("\\b", "\b").replace("\\n", "\n")
				.replace("\\r", "\r").replace("\\s", " ").replace("\\r", "\r").replace("\\/", "/")
				.replace("\\\\", "\\");
	}

	/**
	 * encode with base64
	 * 
	 * @param s what to encode
	 * @return the encoded string
	 */
	public static String encodeBase64(String s) {
		return new String(Base64.getEncoder().encode(s.getBytes()));
	}

	/**
	 * encode a string query
	 * 
	 * @param str string to encode
	 * @return the encoded string
	 */
	public static String encodeQueryStringParameter(String str) {
		return str.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r").replace(" ", "\\s")
				.replace("/", "\\/").replace("|", "\\p").replace("\b", "\\b").replace("\f", "\\f").replace("\t", "\\t")
				.replace(String.valueOf((char) 7), "\\a").replace(String.valueOf((char) 11), "\\v");
	}

	/**
	 * hash a string baseEncode64(sha1(String))
	 */
	public static String hashPassword(String s) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			byte[] bytes = s.getBytes();
			int len = bytes.length;

			sha1.update(bytes, 0, len);

			return new String(Base64.getEncoder().encode(sha1.digest()));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private final InetAddress address;
	private final int port;
	private final String apikey;

	Query<?> currentQuery;
	long floodRate = 250L;
	final List<Handler> HANDLERS = new ArrayList<>();
	final BlockingQueue<Query<?>> queue = new LinkedBlockingQueue<Query<?>>();
	AtomicInteger selectedSchandlerid = new AtomicInteger();

	private Socket socket;
	private boolean started = false;

	private QueryWritter writter;
	private QueryReader reader;

	/**
	 * create a clientquery to default localhost:25639
	 * 
	 * @param apikey client APIKEY
	 * @throws UnknownHostException if localhost can't be resolved
	 * @see TSClientQuery#TSClientQuery(String, InetAddress, int)
	 */
	public TSClientQuery(String apikey) throws UnknownHostException {
		this(apikey, InetAddress.getLocalHost(), 25639);
	}

	/**
	 * create a clientquery to a non-default address
	 * 
	 * @param apikey  client APIKEY
	 * @param address clientquery server address
	 * @param port    clientquery server port
	 * @see TSClientQuery#TSClientQuery(String)
	 */
	public TSClientQuery(String apikey, InetAddress address, int port) {
		this.apikey = apikey;
		this.address = address;
		this.port = port;
	}

	private boolean auth() {
		try {
			sendQuery(new AuthQuery(apikey));
		} catch (InvalidParameterQueryException e) {
			return false;
		}
		return true;
	}

	/**
	 * send a {@link BanAddQuery} and get the ban ID
	 * 
	 * @param query the query to send
	 * @return the ban ID of the new ban
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 * @see BanAddQueryBuilder
	 */
	public int banAdd(BanAddQuery query) throws InsufficientClientPermissionsQueryException {
		return sendQuery(query).getBanId();
	}

	/**
	 * send a {@link BanClientQuery} and get the ban IDs
	 * 
	 * @param query the query to send
	 * @return the ban IDs of the new bans
	 * @throws QueryException if an error occur
	 * @see BanClientQueryBuilder
	 */
	public int[] banClient(BanClientQuery query) throws InsufficientClientPermissionsQueryException {
		MultipleBanAnswer asw = sendQuery(query);
		return asw.getBanList().stream().mapToInt(BanAnswer::getBanId).toArray();
	}

	/**
	 * send a {@link ChannelAddPermQuery} to a channel (usable to add multiple
	 * permission in one query)
	 * 
	 * @param query the query to send
	 * @return if the permission(s) is(were) added
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public boolean channelAddPerm(ChannelAddPermQuery query) throws InsufficientClientPermissionsQueryException {
		return sendQuery(query).getId() == QueryException.ERROR_ID_OK;
	}

	/**
	 * add a permission to a channel, to add multiple permissions in one query use
	 * {@link TSClientQuery#channelAddPerm(ChannelAddPermQuery)}
	 * 
	 * @param channelId the channel id
	 * @param permid    the perm id
	 * @param permvalue the perm value
	 * @return if the permission is added
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public boolean channelAddPerm(int channelId, int permid, int permvalue)
			throws InsufficientClientPermissionsQueryException {
		return channelAddPerm(new ChannelAddPermQuery(channelId, permid, permvalue));
	}

	/**
	 * add a permission to a channel, to add multiple permissions in one query use
	 * {@link TSClientQuery#channelAddPerm(ChannelAddPermQuery)}
	 * 
	 * @param channelId the channel id
	 * @param permid    the perm name
	 * @param permvalue the perm value
	 * @return if the permission is added
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public boolean channelAddPerm(int channelId, String permName, int permvalue)
			throws InsufficientClientPermissionsQueryException {
		return channelAddPerm(new ChannelAddPermQuery(channelId, permName, permvalue));
	}

	/**
	 * send a {@link ChannelClientAddPermQuery} to a channel (usable to add multiple
	 * permission in one query)
	 * 
	 * @param query the query to change
	 * @return if the permission is added
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public boolean channelClientAddPerm(ChannelClientAddPermQuery query)
			throws InsufficientClientPermissionsQueryException {
		return sendQuery(query).getId() == QueryException.ERROR_ID_OK;
	}

	/**
	 * add a permission to a channel,to add multiple permissions in one query use
	 * {@link TSClientQuery#channelClientAddPerm(ChannelClientAddPermQuery)}
	 * 
	 * @param channelId the channel id
	 * @param cldbid    the client database id
	 * @param permid    the perm id
	 * @param permvalue the perm value
	 * @return if the permission is added
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public boolean channelClientAddPerm(int channelId, int cldbid, int permid, int permvalue)
			throws InsufficientClientPermissionsQueryException {
		return channelClientAddPerm(new ChannelClientAddPermQuery(channelId, cldbid, permid, permvalue));
	}

	/**
	 * add a permission to a channel,to add multiple permissions in one query use
	 * {@link TSClientQuery#channelClientAddPerm(ChannelClientAddPermQuery)}
	 * 
	 * @param channelId the channel id
	 * @param cldbid    the client database id
	 * @param permid    the perm name
	 * @param permvalue the perm value
	 * @return if the permission is added
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public boolean channelClientAddPerm(int channelId, int cldbid, String permName, int permvalue)
			throws InsufficientClientPermissionsQueryException {
		return channelClientAddPerm(new ChannelClientAddPermQuery(channelId, cldbid, permName, permvalue));
	}

	/**
	 * get the client list of a channel (or empty if unsubscribable)
	 * 
	 * @param cid the channel ID
	 * @return the {@link ChannelClientListAnswer}
	 */
	public ChannelClientListAnswer channelClientList(int cid) {
		return sendQuery(new ChannelClientListQuery(cid));
	}

	/**
	 * get the client list of a channel (or empty if unsubscribable)
	 * 
	 * @param cid     the channel ID
	 * @param uid     get client UID?
	 * @param away    get client away?
	 * @param voice   get client voice state?
	 * @param groups  get client groups?
	 * @param icon    get client icon?
	 * @param country get client country?
	 * @return the {@link ChannelClientListAnswer}
	 */
	public ChannelClientListAnswer channelClientList(int cid, boolean uid, boolean away, boolean voice, boolean groups,
			boolean icon, boolean country) {
		return sendQuery(new ChannelClientListQuery(cid, uid, away, voice, groups, icon, country));
	}

	/**
	 * 
	 * get the current channel connection info
	 * 
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public ChannelConnectInfoAnswer channelConnectInfo() throws InsufficientClientPermissionsQueryException {
		return sendQuery(new ChannelConnectInfoQuery());
	}

	/**
	 * get the channel connection info
	 * 
	 * @param cid channel ID
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public ChannelConnectInfoAnswer channelConnectInfo(int cid) throws InsufficientClientPermissionsQueryException {
		return sendQuery(new ChannelConnectInfoQuery(cid));
	}

	private void checkStartedClient() throws UnStartedClientException {
		if (!isStarted())
			throw new UnStartedClientException();
	}

	/**
	 * register an event to a all schandlerid
	 * 
	 * @param event the event to register
	 */
	public void clientNotifyRegister(EnumEvent event) {
		clientNotifyRegister(0, event);
	}

	/**
	 * register an event to a certain schandlerid
	 * 
	 * @param schandlerid the schandlerid (0 for any)
	 * @param event       the event to register
	 */
	public void clientNotifyRegister(int schandlerid, EnumEvent event) {
		sendQuery(new QueryClientNotifyRegister(schandlerid, event));
	}

	/**
	 * register all events to a all schandlerid
	 */
	public void clientNotifyRegisterAll() {
		clientNotifyRegisterAll(0);
	}

	/**
	 * register all events to a certain schandlerid
	 * 
	 * @param schandlerid the schandlerid (0 for any)
	 */
	public void clientNotifyRegisterAll(int schandlerid) {
		clientNotifyRegister(schandlerid, EnumEvent.any);
	}

	/**
	 * unregister all events to a all schandlerid
	 */
	public void clientNotifyUnregisterAll() {
		clientNotifyUnregisterAll(0);
	}

	/**
	 * unregister an event to a all schandlerid
	 * 
	 * @param event the event to unregister
	 */
	public void clientNotifyUnregisterAll(EnumEvent event) {
		clientNotifyRegister(0, event);
	}

	/**
	 * unregister all events to a certain schandlerid
	 * 
	 * @param schandlerid the schandlerid (0 for any)
	 */
	public void clientNotifyUnregisterAll(int schandlerid) {
		clientNotifyUnregisterAll(schandlerid, EnumEvent.any);
	}

	/**
	 * unregister an event to a certain schandlerid
	 * 
	 * @param schandlerid the schandlerid (0 for any)
	 * @param event       the event to unregister
	 */
	public void clientNotifyUnregisterAll(int schandlerid, EnumEvent event) {
		sendQuery(new QueryClientNotifyUnregister(schandlerid, event));
	}

	/**
	 * create a channel
	 * 
	 * @param name       the channel name
	 * @param properties the channel properties
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public void createChannel(String name, ChannelProperty... properties)
			throws InsufficientClientPermissionsQueryException {
		sendQuery(new ChannelCreateQuery(name, properties));
	}

	/**
	 * delete all bans from this server
	 * 
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public void deleteAllBans() throws InsufficientClientPermissionsQueryException {
		sendQuery(new BanDelAllQuery());
	}

	/**
	 * delete a ban
	 * 
	 * @param banid the ban ID
	 * @return if the ban existed and has been deleted
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 */
	public boolean deleteBan(int banid) throws InsufficientClientPermissionsQueryException {
		return sendQuery(new BanDel(banid)).getId() == QueryException.ERROR_ID_OK;
	}

	/**
	 * get the help file for ClientQuery
	 * 
	 * @return the lines of the file
	 */
	public List<String> help() {
		return sendQuery(new HelpQuery()).getLines();
	}

	/**
	 * get the help file for a ClientQuery command
	 * 
	 * @param command the command to search
	 * @return the lines of the file
	 * @throws InvalidParameterQueryException is the command is unknown
	 */
	public List<String> help(String command) throws InvalidParameterQueryException {
		return sendQuery(new HelpQuery(command)).getLines();
	}

	/**
	 * @return if the client is started
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * register an handler
	 * 
	 * @param handler the handler
	 */
	public synchronized void registerHandler(Handler handler) {
		HANDLERS.add(handler);
	}

	/**
	 * 
	 * send a poke to another client
	 * 
	 * @param clientid the client id to sent the poke
	 * @param message  the poke text message
	 * @throws MessageTooLongException                     if the size of the
	 *                                                     message is too long
	 * @throws InsufficientClientPermissionsQueryException if the client hasn't the
	 *                                                     permission to do this
	 * @see TSClientQuery#MAX_POKE_LENGTH
	 */
	public void sendPoke(int clientid, String message)
			throws InsufficientClientPermissionsQueryException, MessageTooLongException {
		if (message.length() > MAX_POKE_LENGTH)
			throw new MessageTooLongException();
		sendQuery(new ClientPokeQuery(message, clientid));
	}

	/**
	 * send a query
	 * 
	 * @param <T>   query answer type
	 * @param query the query to send
	 * @return the answer to this query
	 * @throws CommandNotFoundException                    if the query command
	 *                                                     isn't a valid one
	 * @throws NotConnectedQueryException                  if not connected to a
	 *                                                     server connection
	 * @throws InsufficientClientPermissionsQueryException if the client haven't the
	 *                                                     permission to do this
	 *                                                     command
	 * @throws UnRegisterQueryException                    if this query require to
	 *                                                     register a notify event
	 * @throws InvalidParameterQueryException              if the parameter aren't
	 *                                                     valid
	 */
	public <T extends Answer> T sendQuery(Query<T> query)
			throws CommandNotFoundException, InsufficientClientPermissionsQueryException, UnRegisterQueryException,
			InvalidParameterQueryException, NotConnectedQueryException {
		checkStartedClient();
		synchronized (query) {
			queue.add(query);
			try {
				query.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return (T) null;
			}
		}
		ErrorAnswer err = query.getError();
		if (err instanceof RequireRegisterAnswer)
			throw new UnRegisterQueryException((RequireRegisterAnswer) err);

		switch (err.getId()) {
		case ERROR_ID_COMMAND_NOT_FOUND:
			throw new CommandNotFoundException(err);
		case ERROR_ID_NOT_CONNECTED:
			throw new NotConnectedQueryException(err);
		case ERROR_ID_INVALID_PARAMETER:
			throw new InvalidParameterQueryException(err);
		case ERROR_ID_INSUFFICIENT_CLIENT_PERMISSIONS:
			throw new InsufficientClientPermissionsQueryException(err);
		case ERROR_ID_OK:
		default:
			return query.getAnswer();
		}
	}

	/**
	 * send a message to the current channel chat
	 * 
	 * @param message the text message
	 * @throws MessageTooLongException                     if the size of the
	 *                                                     message is too long
	 * @throws InsufficientClientPermissionsQueryException if the client hasn't the
	 *                                                     permission to do this
	 * @see TSClientQuery#MAX_MESSAGE_LENGTH
	 */
	public void sendTextMessageToChannel(String message)
			throws MessageTooLongException, InsufficientClientPermissionsQueryException {
		if (message.length() > MAX_MESSAGE_LENGTH)
			throw new MessageTooLongException();
		sendQuery(new SendTextMessageQuery(SendTextMessageQuery.TargetMode.CHANNEL, message));
	}

	/**
	 * send a message to another client
	 * 
	 * @param clientid the client id to sent the message
	 * @param message  the text message
	 * @throws MessageTooLongException                     if the size of the
	 *                                                     message is too long
	 * @throws InsufficientClientPermissionsQueryException if the client hasn't the
	 *                                                     permission to do this
	 * @see TSClientQuery#MAX_MESSAGE_LENGTH
	 */
	public void sendTextMessageToClient(int clientid, String message)
			throws MessageTooLongException, InsufficientClientPermissionsQueryException {
		if (message.length() > MAX_MESSAGE_LENGTH)
			throw new MessageTooLongException();
		sendQuery(new SendTextMessageQuery(clientid, message));
	}

	/**
	 * send a message to the current server chat
	 * 
	 * @param message the text message
	 * @throws MessageTooLongException                     if the size of the
	 *                                                     message is too long
	 * @throws InsufficientClientPermissionsQueryException if the client hasn't the
	 *                                                     permission to do this
	 * @see TSClientQuery#MAX_MESSAGE_LENGTH
	 */
	public void sendTextMessageToServer(String message)
			throws InsufficientClientPermissionsQueryException, MessageTooLongException {
		if (message.length() > MAX_MESSAGE_LENGTH)
			throw new MessageTooLongException();
		sendQuery(new SendTextMessageQuery(SendTextMessageQuery.TargetMode.SERVER, message));
	}

	/**
	 * Set the flood rate between every queries, default value is 250
	 * 
	 * @param floodRate the flood rate in millis
	 */
	public synchronized void setFloodRate(long floodRate) {
		if (floodRate < 0)
			throw new IllegalArgumentException("Negative flood rate!");
		this.floodRate = floodRate;
	}

	/**
	 * start the client
	 * 
	 * @throws IOException           if the client can't create a valid socket
	 * @throws WrongAuthKeyException if the apikey is wrong
	 */
	public synchronized void start() throws IOException, WrongAuthKeyException {
		if (started)
			throw new AlreadyStartedException();
		socket = new Socket(address, port);

		currentQuery = null;
		queue.clear();

		reader = new QueryReader(this, socket.getInputStream());
		writter = new QueryWritter(this, socket.getOutputStream());

		reader.start();
		writter.start();

		started = true;

		if (!auth()) {
			stop();
			throw new WrongAuthKeyException();
		}
	}

	/**
	 * stop the client
	 * 
	 * @throws UnStartedClientException if the client isn't started
	 * @throws IOException              if the client can't close the connection
	 */
	public synchronized void stop() throws IOException {
		checkStartedClient();
		reader.interrupt();
		writter.interrupt();
		socket.close();

		try {
			reader.join();
			writter.join();
		} catch (InterruptedException e) {
		} finally {
			started = false;

			reader = null;
			writter = null;
			socket = null;

			currentQuery = null;
			queue.clear();
		}
	}

	/**
	 * unregister all registered handlers
	 */
	public synchronized void unregisterAllHandlers() {
		HANDLERS.clear();
	}

	/**
	 * unregister an handler
	 * 
	 * @param handler the handler
	 */
	public synchronized void unregisterHandler(Handler handler) {
		HANDLERS.remove(handler);
	}

	/**
	 * Selects the currently active server connection handler.
	 */
	public void use() {
		sendQuery(new UseQuery());
	}

	/**
	 * Selects the server connection handler scHandlerID
	 * 
	 * @param scHandlerID the server connection handler
	 */
	public void use(int scHandlerID) {
		sendQuery(new UseQuery(scHandlerID));
	}

	/**
	 * request our ChannelID and our ClientID on this server connection
	 * 
	 * @return a {@link WhoAmIAnswer} with those information
	 */
	public WhoAmIAnswer whoAmI() {
		return sendQuery(new WhoAmIQuery());
	}
}
