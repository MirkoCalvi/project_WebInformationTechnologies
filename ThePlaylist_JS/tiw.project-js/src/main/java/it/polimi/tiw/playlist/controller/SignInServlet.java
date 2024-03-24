package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import it.polimi.tiw.playlist.dao.UserDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;

@WebServlet("/SignIn")
@MultipartConfig
public class SignInServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public SignInServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//method that checks the user credentials
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		
		//checking the given parameters
		if(userName == null || password == null || userName.isEmpty() || password.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			response.getWriter().println("Missing parameters");
			return;
		}
		//checking if the credentials are right
		try {
			if(new UserDAO(this.connection).authentication(userName, password)) {
				request.getSession(true).setAttribute("user", userName);
				response.setStatus(HttpServletResponse.SC_OK);//Code 200
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(userName);
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//Code 401
				response.getWriter().println("Wrong UserName or Password");
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
			response.getWriter().println("Database error, try again");
		}
		
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
    }
}