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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.IOException;
import java.io.InputStream;

/**
 * Text extractor for Rich Text Format (RTF)
 */
public class RTFTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RTFTextExtractor.class);

	/**
	 * Creates a new <code>RTFTextExtractor</code> instance.
	 */
	public RTFTextExtractor() {
		super(new String[]{"application/rtf", "text/rtf"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {

		try {
			RTFEditorKit rek = new RTFEditorKit();
			DefaultStyledDocument doc = new DefaultStyledDocument();
			rek.read(stream, doc, 0);
			String text = doc.getText(0, doc.getLength());
			return text;
		} catch (Throwable e) {
			logger.warn("Failed to extract RTF text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}
}
