package ua.nettel.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Formatter;
import java.util.List;

import ua.nettel.packet.Command;
import ua.nettel.packet.Message;
import ua.nettel.packet.Packet;
import ua.nettel.packet.User;

public class Connection implements Runnable{
	
	private static final int SERVIS_PERIOD = 2000;
	
	private boolean stoped = false;
	
	private User user;
	//private String nickname;				// запокавать в объект User
	//private String IP;						//
	
	private Socket socket = null;
	
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	private List <Connection> connect;
	
	
	
	public Connection (Socket socket, List <Connection> connect) {// throws IOException{
		//this.nickname = nickname;
		this.connect = connect;
		this.socket = socket;
		//this.IP = socket.getRemoteSocketAddress().toString();
		
		try {
//			BufferedInputStream bis = new BufferedInputStream(
//					socket.getInputStream());
//			ois = new ObjectInputStream(bis);
//
//			BufferedOutputStream bos = new BufferedOutputStream(
//					socket.getOutputStream());
//			oos = new ObjectOutputStream(bos);
			
			oos = new ObjectOutputStream (this.socket.getOutputStream());
			ois = new ObjectInputStream (this.socket.getInputStream());
			
			//Server.printLog (Server.getLocaleText("connection.new"), this.getIP (), Server.getCountConnections()+1); //+":" + port);
		} catch (IOException e) {
			Server.printLog(e);
			this.stop ();
		}
	}
	
	public String getNickname () {
		//return this.nickname;
		return this.user.getNickname();
	}
	
	public String getIP () {
		//return this.IP;
		return this.user.getIP();
	}
	
	@Override
	public void run() {
		try {
			while (this.isConnect()) {
				Object newPacket = ois.readObject();
				if (null != newPacket && newPacket instanceof Packet) {
					if (newPacket.getClass().equals(Message.class)) {
						this.send((Message)newPacket );
					}
					if (newPacket.getClass().equals(Command.class)) {
						if ( ( (Command) newPacket ).getCommand() == Command.CONNECT_CLOSE ) { //System.out.println("CONNECT_CLOSSED");
						
							break;
						}
					}
					if (newPacket.getClass().equals(User.class)) {
						//this.nickname = ( (User) newPacket).getNickname();
						
						//sendServiceMassege(Server.getLocaleText("user.new"));
						//sendUser (User.COMMAND_ADD);
						this.user  = (User) newPacket;
						this.user.setIP( socket.getInetAddress() ); 
						
						Server.printLog (Server.getLocaleText("connection.new"), this.getNickname(), this.getIP (), Server.getCountConnections()); //+":" + port);
						
						this.user.setCommand(User.COMMAND_ADD);
						sendUser();
						sendListUsers ();
						
					}
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
		//sendUser (User.COMMAND_DEL);
		this.user.setCommand (User.COMMAND_DEL);
		sendUser ();
		//sendServiceMassege(Server.getLocaleText("user.exit"));
		this.send(new Command(Server.getLocaleText("server.name"), Command.CONNECT_CLOSE));
		
		this.stoped = true;
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
		Server.printLog(Server.getLocaleText ("connection.close"), this.getNickname(), this.getIP(),Server.getCountConnections() );
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
	
	private void sendUser () {
		this.send(this.user);
    }
	
	@Deprecated
	private void sendUser (int command){							
		this.send(
				new User( this.getNickname(), command) );
		
	}
	
	private void sendListUsers () {
		for (Connection c: connect) {
			if (null != c && c.isConnect () && this != c) {
				User oldUser = new User (c.getNickname(), c.getIP(), User.COMMAND_ADD_OLD);
				
				this.sendMe (oldUser);
				
			}
		}
	}
	
	@Deprecated
	public void sendServiceMassege (String format) {
		Formatter message = new Formatter();
		message.format(format, this.getNickname () );
		this.send(new Message( Server.getLocaleText("server.name"), message.toString() ));
		message.close();
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
