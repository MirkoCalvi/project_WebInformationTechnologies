package it.polimi.tiw.playlist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.playlist.beans.Album;
import it.polimi.tiw.playlist.beans.MusicGenre;
import it.polimi.tiw.playlist.beans.Song;


public class SongDAO {

	private Connection con;
	private static final int maxLimit=5;

	public SongDAO(Connection connection) {
		this.con = connection;
	}
	
	//checkers----------------------------------------------------------------------------------------------------------------------------------
	public boolean checkSongPresence(Song s) throws SQLException {
		
		String query="SELECT * "
				   + "FROM SONGS "
				   + "JOIN ALBUMS on SONGS.Album=ALBUMS.AlbumID "
				   + "WHERE SongUser=? AND SongTitle=? AND AlbumTitle=? AND Artist=? ";

		PreparedStatement pstatement =null;
		ResultSet result = null;
		Boolean flag;
		
		try {
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1,  s.getUser());
			pstatement.setString(2,  s.getTitle());
			pstatement.setString(3,  s.getAlbum().getTitle());
			pstatement.setString(4,  s.getAlbum().getArtist());
			
			result = pstatement.executeQuery();
			
			if (result.next())
				flag= true;
			else {
				flag= false;
			}	
		}catch (SQLException e) {
			throw e;

		} finally {
			
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		return flag;
	}
	
	//getter methods ----------------------------------------------------------------------------------------------------------------------------
	
public int getSongIDfromUser(String User, Song s) throws SQLException {
		
		String query="SELECT SongID "
					+ "FROM SONGS "
					+ "JOIN ALBUMS ON SONGS.Album=ALBUMS.AlbumID "
				    + "WHERE SongUser=? AND SongTitle=? AND AlbumTitle=? AND Artist=?";
		
		PreparedStatement pstatement=null;
		int res=-1;
		ResultSet result =null;

		try {
			
			pstatement = con.prepareStatement(query);
			pstatement.setString(1,  User);
			pstatement.setString(2,  s.getTitle());
			pstatement.setString(3,  s.getAlbum().getTitle());
			pstatement.setString(4,  s.getAlbum().getArtist());
			
			result = pstatement.executeQuery(); 
			
			if (result.isBeforeFirst()){
				while(result.next()) {
					res = result.getInt("SongID");
				}
			}else {
				res=-1;
			}
	
			
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {		
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		return res;
		
	}
	
	public Song getSongFromID(int ID) throws SQLException {
		
		String query="SELECT * "
					+ "FROM SONGS JOIN ALBUMS on SONGS.Album=ALBUMS.AlbumID "
				    + "WHERE SongID=? ";
		
		PreparedStatement pstatement=null;
		Song res=null;
		ResultSet result =null;

		try {
			
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1,  ID);
		
			result = pstatement.executeQuery(); 
			
			if (result.isBeforeFirst()){
				if(result.next()) {
					res=new Song();
					res.setID(result.getInt("SongID"));
					res.setAudioFileName(result.getString("AudioFile"));
					res.setGenre(MusicGenre.getGenre(result.getString("Genre")));
					res.setTitle(result.getString("SongTitle"));
					res.setUser(result.getString("SongUser"));
					
					Album a=new Album();
					a.setID(result.getInt("AlbumID"));
					a.setTitle(result.getString("AlbumTitle"));
					a.setArtist(result.getString("Artist"));
					a.setPubblicationYear(result.getInt("PublicationYear"));
					a.setImgFileName(result.getString("Img"));
					
					res.setAlbum(a);
					
				}
			}else {
				res=null;
			}
	
			
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {		
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		return res;
		
	}

	public ArrayList<Song> getSongsByUser(String username) throws SQLException{
		
		String query= "SELECT * "
					+ "FROM SONGS JOIN ALBUMS on AlbumID=Album "
					+ "WHERE SongUser=?";
		
		PreparedStatement pstatement=null;
		ResultSet result =null;
		ArrayList<Song> songs=null;
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, username);
			
			result = pstatement.executeQuery();
			
			if (result.isBeforeFirst()){
				
				songs=new ArrayList<>();
				
				while(result.next()) {
					Song s=new Song();
					Album a=new Album();
					
					s.setID(result.getInt("SongID"));
					s.setUser(result.getString("SongUser"));
					s.setGenre(MusicGenre.getGenre(result.getString("Genre")));
					s.setTitle(result.getString("SongTitle"));
					s.setAudioFileName(result.getString("AudioFile"));
					
					
					a.setID(result.getInt("AlbumID"));
					a.setTitle(result.getString("AlbumTitle"));
					a.setArtist(result.getString("Artist"));
					a.setPubblicationYear(result.getInt("PublicationYear"));
					a.setImgFileName(result.getString("Img"));
					
					s.setAlbum(a);
				
					songs.add(s);
				}
				
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {		
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		return songs;
		
	}
	
	public ArrayList<Song> getSongsByPlaylistAndUsername(String pl, String username, int NumbOfQuintuple) throws SQLException{
		
		PreparedStatement pstatement=null;
		ResultSet result=null;
		ArrayList<Song> songs=null;
		
		String query= "SELECT * "
					+ "FROM SONGS "
					+ "JOIN ALBUMS ON SONGS.Album = ALBUMS.AlbumID "
					+ "JOIN PLcontainsSong ON SONGS.SongID = PLcontainsSONG.SgID "
					+ "WHERE PLcontainsSONG.PlTitle=? AND PLcontainsSONG.PlUser=? "
					+ "LIMIT ? OFFSET ? ";
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, pl);
			pstatement.setString(2, username);
			pstatement.setInt(3, maxLimit);
			pstatement.setInt(4, maxLimit*NumbOfQuintuple);
			
			
			result = pstatement.executeQuery();
			
			if (result.isBeforeFirst()) {
				
				songs=new ArrayList<>();
				
				while(result.next()) {
					
					Song s=new Song();
					Album a=new Album();
					
					s.setID(result.getInt("SongID"));
					s.setUser(result.getString("SongUser"));
					s.setGenre(MusicGenre.getGenre(result.getString("Genre")));
					s.setTitle(result.getString("SongTitle"));
					s.setAudioFileName(result.getString("AudioFile"));
					
					
					a.setID(result.getInt("AlbumID"));
					a.setTitle(result.getString("AlbumTitle"));
					a.setArtist(result.getString("Artist"));
					a.setPubblicationYear(result.getInt("PublicationYear"));
					a.setImgFileName(result.getString("Img"));
					
					s.setAlbum(a);
				
					songs.add(s);
				}
			}
			
		}catch (SQLException e) {
			throw e;
		} finally {
			
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			
			} catch (SQLException e2) {
				throw e2;
			}
		
		}
		
		return songs;
		
	}
	
	public ArrayList<Song> getAllSongsInPlaylist(String pl, String username) throws SQLException{
		
		PreparedStatement pstatement=null;
		ResultSet result=null;
		ArrayList<Song> songs=null;
		
		String query= "SELECT * "
					+ "FROM SONGS "
					+ "JOIN ALBUMS ON SONGS.Album = ALBUMS.AlbumID "
					+ "JOIN PLcontainsSong ON SONGS.SongID = PLcontainsSONG.SgID "
					+ "WHERE PLcontainsSONG.PlTitle=? AND PLcontainsSONG.PlUser=? ";
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, pl);
			pstatement.setString(2, username);
			
			result = pstatement.executeQuery();
			
			if (result.isBeforeFirst()) {
				
				songs=new ArrayList<>();
				
				while(result.next()) {
					
					Song s=new Song();
					Album a=new Album();
					
					s.setID(result.getInt("SongID"));
					s.setUser(result.getString("SongUser"));
					s.setGenre(MusicGenre.getGenre(result.getString("Genre")));
					s.setTitle(result.getString("SongTitle"));
					s.setAudioFileName(result.getString("AudioFile"));
					
					
					a.setID(result.getInt("AlbumID"));
					a.setTitle(result.getString("AlbumTitle"));
					a.setArtist(result.getString("Artist"));
					a.setPubblicationYear(result.getInt("PublicationYear"));
					a.setImgFileName(result.getString("Img"));
					
					s.setAlbum(a);
				
					songs.add(s);
				}
			}
			
		}catch (SQLException e) {
			throw e;
		} finally {
			
			try {
				if (result != null) result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null) pstatement.close();
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		return songs;
		
	}
	
