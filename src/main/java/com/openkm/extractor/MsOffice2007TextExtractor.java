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

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Text extractor for MS Office 2007 documents.
 */
public class MsOffice2007TextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(MsOffice2007TextExtractor.class);

	/**
	 * Creates a new <code>MsOffice2007TextExtractor</code> instance.
	 */
	public MsOffice2007TextExtractor() {
		super(new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.template",
				"application/vnd.openxmlformats-officedocument.presentationml.template",
				"application/vnd.openxmlformats-officedocument.presentationml.slideshow",
				"application/vnd.openxmlformats-officedocument.presentationml.presentation",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.template"});
	}

	//-------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		ZipInputStream zis = null;

		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			MsOffice2007ContentHandler contentHandler = null;

			if (type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
					type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.template")) {
				contentHandler = new WordprocessingMLContentHandler();
			} else if (type.equals("application/vnd.openxmlformats-officedocument.presentationml.template") ||
					type.equals("application/vnd.openxmlformats-officedocument.presentationml.slideshow") ||
					type.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
				contentHandler = new PresentationMLContentHandler();
			} else if (type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
					type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.template")) {
				contentHandler = new SpreadsheetMLContentHandler();
			}

			xmlReader.setContentHandler(contentHandler);
			zis = new ZipInputStream(stream);
			ZipEntry ze;
			StringBuffer sb = new StringBuffer();

			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().startsWith(contentHandler.getFilePattern())) {
					// It is unspecified whether the XML parser closes the stream when
					// done parsing. To ensure that the stream gets closed just once,
					// we prevent the parser from closing it by catching the close()
					// call and explicitly close the stream in a finally block.
					InputSource is = new InputSource(new FilterInputStream(zis) {
						public void close() {
						}
					});

					log.debug("Parsing " + ze);
					xmlReader.parse(is);
					sb.append(contentHandler.getContent());
				} else {
					log.debug("- " + ze);
				}
			}

			log.debug("TEXT: " + sb.toString());
			return sb.toString();
		} catch (ParserConfigurationException e) {
			log.warn("Failed to extract Microsoft Office 2007 text content", e);
			return "";
		} catch (SAXException e) {
			log.warn("Failed to extract Microsoft Office 2007 text content", e);
			return "";
		} finally {
			IOUtils.closeQuietly(zis);
			IOUtils.closeQuietly(stream);
		}
	}
}
