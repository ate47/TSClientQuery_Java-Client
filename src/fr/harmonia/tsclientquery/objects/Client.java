package fr.harmonia.tsclientquery.objects;

public class Client extends ChannelClient {
	private String client_meta_data, client_talk_request_msg, client_description, client_nickname_phonetic,
			client_myteamspeak_id, client_myteamspeak_avatar, client_signed_badges, client_badges;
	private boolean client_outputonly_muted, client_flag_avatar, client_talk_request;
	private int client_unread_messages, client_needed_serverquery_view_power, client_channel_group_inherited_channel_id,
			client_integrations;
	public Client(ParsedObject object) {
		super(object);
		fetch();
	}
	public Client(String line) {
		super(line);
		fetch();
	}
	private void fetch() {
		client_meta_data = get("client_meta_data");
		client_talk_request_msg = get("client_talk_request_msg");
		client_description = get("client_description");
		client_nickname_phonetic = get("client_nickname_phonetic");
		client_myteamspeak_id = get("client_myteamspeak_id");
		client_myteamspeak_avatar = get("client_myteamspeak_avatar");
		client_signed_badges = get("client_signed_badges");
		client_badges = get("client_badges");

		client_outputonly_muted = getBoolean("client_outputonly_muted");
		client_flag_avatar = getBoolean("client_flag_avatar");
		client_talk_request = getBoolean("client_talk_request");

		client_unread_messages = getInteger("client_unread_messages");
		client_needed_serverquery_view_power = getInteger("client_needed_serverquery_view_power");
		client_channel_group_inherited_channel_id = getInteger("client_channel_group_inherited_channel_id");
		client_integrations = getInteger("client_integrations");
	}
	

	public String getClientBadges() {
		return client_badges;
	}

	public int getClientChannelGroupInheritedChannelId() {
		return client_channel_group_inherited_channel_id;
	}

	public String getClientDescription() {
		return client_description;
	}

	public int getClientIntegrations() {
		return client_integrations;
	}

	public String getClientMetaData() {
		return client_meta_data;
	}

	public String getClientMyTeamspeakAvatar() {
		return client_myteamspeak_avatar;
	}

	public String getClientMyTeamspeakId() {
		return client_myteamspeak_id;
	}

	public int getClientNeededServerQueryViewPower() {
		return client_needed_serverquery_view_power;
	}

	public String getClientNicknamePhonetic() {
		return client_nickname_phonetic;
	}

	public String getClientSignedBadges() {
		return client_signed_badges;
	}

	public String getClientTalkRequestMessage() {
		return client_talk_request_msg;
	}

	public int getClientUnreadMessages() {
		return client_unread_messages;
	}

	public boolean hasClientAvatar() {
		return client_flag_avatar;
	}

	public boolean isClientOutputOnlyMuted() {
		return client_outputonly_muted;
	}

	public boolean isClientRequestTalk() {
		return client_talk_request;
	}

}
