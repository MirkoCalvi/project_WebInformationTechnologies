package it.polimi.tiw.playlist.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public Logout() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	
    	//Invalidate session
		request.getSession(true).invalidate();
		
		//Redirect to the sign in page
		String path = getServletContext().getContextPath();
		response.sendRedirect(path);
		
	}

}
