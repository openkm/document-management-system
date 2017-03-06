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
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.servlet.admin.BaseServlet;
import com.openkm.util.WebUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Html Preview Servlet
 *
 * Take a look at available brushes alias at http://alexgorbatchev.com/SyntaxHighlighter/manual/brushes/
 */
public class HtmlPreviewServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(HtmlPreviewServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String uuid = WebUtils.getString(request, "uuid");
		String core = WebUtils.getString(request, "core");
		String theme = WebUtils.getString(request, "theme");
		InputStream fis = null;

		try {
			fis = OKMDocument.getInstance().getContent(null, uuid, false);
			StringWriter writer = new StringWriter();
			IOUtils.copy(fis, writer, "UTF-8");
			String content = writer.getBuffer().toString();
			content = content.replaceAll("jsOpenPathByUuid", "parent.jsOpenPathByUuid");

			ServletContext sc = getServletContext();
			sc.setAttribute("cssCore", core);
			sc.setAttribute("cssTheme", theme);
			sc.setAttribute("content", content);
			sc.getRequestDispatcher("/html_preview.jsp").forward(request, response);
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
}
