package controllers;

import java.util.List;

import com.google.gson.GsonBuilder;

import models.Role;
import models.User;
import play.cache.Cache;
import play.mvc.Http.Header;
import play.mvc.results.BadRequest;
import play.mvc.results.NotFound;
import play.mvc.results.RenderJson;
import play.mvc.results.RenderText;
import play.mvc.results.Result;
import utils.JWTUtils;

public class Utils extends AppController {
	
	public static String byteToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars= new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	}
	
	// prosledi role 0 za predsednika, 1 za odbornika itd
	public static Result usersByRole(long roleId) {
		Role userRole = Role.find("byId", roleId).first();
		List<User> users = User.find("byRole", userRole).fetch();
		if(users.isEmpty()) {
			users = User.findAll();
		}

//		Header hUname = request.headers.get("username");
//		if(hUname == null) {
//			return new BadRequest("Invalid user data");
//		}

		final GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
		
//		String uname = hUname.value();
//		User loggedUser = User.find("byUsername", uname).first();
//		String jwt = JWTUtils.generateJWT(loggedUser);
//		String json = "{\"users\": " + builder.create().toJson(users, List.class) 
//						+ ", \"token\": \"" + jwt + "\"}";
//		return new RenderText(json);
		

		return new RenderJson(builder.create().toJson(users, List.class));
	}
}
