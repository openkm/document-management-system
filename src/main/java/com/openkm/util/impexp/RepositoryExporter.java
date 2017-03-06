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

package com.openkm.util.impexp;

import com.google.gson.Gson;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.Version;
import com.openkm.core.*;
import com.openkm.module.DocumentModule;
import com.openkm.module.FolderModule;
import com.openkm.module.MailModule;
import com.openkm.module.ModuleManager;
import com.openkm.util.FileLogger;
import com.openkm.util.FileUtils;
import com.openkm.util.MailUtils;
import com.openkm.util.PathUtils;
import com.openkm.util.impexp.metadata.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Iterator;

public class RepositoryExporter {
	private static Logger log = LoggerFactory.getLogger(RepositoryExporter.class);
	private static final String BASE_NAME = RepositoryExporter.class.getSimpleName();
	private static boolean firstTime = true;

	private RepositoryExporter() {
	}

	/**
	 * Performs a recursive repository content export with metadata
	 */
	public static ImpExpStats exportDocuments(String token, String fldPath, File fs, boolean metadata, boolean history,
	                                          Writer out, InfoDecorator deco) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			IOException, DatabaseException, ParseException, NoSuchGroupException, MessagingException {
		log.debug("exportDocuments({}, {}, {}, {}, {}, {}, {})", new Object[]{token, fldPath, fs, metadata, history, out, deco});
		ImpExpStats stats;

		try {
			FileLogger.info(BASE_NAME, "Start repository export from ''{0}'' to ''{1}''", fldPath, fs.getPath());

			if (fs.exists()) {
				firstTime = true;
				stats = exportDocumentsHelper(token, fldPath, fs, metadata, history, out, deco);
			} else {
				throw new FileNotFoundException(fs.getPath());
			}

			FileLogger.info(BASE_NAME, "Repository export finalized");
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "PathNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "AccessDeniedException ''{0}''", e.getMessage());
			throw e;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "FileNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "RepositoryException ''{0}''", e.getMessage());
			throw e;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "IOException ''{0}''", e.getMessage());
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "DatabaseException ''{0}''", e.getMessage());
			throw e;
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "ParseException ''{0}''", e.getMessage());
			throw e;
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "NoSuchGroupException ''{0}''", e.getMessage());
			throw e;
		} catch (MessagingException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "MessagingException ''{0}''", e.getMessage());
			throw e;
		}

		log.debug("exportDocuments: {}", stats);
		return stats;
	}

	/**
	 * Performs a recursive repository content export with metadata
	 */
	private static ImpExpStats exportDocumentsHelper(String token, String fldPath, File fs, boolean metadata,
	                                                 boolean history, Writer out, InfoDecorator deco) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException, ParseException, NoSuchGroupException, MessagingException {
		log.debug("exportDocumentsHelper({}, {}, {}, {}, {}, {}, {})", new Object[]{token, fldPath, fs, metadata, history, out, deco});
		ImpExpStats stats = new ImpExpStats();
		DocumentModule dm = ModuleManager.getDocumentModule();
		FolderModule fm = ModuleManager.getFolderModule();
		MailModule mm = ModuleManager.getMailModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		Gson gson = new Gson();
		String path = null;
		File fsPath = null;

		if (firstTime) {
			path = fs.getPath();
			fsPath = new File(path);
			firstTime = false;
		} else {
			String dirName = PathUtils.decodeEntities(PathUtils.getName(fldPath));

			// Repository path needs to be "corrected" under Windoze
			path = fs.getPath() + File.separator + FileUtils.toValidFilename(dirName);
			fsPath = new File(path);
			fsPath.mkdirs();
			FileLogger.info(BASE_NAME, "Created folder ''{0}''", fsPath.getPath());

			if (out != null) {
				out.write(deco.print(fldPath, 0, null));
				out.flush();
			}
		}

		for (Iterator<Mail> it = mm.getChildren(token, fldPath).iterator(); it.hasNext(); ) {
			Mail mailChild = it.next();

			try {
				String mailName = PathUtils.decodeEntities(PathUtils.getName(mailChild.getPath()));

				// Repository path needs to be "corrected" under Windoze
				path = fsPath.getPath() + File.separator + FileUtils.toValidFilename(mailName);
				ImpExpStats mailStats = exportMail(token, mailChild.getPath(), path + ".eml", metadata, out, deco);

				// Stats
				stats.setSize(stats.getSize() + mailStats.getSize());
				stats.setMails(stats.getMails() + mailStats.getMails());
			} catch (Exception e) {
				FileLogger.error(BASE_NAME, "{0} ''{1}''", e.toString(), e.getMessage());

				if (out != null) {
					out.write(deco.print(mailChild.getPath(), mailChild.getSize(), e.toString()));
					out.flush();
				}
			}
		}

		for (Iterator<Document> it = dm.getChildren(token, fldPath).iterator(); it.hasNext(); ) {
			Document docChild = it.next();

			try {
				String fileName = PathUtils.decodeEntities(PathUtils.getName(docChild.getPath()));

				// Repository path needs to be "corrected" under Windoze
				path = fsPath.getPath() + File.separator + FileUtils.toValidFilename(fileName);
				ImpExpStats docStats = exportDocument(token, docChild.getPath(), path, metadata, history, out, deco);

				// Stats
				stats.setSize(stats.getSize() + docStats.getSize());
				stats.setDocuments(stats.getDocuments() + docStats.getDocuments());
			} catch (Exception e) {
				FileLogger.error(BASE_NAME, "{0} ''{1}''", e.toString(), e.getMessage());

				if (out != null) {
					out.write(deco.print(docChild.getPath(), docChild.getActualVersion().getSize(), e.toString()));
					out.flush();
				}
			}
		}

		for (Iterator<Folder> it = fm.getChildren(token, fldPath).iterator(); it.hasNext(); ) {
			Folder fldChild = it.next();
			ImpExpStats tmp = exportDocumentsHelper(token, fldChild.getPath(), fsPath, metadata, history, out, deco);
			String dirName = PathUtils.decodeEntities(PathUtils.getName(fldChild.getPath()));

			// Repository path needs to be "corrected" under Windoze
			path = fsPath.getPath() + File.separator + FileUtils.toValidFilename(dirName);

			// Metadata
			if (metadata) {
				FolderMetadata fmd = ma.getMetadata(fldChild);
				String json = gson.toJson(fmd);
				FileOutputStream fos = new FileOutputStream(path + Config.EXPORT_METADATA_EXT);
				IOUtils.write(json, fos);
				fos.close();
			}

			// Stats
			stats.setSize(stats.getSize() + tmp.getSize());
			stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
			stats.setFolders(stats.getFolders() + tmp.getFolders() + 1);
			stats.setOk(stats.isOk() && tmp.isOk());
		}

		log.debug("exportDocumentsHelper: {}", stats);
		return stats;
	}

	/**
	 * Export mail from OpenKM repository to filesystem.
	 */
	public static ImpExpStats exportMail(String token, String mailPath, String destPath, boolean metadata,
	                                     Writer out, InfoDecorator deco) throws PathNotFoundException, RepositoryException, DatabaseException,
			IOException, AccessDeniedException, ParseException, NoSuchGroupException, MessagingException {
		MailModule mm = ModuleManager.getMailModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		Mail mailChild = mm.getProperties(token, mailPath);
		Gson gson = new Gson();
		ImpExpStats stats = new ImpExpStats();
		MimeMessage msg = MailUtils.create(token, mailChild);
		FileOutputStream fos = new FileOutputStream(destPath);
		msg.writeTo(fos);
		IOUtils.closeQuietly(fos);
		FileLogger.info(BASE_NAME, "Created document ''{0}''", mailChild.getPath());

		// Metadata
		if (metadata) {
			MailMetadata mmd = ma.getMetadata(mailChild);
			String json = gson.toJson(mmd);
			fos = new FileOutputStream(destPath + Config.EXPORT_METADATA_EXT);
			IOUtils.write(json, fos);
			IOUtils.closeQuietly(fos);
		}

		if (out != null) {
			out.write(deco.print(mailChild.getPath(), mailChild.getSize(), null));
			out.flush();
		}

		// Stats
		stats.setSize(stats.getSize() + mailChild.getSize());
		stats.setMails(stats.getMails() + 1);

		return stats;
	}

	/**
	 * Export document from OpenKM repository to filesystem.
	 */
	public static ImpExpStats exportDocument(String token, String docPath, String destPath, boolean metadata,
	                                         boolean history, Writer out, InfoDecorator deco) throws PathNotFoundException, RepositoryException,
			DatabaseException, IOException, AccessDeniedException, ParseException, NoSuchGroupException {
		DocumentModule dm = ModuleManager.getDocumentModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		Document docChild = dm.getProperties(token, docPath);
		Gson gson = new Gson();
		ImpExpStats stats = new ImpExpStats();

		// Version history
		if (history) {
			// Create dummy document file
			new File(destPath).createNewFile();

			// Metadata
			if (metadata) {
				DocumentMetadata dmd = ma.getMetadata(docChild);
				String json = gson.toJson(dmd);
				FileOutputStream fos = new FileOutputStream(destPath + Config.EXPORT_METADATA_EXT);
				IOUtils.write(json, fos);
				IOUtils.closeQuietly(fos);
			}

			for (Version ver : dm.getVersionHistory(token, docChild.getPath())) {
				String versionPath = destPath + "#v" + ver.getName() + "#";
				FileOutputStream vos = new FileOutputStream(versionPath);
				InputStream vis = dm.getContentByVersion(token, docChild.getPath(), ver.getName());
				IOUtils.copy(vis, vos);
				IOUtils.closeQuietly(vis);
				IOUtils.closeQuietly(vos);
				FileLogger.info(BASE_NAME, "Created document ''{0}'' version ''{1}''", docChild.getPath(), ver.getName());

				// Metadata
				if (metadata) {
					VersionMetadata vmd = ma.getMetadata(ver, docChild.getMimeType());
					String json = gson.toJson(vmd);
					vos = new FileOutputStream(versionPath + Config.EXPORT_METADATA_EXT);
					IOUtils.write(json, vos);
					IOUtils.closeQuietly(vos);
				}
			}
		} else {
			FileOutputStream fos = new FileOutputStream(destPath);
			InputStream is = dm.getContent(token, docChild.getPath(), false);
			IOUtils.copy(is, fos);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
			FileLogger.info(BASE_NAME, "Created document ''{0}''", docChild.getPath());

			// Metadata
			if (metadata) {
				DocumentMetadata dmd = ma.getMetadata(docChild);
				String json = gson.toJson(dmd);
				fos = new FileOutputStream(destPath + Config.EXPORT_METADATA_EXT);
				IOUtils.write(json, fos);
				IOUtils.closeQuietly(fos);
			}
		}

		if (out != null) {
			out.write(deco.print(docChild.getPath(), docChild.getActualVersion().getSize(), null));
			out.flush();
		}

		// Stats
		stats.setSize(stats.getSize() + docChild.getActualVersion().getSize());
		stats.setDocuments(stats.getDocuments() + 1);

		return stats;
	}
}
