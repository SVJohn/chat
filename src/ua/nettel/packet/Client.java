package ua.nettel.packet;

public class Client extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String password;			//найти безопасный метод хранения пароля
	
	public Client(String nickname) {
		super(nickname);
	}
	public  Client  (String nickname, String IP) {
		super(nickname, IP);
	}

	public void setPassword (String pass) {
		this.password = pass;
	}
	
	protected String getPassword() {
		return this.password;
	}
	@Override
	public boolean  equals (Object other) {
		if (!super.equals(other)) return false;
		Client otherClient = (Client) other;
		return password.equals(otherClient.getPassword());
	}
	
}
