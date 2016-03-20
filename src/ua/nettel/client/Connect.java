package ua.nettel.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

import ua.nettel.packet.Command;
import ua.nettel.packet.Message;
import ua.nettel.packet.Packet;
import ua.nettel.packet.User;

public class Connect implements Runnable {

	private boolean stoped = false;
	
	private Socket socket = null;
	
	private int port;
	private String host;
	
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	@Override
	public void run() { 
		if (isConnect()) {
			try {
				while (!stoped) {
					Object tmp = ois.readObject();
					if (null != tmp && (tmp instanceof Packet)) {
						if (tmp.getClass().equals(Message.class)) {
							//System.out.println( ((Message) tmp).toString() );
							Main.printMessage ((Message)tmp);
						}
						if (tmp.getClass().equals(Command.class)) {
							if ( ( (Command) tmp ).getCommand() == Command.CONNECT_CLOSE ) {
								break;
							}
						
						}
					}
				}
			} catch (Exception e) {
				System.err.println(e);
			} finally {
				this.stop();
			}
		}
	}
	public void stop () {  
		this.stoped = true;
		try {
			if (null != socket) {
				socket.close();
			}
			if (null != ois) {
				ois.close();
			}
			if (null != oos) {
				oos.close();
			}
		} catch (IOException e) {
			System.err.println (e);
		}
		
	}
	
	public boolean send (Packet packet) {
		try {
			if (isConnect()) {
				oos.writeObject(packet);
				return true;
			} 	
		}catch (IOException e) {
			System.err.println(e);
			//return false;
		}
		return false;
	}
	
	public Connect (User user, String host, int port) {
		this.host = host;
		this.port = port;
		socket = new Socket ();
		try {
			socket.connect(new InetSocketAddress(this.host, this.port));
			
//			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
//			ois = new ObjectInputStream (bis);
//			BufferedOutputStream bos = new BufferedOutputStream (socket.getOutputStream());
//			oos = new ObjectOutputStream (bos); 
			
			
			ois = new ObjectInputStream (this.socket.getInputStream());  
			//System.out.println("ObjectInputStream  init");
			oos = new ObjectOutputStream (this.socket.getOutputStream());
			//System.out.println("ObjectOutputStream  init");
			
			this.send(user);
			System.out.printf("Connect %s:%d \n", host, port);
		} catch (IOException e) {
			System.err.println (e);
			this.stop();
		}
		
	}
//	public Connect (User user, String host, int port, Method action) {
//		this(user, host, port);
//		if (isConnect()) {
//		  action.invoke(obj, args);
//		}
//	}
	public boolean isConnect () {
		return (!stoped && null !=socket && !socket.isClosed() && null != ois && null !=oos);
	}

}
