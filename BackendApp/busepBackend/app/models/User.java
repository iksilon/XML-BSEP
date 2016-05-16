package models;

import play.db.jpa.Model;

public class User extends Model {
	
	public int id;
	public String name;
	public String lastName;
	public String username;
	public String password;
	public String password_salt;
	public long role; // kasnije prebaciti u ManyToOne ili sta vec bude 
	
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
}
