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
import javax.servlet.http.HttpSession;


@WebFilter( urlPatterns = {"/GoToHomePage", "/GoToPlaylistPage", "/GoToPlayerPage", "/CreatePlaylist", "/AddSongToPlaylist", "/CreateSong", "/Logout"})

public class SessionChecker extends HttpFilter implements Filter {
		
		//checks that the session is active; in case it is not so, redirect to the Sign In page
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			
			System.out.println("->SessionChecker");
			
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			String loginPath = req.getServletContext().getContextPath() +"/index.html"; // req.getServletContext().getContextPath() +

			HttpSession s = req.getSession();
			if (s.isNew() || s.getAttribute("user") == null) {
				res.sendRedirect(loginPath);
				return;
			}
			chain.doFilter(request, response);
		}
	}
