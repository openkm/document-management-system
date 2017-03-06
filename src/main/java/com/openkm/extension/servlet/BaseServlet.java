package com.openkm.extension.servlet;

import com.openkm.core.HttpSessionManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

	public void updateSessionManager(HttpServletRequest request) {
		HttpSessionManager.getInstance().update(request.getSession().getId());
	}
}
