package fr.harmonia.tsclientquery.query;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.answer.Answer;
import fr.harmonia.tsclientquery.answer.ErrorAnswer;

public abstract class Query<T extends Answer> {
	protected T answer;
	protected ErrorAnswer error;

	private Map<String, String> data = new HashMap<>();
	private String option = "";

	private String name;

	public Query(String name) {
		this.name = name;
	}

	public abstract void addAnswer(String line);

	protected void addArgument(String key, Object value) {
		data.put(TSClientQuery.encodeQueryStringParameter(key), TSClientQuery.encodeQueryStringParameter(String.valueOf(value)));
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
