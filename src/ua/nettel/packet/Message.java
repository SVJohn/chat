package ua.nettel.packet;

import java.io.Serializable;

public class Message extends Packet implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	
	
	private final String message;
	
	
	public Message(String nickname, String message) {
		super (nickname);
		this.message = message;
		
	}


	@Override
	public String toString (){
		return this.getDate().toString()+"::"+getNickname() + "::" +this.message;
	}

	public String getMassage () {
		return this.message;
	}

}
