package controllers;
import models.Log;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.logs;

public class LogView extends Controller {
	
	
	//@Security.Authenticated(Secured.class)
    public static Result logs() {
        return ok(logs.render(Log.find.orderBy("date DESC, id DESC").findList()));
    }
}
