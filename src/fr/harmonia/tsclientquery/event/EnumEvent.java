package fr.harmonia.tsclientquery.event;

import fr.harmonia.tsclientquery.query.BanListQuery;
import fr.harmonia.tsclientquery.query.ChannelClientPermListQuery;

public enum EnumEvent {
	/**
	 * use to register all events to a server connection
	 */
	any("any"),

	channellist("channellist"),

	channellistfinished("channellistfinished"),

	/**
	 * use for {@link BanListQuery}
	 */
	notifybanlist("notifybanlist"),

	/**
	 * use for {@link ChannelClientPermListQuery}
	 */
	notifychannelclientpermlist("notifychannelclientpermlist"),

	notifychannelcreated("notifychannelcreated"),

	notifychanneldeleted("notifychanneldeleted"),

	notifychanneledited("notifychanneledited"),

	notifychannelgrouplist("notifychannelgrouplist"),

	notifychannelgrouppermlist("notifychannelgrouppermlist"),

	notifychannelmoved("notifychannelmoved"),

	notifychannelpermlist("notifychannelpermlist"),

	notifyclientchatclosed("notifyclientchatclosed"),

	notifyclientchatcomposing("notifyclientchatcomposing"),

	notifyclientdbidfromuid("notifyclientdbidfromuid"),

	notifycliententerview("notifycliententerview"),

	notifyclientids("notifyclientids"),

	notifyclientleftview("notifyclientleftview"),

	notifyclientmoved("notifyclientmoved"),

	notifyclientnamefromdbid("notifyclientnamefromdbid"),

	notifyclientnamefromuid("notifyclientnamefromuid"),

	/**
	 * @see Handler#onPoke(int, int, String, String, String)
	 */
	notifyclientpoke("notifyclientpoke"),

	notifyclientuidfromclid("notifyclientuidfromclid"),

	notifyclientupdated("notifyclientupdated"),

	notifycomplainlist("notifycomplainlist"),

	notifyconnectioninfo("notifyconnectioninfo"),

	notifyconnectstatuschange("notifyconnectstatuschange"),

	notifycurrentserverconnectionchanged("notifycurrentserverconnectionchanged"),

	notifymessage("notifymessage"),

	notifymessagelist("notifymessagelist"),

	notifyserveredited("notifyserveredited"),

	notifyservergroupclientlist("notifyservergroupclientlist"),

	notifyservergrouplist("notifyservergrouplist"),

	notifyservergrouppermlist("notifyservergrouppermlist"),

	notifyserverupdated("notifyserverupdated"),

	notifytalkstatuschange("notifytalkstatuschange"),

	/**
	 * @see Handler#onMessage(int, int, String, int, String, String)
	 * @see Handler#onPrivateMessage(int, String, int, int, String, String)
	 */
	notifytextmessage("notifytextmessage");

	private String name;

	EnumEvent(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
