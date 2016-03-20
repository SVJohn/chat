package ua.nettel.packet;

import java.io.Serializable;

public class User extends Packet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User(String nickname) {
		super(nickname);
		
	}

	
}
