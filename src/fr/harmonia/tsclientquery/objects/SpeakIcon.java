package fr.harmonia.tsclientquery.objects;

/**
 * basic tool to help representation of the status of a client
 * 
 * @author ATE47
 *
 */
public enum SpeakIcon {

	/**
	 * DARK BLUE CIRCLE
	 */
	IDLE,
	/**
	 * DARK ORANGE CIRCLE
	 */
	CHANNEL_COMMANDER,
	/**
	 * BLUE CIRCLE
	 */
	SPEAKING,
	/**
	 * ORANGE CIRCLE
	 */
	CHANNEL_COMMANDER_SPEAKING,
	/**
	 * RED CIRCLE
	 */
	WHISPER,
	/**
	 * MICROPHONE WITH CROSS
	 */
	MICROPHONE_MUTED,
	/**
	 * MICROPHONE WITH BARRIER
	 */
	MICROPHONE_DISABLED,
	/**
	 * SPEAKER WITH CROSS
	 */
	SPEAKER_MUTED,
	/**
	 * SPEAKER WITH BARRIER
	 */
	SPEAKER_DISABLE;
}
