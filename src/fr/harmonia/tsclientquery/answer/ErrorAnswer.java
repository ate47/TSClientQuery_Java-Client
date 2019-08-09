package fr.harmonia.tsclientquery.answer;

/*
 * error id=0 msg=ok
 */
@SuppressWarnings("deprecation")
public class ErrorAnswer extends Answer {
	
	public ErrorAnswer(String line) {
		super(line);
	}

	public int getId() {
		return getInteger("id");
	}

	public String getMsg() {
		return get("msg");
	}

}
