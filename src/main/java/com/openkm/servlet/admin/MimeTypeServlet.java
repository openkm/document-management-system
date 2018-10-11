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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.MimeTypeDAO;
import com.openkm.dao.bean.MimeType;
import com.openkm.servlet.admin.DatabaseQueryServlet.WorkerUpdate;
import com.openkm.util.SecureStore;
import com.openkm.util.UserActivity;
import com.openkm.util.WarUtils;
import com.openkm.util.WebUtils;

/**
 * Mime type management servlet
 */
public class MimeTypeServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(MimeTypeServlet.class);

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			} else if (action.equals("export")) {
				export(userId, request, response);
			} else {
				list(userId, request, response);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();
		Session dbSession = null;
		updateSessionManager(request);

		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				InputStream is = null;
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				MimeType mt = new MimeType();
				byte data[] = null;

				for (Iterator<FileItem> it = items.iterator(); it.hasNext(); ) {
					FileItem item = it.next();

					if (item.isFormField()) {
						if (item.getFieldName().equals("action")) {
							action = item.getString("UTF-8");
						} else if (item.getFieldName().equals("mt_id")) {
							mt.setId(Integer.parseInt(item.getString("UTF-8")));
						} else if (item.getFieldName().equals("mt_name")) {
							mt.setName(item.getString("UTF-8").toLowerCase());
						} else if (item.getFieldName().equals("mt_description")) {
							mt.setDescription(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("mt_search")) {
							mt.setSearch(true);
						} else if (item.getFieldName().equals("mt_extensions")) {
							String[] extensions = item.getString("UTF-8").split(" ");

							for (int i = 0; i < extensions.length; i++) {
								mt.getExtensions().add(extensions[i].toLowerCase());
							}
						}
					} else {
						is = item.getInputStream();
						data = IOUtils.toByteArray(is);
						mt.setImageMime(MimeTypeConfig.mimeTypes.getContentType(item.getName()));
						is.close();
					}
				}

				if (action.equals("create")) {
					// Because this servlet is also used for SQL import and in that case I don't
					// want to waste a b64Encode conversion. Call it a sort of optimization.
					mt.setImageContent(SecureStore.b64Encode(data));
					long id = MimeTypeDAO.create(mt);
					MimeTypeConfig.loadMimeTypes();

					// Activity log
					UserActivity.log(userId, "ADMIN_MIME_TYPE_CREATE", Long.toString(id), null, mt.toString());
					list(userId, request, response);
				} else if (action.equals("edit")) {
					// Because this servlet is also used for SQL import and in that case I don't
					// want to waste a b64Encode conversion. Call it a sort of optimization.
					mt.setImageContent(SecureStore.b64Encode(data));
					MimeTypeDAO.update(mt);
					MimeTypeConfig.loadMimeTypes();

					// Activity log
					UserActivity.log(userId, "ADMIN_MIME_TYPE_EDIT", Long.toString(mt.getId()), null, mt.toString());
					list(userId, request, response);
				} else if (action.equals("delete")) {
					MimeTypeDAO.delete(mt.getId());
					MimeTypeConfig.loadMimeTypes();

					// Activity log
					UserActivity.log(userId, "ADMIN_MIME_TYPE_DELETE", Long.toString(mt.getId()), null, null);
					list(userId, request, response);
				} else if (action.equals("import")) {
					dbSession = HibernateUtil.getSessionFactory().openSession();
					importMimeTypes(userId, request, response, data, dbSession);
					list(userId, request, response);
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (FileUploadException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} finally {
			HibernateUtil.close(dbSession);
		}
	}

	/**
	 * List registered mime types
	 */
	private void list(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		sc.setAttribute("mimeTypes", MimeTypeDAO.findAll("mt.name"));
		sc.getRequestDispatcher("/admin/mime_list.jsp").forward(request, response);
		log.debug("list: void");
	}

	/**
	 * Delete mime type
	 */
	private void delete(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("delete({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		int mtId = WebUtils.getInt(request, "mt_id");
		MimeType mt = MimeTypeDAO.findByPk(mtId);
		String extensions = "";

		for (String ext : mt.getExtensions()) {
			extensions += ext + " ";
		}

		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("extensions", extensions.trim());
		sc.setAttribute("mt", mt);
		sc.getRequestDispatcher("/admin/mime_edit.jsp").forward(request, response);
		log.debug("delete: void");
	}

	/**
	 * Create mime type
	 */
	private void create(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("create({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		MimeType mt = new MimeType();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("extensions", null);
		sc.setAttribute("mt", mt);
		sc.getRequestDispatcher("/admin/mime_edit.jsp").forward(request, response);
		log.debug("create: void");
	}

	/**
	 * Edit mime type
	 */
	private void edit(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("edit({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		int mtId = WebUtils.getInt(request, "mt_id");
		MimeType mt = MimeTypeDAO.findByPk(mtId);
		String extensions = "";

		for (String ext : mt.getExtensions()) {
			extensions += ext + " ";
		}

		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("extensions", extensions.trim());
		sc.setAttribute("mt", mt);
		sc.getRequestDispatcher("/admin/mime_edit.jsp").forward(request, response);
		log.debug("edit: void");
	}

	/**
	 * Export mime types
	 */
	private void export(String userId, HttpServletRequest request, HttpServletResponse response) throws DatabaseException, IOException {
		log.debug("export({}, {}, {})", new Object[]{userId, request, response});

		// Disable browser cache
		response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
		response.setHeader("Cache-Control", "max-age=0, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		String fileName = "OpenKM_" + WarUtils.getAppVersion().getVersion() + "_MimeTypes.sql";

		response.setHeader("Content-disposition", "inline; filename=\"" + fileName + "\"");
		response.setContentType("text/x-sql; charset=UTF-8");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
		out.println("DELETE FROM OKM_MIME_TYPE_EXTENSION;");
		out.println("DELETE FROM OKM_MIME_TYPE;");

		for (MimeType mimeType : MimeTypeDAO.findAll("mt.id")) {
			StringBuffer insertMime = new StringBuffer("INSERT INTO OKM_MIME_TYPE (MT_ID, MT_NAME, MT_DESCRIPTION, MT_IMAGE_CONTENT, MT_IMAGE_MIME, MT_SEARCH) VALUES (");
			insertMime.append(mimeType.getId()).append(", '");
			insertMime.append(mimeType.getName()).append("', '");
			insertMime.append(mimeType.getDescription()).append("', '");
			insertMime.append(mimeType.getImageContent()).append("', '");
			insertMime.append(mimeType.getImageMime()).append("', '");
			if(mimeType.isSearch()) {
			    insertMime.append('T').append("');");
			} else {
			    insertMime.append('F').append("');");
			}
			out.println(insertMime);

			for (String ext : mimeType.getExtensions()) {
				StringBuffer insertExtension = new StringBuffer("INSERT INTO OKM_MIME_TYPE_EXTENSION (MTE_ID, MTE_NAME) VALUES (");
				insertExtension.append(mimeType.getId()).append(", '");
				insertExtension.append(ext).append("');");
				out.println(insertExtension);
			}
		}
		out.flush();
		log.debug("export: sql-file");
	}

	/**
	 * Import mime types into database
	 */
	private void importMimeTypes(String userId, HttpServletRequest request, HttpServletResponse response,
	                             final byte[] data, Session dbSession) throws DatabaseException,
			IOException, SQLException {
		log.debug("import({}, {}, {}, {}, {})", new Object[]{userId, request, response, data, dbSession});

        WorkerUpdate worker = new DatabaseQueryServlet().new WorkerUpdate();
        worker.setData(data);
        dbSession.doWork(worker);
        log.debug("importMimeTypes: void");
	}
}
