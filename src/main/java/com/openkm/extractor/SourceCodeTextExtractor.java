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

package com.openkm.extractor;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Text extractor for source code.
 */
public class SourceCodeTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(SourceCodeTextExtractor.class);

	/**
	 * Creates a new <code>AudioTextExtractor</code> instance.
	 */
	public SourceCodeTextExtractor() {
		super(new String[]{"text/x-java", "text/css", "text/x-csrc", "text/x-sql",
				"application/x-php", "application/javascript"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			if (encoding != null) {
				return IOUtils.toString(stream, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			log.warn("Unsupported encoding '{}', using default ({}) instead.", encoding, System.getProperty("file.encoding"));
		}

		return IOUtils.toString(stream);
	}
}
