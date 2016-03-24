package ua.nettel.client;

import java.util.Objects;

public class Server implements Comparable<Server>{
	
	private static final String SEPARATOR = ":";
	private static final String FORMAT = "%s%s%d";
	
	private String host;
	private int port;
	
	public Server (String host){
		String[] server = host.split(SEPARATOR);
		this.setData (server[0],Integer.parseInt(server[1]));
	}
	public Server (String host, int port) {
		this.setData(host, port);
	}
	
	private void setData (String host, int port) {
		this.host = host;
		this.port = port;
	}
	public String getHost () {
		return this.host;
	}
	public int getPort () {
		return this.port;
	}
	
	
	@Override
	public String toString () {
		return String.format(FORMAT, host, SEPARATOR, port);
	}

	@Override
	public int hashCode (){
		return Objects.hash(host,port);
	}
	
	@Override
	public boolean equals (Object otherObject) {
	 	if (this == otherObject) return true;
	 	if (null == otherObject) return false;
	 	if (getClass() != otherObject.getClass()) return false;
	 	Server other = (Server) otherObject;
	 	return host.equals(other.getHost()) && port ==other.getPort();
	}
	
	@Override
	public int compareTo(Server other) {
		return this.toString().compareTo(other.toString());
	}
}
