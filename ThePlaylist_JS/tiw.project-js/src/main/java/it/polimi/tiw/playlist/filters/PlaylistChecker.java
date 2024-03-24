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

@WebFilter( urlPatterns = {"/EditPlaylist", "/EditSorting", "/GetSongsInPlaylist"})
public class PlaylistChecker implements Filter {
		
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;

		if (req.getParameter("playlistName") == null || req.getParameter("playlistName").isEmpty()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
			res.getWriter().println("Playlist not found");
			return;
		}
		chain.doFilter(request, response);
	}
	
}