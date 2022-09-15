/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Text extractor for MS Office 2007 documents.
 */
@PluginImplementation
public class MsOffice2007TextExtractor extends AbstractTextExtractor {
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

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		log.debug("extractText({}, {}, {})", stream, type, encoding);

		try {
			switch (type) {
				case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
				case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
					try (OPCPackage opcPackage = OPCPackage.open(stream)) {
						XWPFWordExtractor extractor = new XWPFWordExtractor(opcPackage);
						String text = extractor.getText();
						log.debug("TEXT: " + text);
						return text;
					}

				case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
				case "application/vnd.openxmlformats-officedocument.spreadsheetml.template":
					try (OPCPackage opcPackage = OPCPackage.open(stream)) {
						XSSFExcelExtractor extractor = new XSSFExcelExtractor(opcPackage);
						String text = extractor.getText();
						log.debug("TEXT: " + text);
						return text;
					}

				case "application/vnd.openxmlformats-officedocument.presentationml.template":
				case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
				case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
					try (OPCPackage opcPackage = OPCPackage.open(stream)) {
						XSLFPowerPointExtractor extractor = new XSLFPowerPointExtractor(opcPackage);
						String text = extractor.getText();
						log.debug("TEXT: " + text);
						return text;
					}

				default:
					return "";
			}
		} catch (OpenXML4JException | XmlException e) {
			log.warn("Failed to extract Microsoft Office 2007 text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
}
