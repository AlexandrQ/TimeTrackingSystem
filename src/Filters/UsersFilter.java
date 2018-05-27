package Filters;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Beans.User;




@WebFilter("/UsersFilter")
public class UsersFilter implements Filter {

   
    public UsersFilter() {
       
    }

	
	public void destroy() {
		
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		User session = (User) req.getSession().getAttribute("user");
		String url = req.getRequestURI();
		Cookie cookie = null;
		
		
		if (session == null || !session.isLogged()) {
			if(url.indexOf("/myActivity.xhtml") >= 0 || url.indexOf("/logout.xhtml") >= 0) {
				resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
			} else {
				chain.doFilter(request, response);
			}
		} else {
			if (url.indexOf("/register.xhtml") >= 0 || url.indexOf("/login.xhtml") >= 0) {
				resp.sendRedirect(req.getServletContext().getContextPath() + "/myActivity.xhtml");
			} else if(url.indexOf("/logout.xhtml") >= 0) {
				req.getSession().removeAttribute("user");
				resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
			} else {
				chain.doFilter(request, response);
			}
		}
		
		
		
		
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
		
	}


}
