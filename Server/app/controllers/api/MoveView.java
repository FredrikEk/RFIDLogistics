package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.api.helpers.ScannedTag;
import models.MovedPallet;
import models.Pallet;
import models.PalletSlot;
import models.Reader;
import play.Play;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import util.Tools;

import java.text.ParseException;
import java.util.Date;

public class MoveView extends Controller {

    public static final String TAG_ATTRIBUTE = "tag";

    public static final String TAGS_ATTRIBUTE = "tags";

    public static final String READER_ATTRIBUTE = "reader";

    public static final String DATE_ATTRIBUTE = "date";

    public static final String SLOT_ATTRIBUTE = "slot";

    public static final String PUT_DOWN_ATTRIBUTE = "put_down";

    public static Result getMoves() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        for (MovedPallet move : MovedPallet.find.orderBy("date").findList()) {
            ObjectNode node = mapper.createObjectNode();
            ArrayNode tags = mapper.createArrayNode();
            tags.add(move.getPallet().getTag1().getId());
            tags.add(move.getPallet().getTag2().getId());
            node.put(TAGS_ATTRIBUTE, tags);
            node.put(SLOT_ATTRIBUTE, move.getLocation().getPosition());
            node.put(READER_ATTRIBUTE, move.getReader().getId());
            node.put(DATE_ATTRIBUTE, move.getDate().toString());
            node.put(PUT_DOWN_ATTRIBUTE, move.getPutDown().toString());
            result.add(node);
        }
        return ok(result);
    }

    /**
     * Register a scanned tag related to a move.
     *
     * When a pallet's tag is sent to this method, it will be stored as picked up. Where the pallet was put down will be
     * decided when the next pallet is scanned. (1) If one or more slots were sent here in between, the first pallet was
     * put on the last of the slots. (2) If no slot was sent to this method in between, the first pallet wasn't placed
     * on a valid slot (i.e. the floor).
     *
     * JSON format
     * ===========
     *
     * This method expects a POST request with a JSON object formatted as specified below.
     *
     * Standard
     * --------
     *
     *     {
     *         "tag": "123abc",
     *         "reader": "client 1",
     *         "date": "2014-01-01 01:01:01"
     *     }
     *
     * Date as a unix timestamp
     * ------------------------
     *
     *     {
     *         "tag": "123abc",
     *         "reader": "client 1",
     *         "date": "1395434421"
     *     }
     *
     * Scenarios
     * =========
     *
     * A set of sequences of scanned tags that a forklift might do, and what the outcome would be in each case.
     *
     * Variables: Pallet = P*, Slot = S*
     *
     * immediately-put-down-pallets=true
     * ---------------------------------
     *
     * ## 1
     * Sent: P1
     * Outcome: P1 is picked up by the forklift.
     *
     * ## 2
     * Sent: P1 - S1
     * Outcome: P1 is picked up and is placed on S1.
     *
     * ## 3
     * Sent: P1 - S1 - S2 - S3
     * Outcome: P1 is picked up is placed on S1.
     *
     * ## 4
     * Sent: P1 - S1 - P2
     * Outcome: P1 is picked up and placed on S1. Then P2 is picked up.
     *
     * ## 5
     * Sent: P1 - S1 - S2 - S3 - P2
     * Outcome: P1 is picked up and placed on S1. Then P2 is picked up.
     *
     * ## 6
     * Sent: S1 - P1 - S2 - S3 - P2
     * Outcome: P1 is picked up and placed on S2. Then P2 is picked up.
     *
     * ## 7
     * Sent: S1 - S2 - S3 - P1 - S4 - S5 - P2
     * Outcome: P1 is picked up and placed on S4. Then P2 is picked up.
     *
     * ## 8
     * Sent: P1 - P2
     * Outcome: P1 is picked up and not placed on a valid slot. Then P2 is picked up.
     *
     * ## 9
     * Sent: P1 - P2 - S1 - P1
     * Outcome: P1 is picked up and not placed on a valid slot. Then P2 is picked up and placed on S1. Then P1 is picked
     *     up again.
     *
     * immediately-put-down-pallets=false
     * ----------------------------------
     *
     * ## 1
     * Sent: P1
     * Outcome: P1 is picked up by the forklift.
     *
     * ## 2
     * Sent: P1 - S1
     * Outcome: P1 is picked up and might be placed on S1.
     *
     * ## 3
     * Sent: P1 - S1 - S2 - S3
     * Outcome: P1 is picked up and might be placed on S3.
     *
     * ## 4
     * Sent: P1 - S1 - P2
     * Outcome: P1 is picked up and placed on S1. Then P2 is picked up.
     *
     * ## 5
     * Sent: P1 - S1 - S2 - S3 - P2
     * Outcome: P1 is picked up and placed on S3. Then P2 is picked up.
     *
     * ## 6
     * Sent: S1 - P1 - S2 - S3 - P2
     * Outcome: P1 is picked up and placed on S3. Then P2 is picked up.
     *
     * ## 7
     * Sent: S1 - S2 - S3 - P1 - S4 - S5 - P2
     * Outcome: P1 is picked up and placed on S5. Then P2 is picked up.
     *
     * ## 8
     * Sent: P1 - P2
     * Outcome: P1 is picked up and not placed on a valid slot. Then P2 is picked up.
     *
     * ## 9
     * Sent: P1 - P2 - S1 - P1
     * Outcome: P1 is picked up and not placed on a valid slot. Then P2 is picked up and placed on S1. Then P1 is picked
     *     up again.
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result newMove() {
        JsonNode json = request().body().asJson();

        // A tag, a reader and a date are required
        if (!Tools.jsonHasAttributes(json, TAG_ATTRIBUTE, READER_ATTRIBUTE, DATE_ATTRIBUTE)) {
            return badRequest(
                    String.format("Missing attribute. The attributes '%s', '%s' and '%s' must be present.",
                            TAG_ATTRIBUTE, READER_ATTRIBUTE, DATE_ATTRIBUTE));
        }

        String tag = json.path(TAG_ATTRIBUTE).asText();
        String readerId = json.path(READER_ATTRIBUTE).asText();
        String dateString = json.path(DATE_ATTRIBUTE).asText();

        // Parse the supplied date string
        Date date;
        try {
            date = Tools.parseDate(dateString);
        } catch (ParseException e) {
            return badRequest(String.format("Wrong format on the date. Should be '%1$s'.", Tools.DATE_FORMAT));
        }

        // Create a database instance of the reader if it doesn't already exist
        Reader reader = getOrCreateReader(readerId);

        // Check what entity the tag belongs to and do action depending on that
        ScannedTag scannedTag = new ScannedTag(tag);
        if (scannedTag.isPallet())
            return handleScannedPallet(scannedTag.getPallet(), date, reader);
        else if (scannedTag.isPalletSlot())
            return handleScannedPalletSlot(scannedTag.getPalletSlot(), date, reader);
        else
            return badRequest("Tag doesn't exist.");
    }

    /**
     * Get the reader, and create it if it doesn't already exist.
     *
     * @param readerId The unique ID of the reader.
     * @return An instance of a Reader.
     */
    private static Reader getOrCreateReader(String readerId) {
        Reader reader = Reader.find.byId(readerId);
        if (reader == null) {
            reader = new Reader(readerId);
            reader.save();
        }
        return reader;
    }

    /**
     * The forklift has scanned a pallet.
     *
     * @param pallet The scanned pallet.
     * @param date The date the pallet was scanned.
     * @param reader The forklift that did the scan.
     * @return Play Result.
     */
    private static Result handleScannedPallet(Pallet pallet, Date date, Reader reader) {
        MovedPallet movedPallet = getLastMovedPalletByReader(reader);
        if (movedPallet != null && !movedPallet.getPutDown() && movedPallet.getPallet().getId() == pallet.getId()) {
            // If the reader has already picked up a pallet and if it's the same pallet as the scanned pallet
            return ok();
        }
        putDownLastScannedPalletByReader(reader, true, date);
        pickUpPalletByReader(pallet, date, reader);
        return ok();
    }

    /**
     * The forklift has scanned a pallet slot.
     *
     * @param palletSlot The scanned pallet slot.
     * @param date The date the slot was scanned.
     * @param reader The forklift that did the scan.
     * @return Play Result.
     */
    private static Result handleScannedPalletSlot(PalletSlot palletSlot, Date date, Reader reader) {
        if (palletSlot.getPosition().equals("floor") || PalletSlot.getPalletMap().get(palletSlot.getPosition()) ==
                null) {
            // No pallet on the slot
            // Save the slot as the reader's last scanned unoccupied slot
            reader.setLastScannedUnoccupiedSlot(palletSlot);
            reader.setLastScannedUnoccupiedSlotDate(date);
            reader.save();
            if (Play.application().configuration().getBoolean("immediately-put-down-pallets")) {
                putDownLastScannedPalletByReader(reader, false, null);
                return ok(String.format("Put down pallet on %s.", palletSlot.getPosition()));
            } else {
                return ok(String.format("%s set as %s's last scanned unoccupied slot.", palletSlot.getPosition(),
                        reader.getId()));
            }
        } else {
            return badRequest(String.format("%s is already occupied.", palletSlot.getPosition()));
        }
    }

    /**
     * Get the move of the last pallet the reader picked up or put down.
     *
     * @param reader Reader.
     * @return The reader's last MovedPallet or null if non has been made.
     */
    private static MovedPallet getLastMovedPalletByReader(Reader reader) {
        return MovedPallet.find.where().eq("reader.id", reader.getId()).orderBy("date DESC, id DESC").setMaxRows(1).
                findUnique();
    }

    /**
     * Put down the reader's last scanned pallet if it hasn't already been put down.
     *
     * @param reader         The reader.
     * @param putOnFloor     If the pallet should be put on the floor if no unoccupied slot has been scanned since the
     *                       pallet was picked up.
     * @param putOnFloorDate The date when the pallet was put on the floor.
     */
    private static void putDownLastScannedPalletByReader(Reader reader, boolean putOnFloor, Date putOnFloorDate) {
        // Check for a previous picked up pallet that hasn't been put down yet
        MovedPallet lastMovedPalletByReader = getLastMovedPalletByReader(reader);
        if (lastMovedPalletByReader != null) {
            // The reader has moved a pallet before
            if (!lastMovedPalletByReader.getPutDown()) {
                // The last pallet was picked up
                if (reader.getLastScannedUnoccupiedSlot() != null) {
                    // The reader has scanned an unoccupied slot since the last pallet was picked up
                    // Create a move where the previously picked up pallet was put down
                    MovedPallet movedPallet = new MovedPallet(lastMovedPalletByReader.getPallet(),
                            reader.getLastScannedUnoccupiedSlotDate(), reader.getLastScannedUnoccupiedSlot(),
                            reader, true);
                    movedPallet.save();
                    // Reset the reader's last unoccupied slot to null
                    reader.setLastScannedUnoccupiedSlot(null);
                    reader.save();
                } else if (putOnFloor) {
                    // The reader hasn't scanned a slot since the last pallet.
                    // Put the previously picked up pallet on the floor
                    PalletSlot floor = ScannedTag.getOrCreateFloorSlot();
                    MovedPallet movedPallet = new MovedPallet(lastMovedPalletByReader.getPallet(), putOnFloorDate,
                            floor, reader, true);
                    movedPallet.save();
                }
            }
        }
    }

    /**
     * Pick up a pallet by reader. It will be picked up from the floor if the pallet hasn't been put down anywhere yet.
     *
     * @param pallet The pallet to be marked as picked up.
     * @param date   The date when the pallet was picked up.
     * @param reader The reader that picked up the pallet.
     */
    private static void pickUpPalletByReader(Pallet pallet, Date date, Reader reader) {
        PalletSlot pickedUpFrom = Pallet.getSlotMap().get(pallet.getId());
        if (pickedUpFrom == null) {
            // Picked up from the floor if the pallet hasn't been placed anywhere yet
            pickedUpFrom = ScannedTag.getOrCreateFloorSlot();
        }
        // Picked up the pallet
        MovedPallet movedPallet = new MovedPallet(pallet, date, pickedUpFrom, reader, false);
        movedPallet.save();
    }
}
