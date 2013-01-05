package goos.tests;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;

import goos.AuctionSniperDriver;
import goos.Item;
import goos.MainWindow;
import goos.SniperPortfolio;
import goos.UserRequestListener;

public class MainWindowTest {
	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
	
	@Test
	public void makesUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<Item>(equalTo(new Item("an item-id", 789)), "item request");
		
		mainWindow.addUserRequestListener(
				new UserRequestListener() {
					@Override
					public void joinAuction(Item item) {
						itemProbe.setReceivedValue(item);
					}
		});
		
		driver.startBiddingFor("an item-id", 789);
		driver.check(itemProbe);
	}
}
