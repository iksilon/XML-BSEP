package controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import models.User;
import play.cache.Cache;
import play.mvc.results.BadRequest;
import play.mvc.results.Error;
import play.mvc.results.NotFound;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;
import utils.hashAndSaltUtils.HashAndSaltUtil;

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
//		if(!msgNumOk || timestampCheckResult != 0) {
//			return new BadRequest("Invalid request");
//		}
//		else if(timestampCheckResult == 2) {
//			return new play.mvc.results.Error("Request processing failed");
//		}
//		else if(timestampCheckResult == 3) {
//			return new BadRequest("Request timed out");
//		}
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
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return new Error("Request processing failed");
		}

		Object msgNum = null;
		if((msgNum = Cache.get(loggedUser.username + "msgNum")) == null) {
			loggedUser.msgNum = 0L;
			Cache.set(loggedUser.username + "msgNum", loggedUser.msgNum);
		}
		else {
			loggedUser.msgNum = Long.parseLong(msgNum.toString());
		}
		
		ObjectMapper om = new ObjectMapper();		
		try {
			Cache.set(loggedUser.username, loggedUser);
			return new RenderJson(om.writeValueAsString(loggedUser));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			Cache.delete(loggedUser.username);
			return new Error("Unable to log in");
		}
	}
	
	public static Result logOut(String uname) {
		Cache.delete(uname);
		return new Ok();
	}
	
	public static Result loginCheck(String body) {
		User user = new Gson().fromJson(body, User.class);
		if(user == null) {
			return new NotFound("user");
		}
		System.out.println(user.username);
		
		User cached = (User) Cache.get(user.username);
		if(cached != null && cached.id == user.id) {
			System.out.println(user.username);
			Object oMsgNum = Cache.get(user.username + "msgNum");
			if(oMsgNum == null) {
				cached.msgNum = 0L;
//				Cache.set(cached.username, cached);
//				Cache.set(cached.username + "msgNum", cached.msgNum);
			}
			
			Long msgNum = Long.parseLong(oMsgNum.toString());
			if(cached.msgNum != msgNum) {
				cached.msgNum = msgNum;
			}
			Cache.set(cached.username, cached);
			Cache.set(cached.username + "msgNum", cached.msgNum);
			
			ObjectMapper om = new ObjectMapper();
			try {
				return new RenderJson(om.writeValueAsString(cached));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return new Error("om");
			}
		}
		
		return new NotFound("cuser");
	}
}
