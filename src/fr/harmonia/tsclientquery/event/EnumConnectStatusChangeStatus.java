package fr.harmonia.tsclientquery.event;

public enum EnumConnectStatusChangeStatus {
	/**
	 * try to connect to server
	 */
	connecting,
	/**
	 * connected to server, ask default channel
	 */
	connected,
	/**
	 * ask every channel -> end with channellistfinished
	 */
	connection_establishing,
	/**
	 * ask every channel / server groups, client needed permissions, server data,
	 * subscribe
	 */
	connection_established,
	/**
	 * when the client disconnect
	 */
	disconnected
}
