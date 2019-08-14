package fr.harmonia.tsclientquery.objects;

public class ChannelClient extends ParsedObject {
	private int cid, client_channel_group_id, client_database_id, client_icon_id, clid, client_talk_power;
	private String client_away_message, client_country, client_nickname, client_unique_identifier;
	private String[] client_servergroups;
	private boolean client_away, client_is_channel_commander, client_input_hardware, client_input_muted,
			client_is_muted, client_output_hardware, client_output_muted, client_is_priority_speaker,
			client_is_recording, client_is_talker, client_flag_talking, client_type;

	public ChannelClient(ParsedObject object) {
		super(object);
		fetch();
	}

	public ChannelClient(String line) {
		super(line);
		fetch();
	}

	private void fetch() {
		client_channel_group_id = getInteger("client_channel_group_id");
		client_database_id = getInteger("client_database_id");
		client_icon_id = getInteger("client_icon_id");
		clid = getInteger("clid");
		client_talk_power = getInteger("client_talk_power");
		cid = getInteger("cid");
		cid = getInteger("cid");

		client_away_message = get("client_away_message");
		client_country = get("client_country");
		client_nickname = get("client_nickname");
		client_unique_identifier = get("client_unique_identifier");

		client_servergroups = get("client_servergroups").split("[,]");

		client_away = getBoolean("client_away");
		client_is_channel_commander = getBoolean("client_is_channel_commander");
		client_input_hardware = getBoolean("client_input_hardware");
		client_input_muted = getBoolean("client_input_muted");
		client_is_muted = getBoolean("client_is_muted");
		client_output_hardware = getBoolean("client_output_hardware");
		client_output_muted = getBoolean("client_output_muted");
		client_is_priority_speaker = getBoolean("client_is_priority_speaker");
		client_is_recording = getBoolean("client_is_recording");
		client_is_talker = getBoolean("client_is_talker");
		client_flag_talking = getBoolean("client_flag_talking");
		client_type = getBoolean("client_type");
	}

	public int getChannelID() {
		return cid;
	}

	public String getClientAwayMessage() {
		return client_away_message;
	}

	public int getClientChannelGroupID() {
		return client_channel_group_id;
	}

	public String getClientCountry() {
		return client_country;
	}

	public int getClientDatabaseID() {
		return client_database_id;
	}

	public int getClientIconId() {
		return client_icon_id;
	}

	public int getClientID() {
		return clid;
	}

	public String getClientNickname() {
		return client_nickname;
	}

	public String[] getClientServerGroups() {
		return client_servergroups;
	}

	public int getClientTalkPower() {
		return client_talk_power;
	}

	public String getClientUID() {
		return client_unique_identifier;
	}

	public SpeakIcon getSpeakIcon() {
		if (!isClientOutputHardware())
			return SpeakIcon.SPEAKER_DISABLE;
		if (isClientOutputMuted())
			return SpeakIcon.SPEAKER_MUTED;
		if (!isClientInputHardware())
			return SpeakIcon.MICROPHONE_DISABLED;
		if (isClientMuted())
			return SpeakIcon.MICROPHONE_MUTED;
		if (isClientTalking()) {
			if (isClientChannelCommander())
				return SpeakIcon.CHANNEL_COMMANDER_SPEAKING;
			else
				return SpeakIcon.SPEAKING;
		}
		if (isClientChannelCommander())
			return SpeakIcon.CHANNEL_COMMANDER;
		else
			return SpeakIcon.IDLE;

	}

	public boolean isClientAway() {
		return client_away;
	}

	public boolean isClientChannelCommander() {
		return client_is_channel_commander;
	}

	public boolean isClientInputHardware() {
		return client_input_hardware;
	}

	public boolean isClientInputMuted() {
		return client_input_muted;
	}

	public boolean isClientMuted() {
		return client_is_muted;
	}

	public boolean isClientOutputHardware() {
		return client_output_hardware;
	}

	public boolean isClientOutputMuted() {
		return client_output_muted;
	}

	public boolean isClientPrioritySpeaker() {
		return client_is_priority_speaker;
	}

	public boolean isClientRecording() {
		return client_is_recording;
	}

	public boolean isClientTalker() {
		return client_is_talker;
	}

	public boolean isClientTalking() {
		return client_flag_talking;
	}

	public boolean isServerQuery() {
		return client_type;
	}
}
