package it.polimi.tiw.playlist.beans;

public class User {

	private String userName;
	
	public User(){
		
	}
	
	public User(String userName) {
		this.userName=userName;
	}
	
	
	//setters-------------------------------------------------
	
	public void setUser(String user) {
		userName=user;
	}
	
	
	//getters-------------------------------------------------
	
	public String getUserName() {
		return userName;
	}
	
	
}
