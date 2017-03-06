/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.servlet.mobile;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
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
public class DashboardServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(DashboardServlet.class);
	private static final long serialVersionUID = 1L;

	public DashboardServlet() {
		super();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		log.debug("action: {}", action);

		try {
			if (action.equals("mainMenu")) {
				mainMenu(request, response);
			} else if (action.equals("lastModified")) {
				lastModified(request, response);
			} else if (action.equals("lastUploaded")) {
				lastUploaded(request, response);
			} else if (action.equals("checkout")) {
				checkout(request, response);
			} else if (action.equals("downloaded")) {
				downloaded(request, response);
			} else if (action.equals("locked")) {
				locked(request, response);
			} else if (action.equals("generalLastWeekViewed")) {
				generalLastWeekViewed(request, response);
			} else if (action.equals("generalLastMonthViewed")) {
				generalLastMonthViewed(request, response);
			} else if (action.equals("generalLastWeekModified")) {
				generalLastWeekModified(request, response);
			} else if (action.equals("generalLastMonthModified")) {
				generalLastMonthModified(request, response);
			} else if (action.equals("generalLastUploaded")) {
				generalLastUploaded(request, response);
			} else if (action.equals("generalLastModified")) {
				generalLastModified(request, response);
			}
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} catch (OKMException e) {
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Dispatch errors 
	 */
	private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response,
	                               Throwable e) throws ServletException, IOException {
		request.setAttribute("javax.servlet.jsp.jspException", e);
		ServletContext sc = getServletConfig().getServletContext();
		sc.getRequestDispatcher("/error.jsp").forward(request, response);
	}

	/**
	 * mainMenu
	 */
	private void mainMenu(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("mainMenu({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_menu.jsp").forward(request, response);
	}

	/**
	 * lastModified
	 */
	private void lastModified(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("lastModified({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getUserLastModifiedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * lastUploaded
	 */
	private void lastUploaded(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("lastUploaded({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getUserLastUploadedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * checkout
	 */
	private void checkout(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("checkout({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getUserCheckedOutDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * downloaded
	 */
	private void downloaded(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("downloaded({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getUserLastDownloadedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * locked
	 */
	private void locked(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("downloaded({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getUserLockedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * generalLastWeekViewed
	 */
	private void generalLastWeekViewed(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("generalLastWeekViewed({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getLastWeekTopDownloadedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * generalLastMonthViewed
	 */
	private void generalLastMonthViewed(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("generalLastWeekViewed({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getLastMonthTopDownloadedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * generalLastWeekModified
	 */
	private void generalLastWeekModified(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("generalLastWeekModified({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getLastWeekTopModifiedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * generalLastMonthModified
	 */
	private void generalLastMonthModified(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("generalLastMonthModified({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getLastMonthTopModifiedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * generalLastUploaded
	 */
	private void generalLastUploaded(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("generalLastUploaded({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getLastUploadedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}

	/**
	 * generalLastModified
	 */
	private void generalLastModified(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException, OKMException {
		log.debug("generalLastModified({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("dashboardDocs", new com.openkm.servlet.frontend.DashboardServlet().getLastModifiedDocuments());
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/dashboard_browse.jsp").forward(request, response);
	}
}