package it.polimi.tiw.playlist.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.playlist.beans.Album;
import it.polimi.tiw.playlist.beans.MusicGenre;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.beans.User;
import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;


@WebServlet("/AddSongToPlaylist")
public class AddSongToPlaylist extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	  
    
    public AddSongToPlaylist() {
        super();
    }

    public void init() throws ServletException {
		
    	connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("---doPost() AddSongToPlaylist:");
		
		HttpSession session = request.getSession();
		String insertError=null;
		User user = (User) session.getAttribute("user");
		PlaylistDAO plDAO=new PlaylistDAO(connection);
		SongDAO sDAO=new SongDAO(connection);
		
		Song mySong=new Song();
		Album myAlbum=new Album();
		String songToAdd=null;
		String songAlbum=null;
		String songArtist=null;
		String targetPlaylist=null;
		ArrayList<Song> userSongs=null;
		ArrayList<Song> playlistSongs=null;
		
	
		//read the request parameters
		try {
			
			songToAdd  = request.getParameter("newSongTitle");
			songAlbum  = request.getParameter("newSongAlbum");
			songArtist = request.getParameter("newSongArtist");
			targetPlaylist = request.getParameter("targetPlaylist");
			
			System.out.println("newSongTitle: "+songToAdd);
			System.out.println("newSongAlbum: "+songAlbum);
			System.out.println("newSongArtist: "+songArtist);
			System.out.println("targetPlaylist: "+targetPlaylist);
			
			mySong.setTitle(songToAdd);
			mySong.setUser(user.getUserName());
			myAlbum.setArtist(songArtist);
			myAlbum.setTitle(songAlbum);
			
			mySong.setAlbum(myAlbum);
			
		} catch (NumberFormatException | NullPointerException e) {
			
			System.out.println("Incorrect or missing param values");
			e.printStackTrace();
			insertError="Incorrect or missing param values";
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToPlaylistPage?insertError=" + insertError+"&numbPage=0";
			response.sendRedirect(path);
			return;	
		}
		
		//FILTERS________________________________________________________
		if(songToAdd==null || songAlbum==null || songArtist==null || targetPlaylist==null) {
			insertError="ops... something went wrong, try again";
			System.out.println(insertError);
		}
		if( songToAdd.isEmpty() || songAlbum.isEmpty() || songArtist.isEmpty() || targetPlaylist.isEmpty()) {
			insertError="error: you must complete all the fields";
			System.out.println(insertError);
		}
		
		if(insertError==null) {
		//check that the playlist exists
			try {
				
				if(!plDAO.checkPlaylistPresence(user.getUserName(), targetPlaylist)) {
					insertError="error: you can't insert the song into "+targetPlaylist+" because it doesn't exists";
					System.out.println(insertError);
				}
				
			}catch(SQLException esql) {
				esql.printStackTrace();
				insertError="error: something went wrong, try again";
				System.out.println(insertError);
			}
		}
		
		
		if(insertError==null) {
			//check that the song exists
			try {
				
				if(!sDAO.checkSongPresence(mySong)) {
					insertError="error: you don't have a song titled :"+songToAdd;
					System.out.println(insertError+ mySong.toString());
				}
				
			}catch(SQLException esql) {
				esql.printStackTrace();
				insertError="error: something went wrong, try again";
				System.out.println(insertError);
			}
		}
		
		//check that the song in not already in the playlist
		if(insertError==null) {
			
			try {
				
				playlistSongs=sDAO.getAllSongsInPlaylist(targetPlaylist, user.getUserName() );

				for(Song s: playlistSongs) {
					
					System.out.println("	analizing: "+s.getTitle());
					
					if(s.getTitle().equals(mySong.getTitle()) &&
					   (s.getAlbum().getTitle()).equals(mySong.getAlbum().getTitle()) &&
					   (s.getAlbum().getArtist()).equals(mySong.getAlbum().getArtist())) {
						
						insertError="error: you altrady have '"+s.getTitle()+"' by artist:"+s.getAlbum().getArtist()+" album:"+s.getAlbum().getTitle()+" in this playlist";
						System.out.println(insertError);
						
					}
				}
			}catch(SQLException esql) {
				esql.printStackTrace();
				insertError="error: something went wrong, try again";
				System.out.println(insertError);
			}
		}
		
		//DAO INSERT__________________________
		System.out.println(" dao insert...");
		
		if(insertError==null) {
		
			int songID;
			
			try {
				
				songID=sDAO.getSongIDfromUser( user.getUserName(), mySong);
				
				if(songID!=-1) {
					plDAO.insertSongInPlaylist(songID, user.getUserName(), targetPlaylist);
				}else {
					insertError="error: impossible to find song ID";
					System.out.println(insertError);
				}
				
			}catch(SQLException e) {
				e.printStackTrace();
				insertError="error: something went wrong, try again";
				System.out.println(insertError);
			}		
		}
		
		//go to PL page 0
		String ctxpath = getServletContext().getContextPath();
		String path;
		
		if(insertError!=null) path = ctxpath + "/GoToPlaylistPage?insertError=" + insertError + "&goToPage=0";
		else path = ctxpath + "/GoToPlaylistPage?goToPage=0";
		
		System.out.println("redirect path: "+path);
		
	//	path = ctxpath + "/GoToPlaylistPage";
		response.sendRedirect(path+"&title="+targetPlaylist);
		
		return;	
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
