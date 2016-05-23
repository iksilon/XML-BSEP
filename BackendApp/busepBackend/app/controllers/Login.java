package controllers;

import java.util.List;

import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.Result;
import play.mvc.results.Unauthorized;

public class Login extends Controller {
	
	public static Result logIn(String uname, String pwd) {
		if(uname.equals("highlord") && pwd.equals("admin")) { //privremeno, dok baza nije gotova
			return new Ok();
		}
		
		List<User> user = User.find("byUsernameAndPassword", uname, pwd).fetch();
		if(user.isEmpty()) {
			return new BadRequest("No such user");
		}
		
		User loggedUser = user.get(0);
		if(loggedUser.role == 2) {
			return new Unauthorized("No such user");
		}
		// Proslediti uvek username kad je neka akcija koja zavisi od korisnika (npr predlaganje amandmana)
		//potrebno radi pronalazenja korisnika u Cache, jer je kljuc njegov username
		//takodje, korisnika ukloniti iz Cache kad se izloguje
		Cache.set(loggedUser.username, loggedUser);
		Cache.set(loggedUser.username + "DeoId", 1);
		Cache.set(loggedUser.username + "ClanId", 1);
		return new Ok();
	}
	
	public static Result logOut(String uname) {
		Cache.delete(uname);
		Cache.delete(uname + "DeoId");
		Cache.delete(uname + "ClanId");
		return new Ok();
	}
}
