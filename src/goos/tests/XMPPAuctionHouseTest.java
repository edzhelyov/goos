package goos.tests;

import goos.Auction;
import goos.AuctionEventListener;
import goos.FakeAuctionServer;
import goos.XMPPAuctionHouse;
import goos.Item;
import static goos.FakeAuctionServer.XMPP_HOSTNAME;
import static goos.ApplicationRunner.SNIPER_ID;
import static goos.ApplicationRunner.SNIPER_PASSWORD;
import static goos.ApplicationRunner.SNIPER_XMPP_ID;

import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XMPPAuctionHouseTest {
	private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
	private XMPPAuctionHouse auctionHouse;
	
	@Before
	public void openConnection() throws Exception {
		auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
	}
	@After
	public void closeConnection() {
		if (auctionHouse != null) {
			auctionHouse.disconnect();
		}
	}
	
	@Before
	public void startAuction() throws XMPPException {
		auctionServer.startSellingItem();
	}
	
	@After
	public void stopAuction() {
		auctionServer.stop();
	}

	@Test
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		
		Auction auction = auctionHouse.auctionFor(new Item(auctionServer.getItemId(), 567));
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
