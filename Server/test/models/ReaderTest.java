package models;

import app.AbstractBaseApp;
import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

public class ReaderTest extends AbstractBaseApp {

    @Test
    public void getBufferedTag() {
        Reader reader = new Reader("testReader");
        reader.save();
        Tag tag = new Tag("123");
        tag.save();
        PalletSlot palletSlot = new PalletSlot("pos", tag);
        palletSlot.save();
        reader.setLastScannedUnoccupiedSlot(palletSlot);
        reader.setLastScannedUnoccupiedSlotDate(new Date());
        reader.save();

        // Possible to get the buffered tag
        reader = Reader.find.all().get(0);
        assertThat(reader.getLastScannedUnoccupiedSlot().getTag().getId()).isEqualTo("123");
    }
}
