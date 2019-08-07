package fr.harmonia.tsclientquery.answer;

import java.util.HashMap;
import java.util.Map;

import fr.harmonia.tsclientquery.query.Query;

public class Answer {

	@SuppressWarnings("unchecked")
	private static Map<String, String>[] parseLines(String line) {
		String[] raw = ((line.startsWith("error") ? line.substring("error ".length()) : line).split("\\|"));
		Map<String, String>[] data;

		if (raw.length == 0) {
			data = new Map[] { new HashMap<>() };

			for (int i = 0; i < data.length; i++)
				data[i] = parseMap(raw[i]);

		} else {
			data = new Map[raw.length];
		}

		return data;
	}

	private static Map<String, String> parseMap(String raw) {
		String[] arguments = raw.split(" ");

		Map<String, String> data = new HashMap<String, String>(arguments.length != 0 ? arguments.length : 1);

		for (String arg : arguments) {
			String[] a = arg.split("[=]", 2);
			if (a.length == 2) {
				data.put(Query.unformatQuery(a[0]), Query.unformatQuery(a[1]));
			} else {
				data.put(Query.unformatQuery(a[0]), "");
			}
		}

		return data;
	}

	private Map<String, String>[] data;
	private final String line;

	public Answer(String line) {
		this.line = line;
	}

	protected String get(int index, String key) {
		return getData()[0].get(key);
	}

	protected String get(String key) {
		return getData()[0].get(key);
	}

	private Map<String, String>[] getData() {
		if (data == null)
			return this.data = parseLines(line);
		else
			return data;
	}

	protected int getInteger(int index, String key) {
		return Integer.parseInt(getData()[index].get(key));
	}

	protected int getInteger(String key) {
		return getInteger(0, key);
	}

}
