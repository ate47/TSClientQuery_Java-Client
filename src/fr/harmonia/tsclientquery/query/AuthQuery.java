package fr.harmonia.tsclientquery.query;

public class AuthQuery extends NoAnswerQuery {

	public AuthQuery(String apikey) {
		super("auth");
		withArgument("apikey", apikey);
	}

}
