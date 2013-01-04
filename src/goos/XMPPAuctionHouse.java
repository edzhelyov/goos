package goos;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	private final XMPPConnection connection;

	public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);

		return new XMPPAuctionHouse(connection);
	}
	
	public XMPPAuctionHouse(XMPPConnection connection) {
		this.connection = connection;
	}

	@Override
	public Auction auctionFor(String itemId) {
		return new XMPPAuction(connection, XMPPAuctionHouse.auctionId(itemId, connection));
	}
	
	public void disconnect() {
		connection.disconnect();		
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}
}
