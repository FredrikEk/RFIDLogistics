package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Tag extends Model {

    @Id
    @Column(length = 22)
    private String id;

    public Tag(String id) {
        this.id = id;
    }

    public static Finder<String, Tag> find = new Finder<>(String.class, Tag.class);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o){
    	if(o.getClass()==this.getClass()){
    		Tag cmp = (Tag)o;
    		return this.id.equals(cmp.id);   			
    	}
    	return false;
    }
}
