package goos;

import goos.AuctionEventListener;

public class AuctionSniper implements AuctionEventListener {
	private final Auction auction;
	private final SniperListener listener;
	private boolean isWinning = false;
	
	public AuctionSniper(Auction auction, SniperListener listener) {
		this.auction = auction;
		this.listener = listener;
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
			listener.sniperBidding();
			auction.bid(price + increment);
			break;
		}
	}
}
