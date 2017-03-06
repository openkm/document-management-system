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

package com.openkm.module.db.stuff;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.NodeDocumentVersionDAO;
import com.openkm.dao.bean.NodeDocumentVersion;
import com.openkm.util.SecureStore;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.NoSuchAlgorithmException;

public class FsDataStore {
	private static Logger log = LoggerFactory.getLogger(FsDataStore.class);
	public static final String DATASTORE_BACKEND_FS = "fs";
	public static final String DATASTORE_BACKEND_DB = "db";
	public static final String DATASTORE_DIRNAME = "datastore";

	/**
	 * Write to data store 
	 */
	public static File save(String uuid, InputStream is) throws IOException {
		log.debug("save({}, {})", uuid, is);
		File fs = resolveFile(uuid);
		fs.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(fs);
		IOUtils.copy(is, fos);
		IOUtils.closeQuietly(fos);
		return fs;
	}

	/**
	 * Read from data store
	 */
	public static InputStream read(String uuid) throws FileNotFoundException {
		log.debug("read({})", uuid);
		return new FileInputStream(resolveFile(uuid));
	}

	/**
	 * Purge data store file
	 */
	public static void delete(String uuid) throws IOException {
		log.debug("delete({})", uuid);
		File fs = resolveFile(uuid);

		if (!fs.delete()) {
			if (fs.exists()) {
				throw new IOException("Can't delete file (locked) '" + fs.getParent() + File.separator + uuid + "'");
			} else {
				throw new IOException("Cant' delete file (not exists) '" + fs.getParent() + File.separator + uuid + "'");
			}
		}
	}

	/**
	 * Copy a datastore file to another version
	 */
	public static void copy(NodeDocumentVersion srcDocVer, NodeDocumentVersion dstDocVer) throws IOException {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(resolveFile(srcDocVer.getUuid()));
			save(dstDocVer.getUuid(), fis);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Persis document file
	 */
	public static void persist(NodeDocumentVersion nDocVer, InputStream is) throws IOException {
		log.debug("persist({}, {})", nDocVer, is);

		if (FsDataStore.DATASTORE_BACKEND_FS.equals(Config.REPOSITORY_DATASTORE_BACKEND)) {
			File dsRaw = FsDataStore.save(nDocVer.getUuid(), is);

			if (Config.REPOSITORY_CONTENT_CHECKSUM) {
				try {
					String checkSum = SecureStore.md5Encode(dsRaw);
					nDocVer.setChecksum(checkSum);
				} catch (NoSuchAlgorithmException e) {
					log.warn(e.getMessage(), e);
				}
			}
		} else {
			byte[] raw = IOUtils.toByteArray(is);
			nDocVer.setContent(raw);

			if (Config.REPOSITORY_CONTENT_CHECKSUM) {
				try {
					String checkSum = SecureStore.md5Encode(raw);
					nDocVer.setChecksum(checkSum);
				} catch (NoSuchAlgorithmException e) {
					log.warn(e.getMessage(), e);
				}
			}
		}

		log.debug("persist: void");
	}

	/**
	 * Verify checksum
	 */
	public static void verifyChecksum(String docUuid, String verName, File fsRaw) throws RepositoryException,
			DatabaseException, IOException {
		log.debug("verifyChecksum({}, {}, {})", new Object[]{docUuid, verName, fsRaw});
		Session session = null;

		try {
			String stChecksum = NodeDocumentVersionDAO.getInstance().getVersionContentChecksumByParent(docUuid, verName);
			String clCheckSum = SecureStore.md5Encode(fsRaw);

			if (!clCheckSum.equals(stChecksum)) {
				throw new RepositoryException("Checksum failure for node '" + docUuid + "' and version '" + verName + "'");
			}
		} catch (NoSuchAlgorithmException e) {
			log.warn(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new RepositoryException("PathNotFound: " + docUuid);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("verifyChecksum: void");
	}

	/**
	 * Purge orphan datastore files.
	 *
	 * This method will remove datastore files not corresponding with a NodeDocumentVersion.
	 */
	public static void purgeOrphanFiles() throws DatabaseException, IOException {
		log.debug("purgeOrphanFiles()");
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			purgeOrphanFilesHelper(session, new File(Config.REPOSITORY_DATASTORE_HOME));
			log.debug("purgeOrphanFiles: void");
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Purge orphan datastore files helper
	 */
	private static void purgeOrphanFilesHelper(Session session, File dir) throws HibernateException, IOException {
		for (File child : dir.listFiles()) {
			if (child.isFile()) {
				if (session.get(NodeDocumentVersion.class, child.getName()) == null) {
					if (!child.delete()) {
						log.warn("Could not delete file '" + child.getCanonicalPath() + "'");
					}
				}
			} else if (child.isDirectory()) {
				purgeOrphanFilesHelper(session, child);
			}
		}
	}

	/**
	 * Get file from uuid
	 */
	public static File resolveFile(String uuid) {
		char[] seq = uuid.replaceAll("-", "").toCharArray();
		StringBuilder path = new StringBuilder();

		// For really big repositories maybe better: i < seq.length
		// But for most usual repositories a 4 depth level is enough 
		for (int i = 0; i < 8; i = i + 2) {
			path.append(seq[i]).append(seq[i + 1]).append(File.separator);
		}

		return new File(Config.REPOSITORY_DATASTORE_HOME + File.separator + path.toString() + uuid);
	}

	/**
	 * Purge empty datastore directories.
	 */
	public static void purgeEmptyDirectories() throws IOException {
		log.debug("purgeEmptyDirectories()");
		purgeEmptyDirectoriesHelper(new File(Config.REPOSITORY_DATASTORE_HOME));
	}

	/**
	 * Purge empty datastore directories helper
	 */
	private static boolean purgeEmptyDirectoriesHelper(File dir) throws IOException {
		boolean isEmpty = true;
		File[] children = dir.listFiles();

		if (children.length > 0) {
			for (File child : children) {
				if (child.isDirectory()) {
					isEmpty = purgeEmptyDirectoriesHelper(child);

					if (isEmpty) {
						child.delete();
					}
				} else if (child.isFile()) {
					isEmpty = false;
				}
			}
		} else {
			isEmpty = true;
		}

		return isEmpty;
	}
}
