package controllers.api;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Pallet;
import models.PalletSlot;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.PersistenceException;

public class PalletSlotView extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getSlots() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        for (PalletSlot slot : PalletSlot.find.orderBy("position").findList()) {
            ObjectNode node = mapper.createObjectNode();
            node.put("position", slot.getPosition());
            node.put("tag", slot.getTag().getId());
            Map<String, Pallet> map = PalletSlot.getPalletMap();
            
            
            Pallet palletOnSlot = map.get(slot.getPosition());
            if (palletOnSlot != null) {
                node.put("pallet_on_slot", palletOnSlot.getId());
            }
            result.add(node);
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result newSlot() {
        JsonNode json = request().body().asJson();
        String position = json.path("position").asText();
        String tag = json.path("tag").asText();
        PalletSlot palletSlot;
        try {
            palletSlot = new PalletSlot(position, tag);
        } catch (IllegalArgumentException e) {
            return badRequest("The tag is already being used.");
        }
        try {
            palletSlot.save();
            return created("");
        } catch (PersistenceException e) {
            return badRequest("Pallet slot position already exists.");
        }
    }
}
