package controllers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.With;
import play.mvc.results.Error;
import play.mvc.results.Result;

//@With(Secure.class)
public class AppController extends Controller {

	protected static int timestampCheckResult = -1;
	protected static boolean msgNumOk;
	
	@Before(unless={"Login.logIn", "Login.loginCheck", "Utils.getUserMessageNumber", "Utils.usersByRole"})
	static Result checkMsgNum() {
		Header hMsgNum = request.headers.get("msgnum");
		Header hUname = request.headers.get("username");
		//ako nema ovog headera, samo prodji, jer su to uglavnom angular requestovi za uzimanje public resursa
		if(hMsgNum == null || hUname == null) {
			return null;
		}
		String msgNumStr = hMsgNum.value();
		if(msgNumStr == null) {
			return new Error("Request processing failed");
		}
		
		Object oPrevMsgNum = Cache.get(hUname.value() + "msgNum");
		if(oPrevMsgNum == null) {
			return new Error("Request processing failed");
		}
		Long prevMsgNum = Long.parseLong(oPrevMsgNum.toString());
		
		Long msgNum = 0L;
		try {
			msgNum = Long.parseLong(msgNumStr);
			if(prevMsgNum < msgNum) {
				msgNumOk = true;
			}
			else {
				msgNumOk = false;
				return new Error("Request processing failed");
			}
		}
		catch(NumberFormatException e) {
			return new Error("Request processing failed");
		}
		
		Cache.set(request.headers.get("username").value() + "msgNum", msgNum);
		
		return null;
	}
	
	@Before
	static Result checkTimestamp() {		
		String tsString = request.headers.get("timestamp").value();
		String tsHashString = request.headers.get("timestamphash").value();
		
		try { // check timestamp hash
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(tsString.getBytes(StandardCharsets.UTF_8));
		    String hexCharsHash = Utils.byteToHex(hashBytes);

			byte[] asd = tsHashString.getBytes(StandardCharsets.UTF_8);
			
			String hexHash = new String(hexCharsHash).toUpperCase();
			String hexTsHash = new String(asd).toUpperCase();
			if(!hexHash.equals(hexTsHash)) {
				timestampCheckResult = 1;
				return new Error("Request processing failed");
			}
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			timestampCheckResult = 2;
			return new Error("Request processing failed");
		}
		
		Long timestamp = Long.parseLong(tsString);
		Long serverTimestamp = System.currentTimeMillis();
		Long timestampDiff = serverTimestamp - timestamp;
		if(timestampDiff <= 0 || timestampDiff > 5000) { // 5 sekundi. mozda treba manje?
			timestampCheckResult = 3;
			return new Error("Request processing failed");
		}
		
		timestampCheckResult = 0;
		
		return null;
	}
}
