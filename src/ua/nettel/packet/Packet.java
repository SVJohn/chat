package ua.nettel.packet;

import java.io.Serializable;

public abstract class Packet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String nickname;
	
	
	public Packet (String nickname) {
		this.nickname = nickname;
	}
	
	public String getNickname () {
		return this.nickname;
	}
	
	//private Object message;
	//abstract public Object getMessage ();
	//abstract public void setMessage (Object message);
}
