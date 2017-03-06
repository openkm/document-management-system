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

import org.apache.poi.hsmf.MAPIMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Text extractor for Microsoft Outlook messages.
 */
public class MsOutlookTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MsOutlookTextExtractor.class);

	/**
	 * Force loading of dependent class.
	 */
	static {
		MAPIMessage.class.getName();
	}

	/**
	 * Creates a new <code>MsOutlookTextExtractor</code> instance.
	 */
	public MsOutlookTextExtractor() {
		super(new String[]{"application/vnd.ms-outlook"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc} Returns an empty reader if an error occured extracting text from
	 * the outlook message.
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			MAPIMessage message = new MAPIMessage(stream);
			StringBuffer buffer = new StringBuffer();
			buffer.append(message.getDisplayFrom()).append('\n');
			buffer.append(message.getDisplayTo()).append('\n');
			buffer.append(message.getSubject()).append('\n');
			buffer.append(message.getTextBody());
			return buffer.toString();
		} catch (Exception e) {
			logger.warn("Failed to extract Message content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}
}
