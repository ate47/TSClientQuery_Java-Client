package fr.harmonia.tsclientquery.answer;

import java.util.HashMap;
import java.util.Map;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.query.Query;

/**
 * a {@link Query} answer, parse returned line into array of maps, to get access
 * by key / index, use an {@link OpenAnswer}
 * 
 * @author ATE47
 *
 */
public class Answer {
	private static Map<String, String> parseMap(String raw) {
		String[] arguments = raw.split(" ");

		Map<String, String> data = new HashMap<String, String>(arguments.length != 0 ? arguments.length : 1);

		for (String arg : arguments) {
			String[] a = arg.split("[=]", 2);
			if (a.length == 2) {
				data.put(TSClientQuery.decodeQueryStringParameter(a[0]),
						TSClientQuery.decodeQueryStringParameter(a[1]));
			} else {
				data.put(TSClientQuery.decodeQueryStringParameter(a[0]), "");
			}
		}

		return data;
	}

	private Map<String, String>[] data;

	private boolean empty;

	private int index = 0;
	private final String line;

	protected Answer(String line) {
		this.line = line;
	}

	/**
	 * get a key value
	 * 
	 * @param index the row index
	 * @param key   the key
	 * @return the value, or null if inexistent
	 */
	protected String get(int index, String key) {
		return getData()[index].get(key);
	}

	/**
	 * get a key value for the pointed row
	 * 
	 * @param key the key
	 * @return the value, or null if inexistent
	 */
	protected String get(String key) {
		return get(index, key);
	}

	/**
	 * get a key value as a boolean
	 * 
	 * @param index the row index
	 * @param key   the key
	 * @return the value, or false if inexistent
	 */
	protected boolean getBoolean(int index, String key) {
		return getInteger(index, key) != 0;
	}

	/**
	 * get a key value as a boolean for the pointed row
	 * 
	 * @param key the key
	 * @return the value, or false if inexistent
	 */
	protected boolean getBoolean(String key) {
		return getBoolean(index, key);
	}

	/**
	 * parse the line if not already done (by asking a value) and return the data
	 * map
	 * 
	 * @return the argument map
	 */
	private Map<String, String>[] getData() {
		if (data == null)
			return this.data = parseLines();
		else
			return data;
	}

	/**
	 * get a key value as a integer
	 * 
	 * @param index the row index
	 * @param key   the key
	 * @return the value, or 0 if inexistent
	 */
	protected int getInteger(int index, String key) {
		String value = getData()[index].get(key);
		return value == null ? 0 : Integer.parseInt(value);
	}

	/**
	 * get a key value as a integer for pointed row
	 * 
	 * @param key the key
	 * @return the value, or 0 if inexistent
	 */
	protected int getInteger(String key) {
		return getInteger(index, key);
	}

	/**
	 * get a key value as a long
	 * 
	 * @param index the row index
	 * @param key   the key
	 * @return the value, or 0 if inexistent
	 */
	protected long getLong(int index, String key) {
		String value = getData()[index].get(key);
		return value == null ? 0L : Long.parseLong(value);
	}

	/**
	 * get a key value as a long for pointed row
	 * 
	 * @param key the key
	 * @return the value, or 0 if inexistent
	 */
	protected long getLong(String key) {
		return getLong(index, key);
	}

	/**
	 * true while the row pointed is empty
	 */
	public boolean rowNotEmpty() {
		return !empty && index != getData().length;
	}

	/**
	 * go to the next row combine with {@link Answer#rowNotEmpty()} to create an
	 * iterator
	 */
	public void next() {
		if (!rowNotEmpty())
			new IllegalArgumentException("No next row");
		++index;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String>[] parseLines() {
		String[] raw = ((line.startsWith("error") ? line.substring("error ".length()) : line).split("\\|"));
		Map<String, String>[] data;

		if (raw.length == 0 || raw[0].isEmpty()) {
			data = new Map[] { new HashMap<>() };
			empty = true;
		} else {
			data = new Map[raw.length];

			for (int i = 0; i < data.length; i++)
				data[i] = parseMap(raw[i]);
			empty = false;
		}

		return data;
	}

	/**
	 * get the number of rows returned
	 */
	public int rowsCount() {
		return getData().length;
	}

}
