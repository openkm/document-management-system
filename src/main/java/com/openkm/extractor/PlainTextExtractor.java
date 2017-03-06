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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Text extractor for plain text.
 */
public class PlainTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PlainTextExtractor.class);

	/**
	 * Creates a new <code>PlainTextExtractor</code> instance.
	 */
	public PlainTextExtractor() {
		super(new String[]{"text/plain"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * Wraps the given input stream to an {@link InputStreamReader} using
	 * the given encoding, or the platform default encoding if the encoding
	 * is not given or is unsupported. Closes the stream and returns an empty
	 * reader if the given encoding is not supported.
	 *
	 * @param stream binary stream
	 * @param type ignored
	 * @param encoding character encoding, optional
	 * @return reader for the plain text content
	 * @throws IOException if the binary stream can not be closed in case
	 *         of an encoding issue
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			if (encoding != null) {
				return IOUtils.toString(stream, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			logger.warn("Unsupported encoding '{}', using default ({}) instead.",
					new Object[]{encoding, System.getProperty("file.encoding")});
		}

		return IOUtils.toString(stream);
	}
}
