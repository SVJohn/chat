package ua.nettel.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import ua.nettel.packet.Client;
import ua.nettel.packet.Command;
import ua.nettel.packet.Data;
import ua.nettel.packet.Packet;
import ua.nettel.packet.User;

public class Connection implements Runnable{
	
	private static final int SERVIS_PERIOD = 2000;
	
	private static final String KEY_CONNECTION_NEW = "connection.new";
	private static final String KEY_CONNECTION_CLOSE = "connection.close";
	private static final String KEY_SERVER_NAME = "server.name";
	
	private boolean stoped = false;
	
	private User user;
	
	private Socket socket = null;
	
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	private List <Connection> connect;
	
	public Connection (Socket socket, List <Connection> connect) {
		this.connect = connect;
		this.socket = socket;
			
		try {
			oos = new ObjectOutputStream (this.socket.getOutputStream());
			ois = new ObjectInputStream (this.socket.getInputStream());
		} catch (IOException e) {
			Server.printLog(e);
			this.stop ();
		}
	}
	
	public String getNickname () {
		//return this.nickname;
		return this.user.getNickName();
	}
	
	public String getIP () {
		//return this.IP;
		return this.user.getIP();
	}
	public User getUser () {
		return this.user;
	}
	
	@Override
	public void run() {
		try {
			while (this.isConnect()) {
				Object newPacket = ois.readObject();
				if (null != newPacket && newPacket instanceof Packet) {
					Packet packet = (Packet) newPacket;
					switch (packet.getCommand().getValue()) {
					case Command.MESSAGE:
						this.send (packet );
						break;

					case Command.CONNECT_CLOSE:
						//this.stoped = true;
						this.stop();
						break;
						
					case Command.ADD:System.out.println("2");
						List <Data> data = packet.getData();
						if (null != data && 
								null != data.get(0) && 
									data.get(0).getClass().equals(Client.class)) {
							
							Client client = (Client) data.get(0);
							// TODO авторизация 
							
							// временная затыча
							boolean isSingIn = true; 
							
							if (isSingIn) {
								this.user = new User(client.getNickName(), client.getIP());
//								this.user = new User(client.getNickName(), 
//													  socket.getInetAddress().getHostAddress() );
								Server.printLog (Server.getLocaleText(KEY_CONNECTION_NEW), this.getNickname(), this.getIP (), Server.getCountConnections());  //+":" + port);
								sendCommandUser(new Command(Command.ADD));
								sendListUsers ();
								break;
							} 
						}  
						System.out.println("4");
						// ошибка авторизации:
						Packet errorMessage = new Packet();
						errorMessage.setCommand(new Command (Command.ERROR_SING_IN) );
						//errorMessage.setData(this.user);
						sendMe(errorMessage);
						this.stoped = true;
						this.stop();
						break;
						
					default:
						break;
					}
//					if (newPacket.getClass().equals(Message.class)) {
//						this.send((Message)newPacket );
//					}
//					if (newPacket.getClass().equals(Command.class)) {
//						if ( ( (Command) newPacket ).getCommand() == Command.CONNECT_CLOSE ) { //System.out.println("CONNECT_CLOSSED");
//						
//							break;
//						}
//					}
//					if (newPacket.getClass().equals(User.class)) {
//						//this.nickname = ( (User) newPacket).getNickname();
//						
//						//sendServiceMassege(Server.getLocaleText("user.new"));
//						//sendUser (User.COMMAND_ADD);
//						this.user  = (User) newPacket;
//						this.user.setIP( socket.getInetAddress() ); 
//						
//						Server.printLog (Server.getLocaleText(KEY_CONNECTION_NEW), this.getNickname(), this.getIP (), Server.getCountConnections()); //+":" + port);
//						
//						this.user.setCommand(User.COMMAND_ADD);
//						sendUser();
//						sendListUsers ();
//					}
				}
			}
		} catch (Exception e) {
			Server.printLog(e);
		} finally {
			if (!this.stoped) {
				this.stop();
			}
		}

	}

	private void stop () {
		if ( !this.stoped ) {
			this.sendCommandUser(new Command(Command.CONNECT_CLOSE));
			this.stoped = true;
		}
		
		
		try {
			Thread.sleep(SERVIS_PERIOD);
		} catch (InterruptedException e) {		}
		
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
			Server.printLog(e);
		}
		Server.printLog(Server.getLocaleText (KEY_CONNECTION_CLOSE), this.getNickname(), this.getIP(),Server.getCountConnections() );
	}
	
	//переделать так, чтобы отправка производиласт в новом потоке (если этого не сделать, при большом количестве 
	//пользователей поток Connection будет долго отправлять сообщения и будеть заблокорован для новых сообщений)
	
	private void send(Packet packet) {					
		for (Connection c : connect) {

			if (null != c && c.isConnect ()) {
				c.sendMe(packet);
			}

		}
	}
	
	private void sendCommandUser (Command command) {
		Packet packet = new Packet();
		//packet.setCommand ( new Command (Command.ADD) );
		packet.setCommand ( command );
		packet.setData (this.user);
		this.send (packet);
    }
	
	
	private void sendListUsers () {
		List <Data> users = new LinkedList<>();
		for (Connection c: connect) {
			if (null != c && c.isConnect () && this != c) {
				users.add(c.getUser());
			}
			
		}
		Packet packet = new Packet();
		packet.setCommand ( new Command (Command.LIST_USER) );
		packet.setListData (users);
		this.sendMe (packet);
	}
	

	
	public void sendMe (Packet packet) {
		try {
			this.oos.writeObject(packet);
		} catch (IOException e) {
			Server.printLog(e);

		}
	}
	
	public boolean isOutputStrim () {
		return (null != oos );
	}
	public boolean isConnect () {
		return (!stoped && null != socket && !socket.isClosed() && null!= ois && null != oos);
	}
	
}
