package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;

@WebServlet("/EditPlaylist")
@MultipartConfig
public class EditPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public EditPlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//method that adds the selected song to the playlist
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		SongDAO songDAO = new SongDAO(this.connection);
		PlaylistDAO playlistDAO = new PlaylistDAO(this.connection);
		String userName = (String)session.getAttribute("user");
		String error = null;
		
		//checking whether playlistName parameter is valid or not
		String playlistName = request.getParameter("playlistName");

		try {
			if( !(playlistDAO.belongTo(playlistName,userName)) ) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
				response.getWriter().println("Playlist not found");
				return;
			}
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
		
		//checking whether the selected song is valid or not
		int songId = -1;
		
		try {
			String song = request.getParameter("song");
			if(song != null) {
				Integer tempId = Integer.parseInt(song);
				if(songDAO.belongTo(tempId, userName)){ 
					//check that the song is not already in the playlist 
					if(!(playlistDAO.getSongTitleAndImg(playlistName, userName).stream().map(x -> x.getId()).filter(x -> x == tempId).findFirst().isPresent()) ) {
						songId = tempId;
					}
					else{
						error = "Song already in the playlist";
					}
				}
				else error = "Song not found";
			}
			else error = "No song selected";
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
		catch(NumberFormatException e1) {
			error = "Something went wrong";
		}
		
		//if an error occurred, it will be shown in the page
		if(error != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			response.getWriter().println(error);
			return;
		}
		
		//Updating the database
		try {
			if(!playlistDAO.addSongToPlaylist(playlistName, userName, songId)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
				response.getWriter().println("Database error: Unable to add this song");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);//Code 200
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
