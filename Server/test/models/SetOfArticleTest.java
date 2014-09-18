package models;

import javax.persistence.PersistenceException;

import org.junit.Test;

import app.AbstractBaseApp;
import static org.fest.assertions.Assertions.assertThat;

public class SetOfArticleTest extends AbstractBaseApp{
	private Tag t1, t2, t3, t4;
	private Pallet p1;
	private Article a1;
	
	
	@Test
	public void SetOfArticle(){
		initMemory();
		SetOfArticle soa = new SetOfArticle(a1, 20, p1);
		soa.save();
		assertThat(soa).isEqualTo(SetOfArticle.find.byId(soa.getId()));
		p1.refresh();
		assertThat(soa).isEqualTo(p1.getArticles().get(0));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void SetOfArticleNullArticle(){
		initMemory();
		SetOfArticle soa = new SetOfArticle(null, 20, p1);
		soa.save();
	}
	@Test(expected=IllegalArgumentException.class)
	public void SetOfArticleNullPallet(){
		initMemory();
		SetOfArticle soa = new SetOfArticle(a1, 20, null);
		soa.save();
	}
	
	
	private void initMemory(){
		t1 = new Tag("testtag1");
		t2 = new Tag("testtag2");
		t1.save();
		t2.save();
		p1 = new Pallet(t1,t2);
		p1.save();
		a1 = new Article("article1", "test article 1");
		a1.save();
	}

	
}
