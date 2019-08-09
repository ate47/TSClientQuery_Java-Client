package fr.harmonia.tsclientquery.answer;

/*

channelclientlist cid=1 -uid -away -voice -groups -icon -country
clid=1 cid=1 client_database_id=2 client_nickname=ATE47 client_type=0 client_away=0 client_away_message client_flag_talking=0 
client_input_muted=0 client_output_muted=1 client_input_hardware=0 client_output_hardware=1 client_talk_power=75 
client_is_talker=0 client_is_priority_speaker=0 client_is_recording=0 client_is_channel_commander=0 client_is_muted=0
client_unique_identifier=5H1C8xKIFE3TQqp7i7P3IE7Jtgk= client_servergroups=6,7 client_channel_group_id=6 client_icon_id=0
client_country|clid=2 cid=1 client_database_id=4 client_nickname=Spktacular client_type=0 client_away=0 client_away_message
client_flag_talking=0 client_input_muted=0 client_output_muted=1 client_input_hardware=1 client_output_hardware=1
client_talk_power=0 client_is_talker=0 client_is_priority_speaker=0 client_is_recording=0 client_is_channel_commander=0
client_is_muted=0 client_unique_identifier=Wk+H5VFRlAfGpj1nFwMjrS+Iv7s= client_servergroups=8 client_channel_group_id=8
client_icon_id=0 client_country
 */
@SuppressWarnings("deprecation")
public class ChannelClientListAnswer extends Answer {

	public ChannelClientListAnswer(String line) {
		super(line);
	}

	public int getChannelID() {
		return getInteger("cid");
	}

	public String getClientAwayMessage() {
		return get("client_away_message");
	}

	public int getClientChannelGroupID() {
		return getInteger("client_channel_group_id");
	}

	public String getClientCountry() {
		return get("client_country");
	}

	public int getClientDatabaseID() {
		return getInteger("client_database_id");
	}

	public int getClientIconId() {
		return getInteger("client_icon_id");
	}

	public int getClientID() {
		return getInteger("clid");
	}

	public String getClientNickname() {
		return get("client_nickname");
	}

	public String[] getClientServerGroups() {
		return get("client_servergroups").split("[,]");
	}

	public int getClientTalkPower() {
		return getInteger("client_talk_power");
	}

	public int getClientType() {
		return getInteger("client_type");
	}

	public String getClientUID() {
		return get("client_unique_identifier");
	}

	public boolean isClientAway() {
		return getBoolean("client_away");
	}

	public boolean isClientChannelCommander() {
		return getBoolean("client_is_channel_commander");
	}

	public boolean isClientInputHardware() {
		return getBoolean("client_input_hardware");
	}

	public boolean isClientInputMuted() {
		return getBoolean("client_input_muted");
	}

	public boolean isClientMuted() {
		return getBoolean("client_is_muted");
	}

	public boolean isClientOutputHardware() {
		return getBoolean("client_output_hardware");
	}

	public boolean isClientOutputMuted() {
		return getBoolean("client_output_muted");
	}

	public boolean isClientPrioritySpeaker() {
		return getBoolean("client_is_priority_speaker");
	}

	public boolean isClientRecording() {
		return getBoolean("client_is_recording");
	}

	public boolean isClientTalker() {
		return getBoolean("client_is_talker");
	}

	public boolean isClientTalking() {
		return getBoolean("client_flag_talking");
	}
}
