package ua.nettel.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainView implements Runnable {
	
	private static final int SERVIS_PERIOD = 1000;
	
	private static final String KEY_TITLE_SERVERS_LIST = "title.ServersPanel";
	private static final String KEY_TITLE_HISTORY = "title.MessageHistory";
	private static final String KEY_TITLE_MESSAGE = "title.newMessage";
	private static final String KEY_TITLE_USERS = "title.users";
	
	private final String  titleFrame = "NetTel";
	
    private final int width = 800;
    private final int height = 400;
        
    private Main main;
    
    private boolean flagOfActivateWork = false;
    
    //private boolean selectedServer = false;
    private static int indexDefaultServer;
    
	private JTextArea tHistoryMessages;
	private JTextArea tNewMessage;
	
//	private JList<String> viewServers;
//	private ListModel<String> modelListServers = new DefaultListModel<>();
	private JList<String> viewServers;
	private ListModel<String> modelListServers = new DefaultListModel<>();
	
	private JList<String> viewUsers;
	private ListModel<String> modelListUsers = new DefaultListModel <>();   // null;
	
	private JButton bSend;

	private JButton bConnect;

	private JButton bDisConnect;

	private JFrame mainFrame;

	private Box boxServers;

	private Box bUsers;

	   
    public MainView (Main main) {
    	this.main = main;
    	//this.locale = Main.getLocale();
    }
    
    public void printMessage (String text) {
    	tHistoryMessages.append(text);
    	tHistoryMessages.append("\n\n");
    }
    
//    private boolean isSelectedServer () {
//    	return selectedServer;
//    }
    
    public Server getSelectServer () {
    	int index = viewServers.getSelectedIndex();
    	//return viewServers.getModel().getElementAt(index);
    	return new Server (viewServers.getModel().getElementAt(index));
    }
    
    
    public void activateWork (boolean flag) {
    	if (flag != this.flagOfActivateWork ) {
    		this.flagOfActivateWork = flag;
    		if (flag) {
    			bConnect.setEnabled(false);
				bDisConnect.setEnabled(true);
				if (tNewMessage.getText().length() > 0 ) bSend.setEnabled(true);
				viewServers.setEnabled(false);
				//viewUsers.setEnabled(true);
				bUsers.setVisible(true);
    		} else {
				bConnect.setEnabled(true);
				bDisConnect.setEnabled(false);
				bSend.setEnabled(false);
				viewServers.setEnabled(true);
				DefaultListModel <String> model = (DefaultListModel <String>) modelListUsers;
				model.clear();
				//viewUsers.setEnabled(false);
				bUsers.setVisible(false);
    		}
    	}
    }
    
    public void addInListUsers (String user){
    	//if (null == modelListUsers) modelListUsers = new DefaultListModel <>();
    	DefaultListModel <String> model = (DefaultListModel <String>) modelListUsers;
    	model.addElement(user);
		int index = model.size() - 1;
        viewUsers.ensureIndexIsVisible(index);
    	
    }
    public void removeInListUsers (String user){
    	DefaultListModel <String> model = (DefaultListModel <String>) modelListUsers;
    	model.removeElement(user);
    }
    
	@Override
	public void run() {
		mainFrame = new JFrame();
		
		mainFrame.setTitle(titleFrame);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setPreferredSize(new Dimension(width, height));
		
		tHistoryMessages = new JTextArea ();
		tHistoryMessages.setEditable(false);
		
		Box bHistoryMessages = Box.createVerticalBox();
		bHistoryMessages.setBorder(new TitledBorder(Main.getLocaleText(KEY_TITLE_HISTORY)));
		bHistoryMessages.add(new JScrollPane (tHistoryMessages));
				
		tNewMessage = new JTextArea ();
		tNewMessage.setRows(2);													//почему-то не работает(
		tNewMessage.setEditable(true);
		tNewMessage.setWrapStyleWord(true);
		tNewMessage.setLineWrap(true);
		tNewMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  //.createLineBorder(Color.GRAY));
		
		
		Box bNewMessage = Box.createVerticalBox();
		bNewMessage.setBorder(new TitledBorder(Main.getLocaleText(KEY_TITLE_MESSAGE)));
		bNewMessage.add(new JScrollPane (tNewMessage));
		
		JPanel messagesPane = new JPanel ();
		
		messagesPane.setLayout(new BoxLayout(messagesPane, BoxLayout.PAGE_AXIS));
		messagesPane.add (bHistoryMessages);
		messagesPane.add (Box.createRigidArea(new Dimension (0, 10)));
		messagesPane.add (bNewMessage);
		messagesPane.add (Box.createRigidArea(new Dimension (0, 10)));
		
		bSend = new JButton ();
		bSend.setText( Main.getLocaleText("button.send") );
		bSend.setEnabled(false);
		bSend.setMnemonic(KeyEvent.VK_ENTER);										// key: ALT + Enter
		messagesPane.add (bSend);
		
		JPanel buttonPanel = new JPanel ();
		
		bConnect = new JButton ();
		bConnect.setText( Main.getLocaleText("button.connect") );
		buttonPanel.add (bConnect);
	//	bConnect.setEnabled(false);
		
		bDisConnect = new JButton ();
		bDisConnect.setText( Main.getLocaleText("button.disconnect") );
		buttonPanel.add (bDisConnect);
		bDisConnect.setEnabled(false);

		bConnect.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.startConnect();
				tNewMessage.requestFocus();
			}
		});
		bDisConnect.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.stopConnect();
			}
		});
		bSend.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				String tmpMessage = tNewMessage.getText();
				if (null != tmpMessage && !tmpMessage.equals("") ) {
					main.send(tmpMessage);
					tNewMessage.setText(null);
					tNewMessage.requestFocus();
					
				}
			}
		});
		
		tHistoryMessages.getDocument().addDocumentListener(new ListererHistoryMessage ());
		tNewMessage.getDocument().addDocumentListener(new ListenerFieldNewMessager () );
		
		
		boxServers = Box.createVerticalBox();
		boxServers.setBorder(new TitledBorder(Main.getLocaleText(KEY_TITLE_SERVERS_LIST)));

		viewServers = new JList<String> (modelListServers);
        viewServers.addListSelectionListener(new ListSelectionListener() {
            

			public void valueChanged(ListSelectionEvent e) {
                int index = viewServers.getSelectedIndex();
                if (index <0 ) viewServers.setSelectedIndex(indexDefaultServer);
                }
        });
        
		boxServers.add(new JScrollPane(viewServers));
		viewServers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new Thread(new LoadingServers()).start();
		
		bUsers = Box.createVerticalBox();
		bUsers.setBorder(new TitledBorder(Main.getLocaleText(KEY_TITLE_USERS)));
		viewUsers = new JList<String>(modelListUsers);
		bUsers.setVisible(false);
		//viewServers.setMaximumSize (new Dimension(50, 10));
		bUsers.add(new JScrollPane(viewUsers));
		
		
		BorderLayout mBLayout = new BorderLayout(); 
		Container cntPane = mainFrame.getContentPane();
		cntPane.setLayout(mBLayout);
		cntPane.add (messagesPane, BorderLayout.CENTER);
		cntPane.add(buttonPanel, BorderLayout.SOUTH);
		cntPane.add(boxServers, BorderLayout.WEST);
		cntPane.add(bUsers, BorderLayout.EAST);
		
//		mainFrame.addWindowListener( new WindowAdapter() {						//фокус на tNewMessage при загрузке JFrame 
//		    public void windowOpened( WindowEvent e ){
//		        tNewMessage.requestFocus();
//		    }
//		});
		
		
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
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
			
			Set<Server> servers = Main.getServers();
			Server[] arrayServers = {};
			arrayServers = servers.toArray(new Server[servers.size()]);
//			viewServers = new JList<String>(arrayServers);
//			viewServers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			DefaultListModel <String> model = (DefaultListModel<String>) modelListServers;
			
			for (Server elm:arrayServers) {
				model.addElement(elm.toString());
				int index = model.size() - 1;
                if (elm.equals( Main.getDefaultServer())) {
                	viewServers.setSelectedIndex(index);
                	indexDefaultServer = index; 
                }
                viewServers.ensureIndexIsVisible(index);
			}
			boxServers.remove(loadingInfo);
//			boxServers.add(new JScrollPane(viewServers));
			mainFrame.pack();
			
		}

		
	}
}
