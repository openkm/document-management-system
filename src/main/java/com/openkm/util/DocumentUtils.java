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
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.NodeDocumentVersionDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.util.metadata.MetadataExtractor;
import com.openkm.util.metadata.OfficeMetadata;
import com.openkm.util.metadata.OpenOfficeMetadata;
import com.openkm.util.metadata.PdfMetadata;
import org.apache.commons.io.IOUtils;
import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

public class DocumentUtils {
	private static Logger log = LoggerFactory.getLogger(DocumentUtils.class);

	public void staticExtractMetadata(NodeDocument nDoc) {
		InputStream is = null;

		try {
			if (MimeTypeConfig.MIME_PDF.equals(nDoc.getMimeType())) {
				is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(nDoc.getUuid(), true);
				PdfMetadata md = MetadataExtractor.pdfExtractor(is);
				log.info("{}", md);
			} else if (MimeTypeConfig.MIME_MS_WORD.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_MS_EXCEL.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_MS_POWERPOINT.equals(nDoc.getMimeType())) {
				is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(nDoc.getUuid(), true);
				OfficeMetadata md = MetadataExtractor.officeExtractor(is, nDoc.getMimeType());
				log.info("{}", md);
			} else if (MimeTypeConfig.MIME_OO_TEXT.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_OO_SPREADSHEET.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_OO_PRESENTATION.equals(nDoc.getMimeType())) {
				is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(nDoc.getUuid(), true);
				OpenOfficeMetadata md = new OpenOfficeMetadata();
				log.info("{}", md);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Text spell checker
	 */
	public static String spellChecker(String text) throws IOException {
		log.debug("spellChecker({})", text);
		StringBuilder sb = new StringBuilder();

		if (Config.SYSTEM_OPENOFFICE_DICTIONARY.equals("")) {
			log.warn("OpenOffice dictionary not configured");
			sb.append(text);
		} else {
			log.info("Using OpenOffice dictionary: {}", Config.SYSTEM_OPENOFFICE_DICTIONARY);
			ZipFile zf = new ZipFile(Config.SYSTEM_OPENOFFICE_DICTIONARY);
			OpenOfficeSpellDictionary oosd = new OpenOfficeSpellDictionary(zf);
			SpellChecker sc = new SpellChecker(oosd);
			sc.setCaseSensitive(false);
			StringTokenizer st = new StringTokenizer(text);

			while (st.hasMoreTokens()) {
				String w = st.nextToken();
				List<String> s = sc.getDictionary().getSuggestions(w);

				if (s.isEmpty()) {
					sb.append(w).append(" ");
				} else {
					sb.append(s.get(0)).append(" ");
				}
			}

			zf.close();
		}

		log.debug("spellChecker: {}", sb.toString());
		return sb.toString();
	}

	/**
	 * Obtain lock token from node id
	 */
	public static String getLockToken(String id) {
		StringBuffer buf = new StringBuffer();
		buf.append(id.toString());
		buf.append('-');
		buf.append(getCheckDigit(id.toString()));
		return buf.toString();
	}

	/**
	 * Calculate check digit for lock token
	 *
	 * @see org.apache.jackrabbit.core.lock.LockToken.getCheckDigit(String uuid)
	 */
	private static char getCheckDigit(String uuid) {
		int result = 0;
		int multiplier = 36;

		for (int i = 0; i < uuid.length(); i++) {
			char c = uuid.charAt(i);
			if (c >= '0' && c <= '9') {
				int num = c - '0';
				result += multiplier * num;
				multiplier--;
			} else if (c >= 'A' && c <= 'F') {
				int num = c - 'A' + 10;
				result += multiplier * num;
				multiplier--;
			} else if (c >= 'a' && c <= 'f') {
				int num = c - 'a' + 10;
				result += multiplier * num;
				multiplier--;
			}
		}

		int rem = result % 37;
		if (rem != 0) {
			rem = 37 - rem;
		}

		if (rem >= 0 && rem <= 9) {
			return (char) ('0' + rem);
		} else if (rem >= 10 && rem <= 35) {
			return (char) ('A' + rem - 10);
		} else {
			return '+';
		}
	}
}
