package goos;

import javax.swing.SwingUtilities;

public class SwingThreadSniperListener implements SniperListener {
	private final SniperListener snipers;
	
	public SwingThreadSniperListener(SniperListener snipers) {
		this.snipers = snipers;
	}
	
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
