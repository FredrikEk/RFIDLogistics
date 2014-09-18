package controllers.api.helpers;

import com.avaje.ebean.Expr;
import models.Pallet;
import models.PalletSlot;

/**
 * This class makes it easier to determine if a (scanned) tag belongs to a pallet or a pallet slot.
 */
public class ScannedTag {

    /**
     * The tag.
     */
    private String tag;

    /**
     * Cached pallet.
     */
    private Pallet pallet = null;

    /**
     * Cached pallet slot.
     */
    private PalletSlot palletSlot = null;

    /**
     * Cache if the tag belongs to a pallet.
     */
    private Boolean isPallet = null;

    /**
     * Cache if the tag belongs to a pallet slot.
     */
    private Boolean isPalletSlot = null;

    /**
     * @param tag A scanned tag string.
     */
    public ScannedTag(String tag) {
        this.tag = tag;
    }

    /**
     * If the tag is a pallet.
     *
     * @return True or false.
     */
    public boolean isPallet() {
        checkPallet();
        return isPallet;
    }

    /**
     * Get the tag's pallet.
     *
     * @return The pallet if it's a pallet, or null.
     */
    public Pallet getPallet() {
        checkPallet();
        return pallet;
    }

    /**
     * Check if the tag is a pallet and store the result/cache as a couple of attributes.
     */
    private void checkPallet() {
        if (isPallet == null) {
            pallet = Pallet.find.where().or(Expr.eq("tag1.id", tag), Expr.eq("tag2.id", tag)).findUnique();
            isPallet = pallet != null;
        }
    }

    /**
     * If the tag is a pallet slot.
     *
     * @return True or false.
     */
    public boolean isPalletSlot() {
        checkPalletSlot();
        return isPalletSlot;
    }

    /**
     * Get the tag's pallet slot.
     *
     * @return The pallet slot if it's a slot, or null.
     */
    public PalletSlot getPalletSlot() {
        checkPalletSlot();
        return palletSlot;
    }

    /**
     * Check if the tag is a pallet slot and store/cache the result as a couple of attributes.
     */
    private void checkPalletSlot() {
        if (isPalletSlot == null) {
            if (tag.toLowerCase().equals("floor")) {
                palletSlot = getOrCreateFloorSlot();
            } else {
                palletSlot = PalletSlot.find.where().eq("tag.id", tag).findUnique();
            }
            isPalletSlot = palletSlot != null;
        }
    }

    /**
     * Get the PalletSlot with position "floor", and create it if it doesn't already exist.
     *
     * @return An instance of a PalletSlot.
     */
    public static PalletSlot getOrCreateFloorSlot() {
        PalletSlot palletSlot = PalletSlot.find.byId("floor");
        if (palletSlot == null) {
            palletSlot = new PalletSlot("floor");
            palletSlot.save();
        }
        return palletSlot;
    }
}

