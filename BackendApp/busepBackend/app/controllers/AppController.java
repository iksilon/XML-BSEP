package controllers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import models.User;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.results.BadRequest;
import play.mvc.results.Error;
import play.mvc.results.Forbidden;
import play.mvc.results.Result;
import utils.GeneralUtils;
import utils.JWTUtils;

//@With(Secure.class)
public class AppController extends Controller {

//	protected static int timestampCheckResult = -1;
//	protected static boolean msgNumOk;
	
	@Before(priority=1)
	static Result csrfOriginCheck() {
		Header hOrig = request.headers.get("origin");
		if(hOrig != null) {
			List<String> origVals = hOrig.values;
			if(origVals != null) {
				String origPageUrl = origVals.get(0);
				if(origPageUrl != null && origPageUrl.length() > 0) {
					if(origPageUrl.contains("https://localhost:9000")) {
						return null; // sve je ok
					}
				}
			}
			return new BadRequest("Invalid origin");
		}
		// mozda nije sve Ok, ali nekada polje origin ne postoji
		return null;
	}
	
	@Before(priority=2)
	static Result csrfRefererCheck() {
		Header hRef = request.headers.get("referer");
		if(hRef != null) {
			List<String> refVals = hRef.values;
			if(refVals != null) {
				String refPageUrl = refVals.get(0);
				if(refPageUrl != null && refPageUrl.length() > 0) {
					if(refPageUrl.equals("https://localhost:9000/")) {
						return null; // sve je ok
					}
				}
			}
			return new BadRequest("Invalid referer");
		}
		// mozda nije sve Ok, ali nekada polje referer ne postoji
		return null;
	}
	
	protected static ConcurrentHashMap<String, Long> userMsgNum = new ConcurrentHashMap<String, Long>();
	@Before(unless={"Login.logIn", "Login.token", "Login.logOut", "Search.doSearch", "Utils.usersByRole",
			"Acts.latestDocuments", "Acts.inProcedure", "Acts.current"}, priority=3)
	static Result checkMsgNum() {
		Header hMsgNum = request.headers.get("msgnum");
		Header hUname = request.headers.get("username");
		//ako nema ovog headera, samo prodji, jer su to uglavnom angular requestovi za uzimanje public resursa
		if(hMsgNum == null || hUname == null) {
			return null;
		}
		String uname = hUname.value();
		String msgNumStr = hMsgNum.value();
		if(msgNumStr == null || msgNumStr.trim().equals("")
				|| uname == null || uname.trim().equals("")) {
			return new BadRequest("Invalid request");
		}
		
		Long oPrevMsgNum = userMsgNum.get(uname);
		if(oPrevMsgNum == null) {
			return new BadRequest("Can't check request validity. Please refresh/relog");
		}
		Long prevMsgNum = Long.parseLong(oPrevMsgNum.toString());
		
		Long msgNum = 0L;
		try {
			msgNum = Long.parseLong(msgNumStr);
			if(prevMsgNum >= msgNum) {
				return new BadRequest("Invalid request");
			}
		}
		catch(NumberFormatException e) {
			return new Error("Request processing failed");
		}
		
		userMsgNum.put(uname, msgNum);		
		return null;
	}
	
	@Before(priority=4)
	static Result checkTimestamp() {		
		String tsString = request.headers.get("timestamp").value();
		String tsHashString = request.headers.get("timestamphash").value();
		
//		try { // check timestamp hash
//			MessageDigest digest = MessageDigest.getInstance("SHA-256");
//			byte[] hashBytes = digest.digest(tsString.getBytes(StandardCharsets.UTF_8));
//		    String hexCharsHash = GeneralUtils.byteToHex(hashBytes);
			String hexCharsHash = GeneralUtils.getHexHash(tsString);

			if(hexCharsHash == null) {
				return new Error("Could not validate request");
			}
			byte[] asd = tsHashString.getBytes(StandardCharsets.UTF_8);
			
			String hexHash = new String(hexCharsHash).toUpperCase();
			String hexTsHash = new String(asd).toUpperCase();
			if(!hexHash.equals(hexTsHash)) {
//				timestampCheckResult = 1;
				return new BadRequest("Invalid request data");
			}
//		} catch (NoSuchAlgorithmException e1) {
//			e1.printStackTrace();
////			timestampCheckResult = 2;
//			return new Error("Request processing failed");
//		}
		
		Long timestamp = Long.parseLong(tsString);
		Long serverTimestamp = System.currentTimeMillis();
		Long timestampDiff = serverTimestamp - timestamp;
		if(timestampDiff <= 0 || timestampDiff > 5000) { // 5 sekundi. mozda treba manje?
//			timestampCheckResult = 3;
			return new BadRequest("Request timed out");
		}
		
//		timestampCheckResult = 0;		
		return null;
	}
	
	@Before(unless={"Login.logIn", "Login.token", "Login.logOut", "Search.doSearch", "Utils.usersByRole",
					"Acts.latestDocuments", "Acts.inProcedure", "Acts.current"}, priority=5)
	static Result jwtCheck() {
		Header hAuth = request.headers.get("authorization");
		if(hAuth != null) {
			List<String> authVals = hAuth.values;
			if(authVals != null) {
				String authString = authVals.get(0);
				if(authString != null && authString.length() > 0) {
					String jwt =  authString.split(" ")[1];
					
					User user = User.find("byUsername", request.headers.get("username").value()).first();
					Cache.set(user.username, user);
					if(JWTUtils.checkJWT(jwt, user)) {
						return null;
					}
					//System.out.println(jwt);
				}
			}
		}
		// nope
		return new Forbidden("Invalid token");
	}
}
