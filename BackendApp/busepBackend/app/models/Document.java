package models;

import play.db.jpa.Model;

public class Document extends Model {
	
	public String name;
	public String type;
	public String path; // relativno/apsolutno?
	
	public Document(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
}
