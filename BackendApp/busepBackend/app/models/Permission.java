package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

@Entity
public class Permission extends Model {
	public String name;
	
	
	@ManyToMany(mappedBy = "permissions")
	public List<Role> roles = new ArrayList<Role>();


	public Permission(String name) {
		super();
		this.name = name;
	}
	

	
}
