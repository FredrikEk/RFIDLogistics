package controllers.api;

import app.AbstractBaseApp;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.mvc.Result;

import static controllers.api.MoveViewTestsCommon.*;

/**
 * @author Daniel Jonsson
 *
 * This class will test the MoveView API with its default configuration. See
 * MoveViewImmediatelyPutDownPalletsFalseTest.java for tests of its second mode.
 */
public class MoveViewTest extends AbstractBaseApp {

    /**
     * Test if it's possible to pick up a pallet from the floor through the API and also fetch it.
     */
    @Test
    public void pickUpPallet() {
        setUpDatabase();

        // Create a JSON move object
        ObjectNode json = createJsonMoveObject(READER_1, "2012-12-10 12:12:12", PALLET_1_TAG_1);

        // Post the JSON move object
        Result result = post(json);

        String moves =
                "[{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}]";
        // Assert that the move was successfully created and that the API returns the correct String
        assertCreatedAndReturns(result, moves);
    }

    /**
     * Test if it's possible to use a unix timestamp as date.
     *
     * Essentially the same as the test `pickUpPallet()` but with the date as a unix timestamp.
     */
    @Test
    public void scanPalletWithDateAsUnixTimestamp() {
        setUpDatabase();

        // Create a JSON move object
        ObjectNode json = createJsonMoveObject(READER_1, "1395434421", PALLET_1_TAG_1);

        // Post the JSON move object
        Result result = post(json);

        String moves =
                "[{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Fri Mar 21 21:40:21 CET 2014','put_down':'false'}]";
        // Assert that the move was successfully created and that the API returns the correct String
        assertCreatedAndReturns(result, moves);
    }

