package ua.nettel.packet;

import java.io.Serializable;

public class Command implements Serializable{
	

	private static final long serialVersionUID = 2L;

	public static final int CONNECT_CLOSE = 100;  					// close connect with server
	public static final int ADD = 101;								// add new user (new connect) / sing in
		
	public static final int LIST_USER = 200;						// send list users
	public static final int MESSAGE = 201;							// send Message
	
	public static final int ERROR_SING_IN = 300;					//  error sing in
	
	
	
	private int command = 0;
	
	public Command (int command) {
		this.command = command;
	}
	
	public int getValue () {
		return this.command;
	}

}
