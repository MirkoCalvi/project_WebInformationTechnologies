package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import it.polimi.tiw.playlist.dao.UserDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;


@WebServlet("/SignUp")
@MultipartConfig
public class SignUpServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public SignUpServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//method that creates the user credentials
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String error = null;
		
		//checking the given parameters
		if(userName == null || password == null || userName.isEmpty() || password.isEmpty()) error = "Missing parameters";
		if(error == null && userName.length() > 50) error = "Username is too long";
		if(error == null && password.length() > 50) error = "Password is too long";
		if(error == null && userName.contains(" ")) error = "Spaces are not allowed in the userName";
		if(error == null) {
			try {
				if(new UserDAO(this.connection).registration(userName, password)) {
					request.getSession(true).setAttribute("user", userName);
					response.setStatus(HttpServletResponse.SC_OK);//Code 200
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().println(userName);
					return;
				}
				error = "UserName already taken";
			} 
			catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
				response.getWriter().println("Database error, try again");
				return;
			}
		}
		
		//if an error occurred, it will be shown in the page
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
		response.getWriter().println(error);
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
  }
}
