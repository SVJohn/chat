package ua.nettel.packet;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Packet implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	private final Date time;
	private Command command;
	private List <Data> data = new LinkedList<>();
	
	public Packet () {
		this.time = new Date();
	}
	
	public void setCommand (Command command) {
		this.command = command;
	}
	
	
	public void setData (Data data) {
		this.data.clear();
		this.addData( data );
	}
		
	public void setListData (List <Data> data) {
		this.data = data;
	}
	public void addData (Data data) {
		this.data.add(data);
	}
	
	public Command getCommand () {
		return this.command;
	}
	
	public List <Data> getData () {
		return this.data;
	}
	
	public Date getTime () {
		return (Date) this.time.clone();
	}
	
	
	//private Object message;
	//abstract public Object getMessage ();
	//abstract public void setMessage (Object message);
}
