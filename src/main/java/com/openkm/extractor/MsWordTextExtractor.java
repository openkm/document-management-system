/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2016 Paco Avila & Josep Llort
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

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Text extractor for Microsoft Word documents.
 */
public class MsWordTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MsWordTextExtractor.class);

	/**
	 * Force loading of dependent class.
	 */
	static {
		WordExtractor.class.getName();
	}

	/**
	 * Creates a new <code>MsWordTextExtractor</code> instance.
	 */
	public MsWordTextExtractor() {
		super(new String[]{"application/vnd.ms-word", "application/msword"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc} Returns an empty reader if an error occured extracting text from
	 * the word document.
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			return new WordExtractor(stream).getText();
		} catch (Exception e) {
			logger.warn("Failed to extract Word text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}
}
