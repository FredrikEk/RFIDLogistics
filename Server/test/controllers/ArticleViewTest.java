package controllers;

import app.AbstractLoggedInBrowserApp;
import models.Article;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ArticleViewTest extends AbstractLoggedInBrowserApp {

    @Test
    public void createArticle() {
        String id = "id123", name = "Name";
        browser.goTo("http://localhost:8000/articles/new");
        browser.fill("input").with(id, name);
        browser.submit("input[type='submit']");

        assertThat(browser.url()).isEqualTo("http://localhost:8000/articles");
        String successAlert = browser.$(".alert-success").getText();
        assertThat(successAlert).isEqualTo(
                String.format("Article: %s with article number %s added to database!", name, id));
        Article article = Article.find.all().get(0);
        assertThat(article.getId()).isEqualTo(id);
        assertThat(article.getName()).isEqualTo(name);
    }
}
