package it.polimi.tiw.playlist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.polimi.tiw.playlist.beans.Album;
import it.polimi.tiw.playlist.beans.Song;

public class AlbumDAO {
	
	private Connection con;

	public AlbumDAO(Connection connection) {
		this.con = connection;
	}
	
	//getter ----------------------------------------------------------------------------------------------------
	public int getAlbumID(String User, Album album) throws SQLException {
		
		int res=-1;
		ResultSet result=null;
		PreparedStatement pstatement=null;
		String query="SELECT AlbumID "
					+"FROM ALBUMS JOIN SONGS ON ALBUMS.AlbumID = SONGS.Album "
				    +"WHERE SongUser=? AND AlbumTitle=? AND Artist=?";
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1,  User);
			pstatement.setString(2,  album.getTitle());
			pstatement.setString(3,  album.getArtist());
			
			result = pstatement.executeQuery();
			
			if (!result.isBeforeFirst()) // no results, db check failed
				res=-1;
			else {
				result.next();
				res=result.getInt("AlbumID");
			}
			
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
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		return res;
	}

	
	
	public Album getAlbum(int id) throws SQLException {
		
		Album res=new Album();
		ResultSet result=null;
		PreparedStatement pstatement=null;
		String query="SELECT * "
					+"FROM ALBUMS  "
				    +"WHERE AlbumID=? ";
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setInt(1,  id);
			
			result = pstatement.executeQuery();
			
			if (!result.isBeforeFirst()) // no results, db check failed
				res=null;
			else {
				if(result.next()) {
					res.setID(result.getInt("AlbumID"));
					res.setTitle(result.getString("AlbumTitle"));
					res.setArtist(result.getString("Artist"));
					res.setImgFileName(result.getString("Img"));
					res.setPubblicationYear(result.getInt("PublicationYear"));
				}else {
					res=null;
				}
			}
			
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
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		return res;
	}
	
	//insert ------------------------------------------------------------------------------------------------------
	
	public int insertAlbum(Album a) throws SQLException {
		
		PreparedStatement pstatement1=null;
		int res=-1;
		String query1="INSERT into ALBUMS (AlbumTitle, Artist, PublicationYear, Img ) VALUES(?, ?, ?, ?)";
				
		con.setAutoCommit(false);
	
		
		try {
			
			pstatement1 = con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
			
			pstatement1.setString(1, a.getTitle());
			pstatement1.setString(2, a.getArtist());
			pstatement1.setInt(3, a.getPublicationYear());
			pstatement1.setString(4, a.getImgFileName());
			
			int affectedRows=pstatement1.executeUpdate(); 
			
			// Check if the insert was successful
            if (affectedRows > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = pstatement1.getGeneratedKeys();
                if (generatedKeys.next()) {
                    // Get the generated key value
                    res = generatedKeys.getInt(1);
                    System.out.println("Generated ID: " + res);
                }
            }
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
		
		return res;
		
	}
	
	
	
	
}
