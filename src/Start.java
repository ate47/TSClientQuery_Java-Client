import java.io.IOException;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.answer.WhoAmIAnswer;
import fr.harmonia.tsclientquery.event.EnumEvent;
import fr.harmonia.tsclientquery.event.Handler;

public class Start {

	public static void main(String[] args) {
		try {
			TSClientQuery client = new TSClientQuery(args[0]);

			client.start();

			testMsg(client);

			client.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testMsg(TSClientQuery client) {
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
				System.out.println("Poke> "+invokername + ": " + msg);
			}
		});
		
		WhoAmIAnswer whoAmI = client.whoAmI();
		int clid = whoAmI.getClientID();
		
		client.sendTextMessageToChannel("Hello channel");
		client.sendTextMessageToServer("Hello server");
		client.sendTextMessageToClient(clid, "Hello me");
		client.sendPoke(clid, "HELLO ME!");
		
	}
}
