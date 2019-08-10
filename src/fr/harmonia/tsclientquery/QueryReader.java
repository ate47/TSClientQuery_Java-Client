package fr.harmonia.tsclientquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.harmonia.tsclientquery.answer.Answer;
import fr.harmonia.tsclientquery.answer.ErrorAnswer;
import fr.harmonia.tsclientquery.answer.RequireRegisterAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;
import fr.harmonia.tsclientquery.query.Query;

class QueryReader extends Thread {
	private TSClientQuery client;
	private InputStream stream;

	public QueryReader(TSClientQuery client, InputStream stream) {
		this.client = client;
		this.stream = stream;
	}

	@SuppressWarnings("deprecation")
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
				System.out.println("line: '" + line + "'");
				if (line == null)
					break;

				if (line.isEmpty())
					continue;
				reader.read(); // read '\r'
				if (line.startsWith("The command") || line.startsWith("For example")) {
					synchronized (client) {
						Query<?> q = client.currentQuery;
						if (q != null) {
							q.addError(new RequireRegisterAnswer());
							client.notify();
						}
					}
				} else if (line.startsWith("notify")) {

					String[] rows = line.split(" ", 2);
					if (rows.length == 2) {
						Answer asw = new Answer(rows[1]) {
						};
						int schandlerid = asw.getInteger("schandlerid");
						try {
							switch (EnumEvent.valueOf(rows[0])) {
							case notifytextmessage: {
								int targetmode = asw.getInteger("targetmode");
								String msg = asw.get("msg");
								int invokerid = asw.getInteger("invokerid");
								String invokername = asw.get("invokername");
								String invokeruid = asw.get("invokeruid");
								switch (targetmode) {
								case 2: // Channel
								case 3: // Server
									client.HANDLERS.forEach(h -> h.onMessage(schandlerid, targetmode, msg, invokerid,
											invokername, invokeruid));
									break;
								case 1: // Client
									int target = asw.getInteger("target");
									client.HANDLERS.forEach(h -> h.onPrivateMessage(schandlerid, msg, target, invokerid,
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
								client.HANDLERS
										.forEach(h -> h.onPoke(schandlerid, invokerid, msg, invokername, invokeruid));
							}
								break;
							case channellist:

								break;
							case channellistfinished:

								break;
							case notifybanlist:

								break;
							case notifychannelcreated:

								break;
							case notifychanneldeleted:

								break;
							case notifychanneledited:

								break;
							case notifychannelmoved:

								break;
							case notifyclientchatclosed:

								break;
							case notifyclientchatcomposing:

								break;
							case notifyclientdbidfromuid:

								break;
							case notifycliententerview:

								break;
							case notifyclientids:

								break;
							case notifyclientleftview:

								break;
							case notifyclientmoved:

								break;
							case notifyclientnamefromdbid:

								break;
							case notifyclientnamefromuid:

								break;
							case notifyclientuidfromclid:

								break;
							case notifyclientupdated:

								break;
							case notifycomplainlist:

								break;
							case notifyconnectioninfo:

								break;
							case notifyconnectstatuschange:

								break;
							case notifycurrentserverconnectionchanged:

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
						} catch (Exception e) {
							e.printStackTrace();
							continue; // wtf?
						}
					}
					// EVENT
				} else if (line.startsWith("selected schandlerid=")) {
					client.selectedSchandlerid.set(Integer.parseInt(line.substring("selected schandlerid=".length())));
				} else
					synchronized (client) {
						Query<?> q = client.currentQuery;
						if (q != null) {
							if (line.startsWith("error")) {
								q.addError(new ErrorAnswer(line));
								client.notify();
							} else
								q.buildAnswer(line);
						}
					}
			}

			reader.close();
		} catch (IOException e) {
		}
	}

}
