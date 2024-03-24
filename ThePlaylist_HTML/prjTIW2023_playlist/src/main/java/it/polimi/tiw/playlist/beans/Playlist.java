package it.polimi.tiw.playlist.beans;

import java.sql.Date;

public class Playlist {
	
	private String playlistTitle;
	private Date date;
	private String playlistUser;
	
	
	public Playlist(){
		
	}
	
	public Playlist(String pt, String pu, Date d) {
		playlistTitle=pt;
		playlistUser=pu;
		date=d;
	}
	
	//setters-------------------------------------------------
	
	public void setTitle(String title) {
		playlistTitle=title;
	}
	
	public void setUser(String user) {
		playlistUser=user;
	}
	
	public void setDate(Date d) {
		date=d;
	}
	
	//getters-------------------------------------------------
	
	public String getTitle() {
		return playlistTitle;
	}
	
	public String getUser() {
		return playlistUser;
	}
	
	public Date getDate() {
		return date;
	}
	
}
