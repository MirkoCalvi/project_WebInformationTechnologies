package it.polimi.tiw.playlist.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import com.google.gson.JsonObject;

import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.GetEncoding;

@WebServlet("/PlaySong")
@MultipartConfig
public class PlaySongServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public PlaySongServlet() {
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
		SongDAO songDao = new SongDAO(this.connection);
		String userName = (String)session.getAttribute("user");
		String error = null;
		
		String Id = request.getParameter("songId");
			
		Integer songId = null;
		try {
			songId = Integer.parseInt(Id);
			
			if( !(songDao.belongTo(songId, userName))) {
				error = "Song not found";
			}

		}catch(NumberFormatException e) {
			error = "Song not found";
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
		
		//if an error occurred, it will be shown in the page
		if(error != null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			response.getWriter().println(error);
			return;
		}
		
		Song song = null;
		try {
			song = songDao.playSong(songId);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
			
		//prepare the json object that will be sent
		JsonObject jSonObject = new JsonObject();
		
		jSonObject.addProperty("singer" , song.getAlbum().getSinger());
		jSonObject.addProperty("albumTitle" , song.getAlbum().getTitle());
		jSonObject.addProperty("publicationYear" , song.getAlbum().getYear());
		jSonObject.addProperty("genre" , song.getGenre());
		
		try {
			jSonObject.addProperty("audioContent" , GetEncoding.getSongEncoding(userName + "_" + song.getFileAudio(), getServletContext()));
		}catch(IOException e) {
			jSonObject.addProperty("audioContent" , "");
		}
		
		String result = new GsonBuilder().create().toJson(jSonObject);
		
		response.setStatus(HttpServletResponse.SC_OK);//Code 200
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(result);		
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
}