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

package com.openkm.util;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.MimeTypeDAO;
import com.openkm.dao.bean.MimeType;
import com.openkm.util.impexp.RepositoryImporter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class FileUtils {
	private static Logger log = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * Returns the name of the file without the extension.
	 */
	public static String getFileName(String file) {
		log.debug("getFileName({})", file);
		int idx = file.lastIndexOf(".");
		String ret = idx >= 0 ? file.substring(0, idx) : file;
		log.debug("getFileName: {}", ret);
		return ret;
	}

	/**
	 * Returns the filename extension.
	 */
	public static String getFileExtension(String file) {
		log.debug("getFileExtension({})", file);
		int idx = file.lastIndexOf(".");
		String ret = idx >= 0 ? file.substring(idx + 1) : "";
		log.debug("getFileExtension: {}", ret);
		return ret;
	}

	/**
	 * Creates a temporal and unique directory
	 *
	 * @throws IOException If something fails.
	 */
	public static File createTempDir() throws IOException {
		File tmpFile = File.createTempFile("okm", null);

		if (!tmpFile.delete())
			throw new IOException();
		if (!tmpFile.mkdir())
			throw new IOException();
		return tmpFile;
	}

	/**
	 * Create temp file
	 */
	public static File createTempFile() throws IOException {
		return File.createTempFile("okm", ".tmp");
	}

	/**
	 * Create temp file
	 */
	public static File createTempFile(String ext) throws IOException {
		return File.createTempFile("okm", "." + ext);
	}

	/**
	 * Create temp file with extension from mime
	 */
	public static File createTempFileFromMime(String mimeType) throws DatabaseException, IOException {
		MimeType mt = MimeTypeDAO.findByName(mimeType);
		String ext = mt.getExtensions().iterator().next();
		return File.createTempFile("okm", "." + ext);
	}

	/**
	 * Wrapper for FileUtils.deleteQuietly
	 *
	 * @param file File or directory to be deleted.
	 */
	public static boolean deleteQuietly(File file) {
		return org.apache.commons.io.FileUtils.deleteQuietly(file);
	}

	/**
	 * Wrapper for FileUtils.cleanDirectory
	 *
	 * @param file File or directory to be deleted.
	 */
	public static void cleanDirectory(File directory) throws IOException {
		org.apache.commons.io.FileUtils.cleanDirectory(directory);
	}

	/**
	 * Wrapper for FileUtils.listFiles
	 *
	 * @param file File or directory to be listed.
	 */
	@SuppressWarnings("unchecked")
	public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {
		return org.apache.commons.io.FileUtils.listFiles(directory, extensions, recursive);
	}

	/**
	 * Wrapper for FileUtils.readFileToByteArray
	 *
	 * @param file File or directory to be deleted.
	 */
	public static byte[] readFileToByteArray(File file) throws IOException {
		return org.apache.commons.io.FileUtils.readFileToByteArray(file);
	}

	/**
	 * Delete directory if empty
	 */
	public static void deleteEmpty(File file) {
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			}
		}
	}

	/**
	 * Count files and directories from a selected directory.
	 */
	public static int countFiles(File dir) {
		File[] found = dir.listFiles();
		int ret = 0;

		if (found != null) {
			for (int i = 0; i < found.length; i++) {
				if (found[i].isDirectory()) {
					ret += countFiles(found[i]);
				}

				ret++;
			}
		}

		return ret;
	}

	/**
	 * Count files and directories from a selected directory.
	 * This version exclude .okm files
	 */
	public static int countImportFiles(File dir) {
		File[] found = dir.listFiles(new RepositoryImporter.NoVersionFilenameFilter());
		int ret = 0;

		if (found != null) {
			for (int i = 0; i < found.length; i++) {
				//log.info("File: {}", found[i].getPath());

				if (found[i].isDirectory()) {
					ret += countImportFiles(found[i]);
				}

				// NAND
				if (!(found[i].isFile() && found[i].getName().toLowerCase().endsWith(Config.EXPORT_METADATA_EXT))) {
					ret++;
				}
			}
		}

		return ret;
	}

	/**
	 * Copy InputStream to File.
	 */
	public static void copy(InputStream input, File output) throws IOException {
		FileOutputStream fos = new FileOutputStream(output);
		IOUtils.copy(input, fos);
		fos.flush();
		fos.close();
	}

	/**
	 * Copy Reader to File.
	 */
	public static void copy(Reader input, File output) throws IOException {
		FileOutputStream fos = new FileOutputStream(output);
		IOUtils.copy(input, fos);
		fos.flush();
		fos.close();
	}

	/**
	 * Copy File to OutputStream
	 */
	public static void copy(File input, OutputStream output) throws IOException {
		FileInputStream fis = new FileInputStream(input);
		IOUtils.copy(fis, output);
		fis.close();
	}

	/**
	 * Copy File to File
	 */
	public static void copy(File input, File output) throws IOException {
		org.apache.commons.io.FileUtils.copyFile(input, output);
	}

	/**
	 * Create "year / month / day" directory structure. 
	 */
	public static File createDateDir(String parent) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator + "dd");
		File dateDir = new File(parent, sdf.format(new Date()));

		if (!dateDir.exists()) {
			dateDir.mkdirs();
		}

		return dateDir;
	}

	/**
	 * Remove reserved characters from filename
	 *
	 * https://msdn.microsoft.com/en-us/library/aa365247
	 */
	public static String toValidFilename(String filename) {
		return filename.replaceAll("[\\\\/:\"*?<>|]+", "");
	}
}
