package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Role extends Model {
	public String name;

	@OneToMany(mappedBy="role")
	public List<Users> users = new ArrayList<Users>();
	
	@ManyToMany
	@JoinTable(name="ROLE_PERMISSIONS")
	public List<Permission> permissions = new ArrayList<Permission>();
	
	public Role(String name) {
		super();
		this.name = name;
	}

}
