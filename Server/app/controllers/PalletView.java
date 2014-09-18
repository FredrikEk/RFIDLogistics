package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.PalletView.*;

import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class PalletView extends Controller {

    /**
     * Defines a form wrapping the Pallet class.
     */ 
    final static Form<Pallet> palletForm = form(Pallet.class);
    final static Form<SetOfArticle> setOfArticleForm = form(SetOfArticle.class);

    /**
     * Display a blank form, where you can add new pallets.
     */ 
    
    public static Result newForm() {
        return ok(newForm.render(palletForm, setOfArticleForm));
    }

    public static Result list() {
        return ok(list.render(Pallet.find.orderBy("timeEntrance").findList(), Pallet.getSlotMap()));
    }
    
    public static Result palletLog(int id) {
        return ok(palletLog.render(id, Log.find.where("(entity='pallet' OR entity='move') AND identifier = '"+id+"'").orderBy("-date").findList()));
    }
    
    /**
     * Edit a pallet.
     *
     * @param id The ID  of the pallet to edit.
     */
    public static Result editForm(int id) {
        Pallet pallet = Pallet.find.byId(id);
        List<SetOfArticle> list = pallet.getArticles();
        
        DynamicForm form = new DynamicForm();
        Map<String, String> initialData = new HashMap<>();
        initialData.put("id", Integer.toString(pallet.getId()));
        initialData.put("tag1", pallet.getTag1().getId());
        initialData.put("tag2", pallet.getTag2().getId());
        if(list.size()>0){
		    SetOfArticle setOfArticle = list.get(0);
		    initialData.put("amount", Integer.toString(setOfArticle.getAmount()));
		    initialData.put("article", setOfArticle.getArticle().getId());
        }
        form = form.fill(initialData);
        return ok(editForm.render(id, form));
    }
    
    /**
     * Edit a pallet
     */
    public static Result update(int id) {
        DynamicForm requestData = Form.form().bindFromRequest();
        final String amount = requestData.get("amount");
        final String article = requestData.get("article");
        Pallet pallet = Pallet.find.byId(id);
        SetOfArticle setOfArticle = pallet.getArticles().get(0);
        String message = "Changed pallet %1$s's article from %2$s to " + "%3$s and amount from %4$s to %5$s.";
        flash("success", String.format(message, id, setOfArticle.getArticle().getId(), article, setOfArticle.getAmount(),
                amount));
        setOfArticle.setArticle(Article.find.byId(article));
        setOfArticle.setAmount(Integer.parseInt(amount));
        setOfArticle.update();
        return redirect(controllers.routes.PalletView.list());
    }
  
    /**
     * Handling submitting of new pallets.
     */
    
    public static Result submit() {
        Pallet pallet = palletForm.bindFromRequest().get();
        SetOfArticle sOA = setOfArticleForm.bindFromRequest().get();
        pallet.setTimeEntrance(new Date());
        sOA.setPallet(pallet);
        Ebean.beginTransaction();
        try {
            pallet.getTag1().save();
            pallet.getTag2().save();
            pallet.save();
            sOA.save();
            Ebean.commitTransaction();
        } catch (PersistenceException e) {
            flash("danger", "Pallet couldn't be created.");
            return badRequest(newForm.render(palletForm, setOfArticleForm));
        } finally {
            Ebean.endTransaction();
        }
        flash("success", "Pallet with IDs: " + pallet.getTag1().getId()+ ", " + pallet.getTag2().getId() +
                " and " + sOA.getAmount() + " pieces of " +
        Article.find.byId(sOA.getArticle().getId()).getName() + " added to database!");
        return redirect(controllers.routes.PalletView.list());
    }

    
    public static Result delete(int id) {
    	Pallet pallet = Pallet.find.byId(id);
    	try {
    		List<MovedPallet> moves = MovedPallet.find.where("pallet_id='"+id+"'").findList();
    		for (MovedPallet m : moves){
    			m.delete();
    		}
    		pallet.delete();
			
        }catch(PersistenceException e){
        	flash("danger", "could not remove: " +e.getMessage());
        	return redirect(controllers.routes.PalletView.editForm(id));
        }
    	List<SetOfArticle> set = pallet.getArticles();
    	if(set.size()>0){
    		SetOfArticle soa = set.get(0);
    		flash("success", "Pallet with id " + pallet.getId() + "with "+soa.getAmount()+"of "+soa.getArticle()+"(s)  has been removed from db.");
    	}else{
    		flash("success", "Pallet with id " + pallet.getId() + " has been removed from db.");
    	}
    	return redirect(controllers.routes.PalletView.list());
    }
}
