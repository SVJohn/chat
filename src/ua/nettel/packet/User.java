package ua.nettel.packet;

import java.io.Serializable;
import java.net.InetAddress;

public class User extends Packet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public static final int COMMAND_ADD = 1;						// add new user (new connect)
	public static final int COMMAND_DEL = 2;						// delete user (disconnect)
	public static final int COMMAND_ADD_OLD = 3;					// add user in old users list (update list) 
	
	private String IP = null;
	private int command = 0;
	
	public User(String nickname) {
		super(nickname);
		
	}
	public User (String nickname, int command) {
		super (nickname);
		this.setCommand(command);
	}

	public User (String nickname, String IP, int command) {
		super (nickname);
		this.setIP(IP);
		this.setCommand(command);
	}
	
	public void setCommand (int command) {
		this.command = command;
	}
	
	public void setIP (String IP) {
		this.IP = IP;
	}
	public void setIP (InetAddress address) {
		this.IP = address.getHostAddress();
	}
	
	public int getCommand () {
		return this.command;
	}
	
	public String getIP (){
		return this.IP;
	}
	
	@Override
	public String toString (){
		return String.format("%1$s (%2$s)" ,this.getNickname(), this.getIP());
	}
}
