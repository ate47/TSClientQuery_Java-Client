package fr.harmonia.tsclientquery.query;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import fr.harmonia.tsclientquery.answer.Answer;
import fr.harmonia.tsclientquery.answer.ErrorAnswer;

public abstract class Query<T extends Answer> {
	public static String formatQuery(String s) {
		return s.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r").replace(" ", "\\s").replace("/", "\\/")
				.replace("|", "\\p").replace("\b", "\\b").replace("\f", "\\f").replace("\t", "\\t")
				.replace(String.valueOf((char) 7), "\\a").replace(String.valueOf((char) 11), "\\v");
	}

	public static String unformatQuery(String s) {
		return s.replace("\\a", String.valueOf((char) 7)).replace("\\v", String.valueOf((char) 11)).replace("\\r", "\r")
				.replace("\\p", "|").replace("\\f", "\f").replace("\\b", "\b").replace("\\n", "\n").replace("\\r", "\r")
				.replace("\\s", " ").replace("\\r", "\r").replace("\\/", "/").replace("\\\\", "\\");
	}

	protected T answer;
	protected ErrorAnswer error;

	private Map<String, String> data = new HashMap<>();

	private String name;

	public Query(String name) {
		this.name = name;
	}

	public String createCommand() {
		return name + " "
				+ data.entrySet().stream().map(e -> e.getKey() + '=' + e.getValue()).collect(Collectors.joining(" "));
	}

	public Query<T> withArgument(String key, Object value) {
		data.put(formatQuery(key), formatQuery(String.valueOf(value)));
		return this;
	}

	public abstract void addAnswer(String line);

	public void addError(ErrorAnswer error) {
		this.error = error;
	}

	public T getAnswer() {
		return answer;
	}

	public ErrorAnswer getError() {
		return error;
	}

}
