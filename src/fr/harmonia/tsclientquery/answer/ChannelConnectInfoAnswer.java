package fr.harmonia.tsclientquery.answer;

public class ChannelConnectInfoAnswer extends Answer {

	public ChannelConnectInfoAnswer(String line) {
		super(line);
	}

	public String getPassword() {
		return get("password");
	}

	public String getPath() {
		return get("path");
	}
}
