package fr.harmonia.tsclientquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import fr.harmonia.tsclientquery.answer.ErrorAnswer;
import fr.harmonia.tsclientquery.answer.OpenAnswer;
import fr.harmonia.tsclientquery.answer.RequireRegisterAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;
import fr.harmonia.tsclientquery.event.Handler;
import fr.harmonia.tsclientquery.objects.Channel;
import fr.harmonia.tsclientquery.objects.Client;
import fr.harmonia.tsclientquery.query.EventAnswerQuery;
import fr.harmonia.tsclientquery.query.Query;

class QueryReader extends Thread {
	private TSClientQuery client;
	private InputStream stream;

	public QueryReader(TSClientQuery client, InputStream stream) {
		this.client = client;
		this.stream = stream;
	}

	private void forEachHandler(Consumer<Handler> h) {
		client.HANDLERS.forEach(h);
	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		try {
			// read info message
			for (int i = 0; i < 6; i++)
				reader.readLine();
			String line;

			while (!this.isInterrupted()) {
				line = reader.readLine();
				System.err.println("line: '" + line + "'");
				if (line == null) {
					client.stop();
					break;
				}

				if (line.isEmpty())
					continue;
				reader.read(); // read '\r'
				synchronized (client.currentQuery) {
					Query<?> q = client.currentQuery.get();
					if (line.startsWith("The command") || line.startsWith("For example")) {
						if (q != null) {
							q.addError(new RequireRegisterAnswer());
							client.currentQuery.notify();
						}
					} else if (line.startsWith("notify")) {
						String[] rows = line.split(" ", 2);
						if (rows.length == 2) {
							try {
								EnumEvent ev = EnumEvent.valueOf(rows[0]);
								OpenAnswer asw = new OpenAnswer(rows[1]);
								int schandlerid = asw.getInteger("schandlerid");

								if (q != null && q instanceof EventAnswerQuery
										&& ((EventAnswerQuery<?>) q).getListenedEvent() == ev
										&& ((EventAnswerQuery<?>) q).getSCHandlerid() == schandlerid) {
									((EventAnswerQuery<?>) q).buildAnswer(asw);
								} else {
									switch (ev) {
									case notifytextmessage: {
										int targetmode = asw.getInteger("targetmode");
										String msg = asw.get("msg");
										int invokerid = asw.getInteger("invokerid");
										String invokername = asw.get("invokername");
										String invokeruid = asw.get("invokeruid");
										switch (targetmode) {
										case 2: // Channel
										case 3: // Server
											forEachHandler(h -> h.onMessage(schandlerid, targetmode, msg, invokerid,
													invokername, invokeruid));
											break;
										case 1: // Client
											int target = asw.getInteger("target");
											forEachHandler(h -> h.onPrivateMessage(schandlerid, msg, target, invokerid,
													invokername, invokeruid));
											break;
										}
									}
										break;
									case notifyclientpoke: {
										String msg = asw.get("msg");
										int invokerid = asw.getInteger("invokerid");
										String invokername = asw.get("invokername");
										String invokeruid = asw.get("invokeruid");
										forEachHandler(
												h -> h.onPoke(schandlerid, invokerid, msg, invokername, invokeruid));
									}
										break;
									case notifycurrentserverconnectionchanged:
										client.selectedSchandlerid.set(schandlerid);
										forEachHandler(h -> h.onChangeCurrentServerConnection(schandlerid));
										break;
									case notifyclientmoved: {
										int channelTargetID = asw.getInteger("ctid");
										int clientID = asw.getInteger("clid");
										switch (asw.getInteger("reasonid")) {
										case 0: { // itself
											forEachHandler(h -> h.onClientMove(schandlerid, channelTargetID, clientID));
										}
											break;
										case 1: { // by other
											int invokerClientID = asw.getInteger("invokerid");
											String invokerName = asw.get("invokername");
											String invokerUID = asw.get("invokeruid");
											client.HANDLERS
													.forEach(h -> h.onClientMovedByOther(schandlerid, channelTargetID,
															invokerClientID, invokerName, invokerUID, invokerClientID));
										}
											break;
										case 4: // kick
										default: {
											int invokerClientID = asw.getInteger("invokerid");
											String invokerName = asw.get("invokername");
											String invokerUID = asw.get("invokeruid");
											String reasonmsg = asw.get("reasonmsg");
											forEachHandler(h -> h.onClientKickFromChannel(schandlerid, channelTargetID,
													invokerClientID, invokerName, invokerUID, reasonmsg,
													invokerClientID));

										}
											break;
										}
									}
										break;
									case notifyclientleftview: {
										int channelTargetID = asw.getInteger("ctid");
										int clientID = asw.getInteger("clid");
										if (channelTargetID != 0) { // move out
											forEachHandler(
													h -> h.onClientLeftView(schandlerid, channelTargetID, clientID));
										} else { // disconnect
											int channelFromID = asw.getInteger("cfid");
											if (asw.getInteger("reasonid") == 5) { // kick server
												int invokerClientID = asw.getInteger("invokerid");
												String invokerName = asw.get("invokername");
												String invokerUID = asw.get("invokeruid");
												String reasonmsg = asw.get("reasonmsg");
												forEachHandler(h -> h.onClientKickFromServer(schandlerid, channelFromID,
														invokerClientID, invokerName, invokerUID, reasonmsg, clientID));
											} else {
												forEachHandler(h -> h.onClientDisconnect(schandlerid, channelFromID,
														clientID));
											}
										}
									}
										break;
									case notifycliententerview: {
										int channelFromID = asw.getInteger("cfid");
										int channelToID = asw.getInteger("ctid");
										Client client = new Client(asw);
										switch (asw.getInteger("reasonid")) {
										case 0: // move or co
											if (channelFromID == 0) { // connection
												forEachHandler(
														h -> h.onClientConnect(schandlerid, channelToID, client));
											} else { // move
												forEachHandler(h -> h.onClientEnterView(schandlerid, channelFromID,
														channelToID, client));
											}
											break;
										case 1: {// moved
											int invokerClientID = asw.getInteger("invokerid");
											String invokerName = asw.get("invokername");
											String invokerUID = asw.get("invokeruid");
											forEachHandler(h -> h.onClientEnterViewMoved(schandlerid, channelFromID,
													channelToID, invokerClientID, invokerName, invokerUID, client));
										}
											break;
										case 4: {// kick
											int invokerClientID = asw.getInteger("invokerid");
											String invokerName = asw.get("invokername");
											String invokerUID = asw.get("invokeruid");
											String reasonmsg = asw.get("reasonmsg");
											forEachHandler(h -> h.onClientKickFromChannelOutOfView(schandlerid,
													channelFromID, channelToID, invokerClientID, invokerName,
													invokerUID, reasonmsg, client));
										}
											break;
										default:
											break;
										}
									}
										break;
									case notifychanneledited: {
										int invokerClientID = asw.getInteger("invokerid");
										String invokerName = asw.get("invokername");
										String invokerUID = asw.get("invokeruid");
										Channel channel = new Channel(asw);
										forEachHandler(h -> h.onChannelEdited(schandlerid, invokerClientID, invokerName,
												invokerUID, channel));
									}
										break;
									case notifychannelcreated: {
										int invokerClientID = asw.getInteger("invokerid");
										String invokerName = asw.get("invokername");
										String invokerUID = asw.get("invokeruid");
										Channel channel = new Channel(asw);
										forEachHandler(h -> h.onChannelCreate(schandlerid, invokerClientID, invokerName,
												invokerUID, channel));
									}
										break;
									case notifychanneldeleted: {
										int invokerClientID = asw.getInteger("invokerid");
										String invokerName = asw.get("invokername");
										String invokerUID = asw.get("invokeruid");
										int channelID = asw.getInteger("cid");
										forEachHandler(h -> h.onChannelDeleted(schandlerid, channelID, invokerClientID,
												invokerName, invokerUID));
									}
										break;
									case notifychannelmoved: {
										int invokerClientID = asw.getInteger("invokerid");
										String invokerName = asw.get("invokername");
										String invokerUID = asw.get("invokeruid");
										Channel channel = new Channel(asw);
										forEachHandler(h -> h.onChannelChangeParentId(schandlerid, invokerClientID,
												invokerName, invokerUID, channel));
									}
										break;
									case channellist:

										break;
									case channellistfinished:

										break;
									case notifybanlist:

										break;
									case notifyclientchatclosed:

										break;
									case notifyclientchatcomposing:

										break;
									case notifyclientids:

										break;
									case notifyclientupdated:

										break;
									case notifycomplainlist:

										break;
									case notifyconnectioninfo:

										break;
									case notifyconnectstatuschange:

										break;
									case notifymessagelist:

										break;
									case notifyserveredited:

										break;
									case notifyserverupdated:

										break;
									case notifytalkstatuschange:

										break;

									case notifymessage:

										break;
									default:
										break;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								continue; // wtf?
							}
						}
					} else if (line.startsWith("selected schandlerid=")) {
						int schandlerid = Integer.parseInt(line.substring("selected schandlerid=".length()));
						client.selectedSchandlerid.updateAndGet(i -> i == 0 ? schandlerid : i);
						client.usedSchandlerid.set(schandlerid);
					} else if (line.startsWith("schandlerid=")) {
						client.selectedSchandlerid.set(Integer.parseInt(line.substring("schandlerid=".length())));
					} else if (q != null) {
						if (line.startsWith("error")) {
							q.addError(new ErrorAnswer(line));
							client.currentQuery.notify();
						} else
							q.buildAnswer(new OpenAnswer(line));
					}
				}
			}

			reader.close();
		} catch (IOException e) {
		}
	}
}
