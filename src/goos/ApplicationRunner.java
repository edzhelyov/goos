package goos;

import static goos.FakeAuctionServer.XMPP_HOSTNAME;
import static goos.Main.AUCTION_RESOURCE;
import static goos.SniperState.JOINING;
import static goos.SniperState.BIDDING;
import static goos.SniperState.WINNING;
import static goos.SniperState.LOST;
import static goos.SniperState.WON;


public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/" + AUCTION_RESOURCE;
	private AuctionSniperDriver driver;
	private String itemId;
	
	public void startBiddingIn(final FakeAuctionServer... auctions) {
		Thread thread = new Thread("Test Application") {
			@Override public void run() {
				try {
					Main.main(arguments(auctions));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
		
		for (FakeAuctionServer auction : auctions) {
			driver.showsSniperStatus("", 0, 0, SnipersTableModel.textFor(JOINING));
		}
	}

	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(BIDDING));
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, SnipersTableModel.textFor(WINNING));
	}

	public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(LOST));
	}

	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, SnipersTableModel.textFor(WON));		
	}
	
	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}
	
	protected static String[] arguments(FakeAuctionServer... auctions) {
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++) {
			arguments[i + 3] = auctions[i].getItemId();
		}
		
		return arguments;
	}
}
