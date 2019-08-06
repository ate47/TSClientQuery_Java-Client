package fr.harmonia.tsclientquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import fr.harmonia.tsclientquery.query.Query;

public class QueryReader extends Thread {
	private AtomicReference<Query> currentQuery = new AtomicReference<>();
	private AtomicInteger selectedSchandlerid;
	private BlockingQueue<Query> queue = new LinkedBlockingQueue<Query>();
	private InputStream stream;

	public QueryReader(AtomicReference<Query> currentQuery, AtomicInteger selectedSchandlerid,
			BlockingQueue<Query> queue, InputStream stream) {
		this.currentQuery = currentQuery;
		this.queue = queue;
		this.stream = stream;
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
				System.out.println("line: '" + line + "'");
				if (line == null)
					break;

				if (line.isEmpty())
					continue;
				reader.read(); // read '\r'
				if (line.startsWith("The command") || line.startsWith("For example")) {
					synchronized (currentQuery) {
						Query q = currentQuery.get();
						if (q != null) {

							// TODO: UNREGISTERED COMMAND

							currentQuery.notify();
						}
					}
				} else if (line.startsWith("notify")) {
					// EVENT
				} else if (line.startsWith("selected schandlerid=")) {
					selectedSchandlerid.set(Integer.parseInt(line.substring("selected schandlerid=".length())));
				} else
					synchronized (currentQuery) {
						Query q = currentQuery.get();
						if (q != null) {
							// TODO: ANSWER

							if (line.startsWith("error"))
								currentQuery.notify();
						}
					}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
