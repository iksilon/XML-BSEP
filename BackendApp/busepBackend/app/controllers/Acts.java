package controllers;

import play.mvc.Controller;
import play.mvc.results.*;
public class Acts extends Controller {
	
	public static Result newAct() {
		return new Ok();
	}
}
