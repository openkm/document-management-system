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

import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.core.*;
import com.openkm.module.db.DbRepositoryModule;
import com.openkm.util.FormUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Property groups servlet
 *
 * @author Paco Avila
 */
public class PropertyGroupsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(PropertyGroupsServlet.class);

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("edit")) {
				edit(request, response);
			}

			list(request, response);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("register")) {
				register(request, response);
			} else if (action.equals("edit")) {
				edit(request, response);
			}

			if (action.equals("") || action.equals("register")) {
				list(request, response);
			}
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Register property group
	 */
	private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			ParseException, DatabaseException {
		log.debug("register({}, {})", new Object[]{request, response});

		// If it is ok, register it
		FileInputStream fis = null;

		try {
			if (Config.REPOSITORY_NATIVE) {
				DbRepositoryModule.registerPropertyGroups(Config.PROPERTY_GROUPS_XML);
			} else {
				// Other implementation
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_PROPERTY_GROUP_REGISTER", null, null, Config.PROPERTY_GROUPS_CND);
		log.debug("register: void");
	}

	/**
	 * List property groups
	 */
	private void list(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, ParseException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("list({}, {})", new Object[]{request, response});
		ServletContext sc = getServletContext();
		FormUtils.resetPropertyGroupsForms();
		OKMPropertyGroup okmPropGroups = OKMPropertyGroup.getInstance();
		List<PropertyGroup> groups = okmPropGroups.getAllGroups(null);
		Map<PropertyGroup, List<Map<String, String>>> pGroups = new LinkedHashMap<PropertyGroup, List<Map<String, String>>>();

		for (PropertyGroup group : groups) {
			List<FormElement> mData = okmPropGroups.getPropertyGroupForm(null, group.getName());
			List<Map<String, String>> fMaps = new ArrayList<Map<String, String>>();

			for (FormElement fe : mData) {
				fMaps.add(FormUtils.toString(fe));
			}

			pGroups.put(group, fMaps);
		}

		sc.setAttribute("pGroups", pGroups);
		sc.getRequestDispatcher("/admin/property_groups_list.jsp").forward(request, response);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_PROPERTY_GROUP_LIST", null, null, null);
		log.debug("list: void");
	}

	/**
	 * Edit property groups
	 */
	private void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		log.debug("edit({}, {})", new Object[]{request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			String definition = request.getParameter("definition");
			FileUtils.writeStringToFile(new File(Config.PROPERTY_GROUPS_XML), definition, "UTF-8");

			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_PROPERTY_GROUP_EDIT", null, null, null);
		} else {
			String xml = FileUtils.readFileToString(new File(Config.PROPERTY_GROUPS_XML), "UTF-8");
			ServletContext sc = getServletContext();
			sc.setAttribute("persist", true);
			sc.setAttribute("action", "edit");
			sc.setAttribute("definition", xml.replace("&", "&amp;"));
			sc.getRequestDispatcher("/admin/property_groups_edit.jsp").forward(request, response);
		}

		log.debug("edit: void");
	}
}
