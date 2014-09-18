package models;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class Log extends Model {
	public static Finder<String, Log> find = new Finder<>(String.class, Log.class);

	/**
	 * Needed to order the events properly. Ordering by only the date becomes a problem when two events happen on the
	 * same date.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment the id
	private int id;

	@Column(length=20)
	private String entity;

	@Column(length=30)
	private String identifier;

	@Column(length=255)
	private String event;

	private int changeType;

	private Date date;

	public Log(){
		;
	}
	
	public Log(String entity, String identifier, String event, int changeType, Date date){
		this.entity=entity; 
		this.identifier=identifier;
		this.event=event;
		this.changeType = changeType;
		this.date=date;
	}
	
	public String getEntity(){
		return entity;
	}
	public String getIdentifier(){
		return identifier;
	}
	public String getEvent(){
		return event;
	}
	public int getChangeType(){
		return changeType;
	}
	
	public Date getDate(){
		return date;
	}
	public String getFormattedDate(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}
}
