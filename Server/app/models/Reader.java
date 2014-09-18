package models;


import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The RFID reader entity
 */
@Entity
public class Reader extends Model {

    @Id
    @Column(length = 30)
    private String id;

    @Basic(optional = true)
    private String description;

    @OneToMany(mappedBy = "reader")
    private List<MovedPallet> moves;

    @OneToOne
    private PalletSlot lastScannedUnoccupiedSlot;

    @Basic
    private Date lastScannedUnoccupiedSlotDate;

    public Reader(String id) {
        this.id = id;
    }

    public Reader(String id, String description) {
        this(id);
        this.description = description;
    }

    public static Finder<String, Reader> find = new Finder<>(String.class, Reader.class);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MovedPallet> getMoves() {
        return moves;
    }

    public void setMoves(List<MovedPallet> moves) {
        this.moves = moves;
    }

    public PalletSlot getLastScannedUnoccupiedSlot() {
        return lastScannedUnoccupiedSlot;
    }

    public void setLastScannedUnoccupiedSlot(PalletSlot lastScannedUnoccupiedSlot) {
        this.lastScannedUnoccupiedSlot = lastScannedUnoccupiedSlot;
    }

    public Date getLastScannedUnoccupiedSlotDate() {
        return lastScannedUnoccupiedSlotDate;
    }

    public void setLastScannedUnoccupiedSlotDate(Date lastScannedUnoccupiedSlotDate) {
        this.lastScannedUnoccupiedSlotDate = lastScannedUnoccupiedSlotDate;
    }
}
