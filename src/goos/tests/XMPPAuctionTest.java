package goos.tests;

import goos.Auction;
import goos.AuctionEventListener;
import goos.FakeAuctionServer;
import goos.Main;
import goos.XMPPAuction;
import static goos.FakeAuctionServer.XMPP_HOSTNAME;
import static goos.ApplicationRunner.SNIPER_ID;
import static goos.ApplicationRunner.SNIPER_PASSWORD;
import static goos.ApplicationRunner.SNIPER_XMPP_ID;

import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import org.jivesoftware.smack.XMPPConnection;
import org.junit.Before;
import org.junit.Test;

public class XMPPAuctionTest {
	private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
	private XMPPConnection connection;
	
	@Before
	public void openConnection() throws Exception {
		connection = Main.connection(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
	}

	@Test
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		
		auctionServer.startSellingItem();
		
		Auction auction = new XMPPAuction(connection, auctionServer.getItemId());
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		
		auction.join();
		auctionServer.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
		auctionServer.announceClosed();
		
		assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));
	}

	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			public void auctionClosed() {
				auctionWasClosed.countDown();
			}

			public void currentPrice(int price, int increment, PriceSource priceSource) {
				// Not implemented
			}
		};
	}
}
