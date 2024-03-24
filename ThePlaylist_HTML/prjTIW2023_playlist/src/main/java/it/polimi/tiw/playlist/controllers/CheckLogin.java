package it.polimi.tiw.playlist.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
//import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.beans.User;
import it.polimi.tiw.playlist.dao.UserDAO;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
    public CheckLogin() {
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

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// obtain and escape params
		String usrn = null;
		String pwd = null;
		String errorMsg=null;
		String path=null;
		
		try {
			
			usrn =StringEscapeUtils.escapeJava( request.getParameter("username") );
			pwd = StringEscapeUtils.escapeJava( request.getParameter("pwd") );
			
			System.out.println("LOGIN");
			System.out.println("user:"+usrn);
			System.out.println("pw:"+pwd);
			
			if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
				
				errorMsg = "Missing or empty credential value";

			}

		} catch (Exception e) {
			
			e.printStackTrace();
			errorMsg="Missing credential value";
			
		}
		
		if(errorMsg!=null) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", errorMsg);
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
			
		}

		// query db to authenticate for user
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			
			user = userDao.checkCredentials(usrn, pwd);
			
			
		} catch (SQLException e) {
			
			errorMsg="Not Possible to check credentials";

			
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message
		
		if (user == null ) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			errorMsg="Not Possible to check credentials";
			ctx.setVariable("errorMsg", errorMsg);
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
			
			
		} else {
			
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/GoToHomePage";
			response.sendRedirect(path);
			
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
