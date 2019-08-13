package fr.harmonia.tsclientquery.objects;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.answer.Answer;

public class ParsedObject {
	@SuppressWarnings("unchecked")
	public static Map<String, String>[] parseLines(String line) {
		String[] raw = ((line.startsWith("error") ? line.substring("error ".length()) : line).split("\\|"));
		Map<String, String>[] data;

		if (raw.length == 0 || raw[0].isEmpty()) {
			data = new Map[] { new HashMap<>() };
		} else {
			data = new Map[raw.length];

			for (int i = 0; i < data.length; i++)
				data[i] = parseMap(raw[i]);
		}

		return data;
	}

	public static Map<String, String> parseMap(String raw) {
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

	private int index = 0;

	private final String line;

	protected ParsedObject(ParsedObject p) {
		this.line = p.line;
		this.index = p.index;
		this.data = p.data;
	}

	protected ParsedObject(String line) {
		this.line = line;
	}

	/**
	 * 
	 * @param converter
	 * @return
	 */
	protected <R> List<R> convertRowInto(Function<String, R> converter) {
		return Arrays.stream(((line.startsWith("error") ? line.substring("error ".length()) : line).split("\\|")))
				.map(converter).collect(Collectors.toList());
	}

	/**
	 * get a key value
	 * 
	 * @param index
	 *            the row index
	 * @param key
	 *            the key
	 * @return the value, or null if inexistent
	 */
	protected String get(int index, String key) {
		return getData()[index].get(key);
	}

	/**
	 * get a key value for the pointed row
	 * 
	 * @param key
	 *            the key
	 * @return the value, or null if inexistent
	 */
	protected String get(String key) {
		return get(index, key);
	}

	/**
	 * get a key value as a boolean
	 * 
	 * @param index
	 *            the row index
	 * @param key
	 *            the key
	 * @return the value, or false if inexistent
	 */
	protected boolean getBoolean(int index, String key) {
		return getInteger(index, key) != 0;
	}

	/**
	 * get a key value as a boolean for the pointed row
	 * 
	 * @param key
	 *            the key
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
			return this.data = parseLines(line);
		else
			return data;
	}

	/**
	 * get a key value as a integer
	 * 
	 * @param index
	 *            the row index
	 * @param key
	 *            the key
	 * @return the value, or 0 if inexistent
	 */
	protected int getInteger(int index, String key) {
		String value = getData()[index].get(key);
		return value == null || value.isEmpty() ? 0 : Integer.parseInt(value);
	}

	/**
	 * get a key value as a integer for pointed row
	 * 
	 * @param key
	 *            the key
	 * @return the value, or 0 if inexistent
	 */
	protected int getInteger(String key) {
		return getInteger(index, key);
	}

	/**
	 * the data line receive
	 */
	public String getLine() {
		return line;
	}

	/**
	 * get a key value as a long
	 * 
	 * @param index
	 *            the row index
	 * @param key
	 *            the key
	 * @return the value, or 0 if inexistent
	 */
	protected long getLong(int index, String key) {
		String value = getData()[index].get(key);
		return value == null ? 0L : Long.parseLong(value);
	}

	/**
	 * get a key value as a long for pointed row
	 * 
	 * @param key
	 *            the key
	 * @return the value, or 0 if inexistent
	 */
	protected long getLong(String key) {
		return getLong(index, key);
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

	/**
	 * true while the row pointed is empty
	 */
	public boolean rowNotEmpty() {
		return index != getData().length && !data[index].isEmpty();
	}

	/**
	 * get the number of rows returned
	 */
	public int rowsCount() {
		return getData().length;
	}

}
