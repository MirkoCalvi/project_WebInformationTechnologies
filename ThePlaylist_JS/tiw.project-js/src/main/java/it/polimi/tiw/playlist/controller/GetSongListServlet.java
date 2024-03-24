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

import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;

@WebServlet("/GetSongList")
@MultipartConfig
public class GetSongListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public GetSongListServlet() {
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
		String userName = (String)session.getAttribute("user");
		ArrayList<Song> songs = null;
				
		try {
			songs = new SongDAO(this.connection).getSongsbyUser(userName);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
		
		//Create the jSon with the answer
		String jSon = new GsonBuilder().create().toJson(songs);
		
		response.setStatus(HttpServletResponse.SC_OK);//Code 200		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jSon);
	}	
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}