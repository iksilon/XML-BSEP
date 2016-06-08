package controllers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import play.Invoker.InvocationContext;
import play.mvc.Before;
import play.mvc.Controller;

public class AppController extends Controller {

	protected static int timestampCheckResult = -1;
	
	@Before
	static void checkTimestamp() {
		String tsString = request.headers.get("timestamp").value();
		String tsHashString = request.headers.get("timestamphash").value();
		
		try { // check timestamp hash
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(tsString.getBytes(StandardCharsets.UTF_8));
		    String hexCharsHash = Utils.byteToHex(hashBytes);
//			System.out.println(hexCharsHash);

			byte[] asd = tsHashString.getBytes(StandardCharsets.UTF_8);			
//			System.out.println(new String(asd));
			
			String hexHash = new String(hexCharsHash).toUpperCase();
			String hexTsHash = new String(asd).toUpperCase();
			if(!hexHash.equals(hexTsHash)) {
				timestampCheckResult = 1;
				return;
			}
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			timestampCheckResult = 2;
			return;
		}
		
		Long timestamp = Long.parseLong(tsString);
		Long serverTimestamp = System.currentTimeMillis();
		Long timestampDiff = serverTimestamp - timestamp;
		if(timestampDiff <= 0 || timestampDiff > 5000) { // 5 sekundi. mozda treba manje?
			timestampCheckResult = 3;
			return;
		}
		
		timestampCheckResult = 0;
	}
}
