package it.polimi.tiw.playlist.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.beans.User;
import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;

/**
 * Servlet implementation class CreatePlaylist
 */
@WebServlet("/CreatePlaylist")
public class CreatePlaylist extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	//private TemplateEngine templateEngine;
	private Connection connection = null;
   
	
    public CreatePlaylist() {
        super();
    }
    
    public void init() throws ServletException {
		
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("---doPost() create playlist:");
		
		HttpSession session = request.getSession();
		String PlTitle=null;
		String plError=null;
		String[] songsList=null;
		List<Integer> songsID=null;
		SongDAO sDAO= new SongDAO(connection);
		ArrayList<Song> songs=null;
		
		//getParameter
		try {
			
			PlTitle = request.getParameter("PlaylistTitle");
			songsList=request.getParameterValues("songList");
			
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			plError="Incorrect or missing param values";
			
		}
		
		System.out.println("Playlist Title:"+PlTitle);
		
		if(PlTitle==null) {
			plError=" missing param 'playlist title'";
		}else if(songsList==null || songsList.length<1) {
			plError=" select at least one song";
		}
		
		if(plError!=null) {
			
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?plError=" + plError;
			response.sendRedirect(path);
			return;
		}
		
		PlaylistDAO plDAO=new PlaylistDAO(connection);
		
		User user=(User)session.getAttribute("user");
		
		//adding a new playlist
		try {
			
			Date today = new Date();
			plDAO.insertPlaylist(PlTitle, user.getUserName(), today);
					
		}catch (SQLException e) {
			e.printStackTrace();
			plError= "Not possible to create playlist, database error";
		}
		
		if(plError!=null) {
			
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?plError=" + plError;
			response.sendRedirect(path);
			return;
		}
		

		//for each song into "songs" ill'get the ID and insert into PlContainsSong
		
		try {
			
			songs=sDAO.getSongsByUser(user.getUserName());
			
		} catch (SQLException e1) {
			
			plError="Not possible to recovers your songs";
			e1.printStackTrace();
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?=plError=" + plError;
			response.sendRedirect(path);
			return;
		}
	
		
		try {
			
			songsID= songs.stream().map(x->x.getID()).collect(Collectors.toList());
			
		} catch (NumberFormatException | NullPointerException e) {
			
			plError="Not possible to recover your songs from db";
			e.printStackTrace();
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?plError=" + plError;
			response.sendRedirect(path);
			return;
			
		}
		
		for( String s: songsList) {
				
			//check that the song s is inside songs
			if( songsID.contains( Integer.parseInt(s)) ) {
				
				try {
					
					System.out.println("	song:"+s);
					
					Song SongToAdd=sDAO.getSongFromID(Integer.parseInt(s));
					
					int songID=sDAO.getSongIDfromUser(user.getUserName(), SongToAdd);
					
					System.out.println("	id: "+Integer.parseInt(s));
					
					if(songID!=-1) plDAO.insertSongInPlaylist(songID, user.getUserName(), PlTitle);
					
				} catch (SQLException e) {
					if(plError== null) plError="Not possible to add "+ s +" to "+ PlTitle ;
					else plError=plError +", Not possible to add "+ s +" to "+ PlTitle;
				}		
			}	
		}
		
		// return the user to the homePage
		String ctxpath = getServletContext().getContextPath();
		String message =PlTitle+" added correctly!";
		String path = ctxpath + "/GoToHomePage?plError=" + plError + "&message=" + message;
		response.sendRedirect(path);
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToHomePage";
		response.sendRedirect(path);
	}
		
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
