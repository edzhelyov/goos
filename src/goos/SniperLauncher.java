package goos;

public class SniperLauncher implements UserRequestListener {
	private final SniperCollector collector;
	private final AuctionHouse auctionHouse;

	public SniperLauncher(SniperCollector collector, AuctionHouse auctionHouse) {
		this.collector = collector;
		this.auctionHouse = auctionHouse;
	}
	@Override
	public void joinAuction(String itemId) {
		Auction auction = auctionHouse.auctionFor(itemId);
		
		AuctionSniper sniper = new AuctionSniper(itemId, auction);
		auction.addAuctionEventListener(sniper);
		collector.addSniper(sniper);
		auction.join();
	}
}
