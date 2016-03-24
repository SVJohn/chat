package ua.nettel.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Formatter;
import java.util.Properties;
import java.util.Set;

import javax.swing.SwingUtilities;

import ua.nettel.packet.Message;
import ua.nettel.packet.User;

public class Main {
	
	private static final String CONFIG_FILE = "config.properties";	
	private static final String LOCALE_KEY = "file.locale";
	private static final String NICKNAME_KEY = "nickname";
	private static final String SERVER_DEFAULT_KEY = "server.default";
	
	//private static final String SERVERS_LIST_FILE = "server";
	//private static Set <String> servers;
	
	private static Properties locale;
	
	private Connect connect = null;

	private static Properties config;
	private User user;

	private static MainView mainView;
	
	public static boolean isLoadServers () {
		return ServersLoader.getInstance().isDone();
	}
	public static Set <Server> getServers () {
		return ServersLoader.getInstance().getServers();
	}
	
	@Deprecated
	public static Properties getLocale () {
		return Main.locale;
	}
	
	public static String getLocaleText (String name) {
		return Main.locale.getProperty(name);
	}
	
	private static Server getServerInfo () {
		return Main.mainView.getSelectServer();
		//return new Server (Main.mainActivity.getSelectServer());
		
	}
	
	public void startConnect () {
//		this.connect = new Connect (this.user,
//									config.getProperty("server.host"), 
//									Integer.parseInt(config.getProperty("server.port")) );
		
		this.connect = new Connect (this.user,
				Main.getServerInfo() );							
		new Thread (connect).start(); 
	}
	public void stopConnect () {
		if (null != connect) this.connect.stop();
		this.connect = null;
	}
	public boolean isConect () {
		return (null != connect && connect.isConnect());				
	}
	
	public void send (String text) {
		if (null != connect) connect.send( new Message(this.user.getNickname(), text) );
	}
	
	
	public static void printMessage (Message message){
		//Main.mainActivity.printMessage(message.toString());
		
		Main.printMessage(message.getDate(), message.getNickname(), message.getMassage());
			
	}
	
	public static void printMessage (User user) {
		Formatter message = new Formatter();
		
		switch ( user.getCommand() ) {
		case User.COMMAND_ADD:
			message.format(Main.getLocaleText("user.new"), user.getNickname () );
			break;
		case User.COMMAND_DEL:
			message.format(Main.getLocaleText("user.exit"), user.getNickname () );
			break;
		default: 
			message.close();
			return;	
		}
		Main.printMessage (user.getDate(), null, message.toString());
		message.close();
	}
	
	public static void printMessage (Date date, String nickname, String message){
		Formatter formatter = new Formatter();
		if (null == nickname ) nickname ="\t";
		formatter.format(Main.getLocaleText("message"), date, nickname, message);
		Main.mainView.printMessage(formatter.toString());
		formatter.close();		
		//Main.mainActivity.printMessage(message);
		
	}
	
	public static Server getDefaultServer () {
		return new Server(config.getProperty(SERVER_DEFAULT_KEY));
	}
	
	private void build () {
		config = new Properties ();
		locale = new Properties ();
		
		try {
			config.load(new FileInputStream (CONFIG_FILE));
			locale.load(new FileInputStream (config.getProperty(LOCALE_KEY)));
			//serversList.load(new FileInputStream(SERVERS_LIST_FILE));
			
		} catch (IOException e) {
			System.err.println (e);
		}

		
		this.user = new User (config.getProperty(NICKNAME_KEY));
		Main.mainView =  new MainView(this);						//переписать активити на использование статического маина
		SwingUtilities.invokeLater( mainView );
	}
	
	public Main () {
		build();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main ();
		while (true) {
			boolean flag = false;
			if (main.isConect()) {
				flag = true;
			} else {
				flag = false;
			}
			Main.mainView.activateWork (flag);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				
			}
		}
	}

}
