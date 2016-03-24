package ua.nettel.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Activity implements Runnable {
	
	private static final int SERVIS_PERIOD = 1000;
	
	private final String  titleFrame = "NetTel";
	
    private final int width = 800;
    private final int height = 400;
    //private final Properties locale;
    
    private Main main;
    
    private boolean flagOfActivateWork = false;

	private JTextArea tHistoryMessages;
	private JTextArea tNewMessage;
	
	private JList<String> viewServers;

	private JButton bSend;

	private JButton bConnect;

	private JButton bDisConnect;

	private JFrame mainFrame;

	private JPanel serversPanel;

	private Box boxServers; 
    
    public Activity (Main main) {
    	this.main = main;
    	//this.locale = Main.getLocale();
    }
    
    public void printMessage (String text) {
    	tHistoryMessages.append(text);
    	tHistoryMessages.append("\n\n");
    }
    
    public void activateWork (boolean flag) {
    	if (flag != this.flagOfActivateWork ) {
    		this.flagOfActivateWork = flag;
    		if (flag) {
    			bConnect.setEnabled(false);
				bDisConnect.setEnabled(true);
				if (tNewMessage.getText().length() > 0 ) bSend.setEnabled(true);
    		} else {
				bConnect.setEnabled(true);
				bDisConnect.setEnabled(false);
				bSend.setEnabled(false);
    		}
    	}
    }
    
	@Override
	public void run() {
		mainFrame = new JFrame();
		mainFrame.setTitle(titleFrame);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setPreferredSize(new Dimension(width, height));
		
		
		tHistoryMessages = new JTextArea ();
		tHistoryMessages.setEditable(false);
		//tHistoryMessages.setText( (new Message("Hello").toString()) );
		
		tNewMessage = new JTextArea ();
		tNewMessage.setRows(2);													//почему-то не работает(
		tNewMessage.setEditable(true);
		tNewMessage.setWrapStyleWord(true);
		tNewMessage.setLineWrap(true);
		tNewMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  //.createLineBorder(Color.GRAY)); 						//
		
		JPanel messagesPane = new JPanel ();
		
		messagesPane.setLayout(new BoxLayout(messagesPane, BoxLayout.PAGE_AXIS));
		messagesPane.add (new JScrollPane (tHistoryMessages));
		messagesPane.add (Box.createRigidArea(new Dimension (0, 10)));
		messagesPane.add (new JScrollPane(tNewMessage));
		messagesPane.add (Box.createRigidArea(new Dimension (0, 10)));
		bSend = new JButton ();
		//bSend.setText( locale.getProperty("button.send") );
		bSend.setText( Main.getLocaleText("button.send") );
		bSend.setEnabled(false);
		messagesPane.add (bSend);
		
		JPanel buttonPanel = new JPanel ();
		
		bConnect = new JButton ();
		bConnect.setText( Main.getLocaleText("button.connect") );
		buttonPanel.add (bConnect);
		
		bDisConnect = new JButton ();
		bDisConnect.setText( Main.getLocaleText("button.disconnect") );
		buttonPanel.add (bDisConnect);
		bDisConnect.setEnabled(false);

		bConnect.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.startConnect();
//				if (main.isConect()) {
//					bConnect.setEnabled(false);
//					bDisConnect.setEnabled(true);
//					if (tNewMessage.getText().length() > 0 ) bSend.setEnabled(true);
//				}
			}
		});
		bDisConnect.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.stopConnect();
//				bConnect.setEnabled(true);
//				bDisConnect.setEnabled(false);
//				bSend.setEnabled(false);
			}
		});
		bSend.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				String tmpMessage = tNewMessage.getText();
				if (null != tmpMessage && !tmpMessage.equals("") ) {
					main.send(tmpMessage);
					tNewMessage.setText(null);
					
				}
			}
		});
		
		tHistoryMessages.getDocument().addDocumentListener(new ListererHistoryMessage ());
		tNewMessage.getDocument().addDocumentListener(new ListenerFieldNewMessager () );
		
		
		boxServers = Box.createVerticalBox();
		boxServers.setBorder(new TitledBorder(Main.getLocaleText("title.ServersPanel")));
		
