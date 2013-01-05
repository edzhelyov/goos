package goos.tests;

import goos.Auction;
import goos.AuctionEventListener.PriceSource;
import goos.AuctionSniper;
import goos.Item;
import goos.SniperListener;
import goos.SniperSnapshot;
import goos.SniperState;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class AuctionSniperTest {
	private final Mockery context = new Mockery();
	private final String ITEM_ID = "item-id"; 
	private final Item item = new Item(ITEM_ID, 1234);
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	private final AuctionSniper sniper = new AuctionSniper(item, auction);
	private final States sniperState = context.states("sniper");
	
	@Before
	public void addSniperListener() {
		sniper.addSniperListener(sniperListener);
	}
	
	@Test
	public void reportsLostWhenAuctionClosesImmediately() {
		context.checking(new Expectations() {{ 
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
		}});
		
		sniper.auctionClosed();
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;
		
		context.checking(new Expectations() {{
			one(auction).bid(price + increment);
			atLeast(1).of(sniperListener).sniperStateChanged(
					new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
		}});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}
	
	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 123, SniperState.WINNING));
				when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(100,  23, PriceSource.FromOtherBidder);
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
	}
	
	@Test
	public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1200, 0, SniperState.LOSING));
		}});
		
		sniper.currentPrice(1200, 40, PriceSource.FromOtherBidder);
	}
	
	@Test
	public void doesNotBidAndReportLosingIfSubsequentPriceIsAboveStopPrice() {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			int bid = 123 + 45;
			allowing(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING));
				when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(2345, 10, PriceSource.FromOtherBidder);
	}
	
	@Test
	public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
		allowingSniperBidding();
		allowingSniperWinning();
		context.checking(new Expectations() {{
			int bid = 123 + 45;
			allowing(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, 168, SniperState.LOSING));
				when(sniperState.is("winning"));
		}});
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(168, 10, PriceSource.FromSniper);
		sniper.currentPrice(2345, 10, PriceSource.FromOtherBidder);
	}
	
	@Test
	public void continuesToBeLosingOnceStopPriceHasBeenReached() {
		final Sequence states = context.sequence("sniper states");
		
		context.checking(new Expectations() {{ 
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, 0, SniperState.LOSING));
				inSequence(states);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2355, 0, SniperState.LOSING));
				inSequence(states);	
		}});
		
		sniper.currentPrice(2345, 10, PriceSource.FromOtherBidder);
		sniper.currentPrice(2355, 10, PriceSource.FromOtherBidder);
	}
	
	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		allowingSniperWinning();
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON)));
				when(sniperState.is("winning"));
		}});

		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
	}
	
	@Test
	public void reportsLostIfAuctionClosesWhenBidding() {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
				when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(234, 56, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	@Test
	public void reportsLostIfAuctionClosesWhenLosing() {
		allowingSniperLosing();
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, 0, SniperState.LOST));
				when(sniperState.is("losing"));
		}});
		
		sniper.currentPrice(2345, 10, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	private void allowingSniperLosing() {
		allowingSniperStateChange(SniperState.LOSING, "losing");
	}
	
	private void allowingSniperBidding() {
		allowingSniperStateChange(SniperState.BIDDING, "bidding");
	}
	
	private void allowingSniperWinning() {
		allowingSniperStateChange(SniperState.WINNING, "winning");
	}
	
	private void allowingSniperStateChange(final SniperState newState, final String stateName) {
		context.checking(new Expectations() {{
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(newState)));
				then(sniperState.is(stateName));
		}});
	}
	
	private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was")
				{
					@Override
					protected SniperState featureValueOf(SniperSnapshot actual) {
						return actual.state;
					}
				};
	}
}
