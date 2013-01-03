package goos.tests;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;

import goos.AuctionSniperDriver;
import goos.MainWindow;
import goos.SnipersTableModel;
import goos.UserRequestListener;

public class MainWindowTest {
	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final MainWindow mainWindow = new MainWindow(tableModel);
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
		
		driver.startBiddingFor("an item-id");
		driver.check(buttonProbe);
	}
}
