package models;

import java.io.File;
import java.sql.Date;
import java.util.Calendar;

import play.db.jpa.Model;

public class Document extends Model {
	
	public int id;
	public File file;
	public String type;
	public String path; // relativno/apsolutno?
	public Date changeDate; // kao java.sql.Date ili java.util.Date  ???
	
	public Document(File file, String type) {
		super();
		this.file = file;
		this.type = type;
		changeDate = (Date) Calendar.getInstance().getTime();
	}
}
