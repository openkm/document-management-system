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

import com.openkm.automation.AutomationException;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.kea.tree.KEATree;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Register thesaurus servlet
 */
public class RegisterThesaurusServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static final String[][] breadcrumb = new String[][]{
			new String[]{"experimental.jsp", "Experimental"},
	};

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		int level = (request.getParameter("level") != null && !request.getParameter("level").equals("")) ? Integer
				.parseInt(request.getParameter("level"))
				: 0;
		updateSessionManager(request);
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Register thesaurus", breadcrumb);
		out.flush();

		if (!Config.KEA_THESAURUS_OWL_FILE.equals("")) {
			out.println("<b>Starting thesaurus creation, this could take some hours.</b><br>");
			out.println("<b>Don't close this window meanwhile OpenKM is creating thesaurus.</b><br>");
			out.println("It'll be displayed creation information while creating nodes until level "
					+ (level + 1) + ", please be patient because tree deep level could be big.<br><br>");
			out.flush();

			try {
				KEATree.generateTree(null, level, "/" + Repository.THESAURUS, new Vector<String>(), out);
			} catch (PathNotFoundException e) {
				sendErrorRedirect(request, response, e);
			} catch (ItemExistsException e) {
				sendErrorRedirect(request, response, e);
			} catch (AccessDeniedException e) {
				sendErrorRedirect(request, response, e);
			} catch (RepositoryException e) {
				sendErrorRedirect(request, response, e);
			} catch (DatabaseException e) {
				sendErrorRedirect(request, response, e);
			} catch (ExtensionException e) {
				sendErrorRedirect(request, response, e);
			} catch (AutomationException e) {
				sendErrorRedirect(request, response, e);
			} catch (LockException e) {
				sendErrorRedirect(request, response, e);
			}

			out.println("<br><b>Finished thesaurus creation.</b><br>");
		} else {
			out.println("<b>Error - there's no thesaurus file defined in OpenKM.cfg</b>");
		}

		footer(out);
		out.flush();
		out.close();
	}
}
