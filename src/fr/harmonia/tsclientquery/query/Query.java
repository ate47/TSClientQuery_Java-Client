package fr.harmonia.tsclientquery.query;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Query {
	private Answer answer;

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public Answer getAnswer() {
		return answer;
	}

	public static String formatQuery(String text) {
		return text; // TODO
	}

	public static String unformatQuery(String text) {
		return text; // TODO
	}

	private Map<String, String> data = new HashMap<>();;
	private String name;

	public Query(String name) {
		this.name = name;
	}

	public Query withArgument(String key, Object value) {
		data.put(formatQuery(key), formatQuery(String.valueOf(value)));
		return this;
	}

	public String createCommand() {
		return name + " "
				+ data.entrySet().stream().map(e -> e.getKey() + '=' + e.getValue()).collect(Collectors.joining(" "));
	}

}
