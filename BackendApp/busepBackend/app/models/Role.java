package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import play.db.jpa.Model;

@Entity
public class Role extends Model {
	
	@Column(length = 50, nullable = false)
	public String name;

	@OneToMany(fetch=FetchType.EAGER, mappedBy="role")
	@JsonIgnore
	public List<User> users = new ArrayList<User>();
	
	@ManyToMany
	@JoinTable(name="ROLE_PERMISSIONS")
	@JsonIgnore
	public List<Permission> permissions = new ArrayList<Permission>();
	
	public Role() {
		super();
	}
	
	public Role(String name) {
		super();
		this.name = name;
	}

}
