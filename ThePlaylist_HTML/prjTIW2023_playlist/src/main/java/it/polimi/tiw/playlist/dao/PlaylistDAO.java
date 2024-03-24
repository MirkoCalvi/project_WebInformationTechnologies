package it.polimi.tiw.playlist.dao;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.playlist.beans.Playlist;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.beans.User;


public class PlaylistDAO  {
	
	private Connection con;

	public PlaylistDAO(Connection connection) {
		this.con = connection;
	}
	
	//checkers-----------------------------------------------------------------------------------------------------------------------------
	public boolean checkPlaylistPresence(String username, String plTitle) throws SQLException{
		
		String query="SELECT * "
					+ "FROM PLAYLISTS "
					+ "WHERE PlaylistUser=? AND PlaylistTitle=?";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		boolean res=false;
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, username);
			pstatement.setString(2, plTitle);
			
			result = pstatement.executeQuery();
			
			if (!result.isBeforeFirst()) // no results, check failed
				res= false;
			else {
				if(result.next()) {
					res= true;
				}else {
					res= false;
				}
			}
			
		} catch (SQLException e) {
			
			throw e;
	
		}finally {
			
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
		}
		
		return res;

	}

	
	public boolean checkSongPresenceInPlaylist(Song s, String PlaylistTitle) throws SQLException{
		
		String query="SELECT * "
					+ "FROM PLcontainsSONG "
					+ "WHERE PlUser=? AND SgID=? AND PlTitle=?";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		boolean res=false;
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, s.getUser());
			pstatement.setInt(2, s.getID());
			pstatement.setString(3, PlaylistTitle);
			
			result = pstatement.executeQuery();
			
			if (!result.isBeforeFirst()) // no results, check failed
				res=false;
			else {			
				res=true;		
			}
			
		} catch (SQLException e) {
			
			throw e;
	
		}finally {
			
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
		}
		
		return res;
	}
	
	
	
	//getters------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<Playlist> getPlaylistSortedByDateFromUsername(String username) throws SQLException{
		
		String query="SELECT PlaylistTitle, CreationDate "
					+ "FROM PLAYLISTS "
					+ "WHERE PlaylistUser=? "
					+ "ORDER BY CreationDate DESC ";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		 ArrayList<Playlist> pls;
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, username);
			
			result = pstatement.executeQuery();
			
			if (!result.isBeforeFirst()) // no results, credential check failed
				pls= null;
			else {
				
				 pls= new ArrayList<>();
				
				while(result.next()) {
					
					Playlist pl=new Playlist();
					pl.setTitle(result.getString("PlaylistTitle"));
					pl.setDate(result.getDate("CreationDate"));
					
					pls.add(pl);	
				}
			}
			
		} catch (SQLException e) {
			
			throw e;

		}finally {
			
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
		}
		
		return pls;
	}
	
	public int getTotNumbOfSong(String Username, String plTitle) throws SQLException {
		
		String query = "SELECT COUNT(*) AS tot "
					  + "FROM  PLcontainsSong JOIN SONGS ON "
									+ "PLcontainsSONG.PlTitle=? and "	
									+ "PLcontainsSONG.PlUser=SONGS.SongUser and "
									+ "PLcontainsSONG.PlUser=? and "
									+ "PLcontainsSONG.SgID=SONGS.SongID " ;
		
		PreparedStatement pstatement;
		ResultSet resultSet ;
		int res=-1;
	
			
		pstatement = con.prepareStatement(query);
	    
	    pstatement.setString(1, plTitle);
	    pstatement.setString(2, Username);
	    
	    resultSet = pstatement.executeQuery();
	    
	    if (resultSet.next()) {
	    	
	    	res=resultSet.getInt("tot");
	    	
	    }
	    
	    return res;
		
	}
	
	//insert methods-----------------------------------------------------------------------------------------------------------------------
	
	public void insertPlaylist(String title, String user, java.util.Date today) throws SQLException{
		
		String query="INSERT into PLAYLISTS (PlaylistTitle, PlaylistUser, CreationDate) VALUES(?, ?, ?)";
		
		PreparedStatement pstatement=null;
		con.setAutoCommit(false);
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, title);
			pstatement.setString(2, user);
			pstatement.setDate(3, new java.sql.Date(today.getTime()));
			pstatement.executeUpdate(); 
			
			con.commit();
			
		}catch (SQLException e) {
			
			// rollback if some exception occurs
			con.rollback();
			throw e;
			
		} finally {
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
			
			con.setAutoCommit(true);
		}	

	}
	
	public void insertSongInPlaylist(int SongsID, String user, String pl) throws SQLException{
		
		String query="INSERT into PLcontainsSONG (PlTitle, PlUser, SgID) VALUES(?, ?, ?)";
		
		PreparedStatement pstatement=null;
		
		con.setAutoCommit(false);
		
		try {
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, pl);
			pstatement.setString(2, user);
			pstatement.setInt(3, SongsID);
			
			pstatement.executeUpdate(); 
			
		}catch (SQLException e) {
			
			con.rollback();
			throw e;
			
		} finally {
			try {
				
				if (pstatement != null)
					pstatement.close();
				
			} catch (SQLException e1) {
				
				throw e1;
				
			}
			
			con.setAutoCommit(true);
		}

	}

}
