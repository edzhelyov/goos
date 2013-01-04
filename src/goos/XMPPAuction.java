package goos;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final Chat chat;
	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
	
	public XMPPAuction(XMPPConnection connection, String itemJId) {
		chat = connection.getChatManager().createChat(itemJId, null);
		chat.addMessageListener(new AuctionMessageTranslator(
				connection.getUser(),
				auctionEventListeners.announce()));
		
	}
	
	public void addAuctionEventListener(AuctionEventListener listener) {
		auctionEventListeners.addListener(listener);
	}
	
	public void join() {
		sendMessage(JOIN_COMMAND_FORMAT);
	}
	
	public void bid(int amount) {
		sendMessage(String.format(BID_COMMAND_FORMAT, amount));
	}
	
	private void sendMessage(final String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}
