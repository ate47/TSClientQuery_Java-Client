package fr.harmonia.tsclientquery.query;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.answer.Answer;
import fr.harmonia.tsclientquery.answer.ErrorAnswer;
import fr.harmonia.tsclientquery.objects.ParsedObject;

public abstract class Query<T extends Answer> {
	protected T answer;
	protected ErrorAnswer error;

	private Map<String, String> data = new HashMap<>();
	private String option = "";

	private String name;

	public Query(String name) {
		this.name = name;
	}

	public abstract void buildAnswer(ParsedObject obj);

	protected void addArgument(String key, Object value) {
		String k = TSClientQuery.encodeQueryStringParameter(key);
		String v = value instanceof Boolean ? (((Boolean) value).booleanValue() ? "1" : "0")
				: value instanceof Number ? String.valueOf(value)
						: TSClientQuery.encodeQueryStringParameter(String.valueOf(value));
		data.put(k, v);
	}

	public void addError(ErrorAnswer error) {
		this.error = error;
	}

	protected void addOption(String opt) {
		option += ' ' + opt;
	}

	public String createCommand() {
		return name + " "
				+ data.entrySet().stream().map(e -> e.getKey() + '=' + e.getValue()).collect(Collectors.joining(" "))
				+ option;
	}

	public T getAnswer() {
		return answer;
	}

	public ErrorAnswer getError() {
		return error;
	}

}
