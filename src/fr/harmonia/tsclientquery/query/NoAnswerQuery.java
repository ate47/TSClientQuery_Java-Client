package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.ErrorAnswer;

public class NoAnswerQuery extends Query<ErrorAnswer> {
	public static class NoAnswerQueryAnswerException extends IllegalArgumentException {

		private static final long serialVersionUID = -451426683223489820L;
		
	}
	public NoAnswerQuery(String name) {
		super(name);
	}

	@Override
	public void addAnswer(String line) {
		throw new NoAnswerQueryAnswerException();
	}

	@Override
	public ErrorAnswer getAnswer() {
		return getError();
	}

}
