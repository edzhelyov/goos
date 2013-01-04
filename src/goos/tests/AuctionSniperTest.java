package goos.tests;

import goos.Auction;
import goos.AuctionEventListener.PriceSource;
import goos.AuctionSniper;
import goos.SniperListener;
import goos.SniperSnapshot;
import goos.SniperState;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class AuctionSniperTest {
	private final Mockery context = new Mockery();
	private final String ITEM_ID = "item-id"; 
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction);
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
	public void reportsLostIfAuctionClosesWhenBidding() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
				then(sniperState.is("bidding"));
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
				when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(234, 56, PriceSource.FromOtherBidder);
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
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
				then(sniperState.is("bidding"));
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 123, SniperState.WINNING));
				when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(100,  23, PriceSource.FromOtherBidder);
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
	}
	
	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
				then(sniperState.is("winning"));
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON)));
				when(sniperState.is("winning"));
		}});

		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
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
