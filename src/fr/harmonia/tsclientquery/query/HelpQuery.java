package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.HelpAnswer;

public class HelpQuery extends Query<HelpAnswer> {

	private String commandName;

	public HelpQuery(String commandName) {
		super("help");
		this.commandName = commandName;
	}

	public HelpQuery() {
		this("");
	}

	@Override
	public String createCommand() {
		return super.createCommand() + ' ' + commandName;
	}

	@Override
	public void addAnswer(String line) {
		if (answer == null)
			answer = new HelpAnswer(line);
		else
			answer.addLine(line);
	}
}
