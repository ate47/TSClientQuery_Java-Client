package fr.harmonia.tsclientquery;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import fr.harmonia.tsclientquery.query.Query;

public class QueryWritter extends Thread {
	private AtomicReference<Query<?>> currentQuery = new AtomicReference<>();
	private BlockingQueue<Query<?>> queue = new LinkedBlockingQueue<Query<?>>();
	private OutputStream stream;

	public QueryWritter(AtomicReference<Query<?>> currentQuery, BlockingQueue<Query<?>> queue, OutputStream stream) {
		this.currentQuery = currentQuery;
		this.queue = queue;
		this.stream = stream;
	}

	@Override
	public void run() {
		PrintStream writter = new PrintStream(stream);
		while (!this.isInterrupted()) {
			try {
				Query<?> q = queue.take();

				synchronized (currentQuery) {
					currentQuery.set(q);

					writter.print(q.createCommand() + "\n\r");
					
					currentQuery.wait();

					currentQuery.set(null);
				}
				
				synchronized (q) {
					q.notify();
				}

			} catch (InterruptedException e) {
				break;
			}
		}
		writter.close();
	}
}
