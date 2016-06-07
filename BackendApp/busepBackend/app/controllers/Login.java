package controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import controllers.hashAndSaltUtils.HashAndSaltUtil;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;

public class Login extends Controller {
	
	public static Result logIn(String uname, String pwd/*, String body*/) {
		// param 'body' i sledece dve linije su ako zelimo da pokupimo JSON iz requesta
		// param se mora zvati 'body'
//		Object json = new Gson().fromJson(body, Object.class);
//		System.out.println(json);
		
		//TODO: OVO VEZANO ZA TIMESTAMP ZA SVAKI REQUEST TREBA
		String tsString = request.headers.get("timestamp").value();
		String tsHashString = request.headers.get("timestamphash").value();
		
		int tsCheck = Utils.checkTimestamp(tsString, tsHashString);		
		if(tsCheck == 1) {
			return new BadRequest("Invalid request");
		}
		else if(tsCheck == 2) {
			return new play.mvc.results.Error("Request processing failed");
		}
		else if(tsCheck == 3) {
			return new BadRequest("Request timed out");
		}
		//KRAJ TIMESTAMP PROVERE
		
		User loggedUser = User.find("byUsername", uname).first();
		if (loggedUser == null) {
			return new BadRequest("Invalid login");
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
