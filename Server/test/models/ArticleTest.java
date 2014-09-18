package models;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.Test;

import app.AbstractBaseApp;

public class ArticleTest extends AbstractBaseApp{

	@Test
	public void createArticleAndUseGetters(){
		String id = "123testid";
		    	String name = "Test Article 1";
		       	Article article = new Article(id,name);
		       	assertEquals(name, article.getName());
		    	assertEquals(id, article.getId());
		    	article.save();
		    	Article a = Article.find.byId(id);
		    	assertNotNull(a);
		    	assertNull(Article.find.byId(name));
		    	assertEquals(name, a.getName());
		    	assertEquals(id, a.getId());
		    	assertTrue(a.getPartOfSets().size()==0);
		    	a.delete();
		    	a = Article.find.byId(id);
		    	assertNull(a);
         
	}
	
	@Test
	public void createManySameNameAndFindSpecific(){
		
			String findingid ="idstring1",testName = "testArticle 1";
			Article a1 = new Article(findingid ,  testName);
			Article a2 = new Article("idstring2",testName);
			Article a3 = new Article("idstring3",testName);
			Article a4 = new Article("idstring4",testName);
			Article a5 = new Article("idstring5",testName);
			a1.save();
			a2.save();
			a3.save();
			a4.save();
			a5.save();
			Article get = Article.find.byId(findingid);
			assertEquals(a1, get);
			List<Article> list = Article.find.where("name='"+testName+"'").findList();
			assertTrue(list.size()==5);
			a1.delete();
			a2.delete();
			a3.delete();
			a4.delete();
			a5.delete();
			list = Article.find.where("name='"+testName+"'").findList();
			assertTrue(list.size()==0);
		
	}
	
	@Test
	public void tryCreatingWithSameId(){
		
				String id = "testId", name1 = "testname1", name2 = "testname2";
				Article article1 = new Article(id, name1), article2 = new Article(id,name2);
				article1.save();
				assertEquals(article1, Article.find.byId(id));
				try{
					article2.save();
				}catch (Exception e ){
					assertTrue(e.getClass() == new PersistenceException().getClass());
				}
				assertTrue(Article.find.where("id='"+id+"'").findList().size()==1);
				Article found = Article.find.byId(id);
				//AssertNotEquals seems to be troublesome. Tried using article1, found but they appeared to match. 
				assertEquals(article1.getName(), found.getName());
				assertNotEquals(article2.getName(), found.getName());
				article1.delete();
				assertTrue(Article.find.byId(id)==null);

	}
	
	@Test
	public void testEquals(){
		Article a = new Article("testid", "testNamn");
		Article test = a;
		assertEquals(a,test);
		test = new Article("testid2", "something");
		assertNotEquals(a,test);
	}
	
	@Test(expected=PersistenceException.class)
	public void sameIdException(){
		Article a = new Article("testid", "testNamn");
		a.save();
		Article b = new Article("testid", "something");
		b.save();
	}	
}
