package it.polimi.tiw.playlist.beans;

public class Song {
	
	private int ID;
	private String user;
	private String title;
	private String audioFileName;
	private MusicGenre  genre;
	private Album album;
	
	public Song() {
		
	}
	
	//getters and setters-------------------------------------------------
	//ID --------
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID=id;
	}
	
	//title --------
	public String getTitle() {
		return title;
	}
	public void setTitle(String songTitle) {
		title=songTitle;
	}
	
	//user --------
	public String getUser() {
		return user;
	}
	public void setUser(String username) {
		user=username;
	}
	
	//audio ------
	public String getAudioFileName() {
		return audioFileName;
	}
	public void setAudioFileName(String audioFileName) {
		this.audioFileName=audioFileName;
	}
	
	//genre ------
	public MusicGenre getGenre() {
		return genre;
	}
	public void setGenre(MusicGenre songGenre) {
		genre=songGenre;
	}
	
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album alb) {
		album=alb;
	}
	
	
}
