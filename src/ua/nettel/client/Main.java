package ua.nettel.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.SwingUtilities;

import ua.nettel.packet.Message;
import ua.nettel.packet.User;

public class Main {

	private static Properties locale;
	
	private Connect connect = null;

	private static Properties config;
	private User user;

	private static Activity mainActivity;
	
	@Deprecated
	public static Properties getLocale () {
		return Main.locale;
	}
	
	public static String getLocaleText (String name) {
		return Main.locale.getProperty(name);
	}
	
	public void startConnect () {
		this.connect = new Connect (this.user,
									config.getProperty("server.host"), 
									Integer.parseInt(config.getProperty("server.port")) );
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
		Main.mainActivity.printMessage(message.toString());
	}
	
	private void build () {
		config = new Properties ();
		locale = new Properties ();
		try {
			config.load(new FileInputStream ("config.properties"));
			locale.load(new FileInputStream (config.getProperty("file.locale")));
			
		} catch (IOException e) {
			System.err.println (e);
		}

		
		this.user = new User (config.getProperty("nickname"));
		Main.mainActivity =  new Activity(this);						//переписать активити на использование статического маина
		SwingUtilities.invokeLater( mainActivity );
	}
	
	public Main () {
		build();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main ();
		/*while (true) {
			if ()
		}*/
	}

}
