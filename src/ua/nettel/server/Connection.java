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
	
	private String nickname;
	
	private Socket socket = null;
	private String IP;
	
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	private List <Connection> connect;
	
	
	
	public Connection (Socket socket, List <Connection> connect) {// throws IOException{
		//this.nickname = nickname;
		this.connect = connect;
		this.socket = socket;
		this.IP = socket.getRemoteSocketAddress().toString();
		
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
			
			Server.printLog (Server.getLocaleText("connection.new"), this.getIP (), Server.getCountConnections()+1); //+":" + port);
		} catch (IOException e) {
			Server.printLog(e);
			this.stop ();
		}
	}
	
	public String getNickname () {
		return this.nickname;
	}
	
	public String getIP () {
		return this.IP;
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
						if ( ( (Command) newPacket ).getCommand() == Command.CONNECT_CLOSE ) {
							//String nickname = ( (Command) newPacket).getNickname();
							Formatter message = new Formatter();
							message.format(Server.getLocaleText("user.exit"), this.getNickname () );
							this.send(new Message( Server.getLocaleText("server.name"), message.toString() ));
							message.close();
							break;
						}
					}
					if (newPacket.getClass().equals(User.class)) {
						this.nickname = ( (User) newPacket).getNickname();
						Formatter message = new Formatter();
						message.format(Server.getLocaleText("user.new"), this.getNickname () );
						this.send(new Message( Server.getLocaleText("server.name"), message.toString() ));
						message.close();
					}
				}
			}
		} catch (Exception e) {
			Server.printLog(e);
		} finally {
			stop ();
		}

	}

	private void stop () {					//добавить удаление connect из list
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
	
	private void send(Packet packet) {
		for (Connection c : connect) {

			if (null != c && c.isConnect ()) {
				c.sendMe(packet);
			}

		}
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
		return (null != socket && !socket.isClosed() && null!= ois && null != oos);
	}
	
}
