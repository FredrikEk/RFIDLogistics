package models;

import java.util.HashMap;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;

import com.avaje.ebean.Ebean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The entity for slot for pallets in storage area.
 */
@Entity
public class PalletSlot extends Model {

    @Id
    @Column(length = 30)
    private String position;

    // `optional = true` so it can exist in the system without a tag
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private Tag tag;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<MovedPallet> moves;

    public PalletSlot(String position) {
        
    	this.position = position;
    }

    public PalletSlot(String position, Tag tag) throws IllegalArgumentException {
        this(position);
        if(Pallet.find.where("tag1_id = '"+tag.getId()+"' OR tag2_id = '"+tag.getId()+"'" ).findList().size()>0){
    		throw new IllegalArgumentException("Tag is already in use on Pallet.");
    	}
    	if(PalletSlot.find.where("tag_id = '"+tag.getId()+"'").findList().size()>0){
    		throw new IllegalArgumentException("Tag is already in use on PalletSlot.");
    	}
        this.tag = tag;
    }

    public PalletSlot(String position, String tag) throws IllegalArgumentException {
        this(position);
        Tag t;
        t = Tag.find.byId(tag);
        if (t != null) {
            throw new IllegalArgumentException("Tag is already in use");
        }
        t = new Tag(tag);
        t.save();
        this.tag = t;
    }
    
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * @return The slot's unique position ID.
     */
    @Override
    public String toString() {
        return position;
    }


    public static Finder<String, PalletSlot> find = new Finder<>(String.class, PalletSlot.class);
	
	/**
	 * Full map of Slot and Pallet combinations.
	 * 
	 * @return map with Position as key, Palle as value
	 */
	public static HashMap<String,Pallet> getPalletMap(){
		HashMap<String,Pallet> map; 
		map = new HashMap<String, Pallet>();
		try(Statement s = Ebean.beginTransaction().getConnection().createStatement()){        	
			ResultSet rs = s.executeQuery("select * from pallet_on_slot");
	    	while(rs.next()){
	    		Integer pid = rs.getInt("pallet_id");
	    		String ps = rs.getString("position");
	    		map.put(ps,Pallet.find.byId(pid));
	    	}
	    }catch(SQLException e){
	    	;
	    }finally {
	    	Ebean.endTransaction();
	    }
		return map;
	}
}