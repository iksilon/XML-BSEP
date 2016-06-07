package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	public static void index() {
		// Ovo je potrebno da bismo imali primer pristupanja bazi
		// Ukoliko ovo želiš da pokreneš, odkomentariši prvi get u routes fajlu
		// Ne, nego napravis dugme/link na frontendu, i das mu route koji ce pozvati
		// Application.index, ali tako da route nije u konfliktu sa ostalim
		// rutama koje koriste angular i play
//		System.out.println("index");
//		List<User> users = User.findAll();
//
//		User us1 = new User("us1", "dasdsa");
//		us1.save();
//
//		Role rol1 = new Role("role1");
//		rol1.save();
//
//		us1.role = rol1;
//		us1.save();
//
//		Permission p1 = new Permission("p1");
//		p1.save();
//
//		p1.roles.add(rol1);
//		p1.save();
//
//		User.find("byUsernameAndPassword", "us1", "dasdsa").first();
	}

}