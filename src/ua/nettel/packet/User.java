package ua.nettel.packet;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class User implements Serializable, 								//Comparable<User>,
								Data
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private String nickName = null;
	private String IP = null;
		
	public User(String nickname) {
		this.setNickName(nickname);
		
	}
	public User (String nickname, String IP) {
		this.setNickName(nickname);
		this.setIP (IP);
	}

	public void setNickName (String nickname) {
		this.nickName = nickname;
	}
	
	public void setIP (String IP) {
		this.IP = IP;
	}
	
	public void setIP (InetAddress address) {
		this.IP = address.getHostAddress();
	}
	
	public String getIP (){
		return this.IP;
	}
	
	public String getNickName () {
		return nickName;
	}
	
	@Override
	public String toString (){
		return String.format("%1$s (%2$s)" ,this.getNickName(), this.getIP());
	}
	
	@Override
	public boolean equals (Object other) {
	 	if (this == other) return true;
	 	if (null == other || getClass() != other.getClass()) return false;
	 	User otherUser = (User) other;
	 	return getNickName().equals(otherUser.getNickName()) 
	 			&& getIP() ==otherUser.getIP();
	}
	@Override
	public int hashCode (){
		return Objects.hash(getNickName(),getIP());
	}
	
//	@Override
//	public int compareTo(User other) {
//		return toString().compareTo(other.toString());
//	}
	
}
