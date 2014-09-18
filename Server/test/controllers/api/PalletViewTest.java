package controllers.api;

import app.AbstractBaseApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Pallet;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class PalletViewTest extends AbstractBaseApp {

    /**
     * Test if it's possible to create a Pallet through the API and also fetch it.
     */
    @Test
    public void createAndGetPallet() {
        // Create a JSON pallet object
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("tag1", "12345");
        json.put("tag2", "12346");
        json.put("time_entrance", "2012-12-10 12:12:12");
        ArrayNode products = mapper.createArrayNode();
        ObjectNode product = mapper.createObjectNode();
        product.put("article", "ICA012");
        product.put("amount", 12);
        products.add(product);
        json.put("products", products);

        // Post the JSON pallet object
        FakeRequest request = new FakeRequest(POST, "/api/pallets").withJsonBody(json);
        Result result = callAction(controllers.api.routes.ref.PalletView.newPallet(), request);

        assertThat(status(result)).isEqualTo(CREATED);
        // Check that the number of pallets is 1
        assertThat(Pallet.find.all().size()).isEqualTo(1);

        // Check that the pallet is also returned correctly
        result = callAction(controllers.api.routes.ref.PalletView.getPallets());
        assertThat(contentAsString(result)).isEqualTo("[{\"tag1\":\"12345\",\"tag2\":\"12346\",\"time_entrance\":\"Mon Dec 10 12:12:12 CET 2012\",\"products\":[{\"article\":\"ICA012\",\"amount\":12}]}]");
    }

    /**
     * Test if it's possible to create a Pallet without any Articles.
     */
    @Test
    public void createAndGetPalletWithoutArticles() {
        // Create a JSON pallet object
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("tag1", "12345");
        json.put("tag2", "12346");
        json.put("time_entrance", "2012-12-10 12:12:12");

        // Post the JSON pallet object
        FakeRequest request = new FakeRequest(POST, "/api/pallets").withJsonBody(json);
        Result result = callAction(controllers.api.routes.ref.PalletView.newPallet(), request);

        assertThat(status(result)).isEqualTo(CREATED);
        // Check that the number of pallets is 1
        assertThat(Pallet.find.all().size()).isEqualTo(1);

        // Check that the pallet is also returned correctly
        result = callAction(controllers.api.routes.ref.PalletView.getPallets());
        assertThat(contentAsString(result)).isEqualTo("[{\"tag1\":\"12345\",\"tag2\":\"12346\",\"time_entrance\":\"Mon Dec 10 12:12:12 CET 2012\",\"products\":[]}]");
    }
}
