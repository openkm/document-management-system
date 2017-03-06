/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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

package com.openkm.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.*;
import com.openkm.api.OKMDocument;
import com.openkm.automation.AutomationException;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import de.svenjacobs.loremipsum.LoremIpsum;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * http://itextpdf.sourceforge.net/howtosign.html
 *
 * @author pavila
 */
public class PDFUtils {
	private static Logger log = LoggerFactory.getLogger(PDFUtils.class);

	/**
	 * Fill PDF form
	 */
	@SuppressWarnings("rawtypes")
	public static void fillForm(InputStream input, Map<String, Object> values, OutputStream output) throws FileNotFoundException,
			DocumentException, TemplateException, IOException {
		log.debug("fillForm({}, {}, {})", new Object[]{input, values, output});
		PdfReader reader = new PdfReader(input);
		PdfStamper stamper = new PdfStamper(reader, output);
		AcroFields fields = stamper.getAcroFields();
		PRAcroForm form = reader.getAcroForm();
		boolean formFlattening = false;

		if (form != null) {
			for (Iterator it = form.getFields().iterator(); it.hasNext(); ) {
				PRAcroForm.FieldInformation field = (PRAcroForm.FieldInformation) it.next();
				String fieldValue = fields.getField(field.getName());
				log.debug("Field: {}, Value: '{}'", field.getName(), fieldValue);

				if (fieldValue != null && !fieldValue.isEmpty()) {
					// if (values.containsKey(field.getName())) {
					String result = TemplateUtils.replace("PDF_FILL_FORM", fieldValue, values);
					log.debug("Field '{}' set to '{}' (by expression)", field.getName(), result);
					fields.setFieldProperty(field.getName(), "textfont", getBaseFont(), null);
					fields.setField(field.getName(), result);
					stamper.partialFormFlattening(field.getName());
					formFlattening = true;
					// } else {
					// log.warn("Field '{}' (expression ignored because not included in map)", field.getName());
					// }
				} else {
					Object value = values.get(field.getName());

					if (value != null) {
						log.debug("Field '{}' set to '{}' (by field name)", field.getName(), value);
						fields.setFieldProperty(field.getName(), "textfont", getBaseFont(), null);
						fields.setField(field.getName(), value.toString());
						stamper.partialFormFlattening(field.getName());
						formFlattening = true;
					} else {
						log.warn("Field '{}' (value ignored because not included in map)", field.getName());
					}
				}
			}
		}

		stamper.setFormFlattening(formFlattening);
		stamper.close();
		reader.close();
	}

	/**
	 * List form fields
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> listFormFields(String input) throws FileNotFoundException, DocumentException, IOException {
		log.debug("listFormFields({})", input);
		List<String> formFields = new ArrayList<String>();
		PdfReader reader = new PdfReader(input);
		PRAcroForm form = reader.getAcroForm();

		if (form != null) {
			for (Iterator it = form.getFields().iterator(); it.hasNext(); ) {
				PRAcroForm.FieldInformation field = (PRAcroForm.FieldInformation) it.next();
				formFields.add(field.getName());
			}
		}

		reader.close();
		log.debug("listFormFields: {}", formFields);
		return formFields;
	}

	/**
	 * Generate sample PDF
	 */
	public static void generateSample(int paragraphs, OutputStream os) throws DocumentException {
		LoremIpsum li = new LoremIpsum();
		Document doc = new Document(PageSize.A4, 25, 25, 25, 25);
		PdfWriter.getInstance(doc, os);
		doc.open();

		for (int i = 0; i < paragraphs; i++) {
			doc.add(new Paragraph(li.getParagraphs()));
		}

		doc.close();
	}

