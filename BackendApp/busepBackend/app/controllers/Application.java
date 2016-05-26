package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	//Ovo je potrebno da bismo imali primer pristupanja bazi
    	//Ukoliko ovo želiš da pokreneš, odkomentariši prvi get u conf.routes fajlu
    	System.out.println("index");
    	List<Users> users = Users.findAll();
    	
    	Users us1 = new Users("us1", "dasdsa");
    	us1.save();
    	
    	
    	Role rol1 = new Role("role1");
    	rol1.save();
    	
    	us1.role = rol1;
    	us1.save();
    	
    	Permission p1 = new Permission("p1");
    	p1.save();
    	
    	p1.roles.add(rol1);
    	p1.save();
    	
    	Users.find("byUsernameAndName", "username", "name" ).first();
    	
    	
    	
    	
    	
    }

}