/**
 *
 *
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 *
 * No bytes were intentionally harmed during the development of this application.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.servlet.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.OmrDAO;
import com.openkm.dao.bean.Omr;
import com.openkm.omr.OMRHelper;
import com.openkm.util.FileUtils;
import com.openkm.util.PropertyGroupUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * omr servlet
 */
public class OmrServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(OmrServlet.class);
	private static final int FILE_TEMPLATE = 1;
	private static final int FILE_ASC = 2;
	private static final int FILE_CONFIG = 3;
	private static final int FILE_FIELDS = 4;

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
			} else if (action.equals("downloadFile")) {
				downloadFile(userId, request, response);
			} else if (action.equals("editAsc")) {
				editAscFile(userId, request, response);
			} else if (action.equals("editFields")) {
				editFieldsFile(userId, request, response);
			} else if (action.equals("check")) {
				check(userId, request, response);
			} else {
				list(userId, request, response);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = "";
		String userId = request.getRemoteUser();
		updateSessionManager(request);

		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				String fileName = null;
				InputStream is = null;
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				Set<String> properties = new HashSet<String>();
				Omr om = new Omr();

				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();

					if (item.isFormField()) {
						if (item.getFieldName().equals("action")) {
							action = item.getString("UTF-8");
						} else if (item.getFieldName().equals("om_id")) {
							om.setId(Integer.parseInt(item.getString("UTF-8")));
						} else if (item.getFieldName().equals("om_name")) {
							om.setName(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("om_properties")) {
							properties.add(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("om_active")) {
							om.setActive(true);
						}
					} else {
						is = item.getInputStream();
						fileName = item.getName();
					}
				}

				om.setProperties(properties);

				if (action.equals("create") || action.equals("edit")) {
					// Store locally template file to be used later
					if (is != null && is.available() > 0) { // Case update only name
						byte[] data = IOUtils.toByteArray(is);
						File tmp = FileUtils.createTempFile();
						FileOutputStream fos = new FileOutputStream(tmp);
						IOUtils.write(data, fos);
						IOUtils.closeQuietly(fos);

						// Store template file
						om.setTemplateFileName(FilenameUtils.getName(fileName));
						om.setTemplateFileMime(MimeTypeConfig.mimeTypes.getContentType(fileName));
						om.setTemplateFilContent(data);
						IOUtils.closeQuietly(is);

						// Create training files
						Map<String, File> trainingMap = OMRHelper.trainingTemplate(tmp);
						File ascFile = trainingMap.get(OMRHelper.ASC_FILE);
						File configFile = trainingMap.get(OMRHelper.CONFIG_FILE);

						// Store asc file
						om.setAscFileName(om.getTemplateFileName() + ".asc");
						om.setAscFileMime(MimeTypeConfig.MIME_TEXT);
						is = new FileInputStream(ascFile);
						om.setAscFileContent(IOUtils.toByteArray(is));
						IOUtils.closeQuietly(is);

						// Store config file
						om.setConfigFileName(om.getTemplateFileName() + ".config");
						om.setConfigFileMime(MimeTypeConfig.MIME_TEXT);
						is = new FileInputStream(configFile);
						om.setConfigFileContent(IOUtils.toByteArray(is));
						IOUtils.closeQuietly(is);

						// Delete temporal files
						FileUtils.deleteQuietly(tmp);
						FileUtils.deleteQuietly(ascFile);
						FileUtils.deleteQuietly(configFile);
					}

					if (action.equals("create")) {
						long id = OmrDAO.getInstance().create(om);

						// Activity log
						UserActivity.log(userId, "ADMIN_OMR_CREATE", Long.toString(id), null, om.toString());
					} else if (action.equals("edit")) {
						OmrDAO.getInstance().updateTemplate(om);
						om = OmrDAO.getInstance().findByPk(om.getId());

						// Activity log
						UserActivity.log(userId, "ADMIN_OMR_EDIT", Long.toString(om.getId()), null, om.toString());
					}

					list(userId, request, response);
				} else if (action.equals("delete")) {
					OmrDAO.getInstance().delete(om.getId());

					// Activity log
					UserActivity.log(userId, "ADMIN_OMR_DELETE", Long.toString(om.getId()), null, null);
					list(userId, request, response);
				} else if (action.equals("editAsc")) {
					Omr omr = OmrDAO.getInstance().findByPk(om.getId());
					omr.setAscFileContent(IOUtils.toByteArray(is));
					omr.setAscFileMime(MimeTypeConfig.MIME_TEXT);
					omr.setAscFileName(omr.getTemplateFileName() + ".asc");
					OmrDAO.getInstance().update(omr);
					omr = OmrDAO.getInstance().findByPk(om.getId());
					IOUtils.closeQuietly(is);

					// Activity log
					UserActivity.log(userId, "ADMIN_OMR_EDIT_ASC", Long.toString(om.getId()), null, null);
					list(userId, request, response);
				} else if (action.equals("editFields")) {
					Omr omr = OmrDAO.getInstance().findByPk(om.getId());
					omr.setFieldsFileContent(IOUtils.toByteArray(is));
					omr.setFieldsFileMime(MimeTypeConfig.MIME_TEXT);
					omr.setFieldsFileName(omr.getTemplateFileName() + ".fields");
					OmrDAO.getInstance().update(omr);
					omr = OmrDAO.getInstance().findByPk(om.getId());
					IOUtils.closeQuietly(is);

					// Activity log
					UserActivity.log(userId, "ADMIN_OMR_EDIT_FIELDS", Long.toString(om.getId()), null, null);
					list(userId, request, response);
				} else if (action.equals("check")) {
					File form = FileUtils.createTempFile();
					OutputStream formFile = new FileOutputStream(form);
					formFile.write(IOUtils.toByteArray(is));
					IOUtils.closeQuietly(formFile);
					formFile.close();
					Map<String, String> results = OMRHelper.process(form, om.getId());
					FileUtils.deleteQuietly(form);
					IOUtils.closeQuietly(is);
					UserActivity.log(userId, "ADMIN_OMR_CHECK_TEMPLATE", Long.toString(om.getId()), null, null);
					results(userId, request, response, action, results, om.getId());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * List omr templates
	 */
	private void list(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException {
		log.debug("list({}, {}, {})", new Object[] { userId, request, response });
		ServletContext sc = getServletContext();
		List<Omr> list = OmrDAO.getInstance().findAll();
		sc.setAttribute("omr", list);
		sc.getRequestDispatcher("/admin/omr_list.jsp").forward(request, response);
		log.debug("list: void");
	}

	/**
	 * New omr template
	 */
	private void create(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException, ParseException, AccessDeniedException, RepositoryException {
		log.debug("create({}, {}, {})", new Object[] { userId, request, response });
		ServletContext sc = getServletContext();
		Omr om = new Omr();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("om", om);
		sc.setAttribute("pgprops", PropertyGroupUtils.getAllGroupsProperties());
		sc.getRequestDispatcher("/admin/omr_edit.jsp").forward(request, response);
		log.debug("create: void");
	}

	/**
	 * edit type record
	 */
	private void edit(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException, ParseException, AccessDeniedException, RepositoryException {
		log.debug("edit({}, {}, {})", new Object[] { userId, request, response });
		ServletContext sc = getServletContext();
		long omId = WebUtils.getLong(request, "om_id");
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("om", OmrDAO.getInstance().findByPk(omId));
		sc.setAttribute("pgprops", PropertyGroupUtils.getAllGroupsProperties());
		sc.getRequestDispatcher("/admin/omr_edit.jsp").forward(request, response);
		log.debug("edit: void");
	}

	/**
	 * delete type record
	 */
	private void delete(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException {
		log.debug("delete({}, {}, {})", new Object[] { userId, request, response });
		ServletContext sc = getServletContext();
		long omId = WebUtils.getLong(request, "om_id");
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("om", OmrDAO.getInstance().findByPk(omId));
		sc.getRequestDispatcher("/admin/omr_edit.jsp").forward(request, response);
		log.debug("delete: void");
	}

	/**
	 * download file
	 */
	private void downloadFile(String userId, HttpServletRequest request, HttpServletResponse response) throws DatabaseException,
			IOException {
		log.debug("downloadFile({}, {}, {})", new Object[] { userId, request, response });
		long omId = WebUtils.getLong(request, "om_id");
		int fileType = WebUtils.getInt(request, "type");
		Omr omr = OmrDAO.getInstance().findByPk(omId);

		if (omr != null && fileType >= FILE_TEMPLATE && fileType <= FILE_FIELDS) {
			OutputStream os = response.getOutputStream();

			try {
				byte[] fileContent = null;
				switch (fileType) {
					case FILE_TEMPLATE:
						fileContent = omr.getTemplateFileContent();
						WebUtils.prepareSendFile(request, response, omr.getTemplateFileName(), omr.getTemplateFileMime(), false);
						break;
					case FILE_ASC:
						fileContent = omr.getAscFileContent();
						WebUtils.prepareSendFile(request, response, omr.getAscFileName(), omr.getAscFileMime(), false);
						break;
					case FILE_CONFIG:
						fileContent = omr.getConfigFileContent();
						WebUtils.prepareSendFile(request, response, omr.getConfigFileName(), omr.getConfigFileMime(), false);
						break;
					case FILE_FIELDS:
						fileContent = omr.getFieldsFileContent();
						WebUtils.prepareSendFile(request, response, omr.getFieldsFileName(), omr.getFieldsFileMime(), false);
						break;
				}

				if (fileContent != null) {
					response.setContentLength(fileContent.length);
					os.write(fileContent);
					os.flush();
				}
			} finally {
				IOUtils.closeQuietly(os);
			}
		}

		log.debug("downloadFile: void");
	}

	/**
	 * editAscFile
	 */
	private void editAscFile(String userId, HttpServletRequest request, HttpServletResponse response) throws DatabaseException,
			ServletException, IOException {
		ServletContext sc = getServletContext();
		long omId = WebUtils.getLong(request, "om_id");
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("om", OmrDAO.getInstance().findByPk(omId));
		sc.getRequestDispatcher("/admin/omr_edit_asc.jsp").forward(request, response);
		log.debug("editAscFile: void");
	}

	/**
	 * editFieldsFile
	 */
	private void editFieldsFile(String userId, HttpServletRequest request, HttpServletResponse response) throws DatabaseException,
			ServletException, IOException {
		ServletContext sc = getServletContext();
		long omId = WebUtils.getLong(request, "om_id");
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("om", OmrDAO.getInstance().findByPk(omId));
		sc.getRequestDispatcher("/admin/omr_edit_fields.jsp").forward(request, response);
		log.debug("editFieldsFile: void");
	}

	/**
	 * check
	 */
	private void check(String userId, HttpServletRequest request, HttpServletResponse response) throws DatabaseException, ServletException,
			IOException {
		ServletContext sc = getServletContext();
		long omId = WebUtils.getLong(request, "om_id");
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("om", OmrDAO.getInstance().findByPk(omId));
		sc.setAttribute("results", null);
		sc.getRequestDispatcher("/admin/omr_check.jsp").forward(request, response);
		log.debug("check: void");
	}

	/**
	 * results
	 */
	private void results(String userId, HttpServletRequest request, HttpServletResponse response, String action,
						 Map<String, String> results, long omId) throws DatabaseException, ServletException, IOException {
		ServletContext sc = getServletContext();
		sc.setAttribute("action", action);
		sc.setAttribute("om", OmrDAO.getInstance().findByPk(omId));
		sc.setAttribute("results", results);
		sc.getRequestDispatcher("/admin/omr_check.jsp").forward(request, response);
		log.debug("check: void");
	}
}
