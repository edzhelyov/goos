package goos;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1247198670811592368L;
	private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0);
	private String statusText = MainWindow.STATUS_JOINING;
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
			return statusText;
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);
		}
	}

	public void setStatusText(String newStatusText) {
		sniperState = STARTING_UP;
		statusText = newStatusText;
		fireTableRowsUpdated(0, 0);
	}

	public void sniperStatusChanged(SniperSnapshot newSniperState, String newStatusText) {
		sniperState = newSniperState;
		statusText = newStatusText;
		fireTableRowsUpdated(0, 0);
	}
}
