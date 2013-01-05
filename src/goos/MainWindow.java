package goos;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class MainWindow extends JFrame {
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String SNIPERS_TABLE_NAME = "snipers table";
	
	private static final long serialVersionUID = 1L;
	public static final String NEW_ITEM_ID_NAME = "new item id";
	public static final String JOIN_BUTTON_NAME = "Join auction";
	public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";
	private SnipersTableModel snipers;
	private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

	public MainWindow(SniperPortfolio portfolio) {
		super(APPLICATION_TITLE);
		snipers = new SnipersTableModel();
		portfolio.addPortfolioListener(snipers);
		
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable(), makeControls());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void fillContentPane(JTable snipersTable, JPanel controls) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(controls, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}
	
	private JTable makeSnipersTable() {
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		
		return snipersTable;
	}
	
	private JPanel makeControls() {
		JPanel controls = new JPanel(new FlowLayout());
		final JTextField itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
		
		final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		stopPriceField.setColumns(7);
		stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
		controls.add(stopPriceField);
		
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
			}
			
			private String itemId() {
				return itemIdField.getText();
			}
			
			private int stopPrice() {
				return ((Number)stopPriceField.getValue()).intValue();
			}
		});
		controls.add(joinAuctionButton);
		
		return controls;
	}

	public void addUserRequestListener(UserRequestListener userRequestListener) {
		userRequests.addListener(userRequestListener);		
	}
}
