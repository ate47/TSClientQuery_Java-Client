package fr.harmonia.tsclientquery.answer;

import java.util.ArrayList;
import java.util.List;

import fr.harmonia.tsclientquery.objects.DataBaseBan;

public class BanListAnswer extends Answer {
	private List<DataBaseBan> bans;

	public BanListAnswer() {
		super("");
		bans = new ArrayList<>();
	}

	public void addToList(String line) {
		OpenAnswer oa = new OpenAnswer(line);
		while (oa.rowNotEmpty()) {
			bans.add(new DataBaseBan(oa.getInteger("banid"), oa.get("ip"), oa.get("name"), oa.get("uid"),
					oa.get("lastnickname"), oa.getLong("created"), oa.getLong("duration"), oa.get("invokername"),
					oa.getInteger("invokercldbid"), oa.get("invokeruid"), oa.get("reason"),
					oa.getInteger("enforcements")));
			oa.next();
		}
	}

	public List<DataBaseBan> getBanList() {
		return bans;
	}

}
