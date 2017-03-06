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

import com.openkm.api.OKMFolder;
import com.openkm.bean.ContentInfo;
import com.openkm.bean.Repository;
import com.openkm.core.Config;
import com.openkm.core.MimeTypeConfig;
import com.openkm.util.FormatUtil;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import com.openkm.util.impexp.DbRepositoryChecker;
import com.openkm.util.impexp.HTMLInfoDecorator;
import com.openkm.util.impexp.ImpExpStats;
import com.openkm.util.impexp.InfoDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Repository checker servlet
 */
public class RepositoryCheckerServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(RepositoryCheckerServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String[][] breadcrumb = new String[][]{new String[]{"utilities.jsp", "Utilities"},};

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
		String repoPath = WebUtils.getString(request, "repoPath", "/" + Repository.ROOT);
		boolean fast = WebUtils.getBoolean(request, "fast");
		boolean versions = WebUtils.getBoolean(request, "versions");
		boolean checksum = WebUtils.getBoolean(request, "checksum");
		updateSessionManager(request);
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Repository checker", breadcrumb);
		out.flush();

		try {
			if (!repoPath.equals("")) {
				out.println("<ul>");

				// Calculate number of nodes
				out.println("<li>Calculate number of nodes</li>");
				out.flush();
				response.flushBuffer();
				log.debug("Calculate number of nodes");

				ContentInfo cInfo = OKMFolder.getInstance().getContentInfo(null, repoPath);

				out.println("<li>Documents: " + cInfo.getDocuments() + "</li>");
				out.println("<li>Folders: " + cInfo.getFolders() + "</li>");
				out.println("<li>Checking repository integrity</li>");
				out.flush();
				response.flushBuffer();
				log.debug("Checking repository integrity");

				long begin = System.currentTimeMillis();
				ImpExpStats stats = null;

				if (Config.REPOSITORY_NATIVE) {
					InfoDecorator id = new HTMLInfoDecorator((int) cInfo.getDocuments());
					stats = DbRepositoryChecker.checkDocuments(null, repoPath, fast, versions, checksum, out, id);
				} else {
					// Other implementation
				}

				long end = System.currentTimeMillis();

				// Finalized
				out.println("<li>Repository check completed!</li>");
				out.println("</ul>");
				out.flush();
				log.debug("Repository check completed!");

				out.println("<hr/>");
				out.println("<div class=\"ok\">Path: " + repoPath + "</div>");
				out.println("<div class=\"ok\">Fast: " + fast + "</div>");
				out.println("<div class=\"ok\">Versions: " + versions + "</div>");

				if (Config.REPOSITORY_NATIVE && Config.REPOSITORY_CONTENT_CHECKSUM) {
					out.println("<div class=\"ok\">Checkum: " + checksum + "</div>");
				} else {
					out.println("<div class=\"warn\">Checkum: disabled</div>");
				}

				out.println("<br/>");
				out.println("<b>Documents:</b> " + stats.getDocuments() + "<br/>");
				out.println("<b>Folders:</b> " + stats.getFolders() + "<br/>");
				out.println("<b>Size:</b> " + FormatUtil.formatSize(stats.getSize()) + "<br/>");
				out.println("<b>Time:</b> " + FormatUtil.formatSeconds(end - begin) + "<br/>");

				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_REPOSITORY_CHECKER", null, null, "Documents: " + stats.getDocuments()
						+ ", Folders: " + stats.getFolders() + ", Size: " + FormatUtil.formatSize(stats.getSize()) + ", Time: "
						+ FormatUtil.formatSeconds(end - begin));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			footer(out);
			out.flush();
			out.close();
		}
	}
}
