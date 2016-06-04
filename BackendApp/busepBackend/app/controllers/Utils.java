package controllers;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.Document;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.results.Error;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;

public class Utils extends Controller {
	
	private static int msgNum = 0;
	
	public static String responseTimestamp(String jsonResponse) {
		jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 1);
		StringBuilder sb = new StringBuilder(jsonResponse);
		long time = System.currentTimeMillis();
		sb.append(", \"timestamp\":");
		sb.append(time);
		sb.append(", \"msgNum\":");
		sb.append(msgNum++);
		sb.append("}");

		System.out.println(sb);
		return sb.toString();
	}
	
	// prosledi role 0 za predsednika, 1 za odbornika itd
	public static void usersByRole(long role) {
		List<User> users = User.find("byRole", role).fetch();
		if(users.isEmpty()) {
			users = User.findAll();
		}
		
		Cache.set("users", users);
	}
	
	public static Result TEST(String testVal, String qq) {
		Object test = testVal + " " + qq;
		return new RenderJson(test);
	}
	
	public static Result loggedUserTest() {
		try {
			User user = (User) Cache.get("loggedUserTest");
			
			ObjectMapper om = new ObjectMapper();
			
			return new RenderJson(om.writeValueAsString(user));
		}
		catch (Exception e) {
			e.printStackTrace();
			return new Error("Could not fetch requested data due to an exception");
		}
	}
	
	public static void latestDocuments(int count) {
		List<Document> latestDocuments = Document.findAll();
		if(count <= 0) {
			Cache.set("documents", latestDocuments);
		}
		
		latestDocuments = new ArrayList<Document>(latestDocuments.subList(latestDocuments.size() - count, latestDocuments.size()));
		Cache.set("documents", latestDocuments);
	}
	
	public static Result getPanelIds(String uname) {
		int panelDeoId = (int) Cache.get(uname + "DeoId");
		int panelClanId = (int) Cache.get(uname + "ClanId");
		Object[] resp = new Object[] {panelDeoId, panelClanId};
		System.out.println("I WERK");
		return new RenderJson(resp);
	}
	
	public static Result setDeoId(String uname) {
		String keyDeo = uname + "DeoId";
		int panelDeoId = (int) Cache.get(keyDeo);
		Cache.set(keyDeo, ++panelDeoId);
		return new Ok();
	}
	
	public static Result setClanId(String uname) {
		String keyClan = uname + "ClanId";
		int panelClanId = (int) Cache.get(keyClan);		
		Cache.set(keyClan, ++panelClanId);
		return new Ok();
	}
}
