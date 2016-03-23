package ua.nettel.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Properties;

public class Server {
	private static String host;
	private static int port;
	
	private static Properties config;
	private static Properties locale;
	
	private static KillerClosedConnections killerClosedConnections;
	
	private static final String PATH_CONFIG = "config/config_server.config";
	//private static String path_locale; 
	
	private static List <Connection> connections = new ArrayList<Connection>();
	
	public static void printLog (String text) {
		System.out.printf("%1$tT %1$td.%1$tm.%1$tY: %2$s \n", new Date(), text);
	}
	public static void printLog (Exception e){
		Server.printLog(e.toString());
	}
	public static void printLog (String format, Object... args ) {
		Formatter message = new Formatter();
		message.format(format, args);
		Server.printLog(message.toString());
		message.close();
	}
	
	public static String getLocaleText (String name) {
		return locale.getProperty(name);
	}

	public static int getCountConnections () {
		int count = 0;
		for (Connection c:connections){
			if (c.isConnect()) count ++;
		}
		return count;	
	}
	
	public static void main (String[] args) throws IOException {
		config = new Properties ();
		locale = new Properties ();
		ServerSocket sSocket = new ServerSocket();

		
		try {
			config.load(new FileInputStream(PATH_CONFIG));
			host = config.getProperty("server.host");
			port = Integer.parseInt(config.getProperty("server.port"));
			
			locale.load(new FileInputStream("config/"+config.getProperty("file.locale")));

			sSocket.bind(new InetSocketAddress(host, port));
			printLog ( locale.getProperty("start.server"), port );
			
			killerClosedConnections = new KillerClosedConnections(connections);
			new Thread(killerClosedConnections).start();
			
			while (true) {
				Socket socket = sSocket.accept();
				
				Connection newConect = new Connection (socket, connections);
				connections.add (newConect);
				new Thread(newConect).start();
				
			}
	
		} finally {
			if (null != sSocket) sSocket.close();
			printLog (locale.getProperty("stop.server"));
			killerClosedConnections.stop();
		}
	}

}
