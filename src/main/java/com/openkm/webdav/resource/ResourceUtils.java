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

package com.openkm.webdav.resource;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMMail;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.util.SystemProfiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ResourceUtils {
	private static final Logger log = LoggerFactory.getLogger(ResourceUtils.class);

	/**
	 * Resolve node resource (may be folder or document)
	 */
	public static Resource getNode(Path srcPath, String path) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getNode({}, {})", srcPath, path);
		long begin = System.currentTimeMillis();
		String fixedPath = ResourceUtils.fixRepositoryPath(path);
		Resource res = null;

		try {
			if (OKMFolder.getInstance().isValid(null, fixedPath)) {
				if (path.startsWith(fixRepositoryPath("/" + Repository.CATEGORIES))) {
					// Is from categories
					log.info("Path: {}", path);
					res = getCategory(srcPath, path);
				} else {
					res = getFolder(srcPath, path);
				}
			} else if (OKMDocument.getInstance().isValid(null, fixedPath)) {
				res = getDocument(path);
			} else if (OKMMail.getInstance().isValid(null, fixedPath)) {
				res = getMail(path);
			}
		} catch (PathNotFoundException e) {
			log.warn("PathNotFoundException: {}", e.getMessage());
		}

		SystemProfiling.log(srcPath + ", " + path, System.currentTimeMillis() - begin);
		log.trace("getNode.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getNode: {}", res);
		return res;
	}

	/**
	 * Resolve folder resource.
	 */
	private static Resource getFolder(Path path, String fldPath) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		long begin = System.currentTimeMillis();
		String fixedFldPath = fixRepositoryPath(fldPath);
		Folder fld = OKMFolder.getInstance().getProperties(null, fixedFldPath);
		List<Folder> fldChilds = OKMFolder.getInstance().getChildren(null, fixedFldPath);
		List<Document> docChilds = OKMDocument.getInstance().getChildren(null, fixedFldPath);
		List<Mail> mailChilds = OKMMail.getInstance().getChildren(null, fixedFldPath);
		Resource fldResource = new FolderResource(path, fld, fldChilds, docChilds, mailChilds);

		SystemProfiling.log(null, System.currentTimeMillis() - begin);
		log.trace("getFolder.Time: {}", System.currentTimeMillis() - begin);
		return fldResource;
	}

	/**
	 * Resolve document resource.
	 */
	private static Resource getDocument(String docPath) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		long begin = System.currentTimeMillis();
		String fixedDocPath = fixRepositoryPath(docPath);
		Document doc = OKMDocument.getInstance().getProperties(null, fixedDocPath);
		Resource docResource = new DocumentResource(doc);

		SystemProfiling.log(null, System.currentTimeMillis() - begin);
		log.trace("getDocument.Time: {}", System.currentTimeMillis() - begin);
		return docResource;
	}

	/**
	 * Resolve mail resource.
	 */
	private static Resource getMail(String mailPath) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		long begin = System.currentTimeMillis();
		String fixedMailPath = fixRepositoryPath(mailPath);
		Mail mail = OKMMail.getInstance().getProperties(null, fixedMailPath);
		Resource docResource = new MailResource(mail);

		SystemProfiling.log(null, System.currentTimeMillis() - begin);
		log.trace("getMail.Time: {}", System.currentTimeMillis() - begin);
		return docResource;
	}

	/**
	 * Resolve category resource.
	 */
	private static Resource getCategory(Path path, String catPath) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		long begin = System.currentTimeMillis();
		String fixedFldPath = fixRepositoryPath(catPath);
		Folder cat = OKMFolder.getInstance().getProperties(null, fixedFldPath);
		List<Folder> catChilds = OKMFolder.getInstance().getChildren(null, fixedFldPath);
		//String uuid = OKMFolder.getInstance().getProperties(null, fixedFldPath).getUuid();
		//List<Folder> fldChilds = OKMSearch.getInstance().getCategorizedFolders(null, uuid);
		//List<Document> docChilds = OKMSearch.getInstance().getCategorizedDocuments(null, uuid);
		//List<Mail> mailChilds = OKMSearch.getInstance().getCategorizedMails(null, uuid);

		// Fix node name
		//for (Folder fld : fldChilds) {
		//fld.setPath(fld.getPath() + "#" + fld.getUuid());
		//}

		//catChilds.addAll(fldChilds);
		List<Document> docChilds = new ArrayList<Document>();
		List<Mail> mailChilds = new ArrayList<Mail>();
		Resource catResource = new CategoryResource(path, cat, catChilds, docChilds, mailChilds);

		SystemProfiling.log(null, System.currentTimeMillis() - begin);
		log.trace("getCategory.Time: {}", System.currentTimeMillis() - begin);
		return catResource;
	}

	/**
	 * Create HTML content.
	 */
	public static void createContent(OutputStream out, Path path, List<Folder> fldChilds, List<Document> docChilds,
	                                 List<Mail> mailChilds) {
		log.debug("createContent({}, {}, {}, {}, {})", new Object[]{out, path, fldChilds, docChilds, mailChilds});
		long begin = System.currentTimeMillis();
		PrintWriter pw = new PrintWriter(out);
		pw.println("<html>");
		pw.println("<header>");
		pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		pw.println("<link rel=\"Shortcut icon\" href=\"/" + path.getFirst() + "/favicon.ico\" />");
		pw.println("<link rel=\"stylesheet\" href=\"/" + path.getFirst() + "/css/style.css\" type=\"text/css\" />");
		pw.println("<title>OpenKM WebDAV</title>");
		pw.println("</header>");
		pw.println("<body>");
		pw.println("<h1>OpenKM WebDAV</h1>");
		pw.println("<table>");

		if (!path.getStripFirst().getStripFirst().isRoot()) {
			String url = path.getParent().toPath();
			pw.print("<tr>");
			pw.print("<td><img src='/" + path.getFirst() + "/img/webdav/folder.png'/></td>");
			pw.print("<td><a href='" + url + "'>..</a></td>");
			pw.println("<tr>");
		}

		if (fldChilds != null) {
			for (Folder fld : fldChilds) {
				Path fldPath = Path.path(fld.getPath());
				String url = path.toPath().concat("/").concat(fldPath.getName());
				pw.print("<tr>");
				pw.print("<td><img src='/" + path.getFirst() + "/img/webdav/folder.png'/></td>");
				pw.print("<td><a href='" + url + "'>" + fldPath.getName() + "</a></td>");
				pw.println("<tr>");
			}
		}

		if (docChilds != null) {
			for (Document doc : docChilds) {
				Path docPath = Path.path(doc.getPath());
				String url = path.toPath().concat("/").concat(docPath.getName());
				pw.print("<tr>");
				pw.print("<td><img src='/" + path.getFirst() + "/mime/" + doc.getMimeType() + "'/></td>");
				pw.print("<td><a href='" + url + "'>" + docPath.getName() + "</a></td>");
				pw.println("<tr>");
			}
		}

		if (mailChilds != null) {
			for (Mail mail : mailChilds) {
				Path mailPath = Path.path(mail.getPath());
				String url = path.toPath().concat("/").concat(mailPath.getName());
				pw.print("<tr>");

				if (mail.getAttachments().isEmpty()) {
					pw.print("<td><img src='/" + path.getFirst() + "/img/webdav/email.png'/></td>");
				} else {
					pw.print("<td><img src='/" + path.getFirst() + "/img/webdav/email_attach.png'/></td>");
				}

				pw.print("<td><a href='" + url + "'>" + mailPath.getName() + "</a></td>");
				pw.println("<tr>");
			}
		}

		pw.println("</table>");
		pw.println("</body>");
		pw.println("</html>");
		pw.flush();
		pw.close();

		SystemProfiling.log(String.valueOf(path), System.currentTimeMillis() - begin);
		log.trace("createContent.Time: {}", System.currentTimeMillis() - begin);
	}

	/**
	 * Correct webdav folder path
	 */
	public static Folder fixResourcePath(Folder fld) {
		if (Config.SYSTEM_WEBDAV_FIX) {
			fld.setPath(fixResourcePath(fld.getPath()));
		}

		return fld;
	}

	/**
	 * Correct webdav document path
	 */
	public static Document fixResourcePath(Document doc) {
		if (Config.SYSTEM_WEBDAV_FIX) {
			doc.setPath(fixResourcePath(doc.getPath()));
		}

		return doc;
	}

	/**
	 * Correct webdav mail path
	 */
	public static Mail fixResourcePath(Mail mail) {
		if (Config.SYSTEM_WEBDAV_FIX) {
			mail.setPath(fixResourcePath(mail.getPath()));
		}

		return mail;
	}

	/**
	 *
	 */
	private static String fixResourcePath(String path) {
		return path.replace("okm:", "okm_");
	}

	/**
	 *
	 */
	public static String fixRepositoryPath(String path) {
		if (Config.SYSTEM_WEBDAV_FIX) {
			return path.replace("okm_", "okm:");
		} else {
			return path;
		}
	}
}
