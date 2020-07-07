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

package com.openkm.extension.servlet;

import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.core.*;
import com.openkm.extension.dao.ZohoTokenDAO;
import com.openkm.extension.dao.bean.ZohoToken;
import com.openkm.module.db.DbDocumentModule;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.servlet.frontend.OKMHttpServlet;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * ZohoFileUploadServlet
 *
 * @author jllort
 */
public class ZohoFileUploadServlet extends OKMHttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ZohoFileUploadServlet.class);

	@SuppressWarnings("unchecked")
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		log.info("service({}, {})", request, response);
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		InputStream is = null;
		String id = "";

		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");

			// Parse the request and get all parameters and the uploaded file
			List<FileItem> items;

			try {
				items = upload.parseRequest(request);
				for (FileItem item : items) {
					if (item.isFormField()) {
						// if (item.getFieldName().equals("format")) { format =
						// item.getString("UTF-8"); }
						if (item.getFieldName().equals("id")) {
							id = item.getString("UTF-8");
						}
						// if (item.getFieldName().equals("filename")) {
						// filename = item.getString("UTF-8"); }
					} else {
						is = item.getInputStream();
					}
				}

				// Retrieve token
				ZohoToken zot = ZohoTokenDAO.findByPk(id);

				// Save document
				String sysToken = DbSessionManager.getInstance().getSystemToken();
				String path = OKMRepository.getInstance().getNodePath(sysToken, zot.getNode());
				new DbDocumentModule().checkout(sysToken, path, zot.getUser());
				new DbDocumentModule().checkin(sysToken, path, is, "Modified from Zoho", zot.getUser());
			} catch (FileUploadException | PathNotFoundException | RepositoryException | DatabaseException
					| FileSizeExceededException | UserQuotaExceededException | VirusDetectedException | VersionException
					| LockException | AccessDeniedException | AutomationException e) {
				log.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
