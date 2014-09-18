package controllers;

import java.util.List;

import models.Article;
import models.SetOfArticle;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.ArticleView.*;

import javax.persistence.PersistenceException;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class ArticleView extends Controller {

    /**
     * Defines a form wrapping the Article class.
     */
    private final static Form<Article> articleForm = form(Article.class);

    public static Result list() {
        return ok(list.render(Article.find.all()));
    }

    /**
     * Display a blank form, where you can add new articles.
     */
   
    public static Result newForm() {
        return ok(newform.render(articleForm));
    }

    /**
     * Handling submitting of new articles
     */
    
    public static Result submit() {
        Article article = articleForm.bindFromRequest().get();
        try {
            article.save();
        } catch (PersistenceException e) {
            flash("danger", "Article couldn't be created.");
            return badRequest(newform.render(articleForm));
        }
        flash("success", String.format("Article: %1$s with article number %2$s added to database!",
                                       article.getName(), article.getId()));
        return redirect(controllers.routes.ArticleView.list());
    }

    
    public static Result editForm(String id) {
        Form<Article> articleForm = form(Article.class).fill(Article.find.byId(id));
        return ok(editform.render(id, articleForm));
    }

    
    public static Result update(String id) {
        Article article = articleForm.bindFromRequest().get();
        Article oldArticle = Article.find.byId(id);
        if(id.equals(article.getId())){
        	flash("success", String.format("Changed article %1$s's name from %2$s to %3$s.",
                    id, oldArticle.getName(), article.getName()));
			oldArticle.setName(article.getName());
			oldArticle.save();
        }else{
            flash("success", String.format("Changed article %1$s's ID to %2$s and it's name from %3$s to %4$s.",
                    id, article.getId(), oldArticle.getName(), article.getName()));
        	java.util.List<SetOfArticle> list = oldArticle.getPartOfSets();
        	article.save();
        	for(SetOfArticle soa : list){
    			soa.setArticle(article);
    			soa.update();
    		}
        	oldArticle.delete();
        }
        return redirect(controllers.routes.ArticleView.list());
    }

    
    public static Result delete(String id) {
        Article article = Article.find.byId(id);
        List<SetOfArticle> sets = SetOfArticle.find.where("article_id='"+id+"'").findList();
        if(sets.size()>0){
        	flash("danger", String.format("Could not remove the article %1$s with article number %2$s, since it is in system on pallets from database!",
                    article.getName(), article.getId()));
        	return redirect(controllers.routes.ArticleView.editForm(id));
        }
        flash("success", String.format("Article: %1$s with article number %2$s removed from database!",
                                       article.getName(), article.getId()));
        article.delete();
        return redirect(controllers.routes.ArticleView.list());
    }
}
