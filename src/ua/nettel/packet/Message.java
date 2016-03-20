package ua.nettel.packet;

import java.io.Serializable;
import java.util.Date;

public class Message extends Packet implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Date date = new Date();
	
	private final String message;
	
	
	public Message(String nickname, String message) {
		super (nickname);
		this.message = message;
		
	}


	@Override
	public String toString (){
		return date.toString()+"::"+getNickname() + "::" +this.message;
	}

	public Date getDate () {
		return (Date) date.clone(); 
	}
	public String getMassage () {
		return this.message;
	}

}
