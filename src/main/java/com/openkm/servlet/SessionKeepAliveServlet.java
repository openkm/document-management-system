package com.openkm.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author pavila
 * @see http://tech.top21.de/techblog/20110125-maintaining-sessions-with-ajax-polling-and-servlets.html
 */
public class SessionKeepAliveServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(SessionKeepAliveServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("Session keep alive poll from {}", request.getHeader("Referer"));

		// Access the session without creating it - this maintains the session
		request.getSession(false);

		// Send a 204
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}
