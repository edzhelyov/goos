package goos;

import goos.AuctionEventListener;

public class AuctionSniper implements AuctionEventListener {
	private final Auction auction;
	private SniperListener listener;
	private SniperSnapshot snapshot;
	
	public AuctionSniper(String itemId, Auction auction) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(itemId);
	}
	
	public void addSniperListener(SniperListener listener) {
		this.listener = listener;
	}

	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch (priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
			break;
		}
		notifyChange();
	}
	
	public SniperSnapshot getSnapshot() {
		return snapshot;
	}
	
	private void notifyChange() {
		listener.sniperStateChanged(snapshot);
	}
}
