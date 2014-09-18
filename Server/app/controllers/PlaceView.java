package controllers;

import java.util.List;

import models.MovedPallet;
import models.PalletSlot;
import models.Tag;
import play.mvc.Security;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.PlaceView.*;

import javax.persistence.PersistenceException;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class PlaceView extends Controller {

    /**
     * Defines a form wrapping the PalletSlot class.
     */ 
    final static Form<PalletSlot> placeForm = form(PalletSlot.class);

    /**
     * Display a list of pallet slots.
     */
    public static Result list() {
        return ok(list.render(PalletSlot.find.where("position <> 'floor'").orderBy("position").findList(), PalletSlot.getPalletMap()));
    }

    /**
     * Display a blank form, where you can add new places.
     */
    public static Result newForm() {
        return ok(newForm.render(placeForm));
    }

    /**
     * Edit current places.
     */
    public static Result editForm(String id) {
    	PalletSlot ps = PalletSlot.find.byId(id);
    	if (ps == null){
    		flash("danger", "Palletslot is gone..");
            return redirect(controllers.routes.PlaceView.list());
    	}
    	Form<PalletSlot> placeForm = form(PalletSlot.class).fill(ps);
        return ok(editForm.render(id, placeForm));
    }
    
    public static Result update(String id) {
    	PalletSlot palletslot = placeForm.bindFromRequest().get();
    	PalletSlot oldps =PalletSlot.find.byId(id);
    	
    	if(palletslot.getPosition().equals(id)){//Position name is the same
    		if(oldps.getTag() != null){
    			if (palletslot.getTag().getId().equals(oldps.getTag().getId())){
        			return redirect(controllers.routes.PlaceView.list());
        		}
    		}
    		Tag oldt = oldps.getTag(), newt = palletslot.getTag();
    		oldps.setTag(newt);
    		if(Tag.find.byId(newt.getId())==null){
    			newt.save();
    			oldps.save();
    			if(oldt != null){
    				oldt.delete();
    			}
    		}else{
    			oldps.save();
    		}
    	} else{//Position name is different
    		Tag oldt = oldps.getTag(), newt = palletslot.getTag();
    		if(oldt == null){
    			newt.save();
    		} else {
				if(!oldt.equals(newt)){
					if (Tag.find.byId(newt.getId())!=null){	
		    			flash("danger", "Tag is in use somewhere else.");
		                return badRequest(editForm.render(id, placeForm));
					}
    			}
				oldps.setTag(null);
    			oldps.update();
    			oldt.delete();
    		}
    		palletslot.save();
    		List<MovedPallet> moves = MovedPallet.find.where("location_position='"+id+"'").findList();
    		for (MovedPallet m : moves){
    			m.setLocation(palletslot);
    			m.update();
    		}
    		oldps.delete();
    	}
    	return redirect(controllers.routes.PlaceView.list());
    }
  
    /**
     * Handling submitting of new places.
     */
    public static Result submit() {
        PalletSlot palletSlot = placeForm.bindFromRequest().get();
        try {
            palletSlot.getTag().save();
            palletSlot.save();
        } catch (PersistenceException e) {
            flash("danger", "Pallet Slot couldn't be created.");
            return badRequest(newForm.render(placeForm));
        }
        flash("success", "Palletslot: " + palletSlot.getPosition() + " with the tag id: " +
                palletSlot.getTag().getId() + " added to database!");
        return redirect(controllers.routes.PlaceView.list());
    }

    /**
     * Submit form to delete a pallet slot.
     */
    public static Result delete(String id) {
    	PalletSlot palletslot = PalletSlot.find.byId(id);
    	Tag t = palletslot.getTag();
    	try {
    		List<MovedPallet> moves = MovedPallet.find.where("location_position='"+id+"'").findList();
    		for (MovedPallet m : moves){
    			m.delete();
    		}
    		palletslot.delete();
        }catch(PersistenceException e){
        	flash("danger", "could not remove: " +e.getMessage());
        	return redirect(controllers.routes.PlaceView.editForm(id));
        }
    	flash("success", palletslot.getPosition() +" is removed from database");
    	return redirect(controllers.routes.PlaceView.list());
    }
}