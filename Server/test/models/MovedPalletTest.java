package models;

import java.util.Date;
import static org.junit.Assert.*;
import app.AbstractBaseApp;

public class MovedPalletTest extends AbstractBaseApp {

	//Constructor tests
	public void makeAMove(){
		Pallet pallet = new Pallet(new Tag("tag1"),new Tag("tag2"));
		PalletSlot ps = new PalletSlot("Slot", new Tag("tag3"));
		MovedPallet mp = new MovedPallet(pallet, new Date(), ps, new Reader("jensa"), true);
		assertTrue(MovedPallet.find.all().size()==1);
	}
}
