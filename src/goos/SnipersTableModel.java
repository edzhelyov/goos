package goos;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
	private static final long serialVersionUID = 1247198670811592368L;
	private static String[] STATUS_TEXT = {
		"Joining", "Bidding", "Winning", "Losing", "Lost", "Won", "Failed"
	};
	private ArrayList<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

	public int getColumnCount() {
		return Column.values().length;
	}

	public int getRowCount() {
		return snapshots.size();
	}
	
	@Override
	public String getColumnName(int column) {
		return Column.at(column).name;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}

	public void sniperStateChanged(SniperSnapshot newSniperState) {
		for (int i = 0; i < snapshots.size(); i++) {
			if (newSniperState.isForSameItemAs(snapshots.get(i))) {
				snapshots.set(i, newSniperState);
				fireTableRowsUpdated(i, i);
				return;
			}
		}
		
		throw new Defect("No existing Sniper state for " + newSniperState.itemId);
	}
	
	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	@Override
	public void sniperAdded(AuctionSniper sniper) {
		addSniperSnapshot(sniper.getSnapshot());
		sniper.addSniperListener(new SwingThreadSniperListener(this));
	}

	private void addSniperSnapshot(SniperSnapshot snapshot) {
		snapshots.add(snapshot);
		int row = snapshots.size() - 1;
		fireTableRowsInserted(row, row);
	}
}
