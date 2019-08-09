package fr.harmonia.tsclientquery.event;

public enum EnumEvent {
	any("any"),

	channellist("channellist"),

	channellistfinished("channellistfinished"),

	notifybanlist("notifybanlist"),

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
