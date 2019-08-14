package fr.harmonia.tsclientquery.answer;

public class ClientFromAnswer extends Answer {

	public ClientFromAnswer(String line) {
		super(line);
	}

	public String getClientUID() {
		return get("cluid");
	}

	public int getClientDatabaseID() {
		return getInteger("cldbid");
	}

	public String getClientName() {
		return get("name");
	}
}
