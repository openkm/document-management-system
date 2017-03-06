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

import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.ProfileDAO;
import com.openkm.dao.UserConfigDAO;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User config servlet
 */
public class UserConfigServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(UserConfigServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String userId = request.getRemoteUser();
		updateSessionManager(request);

		try {
			if (WebUtils.getBoolean(request, "persist")) {
				ServletContext sc = getServletContext();
				String ucUser = WebUtils.getString(request, "uc_user");
				int upId = WebUtils.getInt(request, "uc_profile");
				UserConfigDAO.updateProfile(ucUser, upId);
				sc.getRequestDispatcher("/admin/Auth").forward(request, response);

				// Activity log
				UserActivity.log(userId, "ADMIN_USER_CONFIG_EDIT", ucUser, null, Integer.toString(upId));
			} else {
				ServletContext sc = getServletContext();
				String ucUser = WebUtils.getString(request, "uc_user");
				sc.setAttribute("persist", true);
				sc.setAttribute("profiles", ProfileDAO.findAll(true));
				sc.setAttribute("uc", UserConfigDAO.findByPk(ucUser));
				sc.getRequestDispatcher("/admin/user_config_edit.jsp").forward(request, response);
			}
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}
}
