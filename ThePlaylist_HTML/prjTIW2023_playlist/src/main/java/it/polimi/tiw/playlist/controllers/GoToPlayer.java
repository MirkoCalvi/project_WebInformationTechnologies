package it.polimi.tiw.playlist.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.beans.User;
import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.FileHandler;
import it.polimi.tiw.playlist.utils.directoryHandler;

/**
 * Servlet implementation class GoToPlayer
 */
@WebServlet("/GoToPlayerPage")
public class GoToPlayer extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;   
      
    
    public GoToPlayer() {
        super();
    }

    public void init() throws ServletException {
		
    	ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		System.out.println("---doGet() GoToPlayerPage:");
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		//dao------------
		SongDAO sDAO= new SongDAO(connection);
		PlaylistDAO plDAO=new PlaylistDAO(connection);
		
		String generalError=null;
		int songID=-1;
		Song mySong=null;
		String currentPlaylist=null;
		
		
		//parsing request parameters________________________

		try {
			
			songID=Integer.parseInt( request.getParameter("songID") );
			currentPlaylist=request.getParameter("currentPlaylist");
			
			System.out.println("songID: "+ songID);
			System.out.println("currentPlaylist: "+ currentPlaylist);
			
		}catch (NumberFormatException | NullPointerException e) {
			
			generalError="en error occured while parsing";
			e.printStackTrace();
			
		}
		
		if(songID==-1 ) {
			generalError="en error occured while parsing";
			System.out.println(generalError);
		}
		
		//FILTERS_______________________
		//recover info
		try {
			
			mySong=sDAO.getSongFromID(songID);
			
		}catch(SQLException e){
			
			e.printStackTrace();
			generalError="something went wrong while searcing in the dataase";
			System.out.println(generalError);
			
		}
		if(mySong!=null && generalError==null) {
			//check the song user exists
			if(!mySong.getUser().equals(user.getUserName())) {
				generalError="you can't play that song";
				System.out.println(generalError);
			}
			
			//check if the current playlist contains the song I want to play
			try {
				if(!plDAO.checkSongPresenceInPlaylist(mySong, currentPlaylist) && generalError==null) {
					generalError="the song is not in this playlist";
					System.out.println(generalError);
				}
			} catch (SQLException e) {
				generalError="something went wrong while searcing in the dataase";
				System.out.println(generalError);
				e.printStackTrace();
			}
		}
		
		if(generalError==null) {
			//send to PlayerPage
			String path = "/WEB-INF/PlayerPage.html";
			ServletContext servletContext = getServletContext();
			
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			String img= mySong.getAlbum().getImgFileName();
			String imgPath=directoryHandler.getImgFileDirectory(getServletContext())+ mySong.getUser() + File.separator + mySong.getAlbum().getTitle() + File.separator + img;	
			ctx.setVariable("imgFile", FileHandler. getFileBase64(imgPath) );
			System.out.println("img: "+img);
			//System.out.println("imgPath: "+imgPath);
			
			String audio= mySong.getAudioFileName();
			String audioPath=directoryHandler.getAudioFileDirectory(getServletContext())+ mySong.getUser() + File.separator + mySong.getAlbum().getTitle() + File.separator + audio;	
			ctx.setVariable("audioFile", FileHandler.getFileBase64(audioPath) );
			System.out.println("audio: "+audio);
			//System.out.println("audioPath: "+audioPath);
			
			ctx.setVariable("song", mySong);
			ctx.setVariable("currentPlaylist", currentPlaylist);
			
			templateEngine.process(path, ctx, response.getWriter());
		}else {
			
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?generalError=" + generalError ;
			response.sendRedirect(path);
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
