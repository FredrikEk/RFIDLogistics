package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity
public class Pallet extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment the id
    private int id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @Column(unique = true) // doesn't work?
    private Tag tag1;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @Column(unique = true) // doesn't work?
    private Tag tag2;

    @Basic(optional = false)
    private Date timeEntrance;


    /**
     * Reverse relation from `SetOfArticle`.
     *
     * Multiple `SetOfArticle`s can point to the same pallet. This allows for placing multiple different types of
     * products on the same pallet.
     */
    @OneToMany(mappedBy = "pallet", cascade = CascadeType.ALL)
    private List<SetOfArticle> articles;

    @OneToMany(mappedBy = "pallet", cascade = CascadeType.ALL)
    private List<MovedPallet> moves;

    public Pallet(Tag tag1, Tag tag2) throws IllegalArgumentException{
        this(tag1, tag2, new Date());
    }

    public Pallet(Tag tag1, Tag tag2, String timeEntrance) throws ParseException, IllegalArgumentException {
        this(tag1, tag2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeEntrance));
    }

    public Pallet(final String tag1, final String tag2, String timeEntrance) throws ParseException, IllegalArgumentException {
        this.timeEntrance = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeEntrance);
        Ebean.beginTransaction();
        try {
            this.tag1 = new Tag(tag1);
            this.tag1.save();
            this.tag2 = new Tag(tag2);
            this.tag2.save();
            validateTagConstructorArguments(this.tag1, this.tag2);
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
    }

    public Pallet(Tag tag1, Tag tag2, Date timeEntrance) throws IllegalArgumentException {
        if (tag1 == null || tag2 == null) {
            throw new IllegalArgumentException("Tag is null");
        }
        validateTagConstructorArguments(tag1, tag2);
        setTag1(tag1);
        setTag2(tag2);
        setTimeEntrance(timeEntrance);
    }

    private void validateTagConstructorArguments(Tag tag1, Tag tag2) throws IllegalArgumentException {
        if(tag1.equals(tag2)){
            throw new IllegalArgumentException("Tags are not Unique");
        }
        //TODO: Make this easier to check
        if(Pallet.find.where("tag1_id = '"+tag1.getId()+"' OR tag1_id = '"+tag2.getId()+"' OR tag2_id = '"+tag1.getId()+"' OR tag2_id = '"+tag2.getId()+"'" ).findList().size()>0){
            throw new IllegalArgumentException("Tags are already in use on Pallet.");
        }
        if(PalletSlot.find.where("tag_id = '"+tag1.getId()+"' OR tag_id = '"+tag2.getId()+"'" ).findList().size()>0){
            throw new IllegalArgumentException("Tags are already in use on PalletSlot.");
        }
    }

    /**
     * Get the pallet slot the pallet is currently on.
     *
     * @return The pallet slot it's on. If it isn't on one, return null.
     */
    public PalletSlot getCurrentLocation() {
    	PalletSlot ps = null;
        try(Statement s = Ebean.beginTransaction().getConnection().createStatement()){
        	ResultSet rs = s.executeQuery("select * from pallets_on_slots where pallet_id='"+this.id+"'");
        	if(rs.next()){
        		String pallet = rs.getString("location_position");
        		ps = PalletSlot.find.byId(pallet);
        	}
        }catch(SQLException e){
        	;
        }finally {
        	Ebean.endTransaction();
        }
        return ps;
    }

    public static Finder<Integer, Pallet> find = new Finder<>(Integer.class, Pallet.class);

    public List<SetOfArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<SetOfArticle> articles) {
        this.articles = articles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tag getTag1() {
        return tag1;
    }

    public void setTag1(Tag tag1) throws IllegalArgumentException {
    	if(tag1==null){
    		throw new IllegalArgumentException("New tag is null");
    	}
        this.tag1 = tag1;
    }

    public Tag getTag2() {
        return tag2;
    }

    public void setTag2(Tag tag2) throws IllegalArgumentException {
    	if(tag2==null){
    		throw new IllegalArgumentException("New tag is null");
    	}
    	this.tag2 = tag2;
    }

    public Date getTimeEntrance() {
        return timeEntrance;
    }

    public String getFormattedTimeEntrance() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(timeEntrance);
    }

    public void setTimeEntrance(Date timeEntrance) {
        this.timeEntrance = timeEntrance;
    }

    /**
     * Full map of Pallets and slot-combinations.
     * 
     * @return map with PalletId as key, Location as value
     */
    public static HashMap<Integer,PalletSlot> getSlotMap(){
    	HashMap<Integer,PalletSlot> map; 
    	map = new HashMap<Integer, PalletSlot>();
    	try(Statement s = Ebean.beginTransaction().getConnection().createStatement()){        	
    		ResultSet rs = s.executeQuery("select * from pallet_on_slot");
        	while(rs.next()){
        		Integer p = rs.getInt("pallet_id");
        		String ps = rs.getString("position");
        		map.put(p,PalletSlot.find.byId(ps));
        	}
        }catch(SQLException e){
        	;
        }finally {
        	Ebean.endTransaction();
        }
    	return map;
    }
}
