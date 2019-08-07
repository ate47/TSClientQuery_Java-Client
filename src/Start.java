import java.io.IOException;

import fr.harmonia.tsclientquery.TSClientQuery;

public class Start {

	public static void main(String[] args) {
		try {
			TSClientQuery client = new TSClientQuery(args[0]);
			
			client.start();
			
			client.registerAllEvents();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
