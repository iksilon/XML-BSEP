package models;

import play.db.jpa.Model;

public class User extends Model {
	
	public String username;
	public String password;
	public String name;
	public String lastName;
	public String role; // kasnije prebaciti u tip ROLE, kad budemo imali to
	
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
}
