package goos;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final Chat chat;
	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
	
	public XMPPAuction(XMPPConnection connection, String itemJId, XMPPFailureReporter failureReporter) {
		AuctionMessageTranslator translator = translatorFor(connection, failureReporter);
		chat = connection.getChatManager().createChat(itemJId, translator);
		addAuctionEventListener(chatDisconectorFor(translator));
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
	
	
	private AuctionMessageTranslator translatorFor(XMPPConnection connection, XMPPFailureReporter failureReporter) {
		return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
	}
	
	private AuctionEventListener chatDisconectorFor(final AuctionMessageTranslator translator) {
		return new AuctionEventListener() {
			public void auctionFailed() {
				chat.removeMessageListener(translator);
			}
			
			public void auctionClosed() {
				// Not Implemented	
			}
			
			public void currentPrice(int price, int increment, PriceSource priceSource) {
				// Not Implemented
			}
		};
	}
	
	private void sendMessage(final String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}
