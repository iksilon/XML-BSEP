package controllers;

import models.Role;
import models.User;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.Result;

/**
 * Created by Nemanja on 5/29/2016.
 */
public class AddUser extends Controller {

	public static Result addUser(String uname, String role, String passwd){

		User newUser = User.find("byUsername", uname).first();

		if (newUser != null) {
			return new BadRequest("Invalid username");
		}

		newUser = new User();
		
		newUser.role = Role.find("byName", role).first();
		if (newUser.role == null){
			return new BadRequest("Invalid role");
		}
		
		newUser.username = uname;
		newUser.password = passwd;
		
		newUser.save();

		return new Ok();
	}
}
