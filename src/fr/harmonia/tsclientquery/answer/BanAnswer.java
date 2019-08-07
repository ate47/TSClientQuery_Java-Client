package fr.harmonia.tsclientquery.answer;

public class BanAnswer extends Answer {

	public BanAnswer(String line) {
		super(line);
	}
	
	public int getBanId() {
		return getInteger("banid");
	}

}
