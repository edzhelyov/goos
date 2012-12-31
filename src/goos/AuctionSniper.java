package goos;

import goos.AuctionEventListener;

public class AuctionSniper implements AuctionEventListener {
	private final Auction auction;
	private final SniperListener listener;
	private boolean isWinning = false;
	private SniperSnapshot snapshot;
	
	public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
		this.auction = auction;
		this.listener = listener;
		this.snapshot = SniperSnapshot.joining(itemId);
	}

	@Override
	public void auctionClosed() {
		if (isWinning) {
			listener.sniperWon();
		} else {
			listener.sniperLost();	
		}
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		isWinning = (priceSource == PriceSource.FromSniper);
		
		if (isWinning) {
			snapshot = snapshot.winning(price);
		} else {
			int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
		}
		
		listener.sniperStateChanged(snapshot);
	}
}