//		serversPanel = new JPanel();
//		serversPanel.setLayout(new BoxLayout(serversPanel, BoxLayout.PAGE_AXIS));
//		setTitle(serversPanel, 
//				Main.getLocaleText("title.ServersPanel"));
		
		new Thread(new LoadingServers()).start();
		
		BorderLayout mBLayout = new BorderLayout(); 
		Container cntPane = mainFrame.getContentPane();
		cntPane.setLayout(mBLayout);
		cntPane.add (messagesPane, BorderLayout.CENTER);
		cntPane.add(buttonPanel, BorderLayout.SOUTH);
		cntPane.add(boxServers, BorderLayout.WEST);
		//cntPane.add(serversPanel, BorderLayout.WEST);
		//cntPane.add(viewServers, BorderLayout.WEST);
		
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	// public static void main(String[] args) {
	//
	// }
	@Deprecated
	private void setTitle (JPanel panel, String title) {
		JLabel titleLabel = new JLabel(title);
		panel.add(titleLabel);
	}
	
	
	class ListererHistoryMessage implements DocumentListener{

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			SwingUtilities.invokeLater(new Runnable()  {
				@Override
				public void run() {
					tHistoryMessages.setCaretPosition(tHistoryMessages.getDocument().getLength());
				}
			});
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
		}
		
	}
	class ListenerFieldNewMessager implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {	}

		@Override
		public void insertUpdate(DocumentEvent e) {		
/*			SwingUtilities.invokeLater(new Runnable()  {
				@Override
				public void run() {
					System.out.println(tNewMessage.getText().length());
					if (null != tNewMessage.getText() && !tNewMessage.getText().equals("") && main.isConect()) {
						bSend.setEnabled(true);
					} else {
						bSend.setEnabled(false);
					}
				}
				
			});*/
			SwingUtilities.invokeLater(new Runnable()  {
				@Override
				public void run() {
					if (main.isConect()) bSend.setEnabled(true);
				}
				
			});
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() { 
					if (0 == tNewMessage.getText().length()) {			//System.out.println(tNewMessage.getText());System.out.println(tNewMessage.getText().length());
						bSend.setEnabled(false);
					}
				}

			});
		}
	}
	class LoadingServers implements Runnable {

		
		

		@Override
		public void run() {
			JLabel loadingInfo = new JLabel(Main.getLocaleText("loadind.info"));
			boxServers.add(loadingInfo);
			while (!Main.isLoadServers()) {

				try {
					Thread.sleep(SERVIS_PERIOD);
				} catch (InterruptedException e) {
				}
			}
			Set<String> servers = Main.getServers();
			String[] arrayServers = {};
			arrayServers = servers.toArray(new String[servers.size()]);
			viewServers = new JList<String>(arrayServers);
			viewServers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			boxServers.remove(loadingInfo);
			boxServers.add(new JScrollPane(viewServers));
			mainFrame.pack();
		}
//		public void run() {
//			JLabel loadingInfo = new JLabel(Main.getLocaleText("loadind.info"));
//			serversPanel.add(loadingInfo);
//			while (!Main.isLoadServers()){
//				
//				try {
//					Thread.sleep(SERVIS_PERIOD);
//				} catch (InterruptedException e) {		}
//			}
//			Set <String> servers = Main.getServers();
//			String[] arrayServers = {};
//			arrayServers = servers.toArray(new String[servers.size()]);
//			viewServers = new JList<String>(arrayServers);
//			viewServers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//			serversPanel.remove(loadingInfo);
//			serversPanel.add( new JScrollPane (viewServers));
//			mainFrame.pack();
//		}
		
	}
}
