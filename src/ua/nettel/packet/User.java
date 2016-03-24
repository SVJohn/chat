package ua.nettel.packet;

import java.io.Serializable;

public class User extends Packet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public static final int COMMAND_ADD = 1;
	public static final int COMMAND_DEL = 2;
	
	private int command = 0;
	
	public User(String nickname) {
		super(nickname);
		
	}
	public User (String nickname, int command) {
		super (nickname);
		this.setCommand(command);
	}

	public void setCommand (int command) {
		this.command = command;
	}
	public int getCommand () {
		return this.command;
	}
	
	@Override
	public String toString (){
		return this.getDate().toString()+ "::\t"+this.getNickname();
	}
}
