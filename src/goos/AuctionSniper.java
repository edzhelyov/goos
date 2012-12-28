package goos;

public class AuctionSniper implements AuctionEventListener {
	private final Auction auction;
	private final SniperListener listener;
	
	public AuctionSniper(Auction auction, SniperListener listener) {
		this.auction = auction;
		this.listener = listener;
	}

	@Override
	public void auctionClosed() {
		listener.sniperLost();
	}

	@Override
	public void currentPrice(int price, int increment) {
		listener.sniperBidding();
		auction.bid(price + increment);
	}
}
