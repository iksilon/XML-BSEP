package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.hashAndSaltUtils.HashAndSaltUtil;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Login extends Controller {
	
	public static Result logIn(String uname, String pwd) {
		User loggedUser = User.find("byUsername", uname).first();

		if (loggedUser == null)
			return new BadRequest("Invalid login");

		HashAndSaltUtil hasu = new HashAndSaltUtil();

		try {
			if (!hasu.authenticate(pwd, loggedUser))
				return new BadRequest("Invalid login");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		ObjectMapper om = new ObjectMapper();		
		try {
			Cache.set(loggedUser.username, loggedUser);
			Cache.set(loggedUser.username + "DeoId", 1);
			Cache.set(loggedUser.username + "ClanId", 1);
//			Cache.set("loggedUserTest", loggedUser);
			System.out.println(uname + ": Work work.");

			System.out.println(Cache.get(loggedUser.username + "ClanId"));
			System.out.println(Cache.get(loggedUser.username + "DeoId"));
			
			String userJsonString = om.writeValueAsString(loggedUser);
			userJsonString = userJsonString.substring(0, userJsonString.length() - 1);
			return new RenderJson(Utils.responseTimestamp(userJsonString));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Cache.delete(loggedUser.username);
			Cache.delete(loggedUser.username + "DeoId");
			Cache.delete(loggedUser.username + "ClanId");
			return new play.mvc.results.Error("Unable to log in");
		}
		// Proslediti uvek username kad je neka akcija koja zavisi od korisnika (npr predlaganje amandmana)
		//potrebno radi pronalazenja korisnika u Cache, jer je kljuc njegov username
		//takodje, korisnika ukloniti iz Cache kad se izloguje
		
		
		
//		return new Ok();
	}
	
	public static Result logOut(String uname) {
		Cache.delete(uname);
		Cache.delete(uname + "DeoId");
		Cache.delete(uname + "ClanId");
		System.out.println(uname + ": Me not that kind of orc.");
		return new Ok();
	}
}
