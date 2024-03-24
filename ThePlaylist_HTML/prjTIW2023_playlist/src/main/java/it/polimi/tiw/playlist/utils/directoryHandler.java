package it.polimi.tiw.playlist.utils;

import java.io.File;

import javax.servlet.ServletContext;

public class directoryHandler {
	
	public static String getFileDirectory(ServletContext context) {
		
		return context.getInitParameter("fileDir");
		
	}
	
	public static String getImgFileDirectory(ServletContext context) {
		
		return context.getInitParameter("fileDir")+"/imgfile/";
		
	}
	
	public static String getAudioFileDirectory(ServletContext context) {
		
		return context.getInitParameter("fileDir")+"/audiofile/";
		
	}

}
