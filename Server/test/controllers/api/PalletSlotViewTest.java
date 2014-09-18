package controllers.api;

import app.AbstractBaseApp;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.PalletSlot;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class PalletSlotViewTest extends AbstractBaseApp {

    /**
     * Test if it's possible to create one PalletSlot through the API.
     */
    @Test
    public void createOnePalletSlot() {
        createPalletSlots(new ArrayList<List<String>>() {
            {
                add(new ArrayList<String>() {
                    {
                        add("position1");
                        add("tag1");
                    }
                });
            }
        });
    }

    /**
     * Test if it's possible to create multiple PalletSlots through the API.
     */
    @Test
    public void createMultiplePalletSlots() {
        createPalletSlots(new ArrayList<List<String>>() {
            {
                add(new ArrayList<String>() {
                    {
                        add("position1");
                        add("tag1");
                    }
                });
                add(new ArrayList<String>() {
                    {
                        add("position2");
                        add("tag2");
                    }
                });
                add(new ArrayList<String>() {
                    {
                        add("position3");
                        add("tag3");
                    }
                });
            }
        });
    }

    /**
     * Create pallet slots through the API and assert that they are created.
     */
    private void createPalletSlots(List<List<String>> slots) {
        final int positionId = 0;
        final int tagId = 1;
        ObjectMapper mapper = new ObjectMapper();
        // Post the slots and create them through the API
        for (List<String> slot : slots) {
            final String position = slot.get(0);
            final String tag = slot.get(1);

            // Create a JSON pallet slot object
            ObjectNode json = mapper.createObjectNode();
            json.put("tag", tag);
            json.put("position", position);

            // Post the JSON object
            FakeRequest request = new FakeRequest(POST, "/api/palletslots").withJsonBody(json);
            Result result = callAction(controllers.api.routes.ref.PalletSlotView.newSlot(), request);

            assertThat(contentAsString(result)).isEqualTo("");
            assertThat(status(result)).isEqualTo(CREATED);
        }

        // Check that the number of pallet slots is correct
        assertThat(PalletSlot.find.all().size()).isEqualTo(slots.size());

        // See if the pallet slots were created
        for (List<String> object : slots) {
            List<PalletSlot> palletSlots = PalletSlot.find.where(Expr.eq("position", object.get(positionId))).findList();
            assertThat(palletSlots.size()).isEqualTo(1);
            PalletSlot palletSlot = palletSlots.get(0);
            assertThat(palletSlot).isNotNull();
            assertThat(palletSlot.getPosition()).isEqualTo(object.get(positionId));
            assertThat(palletSlot.getTag().getId()).isEqualTo(object.get(tagId));
        }
    }

    /**
     * Test if it's possible to fetch one PalletSlot through the API.
     */
    @Test
    public void getOnePalletSlot() {
        getPalletSlots(new ArrayList<List<String>>() {
            {
                add(new ArrayList<String>() {
                    {
                        add("position1");
                        add("tag1");
                    }
                });
            }
        });
    }

    /**
     * Test if it's possible to fetch multiple PalletSlots through the API.
     */
    @Test
    public void getMultiplePalletSlots() {
        getPalletSlots(new ArrayList<List<String>>() {
            {
                add(new ArrayList<String>() {
                    {
                        add("position1");
                        add("tag1");
                    }
                });
                add(new ArrayList<String>() {
                    {
                        add("position2");
                        add("tag2");
                    }
                });
                add(new ArrayList<String>() {
                    {
                        add("position3");
                        add("tag3");
                    }
                });
            }
        });
    }

    /**
     * Create pallet slots and try to fetch them through the API
     */
    private void getPalletSlots(List<List<String>> slots) {
        List<String> jsonObjects = new ArrayList<>();
        // Post the slots and create them through the API
        for (List<String> slot : slots) {
            String position = slot.get(0);
            String tag = slot.get(1);
            new PalletSlot(position, tag).save();
            // Store how the object should look like as JSON
            jsonObjects.add(String.format("{\"position\":\"%s\",\"tag\":\"%s\"}", position, tag));
        }

        // Put together the string we expect the json pallet slots API to return
        String json = String.format("[%s]", StringUtils.join(jsonObjects, ","));

        // Check that the slots are returned correctly
        Result result = callAction(controllers.api.routes.ref.PalletSlotView.getSlots());
        assertThat(contentAsString(result)).isEqualTo(json);
    }
}

