package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Document;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.*;

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
