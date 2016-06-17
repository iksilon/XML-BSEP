package controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import com.google.gson.Gson;

import models.Role;
import models.User;
import play.mvc.Http.Header;
import play.mvc.results.BadRequest;
import play.mvc.results.Ok;
import play.mvc.results.RenderText;
import play.mvc.results.Result;
import utils.CsrfTokenUtils;
import utils.JWTUtils;
import utils.hashAndSaltUtils.HashAndSaltUtil;

/**
 * Created by Nemanja on 5/29/2016.
 */
public class AddUser extends AppController {

	public static Result addUser(String body){
		ArrayList<String> data = new Gson().fromJson(body, ArrayList.class);
		if(data == null) {
			return new BadRequest("No payload data");
		}
		
		String name = data.get(0);
		String lastname = data.get(1);
		String uname = data.get(2);
		String role = data.get(3);
		String password = data.get(4);
		if(name == null || name.trim().equals("")
				|| lastname == null || lastname.trim().equals("")
				|| uname == null || uname.trim().equals("")
				|| role == null || role.trim().equals("")
				|| password == null || password.trim().equals("")) {
			return new BadRequest("Invalid payload data");
		}
		
		//String uname, String role, String passwd
		Header hUsername = request.headers.get("username");
		if(hUsername == null) {
			return new BadRequest("Invalid issuing user data");
		}
		String username = hUsername.value();
		User loggedUser = User.find("byUsername", username).first();

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
			newUser.password = hasu.base64Encode(hasu.hashPassword(password, hasu.base64Decode(newUser.salt)));
			newUser.name = name;
			newUser.lastName = lastname;
			
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
		
		String user = session.get("user");
		String token = CsrfTokenUtils.generateToken(user);
		String json = "{\"token\": \"" + token + "\"}";
		return new RenderText(json);

//		return new Ok();
	}
}
