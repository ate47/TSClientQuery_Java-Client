package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.ClientFromAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;

public class ClientFromDatabaseIDQuery extends EventAnswerQuery<ClientFromAnswer> {

	public ClientFromDatabaseIDQuery(int dbid) {
		super("clientgetnamefromdbid", EnumEvent.notifyclientnamefromdbid);
		addArgument("cldbid", dbid);
	}

	@Override
	public void buildAnswer(String line) {
		answer = new ClientFromAnswer(line);
	}

}
