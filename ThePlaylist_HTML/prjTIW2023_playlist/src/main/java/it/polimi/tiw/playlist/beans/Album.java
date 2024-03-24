package it.polimi.tiw.playlist.beans;

public class Album {
	
	private int ID;
	private String title;
	private String artist;
	private int publicationYear;
	private String imgFileName;
	
	public Album() {
		
	}
	
	
	//getters and setters-------------------------------------------------
	//ID -------
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		 ID= id;
	}
	
	//title -----
	public String getTitle() {
		return title;
	}
	public void setTitle(String albumTitle) {
		 title=albumTitle;
	}
	
	//artist -----
	public String getArtist() {
		return artist;
	}
	public void setArtist(String albumArtist) {
		 artist=albumArtist;
	}
	
	//imgPath -----
	
	public String getImgFileName() {
		return imgFileName;
	}
	public void setImgFileName(String imgFileName) {
		this.imgFileName=imgFileName;
	}
	
	//publication year ----
	public int getPublicationYear() {
		return publicationYear;
	}
	public void setPubblicationYear(int year) {
		publicationYear=year;
	}
	
	
}
