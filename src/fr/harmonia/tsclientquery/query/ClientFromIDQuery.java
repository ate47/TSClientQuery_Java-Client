package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.ClientFromAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;

public class ClientFromIDQuery extends EventAnswerQuery<ClientFromAnswer> {

	public ClientFromIDQuery(int id) {
		super("clientgetuidfromclid", EnumEvent.notifyclientuidfromclid);
		addArgument("clid", id);
	}

	@Override
	public void buildAnswer(String line) {
		answer = new ClientFromAnswer(line);
	}

}
