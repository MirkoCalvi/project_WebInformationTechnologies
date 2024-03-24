package it.polimi.tiw.playlist.utils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;

public class GetEncoding {
	

	//Static method that takes from the local storage the image and encodes it into a base64 string
	public static String getImageEncoding(String fileImage , ServletContext context) throws IOException {
		
		String imgFolderPath = context.getInitParameter("imgFolderPath");
		
		File file = new File(imgFolderPath + fileImage);
		
		if (!file.exists() || file.isDirectory()) {
			return null;
		}
		
		//Take the byte array of the image
		byte[] fileContent = FileUtils.readFileToByteArray(file);
		//Take the base64 string
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		
		//Take the right type of each file (png , jpeg ...)
		encodedString = "data:" + context.getMimeType(fileImage) + ";base64," + encodedString ;
		
		return encodedString;
	}


	//Static method that takes from the local storage the song and encodes it into a base64 string
	public static String getSongEncoding(String fileAudio , ServletContext context) throws IOException {

		String audioFolderPath = context.getInitParameter("audioFolderPath");
		
		File file = new File(audioFolderPath + fileAudio);
		
		if (!file.exists() || file.isDirectory()) {
			return null;
		}
		
		//Take the byte array of the image
		byte[] fileContent = FileUtils.readFileToByteArray(file);
		//Take the base64 string
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		
		//Take the right type of the file (mp3)
		encodedString = "data:" + context.getMimeType(fileAudio) + ";base64," + encodedString ;
		
		return encodedString;
	}
}