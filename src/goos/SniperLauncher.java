package goos;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class SniperLauncher implements UserRequestListener {
	private final SnipersTableModel snipers;
	private final AuctionHouse auctionHouse;
	private ArrayList<Auction> notToBeGCd = new ArrayList<Auction>();

	public SniperLauncher(SnipersTableModel snipers, AuctionHouse auctionHouse) {
		this.snipers = snipers;
		this.auctionHouse = auctionHouse;
	}
	@Override
	public void joinAuction(String itemId) {
		snipers.addSniper(SniperSnapshot.joining(itemId));
		
		Auction auction = auctionHouse.auctionFor(itemId);
		notToBeGCd.add(auction);
		auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener()));
		auction.join();
	}

	public class SwingThreadSniperListener implements SniperListener {
		@Override
		public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					snipers.sniperStateChanged(sniperSnapshot);
				}
			});
		}
	}
}
