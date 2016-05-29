package controllers;

import play.mvc.Controller;
import play.mvc.results.Ok;
import play.mvc.results.Result;

/**
 * Created by Nemanja on 5/29/2016.
 */
public class AddUser extends Controller {

	public static Result addUser(String uname, String role, String passwd){

		return new Ok();
	}
}
