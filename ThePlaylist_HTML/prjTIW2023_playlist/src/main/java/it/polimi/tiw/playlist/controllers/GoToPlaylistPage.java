package it.polimi.tiw.playlist.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
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

import it.polimi.tiw.playlist.beans.Album;
import it.polimi.tiw.playlist.beans.MusicGenre;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.beans.User;
import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.FileHandler;
import it.polimi.tiw.playlist.utils.directoryHandler;


@WebServlet("/GoToPlaylistPage")

public class GoToPlaylistPage extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;   
   
    public GoToPlaylistPage() {
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
		
		System.out.println("---doGet() GoToPlaylist:");
		
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		String generalError=null;
		ArrayList<Song> songsToShow=null;
		
		SongDAO sDAO=new SongDAO(connection);
		PlaylistDAO plDAO=new PlaylistDAO(connection);
		
		Integer nubOfQuintuple=-1;
		Integer totNumbOfSong;
		
		String PlTitle=null;
		String[] values=null;
		
		//reading the parameters
		try {
			
			if(request.getParameter("numbPage") != null) nubOfQuintuple=Integer.parseInt(request.getParameter("numbPage"));
			if(request.getParameter("goToPage") != null) {
				System.out.println("reading goToPage");
				nubOfQuintuple=Integer.parseInt(URLDecoder.decode(request.getParameter("goToPage"), "UTF-8"));
				System.out.println("  nubOfQuintuple:"+ nubOfQuintuple.toString());
			}
			
			if(request.getParameter("title") != null) PlTitle=request.getParameter("title");
			System.out.println("  nubOfQuintuple:"+ nubOfQuintuple.toString());
			System.out.println("  PlTitle:"+ PlTitle);
			
		}catch (NumberFormatException | NullPointerException e) {	
			generalError="en error occured while parsing";
			e.printStackTrace();
		}
		
		if(nubOfQuintuple==null || nubOfQuintuple<0  ){		
			generalError="Incorrect or missing param values";
		}
		
		if(generalError!=null) {	
			
			System.out.println(generalError);
	
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?generalError=" + generalError;
			response.sendRedirect(path);
			return;	
		}
		if(PlTitle==null) {
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage";
			response.sendRedirect(path);
			return;	
		}
			
				
		try {
			
			//checking that the requested Playlist appertains to the current user
			if( !plDAO.checkPlaylistPresence(user.getUserName(), PlTitle)) {
				System.out.println("redirect to /index.html");
				String loginpath = getServletContext().getContextPath() + "/index.html";
				response.sendRedirect(loginpath);
				return;
			}
			
			songsToShow = sDAO.getSongsByPlaylistAndUsername(PlTitle, user.getUserName(), nubOfQuintuple );
			System.out.println("  number songsToShow: "+ ((songsToShow==null)? "0" : songsToShow.size() ));
			totNumbOfSong= plDAO.getTotNumbOfSong( user.getUserName(), PlTitle );
			System.out.println("  totNumbOfSong: "+ totNumbOfSong);
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover playlists ");
			return;
		}
		
		if(songsToShow==null || totNumbOfSong==null ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "not possible to recover data");
			return;
		}
		
		
		for(Song s: songsToShow) {
			
			//converting the image to base64
			String img= s.getAlbum().getImgFileName();
			String imgPath=directoryHandler.getImgFileDirectory(getServletContext())+ s.getUser() + File.separator + s.getAlbum().getTitle() + File.separator + img;
			s.getAlbum().setImgFileName(FileHandler.getFileBase64(imgPath));
			
		} 
		
		
		
		// Redirect 
		String path = "/WEB-INF/PlaylistPage.html";
		ServletContext servletContext = getServletContext();
		
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		ctx.setVariable("PlTitle", PlTitle);
		ctx.setVariable("songs", songsToShow);
		ctx.setVariable("filePath", directoryHandler.getImgFileDirectory(getServletContext()));
		ctx.setVariable("nubOfQuintuple", nubOfQuintuple );
		ctx.setVariable("rightButton", (nubOfQuintuple+1)*5<totNumbOfSong );
		ctx.setVariable("leftButton", nubOfQuintuple>0 );
		
		if(request.getParameter("insertError") != null) {
			ctx.setVariable("insertError", URLDecoder.decode(request.getParameter("insertError"), "UTF-8"));
		}
		
		templateEngine.process(path, ctx, response.getWriter());
			
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		doGet( request,response );
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
