package goos;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	void sniperLost();

	void sniperWon();

	void sniperStateChanged(SniperSnapshot sniperSnapshot);
}
