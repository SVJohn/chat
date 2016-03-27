package ua.nettel.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.SwingUtilities;

import ua.nettel.packet.Command;
import ua.nettel.packet.Data;
import ua.nettel.packet.Message;
import ua.nettel.packet.Packet;
import ua.nettel.packet.User;

public class Main {
	
	private static final String CONFIG_FILE = "config.properties";	
	private static final String LOCALE_KEY = "file.locale";
	private static final String NICKNAME_KEY = "nickname";
	private static final String SERVER_DEFAULT_KEY = "server.default";
	private static final String FORMAT_MESSAGE = "message";
	
	//private static final String SERVERS_LIST_FILE = "server";
	//private static Set <String> servers;
		
	private static Properties locale;
	private static String KEY_BY = "message.by";
	private static String KEY_SERVER_NAME = "server.name";
	private static String KEY_USER_NEW = "user.new";
	private static String KEY_USER_EXIT = "user.exit";
	
	private static Connect connect = null;

	private static Properties config;
	private static User user;

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
		
		Main.connect = new Connect (Main.getUser (),
				Main.getServerInfo() );							
		new Thread (connect).start(); 
	}
	public static void stopConnect () {
		if (null != connect) Main.connect.stop();
		Main.connect = null;
	}
	public boolean isConect () {
		return (null != connect && connect.isConnect());				
	}
	
	public void send (String text) {
		if (null != connect) {
			Packet packet = new Packet();
			packet.setCommand(new Command(Command.MESSAGE));
			Message message = new Message(text);
			packet.addData(Main.getUser () );
			packet.addData(message);
			connect.send(packet);
		}
	}
	
	
	public static void printMessage (Packet packet){
		List <Data> data = packet.getData();
		if ( null == data ) return;
		if ( null != data && data.isEmpty()) return;
		Data user = data.get(0);
		Data message = data.get(1);
		if ( user.getClass().equals(User.class) &&
				message.getClass().equals(Message.class) )
			{
			Main.printMessage(packet.getTime(), user.toString(), message.toString());
			}
			
	}
	private static void printMessage (Date date, String userInfo, String message){
		Formatter formatter = new Formatter();
		if (null == userInfo ) userInfo ="\t";
		formatter.format(config.getProperty(FORMAT_MESSAGE), date, Main.getLocaleText(KEY_BY), userInfo, message);
		Main.mainView.printMessage(formatter.toString());
		formatter.close();		
		//Main.mainActivity.printMessage(message);
		
	}
	
	public static void addUsers (Packet packet) {
		List <Data> data = packet.getData();
		if ( null == data ) return;
		if ( null != data && data.isEmpty()) return;
		if (0 == data.size()) {
			Main.printMessage (packet.getTime(), 
						   		Main.getLocaleText(KEY_SERVER_NAME), 
						   		String.format (Main.getLocaleText (Main.KEY_USER_NEW), data.get(0).toString()) );
		} else {
			Main.mainView.addInListUsers(data);
		}
		
	}
	public static void removeUser (Packet packet) {
		List <Data> data = packet.getData();
		if ( null != data && !data.isEmpty() &&
				data.get(0).getClass().equals(User.class)) 
		{
			User user = (User) data.get(0);
			Main.printMessage (packet.getTime(),
								Main.getLocaleText(KEY_SERVER_NAME), 
								String.format(Main.getLocaleText(Main.KEY_USER_EXIT), user.toString()) );
			Main.mainView.removeInListUsers(user.toString());
			if (Main.getUser().equals( user )) {
				Main.stopConnect();
			}
		}
	}

	
  	public static Server getDefaultServer () {
		return new Server(config.getProperty(SERVER_DEFAULT_KEY));
	}
	
	private static User getUser () {
		return Main.user;
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

		
		Main.user = new User (config.getProperty(NICKNAME_KEY));
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
