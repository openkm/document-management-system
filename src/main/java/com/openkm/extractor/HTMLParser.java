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

import org.apache.xerces.parsers.AbstractSAXParser;
import org.apache.xerces.xni.*;
import org.cyberneko.html.HTMLConfiguration;

/**
 * Helper class for HTML parsing
 */
public class HTMLParser extends AbstractSAXParser {
	private StringBuffer buffer;

	public HTMLParser() {
		super(new HTMLConfiguration());
	}

	public void startDocument(XMLLocator arg0, String arg1, NamespaceContext arg2, Augmentations arg3) throws XNIException {
		super.startDocument(arg0, arg1, arg2, arg3);
		buffer = new StringBuffer();
	}

	public void characters(XMLString xmlString, Augmentations augmentations) throws XNIException {
		super.characters(xmlString, augmentations);
		buffer.append(xmlString.toString());
	}

	private String filterAndJoin(String text) {
		boolean space = false;
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if ((c == '\n') || (c == ' ') || Character.isWhitespace(c)) {
				if (space) {
					continue;
				} else {
					space = true;
					buffer.append(' ');
					continue;
				}
			}

			space = false;
			buffer.append(c);
		}

		return buffer.toString();
	}

	/**
	 * Returns parsed content
	 *
	 * @return String Parsed content
	 */
	public String getContents() {
		String text = filterAndJoin(buffer.toString());
		return text;
	}
}
