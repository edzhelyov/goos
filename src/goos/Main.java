package goos;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Main {
	private MainWindow ui;
	
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	private final SnipersTableModel snipers = new SnipersTableModel();

	private ArrayList<Chat> notToBeGCd = new ArrayList<Chat>();

	public Main() throws Exception {
		startUserInterface();
	}

	public static void main(String... args) throws Exception {
		Main main = new Main();
		XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		
		main.disconnectWhenUICloses(connection);
		main.addUserRequestListenerFor(connection);
	}

	private void addUserRequestListenerFor(final XMPPConnection connection) {
		ui.addUserRequestListener(new UserRequestListener() {
			@Override
			public void joinAuction(String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));
				final Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);				
				notToBeGCd.add(chat);
				
				Auction auction = new XMPPAuction(chat);
				
				chat.addMessageListener(new AuctionMessageTranslator(
						connection.getUser(),
						new AuctionSniper(itemId, auction, new SwingThreadSniperListener())));
				auction.join();
			}
		});
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);

		return connection;
	}

	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
		  public void run() {
			  ui = new MainWindow(snipers);
		  }
		});
	}

	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				connection.disconnect();
			}
		});
	}
	
	public class SwingThreadSniperListener implements SniperListener {
		@Override
		public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					snipers.sniperStateChanged(sniperSnapshot);
				}
			});
		}
	}
}
