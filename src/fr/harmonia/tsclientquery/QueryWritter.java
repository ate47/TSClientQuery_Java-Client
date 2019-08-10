package fr.harmonia.tsclientquery;

import java.io.OutputStream;
import java.io.PrintStream;

import fr.harmonia.tsclientquery.query.Query;

class QueryWritter extends Thread {
	private TSClientQuery client;

	private OutputStream stream;

	public QueryWritter(TSClientQuery client, OutputStream stream) {
		this.client = client;
		this.stream = stream;
	}

	@Override
	public void run() {
		PrintStream writter = new PrintStream(stream);
		while (!this.isInterrupted()) {
			try {
				Query<?> q = client.queue.take();

				synchronized (client) {
					client.currentQuery = q;

					writter.print(q.createCommand() + "\n\r");

					client.wait();

					client.currentQuery = null;
				}

				synchronized (q) {
					q.notify();
				}
				if (client.floodRate != 0)
					Thread.sleep(client.floodRate);
			} catch (InterruptedException e) {
				break;
			}
		}
		writter.close();
	}
}
