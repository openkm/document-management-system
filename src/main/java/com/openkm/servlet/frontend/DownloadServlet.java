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

package com.openkm.servlet.frontend;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMMail;
import com.openkm.api.OKMRepository;
import com.openkm.api.OKMSearch;
import com.openkm.bean.Document;
import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.bean.Version;
import com.openkm.core.*;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.util.*;
import com.openkm.util.impexp.RepositoryExporter;
import com.openkm.util.impexp.TextInfoDecorator;

/**
 * Document download servlet
 */
public class DownloadServlet extends OKMHttpServlet {
	private static Logger log = LoggerFactory.getLogger(DownloadServlet.class);
	private static final long serialVersionUID = 1L;
	private static final boolean exportZip = true;
	private static final boolean exportJar = false;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("service({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String path = request.getParameter("path");
		String uuid = request.getParameter("uuid");
		String[] uuidList = request.getParameterValues("uuidList");
		String[] pathList = request.getParameterValues("pathList");
		String checkout = request.getParameter("checkout");
		String ver = request.getParameter("ver");
		boolean export = request.getParameter("export") != null;
		boolean inline = request.getParameter("inline") != null;
		File tmp = File.createTempFile("okm", ".tmp");
		InputStream is = null;
		updateSessionManager(request);

		try {
			// Now an document can be located by UUID
			if (uuid != null && !uuid.isEmpty()) {
				uuid = FormatUtil.sanitizeInput(uuid);
				path = OKMRepository.getInstance().getNodePath(null, uuid);
			} else if (path != null) {
				path = FormatUtil.sanitizeInput(path);
				path = new String(path.getBytes("ISO-8859-1"), "UTF-8");
			}

			if (export) {
				if (exportZip) {
					FileOutputStream os = new FileOutputStream(tmp);
					String fileName = "export.zip";

					if (path != null) {
						exportFolderAsZip(path, os);
						fileName = PathUtils.getName(path) + ".zip";
					} else if (uuidList != null || pathList != null) {
						// Export into a zip file multiple documents
						List<String> paths = new ArrayList<String>();

						if (uuidList != null) {
							for (String uuidElto : uuidList) {
								uuidElto = FormatUtil.sanitizeInput(uuidElto);
								String foo = new String(uuidElto.getBytes("ISO-8859-1"), "UTF-8");
								paths.add(OKMRepository.getInstance().getNodePath(null, foo));
							}
						} else if (pathList != null) {
							for (String pathElto : pathList) {
								pathElto = FormatUtil.sanitizeInput(pathElto);
								String foo = new String(pathElto.getBytes("ISO-8859-1"), "UTF-8");
								paths.add(foo);
							}
						}

						fileName = PathUtils.getName(PathUtils.getParent(paths.get(0)));
						exportDocumentsAsZip(paths, os, fileName);
						fileName += ".zip";
					}

					os.flush();
					os.close();
					is = new FileInputStream(tmp);

					// Send document
					WebUtils.sendFile(request, response, fileName, MimeTypeConfig.MIME_ZIP, inline, is, tmp.length());
				} else if (exportJar) {
					// Get document
					FileOutputStream os = new FileOutputStream(tmp);
					exportFolderAsJar(path, os);
					os.flush();
					os.close();
					is = new FileInputStream(tmp);

					// Send document
					String fileName = PathUtils.getName(path) + ".jar";
					WebUtils.sendFile(request, response, fileName, "application/x-java-archive", inline, is, tmp.length());
				}
			} else {
				if (OKMDocument.getInstance().isValid(null, path)) {
					// Get document
					Document doc = OKMDocument.getInstance().getProperties(null, path);
					long overallSize = doc.getActualVersion().getSize();

					if (ver != null && !ver.equals("")) {
						is = OKMDocument.getInstance().getContentByVersion(null, path, ver);
						for (Version version : OKMDocument.getInstance().getVersionHistory(null, path)) {
							if (version.getName().equals(ver)) {
								overallSize = version.getSize();
								break;
							}
						}
					} else {
						is = OKMDocument.getInstance().getContent(null, path, checkout != null);
						ver = doc.getActualVersion().getName();
					}

					// Send document
					String fileName = PathUtils.getName(doc.getPath());

					// Optinal append version to download ( not when doing checkout )
					if (Config.VERSION_APPEND_DOWNLOAD && (checkout == null)) {
						String versionToAppend = "";
						if (ver != null && !ver.equals("")) {
							versionToAppend = " rev " + ver;
						} else {
							versionToAppend = " rev " + doc.getActualVersion().getName();
						}
						String[] nameParts = fileName.split("\\.(?=[^\\.]+$)");
						fileName = nameParts[0] + versionToAppend + "." + nameParts[1];
					}

					WebUtils.sendFile(request, response, fileName, doc.getMimeType(), inline, is, overallSize);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					// Get mail
					Mail mail = OKMMail.getInstance().getProperties(null, path);

					// Send mail
					ServletOutputStream sos = response.getOutputStream();
					String fileName = PathUtils.getName(mail.getSubject() + ".eml");
					WebUtils.prepareSendFile(request, response, fileName, MimeTypeConfig.MIME_EML, inline);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					MimeMessage msg = MailUtils.create(null, mail);
					msg.writeTo(baos);
					response.setContentLength(baos.size());
					baos.writeTo(sos);
					IOUtils.closeQuietly(baos);
					IOUtils.closeQuietly(sos);
				}
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage()));
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_Repository),
					e.getMessage()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_IO),
					e.getMessage()));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_Database),
					e.getMessage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_General),
					e.getMessage()));
		} finally {
			IOUtils.closeQuietly(is);
			FileUtils.deleteQuietly(tmp);
		}

		log.debug("service: void");
	}

	/**
	 * Generate a zip file from a repository folder path
	 */
	private void exportFolderAsZip(String fldPath, OutputStream os) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, ArchiveException, ParseException, NoSuchGroupException, IOException, DatabaseException, MessagingException {
		log.debug("exportFolderAsZip({}, {})", fldPath, os);
		StringWriter out = new StringWriter();
		FileOutputStream fos = null;
		InputStream is = null;
		File tmp = null;

		try {
			tmp = FileUtils.createTempDir();

			if (fldPath.startsWith("/" + Repository.CATEGORIES)) {
				String categoryId = OKMRepository.getInstance().getNodeUuid(null, fldPath);

				for (Document doc : OKMSearch.getInstance().getCategorizedDocuments(null, categoryId)) {
					is = OKMDocument.getInstance().getContent(null, doc.getUuid(), false);
					fos = new FileOutputStream(new File(tmp, PathUtils.getName(doc.getPath())));
					IOUtils.copy(is, fos);
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(fos);
				}
			} else {
				RepositoryExporter.exportDocuments(null, fldPath, tmp, false, false, out, new TextInfoDecorator(fldPath));
			}

			// Zip files
			ArchiveUtils.createZip(tmp, PathUtils.getName(fldPath), os);
		} catch (IOException e) {
			log.error("Error exporting zip", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(out);
			FileUtils.deleteQuietly(tmp);
		}

		log.debug("exportFolderAsZip: void");
	}

	/**
	 * Generate a zip file from a list of documents
	 */
	private void exportDocumentsAsZip(List<String> paths, OutputStream os, String zipname) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, ArchiveException, ParseException, NoSuchGroupException, IOException,
			DatabaseException {
		log.debug("exportDocumentsAsZip({}, {})", paths, os);
		StringWriter out = new StringWriter();
		File tmp = null;

		try {
			tmp = FileUtils.createTempDir();
			File fsPath = new File(tmp.getPath());

			// Export files
			for (String docPath : paths) {
				String destPath = fsPath.getPath() + File.separator + PathUtils.getName(docPath).replace(':', '_');
				RepositoryExporter.exportDocument(null, docPath, destPath, false, false, out, new TextInfoDecorator(docPath));
			}

			// Zip files
			ArchiveUtils.createZip(tmp, zipname, os);
		} catch (IOException e) {
			log.error("Error exporting zip", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(out);
			FileUtils.deleteQuietly(tmp);
		}

		log.debug("exportDocumentsAsZip: void");
	}

	/**
	 * Generate a jar file from a repository folder path
	 */
	private void exportFolderAsJar(String fldPath, OutputStream os) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, ArchiveException, ParseException, NoSuchGroupException, IOException, DatabaseException, MessagingException {
		log.debug("exportFolderAsJar({}, {})", fldPath, os);
		StringWriter out = new StringWriter();
		File tmp = null;

		try {
			tmp = FileUtils.createTempDir();

			// Export files
			RepositoryExporter.exportDocuments(null, fldPath, tmp, false, false, out, new TextInfoDecorator(fldPath));

			// Jar files
			ArchiveUtils.createJar(tmp, PathUtils.getName(fldPath), os);
		} catch (IOException e) {
			log.error("Error exporting jar", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(out);
			FileUtils.deleteQuietly(tmp);
		}

		log.debug("exportFolderAsJar: void");
	}
}
