package controllers.api;

import app.AbstractBaseAppImmediatelyPutDownPalletsFalse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.mvc.Result;

import static controllers.api.MoveViewTestsCommon.*;

/**
 * @author Daniel Jonsson
 *
 * Very much like MoveViewTest, but the setting `immediately-put-down-pallets` in application.conf is set to `false`
 * instead of the default value `true`.
 */
public class MoveViewImmediatelyPutDownPalletsFalseTest extends AbstractBaseAppImmediatelyPutDownPalletsFalse {

    /**
     * In this scenario is pallet 1 picked up from the floor, next is the unoccupied slot 1 scanned, then is pallet 2
     * picked up (and pallet 1 put down on slot 1).
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
        // Pallet 1 hasn't been put down yet, so the GET method returns the same moves as before
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_1_POSITION, READER_1));

        // Pick up pallet 2 (i.e. pallet 1 was put down on a slot 1 and pallet 2 was picked up)
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
     * In this scenario is pallet 1 picked up from the floor, next are 3 unoccupied slots scanned, then is pallet 2
     * picked up (and pallet 1 put down on slot 3 since it was the last scanned slot).
     */
    @Test
    public void pickedUpAndPutDownPalletOnLastSlot() {
        setUpDatabase();

        // Pick up pallet 1 from the floor
        ObjectNode json = createJsonMoveObject(READER_1, "2012-12-10 12:12:12", PALLET_1_TAG_1);
        Result result = post(json);
        String moves =
                "[{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                        "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}]";
        assertCreatedAndReturns(result, moves);

        // Scan unoccupied slot tags. The moves from the API (second argument) isn't affected
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:22:12", SLOT_1_TAG));
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_1_POSITION, READER_1));
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:32:12", SLOT_2_TAG));
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_2_POSITION, READER_1));
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:42:12", SLOT_3_TAG));
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_3_POSITION, READER_1));

        // Pick up pallet 2 (i.e. pallet 1 was put down on a slot 3 (last scanned slot) and pallet 2 was picked up)
        json = createJsonMoveObject(READER_1, "2012-12-10 12:52:12", PALLET_2_TAG_1);
        result = post(json);
        moves = "[" +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:12:12 CET 2012','put_down':'false'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_3_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:42:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_2_TAG_1+"','"+PALLET_2_TAG_2+"'],'slot':'"+SLOT_FLOOR_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:52:12 CET 2012','put_down':'false'}" +
                "]";

        assertCreatedAndReturns(result, moves);
    }

    /**
     * In this scenario happens the following events:
     *
     *     * Pallet 1 is scanned and picked up.
     *     * Slot 1 is scanned.
     *     * Pallet 2 is scanned and picked up. Also results in pallet 1 being put down on slot 1.
     *     * Slot 2 is scanned.
     *     * Slot 1 is scanned.
     *     * Pallet 1 is scanned and picked up. Also results in pallet 2 being put down on slot 2 since slot 1 is
     *           occupied.
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

        // Scan unoccupied slot tags
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:22:12", SLOT_1_TAG));
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_1_POSITION, READER_1));

        // Pick up pallet 2 (i.e. pallet 1 was put down on a slot 1 and pallet 2 was picked up)
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

        // Scan unoccupied slot 2
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:42:12", SLOT_2_TAG));
        assertCreatedAndReturns(result, moves, String.format(SCANNED_UNOCCUPIED_SLOT, SLOT_2_POSITION, READER_1));
        // Scan occupied slot 1
        result = post(createJsonMoveObject(READER_1, "2012-12-10 12:52:12", SLOT_1_TAG));
        assertBadRequest(result, String.format(SCANNED_OCCUPIED_SLOT, SLOT_1_POSITION));

        // Pick up pallet 1 again. Pallet 2 should be put down on slot 2
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
                "'reader':'"+READER_1+"','date':'Mon Dec 10 12:42:12 CET 2012','put_down':'true'}," +
                "{'tags':['"+PALLET_1_TAG_1+"','"+PALLET_1_TAG_2+"'],'slot':'"+SLOT_1_POSITION+"'," +
                "'reader':'"+READER_1+"','date':'Mon Dec 10 13:02:12 CET 2012','put_down':'false'}" +
                "]";
        assertCreatedAndReturns(result, moves);
    }
}
