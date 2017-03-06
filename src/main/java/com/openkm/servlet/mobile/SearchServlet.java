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

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMSearch;
import com.openkm.bean.ResultSet;
import com.openkm.core.*;
import com.openkm.dao.bean.QueryParams;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Servlet implementation class DashboardServlet
 */
public class SearchServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(SearchServlet.class);
	private static final long serialVersionUID = 1L;

	public SearchServlet() {
		super();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		log.debug("action: {}", action);

		try {
			if (action.equals("searchNormal")) {
				normalSearch(request, response);
			} else if (action.equals("searchMenu")) {
				menu(request, response);
			} else {
				normalSearch(request, response);
			}
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} catch (Exception e) {
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Dispatch errors
	 */
	private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response, Throwable e)
			throws ServletException, IOException {
		ServletContext sc = getServletContext();
		sc.setAttribute("exception", e);
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/error.jsp").forward(request, response);
	}

	/**
	 * normalSearch
	 */
	private void normalSearch(HttpServletRequest request, HttpServletResponse response) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, IOException, ServletException, DatabaseException, ParseException {
		log.debug("mainMenu({}, {})", request, response);
		String content = WebUtils.getString(request, "content", "");
		String filename = WebUtils.getString(request, "filename", "");
		String keywords = WebUtils.getString(request, "keywords", "");
		int offset = WebUtils.getInt(request, "offset", 0);
		int limit = WebUtils.getInt(request, "limit", 10);
		ServletContext sc = getServletContext();

		if (!content.isEmpty() || !filename.isEmpty() || !keywords.isEmpty()) {
			Set<String> kwd = new HashSet<String>();
			for (StringTokenizer st = new StringTokenizer(keywords); st.hasMoreTokens(); ) {
				kwd.add(st.nextToken());
			}

			QueryParams qp = new QueryParams();
			qp.setContent(content);
			qp.setName(filename);
			qp.setKeywords(kwd);

			sc.setAttribute("content", content);
			sc.setAttribute("filename", filename);
			sc.setAttribute("keywords", keywords);
			sc.setAttribute("offset", offset);
			sc.setAttribute("limit", limit);
			sc.setAttribute("resultSet", OKMSearch.getInstance().findPaginated(null, qp, offset, limit));
		} else {
			sc.setAttribute("content", null);
			sc.setAttribute("filename", null);
			sc.setAttribute("keywords", null);
			sc.setAttribute("offset", null);
			sc.setAttribute("limit", null);
			sc.setAttribute("resultSet", new ResultSet());
		}

		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/search_normal.jsp").forward(request, response);
	}

	/**
	 * menu
	 */
	private void menu(HttpServletRequest request, HttpServletResponse response) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, IOException, ServletException, DatabaseException {
		log.debug("menu({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("doc", OKMDocument.getInstance().getProperties(null, "docPath"));
		sc.getRequestDispatcher("/" + Config.MOBILE_CONTEXT + "/search_menu.jsp").forward(request, response);
	}
}