package fr.harmonia.tsclientquery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import fr.harmonia.tsclientquery.event.EnumEvent;
import fr.harmonia.tsclientquery.query.Answer;
import fr.harmonia.tsclientquery.query.AuthQuery;
import fr.harmonia.tsclientquery.query.Query;
import fr.harmonia.tsclientquery.query.QueryClientNotifyRegister;

public class TSClientQuery {
	private AtomicReference<Query> currentQuery = new AtomicReference<>();
	private BlockingQueue<Query> queue = new LinkedBlockingQueue<Query>();
	private QueryReader reader;
	private QueryWritter writter;
	private Socket socket;
	private AtomicInteger selectedSchandlerid = new AtomicInteger();

	private String apikey;
	private InetAddress address;
	private int port;

	public TSClientQuery(String apikey, InetAddress address, int port) {
		this.apikey = apikey;
		this.address = address;
		this.port = port;
	}

	public TSClientQuery(String apikey) throws UnknownHostException {
		this(apikey, InetAddress.getLocalHost(), 25639);
	}

	public Answer sendQuery(Query query) {
		synchronized (query) {
			queue.add(query);
			try {
				query.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return query.getAnswer();
	}

	private void auth() {
		sendQuery(new AuthQuery(apikey));
	}

	public void start() throws IOException {
		socket = new Socket(address, port);

		reader = new QueryReader(currentQuery, selectedSchandlerid, queue, socket.getInputStream());
		writter = new QueryWritter(currentQuery, queue, socket.getOutputStream());

		reader.start();
		writter.start();

		auth();
	}

	public void registerAllEvents() {
		registerAllEvents(0);
	}

	public void registerAllEvents(int schandlerid) {
		registerEvent(schandlerid, EnumEvent.any);
	}

	public void registerEvent(EnumEvent event) {
		registerEvent(0, event);
	}

	public void registerEvent(int schandlerid, EnumEvent event) {
		sendQuery(new QueryClientNotifyRegister(schandlerid, event));
	}

	public void stop() throws IOException {
		reader.interrupt();
		writter.interrupt();
		socket.close();
	}

}
