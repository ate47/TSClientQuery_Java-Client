package fr.harmonia.tsclientquery.answer;

@SuppressWarnings("deprecation")
public class BanAnswer extends Answer {

	public BanAnswer(String line) {
		super(line);
	}
	
	public int getBanId() {
		return getInteger("banid");
	}

}
