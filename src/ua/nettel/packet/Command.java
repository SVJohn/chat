package ua.nettel.packet;

import java.io.Serializable;

public class Command extends Packet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int CONNECT_CLOSE = 1;  // close connect with server
	
	private int command = 0;
	
	public Command (String nickname, int command) {
		super (nickname);
		this.command = command;
	}
	
	public int getCommand () {
		return this.command;
	}

}
