package it.polimi.tiw.playlist.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.annotation.WebServlet;

import org.apache.tomcat.util.codec.binary.Base64;



public class FileHandler {
	
    public static String getFileBase64(String ImgPath) {
		
        // Read the image file
        File file = new File(ImgPath);
        String base64File=null;
        
        try {
        	
            FileInputStream fis = new FileInputStream(file);

            // Convert the image to a Base64-encoded string
            byte[] imageBytes = new byte[(int) file.length()];
            fis.read(imageBytes);
            base64File = Base64.encodeBase64String(imageBytes);
            
            fis.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return base64File;
    }

}








