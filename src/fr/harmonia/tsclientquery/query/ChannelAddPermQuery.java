package fr.harmonia.tsclientquery.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
help channeladdperm
Usage: channeladdperm cid={channelID} ( permid={permID}|permsid={permName}
       permvalue={permValue} )...

Adds a set of specified permissions to a channel. Multiple permissions can be
added by providing the two parameters of each permission. A permission can be
specified by permid or permsid.

Example:
   channeladdperm cid=16 permsid=i_client_needed_join_power permvalue=50
   error id=0 msg=ok


error id=0 msg=ok
 */
public class ChannelAddPermQuery extends NoAnswerQuery {
	private class Perm {
		String permsid;
		int permvalue;

		public Perm(String permsid, int permvalue) {
			this.permsid = permsid;
			this.permvalue = permvalue;
		}
	}

	private List<Perm> perms = new ArrayList<>();

	public ChannelAddPermQuery(int channelId, String permsid, int permvalue) {
		super("channeladdperm");
		withArgument("cid", channelId);
		addPerm(permsid, permvalue);
	}

	public ChannelAddPermQuery addPerm(String permsid, int permvalue) {
		perms.add(new Perm(permsid, permvalue));
		return this;
	}

	@Override
	public String createCommand() {
		return super.createCommand() + ' '
				+ perms.stream().map(k -> "permsid=" + formatQuery(k.permsid) + " permvalue=" + k.permvalue)
						.collect(Collectors.joining("|"));
	}

}
