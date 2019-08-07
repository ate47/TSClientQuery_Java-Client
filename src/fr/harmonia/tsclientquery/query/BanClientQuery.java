package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.answer.MultipleBanAnswer;

/*
Usage: banclient clid={clientID}|cldbid={clientDatabaseID}|uid={clientUID} \
                 [time={timeInSeconds}] [banreason={text}]

Bans the client specified with ID clid from the server. Please note that this
will create two separate ban rules for the targeted clients IP address and his
unique identifier.

Note that banning via cldbid parameter requires a 3.0.1 server version.
Note that banning via uid parameter requires a 3.0.2 server version.

Example:
   banclient clid=4 time=3600
   banid=2
   banid=3
   error id=0 msg=ok


error id=0 msg=ok

 */

public class BanClientQuery extends Query<MultipleBanAnswer> {
	public static class BanClientQueryBuilder {
		private int clid;
		private int cldbid;
		private String uid;
		private long timeInSeconds = -1;
		private String banreason;

		public BanClientQuery build() {
			if (clid < 0 && cldbid < 0 && uid == null)
				throw new BanClientQueryNoTargetException();

			return new BanClientQuery(clid, cldbid, uid, timeInSeconds, banreason);
		}

		public BanClientQueryBuilder withCldbid(int cldbid) {
			this.cldbid = cldbid;
			return this;
		}

		public BanClientQueryBuilder withClid(int clid) {
			this.clid = clid;
			return this;
		}

		public BanClientQueryBuilder withReason(String banreason) {
			this.banreason = banreason;
			return this;
		}

		public BanClientQueryBuilder withTime(long timeInSeconds) {
			this.timeInSeconds = timeInSeconds;
			return this;
		}

		public BanClientQueryBuilder withUID(String uid) {
			this.uid = uid;
			return this;
		}
	}

	public static class BanClientQueryNoTargetException extends IllegalArgumentException {
		private static final long serialVersionUID = -420909279135279327L;

	}

	private BanClientQuery(int clid, int cldbid, String uid, long timeInSeconds, String banreason) {
		super("banclient");
		if (clid >= 0)
			withArgument("clid", clid);
		if (cldbid >= 0)
			withArgument("cldbid", cldbid);
		if (timeInSeconds >= 0)
			withArgument("time", timeInSeconds);

		if (uid != null)
			withArgument("uid", uid);
		if (banreason != null)
			withArgument("banreason", banreason);
	}

	@Override
	public void addAnswer(String line) {
		if (answer != null)
			answer.addBan(line);
		else
			answer = new MultipleBanAnswer(line);
	}
}
