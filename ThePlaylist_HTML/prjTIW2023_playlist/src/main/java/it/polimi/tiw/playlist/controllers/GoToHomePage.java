package it.polimi.tiw.playlist.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.directoryHandler;
import it.polimi.tiw.playlist.beans.*;
import it.polimi.tiw.playlist.dao.*;


@WebServlet("/GoToHomePage")
public class GoToHomePage extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
  
    
    public GoToHomePage() {
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

	
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		PlaylistDAO plDAO = new PlaylistDAO(connection);
		List<Playlist> playlists = new ArrayList<>();
		String GeneralError=null;

		try {
			playlists = plDAO.getPlaylistSortedByDateFromUsername(user.getUserName());
		} catch (SQLException e) {
			GeneralError="Not possible to recover playlists ";
		}
		
		SongDAO sDAO= new SongDAO(connection);
		List<Song> songs= new ArrayList<Song>();
		ArrayList<Album> myAlbums=null;

		try {
			songs = sDAO.getSongsByUser(user.getUserName());
		} catch (SQLException e) {
			if(GeneralError==null) GeneralError="Not possible to recover your songs ";
			else GeneralError=GeneralError +", Not possible to recover your songs ";
		}
		
		
		myAlbums= new ArrayList<>();
		for(Song s: songs) {
			//adding the album if not already in
			boolean flag=false;
			for(Album a: myAlbums) {
				if(a.getID()==s.getAlbum().getID()) {
					flag=true;
					break;
				}
			}
			if(!flag) myAlbums.add(s.getAlbum());
		}
		
		
		
		// Redirect to the Home page and add the parameters
		String path = "/WEB-INF/HomePage.html";
		ServletContext servletContext = getServletContext();
		
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("playlists", playlists);
		ctx.setVariable("MySongs", songs);
		ctx.setVariable("myAlbums", myAlbums);
		ctx.setVariable("genres", MusicGenre.values());
		ctx.setVariable("username", user.getUserName());
		ctx.setVariable("GeneralError", GeneralError);
		String impath= "C:\\Users\\Mirko\\Desktop\\poli\\anno_III\\TIW\\progettoTIW2023\\files\\imgfile\\giradischi.jpg";
		System.out.println("imgpath:"+impath);
		ctx.setVariable("playlistsImg", directoryHandler.getFileDirectory(getServletContext())); //directoryHandler.getFileDirectory(getServletContext())+ File.separator +"imgfiles"+ File.separator +"giradischi.jpg"
		
		//System.out.println(ImageHandler.getImage( StringEscapeUtils.escapeJava( "C:\\Users\\Mirko\\Desktop\\poli\\anno_III\\TIW\\progettoTIW2023\\files\\imgfile\\giradischi.jpg")));
		
		//taking the errors coming from the create playlist form in the home page
		if(request.getParameter("plError") != null) {
			ctx.setVariable("plError", URLDecoder.decode(request.getParameter("plError"), "UTF-8"));
		}
		
		//taking the errors coming from the create song form in the home page
		if(request.getParameter("sgError") != null) {
			ctx.setVariable("sgError", URLDecoder.decode(request.getParameter("sgError"), "UTF-8"));
		}
				
		//taking the errors
		if(request.getParameter("generalError") != null) {
			ctx.setVariable("generalError", URLDecoder.decode(request.getParameter("generalError"), "UTF-8"));
		}
				
		//taking the messages 
		if(request.getParameter("message") != null) {
			ctx.setVariable("message", URLDecoder.decode(request.getParameter("message"), "UTF-8"));
		}
		
		templateEngine.process(path, ctx, response.getWriter());
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
