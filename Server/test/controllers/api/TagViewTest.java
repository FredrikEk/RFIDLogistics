package controllers.api;

import app.AbstractBaseApp;
import models.Pallet;
import models.PalletSlot;
import models.Tag;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;

import java.text.ParseException;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class TagViewTest extends AbstractBaseApp {

    /**
     * Delete a tag that's not used on a slot or pallet.
     */
    @Test
    public void deleteTag() {
        new Tag("tag1").save();
        assertThat(Tag.find.all().size()).isEqualTo(1);
        Result result = makeDeleteTagRequest("tag1");
        assertThat(status(result)).isEqualTo(OK);
        assertThat(Tag.find.all().size()).isEqualTo(0);
    }

    /**
     * Also delete the pallet when deleting a tag registered on a pallet.
     */
    @Test
    public void deletePalletTag() throws ParseException {
        new Pallet("tag1", "tag2", "2014-02-02 12:12:12").save();
        assertThat(Pallet.find.all().size()).isEqualTo(1);
        assertThat(Tag.find.all().size()).isEqualTo(2);
        Result result = makeDeleteTagRequest("tag1");
        assertThat(status(result)).isEqualTo(OK);
        assertThat(Pallet.find.all().size()).isEqualTo(0);
        assertThat(Tag.find.all().size()).isEqualTo(0);
    }

    /**
     * Also delete the pallet slot when deleting a tag registered on a pallet slot.
     */
    @Test
    public void deletePalletSlotTag() {
        new PalletSlot("position1", "tag1").save();
        assertThat(PalletSlot.find.all().size()).isEqualTo(1);
        assertThat(Tag.find.all().size()).isEqualTo(1);
        Result result = makeDeleteTagRequest("tag1");
        assertThat(status(result)).isEqualTo(OK);
        assertThat(PalletSlot.find.all().size()).isEqualTo(0);
        assertThat(Tag.find.all().size()).isEqualTo(0);
    }

    /**
     * Get a bad request when the tag doesn't exist.
     */
    @Test
    public void deleteNonexistentTag() {
        Result result = makeDeleteTagRequest("tag1");
        assertThat(status(result)).isEqualTo(BAD_REQUEST);
    }

    /**
     * Make a request to delete a tag via the API.
     * @param tag The tag.
     * @return The request's response.
     */
    private Result makeDeleteTagRequest(String tag) {
        FakeRequest request = new FakeRequest(DELETE, String.format("/api/pallets/%s", tag));
        return callAction(controllers.api.routes.ref.TagView.deleteTag(tag), request);
    }
}
