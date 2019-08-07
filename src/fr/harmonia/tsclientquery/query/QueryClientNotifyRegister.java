package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.event.EnumEvent;

public class QueryClientNotifyRegister extends NoAnswerQuery {

	public QueryClientNotifyRegister(int schandlerid, EnumEvent event) {
		super("clientnotifyregister");
		withArgument("schandlerid", schandlerid);
		withArgument("event", event);
	}

}
