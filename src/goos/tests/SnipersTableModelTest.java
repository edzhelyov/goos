package goos.tests;

import goos.AuctionSniper;
import goos.Item;
import goos.SniperSnapshot;
import goos.SnipersTableModel;
import goos.Column;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SnipersTableModelTest {
	private final Mockery context = new Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private AuctionSniper sniper = new AuctionSniper(new Item("item id", 567), null);
	private final SnipersTableModel model = new SnipersTableModel();
	
	@Before
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}
	
	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}
	
	@Test
	public void setsSniperValuesInColumns() {
		SniperSnapshot bidding = sniper.getSnapshot().bidding(123, 100);
		context.checking(new Expectations() {{
			allowing(listener).tableChanged(with(anInsertionAtRow(0)));
			
			one(listener).tableChanged(with(aRowChangedEvent(0)));
		}});
		
		model.sniperAdded(sniper);
		model.sniperStateChanged(bidding);
		
		assertRowMatchesSnapshot(0, bidding);
	}
	
	@Test
	public void setsUpColumnHeadings() {
		for (Column column : Column.values()) {
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}
	
	@Test
	public void notifiesListenersWhenAddingASniper() {
		context.checking(new Expectations() {{
			one(listener).tableChanged(with(anInsertionAtRow(0)));
		}});
		
		assertEquals(0, model.getRowCount());
		
		model.sniperAdded(sniper);
		
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, sniper.getSnapshot());
	}
	
	@Test
	public void holdsSnipersInAdditionOrder() {
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.sniperAdded(new AuctionSniper(new Item("item 0", 567), null));
		model.sniperAdded(new AuctionSniper(new Item("item 1", 567), null));
		
		assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}
	
	private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
		assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
	    assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
	    assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
	    assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
	}
	
	private Object cellValue(int rowIndex, Column column) {
	    return model.getValueAt(rowIndex, column.ordinal());
	}
	
	private Matcher<TableModelEvent> aRowChangedEvent(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row));
	}
	
	private  Matcher<TableModelEvent> anInsertionAtRow(final int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}
}
