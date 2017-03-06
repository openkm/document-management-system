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

package com.openkm.servlet;

import com.openkm.api.OKMSearch;
import com.openkm.bean.QueryResult;
import com.openkm.bean.Repository;
import com.openkm.core.Config;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.bean.Activity;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Status Servlet
 */
public class StatusServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(StatusServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String action = request.getPathInfo();
		org.hibernate.Session dbSession = null;
		log.debug("action: {}", action);

		try {
			String user = PrincipalUtils.getUser();
			dbSession = HibernateUtil.getSessionFactory().openSession();

			// Check database
			checkDatabase(dbSession);

			// Check repository
			checkRepository();

			response.setContentType("text/plain; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("OK");
			out.close();

			// Activity log
			UserActivity.log(user, "MISC_STATUS", null, null, "OK");
		} catch (Exception e) {
			// Activity log
			UserActivity.log(request.getRemoteUser(), "MISC_STATUS", null, null, e.getMessage());
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			HibernateUtil.close(dbSession);
		}
	}

	/**
	 * Check database connection
	 */
	@SuppressWarnings("unchecked")
	private void checkDatabase(org.hibernate.Session session) throws Exception {
		String qs = "from Activity where action='MISC_STATUS'";
		org.hibernate.Query q = session.createQuery(qs);
		List<Activity> ret = q.list();

		for (Activity act : ret) {
			String txt = act.toString();
			log.debug("checkDatabase: {}", txt);
		}
	}

	/**
	 * Check repository connection
	 */
	private void checkRepository() throws Exception {
		String token = DbSessionManager.getInstance().getSystemToken();
		QueryParams params = new QueryParams();
		params.setAuthor(Config.ADMIN_USER);
		params.setPath("/" + Repository.ROOT);
		List<QueryResult> results = OKMSearch.getInstance().find(token, params);

		for (QueryResult qr : results) {
			log.info("Result: {}", qr);
		}
	}
}
