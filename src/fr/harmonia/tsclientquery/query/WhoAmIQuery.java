package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.WhoAmIAnswer;

public class WhoAmIQuery extends Query<WhoAmIAnswer> {

	public WhoAmIQuery() {
		super("whoami");
	}

	@Override
	public void buildAnswer(String line) {
		answer = new WhoAmIAnswer(line);
	}

}
