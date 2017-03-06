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

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.extractor.RegisteredExtractors;
import com.openkm.extractor.TextExtractor;
import com.openkm.util.PathUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Mime type management servlet
 */
public class CheckTextExtractionServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(CheckTextExtractionServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		updateSessionManager(request);

		ServletContext sc = getServletContext();
		sc.setAttribute("repoPath", "/" + Repository.ROOT);
		sc.setAttribute("docUuid", null);
		sc.setAttribute("text", null);
		sc.setAttribute("time", null);
		sc.setAttribute("mimeType", null);
		sc.setAttribute("extractor", null);
		sc.getRequestDispatcher("/admin/check_text_extraction.jsp").forward(request, response);
	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		updateSessionManager(request);
		InputStream is = null;

		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				String docUuid = null;
				String repoPath = null;
				String text = null;
				String error = null;
				String mimeType = null;
				String extractor = null;

				for (Iterator<FileItem> it = items.iterator(); it.hasNext(); ) {
					FileItem item = it.next();

					if (item.isFormField()) {
						if (item.getFieldName().equals("docUuid")) {
							docUuid = item.getString("UTF-8");
						} else if (item.getFieldName().equals("repoPath")) {
							repoPath = item.getString("UTF-8");
						}
					} else {
						is = item.getInputStream();
						String name = FilenameUtils.getName(item.getName());
						mimeType = MimeTypeConfig.mimeTypes.getContentType(name.toLowerCase());

						if (!name.isEmpty() && item.getSize() > 0) {
							docUuid = null;
							repoPath = null;
						} else if (docUuid.isEmpty() && repoPath.isEmpty()) {
							mimeType = null;
						}
					}
				}

				if (docUuid != null && !docUuid.isEmpty()) {
					repoPath = OKMRepository.getInstance().getNodePath(null, docUuid);
				}

				if (repoPath != null && !repoPath.isEmpty()) {
					String name = PathUtils.getName(repoPath);
					mimeType = MimeTypeConfig.mimeTypes.getContentType(name.toLowerCase());
					is = OKMDocument.getInstance().getContent(null, repoPath, false);
				}

				long begin = System.currentTimeMillis();

				if (is != null) {
					if (!MimeTypeConfig.MIME_UNDEFINED.equals(mimeType)) {
						TextExtractor extClass = RegisteredExtractors.getTextExtractor(mimeType);

						if (extClass != null) {
							try {
								extractor = extClass.getClass().getCanonicalName();
								text = RegisteredExtractors.getText(mimeType, null, is);
							} catch (Exception e) {
								error = e.getMessage();
							}
						} else {
							extractor = "Undefined text extractor";
						}
					}
				}

				ServletContext sc = getServletContext();
				sc.setAttribute("docUuid", docUuid);
				sc.setAttribute("repoPath", repoPath);
				sc.setAttribute("text", text);
				sc.setAttribute("time", System.currentTimeMillis() - begin);
				sc.setAttribute("mimeType", mimeType);
				sc.setAttribute("error", error);
				sc.setAttribute("extractor", extractor);
				sc.getRequestDispatcher("/admin/check_text_extraction.jsp").forward(request, response);
			}
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} catch (FileUploadException e) {
			sendErrorRedirect(request, response, e);
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			sendErrorRedirect(request, response, e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
