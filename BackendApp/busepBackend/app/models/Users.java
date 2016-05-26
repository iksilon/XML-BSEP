package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Users extends Model {
	public String username;
	public String password;
	public String salt;
	
	public String name;
	public String surname;
	
	@ManyToOne
	public Role role;

	public Users(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
}
