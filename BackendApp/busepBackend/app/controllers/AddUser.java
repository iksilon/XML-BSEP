package controllers;

import controllers.hashAndSaltUtils.HashAndSaltUtil;
import models.Role;
import models.User;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.Result;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Nemanja on 5/29/2016.
 */
public class AddUser extends AppController {

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

		HashAndSaltUtil hasu = new HashAndSaltUtil();
		try {
			newUser.salt = hasu.base64Encode(hasu.generateSalt());
			newUser.password = hasu.base64Encode(hasu.hashPassword(passwd, hasu.base64Decode(newUser.salt)));

			newUser.save();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new BadRequest("Unable to create new user due to serverError 1");
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return new BadRequest("Unable to create new user due to serverError 2");
		} catch (IOException e) {
			e.printStackTrace();
			return new BadRequest("Unable to create new user due to serverError 3");
		}

		return new Ok();
	}
}
