package Filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Beans.MainBean;




@WebFilter("/UsersFilter")
public class UsersFilter implements Filter {

   
    public UsersFilter() {
       
    }

	
	public void destroy() {
		
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		MainBean session = (MainBean) req.getSession().getAttribute("mainBean");
		String url = req.getRequestURI();		
		
		if (session == null || !session.getUser().isLogged()) {
			if(url.indexOf("/myActivity.xhtml") >= 0 || url.indexOf("/logout.xhtml") >= 0 || url.indexOf("/myVacations.xhtml") >= 0 
					|| url.indexOf("/register.xhtml") >= 0 || url.indexOf("/projects.xhtml") >= 0 
					|| url.indexOf("/tasks.xhtml") >= 0 || url.indexOf("/reports.xhtml") >= 0) {
				resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
			} else {
				chain.doFilter(request, response);
			}
		} else {
			if (url.indexOf("/login.xhtml") >= 0) {
				if (session.getUser().getRole().equals("administrator")) {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/register.xhtml");
				} else {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/myActivity.xhtml");
				}	
			} else if(url.indexOf("/activityConfirmation.xhtml") >= 0) {
				if (session.getUser().getRole().equals("manager")) {
					chain.doFilter(request, response);
				} else if (session.getUser().getRole().equals("administrator")){
					resp.sendRedirect(req.getServletContext().getContextPath() + "/register.xhtml");
				} else {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/myActivity.xhtml");
				}
			} else if(url.indexOf("/myActivity.xhtml") >= 0) {
				if (session.getUser().getRole().equals("administrator")) {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/register.xhtml");
				} else {
					chain.doFilter(request, response);
				}
			} else if(url.indexOf("/myVacations.xhtml") >= 0) {
				if (session.getUser().getRole().equals("administrator")) {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/register.xhtml");
				} else {
					chain.doFilter(request, response);
				}
			} else if(url.indexOf("/projects.xhtml") >= 0) {
				if (session.getUser().getRole().equals("administrator")) {
					chain.doFilter(request, response);
				} else {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/myActivity.xhtml");
				}
			} else if(url.indexOf("/register.xhtml") >= 0) {
				if (session.getUser().getRole().equals("administrator")) {
					chain.doFilter(request, response);
				} else {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/myActivity.xhtml");
				}
			} else if(url.indexOf("/reports.xhtml") >= 0) {
				if (session.getUser().getRole().equals("hr")) {
					chain.doFilter(request, response);
				} else if (session.getUser().getRole().equals("administrator")){
					resp.sendRedirect(req.getServletContext().getContextPath() + "/register.xhtml");
				} else {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/myActivity.xhtml");
				}
			} else if(url.indexOf("/tasks.xhtml") >= 0) {
				if (session.getUser().getRole().equals("manager")) {
					chain.doFilter(request, response);
				} else if (session.getUser().getRole().equals("administrator")){
					resp.sendRedirect(req.getServletContext().getContextPath() + "/register.xhtml");
				} else {
					resp.sendRedirect(req.getServletContext().getContextPath() + "/myActivity.xhtml");
				}
			} else if(url.indexOf("/logout.xhtml") >= 0) {
				req.getSession().removeAttribute("mainBean");
				req.getSession().removeAttribute("vacationBean");
				req.getSession().removeAttribute("regBean");
				req.getSession().removeAttribute("projBean");
				req.getSession().removeAttribute("confirmBean");
				req.getSession().removeAttribute("tasksBean");
				req.getSession().removeAttribute("reportsBean");
				resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
			} else {
				chain.doFilter(request, response);
			}
		}
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
		
	}


}
