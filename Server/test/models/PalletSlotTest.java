package models;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import javax.persistence.PersistenceException;

import org.junit.Test;

import app.AbstractBaseApp;

public class PalletSlotTest extends AbstractBaseApp{
	private Tag t1, t2, t3;
	
	public void addPalletSlotCorrectly(){
		initMemory();
		PalletSlot ps1 = new PalletSlot("position 1", t1);
		PalletSlot ps2 = new PalletSlot("position 2");
		PalletSlot ps3 = new PalletSlot("position 3","TestTag3");
		ps1.save();
		ps2.save();
		ps3.save();
		assertNotNull( PalletSlot.find.byId(ps1.getPosition()));
		assertNotNull( PalletSlot.find.byId(ps2.getPosition()));
		assertNotNull( PalletSlot.find.byId(ps2.getPosition()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addWithUsedTag(){
		initMemory();
		PalletSlot ps1 = new PalletSlot("Position 1", t1);
		ps1.save();
		PalletSlot ps2 = new PalletSlot("Position 2", t1);
		ps2.save();
	}
	
	@Test(expected=PersistenceException.class)
	public void addWithSamePosition(){
		initMemory();
		PalletSlot ps1 = new PalletSlot("Position 1", t1);
		ps1.save();
		PalletSlot ps2 = new PalletSlot("Position 1", t2);
		ps2.save();
	}
	
    @Test
    public void getPalletMap() {
        initMemory();
        Pallet pallet = new Pallet(t1, t2);
        pallet.save();
        PalletSlot slot = new PalletSlot("position", t3);
        slot.save();
        Reader reader = new Reader("reader");
        reader.save();
        new MovedPallet(pallet, new Date(), slot, reader, true).save();
        
        slot = PalletSlot.find.all().get(0);
        
        assertThat(PalletSlot.getPalletMap().containsKey(slot.getPosition())).isTrue();
        assertThat(PalletSlot.getPalletMap().get(slot.getPosition()).getId()).isEqualTo(pallet.getId());
    }
	
	private void initMemory(){
		t1 = new Tag("testtag1");
		t2 = new Tag("testtag2");
		t3 = new Tag("testtag3");
		t1.save();
		t2.save();
		t3.save();
	}
}
