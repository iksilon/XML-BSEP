package controllers;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.Role;
import models.User;
import play.mvc.results.RenderJson;
import play.mvc.results.Result;

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
		final GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
//	    final Gson gson = builder.create();
		return new RenderJson(builder.create().toJson(users, List.class));
	}
	
//	public static Result usersList() {
//		List<User> predsednici = User.find("byRoleId", 1).fetch();
//		List<User> odbornici = User.find("byRoleId", 2).fetch();
//		
//		predsednici.addAll(odbornici);
//		return new RenderJson(new Gson().toJson(predsednici));
//	}
}
