package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Document;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;

public class MainPage extends Controller {

	public static void show(String mode) {
		List<User> users = (List<User>) Cache.get("users");
		List<Document> documents = (List<Document>) Cache.get("documents");
		
		mode = (mode != null) ? (mode.isEmpty() ? "edit" : mode) : "edit";
		
		render(users, documents, mode);
	}
	
	// prosledi role kao "odbornik" (ili kako ih vec budemo obelezavali), i dobices odbornike
	// slicno za ostale
	public static void usersByRole(String role) {
		List<User> users = User.find("byRole", role).fetch();
		Cache.set("users", users);
		render("");
	}
		
	public static void latestDocuments(int count) {
		List<Document> latestDocuments = Document.findAll();		
		latestDocuments = new ArrayList<Document>(latestDocuments.subList(latestDocuments.size() - count, latestDocuments.size()));
		Cache.set("documents", latestDocuments);
		
		show("");
	}
}
