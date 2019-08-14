package fr.harmonia.tsclientquery.event;

import fr.harmonia.tsclientquery.objects.Client;
import fr.harmonia.tsclientquery.query.BanListQuery;
import fr.harmonia.tsclientquery.query.ChannelClientPermListQuery;
import fr.harmonia.tsclientquery.query.ClientFromDatabaseIDQuery;
import fr.harmonia.tsclientquery.query.ClientFromIDQuery;
import fr.harmonia.tsclientquery.query.ClientFromUIDQuery;

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

	/**
	 * @see Handler#onChannelCreate(int, int, int, String, String,
	 *      fr.harmonia.tsclientquery.objects.Channel)
	 */
	notifychannelcreated("notifychannelcreated"),

	/**
	 * @see Handler#onChannelDeleted(int, int, int, String, String)
	 */
	notifychanneldeleted("notifychanneldeleted"),

	/**
	 * @see Handler#onChannelEdited(int, int, int, String, String,
	 *      fr.harmonia.tsclientquery.objects.Channel)
	 */
	notifychanneledited("notifychanneledited"),

	notifychannelgrouplist("notifychannelgrouplist"),

	notifychannelgrouppermlist("notifychannelgrouppermlist"),

	/**
	 * @see Handler#onChannelChangeParentId(int, int, String, String,
	 *      fr.harmonia.tsclientquery.objects.Channel)
	 */
	notifychannelmoved("notifychannelmoved"),

	notifychannelpermlist("notifychannelpermlist"),

	notifyclientchatclosed("notifyclientchatclosed"),

	notifyclientchatcomposing("notifyclientchatcomposing"),

	notifyclientdbidfromuid("notifyclientdbidfromuid"),

	/**
	 * @see Handler#onClientEnterView(int, int, int, Client)
	 * @see Handler#onClientEnterViewMoved(int, int, int, int, String, String,
	 *      Client)
	 * @see Handler#onClientKickFromChannelOutOfView(int, int, int, int, String,
	 *      String, String, Client)
	 * @see Handler#onClientConnect(int, int, Client)
	 * @see Handler#onClientConnect(int, int, Client)
	 */
	notifycliententerview("notifycliententerview"),

	notifyclientids("notifyclientids"),

	/**
	 * @see Handler#onClientDisconnect(int, int, int)
	 * @see Handler#onClientKickFromChannel(int, int, int, String, String, String,
	 *      int)
	 * @see Handler#onClientKickFromServer(int, int, int, String, String, String,
	 *      int)
	 * @see Handler#onClientLeftView(int, int, int)
	 */
	notifyclientleftview("notifyclientleftview"),

	/**
	 * @see Handler#onClientMove(int, int, int)
	 * @see Handler#onClientDisconnect(int, int, int)
	 * @see Handler#onClientMovedByOther(int, int, int, String, String, int)
	 */
	notifyclientmoved("notifyclientmoved"),

	/**
	 * use for {@link ClientFromDatabaseIDQuery}
	 */
	notifyclientnamefromdbid("notifyclientnamefromdbid"),

	/**
	 * use for {@link ClientFromUIDQuery}
	 */
	notifyclientnamefromuid("notifyclientnamefromuid"),

	/**
	 * @see Handler#onPoke(int, int, String, String, String)
	 */
	notifyclientpoke("notifyclientpoke"),

	/**
	 * use for {@link ClientFromIDQuery}
	 */
	notifyclientuidfromclid("notifyclientuidfromclid"),

	notifyclientupdated("notifyclientupdated"),

	notifycomplainlist("notifycomplainlist"),

	notifyconnectioninfo("notifyconnectioninfo"),

	notifyconnectstatuschange("notifyconnectstatuschange"),

	/**
	 * @see Handler#onChangeCurrentServerConnection(int)
	 */
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
