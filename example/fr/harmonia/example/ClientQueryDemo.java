package fr.harmonia.example;

import java.io.IOException;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.answer.WhoAmIAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;
import fr.harmonia.tsclientquery.event.Handler;
import fr.harmonia.tsclientquery.exception.InsufficientClientPermissionsQueryException;

public class ClientQueryDemo {

	public static void main(String[] args) {
		try {
			TSClientQuery client = new TSClientQuery(args[0]);

			client.start();

//			testReceiveMsg(client);
//			testSendMsg(client);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * demo to receive message from the client
	 */
	public static void testReceiveMsg(TSClientQuery client) {
		client.clientNotifyRegister(EnumEvent.notifytextmessage);
		client.clientNotifyRegister(EnumEvent.notifyclientpoke);
		client.registerHandler(new Handler() {

			@Override
			public void onPrivateMessage(int schandlerid, String msg, int target, int invokerid, String invokername,
					String invokeruid) {
				System.out.println(invokername + ": " + msg);
			}

			@Override
			public void onMessage(int schandlerid, int targetmode, String msg, int invokerid, String invokername,
					String invokeruid) {
				System.out.println((MESSAGE_TARGET_MODE_CHANNEL == targetmode ? "Channel" : "Server") + "> "
						+ invokername + ": " + msg);
			}

			@Override
			public void onPoke(int schandlerid, int invokerid, String msg, String invokername, String invokeruid) {
				System.out.println("Poke> " + invokername + ": " + msg);
			}
		});
	}

	/**
	 * demo to send message with the client
	 */
	public static void testSendMsg(TSClientQuery client) {

		WhoAmIAnswer whoAmI = client.whoAmI();
		int clid = whoAmI.getClientID();

		try {
			
			client.sendPoke(clid, "HELLO ME!");
			client.sendTextMessageToChannel("Hello channel");
			client.sendTextMessageToServer("Hello server");
			client.sendTextMessageToClient(clid, "Hello me");
			
		} catch (InsufficientClientPermissionsQueryException e) {
			e.printStackTrace();
		}

	}
}
