package controllers;

import java.util.List;

import models.User;
import play.cache.Cache;
import play.mvc.Controller;

public class Login extends Controller {

	public static void show(String mode) {

		mode = (mode != null) ? (mode.isEmpty() ? "edit" : mode) : "edit";
		
		render(mode);
	}
	
	public static void logIn(String username, String password) {
		List<User> user = User.find("byUsernameAndPassword", username, password).fetch();
		if(user.isEmpty()) {
			show("error"); //ili kako cemo vec za prikaz greske. Da li uopste postoji error mode?
		}
		
		User loggedUser = user.get(0);
		// Proslediti uvek username kad je neka akcija koja zavisi od korisnika (npr predlaganje amandmana)
		//potrebno radi pronalazenja korisnika u Cache, jer je kljuc njegov username
		//takodje, korisnika ukloniti iz Cache kad se izloguje
		Cache.set(loggedUser.username, loggedUser);
	}
	
	public static void logOut(String username) {
		Cache.delete(username);
		show("logout"); //da li postoji logout mode? Da li mi definisemo mode ili su modovi predefinisani???
	}
}
