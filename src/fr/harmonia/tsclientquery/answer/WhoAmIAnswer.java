package fr.harmonia.tsclientquery.answer;

@SuppressWarnings("deprecation")
public class WhoAmIAnswer extends Answer {

	public WhoAmIAnswer(String line) {
		super(line);
	}
	
	public int getChannelID() {
		return getInteger("cid");
	}
	public int getClientID() {
		return getInteger("clid");
	}

}
