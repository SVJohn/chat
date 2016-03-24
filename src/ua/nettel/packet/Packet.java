package ua.nettel.packet;

import java.io.Serializable;
import java.util.Date;

public abstract class Packet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String nickname;
	private final Date date = new Date();
	
	public Packet (String nickname) {
		this.nickname = nickname;
	}
	
	public String getNickname () {
		return this.nickname;
	}

	public Date getDate () {
		return (Date) date.clone(); 
	}
	
	//private Object message;
	//abstract public Object getMessage ();
	//abstract public void setMessage (Object message);
}
