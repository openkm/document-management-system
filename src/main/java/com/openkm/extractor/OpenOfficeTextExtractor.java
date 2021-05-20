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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Text extractor for OpenOffice documents.
 */
public class OpenOfficeTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(OpenOfficeTextExtractor.class);

	/**
	 * Creates a new <code>OpenOfficeTextExtractor</code> instance.
	 */
	public OpenOfficeTextExtractor() {
		super(new String[]{"application/vnd.oasis.opendocument.database", "application/vnd.oasis.opendocument.formula",
				"application/vnd.oasis.opendocument.graphics", "application/vnd.oasis.opendocument.presentation",
				"application/vnd.oasis.opendocument.spreadsheet", "application/vnd.oasis.opendocument.text",
				"application/vnd.sun.xml.calc", "application/vnd.sun.xml.draw", "application/vnd.sun.xml.impress",
				"application/vnd.sun.xml.writer"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);

			ZipInputStream zis = new ZipInputStream(stream);
			ZipEntry ze = zis.getNextEntry();

			while (ze != null && !ze.getName().equals("content.xml")) {
				ze = zis.getNextEntry();
			}

			OpenOfficeContentHandler contentHandler = new OpenOfficeContentHandler();
			xmlReader.setContentHandler(contentHandler);

			try {
				xmlReader.parse(new InputSource(zis));
			} finally {
				zis.close();
			}

			return contentHandler.getContent();
		} catch (ParserConfigurationException | SAXException e) {
			logger.warn("Failed to extract OpenOffice text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}

	// --------------------------------------------< OpenOfficeContentHandler >

	private static class OpenOfficeContentHandler extends DefaultHandler {
		private StringBuffer content;
		private boolean appendChar;

		public OpenOfficeContentHandler() {
			content = new StringBuffer();
			appendChar = false;
		}

		/**
		 * Returns the text content extracted from parsed content.xml
		 */
		public String getContent() {
			return content.toString();
		}

		public void startElement(String namespaceURI, String localName, String rawName, Attributes atts) throws SAXException {
			if (rawName.startsWith("text:")) {
				appendChar = true;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (appendChar) {
				content.append(ch, start, length).append(" ");
			}
		}

		public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
			appendChar = false;
		}
	}
}
