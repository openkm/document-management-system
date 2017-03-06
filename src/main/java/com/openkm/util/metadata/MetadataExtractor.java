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

package com.openkm.util.metadata;

import com.catcode.odf.ODFMetaFileAnalyzer;
import com.catcode.odf.OpenDocumentMetadata;
import com.openkm.core.MimeTypeConfig;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class MetadataExtractor {
	private static Logger log = LoggerFactory.getLogger(MetadataExtractor.class);

	/**
	 * Extract metadata from PDF
	 */
	public static PdfMetadata pdfExtractor(InputStream is) throws IOException {
		PDDocument doc = PDDocument.load(is);
		PDDocumentInformation info = doc.getDocumentInformation();
		PdfMetadata md = new PdfMetadata();

		md.setNumberOfPages(doc.getNumberOfPages());
		md.setTitle(info.getTitle());
		md.setAuthor(info.getAuthor());
		md.setSubject(info.getSubject());
		md.setKeywords(info.getKeywords());
		md.setCreator(info.getCreator());
		md.setProducer(info.getProducer());
		md.setTrapped(info.getTrapped());
		md.setCreationDate(info.getCreationDate());
		md.setModificationDate(info.getModificationDate());

		log.info("pdfExtractor: {}", md);
		return md;
	}

	/**
	 * Extract metadata from Office Word
	 */
	public static OfficeMetadata officeExtractor(InputStream is, String mimeType) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(is);
		OfficeMetadata md = new OfficeMetadata();
		SummaryInformation si = null;

		if (MimeTypeConfig.MIME_MS_WORD.equals(mimeType)) {
			si = new WordExtractor(fs).getSummaryInformation();
		} else if (MimeTypeConfig.MIME_MS_EXCEL.equals(mimeType)) {
			si = new ExcelExtractor(fs).getSummaryInformation();
		} else if (MimeTypeConfig.MIME_MS_POWERPOINT.equals(mimeType)) {
			si = new PowerPointExtractor(fs).getSummaryInformation();
		}

		if (si != null) {
			md.setTitle(si.getTitle());
			md.setSubject(si.getSubject());
			md.setAuthor(si.getAuthor());
			md.setLastAuthor(si.getLastAuthor());
			md.setKeywords(si.getKeywords());
			md.setComments(si.getComments());
			md.setTemplate(si.getTemplate());
			md.setRevNumber(si.getRevNumber());
			md.setApplicationName(si.getApplicationName());
			md.setEditTime(si.getEditTime());
			md.setPageCount(si.getPageCount());
			md.setWordCount(si.getWordCount());
			md.setCharCount(si.getCharCount());
			md.setSecurity(si.getSecurity());

			Calendar createDateTime = Calendar.getInstance();
			createDateTime.setTime(si.getCreateDateTime());
			md.setCreateDateTime(createDateTime);

			Calendar lastSaveDateTime = Calendar.getInstance();
			lastSaveDateTime.setTime(si.getLastSaveDateTime());
			md.setLastSaveDateTime(lastSaveDateTime);

			Calendar lastPrinted = Calendar.getInstance();
			lastPrinted.setTime(si.getLastPrinted());
			md.setLastPrinted(lastPrinted);
		}

		log.info("officeExtractor: {}", md);
		return md;
	}

	public static void OpenOfficeExtractor(InputStream is) {
		ODFMetaFileAnalyzer analyzer = new ODFMetaFileAnalyzer();
		OpenDocumentMetadata odmt = analyzer.analyzeZip(is);
		OpenOfficeMetadata md = new OpenOfficeMetadata();

		if (odmt != null) {
			md.setTitle(odmt.getTitle());
			md.setSubject(odmt.getSubject());
			md.setCreator(odmt.getCreator());
			md.setInitialCreator(odmt.getInitialCreator());
			md.setKeyword(odmt.getKeyword());
			md.setDescription(odmt.getDescription());
			md.setEditingCycles(odmt.getEditingCycles());
			md.setEditingDuration((long) odmt.getEditingDuration().getSeconds() +
					odmt.getEditingDuration().getMinutes() * 60 +
					odmt.getEditingDuration().getHours() * 3600 +
					odmt.getEditingDuration().getDays() * 86400);
			md.setPageCount(odmt.getPageCount());
			md.setWordCount(odmt.getWordCount());
			md.setCharacterCount(odmt.getCharacterCount());

			Calendar creationDate = Calendar.getInstance();
			creationDate.setTime(odmt.getCreationDate());
			md.setCreationDate(creationDate);

			Calendar date = Calendar.getInstance();
			date.setTime(odmt.getDate());
			md.setDate(date);

			Calendar printDate = Calendar.getInstance();
			printDate.setTime(odmt.getPrintDate());
			md.setPrintDate(printDate);
		}

		log.info("OpenOfficeExtractor: {}", odmt);
	}
}
