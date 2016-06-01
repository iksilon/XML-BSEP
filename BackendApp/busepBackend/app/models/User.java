package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.jpa.Model;

@Entity(name="Users")
public class User extends Model {

	@Column(length = 30, unique = true, nullable = false)
	public String username;

	@Column(nullable = false)
	@JsonIgnore
	public String password;

	@Column(name = "PASSWORD_SALT")
	@JsonIgnore
	public String salt;

	@Column(length = 150)
	public String name;
	
	@Column(name = "LAST_NAME", length = 150)
	public String lastName;

	@ManyToOne(fetch=FetchType.EAGER)
//	@JsonIgnore
	public Role role;

	public User() {
		super();
	}

	public User(String username, String password) {
		super();
		// TODO implementirati salt and hash
		this.username = username;
		this.password = password;
	}

	/*public User(String username, String password, String roleName){
		super();
		//this.username = username;
		//this.password = password;
		//this.role = new Role(roleName);		
	}*/
}
