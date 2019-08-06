package fr.harmonia.tsclientquery.query;

public class AuthQuery extends Query {

	public AuthQuery(String apikey) {
		super("auth");
		withArgument("apikey", apikey);
	}

}
