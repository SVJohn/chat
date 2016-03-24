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
public class Servers {
	
	private static Servers serversReader = null;
	
	private static Set <String> servers;
	
	private static boolean loading = false;
	
	private static final String SERVERS_FILE = "servers";
	
	private Servers (){
		serversReader = this;
		new Thread(getRun()).start();
	}
	public static Servers getInstance (){
		if (null != serversReader) return serversReader;
		   else {
			   serversReader = new Servers(); 
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
			        servers = new TreeSet <String>();
			        while ((line = reader.readLine()) !=null) {
			            servers.add(line);
			        }
			        loading = true;
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
			
		};
		
	}

	public Set <String> getServers () {			
		return servers; //Servers.getInstance().servers;
	}
	public boolean isDone () {
		return loading;
	}
}