    /**
     * In this scenario is pallet 1 picked up from the floor, then is pallet 2 picked up.
     *
     * This results in pallet 1 being put down on the floor, because no slot was scanned in-between.
     */
    @Test
    public void pickedUpAndPutDownPalletOnFloor() {
        setUpDatabase();

        // Pick up pallet 1 from the floor
        ObjectNode json = createJsonMoveObject(READER_1, "2012-12-10 12:12:12", PALLET_1_TAG_1);
        Result result = post(json);
        String moves =
                "[{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                        "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}]";
        assertCreatedAndReturns(result, moves);

        // Pick up pallet 2 (i.e. pallet 1 was put down on the floor and pallet 2 was picked up)
        json = createJsonMoveObject(READER_1, "2012-12-10 12:32:12", PALLET_2_TAG_1);
        result = post(json);
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:32:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:32:12 CET 2012','put_down':'false'}" +
                "]";

        assertCreatedAndReturns(result, moves);
    }

    /**
     * In this scenario is pallet 1 picked up from the floor. Next is the unoccupied slot 1 scanned and pallet 1 is put
     * down on it. Then is pallet 2 picked up.
     */
    @Test
    public void pickedUpAndPutDownPallet() {
        setUpDatabase();

        // Pick up pallet 1 from the floor
        ObjectNode json = createJsonMoveObject(READER_1, "2012-12-10 12:12:12", PALLET_1_TAG_1);
        Result result = post(json);
        String moves =
                "[{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                        "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}]";
        assertCreatedAndReturns(result, moves);

        // Scan a slot tag, whose slot is unoccupied (slot 1)
        json = createJsonMoveObject(READER_1, "2012-12-10 12:22:12", SLOT_1_TAG);
        result = post(json);
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}" +
                "]";
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_1_POSITION, READER_1));

        // Pick up pallet 2
        json = createJsonMoveObject(READER_1, "2012-12-10 12:32:12", PALLET_2_TAG_1);
        result = post(json);
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:32:12 CET 2012','put_down':'false'}" +
                "]";

        assertCreatedAndReturns(result, moves);
    }

    /**
     * In this scenario is pallet 1 picked up from the floor. Next are 3 unoccupied slots scanned and pallet 1 is put on
     * the first of them. Then is pallet 2 picked up.
     */
    @Test
    public void pickedUpAndPutDownPalletOnFirstSlot() {
        setUpDatabase();

        // Pick up pallet 1 from the floor
        ObjectNode json = createJsonMoveObject(READER_1, "2012-12-10 12:12:12", PALLET_1_TAG_1);
        Result result = post(json);
        String moves =
                "[{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                        "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}]";
        assertCreatedAndReturns(result, moves);

        // Scan unoccupied slot tags and the pallet is put down on the first one
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:22:12", SLOT_1_TAG));
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}" +
                "]";
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_1_POSITION, READER_1));
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:32:12", SLOT_2_TAG));
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_2_POSITION, READER_1));
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:42:12", SLOT_3_TAG));
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_3_POSITION, READER_1));

        // Pick up pallet 2
        json = createJsonMoveObject(READER_1, "2012-12-10 12:52:12", PALLET_2_TAG_1);
        result = post(json);
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:52:12 CET 2012','put_down':'false'}" +
                "]";

        assertCreatedAndReturns(result, moves);
    }

    /**
     * In this scenario happens the following events:
     *
     *     * Pallet 1 is scanned and picked up.
     *     * Slot 1 is scanned and pallet 1 is put down.
     *     * Pallet 2 is scanned and picked up.
     *     * Slot 1 is scanned and pallet 2 isn't put down.
     *     * Slot 2 is scanned and pallet 2 is put down.
     *     * Pallet 1 is scanned and picked up.
     */
    @Test
    public void pickedUpAndTryToPutDownPalletOnOccupiedSlot() {
        setUpDatabase();

        // Pick up pallet 1 from the floor
        ObjectNode json = createJsonMoveObject(READER_1, "2012-12-10 12:12:12", PALLET_1_TAG_1);
        Result result = post(json);
        String moves =
                "[{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                        "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}]";
        assertCreatedAndReturns(result, moves);

        // Scan unoccupied slot tag
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:22:12", SLOT_1_TAG));
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}" +
                "]";
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_1_POSITION, READER_1));

        // Pick up pallet 2
        json = createJsonMoveObject(READER_1, "2012-12-10 12:32:12", PALLET_2_TAG_1);
        result = post(json);
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:32:12 CET 2012','put_down':'false'}" +
                "]";
        assertCreatedAndReturns(result, moves);

        // Scan occupied slot 1
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:42:12", SLOT_1_TAG));
        assertBadRequest(result, String.format(SCANNED_OCCUPIED_SLOT, SLOT_1_POSITION));
        // Scan unoccupied slot 2
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:52:12", SLOT_2_TAG));
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:32:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_2_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:52:12 CET 2012','put_down':'true'}" +
                "]";
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_2_POSITION, READER_1));

        // Pick up pallet 1 again
        json = createJsonMoveObject(READER_1, "2012-12-10 13:02:12", PALLET_1_TAG_1);
        result = post(json);
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:22:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:32:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_2_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:52:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 13:02:12 CET 2012','put_down':'false'}" +
                "]";
        assertCreatedAndReturns(result, moves);
    }

    /**
     * Get response "bad request" when trying to create a move without a reader.
     */
    @Test
    public void failCreateMoveWhenMissingReaderAttribute() {
        setUpDatabase();
        ObjectNode json = createJsonMoveObject(null, "1395434421", PALLET_1_TAG_1);
        Result result = post(json);
        assertBadRequest(result, MISSING_ATTRIBUTE_ERROR_MESSAGE);
    }

    /**
     * Get response "bad request" when trying to create a move without a date.
     */
    @Test
    public void failCreateMoveWhenMissingDateAttribute() {
        setUpDatabase();
        ObjectNode json = createJsonMoveObject(READER_1, null, SLOT_1_TAG);
        Result result = post(json);
        assertBadRequest(result, MISSING_ATTRIBUTE_ERROR_MESSAGE);
    }

    /**
     * Get response "bad request" when trying to create a move without a tag.
     */
    @Test
    public void failCreateMoveWhenMissingTagAttribute() {
        setUpDatabase();
        ObjectNode json = createJsonMoveObject(READER_1, "1395434421", null);
        Result result = post(json);
        assertBadRequest(result, MISSING_ATTRIBUTE_ERROR_MESSAGE);
    }

    /**
     * Get response "bad request" when trying to create a move without an existing pallet tag.
     */
    @Test
    public void failCreateMoveWithNonexistentPalletTag() {
        setUpDatabase();
        ObjectNode json = createJsonMoveObject(READER_1, "1395434421", "palletTag1337");
        Result result = post(json);
        assertBadRequest(result, COULD_NOT_FIND_TAG_ERROR_MESSAGE);
    }


    /**
     * Get response "bad request" when trying to create a move with a date in wrong format.
     */
    @Test
    public void failCreateMoveWithDateInWrongFormat() {
        setUpDatabase();
        ObjectNode json = createJsonMoveObject(READER_1, "12:12:12 2012-12-10", PALLET_1_TAG_1);
        Result result = post(json);
        assertBadRequest(result, DATE_IN_WRONG_FORMAT_ERROR_MESSAGE);
    }
}
