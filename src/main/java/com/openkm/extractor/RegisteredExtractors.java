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

package com.openkm.extractor;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.module.db.stuff.PersistentFile;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pavila
 */
public class RegisteredExtractors {
	private static Logger log = LoggerFactory.getLogger(RegisteredExtractors.class);
	private static Map<String, TextExtractor> engine = new HashMap<String, TextExtractor>();
	private static final int MIN_EXTRACTION = 16;

	/**
	 * Initialize text extractors from REGISTERED_TEXT_EXTRACTORS
	 */
	public static synchronized void init() {
		log.info("Initializing text extractors");

		for (String clazz : Config.REGISTERED_TEXT_EXTRACTORS) {
			try {
				Object obj = Class.forName(clazz).newInstance();

				if (obj instanceof TextExtractor) {
					TextExtractor te = (TextExtractor) obj;

					for (String contType : te.getContentTypes()) {
						log.info("Registering {} for '{}'", te.getClass().getCanonicalName(), contType);
						engine.put(contType, te);
					}
				} else {
					log.warn("Unknown text extractor class: {}", clazz);
				}
			} catch (ClassNotFoundException e) {
				log.warn("Extractor class not found: {}", clazz, e);
			} catch (LinkageError e) {
				log.warn("Extractor dependency not found: {}", clazz, e);
			} catch (IllegalAccessException e) {
				log.warn("Extractor constructor not accessible: {}", clazz, e);
			} catch (InstantiationException e) {
				log.warn("Extractor instantiation failed: {}", clazz, e);
			}
		}
	}

	/**
	 * Return registered content types
	 */
	public static String[] getContentTypes() {
		return engine.keySet().toArray(new String[engine.keySet().size()]);
	}

	/**
	 * Return guessed text extractor
	 */
	public static TextExtractor getTextExtractor(String mimeType) {
		return engine.get(mimeType);
	}

	/**
	 * Check for registered text extractor
	 */
	public static boolean isRegistered(String className) {
		List<String> classes = Config.REGISTERED_TEXT_EXTRACTORS;

		for (String name : classes) {
			if (name.equals(className)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Extract text to be indexed
	 */
	public static String getText(String docPath, String mimeType, String encoding, InputStream isContent) throws IOException {
		log.debug("getText({}, {}, {}, {})", new Object[]{docPath, mimeType, encoding, isContent});
		long begin = System.currentTimeMillis();
		String failureMessage = "Unknown error";
		boolean failure = false;
		String text = null;

		try {
			text = getText(mimeType, encoding, isContent);

			// Check for minimum text extraction size
			if (text.length() < MIN_EXTRACTION) {
				failureMessage = "Too few text extracted";
				failure = true;
			}
		} catch (Exception e) {
			log.warn("Text extraction failure: {}", e.getMessage());
			failureMessage = e.getMessage();
			failure = true;
		}

		SystemProfiling.log(docPath + ", " + mimeType, System.currentTimeMillis() - begin);
		log.trace("getText.Time: {} ms for {}", System.currentTimeMillis() - begin, docPath);

		if (failure) {
			throw new IOException(failureMessage);
		}

		log.debug("getText: {}", text);
		return text;
	}

	/**
	 * Extract text to be indexed
	 */
	public static String getText(String mimeType, String encoding, InputStream isContent) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(isContent);
		TextExtractor te = engine.get(mimeType);
		String text = null;

		if (te != null) {
			if (mimeType.startsWith("text/") && encoding == null) {
				CharsetDetector detector = new CharsetDetector();
				detector.setText(bis);
				CharsetMatch cm = detector.detect();
				encoding = cm.getName();
			}

			text = te.extractText(bis, mimeType, encoding);
		} else {
			throw new IOException("Full text indexing of '" + mimeType + "' is not supported");
		}


		IOUtils.closeQuietly(bis);
		return text;
	}

	//
	// DB Methods
	//

	/**
	 * Extract text to be indexed
	 */
	@SuppressWarnings("unused")
	private static String getDbText(String docUuid, String mimeType, String encoding, InputStream isContent)
			throws IOException, PathNotFoundException, DatabaseException {
		log.debug("getDbText({}, {}, {}, {})", new Object[]{docUuid, mimeType, encoding, isContent});
		String text = null;

		try {
			text = getText(null, mimeType, encoding, isContent);
		} catch (IOException e) {
			if (docUuid != null) {
				String nodePath = NodeBaseDAO.getInstance().getPathFromUuid(docUuid);
				log.warn("There was a problem extracting text from '{}'", nodePath);
				UserActivity.log(Config.SYSTEM_USER, "MISC_TEXT_EXTRACTION_FAILURE", docUuid, nodePath, e.getMessage());
			}
		}

		log.debug("getDbText: {}", text);
		return text;
	}

	/**
	 * Sample lazy text extraction.
	 *
	 * @see com.openkm.module.db.stuff.LazyField
	 */
	public static String getText(PersistentFile persistentFile) throws IOException {
		InputStream isContent = null;
		String text = null;

		try {
			isContent = persistentFile.getInputStream();
			text = getText("text/plain", "UTF-8", isContent);
			return text;
		} finally {
			IOUtils.closeQuietly(isContent);
		}
	}
}
