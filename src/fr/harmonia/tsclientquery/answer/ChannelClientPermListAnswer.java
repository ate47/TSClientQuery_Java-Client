package fr.harmonia.tsclientquery.answer;

import java.util.ArrayList;
import java.util.List;

import fr.harmonia.tsclientquery.objects.ParsedObject;
import fr.harmonia.tsclientquery.objects.Permission;

public class ChannelClientPermListAnswer extends Answer {
	private int cid;
	private int cldbid;
	private List<Permission> perms;

	public ChannelClientPermListAnswer(int cid, int cldbid) {
		super("");
		this.cid = cid;
		this.cldbid = cldbid;
		this.perms = new ArrayList<>();
	}

	public void addLine(ParsedObject obj) {
		OpenAnswer oa = new OpenAnswer(obj);
		while (oa.rowNotEmpty())
			perms.add(new Permission(oa.getInteger("permid"), oa.getInteger("permvalue"), oa.getBoolean("permnegated"),
					oa.getBoolean("permskip")));
	}

	public int getChannelID() {
		return cid;
	}

	public int getClientDatabaseID() {
		return cldbid;
	}

	public List<Permission> getPermissions() {
		return perms;
	}
}
