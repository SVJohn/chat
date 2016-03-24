package ua.nettel.client;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author John
 * 
 * Singleton
 * 
 */
public class ServersLoader {
	
	private static ServersLoader serversReader = null;
	
	private static Set <Server> servers;
	
	private static boolean complete = false;
	
	private static final String SERVERS_FILE = "servers";
	
	private ServersLoader (){
		serversReader = this;
		new Thread(getRun()).start();
	}
	public static ServersLoader getInstance (){
		if (null != serversReader) return serversReader;
		   else {
			   serversReader = new ServersLoader(); 
			   return serversReader;
		   }
	}
	
	private Runnable getRun () {
		return new Runnable () {

			@Override
			public void run() {
				LineNumberReader reader;
				try {
					reader = new LineNumberReader(new FileReader(SERVERS_FILE));
			        String line = null;
			        servers = new TreeSet <Server>();
			        while ((line = reader.readLine()) !=null) {
			            servers.add(new Server(line));
			        }
			        complete = true;
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
			
		};
		
	}

	public Set <Server> getServers () {			
		return servers; //Servers.getInstance().servers;
	}
	public boolean isDone () {
		return complete;
	}
}
