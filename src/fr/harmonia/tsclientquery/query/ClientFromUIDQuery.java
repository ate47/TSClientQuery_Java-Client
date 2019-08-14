package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.ClientFromAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;

public class ClientFromUIDQuery extends EventAnswerQuery<ClientFromAnswer> {

	public ClientFromUIDQuery(String uid) {
		super("clientgetnamefromuid", EnumEvent.notifyclientnamefromuid);
		addArgument("cluid", uid);
	}

	@Override
	public void buildAnswer(String line) {
		answer = new ClientFromAnswer(line);
	}

}
