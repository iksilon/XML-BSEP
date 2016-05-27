package controllers;

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
