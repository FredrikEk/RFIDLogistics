package controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Pallet;
import models.PalletSlot;
import models.Reader;
import models.Tag;
import play.mvc.Result;
import play.test.FakeRequest;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created by matachi on 5/4/14.
 */
public class MoveViewTestsCommon {

    static final String MISSING_ATTRIBUTE_ERROR_MESSAGE =
            "Missing attribute. The attributes 'tag', 'reader' and 'date' must be present.";

    static final String COULD_NOT_FIND_TAG_ERROR_MESSAGE = "Tag doesn't exist.";

    static final String DATE_IN_WRONG_FORMAT_ERROR_MESSAGE =
            "Wrong format on the date. Should be 'yyyy-MM-dd HH:mm:ss'.";

    static final String SCANNED_UNOCCUPIED_SLOT = "%s set as %s's last scanned unoccupied slot.";

    static final String SCANNED_OCCUPIED_SLOT = "%s is already occupied.";

    static final String PALLET_1_TAG_1 = "palletTag1";

    static final String PALLET_1_TAG_2 = "palletTag2";

    static final String PALLET_2_TAG_1 = "palletTag3";

    static final String PALLET_2_TAG_2 = "palletTag4";

    static final String SLOT_1_POSITION = "slot1";

    static final String SLOT_1_TAG = "slotTag1";

    static final String SLOT_2_POSITION = "slot2";

    static final String SLOT_2_TAG = "slotTag2";

    static final String SLOT_3_POSITION = "slot3";

    static final String SLOT_3_TAG = "slotTag3";

    static final String SLOT_FLOOR_POSITION = "floor";

    static final String READER_1 = "reader";

    /**
     * Initialize the database with some objects.
     */
    public static void setUpDatabase() {
        // Pallet 1
        Tag tag1 = new Tag(PALLET_1_TAG_1);
        tag1.save();
        Tag tag2 = new Tag(PALLET_1_TAG_2);
        tag2.save();
        new Pallet(tag1, tag2).save();

        // Pallet 2
        tag1 = new Tag(PALLET_2_TAG_1);
        tag1.save();
        tag2 = new Tag(PALLET_2_TAG_2);
        tag2.save();
        new Pallet(tag1, tag2).save();

        // Slot 1
        new PalletSlot(SLOT_1_POSITION, SLOT_1_TAG).save();

        // Slot 2
        new PalletSlot(SLOT_2_POSITION, SLOT_2_TAG).save();

        // Slot 3
        new PalletSlot(SLOT_3_POSITION, SLOT_3_TAG).save();

        // Reader
        new Reader(READER_1).save();
    }

    /**
     * Set up a standard JSON object that will be used as the data to call the API.
     */
    public static ObjectNode createJsonMoveObject(String reader, String date, String tag) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        if (reader != null)
            json.put("reader", reader);
        if (date != null)
            json.put("date", date);
        if (tag != null) {
            json.put("tag", tag);
        }
        return json;
    }

    /**
     * Make a POST request to /api/moves with a JSON object.
     *
     * @param json A ObjectNode object.
     * @return The Result object with the result of the request.
     */
    public static Result post(ObjectNode json) {
        FakeRequest request = new FakeRequest(POST, "/api/moves").withJsonBody(json);
        return callAction(controllers.api.routes.ref.MoveView.newMove(), request);
    }

    /**
     * Check if `result` has status OK and the string `response` as content. Then that the GET moves API returns the
     * string `moves`.
     *
     * @param result   A Result object.
     * @param moves    A String that the GET moves API is expected to return.
     * @param response Response from the register a scanned tag POST API.
     */
    public static void assertCreatedAndReturns(Result result, String moves, String response) {
        assertThat(contentAsString(result)).isEqualTo(response);
        assertThat(status(result)).isEqualTo(OK);

        // Check that the move is also returned correctly
        result = callAction(controllers.api.routes.ref.MoveView.getMoves());
        assertThat(contentAsString(result)).isEqualTo(moves.replace("'", "\""));
    }

    /**
     * Check if `result` has status OK and an empty string as content. Then that the GET moves API returns the string
     * `moves`.
     *
     * @param result A Result object.
     * @param moves  A String that the GET moves API is expected to return.
     */
    public static void assertCreatedAndReturns(Result result, String moves) {
        assertCreatedAndReturns(result, moves, "");
    }

    /**
     * Assert that `result` has status BAD REQUEST and response `response`.
     *
     * @param result   A Result object.
     * @param response The response's content.
     */
    public static void assertBadRequest(Result result, String response) {
        assertThat(contentAsString(result)).isEqualTo(response);
        assertThat(status(result)).isEqualTo(BAD_REQUEST);
    }
}
