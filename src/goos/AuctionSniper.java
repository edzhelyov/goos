package goos;

import goos.AuctionEventListener;

public class AuctionSniper implements AuctionEventListener {
	private final Auction auction;
	private Item item;
	private SniperListener listener;
	private SniperSnapshot snapshot;
	
	public AuctionSniper(Item item, Auction auction) {
		this.item = item;
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(item.identifier);
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
			if (item.allowsBid(bid)) {
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
			} else {
				snapshot = snapshot.losing(price);
			}
			break;
		}
		notifyChange();
	}

	@Override
	public void auctionFailed() {
		snapshot = snapshot.failed();
		notifyChange();
	}
	
	public SniperSnapshot getSnapshot() {
		return snapshot;
	}
	
	private void notifyChange() {
		listener.sniperStateChanged(snapshot);
	}
}
