package controllers.api;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Article;
import models.Pallet;
import models.SetOfArticle;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.PersistenceException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static util.Tools.isAnyStringEmpty;

public class PalletView extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getPallets() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        for (Pallet pallet : Pallet.find.all()) {
            ObjectNode node = mapper.createObjectNode();
            node.put("tag1", pallet.getTag1().getId());
            node.put("tag2", pallet.getTag2().getId());
            node.put("time_entrance", pallet.getTimeEntrance().toString());
            ArrayNode products = mapper.createArrayNode();
            for (SetOfArticle setOfArticle : pallet.getArticles()) {
                ObjectNode product = mapper.createObjectNode();
                product.put("article", setOfArticle.getArticle().getId());
                product.put("amount", setOfArticle.getAmount());
                products.add(product);
            }
            node.put("products", products);
            result.add(node);
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result newPallet() {
        JsonNode json = request().body().asJson();
        String tag1 = json.path("tag1").asText();
        String tag2 = json.path("tag2").asText();
        String timeEntrance = json.path("time_entrance").asText();
        Iterator<JsonNode> jsonProducts = json.path("products").elements();
        Map<String, Integer> products = new HashMap<>();
        while (jsonProducts.hasNext()) {
            JsonNode node = jsonProducts.next();
            products.put(node.findPath("article").asText(), node.findPath("amount").asInt());
        }

        if (isAnyStringEmpty(tag1, tag2, timeEntrance)) {
            return badRequest("Missing parameter.");
        }

        Pallet pallet;
        try {
            pallet = new Pallet(tag1, tag2, timeEntrance);
            pallet.save();
        } catch (PersistenceException e) {
            // If one of the tags already exists in the database
            return badRequest(e.getMessage());
        } catch (ParseException e) {
            return badRequest("Invalid formatted time_entrance.");
        }

        for (Map.Entry<String, Integer> product : products.entrySet()) {
            Article article;
            Ebean.beginTransaction();
            try {
                article = Article.find.byId(product.getKey());
                if (article == null) {
                    article = new Article(product.getKey());
                    article.save();
                }
                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }
            new SetOfArticle(article, product.getValue(), pallet).save();
        }

        return created();
    }

    public static Result getPallet(String tag) {
        return TODO;
    }

    public static Result updatePallet(String tag) {
        return TODO;
    }

    public static Result deletePallet(String tag) {
        if (tag == null) {
            return badRequest("Missing parameter `tag`.");
        } else {
            // TODO
            return ok();
        }
    }
}
