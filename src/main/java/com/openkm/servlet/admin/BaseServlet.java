package com.openkm.servlet.admin;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.HttpSessionManager;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected static final String METHOD_GET = "GET";
	protected static final String METHOD_POST = "POST";

	/**
	 * Dispatch errors
	 */
	protected void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response,
	                                 Throwable e) throws ServletException, IOException {
		request.setAttribute("javax.servlet.jsp.jspException", e);
		ServletContext sc = getServletConfig().getServletContext();
		sc.getRequestDispatcher("/error.jsp").forward(request, response);
	}

	/**
	 * Dispatch errors
	 */
	protected void sendError(PrintWriter out, String msg) throws ServletException, IOException {
		out.println("<div class=\"error\">" + msg + "</div>");
		out.flush();
	}

	/**
	 * Update HTTP session manager
	 */
	public void updateSessionManager(HttpServletRequest request) {
		HttpSessionManager.getInstance().update(request.getSession().getId());
	}

	/**
	 * Check if this is a POST request
	 */
	public boolean isPost(HttpServletRequest request) {
		return WebUtils.METHOD_POST.equals(request.getMethod());
	}
	
	/**
	 * Test if an user can access to administration
	 */
	public static boolean isAdmin(HttpServletRequest request) {
		return request.isUserInRole(Config.DEFAULT_ADMIN_ROLE);
	}

	/**
	 * Test if an user can access to administration when configured as SaaS: An user can
	 * access if:
	 * <p>
	 * - Multiple Instances is active AND user id okmAdmin
	 * - Multiple Instances is inactive AND user has AdminRole role
	 */
	public static boolean isMultipleInstancesAdmin(HttpServletRequest request) {
		return (Config.SYSTEM_MULTIPLE_INSTANCES || Config.CLOUD_MODE) && request.getRemoteUser().equals(Config.ADMIN_USER) ||
				!(Config.SYSTEM_MULTIPLE_INSTANCES || Config.CLOUD_MODE) && request.isUserInRole(Config.DEFAULT_ADMIN_ROLE);
	}

	/**
	 * Check for forbidden access
	 */
	public boolean checkMultipleInstancesAccess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isMultipleInstancesAdmin(request)) {
			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_ACCESS_DENIED", request.getRequestURI(), null, request.getQueryString());

			AccessDeniedException ade = new AccessDeniedException("You should not access this resource");
			sendErrorRedirect(request, response, ade);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Print HTML page header
	 */
	public void header(PrintWriter out, String title, String[][] breadcrumb) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<link rel=\"Shortcut icon\" href=\"favicon.ico\" />");
		out.println("<link rel=\"stylesheet\" href=\"css/style.css\" type=\"text/css\" />");
		out.println("<script src=\"js/biblioteca.js\" type=\"text/javascript\"></script>");
		out.println("<script type=\"text/javascript\">scrollToBottom();</script>");
		out.println("<script type=\"text/javascript\" src=\"../js/jquery-1.11.3.min.js\"></script>");
		out.println("<script type=\"text/javascript\" src=\"js/jquery.DOMWindow.js\"></script>");
		out.println("<script type=\"text/javascript\">");
		out.println("$(document).ready(function() { $dm = $('.ds').openDOMWindow({");
		out.println("height:200, width:300, eventType:'click', overlayOpacity:'57', windowSource:'iframe', windowPadding:0");
		out.println("})});");
		out.println("function dialogClose() { $dm.closeDOMWindow(); }");
		out.println("function keepSessionAlive() { $.ajax({ type:'GET', url:'../SessionKeepAlive', cache:false, async:false }); }");
		out.println("window.setInterval('keepSessionAlive()', " + TimeUnit.MINUTES.toMillis(Config.KEEP_SESSION_ALIVE_INTERVAL) + ");");
		out.println("</script>");
		out.println("<title>" + title + "</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<ul id=\"breadcrumb\">");

		for (String[] elto : breadcrumb) {
			out.println("<li class=\"path\">");
			out.print("<a href=\"" + elto[0] + "\">" + elto[1] + "</a>");
			out.print("</li>");
		}

		out.println("<li class=\"path\">" + title + "</li>");
		out.println("</ul>");
		out.println("<br/>");
	}

	/**
	 * Print HTML page footer
	 */
	public void footer(PrintWriter out) {
		out.println("</body>");
		out.println("</html>");
	}

	/**
	 * Print ok messages
	 */
	public void ok(PrintWriter out, String msg) {
		out.print("<div class=\"ok\">" + msg + "</div>");
	}

	/**
	 * Print warn messages
	 */
	public void warn(PrintWriter out, String msg) {
		out.print("<div class=\"warn\">" + msg + "</div>");
	}
}
