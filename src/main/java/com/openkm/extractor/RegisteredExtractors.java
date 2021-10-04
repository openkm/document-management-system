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
import com.openkm.dao.PluginDAO;
import com.openkm.module.db.stuff.PersistentFile;
import com.openkm.util.PluginUtils;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import net.xeoh.plugins.base.Plugin;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pavila
 */
public class RegisteredExtractors {
	private static final Logger log = LoggerFactory.getLogger(RegisteredExtractors.class);
	public static final String PLUGIN_URI = "classpath://com.openkm.extractor.**";
	private static List<TextExtractor> extractorList = null;
	private static final int MIN_EXTRACTION = 16;

	/**
	 * Return guessed text extractor
	 */
	public static TextExtractor getTextExtractor(String mimeType) throws URISyntaxException {
		for (TextExtractor te : findExtractors(false)) {
			for (String teMime : te.getContentTypes()) {
				if (teMime.equals(mimeType)) {
					log.debug("Text extractor for '{}' found: {}", mimeType, te.getClass());
					return te;
				}
			}
		}

		return null;
	}

	/**
	 * Check for registered text extractor
	 */
	public static boolean isRegistered(String className) throws URISyntaxException {
		for (TextExtractor te : findExtractors(false)) {
			if (te.getClass().getCanonicalName().equals(className)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Extract text to be indexed
	 */
	public static String getText(String docPath, String mimeType, String encoding, InputStream isContent) throws IOException {
		log.debug("getText({}, {}, {}, {})", docPath, mimeType, encoding, isContent);
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
		String text = null;

		try {
			TextExtractor te = getTextExtractor(mimeType);

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
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
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
	private static String getDbText(String docUuid, String mimeType, String encoding, InputStream isContent) throws
			PathNotFoundException, DatabaseException {
		log.debug("getDbText({}, {}, {}, {})", docUuid, mimeType, encoding, isContent);
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

	//
	// DB Methods
	//

	/**
	 * Get all converters
	 */
	public static synchronized List<TextExtractor> findExtractors(boolean reload) throws URISyntaxException {
		log.debug("findExtractors({})", reload);

		if (extractorList == null || reload) {
			extractorList = new ArrayList<>();
			URI uri = new URI(PLUGIN_URI);

			for (Plugin plg : PluginUtils.getPlugins(uri, TextExtractor.class)) {
				extractorList.add((TextExtractor) plg);
			}
		}

		return extractorList;
	}
}
