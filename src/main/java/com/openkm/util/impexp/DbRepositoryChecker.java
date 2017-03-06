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

import com.openkm.bean.Document;
import com.openkm.bean.Version;
import com.openkm.core.*;
import com.openkm.dao.*;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.module.DocumentModule;
import com.openkm.module.ModuleManager;
import com.openkm.module.db.stuff.FsDataStore;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.FileLogger;
import com.openkm.util.FileUtils;
import com.openkm.util.SystemProfiling;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.*;

public class DbRepositoryChecker {
	private static Logger log = LoggerFactory.getLogger(DbRepositoryChecker.class);
	private static final String BASE_NAME = DbRepositoryChecker.class.getSimpleName();

	private DbRepositoryChecker() {
	}

	/**
	 * Performs a recursive repository document check
	 */
	public static ImpExpStats checkDocuments(String token, String fldPath, boolean fast, boolean versions, boolean checksum, Writer out,
	                                         InfoDecorator deco) throws PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.debug("checkDocuments({}, {}, {}, {}, {}, {}, {})", new Object[]{token, fldPath, fast, versions, checksum, out, deco});
		long begin = System.currentTimeMillis();
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;
		ImpExpStats stats = new ImpExpStats();

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			FileLogger.info(BASE_NAME, "Start repository check for ''{0}'", fldPath);
			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(fldPath);
			stats = checkDocumentsHelper(token, uuid, fast, versions, checksum, out, deco);
			FileLogger.info(BASE_NAME, "Repository check finalized");
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "PathNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "AccessDeniedException ''{0}''", e.getMessage());
			throw e;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "FileNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "RepositoryException ''{0}''", e.getMessage());
			throw e;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "IOException ''{0}''", e.getMessage());
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "DatabaseException ''{0}''", e.getMessage());
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(fldPath + ", " + versions, System.currentTimeMillis() - begin);
		log.trace("checkDocuments.Time: {}", System.currentTimeMillis() - begin);
		log.debug("checkDocuments: {}", stats);
		return stats;
	}

	/**
	 * Performs a recursive repository document check
	 */
	private static ImpExpStats checkDocumentsHelper(String token, String uuid, boolean fast, boolean versions, boolean checksum,
	                                                Writer out, InfoDecorator deco) throws FileNotFoundException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException {
		log.debug("checkDocumentsHelper({}, {}, {}, {}, {}, {}, {})", new Object[]{token, uuid, fast, versions, checksum, out, deco});
		long begin = System.currentTimeMillis();
		ImpExpStats stats = new ImpExpStats();

		// Check documents
		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(uuid)) {
			String path = NodeBaseDAO.getInstance().getPathFromUuid(nDoc.getUuid());
			ImpExpStats tmp = readDocument(token, path, fast, versions, checksum, out, deco);
			stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
			stats.setFolders(stats.getFolders() + tmp.getFolders());
			stats.setSize(stats.getSize() + tmp.getSize());
			stats.setOk(stats.isOk() && tmp.isOk());
		}

		// Check folders
		for (NodeFolder nFld : NodeFolderDAO.getInstance().findByParent(uuid)) {
			ImpExpStats tmp = readFolder(token, nFld, fast, versions, checksum, out, deco);
			stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
			stats.setFolders(stats.getFolders() + tmp.getFolders());
			stats.setSize(stats.getSize() + tmp.getSize());
			stats.setOk(stats.isOk() && tmp.isOk());
		}

		// Check mails
		for (NodeMail nMail : NodeMailDAO.getInstance().findByParent(uuid)) {
			ImpExpStats tmp = readMail(token, nMail, fast, versions, checksum, out, deco);
			stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
			stats.setFolders(stats.getFolders() + tmp.getFolders());
			stats.setSize(stats.getSize() + tmp.getSize());
			stats.setOk(stats.isOk() && tmp.isOk());
		}

		SystemProfiling.log(uuid + ", " + versions, System.currentTimeMillis() - begin);
		log.trace("checkDocumentsHelper.Time: {}", System.currentTimeMillis() - begin);
		log.debug("checkDocumentsHelper: {}", stats);
		return stats;
	}

	/**
	 * Read document contents.
	 */
	@SuppressWarnings("resource")
	private static ImpExpStats readDocument(String token, String docPath, boolean fast, boolean versions, boolean checksum, Writer out,
	                                        InfoDecorator deco) throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, IOException {
		log.debug("readDocument({}, {}, {}, {})", new Object[]{docPath, fast, versions, checksum});
		long begin = System.currentTimeMillis();
		DocumentModule dm = ModuleManager.getDocumentModule();
		File fsTmp = FileUtils.createTempFile();
		FileOutputStream fosTmp = null;
		InputStream is = null;
		ImpExpStats stats = new ImpExpStats();
		Document doc = dm.getProperties(token, docPath);
		String curVerName = null;

		try {
			String docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docPath);

			if (Config.REPOSITORY_NATIVE && FsDataStore.DATASTORE_BACKEND_FS.equals(Config.REPOSITORY_DATASTORE_BACKEND) && fast) {
				NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
				File dsDocVerFile = FsDataStore.resolveFile(nDocVer.getUuid());

				if (!dsDocVerFile.exists()) {
					throw new IOException("File does not exists: " + dsDocVerFile);
				} else if (!dsDocVerFile.canRead()) {
					throw new IOException("Can't read file: " + dsDocVerFile);
				}
			} else {
				fosTmp = new FileOutputStream(fsTmp);
				is = dm.getContent(token, docPath, false);
				IOUtils.copy(is, fosTmp);
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(fosTmp);
			}

			if (Config.REPOSITORY_NATIVE && Config.REPOSITORY_CONTENT_CHECKSUM && checksum) {
				curVerName = NodeDocumentVersionDAO.getInstance().findCurrentVersionName(docUuid);
				FsDataStore.verifyChecksum(docUuid, curVerName, fsTmp);
			}

			if (versions) { // Check version history
				if (curVerName == null) {
					curVerName = NodeDocumentVersionDAO.getInstance().findCurrentVersionName(docUuid);
				}

				for (Version ver : dm.getVersionHistory(token, docPath)) {
					if (!curVerName.equals(ver.getName())) {
						if (Config.REPOSITORY_NATIVE && FsDataStore.DATASTORE_BACKEND_FS.equals(Config.REPOSITORY_DATASTORE_BACKEND)
								&& fast) {
							NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findVersion(docUuid, ver.getName());
							File dsDocVerFile = FsDataStore.resolveFile(nDocVer.getUuid());

							if (!dsDocVerFile.exists()) {
								throw new IOException("File does not exists: " + dsDocVerFile + ", version: " + ver.getName());
							} else if (!dsDocVerFile.canRead()) {
								throw new IOException("Can't read file: " + dsDocVerFile + ", version: " + ver.getName());
							}
						} else {
							is = dm.getContentByVersion(token, docPath, ver.getName());
							fosTmp = new FileOutputStream(fsTmp);
							IOUtils.copy(is, fosTmp);
							IOUtils.closeQuietly(is);
							IOUtils.closeQuietly(fosTmp);

							if (Config.REPOSITORY_NATIVE && Config.REPOSITORY_CONTENT_CHECKSUM && checksum) {
								FsDataStore.verifyChecksum(docUuid, ver.getName(), fsTmp);
							}
						}
					}
				}

				FileLogger.info(BASE_NAME, "Checked document version ''{0} - {1}''", docPath, curVerName);
			}

			out.write(deco.print(docPath, doc.getActualVersion().getSize(), null));
			out.flush();

			// Stats
			stats.setSize(stats.getSize() + doc.getActualVersion().getSize());
			stats.setDocuments(stats.getDocuments() + 1);

			FileLogger.info(BASE_NAME, "Checked document ''{0}''", docPath);
		} catch (RepositoryException e) {
			log.error(e.getMessage());
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "RepositoryException ''{0}''", e.getMessage());
			out.write(deco.print(docPath, doc.getActualVersion().getSize(), e.getMessage()));
			out.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "IOException ''{0}''", e.getMessage());
			out.write(deco.print(docPath, doc.getActualVersion().getSize(), e.getMessage()));
			out.flush();
		} catch (Exception e) {
			log.error(e.getMessage());
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "Exception ''{0}''", e.getMessage());
			out.write(deco.print(docPath, doc.getActualVersion().getSize(), e.getMessage()));
			out.flush();
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fosTmp);
			org.apache.commons.io.FileUtils.deleteQuietly(fsTmp);
		}

		SystemProfiling.log(docPath + ", " + versions, System.currentTimeMillis() - begin);
		log.trace("readDocument.Time: {}", System.currentTimeMillis() - begin);
		return stats;
	}

	/**
	 * Read folder contents.
	 */
	private static ImpExpStats readFolder(String token, NodeFolder nFld, boolean fast, boolean versions, boolean checksum, Writer out,
	                                      InfoDecorator deco) throws FileNotFoundException, PathNotFoundException, AccessDeniedException, RepositoryException,
			IOException, DatabaseException {
		String fldPath = NodeBaseDAO.getInstance().getPathFromUuid(nFld.getUuid());
		log.debug("readFolder({})", fldPath);
		FileLogger.info(BASE_NAME, "Checked folder ''{0}''", fldPath);
		ImpExpStats stats = checkDocumentsHelper(token, nFld.getUuid(), fast, versions, checksum, out, deco);

		// Stats
		stats.setFolders(stats.getFolders() + 1);

		return stats;
	}

	/**
	 * Read mail contents.
	 */
	private static ImpExpStats readMail(String token, NodeMail nMail, boolean fast, boolean versions, boolean checksum, Writer out,
	                                    InfoDecorator deco) throws FileNotFoundException, PathNotFoundException, AccessDeniedException, RepositoryException,
			IOException, DatabaseException {
		String mailPath = NodeBaseDAO.getInstance().getPathFromUuid(nMail.getUuid());
		log.debug("readMail({})", mailPath);
		FileLogger.info(BASE_NAME, "Checked mail ''{0}''", mailPath);
		ImpExpStats stats = checkDocumentsHelper(token, nMail.getUuid(), fast, versions, checksum, out, deco);

		// Stats
		stats.setDocuments(stats.getDocuments() + 1);

		return stats;
	}
}
