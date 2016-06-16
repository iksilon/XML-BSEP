package controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.User;
import play.cache.Cache;
import play.mvc.results.BadRequest;
import play.mvc.results.Error;
import play.mvc.results.NotFound;
import play.mvc.results.Ok;
import play.mvc.results.RenderJson;
import play.mvc.results.RenderText;
import play.mvc.results.Result;
import utils.JWTUtils;
import utils.hashAndSaltUtils.HashAndSaltUtil;

public class Login extends AppController {

	/**
	 * Metoda za logovanje korisnika
	 * @param body - JSON podaci u "telu" request objekta
	 * @return {@link play.mvc.results.Result} u zavisnosti od podataka u {@code body}
	 */
	public static Result logIn(String body) {
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		if(data == null) {
			return new BadRequest("No payload data");
		}
		
		String uname = data.get(0);
		if(uname == null || uname.trim().equals("")) {
			return new BadRequest("Invalid payload data");
		}
		
		String pwd = data.get(1);
		if(pwd == null || pwd.trim().equals("")) {
			return new BadRequest("Invalid payload data");
		}
		
		User loggedUser = User.find("byUsername", uname).first();
		if (loggedUser == null) {
			return new BadRequest("Invalid login");
		}

		HashAndSaltUtil hasu = new HashAndSaltUtil();
		try {
			if (!hasu.authenticate(pwd, loggedUser)) {
				return new BadRequest("Invalid login");
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			return new Error("Request processing failed");
		}

		Long msgNum = userMsgNum.get(loggedUser.username);
		if(msgNum == null) {
			loggedUser.msgNum = 0L;
			userMsgNum.put(loggedUser.username, loggedUser.msgNum);
		}
		else {
			loggedUser.msgNum = Long.parseLong(msgNum.toString());
		}
		
		session.put("user", loggedUser.username);
		String jwt = JWTUtils.generateJWT(loggedUser);
		Cache.set(loggedUser.username, loggedUser);
		String json = "{\"role\": \"" + loggedUser.role.name
						+ "\", \"username\": \"" + loggedUser.username
						+ "\", \"msgNum\": " + loggedUser.msgNum
						+ ", \"token\": \"" + jwt + "\"}";
		return new RenderText(json);
		
//		ObjectMapper om = new ObjectMapper();		
//		try {
//			Cache.set(loggedUser.username, loggedUser);
//			return new RenderJson(om.writeValueAsString(loggedUser));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//			Cache.delete(loggedUser.username);
//			return new Error("Unable to log in");
//		}
	}
	
	public static Result token(String body) {
//		String a = session.get("user");
//		if(a != null)
//			System.out.println("USER--------" + a);
		
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		if(data == null) {
			return new BadRequest("No payload data");
		}
		
		String jwt = data.get(0);
		if(jwt == null || jwt.trim().equals("")) {
			return new BadRequest("Invalid payload data");
		}

		String uname = data.get(1);
		if(uname == null || uname.trim().equals("")) {
			return new BadRequest("Invalid payload data");
		}
		
		User loggedUser = User.find("byUsername", uname).first();
		if (loggedUser == null) {
			return new BadRequest("Invalid token");
		}
		
		String username = JWTUtils.getAudience(jwt, loggedUser);
		if(username != null && !username.equals(uname)) {
			return new BadRequest("Invalid token");
		}
		
		if(!JWTUtils.checkJWT(jwt, loggedUser)) {
			return new BadRequest("Expired token");
		}

		Long msgNum = userMsgNum.get(loggedUser.username);
		if(msgNum == null) {
			loggedUser.msgNum = 0L;
			userMsgNum.put(loggedUser.username, loggedUser.msgNum);
		}
		else {
			loggedUser.msgNum = Long.parseLong(msgNum.toString());
		}

		session.put("user", loggedUser.username);
		jwt = JWTUtils.generateJWT(loggedUser);
		String json = "{\"role\": \"" + loggedUser.role.name
						+ "\", \"username\": \"" + loggedUser.username
						+ "\", \"msgNum\": " + loggedUser.msgNum
						+ ", \"token\": \"" + jwt + "\"}";
		return new RenderText(json);
	}
	
	public static Result logOut(String uname) {
		Cache.delete(uname);
		return new Ok();
	}
	
	public static Result loginCheck(String body) {
		User user = new Gson().fromJson(body, User.class);
		if(user == null) {
			return new NotFound("Unable to check user state");
		}
		
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
				return new Error("Request processing failed");
			}
		}

		return new NotFound("No such user"); 
	}
}
