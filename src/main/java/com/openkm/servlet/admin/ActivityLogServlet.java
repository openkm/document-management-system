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

import com.openkm.api.OKMAuth;
import com.openkm.core.DatabaseException;
import com.openkm.dao.ActivityDAO;
import com.openkm.dao.bean.ActivityFilter;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Activity log servlet
 */
public class ActivityLogServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ActivityLogServlet.class);
	String actions[] = {
			"Auth",
			"LOGIN", "LOGOUT", "SESSION_EXPIRATION",
			"GRANT_USER", "REVOKE_USER", "GRANT_ROLE", "REVOKE_ROLE",

			//---------------------------------
			"Document",
			"CANCEL_DOCUMENT_CHECKOUT", "CHECKIN_DOCUMENT", "CHECKOUT_DOCUMENT", "CREATE_DOCUMENT",
			"DELETE_DOCUMENT", "GET_CHILDREN_DOCUMENTS", "GET_DOCUMENT_CONTENT",
			"GET_DOCUMENT_CONTENT_BY_VERSION", "GET_DOCUMENT_PROPERTIES",
			"GET_DOCUMENT_VERSION_HISTORY", "GET_PROPERTY_GROUP_PROPERTIES",
			"LOCK_DOCUMENT", "MOVE_DOCUMENT", "PURGE_DOCUMENT", "RENAME_DOCUMENT",
			"SET_DOCUMENT_PROPERTIES", "UNLOCK_DOCUMENT",

			//---------------------------------
			"Folder",
			"COPY_FOLDER", "CREATE_FOLDER", "DELETE_FOLDER", "GET_CHILDREN_FOLDERS",
			"GET_FOLDER_CONTENT_INFO", "GET_FOLDER_PROPERTIES", "MOVE_FOLDER", "PURGE_FOLDER",
			"RENAME_FOLDER",

			//---------------------------------
			"Mail",
			"CREATE_MAIL", "GET_MAIL_PROPERTIES", "DELETE_MAIL", "PURGE_MAIL", "RENAME_MAIL",
			"MOVE_MAIL", "COPY_MAIL", "GET_CHILDREN_MAILS",

			//---------------------------------
			"Repository",
			"PURGE_TRASH",

			//---------------------------------
			"Admin",
			"ADMIN_ACTIVITY_LOG", "ADMIN_ACTIVE_SESSIONS",
			"ADMIN_USER_CREATE", "ADMIN_USER_EDIT", "ADMIN_USER_DELETE", "ADMIN_USER_ACTIVE",
			"ADMIN_ROLE_CREATE", "ADMIN_ROLE_EDIT", "ADMIN_ROLE_DELETE", "ADMIN_ROLE_ACTIVE",
			"ADMIN_CHECK_EMAIL",
			"ADMIN_CONFIG_CREATE", "ADMIN_CONFIG_EDIT", "ADMIN_CONFIG_DELETE",
			"ADMIN_CRONTAB_CREATE", "ADMIN_CRONTAB_EDIT", "ADMIN_CRONTAB_DELETE", "ADMIN_CRONTAB_EXECUTE",
			"ADMIN_DATABASE_QUERY", "ADMIN_DATABASE_UPDATE",
			"ADMIN_LANGUAGE_CREATE", "ADMIN_LANGUAGE_EDIT", "ADMIN_LANGUAGE_DELETE", "ADMIN_LANGUAGE_IMPORT",
			"ADMIN_LOGCAT_LIST", "ADMIN_LOGCAT_VIEW",
			"ADMIN_LOGGED_USERS",
			"ADMIN_MAIL_ACCOUNT_CREATE", "ADMIN_MAIL_ACCOUNT_EDIT", "ADMIN_MAIL_ACCOUNT_DELETE", "ADMIN_MAIL_ACCOUNT_CHECK",
			"ADMIN_MAIL_FILTER_CREATE", "ADMIN_MAIL_FILTER_EDIT", "ADMIN_MAIL_FILTER_DELETE",
			"ADMIN_MAIL_FILTER_RULE_CREATE", "ADMIN_MAIL_FILTER_RULE_EDIT", "ADMIN_MAIL_FILTER_RULE_DELETE",
			"ADMIN_MIME_TYPE_CREATE", "ADMIN_MIME_TYPE_EDIT", "ADMIN_MIME_TYPE_DELETE",
			"ADMIN_USER_PROFILE_CREATE", "ADMIN_USER_PROFILE_EDIT", "ADMIN_USER_PROFILE_DELETE",
			"ADMIN_PROPERTY_GROUP_REGISTER", "ADMIN_PROPERTY_GROUP_LIST",
			"ADMIN_REPORT_CREATE", "ADMIN_REPORT_EDIT", "ADMIN_REPORT_DELETE", "ADMIN_REPORT_EXECUTE",
			"ADMIN_REPOSITORY_SEARCH", "ADMIN_REPOSITORY_REINDEX",
			"ADMIN_REPOSITORY_UNLOCK", "ADMIN_REPOSITORY_CHECKIN",
			"ADMIN_REPOSITORY_EDIT", "ADMIN_REPOSITORY_SAVE", "ADMIN_REPOSITORY_LIST",
			"ADMIN_REPOSITORY_REMOVE_CONTENT", "ADMIN_REPOSITORY_REMOVE_CURRENT", "ADMIN_REPOSITORY_REMOVE_MIXIN",
			"ADMIN_WORKFLOW_REGISTER",
			"ADMIN_PROCESS_DEFINITION_DELETE",
			"ADMIN_PROCESS_INSTANCE_DELETE", "ADMIN_PROCESS_INSTANCE_END", "ADMIN_PROCESS_INSTANCE_RESUME",
			"ADMIN_PROCESS_INSTANCE_SUSPEND", "ADMIN_PROCESS_INSTANCE_ADD_COMMENT",
			"ADMIN_PROCESS_INSTANCE_VARIABLE_DELETE", "ADMIN_PROCESS_INSTANCE_VARIABLE_ADD",
			"ADMIN_TASK_INSTANCE_SET_ACTOR", "ADMIN_TASK_INSTANCE_START", "ADMIN_TASK_INSTANCE_END",
			"ADMIN_TASK_INSTANCE_SUSPEND", "ADMIN_TASK_INSTANCE_ADD_COMMENT",
			"ADMIN_TASK_INSTANCE_VARIABLE_DELETE", "ADMIN_TASK_INSTANCE_VARIABLE_ADD",
			"ADMIN_TASK_INSTANCE_RESUME", "ADMIN_TOKEN_SUSPEND", "ADMIN_TOKEN_RESUME", "ADMIN_TOKEN_END",
			"ADMIN_TOKEN_SET_NODE", "ADMIN_TOKEN_SIGNAL",
			"ADMIN_STAMP_IMAGE_CREATE", "ADMIN_STAMP_IMAGE_EDIT", "ADMIN_STAMP_IMAGE_DELETE", "ADMIN_STAMP_IMAGE_ACTIVE",
			"ADMIN_STAMP_TEXT_CREATE", "ADMIN_STAMP_TEXT_EDIT", "ADMIN_STAMP_TEXT_DELETE", "ADMIN_STAMP_TEXT_ACTIVE",
			"ADMIN_TWITTER_ACCOUNT_CREATE", "ADMIN_TWITTER_ACCOUNT_EDIT", "ADMIN_TWITTER_ACCOUNT_DELETE",
			"ADMIN_USER_CONFIG_EDIT", "ADMIN_SCRIPTING",
			"ADMIN_OMR_CREATE", "ADMIN_OMR_EDIT", "ADMIN_OMR_DELETE", "ADMIN_OMR_EXECUTE", "ADMIN_OMR_CHECK_TEMPLATE",
			
			//---------------------------------
			"Misc",
			"MISC_OPENKM_START", "MISC_OPENKM_STOP",
			"MISC_STATUS", "MISC_TEXT_EXTRACTION_FAILURE"
	};

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		ServletContext sc = getServletContext();
		request.setCharacterEncoding("UTF-8");
		String dbegin = WebUtils.getString(request, "dbegin");
		String dend = WebUtils.getString(request, "dend");
		String user = WebUtils.getString(request, "user");
		String action = WebUtils.getString(request, "action");
		String item = WebUtils.getString(request, "item");

		try {
			if (!dbegin.equals("") && !dend.equals("")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				ActivityFilter filter = new ActivityFilter();
				Calendar begin = Calendar.getInstance();
				begin.setTime(sdf.parse(dbegin));
				begin.set(Calendar.HOUR, 0);
				begin.set(Calendar.MINUTE, 0);
				begin.set(Calendar.SECOND, 0);
				begin.set(Calendar.MILLISECOND, 0);
				filter.setBegin(begin);
				Calendar end = Calendar.getInstance();
				end.setTime(sdf.parse(dend));
				end.add(Calendar.DAY_OF_MONTH, 1);
				end.set(Calendar.HOUR, 0);
				end.set(Calendar.MINUTE, 0);
				end.set(Calendar.SECOND, 0);
				end.set(Calendar.MILLISECOND, 0);
				filter.setEnd(end);
				filter.setUser(user);
				filter.setAction(action);
				filter.setItem(item);
				sc.setAttribute("results", ActivityDAO.findByFilter(filter));

				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_ACTIVITY_LOG", null, null, filter.toString());
			} else {
				sc.setAttribute("results", null);
			}

			sc.setAttribute("dbeginFilter", dbegin);
			sc.setAttribute("dendFilter", dend);
			sc.setAttribute("userFilter", user);
			sc.setAttribute("actionFilter", action);
			sc.setAttribute("itemFilter", item);
			sc.setAttribute("actions", actions);
			sc.setAttribute("users", OKMAuth.getInstance().getUsers(null));
			sc.getRequestDispatcher("/admin/activity_log.jsp").forward(request, response);
		} catch (ParseException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} catch (PrincipalAdapterException e) {
			sendErrorRedirect(request, response, e);
		}
	}
}
