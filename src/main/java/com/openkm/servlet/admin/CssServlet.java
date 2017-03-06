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

package com.openkm.servlet.admin;

import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.CssDAO;
import com.openkm.dao.bean.Css;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Css styles servlet
 */
public class CssServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(CssServlet.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();
		updateSessionManager(request);

		try {
			if (action.equals("create")) {
				create(userId, request, response);
			} else if (action.equals("edit")) {
				edit(userId, request, response);
			} else if (action.equals("delete")) {
				delete(userId, request, response);
			} else if (action.equals("download")) {
				download(userId, request, response);
			}

			if (action.equals("") || WebUtils.getBoolean(request, "persist")) {
				list(userId, request, response);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();
		updateSessionManager(request);

		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				Css css = new Css();
				css.setActive(false);

				for (Iterator<FileItem> it = items.iterator(); it.hasNext(); ) {
					FileItem item = it.next();

					if (item.isFormField()) {
						if (item.getFieldName().equals("action")) {
							action = item.getString("UTF-8");
						} else if (item.getFieldName().equals("css_id")) {
							if (!item.getString("UTF-8").isEmpty()) {
								css.setId(new Long(item.getString("UTF-8")).longValue());
							}
						} else if (item.getFieldName().equals("css_name")) {
							css.setName(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("css_context")) {
							css.setContext(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("css_content")) {
							css.setContent(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("css_active")) {
							css.setActive(true);
						}
					}
				}

				if (action.equals("edit")) {
					CssDAO.getInstance().update(css);

					// Activity log
					UserActivity.log(userId, "ADMIN_CSS_UPDATE", String.valueOf(css.getId()), null, css.getName());
				} else if (action.equals("delete")) {
					String name = WebUtils.getString(request, "css_name");
					CssDAO.getInstance().delete(css.getId());

					// Activity log
					UserActivity.log(userId, "ADMIN_CSS_DELETE", String.valueOf(css.getId()), null, name);
				} else if (action.equals("create")) {
					long id = CssDAO.getInstance().create(css);

					// Activity log
					UserActivity.log(userId, "ADMIN_CSS_CREATE", String.valueOf(id), null, css.getName());
				}
			}

			list(userId, request, response);
		} catch (FileUploadException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Download CSS
	 */
	private void download(String userId, HttpServletRequest request, HttpServletResponse response) throws DatabaseException, IOException {
		log.debug("download({}, {}, {})", new Object[]{userId, request, response});
		long id = WebUtils.getLong(request, "css_id");
		Css css = CssDAO.getInstance().findByPk(id);
		ByteArrayInputStream bais = null;

		try {
			bais = new ByteArrayInputStream(css.getContent().getBytes("UTF-8"));
			WebUtils.sendFile(request, response, css.getName() + ".css", MimeTypeConfig.MIME_CSS, false, bais);
		} finally {
			IOUtils.closeQuietly(bais);
		}

		log.debug("download: void");
	}

	/**
	 * Delete CSS
	 */
	private void delete(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("delete({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		long id = WebUtils.getLong(request, "css_id");

		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("css", CssDAO.getInstance().findByPk(id));
		sc.getRequestDispatcher("/admin/css_edit.jsp").forward(request, response);

		log.debug("edit: void");
	}

	/**
	 * Edit CSS
	 */
	private void edit(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("edit({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		long id = WebUtils.getLong(request, "css_id");

		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("css", CssDAO.getInstance().findByPk(id));
		sc.getRequestDispatcher("/admin/css_edit.jsp").forward(request, response);

		log.debug("edit: void");
	}

	/**
	 * Create CSS
	 */
	private void create(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("create({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();

		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("css", null);
		sc.getRequestDispatcher("/admin/css_edit.jsp").forward(request, response);

		log.debug("create: void");
	}

	/**
	 * List CSS
	 */
	private void list(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		sc.setAttribute("cssList", CssDAO.getInstance().findAll());
		sc.getRequestDispatcher("/admin/css_list.jsp").forward(request, response);
		log.debug("list: void");
	}
}