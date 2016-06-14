package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void submitToArchive() {
    	System.out.println("-------------Submission received, commencing parse-----------");
    	System.out.println("-------------Submission stored-----------");
    }

}