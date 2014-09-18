package models;

import app.AbstractBaseApp;
import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class PalletTest extends AbstractBaseApp{
	private Tag t1, t2, t3, t4;
	
	@Test
	public void addPalletCorrectly(){
		initMemory();
		Pallet p1 = new Pallet(t1, t2);
		Pallet p2 = new Pallet(t3,t4);
		p1.save();
		p2.save();
		assertNotNull( Pallet.find.byId(p1.getId()));
		assertNotNull( Pallet.find.byId(p2.getId()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addWithSameTag(){
		initMemory();
		Pallet p1 = new Pallet(t1,t1);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addIdenticalPallets(){
		initMemory();
		Pallet p1 = new Pallet(t1,t2);
		p1.save();
		Pallet p2 = new Pallet(t1,t2);
		p2.save();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addPalletWithFlippedTags(){
		initMemory();
		Pallet p1 = new Pallet(t1,t2);
		p1.save();
		Pallet p2 = new Pallet(t2,t1);
		p2.save();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addPalletWithTag1ThatIsNull(){
		initMemory();
		Pallet p1 = new Pallet(null,t1);
		p1.save(); 
	}
	@Test(expected=IllegalArgumentException.class)
	public void addPalletWithTag2ThatIsNull(){
		initMemory();
		Pallet p1 = new Pallet(t1,null);
		p1.save(); 
	}
	@Test(expected=IllegalArgumentException.class)
	public void addPalletWithUsedTag(){
		initMemory();
		PalletSlot ps = new PalletSlot("position", t1);
		ps.save();
		Pallet p1 = new Pallet(t1,t2);
		p1.save(); 
	}

    /**
     * This test will only work in MYSQL mode
     */
    @Test
    public void getSlotMap() {
        initMemory();
        Pallet pallet = new Pallet(t1, t2);
        pallet.save();
        PalletSlot slot = new PalletSlot("position", t3);
        slot.save();
        Reader reader = new Reader("reader");
        reader.save();
        new MovedPallet(pallet, new Date(), slot, reader, true).save();

        pallet = Pallet.find.all().get(0);
        assertThat(Pallet.getSlotMap().containsKey(pallet.getId())).isTrue();
        assertThat(Pallet.getSlotMap().get(pallet.getId()).getPosition()).isEqualTo("position");
    }

    private void initMemory(){
		t1 = new Tag("testtag1");
		t2 = new Tag("testtag2");
		t3 = new Tag("testtag3");
		t4 = new Tag("testtag4");
		t1.save();
		t2.save();
		t3.save();
		t4.save();
	}
	
	
}
