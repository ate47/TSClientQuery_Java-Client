package fr.harmonia.tsclientquery.answer;

import fr.harmonia.tsclientquery.objects.ParsedObject;

public class ClientFromAnswer extends Answer {

	public ClientFromAnswer(ParsedObject obj) {
		super(obj);
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
