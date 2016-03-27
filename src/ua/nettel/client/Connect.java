package ua.nettel.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import ua.nettel.packet.Client;
import ua.nettel.packet.Command;
import ua.nettel.packet.Packet;
import ua.nettel.packet.User;

public class Connect implements Runnable {

	private static final int SERVIS_PERIOD = 2000;
	
	private boolean stoped = false;

	private Socket socket = null;

	private int port;
	private String host;

	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;

	private User user;

	@Override
	public void run() {

		try {
			while (this.isConnect()) {// while (!stoped) {
				Object newPacket = ois.readObject();
				if (null != newPacket && (newPacket instanceof Packet)) {
					Packet packet = (Packet) newPacket;
					//List <Data> data = packet.getData();
					switch (packet.getCommand().getValue()) {
					case Command.MESSAGE:
						Main.printMessage(packet);
						break;

					case Command.CONNECT_CLOSE:									
						Main.removeUser (packet);
						//this.stoped = true;
						//this.stop();
						break;
					case Command.ADD:												//add new User
					case Command.LIST_USER:											//Users list 
//						if ( null != data ) {// && data.get(0).getClass().equals(User.class)  ) 
//							Main.addUsers (data);
//						}
						Main.addUsers (packet);
						break;
						
					case Command.ERROR_SING_IN:				//обработка Ошибка авторизации
						//TODO
						break;
					
						
					default:
						break;
					}
					
//					if (tmp.getClass().equals(Message.class)) {
//						// System.out.println( ((Message) tmp).toString() );
//						Main.printMessage((Message) tmp);
//					}
//					if (tmp.getClass().equals(Command.class)) {
//						if (((Command) tmp).getValue() == Command.CONNECT_CLOSE) {
//							break;
//						}
//					
//					}
//					if (tmp.getClass().equals(User.class)){
//						Main.printMessage( (User)tmp);
//					}
				}

			}
		} catch (Exception e) {
			System.err.println(e);

		} finally {
			if (!this.stoped) {
				this.stop();
			}
		}

	}

	public void stop() {
		if (!stoped) {
			Packet packet = new Packet();
			packet.setCommand ( new Command (Command.CONNECT_CLOSE) );
			packet.setData (this.user);
			this.send (packet);
			
			this.stoped = true;
		}
		
		try {
			Thread.sleep(SERVIS_PERIOD);
		} catch (InterruptedException e) {
			
		}
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
			System.err.println(e);
			
		} finally {
			System.out.printf(Main.getLocaleText("connection.close"), host, port);
		}

	}

	public boolean send(Packet packet) {
		
		try {
			if (isConnect()) {
				
				oos.writeObject(packet);
				return true;
			}
		} catch (IOException e) {
			System.err.println(e);
			// return false;
		}
		return false;
	}

	public Connect (User user, Server server) {
		this.toConnect(user, server.getHost(), server.getPort());
	}
	@Deprecated
	public Connect(User user, String host, int port) {
		this.toConnect (user, host, port);
		
	}
	private void toConnect (User user, String host, int port) {
		this.host = host;
		this.port = port;
		this.user = user;
		socket = new Socket();
		System.out.printf(Main.getLocaleText("connection.start"));
		try {
			socket.connect(new InetSocketAddress(this.host, this.port));

			ois = new ObjectInputStream(this.socket.getInputStream());
			// System.out.println("ObjectInputStream  init");
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			// System.out.println("ObjectOutputStream  init");
			
			this.user.setIP(socket.getLocalAddress().getHostAddress());
			
			Packet packet = new Packet ();
			packet.setCommand(new Command(Command.ADD));
			Client client = new Client (this.user.getNickName(),this.user.getIP());
			packet.setData(client);
			this.send(packet);
			System.out.printf(Main.getLocaleText("connection.ok"), host, port);
		} catch (IOException e) {
			System.err.println(e);
			this.stop();
		}

	}

	// public Connect (User user, String host, int port, Method action) {
	// this(user, host, port);
	// if (isConnect()) {
	// action.invoke(obj, args);
	// }
	// }
	public boolean isConnect() {
		return (!stoped && null != socket && !socket.isClosed() && null != ois && null != oos);
	}

}
