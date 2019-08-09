package fr.harmonia.tsclientquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import fr.harmonia.tsclientquery.answer.ErrorAnswer;
import fr.harmonia.tsclientquery.answer.RequireRegisterAnswer;
import fr.harmonia.tsclientquery.query.Query;

class QueryReader extends Thread {
	private AtomicReference<Query<?>> currentQuery = new AtomicReference<>();
	private AtomicInteger selectedSchandlerid;
	private InputStream stream;

	public QueryReader(AtomicReference<Query<?>> currentQuery, AtomicInteger selectedSchandlerid, InputStream stream) {
		this.currentQuery = currentQuery;
		this.selectedSchandlerid = selectedSchandlerid;
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
						Query<?> q = currentQuery.get();
						if (q != null) {
							q.addError(new RequireRegisterAnswer());
							currentQuery.notify();
						}
					}
				} else if (line.startsWith("notify")) {
					// EVENT
				} else if (line.startsWith("selected schandlerid=")) {
					selectedSchandlerid.set(Integer.parseInt(line.substring("selected schandlerid=".length())));
				} else
					synchronized (currentQuery) {
						Query<?> q = currentQuery.get();
						if (q != null) {
							if (line.startsWith("error")) {
								q.addError(new ErrorAnswer(line));
								currentQuery.notify();
							} else
								q.addAnswer(line);
						}
					}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
