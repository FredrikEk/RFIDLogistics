package controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Article;
import play.mvc.BodyParser;
import play.mvc.Result;

import static play.mvc.Results.ok;

public class ArticleView {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getArticles() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        for (Article article : Article.find.all()) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", article.getId());
            node.put("name", article.getName());
            result.add(node);
        }
        return ok(result);
    }
}
