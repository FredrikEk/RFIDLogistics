package controllers.api;

import com.avaje.ebean.Expr;
import models.Pallet;
import models.PalletSlot;
import models.Tag;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class TagView extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getTags() {
        return ok(Json.toJson(Tag.find.all()));
    }

    public static Result deleteTag(String tag) {
        Tag t = Tag.find.byId(tag);
        if (t == null) {
            return badRequest("The tag isn't in the system.");
        }
        // Try to delete a pallet using the tag
        List<Pallet> pallets = Pallet.find.where().or(Expr.eq("tag1.id", tag), Expr.eq("tag2.id", tag)).findList();
        if (pallets.size() > 0) {
            pallets.get(0).delete();
            return ok();
        }
        // Try to delete a pallet slot using the tag
        PalletSlot palletSlot = PalletSlot.find.where().eq("tag.id", tag).findUnique();
        if (palletSlot != null) {
            palletSlot.delete();
            return ok();
        }
        // Finally delete the tag itself if no pallet or slot was using it.
        // This call will only be reached if no pallet or slot was deleted.
        t.delete();
        return ok();
    }
}
