/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.servlet.mobile;

import com.openkm.api.OKMAuth;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class DashboardServlet
 */
public class GeneralServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(GeneralServlet.class);
	private static final long serialVersionUID = 1L;

	public GeneralServlet() {
		super();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		log.debug("action: {}", action);

		try {
			if (action.equals("logout")) {
				logout(request, response);
			}
		} catch (RepositoryException e) {
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		}
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			AccessDeniedException, RepositoryException, DatabaseException, IOException {
		log.debug("logout({}, {})", request, response);
		OKMAuth.getInstance().logout(null);
		request.getSession().invalidate();
		response.sendRedirect(request.getContextPath());
	}

	/**
	 * Dispatch errors
	 */
	private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response, Throwable e)
			throws ServletException, IOException {
		request.setAttribute("javax.servlet.jsp.jspException", e);
		ServletContext sc = getServletConfig().getServletContext();
		sc.getRequestDispatcher("/error.jsp").forward(request, response);
	}
}