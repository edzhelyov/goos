package goos;

public class SniperLauncher implements UserRequestListener {
	private final SniperCollector collector;
	private final AuctionHouse auctionHouse;

	public SniperLauncher(SniperCollector collector, AuctionHouse auctionHouse) {
		this.collector = collector;
		this.auctionHouse = auctionHouse;
	}
	@Override
	public void joinAuction(Item item) {
		Auction auction = auctionHouse.auctionFor(item);
		
		AuctionSniper sniper = new AuctionSniper(item, auction);
		auction.addAuctionEventListener(sniper);
		collector.addSniper(sniper);
		auction.join();
	}
}
