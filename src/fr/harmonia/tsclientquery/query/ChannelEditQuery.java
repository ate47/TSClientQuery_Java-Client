package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.objects.ChannelProperty;

public class ChannelEditQuery extends NoAnswerQuery {

	public ChannelEditQuery(int cid, ChannelProperty... properties) {
		super("channeledit");
		addArgument("cid", cid);
		for (ChannelProperty prop : properties)
			addArgument(prop.getProperty().getPropertyName(false), prop.getValue());
	}
}
