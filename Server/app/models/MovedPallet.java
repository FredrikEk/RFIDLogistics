package models;


import play.db.ebean.Model;

import javax.persistence.*;

import java.util.Date;

/**
 * Relationship between trucks moving pallets.
 *
 * This class models a pallet's position in a point in time.
 */
@Entity
public class MovedPallet extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    private Pallet pallet;

    @Basic(optional = false)
    private Date date;

    @ManyToOne(optional = false)
    private PalletSlot location;

    @ManyToOne(optional = false)
    private Reader reader;

    /**
     * Specifies if it was a move that put the pallet down on a slot.
     *
     * True if it was put on a slot.
     * False if it was lifted from a slot.
     */
    @Basic
    private Boolean putDown;

    /**
     * Specify where a pallet has been located at a given point in time.
     */
    public MovedPallet(Pallet pallet, Date date, PalletSlot location, Reader reader, boolean putDown) {
        this.pallet 	= pallet;
        this.date 		= date;
        this.location 	= location;
        this.reader 	= reader;
        this.putDown 	= putDown;
    }

    public static Finder<String, MovedPallet> find = new Finder<>(String.class, MovedPallet.class);

    public long getId(){
    	return this.id;
    }
    
    public Pallet getPallet() {
        return pallet;
    }

    public void setPallet(Pallet pallet) {
        this.pallet = pallet;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PalletSlot getLocation() {
        return location;
    }

    public void setLocation(PalletSlot location) {
        this.location = location;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Boolean getPutDown() {
        return putDown;
    }

    public void setPutDown(Boolean putDown) {
        this.putDown = putDown;
    }

}
