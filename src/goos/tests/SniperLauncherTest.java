package goos.tests;

import goos.Auction;
import goos.AuctionHouse;
import goos.AuctionSniper;
import goos.Item;
import goos.SniperCollector;
import goos.SniperLauncher;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Test;

public class SniperLauncherTest {
	private final Mockery context = new Mockery();
	private final Auction auction = context.mock(Auction.class);
	private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
	private final SniperCollector collector = context.mock(SniperCollector.class);
	private final SniperLauncher launcher = new SniperLauncher(collector, auctionHouse);
	private final States auctionState = context.states("auction state").startsAs("not joined");


	@Test
	public void addsNewSniperToCollectorAndThenJoinsAuction() {
		final Item item = new Item("item 123", 789);
		context.checking(new Expectations() {{
			allowing(auctionHouse).auctionFor(item);
				will(returnValue(auction));
			oneOf(auction).addAuctionEventListener(with(sniperForItem(item)));
				when(auctionState.is("not joined"));
			oneOf(collector).addSniper(with(sniperForItem(item)));
				when(auctionState.is("not joined"));
			one(auction).join();
				then(auctionState.is("joined"));
		}});
		
		launcher.joinAuction(item);
	}
	
	protected Matcher<AuctionSniper>sniperForItem(Item item) {
		return new FeatureMatcher<AuctionSniper, String>(equalTo(item.identifier), "sniper with item id", "item") {
			@Override protected String featureValueOf(AuctionSniper actual) {
				return actual.getSnapshot().itemId;
			}
		};
	}
}
