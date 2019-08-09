package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.BanAnswer;

/*

help banadd
Usage: banadd [ip={regexp}] [name={regexp}] [uid={clientUID}]
       [time={timeInSeconds}] [banreason={text}]

Adds a new ban rule on the selected virtual server. All parameters are optional
but at least one of the following must be set: ip, name, or uid.

Example:
   banadd ip=1.2.3.4 banreason=just\s4\sfun
   banid=1
   error id=0 msg=ok

error id=0 msg=ok

 */
public class BanAddQuery extends Query<BanAnswer> {

	public static class BanAddQueryBuilder {
		private String ip;
		private String regex;
		private String uid;
		private long timeInSeconds = -1L;
		private String banreason;

		public BanAddQuery build() {
			if (ip == null && regex == null && uid == null)
				throw new BanAddQueryNoTargetException();
			return new BanAddQuery(ip, regex, uid, timeInSeconds, banreason);
		}

		public BanAddQueryBuilder withIp(String ip) {
			this.ip = ip;
			return this;
		}

		public BanAddQueryBuilder withName(String regex) {
			this.regex = regex;
			return this;
		}

		public BanAddQueryBuilder withReason(String banreason) {
			this.banreason = banreason;
			return this;
		}

		public BanAddQueryBuilder withTime(long timeInSeconds) {
			this.timeInSeconds = timeInSeconds;
			return this;
		}

		public BanAddQueryBuilder withUID(String uid) {
			this.uid = uid;
			return this;
		}
	}

	public static class BanAddQueryNoTargetException extends IllegalArgumentException {
		private static final long serialVersionUID = 5425099683145168929L;
	}

	private BanAddQuery(String ip, String regex, String uid, long timeInSeconds, String banreason) {
		super("banadd");
		if (ip != null)
			addArgument("ip", ip);
		if (regex != null)
			addArgument("name", regex);
		if (uid != null)
			addArgument("uid", uid);

		if (timeInSeconds >= 0)
			addArgument("time", timeInSeconds);

		if (banreason != null)
			addArgument("banreason", banreason);
	}

	@Override
	public void addAnswer(String line) {
		answer = new BanAnswer(line);
	}

}
