package ua.nettel.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import ua.nettel.packet.Command;
import ua.nettel.packet.Message;
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
				Object tmp = ois.readObject();
				if (null != tmp && (tmp instanceof Packet)) {
					if (tmp.getClass().equals(Message.class)) {
						// System.out.println( ((Message) tmp).toString() );
						Main.printMessage((Message) tmp);
					}
					if (tmp.getClass().equals(Command.class)) {
						if (((Command) tmp).getCommand() == Command.CONNECT_CLOSE) {
							break;
						}

					}
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
		this.send(new Command(user.getNickname(), Command.CONNECT_CLOSE));
		this.stoped = true;
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

	public Connect(User user, String host, int port) {
		this.host = host;
		this.port = port;
		this.user = user;
		socket = new Socket();
		System.out.printf(Main.getLocaleText("connection.start"));
		try {
			socket.connect(new InetSocketAddress(this.host, this.port));

			// BufferedInputStream bis = new
			// BufferedInputStream(socket.getInputStream());
			// ois = new ObjectInputStream (bis);
			// BufferedOutputStream bos = new BufferedOutputStream
			// (socket.getOutputStream());
			// oos = new ObjectOutputStream (bos);

			ois = new ObjectInputStream(this.socket.getInputStream());
			// System.out.println("ObjectInputStream  init");
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			// System.out.println("ObjectOutputStream  init");

			this.send(this.user);
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
