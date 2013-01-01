package goos.tests;

import goos.Column;
import goos.SniperSnapshot;
import goos.SniperState;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ColumnTest {
	@Test
	public void retrievesValuesFromASniperSnapshot() {
		SniperSnapshot snapshot = new SniperSnapshot("item id", 123, 145, SniperState.BIDDING);
		
		assertEquals("item id", Column.ITEM_IDENTIFIER.valueIn(snapshot));
		assertEquals(123, Column.LAST_PRICE.valueIn(snapshot));
		assertEquals(145, Column.LAST_BID.valueIn(snapshot));
		assertEquals("Bidding", Column.SNIPER_STATE.valueIn(snapshot));
	}
	
}
