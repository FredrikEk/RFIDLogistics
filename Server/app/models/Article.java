package models;

import play.db.ebean.Model;

import javax.persistence.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A product with a unique article id.

 */
@Entity
public class Article extends Model {

    /**
     * A unique article ID.
     */
    @Id
    @Column(length = 30)
    private String id;

    /**
     * An optional name to make the article more readable for display.
     *
     * It's optional since it should be possible to put articles into the database without knowing their name
     * beforehand. For example shouldn't the system halt when you scan a pallet entering the warehouse which lacks a
     * known name.
     */
    @Basic(optional = true)
    @Column(length = 50)
    private String name;

    /**
     * Sets of articles this article is part of.
     *
     * A `SetOfArticle` object has a foreign key in `Article` and an integer specifying the number of units of the
     * product. This is a reverse mapping of that relationship, to easily access the sets this object is part of.
     */
    @OneToMany(mappedBy = "article")
    private List<SetOfArticle> partOfSets;

    /**
     * @param id A unique article ID.
     */
    public Article(String id) {
        this.id = id;
    }

    /**
     * @param id A unique article ID.
     * @param name An optional name.
     */
    public Article(String id, String name) {
        this(id);
        this.name = name;
    }

    public static Finder<String, Article> find = new Finder<>(String.class, Article.class);

    /**
     * Get a map of IDs and names.
     *
     * @return A map of all articles where the ID is the key and its name the value. If the name is null the value will
     *         be the ID instead.
     */
    public static Map<String, String> getIdNameMap() {
        Map<String, String> options = new HashMap<>();
        for (Article article : Article.find.all()) {
            options.put(article.getId(), article.toString());
        }
        
        return options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SetOfArticle> getPartOfSets() {
        return partOfSets;
    }

    public void setPartOfSets(List<SetOfArticle> partOfSets) {
        this.partOfSets = partOfSets;
    }

    /**
     * Return the name if isn't null, otherwise the ID.
     */
    @Override
    public String toString() {
        return getName() == null ? getId() : getName();
    }
    
    
    @Override
    public boolean equals(Object o){
    	if(this.getClass() == o.getClass()){
    		Article obj = (Article)o;
    		return this.getId().equals(obj.getId()) && this.getName().equals(obj.getName());
    	}
    	return false;
    }

}

