package it.polimi.tiw.playlist.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import java.util.Calendar;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;

@WebServlet("/CreateSong")
@MultipartConfig
public class CreateSongServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private String imgFolderPath;
	private String audioFolderPath;
	
	public CreateSongServlet() {
		super();
	}
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		imgFolderPath = context.getInitParameter("imgFolderPath");
		audioFolderPath = context.getInitParameter("audioFolderPath");
		
		try {
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//method that creates a song
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		String userName = (String)session.getAttribute("user");
		
		String songTitle = request.getParameter("songTitle");
		String genre = request.getParameter("genre");
		String singer = request.getParameter("singer");
		String albumTitle = request.getParameter("albumTitle");
		String year = request.getParameter("year");
		int publicationYear = -1;
		
		Part fileImage = request.getPart("fileImage");
		Part fileAudio = request.getPart("fileAudio");
		
		String error = null;
		
		//Checking the String parameters
		if(songTitle == null || songTitle.isEmpty() || genre == null || genre.isEmpty() || singer == null || singer.isEmpty()
				|| albumTitle == null || albumTitle.isEmpty() || year == null || year.isEmpty() 
				|| fileImage == null || fileImage.getSize() <= 0 || fileAudio == null ||  fileAudio.getSize() <= 0) {
			error = "Missing parameters";
		}
		
		if(error == null && (songTitle.length() > 50)) error = "Song title is too long";
		if(error == null && !( genre.equals("Others") || genre.equals("Rap") || genre.equals("Rock") || genre.equals("Jazz") || genre.equals("Pop") )) error = "Genre not valid";
		if(error == null && (singer.length() > 50)) error = "Singer name is too long";
		if(error == null && (singer.length() > 50)) error = "Album title is too long";
		
		if(error == null) {
			try {
				publicationYear = Integer.parseInt(year);
				int currentYear = Calendar.getInstance().get(Calendar.YEAR);
				if(publicationYear > currentYear)
					error = "The release year of the album is bigger than the current year";
			}catch(NumberFormatException e) {
				error = "The release year of the album is not valid";
			}
		}
		
		//if an error occurred, it will be shown in the page
		if(error != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			response.getWriter().println(error);
			return;
		}
		
		//Checking the image file
		String fileImageName = Path.of(fileImage.getSubmittedFileName()).getFileName().toString();
		if(fileImageName.contains("/")) error = "'/' are not allowed in file names";
		else {
			if(!fileImage.getContentType().startsWith("image")) error = "The image file is not valid;";
			else {
				if(fileImage.getSize() > 5000000) error = "Image file size is too big;"; //5 000 000 bytes = 5MB
				else if(fileImageName.length() > 50) error = "Image file name is too long";
			}
		}
		
		//if an error occurred, it will be shown in the page
		if(error != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			response.getWriter().println(error);
			return;
		}
		
		//Checking the audio file
		String fileAudioName = Path.of(fileAudio.getSubmittedFileName()).getFileName().toString();
		if(fileAudioName.contains("/")) error = "'/' are not allowed in file names";
		else {
			if(!fileAudio.getContentType().startsWith("audio")) error = "The audio file is not valid;";
			else {
				if(fileAudio.getSize() > 5000000) error = "Audio file size is too big;"; //5 000 000 bytes = 5MB
				else if(fileAudioName.length() > 50) error = "Audio file name is too long";	
			}
		}
		
		//if an error occurred, it will be shown in the page
		if(error != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			response.getWriter().println(error);
			return;
		}
		
		//Preparing the paths for storing the files
		String fileImagePath = this.imgFolderPath + userName + "_" + fileImageName;
		
		String fileAudioPath = this.audioFolderPath + userName + "_" + fileAudioName;
		
		//checking whether an audio/image file with the same name already exists or not
		if(error == null && new File(fileAudioPath).exists()) error = "An audio file with this name already exists, change the name please";
		
		boolean alreadyExists = new File(fileImagePath).exists();
		
		//storing the two files
		if(error == null) {
			if(!alreadyExists) { //if the image file already exists, it will not be replaced		
				try {
					Files.copy(fileImage.getInputStream(), new File(fileImagePath).toPath());
				} catch (Exception e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
					response.getWriter().println("Error in uploading the image");
					return;
				}
			}
			
			if(error == null) {
				try {
					Files.copy(fileAudio.getInputStream(), new File(fileAudioPath).toPath());
				} catch (Exception e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
					response.getWriter().println("Error in uploading the audio");
					return;
				}
			}
		}
		
		
		//if an error occurred, it will be shown in the page
		if(error != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			response.getWriter().println(error);
			return;
		}
		
		//Updating the database
		Integer addedSongId;
		try {
			addedSongId = new SongDAO(this.connection).addSongAndAlbum(songTitle, genre, fileAudioName, userName, albumTitle, fileImageName, singer, publicationYear);
			if( addedSongId == -1 ) {
				if(!alreadyExists) {
					new File(fileImagePath).delete();
				}
				new File(fileAudioPath).delete();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
				response.getWriter().println("This song already exists");
				return;
			}
		} catch (SQLException e) {
			if(!alreadyExists) {
				new File(fileImagePath).delete();
			}
			new File(fileAudioPath).delete();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);//Code 200
		response.getWriter().println(addedSongId.toString());
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
