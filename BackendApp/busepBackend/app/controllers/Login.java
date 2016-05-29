package controllers;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.Result;
import play.mvc.results.Unauthorized;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

public class Login extends Controller {
	
	public static Result logIn(String uname, String pwd) {
		List<User> user = User.find("byUsernameAndPassword", uname, pwd).fetch();
		if(user.isEmpty()) {
			return new BadRequest("Invalid login");
		}
		
		User loggedUser = user.get(0);
		// Proslediti uvek username kad je neka akcija koja zavisi od korisnika (npr predlaganje amandmana)
		//potrebno radi pronalazenja korisnika u Cache, jer je kljuc njegov username
		//takodje, korisnika ukloniti iz Cache kad se izloguje
		Cache.set(loggedUser.username, loggedUser);
		Cache.set(loggedUser.username + "DeoId", 1);
		Cache.set(loggedUser.username + "ClanId", 1);
		Cache.set("loggedUserTest", loggedUser);
		// User role bi trebalo sacuvati u JWT ili vec negde gde je nedostupan klijentu
		return new Ok();
	}
	
	public static Result logOut(String uname) {
		Cache.delete(uname);
		Cache.delete(uname + "DeoId");
		Cache.delete(uname + "ClanId");
		System.out.println(uname + " LOGGED OUT");
		return new Ok();
	}
}
