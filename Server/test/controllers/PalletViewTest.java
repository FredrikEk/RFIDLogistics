package controllers;

import app.AbstractLoggedInBrowserApp;
import models.Article;
import models.Pallet;
import models.SetOfArticle;
import org.junit.Test;

import java.text.ParseException;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class PalletViewTest extends AbstractLoggedInBrowserApp {

    @Test
    public void createPallet() {
        // The Id and the name of the article we want to create a pallet of.
        String articleId = "articleId2", articleName = "articleName2";
        // Create a few articles to pick from.
        new Article("articleId1", "articleName1").save();
        new Article(articleId, articleName).save();
        new Article("articleId3", "articleName3").save();

        String tag1 = "tag1", tag2 = "tag2";
        Integer amount = 1337;
        // Submit a new pallet using the form.
        browser.goTo("http://localhost:8000/pallets/new");
        browser.fill("input").with(tag1, tag2, Integer.toString(amount));
        browser.click("option", withText(articleName));
        browser.submit("input[type='submit']");

        // Redirected after submitted
        assertThat(browser.url()).isEqualTo("http://localhost:8000/pallets");
        // Correct success message
        String successAlert = browser.$(".alert-success").getText();
        assertThat(successAlert).isEqualTo(
                String.format("Pallet with IDs: %s, %s and 1337 pieces of %s added to database!", tag1, tag2,
                        articleName));
        // The pallet model exists
        Pallet pallet = Pallet.find.all().get(0);
        assertThat(pallet.getTag1().getId()).isEqualTo(tag1);
        assertThat(pallet.getTag2().getId()).isEqualTo(tag2);
        // The pallet has the correct article
        SetOfArticle setOfArticle = pallet.getArticles().get(0);
        assertThat(setOfArticle.getAmount()).isEqualTo(amount);
        assertThat(setOfArticle.getArticle().getId()).isEqualTo(articleId);
    }

    /**
     * Test to update a pallet's article and amount though the form.
     *
     * @throws ParseException
     */
    @Test
    public void editPallet() throws ParseException {
        String tag1 = "tag1", tag2 = "tag2", articleId = "articleId2", articleName = "articleName2",
                newArticleId = "articleId3", newArticleName = "articleName3";
        Integer amount = 1337, newAmount = 2448;

        /*
         * Set up models.
         */
        new Article("articleId1", "articleName2").save();
        Article article = new Article(articleId, articleName);
        article.save();
        Article newArticle = new Article(newArticleId, newArticleName);
        newArticle.save();
        Pallet pallet = new Pallet(tag1, tag2, "2014-02-02 10:10:10");
        pallet.save();
        new SetOfArticle(article, amount, pallet).save();

        /*
         * Check if the data is rendered correctly in the form.
         */
        String url = String.format("http://localhost:8000/pallets/%s", pallet.getId());
        browser.goTo(url);
        assertThat(browser.$("#id").getValue()).isEqualTo(Integer.toString(pallet.getId()));
        assertThat(browser.$("#tag1").getValue()).isEqualTo(tag1);
        assertThat(browser.$("#tag2").getValue()).isEqualTo(tag2);
        assertThat(browser.$("#amount").getValue()).isEqualTo(Integer.toString(amount));
        assertThat(browser.findFirst("#article option[selected]").getText()).isEqualTo(articleName);

        /*
         * Input new amount and article, and then submit.
         */
        browser.fill("#amount").with(Integer.toString(newAmount));
        browser.click("option", withText(newArticleName));
        browser.submit("input[type='submit']");

        /*
         * Assert that we got the correct alert message and that the pallet was updated as expected.
         */
        // Redirected after submitted
        assertThat(browser.url()).isEqualTo("http://localhost:8000/pallets");
        // Correct success message
        String successAlert = browser.$(".alert-success").getText();
        assertThat(successAlert).isEqualTo(
                String.format("Changed pallet %s's article from %s to %s and amount from %s to %s.", pallet.getId(),
                              articleId, newArticleId, amount, newAmount));
        // The pallet model was also successfully updated
        pallet = Pallet.find.byId(pallet.getId());
        assertThat(pallet.getTag1().getId()).isEqualTo(tag1);
        assertThat(pallet.getTag2().getId()).isEqualTo(tag2);
        // The pallet has the new article and the new amount
        SetOfArticle setOfArticle = pallet.getArticles().get(0);
        assertThat(setOfArticle.getAmount()).isEqualTo(newAmount);
        assertThat(setOfArticle.getArticle().getId()).isEqualTo(newArticleId);
    }
}