	/**
	 * Merge several PDFs into a new one
	 */
	public static void merge(List<InputStream> inputs, OutputStream output) throws IOException, DocumentException {
		Document document = new Document();

		try {
			PdfSmartCopy copy = new PdfSmartCopy(document, output);
			document.open();

			for (InputStream is : inputs) {
				PdfReader reader = new PdfReader(is);

				for (int i = 1; i <= reader.getNumberOfPages(); i++) {
					copy.addPage(copy.getImportedPage(reader, i));
				}
			}

			output.flush();
			document.close();
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	/**
	 * Merge several PDFs into a new one
	 *
	 * @param token Authentication info.
	 * @param docIds The paths that identifies an unique documents or its UUID.
	 * @param dstPath The path of the resulting PDF document (with the name).
	 */
	public static void merge(String token, List<String> docIds, String dstPath) throws IOException, DocumentException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, ExtensionException, AutomationException,
			LockException, VersionException {
		List<InputStream> docIsLst = new ArrayList<InputStream>();
		File docOut = null;
		InputStream docIs = null;
		OutputStream docOs = null;

		try {
			// Get documents content
			for (String docId : docIds) {
				docIs = OKMDocument.getInstance().getContent(token, docId, false);
				docIsLst.add(docIs);
			}

			// Merge PDFs
			docOut = FileUtils.createTempFileFromMime(MimeTypeConfig.MIME_PDF);
			docOs = new FileOutputStream(docOut);
			merge(docIsLst, docOs);

			// Upload to OpenKM
			try {
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().createSimple(token, dstPath, docIs);
			} catch (ItemExistsException e) {
				IOUtils.closeQuietly(docIs);
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().checkout(token, dstPath);
				OKMDocument.getInstance().checkin(token, dstPath, docIs, "Document to PDF");
			}
		} finally {
			IOUtils.closeQuietly(docIs);
			IOUtils.closeQuietly(docOs);

			for (InputStream is : docIsLst) {
				IOUtils.closeQuietly(is);
			}

			FileUtils.deleteQuietly(docOut);
		}
	}

	/**
	 * Mark PDF to be printed.
	 */
	public static File markToPrint(File input) throws DocumentException, IOException {
		File tmpPrint = File.createTempFile("okm", ".pdf");
		InputStream is = null;

		try {
			is = new FileInputStream(input);
			PdfReader pdfReader = new PdfReader(is);
			OutputStream out = new FileOutputStream(tmpPrint);
			PdfStamper stamper = new PdfStamper(pdfReader, out);
			PdfWriter stamperWriter = stamper.getWriter();
			stamperWriter.addJavaScript("this.print(false);");
			stamper.close();
			pdfReader.close();
		} finally {
			IOUtils.closeQuietly(is);
		}

		return tmpPrint;
	}

	/**
	 * Encrypt a document and set permissions
	 *
	 * If a PDF is encrypted using an owner password but no user password, anyone can open the PDF and is restricted by the permissions
	 * selected during encryption. Only someone opening that PDF with its owner password is permitted has unlimited access to the PDF.
	 */
	public static void encrypt(InputStream input, String userPassword, String ownerPassword, int permissions, OutputStream output) throws
			DocumentException, IOException {
		PdfReader pdfReader = new PdfReader(input);
		PdfStamper stamper = new PdfStamper(pdfReader, output);
		stamper.setEncryption(userPassword != null ? userPassword.getBytes() : null, ownerPassword != null ? ownerPassword.getBytes() : null,
				permissions, true);
		stamper.close();
		pdfReader.close();
	}

	/**
	 * Encrypt a document and set permissions
	 *
	 * If a PDF is encrypted using an owner password but no user password, anyone can open the PDF and is restricted by the permissions
	 * selected during encryption. Only someone opening that PDF with its owner password is permitted has unlimited access to the PDF.
	 */
	public static File encrypt(File input, String userPassword, String ownerPassword, int permissions) throws DocumentException, IOException {
		File tmp = File.createTempFile("okm", ".pdf");
		InputStream is = null;
		OutputStream out = null;

		try {
			is = new FileInputStream(input);
			out = new FileOutputStream(tmp);
			encrypt(is, userPassword, ownerPassword, permissions, out);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(out);
		}

		return tmp;
	}

	/**
	 * Encrypt a document and set permissions.
	 *
	 * If a PDF is encrypted using an owner password but no user password, anyone can open the PDF and is restricted by the permissions
	 * selected during encryption. Only someone opening that PDF with its owner password is permitted has unlimited access to the PDF.
	 *
	 * @param token Authentication info.
	 * @param docId The path that identifies an unique document or its UUID.
	 * @param userPassword The user password.
	 * @param ownerPassword The owner password.
	 * @param permissions ORed PDF permissions.
	 * @param dstPath The path of the resulting PDF document (with the name).
	 */
	public static void encrypt(String token, String docId, String userPassword, String ownerPassword, int permissions, String dstPath)
			throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, IOException,
			DocumentException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, VersionException,
			ExtensionException, UnsupportedMimeTypeException, ItemExistsException, AutomationException {
		File pdfCrypt = null;
		InputStream docIs = null;
		OutputStream docOut = null;

		try {
			// Get document content
			docIs = OKMDocument.getInstance().getContent(token, docId, false);

			// Encrypt
			pdfCrypt = FileUtils.createTempFileFromMime(MimeTypeConfig.MIME_PDF);
			docOut = new FileOutputStream(pdfCrypt);
			PDFUtils.encrypt(docIs, userPassword, ownerPassword, permissions, docOut);

			// Upload to OpenKM
			try {
				docIs = new FileInputStream(pdfCrypt);
				OKMDocument.getInstance().createSimple(token, dstPath, docIs);
			} catch (ItemExistsException e) {
				IOUtils.closeQuietly(docIs);
				docIs = new FileInputStream(pdfCrypt);
				OKMDocument.getInstance().checkout(token, dstPath);
				OKMDocument.getInstance().checkin(token, dstPath, docIs, "Encrypt PDF");
			}
		} finally {
			IOUtils.closeQuietly(docIs);
			IOUtils.closeQuietly(docOut);
			FileUtils.deleteQuietly(pdfCrypt);
		}
	}

	/**
	 * Obtain base font for PDF processing
	 */
	private static BaseFont getBaseFont() throws DocumentException, IOException {
		String fontName = Config.HOME_DIR + "/lib/unicode.ttf";
		String fontEncoding = BaseFont.IDENTITY_H;

		if (!new File(fontName).exists()) {
			log.warn("Unicode TTF font not found: {}", fontName);
			fontName = BaseFont.HELVETICA;
			fontEncoding = BaseFont.WINANSI;
		}

		return BaseFont.createFont(fontName, fontEncoding, BaseFont.EMBEDDED);
	}
}
