package ua.nettel.packet;

import java.io.Serializable;

public class Message implements Serializable,
										Data
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private String message;
	
	public Message () {
		
	}
	public Message (String message) {
		this.setMessage(message);
		
	}

	public void setMessage (String message) {
		this.message = message;
	}


	public String getMassage () {
		return this.message;
	}
	
	@Override
	public String toString (){
		return this.getMassage();
	}


}
