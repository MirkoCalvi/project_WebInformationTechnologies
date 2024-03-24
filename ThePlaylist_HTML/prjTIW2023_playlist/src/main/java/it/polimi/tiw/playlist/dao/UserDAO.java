package it.polimi.tiw.playlist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.playlist.beans.User;

public class UserDAO {

	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkCredentials(String USERNAME, String PW) throws SQLException{
		
		String query="SELECT * FROM USERS WHERE UserName=? and Pw=?";
		PreparedStatement pstatement=null;
		ResultSet result=null;
		User user =null;
		
		try {
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, USERNAME);
			pstatement.setString(2, PW);
			
			result = pstatement.executeQuery();
			
			if (!result.isBeforeFirst()) // no results, credential check failed
				user= null;
			else {
				
				result.next();
				user = new User(result.getString("UserName") );
				
			}
			
		} catch (SQLException e) {
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
		
		return user;
		
	}
	
	public void insertUser(String username, String pw) throws SQLException{
		
		String query="INSERT into USERS (UserName, Pw) VALUES(?, ?)";
		PreparedStatement pstatement = null;
		
		try{
			
			pstatement = con.prepareStatement(query);
			
			pstatement.setString(1, username);
			pstatement.setString(2, pw);
			
			pstatement.executeUpdate();
			
		}catch (SQLException e) {
			throw e;
		} finally {
			
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e2) {
				throw e2;
			}
		}
	}
	
	
	
}
