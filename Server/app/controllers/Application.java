package controllers;

import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin;
import views.html.index;
import views.html.login;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.avaje.ebean.Ebean;

import static util.Tools.isAuthenticated;

public class Application extends Controller {

    public static Result index() {
        if (isAuthenticated()) {
        	
        	
        	
            return ok(admin.render("", getPalletErrorMap()));
        } else {
            return ok(index.render(""));
        }
    }

    public static Result login() {
        return ok(login.render(new DynamicForm()));
    }
    
    public static Result authenticate() {
        DynamicForm requestData = Form.form().bindFromRequest();
        final String email = requestData.get("email");
        final String password = requestData.get("password");
        User user = User.find.byId(email);

        if (user == null) {
            DynamicForm form = new DynamicForm();
            Map<String, String> initialData = new HashMap<>();
            initialData.put("email", email);
            form = form.fill(initialData);
            form.reject("email", "A user with that email address doesn't exist.");
            return unauthorized(login.render(form));
        } else if(User.checkPassword(password, user.getPassword())){
            session().clear();
            session("email", email);
            flash("success", String.format("Successfully logged in as %1$s.", email));
            return redirect(controllers.routes.Application.index());
        } else {
            DynamicForm form = new DynamicForm();
            Map<String, String> initialData = new HashMap<>();
            initialData.put("email", email);
            form = form.fill(initialData);
            form.reject("password", "Wrong password.");
            return unauthorized(login.render(form));
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.index());
    }
    
    
    public static HashMap<Integer,String> getPalletErrorMap(){
    	HashMap<Integer,String> map; 
    	map = new HashMap<Integer, String>();
    	try(Statement s = Ebean.beginTransaction().getConnection().createStatement()){        	
    		ResultSet rs = s.executeQuery("select * from pallets_on_move");
        	while(rs.next()){
        		Integer p = rs.getInt("pallet_id");
        		StringBuilder sb = new StringBuilder();
        		sb.append("Pallet: ");
        		sb.append(p);
        		sb.append(" was taken from slot ");
        		String ps = rs.getString("position");
        		sb.append(ps);
        		sb.append(" and hasn't been put down again.");
        		map.put(p,sb.toString());
        	}
        	rs = s.executeQuery("select * from pallet_on_floor");
        	while(rs.next()){
        		Integer p = rs.getInt("pallet_id");
        		StringBuilder sb = new StringBuilder();
        		sb.append("Pallet: ");
        		sb.append(p);
        		sb.append(" was placed on the floor and hasn't been lifted again.");
        		map.put(p,sb.toString());
        	}
        }catch(SQLException e){
        	;
        }finally {
        	Ebean.endTransaction();
        }
    	return map;
    }
    
}