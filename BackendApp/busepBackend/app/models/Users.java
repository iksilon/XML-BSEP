package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Users extends Model {
	
	@Column(length = 30, unique = true, nullable = false)
	public String username;
	@Column(nullable = false)
	public String password;
	@Column(name = "PASSWORD_SALT")
	public String salt;
	
	@Column(length = 150)
	public String name;
	@Column(name = "LAST_NAME", length = 150)
	public String lastName;
	
	@ManyToOne
	public Role role;

	public Users(String username, String password) {
		super();
		//TODO implementirati salt and hash
		this.username = username;
		this.password = password;
	}
}
