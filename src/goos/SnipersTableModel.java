package goos;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1247198670811592368L;
	private static String[] STATUS_TEXT = {
		"Joining", "Bidding", "Winning", "Lost", "Won" 
	};
	private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
	private SniperSnapshot sniperState = STARTING_UP;

	public int getColumnCount() {
		return Column.values().length;
	}

	public int getRowCount() {
		return 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (Column.at(columnIndex)) {
		case ITEM_IDENTIFIER:
			return sniperState.itemId;
		case LAST_PRICE:
			return sniperState.lastPrice;
		case LAST_BID:
			return sniperState.lastBid;
		case SNIPER_STATUS:
			return textFor(sniperState.state);
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);
		}
	}

	public void sniperStatusChanged(SniperSnapshot newSniperState) {
		sniperState = newSniperState;
		fireTableRowsUpdated(0, 0);
	}
	
	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}
}
