package it.polimi.tiw.playlist.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

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

import it.polimi.tiw.playlist.beans.Album;
import it.polimi.tiw.playlist.beans.MusicGenre;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.beans.User;
import it.polimi.tiw.playlist.dao.AlbumDAO;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.directoryHandler;


@WebServlet("/CreateSong")
@MultipartConfig
public class CreateSong extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
    public CreateSong() {
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

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("---doPost() create song:");
		
		HttpSession session = request.getSession();
		User u=(User) session.getAttribute("user");
		String user = u.getUserName();
		Song mySong=new Song();
		Album myAlbum=new Album();
		mySong.setAlbum(myAlbum);
		String myAlbumTitle=null;
		
		//DAO----------
		SongDAO sDAO= new SongDAO(connection);
		AlbumDAO aDAO=new AlbumDAO(connection);
		
		//required param-----------------
		String songTitle=null; //v
		
		String GenStr=null; //v
		MusicGenre genre=null; //v
		
		Part audioPart=null; 
		String uploadDirAudio=null;
		String filenameAudio=null;
		
		Part albumCover=null;
		String uploaImgdDir=null;
		String filenameImg=null;
		
		//necessary pram-----------------
		int existingAlbumTitleID=-1; //v
		String existingAlbumTitleIDString=null;
		String newAlbumTitle=null; //v
		//optional param-----------------
		String artist=null; //v
		int albumId=-1;
		int yearPubl=-1;
		
		
		String sgError=null;
		String message=null;
		
		//read the request parameters
		try {
			
			songTitle = request.getParameter("SongTitle"); //StringEscapeUtils.escapeJava(request.getParameter("SongTitle"));
			
			GenStr = request.getParameter("genre");
			
			audioPart = request.getPart("audioFile");
			
			existingAlbumTitleIDString=StringEscapeUtils.escapeJava(request.getParameter("ExistingAlbumTitle"));
			
			newAlbumTitle=StringEscapeUtils.escapeJava(request.getParameter("newAlbumTitle"));
			
			System.out.println("song: "+songTitle);
			System.out.println("genre: "+GenStr);
		    System.out.println("existing album ID: "+existingAlbumTitleIDString);	
			System.out.println("new album : "+newAlbumTitle);
			
			genre = MusicGenre.getGenre(GenStr);
		
			
		} catch (NumberFormatException | NullPointerException e) {
			
			System.out.println("Incorrect or missing param values");
			e.printStackTrace();
			sgError="Incorrect or missing param values";
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?sgError=" + sgError;
			response.sendRedirect(path);
			return;	
			
		}
		
		//SONG FILTERS_______________________________________________________________________________________________________
		//check required param
		if(songTitle==null || songTitle.isEmpty() || GenStr==null || GenStr.isEmpty() || audioPart==null ){
			
			sgError= " missing param value ";
			System.out.println("error:  missing param value");

		}
		//check album title, both new and existing
		if(	(existingAlbumTitleIDString==null || existingAlbumTitleIDString.isEmpty()) && ( newAlbumTitle==null || newAlbumTitle.isEmpty())){
			
			sgError= " you must at least insert and existing album or create a new one";
			System.out.println(" error: you must at least insert and existing album or create a new one");

		}
		
		
		//NEW ALBUM or OLD ALBUB___________________________________________________________________________________________
		
		//CASE 1: already existing album++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(sgError==null && existingAlbumTitleIDString!=null && !existingAlbumTitleIDString.isEmpty()) {
			
			System.out.println("	-existing album");
			existingAlbumTitleID=Integer.parseInt(existingAlbumTitleIDString);
			//check album existence and owner
			try {
			
				myAlbum=aDAO.getAlbum(existingAlbumTitleID);
				
	        	if(myAlbum!=null) {
	        		
	        		//mySong.setAlbum(myAlbum);
	        		myAlbumTitle=myAlbum.getTitle();
	        		
	        		System.out.println("	title: "+myAlbum.getTitle());
	        		System.out.println("	artist: "+myAlbum.getArtist());
					System.out.println("	yearPubl: "+myAlbum.getPublicationYear());
					
					mySong.setAlbum(myAlbum);
	        		
	        	}else {
	        		sgError="impossible to find the album";
					System.out.println("	error: "+sgError);
	        	}
	        	
	        	
	        }catch(SQLException e ) {
	        	
				sgError="ops... something went wrong with the database";
				System.out.println("ops... something went wrong with the database");
				
	        }catch(Exception e ) {
	        	sgError="ops... generic Exception";
				System.out.println("ops... generic Exception");
	        }
			
			
		}else
			
		//CASE 2: creating a new album	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(newAlbumTitle!=null && !newAlbumTitle.isEmpty() && newAlbumTitle!="" && sgError==null && message==null) {
			
			System.out.println("	-existing album");
			myAlbumTitle=newAlbumTitle;
			
			//lo creo
			
			//read the request parameters of the new album
			try {
				
				artist = request.getParameter("Artist"); //StringEscapeUtils.escapeJava(request.getParameter("SongTitle"));
				
				yearPubl =Integer.parseInt(request.getParameter("yearPubl")); 
				
				albumCover = request.getPart("AlbumCover");
						
			} catch (NumberFormatException | NullPointerException e) {	
				e.printStackTrace();
				sgError="Incorrect or missing param values";	
				System.out.println();
			}
			
			//ALBUM FILTERS___________________________________________________________________________-
			//check required param
			if((artist==null || albumCover==null) && sgError==null){					
				sgError= " missing param value ";
				System.out.println();
			}
			if((yearPubl>Year.now().getValue() || yearPubl<0 ) && sgError==null) {
				sgError= " the pubblication yerar must be beetween 0 and current year ";
			}
			
			if(sgError!=null){
				String ctxpath = getServletContext().getContextPath();
				String path = ctxpath + "/GoToHomePage?sgError=" + sgError;
				response.sendRedirect(path);
				return;
			}
			
			//LOADING img DIR------------------------------------------------
			// Specify the directory to store the uploaded img
	        uploaImgdDir =directoryHandler.getFileDirectory(getServletContext()) + File.separator +"imgfile"+ File.separator + user + File.separator + newAlbumTitle;
	        
	        // Create the directory if it doesn't exist
	        File imgDir = new File(uploaImgdDir);
	        if (!imgDir.exists()) {
	        	imgDir.mkdirs();
	        }
	        
	        // Extract the filename from the submitted file part
	        filenameImg = getSubmittedFileName(albumCover);
	        
	       // System.out.println("	audio path: "+uploadDirAudio+ File.separator +filenameAudio);
	        
			//completo i parametri di myAlbum
	        myAlbum.setTitle(myAlbumTitle);
	        myAlbum.setArtist(artist);
	        myAlbum.setPubblicationYear(yearPubl);
	        myAlbum.setImgFileName(filenameImg);
	        
	        System.out.println("	title: "+myAlbum.getTitle());
    		System.out.println("	artist: "+myAlbum.getArtist());
			System.out.println("	yearPubl: "+myAlbum.getPublicationYear());
			
	        mySong.setAlbum(myAlbum);
	        
	        
	      //verifico che non esista già un album dello stesso artista
			try {
				if(aDAO.getAlbumID(user, myAlbum) != -1) {
					
					sgError="the album: "+newAlbumTitle+" already exists";
				
				}
				else {
					
					System.out.println("	the album: "+newAlbumTitle+" is new");
				}
				
			}catch(SQLException e) {
				
				e.printStackTrace();					
				sgError="opss...impossible to access the database";
				
			}
			
		}
		if(sgError!=null) {
	    	
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?sgError=" + sgError;
			response.sendRedirect(path);
			return;
			
	    }
		
		
		//la creo
		System.out.println("...loading audio dir");
		//LOADING audio DIR--------------------------------------------------
		
		// Specify the directory to store the uploaded audio
        uploadDirAudio =directoryHandler.getFileDirectory(getServletContext()) + File.separator + "audiofile" + File.separator + user+ File.separator +myAlbumTitle;
        
        // Create the directory if it doesn't exist
        File dirAudio = new File(uploadDirAudio);
        if (!dirAudio.exists()) {
        	dirAudio.mkdirs();
        }
        
        System.out.println("...upload audio dir: "+uploadDirAudio);
        
        // Extract the filename from the submitted file part
        filenameAudio = getSubmittedFileName(audioPart);
        
        //setting mySong
        mySong.setAudioFileName(filenameAudio);
        mySong.setTitle(songTitle);
		mySong.setGenre(genre);
		mySong.setUser(user);
		
		//controllo che la canzone non esista già
		//check if already exists a song with the same title, user, album and artist
		boolean flag=true;
		try {
			
			System.out.println("	checking the presence of the song");
			System.out.println("	song title: "+mySong.getTitle());
			System.out.println("	album title: "+mySong.getAlbum().getTitle());
			System.out.println("	album artist: "+mySong.getAlbum().getArtist());
			flag=sDAO.checkSongPresence(mySong);
			
			if(flag) System.out.println("	old song");
			else System.out.println("	new song");
		
		}catch (SQLException e ) {
		
			sgError="ops... something went wrong with the database";
			System.out.println("error: ops... something went wrong with the database");
			e.printStackTrace();
			
		}
		if(sgError==null && flag) {
			
			sgError="you already have a song titled: "+songTitle+" in the album ";
			System.out.println("error: "+sgError);
		
		}
		if(sgError!=null) {
	    	
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?sgError=" + sgError;
			response.sendRedirect(path);
			return;
			
	    }
		
        //adding the new song to DB SONGS
        try {
        	
        	sDAO.insertSong(mySong, myAlbum);
        
        }catch(SQLException e ) {
        	
			sgError="ops... something went wrong with the database";
			System.out.println("ops... something went wrong with the database");
			e.printStackTrace();
			
        }catch(Exception e ) {
        	
        	sgError="ops... generic Exception";
			System.out.println("ops... generic Exception");
			e.printStackTrace();
        }
        
        
		if(sgError!=null) {
	    	
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GoToHomePage?sgError=" + sgError;
			response.sendRedirect(path);
			return;
			
	    }
		
		message=mySong.getTitle()+" has been added into "+ mySong.getAlbum().getTitle();
		
        audioPart.write(uploadDirAudio + File.separator + filenameAudio);    
        if(myAlbumTitle==newAlbumTitle) albumCover.write(uploaImgdDir + File.separator + filenameImg);
        
		// return the user to the homePage
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToHomePage?sgError=" + sgError+"&message="+message;
		response.sendRedirect(path);
		
	}
	
	private String getSubmittedFileName(Part part) {
		
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        
        return null;
        
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
