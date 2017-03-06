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

package com.openkm.servlet.admin;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.ProfilingDAO;
import com.openkm.dao.bean.Profiling;
import com.openkm.dao.bean.ProfilingStats;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Profiling stats servlet
 */
public class ProfilingStatsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ProfilingStatsServlet.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String method = request.getMethod();

		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("activate")) {
				activate(request, response);
				view(request, response);
			} else if (action.equals("deactivate")) {
				deactivate(request, response);
				view(request, response);
			} else if (action.equals("clear")) {
				clear(request, response);
				view(request, response);
			} else if (action.equals("list")) {
				list(request, response);
			} else {
				view(request, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Activate stats
	 */
	private void activate(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		Config.SYSTEM_PROFILING = true;
	}

	/**
	 * Deactivate stats
	 */
	private void deactivate(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		Config.SYSTEM_PROFILING = false;
	}

	/**
	 * Clear stats
	 */
	private void clear(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException, DatabaseException {
		ProfilingDAO.clear();
	}

	/**
	 * View stats
	 */
	private void view(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException, DatabaseException {
		log.debug("view({}, {})", request, response);

		// Query Statistics
		List<ProfilingStats> stats = ProfilingDAO.getStatistics();

		ServletContext sc = getServletContext();
		sc.setAttribute("stats", stats);
		sc.setAttribute("statsEnabled", Config.SYSTEM_PROFILING);
		sc.getRequestDispatcher("/admin/profiling_stats_view.jsp").forward(request, response);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_PROFILING_STATS_VIEW", null, null, null);

		log.debug("view: void");
	}

	/**
	 * List log
	 */
	private void list(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException, DatabaseException {
		log.debug("list({}, {})", request, response);
		String clazz = WebUtils.getString(request, "clazz");
		String method = WebUtils.getString(request, "method");

		// Query Statistics
		List<Profiling> list = ProfilingDAO.findByClazzMethod(clazz, method);

		ServletContext sc = getServletContext();
		sc.setAttribute("list", list);
		sc.setAttribute("clazz", clazz);
		sc.setAttribute("method", method);
		sc.getRequestDispatcher("/admin/profiling_stats_list.jsp").forward(request, response);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_PROFILING_STATS_LIST", null, null, null);

		log.debug("list: void");
	}
}
