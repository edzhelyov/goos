package goos;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	void sniperLost();

	void sniperBidding();
}
