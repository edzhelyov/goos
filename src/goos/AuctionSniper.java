package goos;

import goos.AuctionEventListener;

public class AuctionSniper implements AuctionEventListener {
	private final String itemId;
	private final Auction auction;
	private final SniperListener listener;
	private boolean isWinning = false;
	
	public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
		this.auction = auction;
		this.listener = listener;
		this.itemId = itemId;
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
		
		switch (priceSource) {
		case FromSniper:
			listener.sniperWinning();
			break;
		case FromOtherBidder:
			int bid = price + increment;
			auction.bid(bid);
			listener.sniperBidding(new SniperSnapshot(itemId, price, bid));
			break;
		}
	}
}
