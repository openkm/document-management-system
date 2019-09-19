/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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
import com.openkm.automation.AutomationException;
import com.openkm.bean.ContentInfo;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.util.FileUtils;
import com.openkm.util.FormatUtil;
import com.openkm.util.WebUtils;
import com.openkm.util.impexp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Repository Export
 */
@WebServlet("/admin/Repository")
public class RepositoryServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RepositoryServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);
		process(request, response, action);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doPost({}, {})", request, response);
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);
		process(request, response, action);
	}

	private void process(HttpServletRequest request, HttpServletResponse response, String action) throws ServletException,
            IOException {
		try {
			if (!isMultipleInstancesAdmin(request)) {
				throw new AccessDeniedException("User not allowed to access");
			}

			if (action.equals("export")) {
				repositoryExport(request, response);
			} else if (action.equals("import")) {
				repositoryImport(request, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Repository Export
	 */
	private void repositoryExport(HttpServletRequest request, HttpServletResponse response) throws AccessDeniedException,
            RepositoryException, PathNotFoundException, DatabaseException, IOException, ParseException, NoSuchGroupException,
            MessagingException, ServletException {
		log.debug("repositoryExport({}, {})", request, response);
		String[][] breadcrumb = new String[][]{new String[]{"epository_export.jsp", "Repository export"}};

		if (isPost(request)) {
			request.setCharacterEncoding("UTF-8");
			String repoPath = WebUtils.getString(request, "repoPath", "/" + Repository.ROOT);
			String fsPath = WebUtils.getString(request, "fsPath");
			boolean metadata = WebUtils.getBoolean(request, "metadata");
			boolean history = WebUtils.getBoolean(request, "history");

			if (repoPath != null && !repoPath.isEmpty() && fsPath != null && !fsPath.isEmpty()) {
				if (!fsPath.startsWith(Config.WEBAPPS_DIR)) {
					if (fsPath.startsWith(Config.INSTANCE_CHROOT_PATH)) {
						File dir = new File(fsPath);
						ContentInfo cInfo = OKMFolder.getInstance().getContentInfo(null, repoPath);
						response.setContentType(MimeTypeConfig.MIME_HTML);

						PrintWriter out = response.getWriter();
						header(out, "Repository export", breadcrumb);
						out.println("<br/>");
						out.println("<b>Files & directories to export:</b> " + (cInfo.getDocuments() + cInfo.getFolders()) + "<br/>");

						long begin = System.currentTimeMillis();
						InfoDecorator deco = new HTMLInfoDecorator((int) cInfo.getDocuments() + (int) cInfo.getFolders());
						ImpExpStats stats = RepositoryExporter.exportDocuments(null, repoPath, dir, metadata, history, out, deco);
						long end = System.currentTimeMillis();

						out.println("<hr/>");
						out.println("<div class=\"ok\">Folder '" + repoPath + "' exported to '" + new File(fsPath).getAbsolutePath() + "'</div>");
						out.println("<br/>");
						out.println("<b>Documents:</b> " + stats.getDocuments() + "<br/>");
						out.println("<b>Folders:</b> " + stats.getFolders() + "<br/>");
						out.println("<b>Mails:</b> " + stats.getMails() + "<br/>");
						out.println("<b>Size:</b> " + FormatUtil.formatSize(stats.getSize()) + "<br/>");
						out.println("<b>Time:</b> " + FormatUtil.formatSeconds(end - begin) + "<br/>");
					} else {
						showExportForm(request, response);
					}
				} else {
					showExportForm(request, response);
				}
			} else {
				showExportForm(request, response);
			}
		} else {
			showExportForm(request, response);
		}

		log.debug("repositoryExport: void");
	}

	private void showExportForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext sc = getServletContext();
		sc.setAttribute("repoPath", "/" + Repository.ROOT);
		sc.setAttribute("fsPath", null);
		sc.setAttribute("history", false);
		sc.setAttribute("metadata", false);
		sc.setAttribute("cInfo", null);
		sc.setAttribute("stats", null);
		sc.getRequestDispatcher("/admin/repository_export.jsp").forward(request, response);
	}

	/**
	 * Repository Import
	 */
	private void repositoryImport(HttpServletRequest request, HttpServletResponse response) throws IOException, AutomationException,
            PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException, ServletException {
		log.debug("repositoryImport({}, {})", request, response);
		String[][] breadcrumb = new String[][]{new String[]{"repository_import.jsp", "Repository import"}};

		if (isPost(request)) {
			String repoPath = WebUtils.getString(request, "repoPath", "/" + Repository.ROOT);
			String fsPath = WebUtils.getString(request, "fsPath");
			boolean metadata = WebUtils.getBoolean(request, "metadata");
			boolean history = WebUtils.getBoolean(request, "history");
			boolean uuid = WebUtils.getBoolean(request, "uuid");

			if (repoPath != null && !repoPath.isEmpty() && fsPath != null && !fsPath.isEmpty()) {
				File dir = new File(fsPath);
				int files = FileUtils.countImportFiles(dir);
				response.setContentType(MimeTypeConfig.MIME_HTML);

				PrintWriter out = response.getWriter();
				header(out, "Repository export", breadcrumb);
				out.println("<br/>");
				out.println("<b>Files & directories to import:</b> " + files + "<br/>");

				long begin = System.currentTimeMillis();
				InfoDecorator deco = new HTMLInfoDecorator(files);
				ImpExpStats stats = RepositoryImporter.importDocuments(null, dir, repoPath, metadata, history, uuid, out, deco);
				long end = System.currentTimeMillis();

				out.println("<hr/>");
				out.println("<div class=\"ok\">Folder '" + repoPath + "' imported from '" + new File(fsPath).getAbsolutePath() + "'</div>");
				out.println("<br/>");
				out.println("<b>Documents:</b> " + stats.getDocuments() + "<br/>");
				out.println("<b>Folders:</b> " + stats.getFolders() + "<br/>");
				out.println("<b>Mails:</b> " + stats.getMails() + "<br/>");
				out.println("<b>Size:</b> " + FormatUtil.formatSize(stats.getSize()) + "<br/>");
				out.println("<b>Time:</b> " + FormatUtil.formatSeconds(end - begin) + "<br/>");
			} else {
				showImportForm(request, response);
			}
		} else {
			showImportForm(request, response);
		}

		log.debug("repositoryImport: void");
	}

	private void showImportForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext sc = getServletContext();
		sc.setAttribute("repoPath", "/" + Repository.ROOT);
		sc.setAttribute("fsPath", null);
		sc.setAttribute("history", false);
		sc.setAttribute("metadata", false);
		sc.setAttribute("uuid", false);
		sc.setAttribute("files", 0);
		sc.setAttribute("stats", null);
		sc.getRequestDispatcher("/admin/repository_import.jsp").forward(request, response);
	}
}
