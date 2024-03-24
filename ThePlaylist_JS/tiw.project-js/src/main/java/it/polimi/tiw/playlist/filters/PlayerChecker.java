package it.polimi.tiw.playlist.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter( urlPatterns = {"/PlaySong"})
public class PlayerChecker implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (req.getParameter("songId") == null || req.getParameter("songId").isEmpty()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			res.getWriter().println("Song not found");
			return;
		}
		chain.doFilter(request, response);
	}
	
}