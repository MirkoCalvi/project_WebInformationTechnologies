package it.polimi.tiw.playlist.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.GetEncoding;

@WebServlet("/GetSongsInPlaylist")
@MultipartConfig
public class GetSongsInPlaylistServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetSongsInPlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	public void doGet(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{
		HttpSession session = request.getSession(true);
		PlaylistDAO playlistDAO = new PlaylistDAO(this.connection);
		String userName = (String)session.getAttribute("user");
		
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
		
		//take all the songs in the given playlist (they are already sorted)
		ArrayList<Song> songsInPlaylist = null;
		try {
			songsInPlaylist = playlistDAO.getSongTitleAndImg(playlistName,userName);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
		
		//prepare the json array that will be sent
		JsonArray jArray = new JsonArray();
		JsonObject jSonObject;

		for(Song song : songsInPlaylist) {
				jSonObject = new JsonObject();
				
				jSonObject.addProperty("id", song.getId());
				jSonObject.addProperty("title" , song.getTitle());
				try {
					jSonObject.addProperty("imageContent" , GetEncoding.getImageEncoding(userName + "_" + song.getAlbum().getFileImage() , getServletContext()));
				} catch(IOException e) {
					jSonObject.addProperty("imageContent" , "");
				}
				
				jArray.add(jSonObject);
		}
		
		String result = new GsonBuilder().create().toJson(jArray);
		
		response.setStatus(HttpServletResponse.SC_OK);//Code 200
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(result);
	}

	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
}