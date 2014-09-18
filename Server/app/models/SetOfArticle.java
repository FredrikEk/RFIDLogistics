package models;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Min;

/**
 * This class models a number of units of a specific `Article` that's placed on a specific `Pallet`. This allows for
 * multiple types of articles being places on the same pallet.
 */
@Entity
public class SetOfArticle extends Model {

    /**
     * An ID seems to be needed for Ebean to work properly, even though it doesn't make sense in this case.
     *
     * Otherwise won't, for example, the reverse relation from Pallet to SetOfArticle work when there are no
     * SetOfArticle pointing at the Pallet.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment the id
    private int id;

    /**
     * An article.
     */
    
    @ManyToOne(optional = false)
    private Article article;

    /**
     * The number of units of the article.
     */
    @Basic(optional = false)
    @Min(0)
    private int amount;

    /**
     * The pallet the set of articles is placed on.
     */
    @ManyToOne(optional = false)
    private Pallet pallet;
    
    public static Finder<Long, SetOfArticle> find = new Finder<>(Long.class, SetOfArticle.class);

    public SetOfArticle(Article article, int amount, Pallet pallet) {
        if(article== null || pallet == null){
        	throw new IllegalArgumentException("Not allowed to have article or pallet as null");
        }
    	this.article = article;
        this.amount = amount;
        this.pallet = pallet;
    }

    public long getId() {
        return id;
    }
    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Pallet getPallet() {
        return pallet;
    }

    public void setPallet(Pallet pallet) {
        this.pallet = pallet;
    }

}
