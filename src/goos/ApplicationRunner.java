package goos;

import static goos.FakeAuctionServer.XMPP_HOSTNAME;
import static goos.Main.AUCTION_RESOURCE;

public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/" + AUCTION_RESOURCE;
	private AuctionSniperDriver driver;
	
	public void startBiddingIn(final FakeAuctionServer auction) {
		Thread thread = new Thread("Test Application") {
			@Override public void run() {
				try {
					Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showsSniperStatus(MainWindow.STATUS_JOINING);
	}

	public void hasShownSniperIsBidding() {
		driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
	}

	public void hasShownSniperIsWinning() {
		driver.showsSniperStatus(MainWindow.STATUS_WINNING);
	}

	public void showsSniperHasLostAuction() {
		driver.showsSniperStatus(MainWindow.STATUS_LOST);
	}

	public void showsSniperHasWonAuction() {
		driver.showsSniperStatus(MainWindow.STATUS_WON);		
	}
	
	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}
}
