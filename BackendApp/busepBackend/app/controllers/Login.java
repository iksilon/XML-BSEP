package controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import controllers.hashAndSaltUtils.HashAndSaltUtil;
import models.User;
import play.cache.Cache;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;

public class Login extends AppController {
	/**
	 * Metoda za logovanje korisnika
	 * @param body - JSON podaci u "telu" request objekta
	 * @return {@link play.mvc.results.Result} u zavisnosti od podataka u {@code body}
	 */
	public static Result logIn(String body) {
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		String uname = data.get(0);
		String pwd = data.get(1);
		
		//TODO: TIMESTAMP: OVO VEZANO ZA TIMESTAMP ZA SVAKI REQUEST TREBA
		//TODO: TIMESTAMP (i ostalo): Mislim da je ovako najbolje, samo vratiti "request fail" ako nesto nije u redu, jer drukcije dajemo neke informacije
		if(timestampCheckResult != 0) {
//			return new BadRequest("Invalid request");
//		}
//		else if(timestampCheckResult == 2) {
			return new play.mvc.results.Error("Request processing failed");
//		}
//		else if(timestampCheckResult == 3) {
//			return new BadRequest("Request timed out");
		}
		//TODO: TIMESTAMP: KRAJ TIMESTAMP PROVERE
		
		User loggedUser = User.find("byUsername", uname).first();
		if (loggedUser == null) {
			return new BadRequest("Invalid login"); //TODO: TIMESTAMP pogledati komentar za timestamp
		}

		HashAndSaltUtil hasu = new HashAndSaltUtil();
		try {
			if (!hasu.authenticate(pwd, loggedUser)) {
				return new BadRequest("Invalid login");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new play.mvc.results.Error("Request processing failed");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new play.mvc.results.Error("Request processing failed");
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return new play.mvc.results.Error("Request processing failed");
		}

		ObjectMapper om = new ObjectMapper();		
		try {
			Cache.set(loggedUser.username, loggedUser);
			Cache.set(loggedUser.username + "DeoId", 1);
			Cache.set(loggedUser.username + "ClanId", 1);
			return new RenderJson(/*Utils.responseTimestamp(*/om.writeValueAsString(loggedUser));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			Cache.delete(loggedUser.username);
			Cache.delete(loggedUser.username + "DeoId");
			Cache.delete(loggedUser.username + "ClanId");
			return new play.mvc.results.Error("Unable to log in");
		}
		// Proslediti uvek username kad je neka akcija koja zavisi od korisnika (npr predlaganje amandmana)
		//potrebno radi pronalazenja korisnika u Cache, jer je kljuc njegov username
		//takodje, korisnika ukloniti iz Cache kad se izloguje
	}
	
	public static Result logOut(String uname) {
		Cache.delete(uname);
		Cache.delete(uname + "DeoId");
		Cache.delete(uname + "ClanId");
		return new Ok();
	}
}
