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

package com.openkm.servlet;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Document;
import com.openkm.core.Config;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.util.FormatUtil;
import com.openkm.util.PathUtils;
import com.openkm.util.WebUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Download Servlet
 */
public class DownloadServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(DownloadServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		String userId = request.getRemoteUser();
		String path = WebUtils.getString(request, "path");
		String uuid = WebUtils.getString(request, "uuid");
		boolean inline = WebUtils.getBoolean(request, "inline");
		InputStream is = null;

		try {
			// Now an document can be located by UUID
			if (uuid != null && !uuid.isEmpty()) {
				path = OKMRepository.getInstance().getNodePath(null, uuid);
			} else if (path != null && !path.isEmpty()) {
				path = FormatUtil.sanitizeInput(path);
			}

			if (path != null) {
				Document doc = OKMDocument.getInstance().getProperties(null, path);
				String fileName = PathUtils.getName(doc.getPath());

				// Optinal append version to download ( not when doing checkout )
				if (Config.VERSION_APPEND_DOWNLOAD) {
					String versionToAppend = " rev " + OKMDocument.getInstance().getProperties(null, uuid).getActualVersion().getName();
					String[] nameParts = fileName.split("\\.(?=[^\\.]+$)");
					fileName = nameParts[0] + versionToAppend + "." + nameParts[1];
				}

				log.info("Download {} by {} ({})", new Object[]{path, userId, (inline ? "inline" : "attachment")});
				is = OKMDocument.getInstance().getContent(null, path, false);
				WebUtils.sendFile(request, response, fileName, doc.getMimeType(), inline, is, doc.getActualVersion().getSize());
			} else {
				response.setContentType("text/plain; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("Missing document reference");
				out.close();
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PathNotFoundException: " + e.getMessage());
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "RepositoryException: " + e.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