	//insertion methods----------------------------------------------------------------------------------------------------------------------------
	
	public void insertSong(Song s) throws SQLException {
		
		PreparedStatement pstatement=null;
		
		String query="INSERT into SONGS ( SongUser, SongTitle, AudioFile, Genre, Album) VALUES(?, ?, ?, ?, ?)";
		
		con.setAutoCommit(false);
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, s.getUser());
			pstatement.setString(2, s.getTitle());
			pstatement.setString(3, s.getAudioFileName());
			pstatement.setString(4, s.getGenre().toString());
			pstatement.setInt(5, s.getAlbum().getID());
			
			pstatement.executeUpdate(); 
			
			// commit if everything is ok
			con.commit();
		
		}catch (SQLException e) {
			con.rollback();
			e.printStackTrace();
			throw e;
			
		} finally {
			
			try {
				pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
			con.setAutoCommit(true);
		}
		
	}
	
	public void insertSong(Song s, Album a) throws SQLException {
		
		PreparedStatement pstatement1=null;
		
		String query1="INSERT into SONGS ( SongUser, SongTitle, AudioFile, Genre, Album) VALUES(?, ?, ?, ?, ?)";
		
		AlbumDAO albDAO=new AlbumDAO(con);
		
		con.setAutoCommit(false);
		
		try {
			
			//check if the album already exists
			int AlbumID=albDAO.getAlbumID(s.getUser(), a);
			
			//adding a new album to ALBUMS
			if(AlbumID==-1) {
				AlbumID=albDAO.insertAlbum(a);
			}
			
			System.out.println("insertSong: "+s.getTitle()+"album title:"+ a.getTitle()+"AlbumID:"+AlbumID);
			con.setAutoCommit(false);
			
			pstatement1 = con.prepareStatement(query1);
			
			pstatement1.setString(1, s.getUser());
			pstatement1.setString(2, s.getTitle());
			pstatement1.setString(3, s.getAudioFileName());
			pstatement1.setString(4, s.getGenre().toString());
			pstatement1.setInt(5, AlbumID);
			
			pstatement1.executeUpdate(); 
			
			con.commit();
			
			
		}catch (SQLException e) {
			con.rollback();
			e.printStackTrace();
			throw e;
			
		} finally {
			
			try {
				pstatement1.close();
			} catch (SQLException e1) {
				throw e1;
			}
			con.setAutoCommit(true);
		}
		
	}	
	
}
