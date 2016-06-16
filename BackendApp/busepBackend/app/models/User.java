package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;

import play.db.jpa.Model;

@Entity(name="Users")
public class User extends Model {

	@Column(length = 254, unique = true, nullable = false)
	@Expose
	public String username;

	@Column(length = 150, nullable = false)
	@JsonIgnore
//	@Expose(serialize=false, deserialize=false)
	public String password;

	@Column(name = "PASSWORD_SALT")
	@JsonIgnore
//	@Expose(serialize=false, deserialize=false)
	public String salt;

	@Column(length = 150, nullable = false)
	@Expose
	public String name;
	
	@Column(name = "LAST_NAME", length = 150, nullable = false)
	@Expose
	public String lastName;

	@ManyToOne(fetch=FetchType.EAGER)
	@Expose
	public Role role;

	@Expose
	public long msgNum = 0L;
	
	public User() {
		super();
	}

	public User(String username, String password) {
		super();
		// TODO implementirati salt and hash
		this.username = username;
		this.password = password;
	}
}
