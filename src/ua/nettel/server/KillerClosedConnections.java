package ua.nettel.server;

import java.util.List;

public class KillerClosedConnections implements Runnable {
	
	private final static long PERIOD = 1000;					//time interval looping millisecond
	private boolean stopped = false;
	
	private List <Connection> connections;
	
	public KillerClosedConnections(List <Connection> connections){
		this.connections = connections;
		
	}
	
	public void stop () {
		this.stopped = true;
	}
	@Override
	public void run() {
		Server.printLog(Server.getLocaleText ("killer.start") );
		while (!stopped) {
			//for (Connection c:connections){
			//	if (null == c || c.isConnect()) {
			//  }
			//}
			for (int i=0; i<connections.size(); i++ ) { 
				Connection c = connections.get(i); 
				if (null == c || !c.isConnect()) {
					connections.remove(i);
					Server.printLog( Server.getLocaleText("killer.connectin.closed"), c.getNickname(), c.getIP(), connections.size());
				}
			}
			try {
				Thread.sleep(KillerClosedConnections.PERIOD);
			} catch (InterruptedException e) {
				//e.printStackTrace();
				Server.printLog(e);
			}
			
		}

	}

}
