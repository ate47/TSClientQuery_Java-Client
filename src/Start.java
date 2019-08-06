import java.io.IOException;

import fr.harmonia.tsclientquery.TSClientQuery;

public class Start {

	public static void main(String[] args) {
		try {
			new TSClientQuery("T3GE-KK3G-LU5U-MXQS-OUCM-32FW").start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
