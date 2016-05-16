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

//	public static void show(String mode) {
//		List<User> users = (List<User>) Cache.get("users");
//		List<Document> documents = (List<Document>) Cache.get("documents");
//		
//		mode = (mode != null) ? (mode.isEmpty() ? "edit" : mode) : "edit";
//		
//		render(users, documents, mode);
//	}
	
	// prosledi role kao 0 za predsednika, 1 za odbornika itd
	public static void usersByRole(long role) {
		List<User> users = User.find("byRole", role).fetch();
		if(users.isEmpty()) {
			users = User.findAll();
		}
		
		Cache.set("users", users);
		//show("");
	}
	
	public static void userHighlord() {
		usersByRole(0); // neka 0 bude predsednik, tj glavni dasa
	}
	
	public static void usersAll() {
		usersByRole(-1);
	}
	
	public static Result TEST() {
		Object test = "THE DATA IS ALIVE";
		return new RenderJson(test);
	}
	
	public static void latestDocuments(int count) {
		List<Document> latestDocuments = Document.findAll();
		if(count <= 0) {
			Cache.set("documents", latestDocuments);			
//			show("");
		}
		
		latestDocuments = new ArrayList<Document>(latestDocuments.subList(latestDocuments.size() - count, latestDocuments.size()));
		Cache.set("documents", latestDocuments);
		
//		show(""); 
	}
}
