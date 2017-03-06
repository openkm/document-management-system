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

import com.openkm.bean.HttpSessionInfo;
import com.openkm.core.HttpSessionManager;
import com.openkm.frontend.client.bean.GWTUINotification;
import com.openkm.servlet.frontend.UINotificationServlet;
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
 * Logged users servlet
 */
public class LoggedUsersServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(LoggedUsersServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		updateSessionManager(request);
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();

		if (action.equals("messageCreate")) {
			create(userId, request, response);
		} else if (action.equals("messageList")) {
			messageList(userId, request, response);
		} else if (action.equals("messageEdit")) {
			edit(userId, request, response);
		} else if (action.equals("messageDelete")) {
			delete(userId, request, response);
		}

		if (action.equals("")) {
			list(userId, request, response);
		} else if (WebUtils.getBoolean(request, "persist")) {
			messageList(userId, request, response);
		}
	}

	public void list(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext sc = getServletContext();
		List<HttpSessionInfo> sessions = HttpSessionManager.getInstance().getSessions();
		sc.setAttribute("sessions", sessions);
		sc.getRequestDispatcher("/admin/logged_users.jsp").forward(request, response);

		// Activity log
		UserActivity.log(userId, "ADMIN_LOGGED_USERS", null, null, null);
	}

	public void messageList(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext sc = getServletContext();
		sc.setAttribute("messages", UINotificationServlet.getAll());
		sc.getRequestDispatcher("/admin/message_list.jsp").forward(request, response);

		// Activity log
		UserActivity.log(userId, "ADMIN_GET_MESSAGES", null, null, null);
	}

	public void create(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (WebUtils.getBoolean(request, "persist")) {
			int action = WebUtils.getInt(request, "me_action");
			String message = WebUtils.getString(request, "me_message");
			int type = WebUtils.getInt(request, "me_type");
			boolean show = WebUtils.getBoolean(request, "me_show");
			UINotificationServlet.add(action, message, type, show);

			// Activity log
			UserActivity.log(userId, "ADMIN_ADD_MESSAGE", String.valueOf(action), null, message);
		} else {
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("me", new GWTUINotification());
			sc.getRequestDispatcher("/admin/message_edit.jsp").forward(request, response);
		}
	}

	public void edit(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (WebUtils.getBoolean(request, "persist")) {
			int id = WebUtils.getInt(request, "me_id");
			int action = WebUtils.getInt(request, "me_action");
			String message = WebUtils.getString(request, "me_message");
			int type = WebUtils.getInt(request, "me_type");
			boolean show = WebUtils.getBoolean(request, "me_show");
			GWTUINotification uin = UINotificationServlet.findById(id);
			uin.setAction(action);
			uin.setMessage(message);
			uin.setShow(show);
			uin.setType(type);

			// Activity log
			UserActivity.log(userId, "ADMIN_EDIT_MESSAGE", String.valueOf(action), null, message);
		} else {
			int id = WebUtils.getInt(request, "me_id");
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("me", UINotificationServlet.findById(id));
			sc.getRequestDispatcher("/admin/message_edit.jsp").forward(request, response);
		}
	}

	public void delete(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (WebUtils.getBoolean(request, "persist")) {
			int id = WebUtils.getInt(request, "me_id");
			UINotificationServlet.delete(id);

			// Activity log
			UserActivity.log(userId, "ADMIN_DELETE_MESSAGE", String.valueOf(id), null, null);
		} else {
			int id = WebUtils.getInt(request, "me_id");
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("me", UINotificationServlet.findById(id));
			sc.getRequestDispatcher("/admin/message_edit.jsp").forward(request, response);
		}
	}
}
