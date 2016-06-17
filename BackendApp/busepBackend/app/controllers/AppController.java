package controllers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.Permission;
import models.User;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.results.BadRequest;
import play.mvc.results.Error;
import play.mvc.results.Forbidden;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;
import utils.CsrfTokenUtils;
import utils.GeneralUtils;

//@With(Secure.class)
public class AppController extends Controller {

//	protected static int timestampCheckResult = -1;
//	protected static boolean msgNumOk;
	
	@Before(priority=1)
	static Result csrfOriginRefererCheck() {
		boolean origOk = false;
		boolean refOk = false;
		Header hOrig = request.headers.get("origin");
		Header hRef = request.headers.get("referer");
//		if(hOrig != null || hRef != null) {
			
		List<String> origVals = hOrig != null ? hOrig.values : null;
		List<String> refVals = hRef != null ? hRef.values : null;
			
//			if(origVals != null || refVals != null) {
				
		String origPageUrl = origVals != null ? origVals.get(0) : null;
		String refPageUrl = refVals != null ? refVals.get(0) : null;				
		
//		if((origPageUrl != null && origPageUrl.length() > 0) 
//			|| (refPageUrl != null && refPageUrl.length() > 0)) {
			
		if(origPageUrl != null && origPageUrl.length() > 0 && origPageUrl.contains("https://localhost:9000")) {
			origOk = true;
			System.out.print("Request from origin: ");
			for(String ov: origVals) {
				System.out.println("\t" + ov);
				if(origVals.indexOf(ov) < origVals.size() - 1) {
					System.out.println("\n\t\t\t");
				}
			}
		}
		
		if(refPageUrl != null && refPageUrl.length() > 0 && refPageUrl.equals("https://localhost:9000/")) {
			refOk = true;
			System.out.print("Request from referer: ");
			for(String rv: refVals) {
				System.out.println("\t" + rv);
				if(refVals.indexOf(rv) < refVals.size() - 1) {
					System.out.println("\n\t\t\t");
				}
			} 
		}
		
		if(origOk || refOk) {
			return null; //sve je ok, neki od headera je prisutan
		}
//		}
//			}
//		}

		return new BadRequest("Invalid referer or origin");
		
		// mozda nije sve Ok, ali nekada polje origin ne postoji
//		return null;
	}
	
	protected static ConcurrentHashMap<String, Long> userMsgNum = new ConcurrentHashMap<String, Long>();
	@Before(unless={"Login.logIn", "Login.token", "Login.logOut", "Login.loginCheck", 
			"Search.doSearch", "Acts.current", "Acts.inProcedure", "Acts.getAct",
			"Acts.getActAmendments", "Acts.latestDocuments", "Acts.inAmendments",
			"Utils.usersByRole"}, priority=2)
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
	
	@Before(priority=3)
	static Result checkTimestamp() {		
		String tsString = request.headers.get("timestamp").value();
		String tsHashString = request.headers.get("timestamphash").value();
		
		String hexCharsHash = GeneralUtils.getHexHash(tsString);

		if(hexCharsHash == null) {
			return new Error("Could not validate request");
		}
		byte[] asd = tsHashString.getBytes(StandardCharsets.UTF_8);
		
		String hexHash = new String(hexCharsHash).toUpperCase();
		String hexTsHash = new String(asd).toUpperCase();
		if(!hexHash.equals(hexTsHash)) {
			return new BadRequest("Invalid request data");
		}
		
		Long timestamp = Long.parseLong(tsString);
		Long serverTimestamp = System.currentTimeMillis();
		Long timestampDiff = serverTimestamp - timestamp;
		if(timestampDiff <= 0 || timestampDiff > 5000) { // 5 sekundi. mozda treba manje?
			return new BadRequest("Request timed out");
		}
		
		return null;
	}
	
	@Before(unless={"Login.logIn", "Login.token", "Login.logOut", "Login.loginCheck", 
			"Search.doSearch", "Acts.current", "Acts.inProcedure", "Acts.getAct",
			"Acts.getActAmendments", "Acts.latestDocuments", "Acts.inAmendments",
			"Utils.usersByRole"}, priority=4)
	static Result csrfTokenCheck() {
		Header hAuth = request.headers.get("authorization");
		if(hAuth != null) {
			List<String> authVals = hAuth.values;
			if(authVals != null) {
				String authString = authVals.get(0);
				if(authString != null && authString.length() > 0) {
					String token =  authString.split(" ")[1];
					
					User user = User.find("byUsername", request.headers.get("username").value()).first();
					Cache.set(user.username, user);
					String username = session.get("user");
					if(CsrfTokenUtils.checkToken(username, token)) {
						return null;
					}
				}
			}
		}
		// nope
		return new Forbidden("Invalid token");
	}
	
	@Before(unless={"Login.logIn", "Login.token", "Login.logOut", "Login.loginCheck", 
			"Search.doSearch", "Acts.current", "Acts.inProcedure", "Acts.getAct",
			"Acts.getActAmendments", "Acts.latestDocuments", "Acts.inAmendments",
			"Utils.usersByRole"}, priority=5)
	static Result actionAuthorization() {
		Header hUname = request.headers.get("username");
		String uname = hUname != null ? hUname.value() : null;
		if(uname == null) {
			return new BadRequest("Invalid payload data");
		}

		String action = request.action;
		List<Permission> perms = Permission.find("byName", action).fetch();
		if(perms == null) {
			return new Forbidden("Not enough priviledge");
		}
		
		User caller = User.find("byUsername", uname).first();
		if(caller == null) {
			return new BadRequest("No such user");
		}
		
		for(Permission p: perms) {
			if(p.name.equals(action) && p.roles.contains(caller.role)) {
				return null; //ok, imas pristup
			}
		}
		return new Forbidden("Not enough priviledge");
	}
}
