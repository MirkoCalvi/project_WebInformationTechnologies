package it.polimi.tiw.playlist.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebFilter( urlPatterns = {"/GoToPlayerPage"})
public class PalyerPageFilter extends HttpFilter implements Filter {
      
	//checks that the page is accessed through the button in the Playlist page; in case it is not so, redirect to the Home page
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		System.out.println("->PalyerPageFilter");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String HomePath = req.getServletContext().getContextPath() + "/GoToHomePage?generalError=Song+not+found";

		if (req.getParameter("songID") == null || req.getParameter("songID").isEmpty()) {
			res.sendRedirect(HomePath);
			return;
		}
		
		chain.doFilter(request, response);
	}
}
