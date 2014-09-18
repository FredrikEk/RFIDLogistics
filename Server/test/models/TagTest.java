package models;

import app.AbstractBaseApp;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class TagTest extends AbstractBaseApp {

    @Test
    public void createTag() {
        new Tag("tag1").save();
        assertThat(Tag.find.all().size()).isEqualTo(1);
        assertThat(Tag.find.byId("tag1").getId()).isEqualTo("tag1");
    }

    @Test
    public void deleteTag() {
        Tag tag = new Tag("tag1");
        tag.save();
        assertThat(Tag.find.all().size()).isEqualTo(1);
        tag.delete();
        assertThat(Tag.find.all().size()).isEqualTo(0);
    }
}
