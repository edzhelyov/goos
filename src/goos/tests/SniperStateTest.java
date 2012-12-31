package goos.tests;

import goos.SniperState;

import org.junit.Test;

import com.objogate.exception.Defect;

import static org.junit.Assert.assertEquals;

public class SniperStateTest {
	@Test
	public void isWonWhenAuctionClosesWhileWinning() {
		assertEquals(SniperState.WON, SniperState.WINNING.whenAuctionClosed());
	}
	
	@Test
	public void isLostWhenAuctionClosesWhileJoiningOrBidding() {
		assertEquals(SniperState.LOST, SniperState.JOINING.whenAuctionClosed());
		assertEquals(SniperState.LOST, SniperState.BIDDING.whenAuctionClosed());
	}
	
	@Test(expected=Defect.class)
	public void defectIfAuctionClosesWhenWon() {
		SniperState.WON.whenAuctionClosed();
	}
	
	@Test(expected=Defect.class)
	public void defectIfAuctionClosesWhenLost() {
		SniperState.LOST.whenAuctionClosed();
	}
}
