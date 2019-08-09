package fr.harmonia.tsclientquery.event;

public interface Handler {
	public static final int MESSAGE_TARGET_MODE_CHANNEL = 2;
	public static final int MESSAGE_TARGET_MODE_CLIENT = 1;
	public static final int MESSAGE_TARGET_MODE_SERVER = 3;

	void onPoke(int schandlerid, int invokerid, String msg, String invokername, String invokeruid);
	
	/**
	 * call when the client receive a message, require
	 * {@link EnumEvent#notifytextmessage}
	 * 
	 * @param schandlerid the server connection ID
	 * @param targetmode  the target, {@link Handler#MESSAGE_TARGET_MODE_CHANNEL} or
	 *                    {@link Handler#MESSAGE_TARGET_MODE_SERVER}
	 * @param msg         the sent message
	 * @param invokerid   CLID of who sent the message
	 * @param invokername name of who sent the message
	 * @param invokeruid  UID of who sent the message
	 * @see Handler#onPrivateMessage(int, String, int, int, String, String)
	 */
	void onMessage(int schandlerid, int targetmode, String msg, int invokerid, String invokername, String invokeruid);

	/**
	 * call when the client receive a private message, require
	 * {@link EnumEvent#notifytextmessage}
	 * 
	 * @param schandlerid the server connection ID
	 * @param msg         the sent message
	 * @param target      target CLID of this message
	 * @param invokerid   CLID of who sent the message
	 * @param invokername name of who sent the message
	 * @param invokeruid  UID of who sent the message
	 * @see Handler#onPrivateMessage(int, String, int, int, String, String)
	 */
	void onPrivateMessage(int schandlerid, String msg, int target, int invokerid, String invokername,
			String invokeruid);

}
