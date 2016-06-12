package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.google.gson.annotations.Expose;

import play.db.jpa.Model;

@Entity
public class Permission extends Model {
	public String name;

	@ManyToMany(mappedBy = "permissions")
	@Expose
	public List<Role> roles = new ArrayList<Role>();

	public Permission() {
		super();
	}

	public Permission(String name) {
		super();
		this.name = name;
	}
}
