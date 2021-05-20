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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.CharArrayWriter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Text extractor for XML documents. This class extracts the text content
 * and attribute values from XML documents.
 * <p>
 * This class can handle any XML-based format (<code>application/xml+something</code>), not just the base XML content
 * types reported by {@link #getContentTypes()}. However, it often makes sense to use more specialized extractors that
 * better understand the specific content type.
 */
public class XMLTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(XMLTextExtractor.class);

	/**
	 * Creates a new <code>XMLTextExtractor</code> instance.
	 */
	public XMLTextExtractor() {
		super(new String[]{"text/xml", "application/xml", "application/vnd.scribus"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * Returns a reader for the text content of the given XML document.
	 * Returns an empty reader if the given encoding is not supported or
	 * if the XML document could not be parsed.
	 *
	 * @param stream XML document
	 * @param type XML content type
	 * @param encoding character encoding, or <code>null</code>
	 * @return reader for the text content of the given XML document,
	 *         or an empty reader if the document could not be parsed
	 * @throws IOException if the XML document stream can not be closed
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			CharArrayWriter writer = new CharArrayWriter();
			ExtractorHandler handler = new ExtractorHandler(writer);

			// TODO: Use a pull parser to avoid the memory overhead
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
			reader.setFeature("http://xml.org/sax/features/validation", false);
			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);

			// It is unspecified whether the XML parser closes the stream when
			// done parsing. To ensure that the stream gets closed just once,
			// we prevent the parser from closing it by catching the close()
			// call and explicitly close the stream in a finally block.
			InputSource source = new InputSource(new FilterInputStream(stream) {
				public void close() {
				}
			});

			if (encoding != null) {
				try {
					Charset.forName(encoding);
					source.setEncoding(encoding);
				} catch (Exception e) {
					logger.warn("Unsupported encoding '{}', using default ({}) instead.",
							new Object[]{encoding, System.getProperty("file.encoding")});
				}
			}

			reader.parse(source);
			return writer.toString();
		} catch (ParserConfigurationException | SAXException e) {
			logger.warn("Failed to extract XML text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}
}
