package goos.tests;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;

import goos.AuctionSniperDriver;
import goos.MainWindow;
import goos.SniperPortfolio;
import goos.UserRequestListener;

public class MainWindowTest {
	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
	
	@Test
	public void makesUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");
		
		mainWindow.addUserRequestListener(
				new UserRequestListener() {
					@Override
					public void joinAuction(String itemId) {
						buttonProbe.setReceivedValue(itemId);
					}
		});
		
		driver.startBiddingFor("an item-id", Integer.MAX_VALUE);
		driver.check(buttonProbe);
	}
}
