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

import bsh.EvalError;
import com.lowagie.text.*;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;
import com.openkm.api.OKMDocument;
import com.openkm.automation.AutomationException;
import com.openkm.bean.ExecutionResult;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.extractor.PdfTextExtractor;
import freemarker.template.TemplateException;
import jashi.JaSHi;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class DocConverter {
	private static Logger log = LoggerFactory.getLogger(DocConverter.class);
	public static ArrayList<String> validOpenOffice = new ArrayList<String>();
	public static ArrayList<String> validImageMagick = new ArrayList<String>();
	private static ArrayList<String> validGhoscript = new ArrayList<String>();
	private static ArrayList<String> validInternal = new ArrayList<String>();
	private static DocConverter instance = null;
	private static OfficeManager officeManager = null;

	private DocConverter() {
		// Basic
		validOpenOffice.add("text/plain");
		validOpenOffice.add("text/html");
		validOpenOffice.add("text/csv");
		validOpenOffice.add("application/rtf");

		// OpenOffice.org OpenDocument
		validOpenOffice.add("application/vnd.oasis.opendocument.text");
		validOpenOffice.add("application/vnd.oasis.opendocument.presentation");
		validOpenOffice.add("application/vnd.oasis.opendocument.spreadsheet");
		validOpenOffice.add("application/vnd.oasis.opendocument.graphics");
		validOpenOffice.add("application/vnd.oasis.opendocument.database");

		// Microsoft Office
		validOpenOffice.add("application/msword");
		validOpenOffice.add("application/vnd.ms-excel");
		validOpenOffice.add("application/vnd.ms-powerpoint");

		// Microsoft Office 2007
		validOpenOffice.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		validOpenOffice.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		validOpenOffice.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");

		// Postcript
		validGhoscript.add("application/postscript");

		// Images
		validImageMagick.add("image/jpeg");
		validImageMagick.add("image/png");
		validImageMagick.add("image/gif");
		validImageMagick.add("image/tiff");
		validImageMagick.add("image/bmp");
		validImageMagick.add("image/svg+xml");
		validImageMagick.add("image/x-psd");

		// Internal conversion
		validInternal.add(MimeTypeConfig.MIME_ZIP);
		validInternal.add(MimeTypeConfig.MIME_XML);
		validInternal.add(MimeTypeConfig.MIME_SQL);
		validInternal.add(MimeTypeConfig.MIME_JAVA);
		validInternal.add(MimeTypeConfig.MIME_PHP);
		validInternal.add(MimeTypeConfig.MIME_BSH);
		validInternal.add(MimeTypeConfig.MIME_SH);
	}

	/**
	 * Retrieve class instance
	 */
	public static synchronized DocConverter getInstance() {
		if (instance == null) {
			instance = new DocConverter();

			if (!Config.SYSTEM_OPENOFFICE_PATH.equals("")) {
				log.info("*** Build Office Manager ***");
				log.info("{}={}", Config.PROPERTY_SYSTEM_OPENOFFICE_PATH, Config.SYSTEM_OPENOFFICE_PATH);
				log.info("{}={}", Config.PROPERTY_SYSTEM_OPENOFFICE_TASKS, Config.SYSTEM_OPENOFFICE_TASKS);
				log.info("{}={}", Config.PROPERTY_SYSTEM_OPENOFFICE_PORT, Config.SYSTEM_OPENOFFICE_PORT);

				officeManager = new DefaultOfficeManagerConfiguration().setOfficeHome(Config.SYSTEM_OPENOFFICE_PATH)
						.setMaxTasksPerProcess(Config.SYSTEM_OPENOFFICE_TASKS).setPortNumber(Config.SYSTEM_OPENOFFICE_PORT)
						.buildOfficeManager();
			} else {
				log.warn("{} not configured", Config.PROPERTY_SYSTEM_OPENOFFICE_PATH);

				if (!Config.SYSTEM_OPENOFFICE_SERVER.equals("")) {
					log.warn("but {} is configured", Config.PROPERTY_SYSTEM_OPENOFFICE_SERVER);
					log.info("{}={}", Config.PROPERTY_SYSTEM_OPENOFFICE_SERVER, Config.SYSTEM_OPENOFFICE_SERVER);
				} else {
					log.warn("and also {} not configured", Config.PROPERTY_SYSTEM_OPENOFFICE_SERVER);
				}
			}
		}

		return instance;
	}

	/**
	 * Start OpenOffice instance
	 */
	public void start() {
		if (officeManager != null) {
			officeManager.start();
		}
	}

	/**
	 * Stop OpenOffice instance
	 */
	public void stop() {
		if (officeManager != null) {
			officeManager.stop();
		}
	}

	/**
	 * Obtain OpenOffice Manager
	 */
	public OfficeManager getOfficeManager() {
		return officeManager;
	}

	/**
	 * Test if a MIME document can be converted to PDF
	 */
	public boolean convertibleToPdf(String from) {
		log.trace("convertibleToPdf({})", from);
		boolean ret = false;

		if (!Config.REMOTE_CONVERSION_SERVER.equals("")
				&& (validOpenOffice.contains(from) || validImageMagick.contains(from) || validGhoscript.contains(from))) {
			ret = true;
		} else if ((!Config.SYSTEM_OPENOFFICE_PATH.equals("") || !Config.SYSTEM_OPENOFFICE_SERVER.equals(""))
				&& validOpenOffice.contains(from)) {
			ret = true;
		} else if (!Config.SYSTEM_IMAGEMAGICK_CONVERT.equals("") && validImageMagick.contains(from)) {
			ret = true;
		} else if (!Config.SYSTEM_GHOSTSCRIPT.equals("") && validGhoscript.contains(from)) {
			ret = true;
		} else if (validInternal.contains(from)) {
			ret = true;
		}

		log.trace("convertibleToPdf: {}", ret);
		return ret;
	}

	/**
	 * Test if a MIME document can be converted to SWF
	 */
	public boolean convertibleToSwf(String from) {
		log.trace("convertibleToSwf({})", from);
		boolean ret = false;

		if (!Config.REMOTE_CONVERSION_SERVER.equals("")
				&& (MimeTypeConfig.MIME_PDF.equals(from) || validOpenOffice.contains(from) || validImageMagick.contains(from)
				|| validGhoscript.contains(from))) {
			ret = true;
		} else if (!Config.SYSTEM_SWFTOOLS_PDF2SWF.equals("") && (MimeTypeConfig.MIME_PDF.equals(from) || convertibleToPdf(from))) {
			ret = true;
		}

		log.trace("convertibleToSwf: {}", ret);
		return ret;
	}

	/**
	 * Convert a document format to another one.
	 */
	public void convert(File inputFile, String mimeType, File outputFile) throws ConversionException {
		log.debug("convert({}, {}, {})", new Object[]{inputFile, mimeType, outputFile});

		if (Config.SYSTEM_OPENOFFICE_PATH.equals("") && Config.SYSTEM_OPENOFFICE_SERVER.equals("")) {
			throw new ConversionException(Config.PROPERTY_SYSTEM_OPENOFFICE_PATH + " or " + Config.PROPERTY_SYSTEM_OPENOFFICE_SERVER
					+ " not configured");
		}

		if (!validOpenOffice.contains(mimeType)) {
			throw new ConversionException("Invalid document conversion MIME type: " + mimeType);
		}

		try {
			if (!Config.SYSTEM_OPENOFFICE_PATH.equals("")) {
				// Document conversion managed by local OO instance
				OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
				converter.convert(inputFile, outputFile);
			} else if (!Config.SYSTEM_OPENOFFICE_SERVER.equals("")) {
				// Document conversion managed by remote conversion server
				remoteConvert(Config.SYSTEM_OPENOFFICE_SERVER, inputFile, mimeType, outputFile, MimeTypeConfig.MIME_PDF);
			}
		} catch (OfficeException e) {
			throw new ConversionException("Error converting document: " + e.getMessage());
		}
	}

	/**
	 * Handle remote OpenOffice server conversion
	 */
	public void remoteConvert(String uri, File inputFile, String srcMimeType, File outputFile, String dstMimeType)
			throws ConversionException {
		PostMethod post = new PostMethod(uri);

		try {
			Part[] parts = {new FilePart(inputFile.getName(), inputFile), new StringPart("src_mime", srcMimeType),
					new StringPart("dst_mime", dstMimeType), new StringPart("okm_uuid", Repository.getUuid())};
			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
			HttpClient httpclient = new HttpClient();
			int rc = httpclient.executeMethod(post);
			log.info("Response Code: {}", rc);

			if (rc == HttpStatus.SC_OK) {
				FileOutputStream fos = new FileOutputStream(outputFile);
				BufferedInputStream bis = new BufferedInputStream(post.getResponseBodyAsStream());
				IOUtils.copy(bis, fos);
				bis.close();
				fos.close();
			} else {
				throw new IOException("Error in conversion: " + rc);
			}
		} catch (HttpException e) {
			throw new ConversionException("HTTP exception", e);
		} catch (FileNotFoundException e) {
			throw new ConversionException("File not found exeption", e);
		} catch (IOException e) {
			throw new ConversionException("IO exception", e);
		} finally {
			post.releaseConnection();
		}
	}

	/**
	 * Convert document to PDF.
	 */
	public void doc2pdf(File input, String mimeType, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from {} to PDF **", mimeType);
		FileOutputStream fos = null;

		try {
			long start = System.currentTimeMillis();
			convert(input, mimeType, output);
			log.debug("Elapse doc2pdf time: {}", FormatUtil.formatSeconds(System.currentTimeMillis() - start));
		} catch (Exception e) {
			throw new ConversionException("Error in " + mimeType + " to PDF conversion", e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert a document from repository and put the result in the repository.
	 *
	 * @param token Authentication info.
	 * @param docId The path that identifies an unique document or its UUID.
	 * @param dstPath The path of the resulting PDF document (with the name).
	 */
	public static void doc2pdf(String token, String docId, String dstPath) throws RepositoryException, PathNotFoundException,
			DatabaseException, AccessDeniedException, IOException, ConversionException, UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, ItemExistsException, ExtensionException,
			AutomationException, DocumentException, EvalError, LockException, VersionException {
		File docIn = null;
		File docOut = null;
		InputStream docIs = null;
		OutputStream docOs = null;

		try {
			// Get document content
			com.openkm.bean.Document doc = OKMDocument.getInstance().getProperties(token, docId);
			docIs = OKMDocument.getInstance().getContent(token, docId, false);
			String mimeType = doc.getMimeType();

			// Store in filesystem
			docIn = FileUtils.createTempFileFromMime(mimeType);
			docOut = FileUtils.createTempFileFromMime(MimeTypeConfig.MIME_PDF);
			docOs = new FileOutputStream(docIn);
			IOUtils.copy(docIs, docOs);
			IOUtils.closeQuietly(docIs);
			IOUtils.closeQuietly(docOs);

			// Convert to PDF
			DocConverter.getInstance().doc2pdf(docIn, mimeType, docOut);

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
			FileUtils.deleteQuietly(docIn);
			FileUtils.deleteQuietly(docOut);
		}
	}

	/**
	 * Convert document to TXT.
	 */
	public void doc2txt(InputStream input, String mimeType, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from {} to TXT **", mimeType);
		File tmp = FileUtils.createTempFileFromMime(mimeType);
		FileOutputStream fos = new FileOutputStream(tmp);

		try {
			long start = System.currentTimeMillis();

			if (MimeTypeConfig.MIME_PDF.equals(mimeType)) {
				String text = new PdfTextExtractor().extractText(input, mimeType, "utf-8");
				fos.close();
				fos = new FileOutputStream(output);
				IOUtils.write(text, fos);
			} else if (validOpenOffice.contains(mimeType)) {
				IOUtils.copy(input, fos);
				fos.flush();
				fos.close();
				convert(tmp, mimeType, output);
			}

			log.debug("Elapse doc2txt time: {}", FormatUtil.formatSeconds(System.currentTimeMillis() - start));
		} catch (Exception e) {
			throw new ConversionException("Error in " + mimeType + " to TXT conversion", e);
		} finally {
			FileUtils.deleteQuietly(tmp);
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert RTF to HTML.
	 */
	public void rtf2html(InputStream input, OutputStream output) throws ConversionException {
		File docIn = null;
		File docOut = null;
		InputStream docIs = null;
		OutputStream docOs = null;

		try {
			docIn = FileUtils.createTempFileFromMime(MimeTypeConfig.MIME_RTF);
			docOut = FileUtils.createTempFileFromMime(MimeTypeConfig.MIME_HTML);
			docOs = new FileOutputStream(docIn);
			IOUtils.copy(input, docOs);
			IOUtils.closeQuietly(docOs);

			// Conversion
			rtf2html(docIn, docOut);

			docIs = new FileInputStream(docOut);
			IOUtils.copy(docIs, output);
		} catch (DatabaseException e) {
			throw new ConversionException("Database exception", e);
		} catch (IOException e) {
			throw new ConversionException("IO exception", e);
		} finally {
			IOUtils.closeQuietly(docIs);
			IOUtils.closeQuietly(docOs);
			FileUtils.deleteQuietly(docIn);
			FileUtils.deleteQuietly(docOut);
		}
	}

	/**
	 * Convert RTF to HTML.
	 */
	public void rtf2html(File input, File output) throws ConversionException {
		log.debug("** Convert from RTF to HTML **");
		FileOutputStream fos = null;

		try {
			long start = System.currentTimeMillis();
			convert(input, MimeTypeConfig.MIME_RTF, output);
			log.debug("Elapse rtf2html time: {}", FormatUtil.formatSeconds(System.currentTimeMillis() - start));
		} catch (Exception e) {
			throw new ConversionException("Error in RTF to HTML conversion", e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert PS to PDF (for document preview feature).
	 */
	public void ps2pdf(File input, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from PS to PDF **");
		FileOutputStream fos = null;
		String cmd = null;

		if (!input.getName().toLowerCase().endsWith(".ps")) {
			log.warn("ps2pdf conversion needs *.ps as input file");
		}

		try {
			// Performs conversion
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", input.getPath());
			hm.put("fileOut", output.getPath());
			String tpl = Config.SYSTEM_GHOSTSCRIPT + " ${fileIn} ${fileOut}";
			cmd = TemplateUtils.replace("SYSTEM_GHOSTSCRIPT_PS2PDF", tpl, hm);
			ExecutionResult er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new ConversionException(er.getStderr());
			}
		} catch (SecurityException e) {
			throw new ConversionException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new ConversionException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new ConversionException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new ConversionException("Template exception", e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert IMG to PDF (for document preview feature).
	 *
	 * [0] => http://www.rubblewebs.co.uk/imagemagick/psd.php
	 */
	public void img2pdf(File input, String mimeType, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from {} to PDF **", mimeType);
		FileOutputStream fos = null;
		String cmd = null;

		try {
			// Performs conversion
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", input.getPath());
			hm.put("fileOut", output.getPath());

			if (MimeTypeConfig.MIME_PSD.equals(mimeType)) {
				String tpl = Config.SYSTEM_IMAGEMAGICK_CONVERT + " ${fileIn}[0] ${fileOut}";
				cmd = TemplateUtils.replace("SYSTEM_IMAGEMAGICK_CONVERT", tpl, hm);
			} else {
				String tpl = Config.SYSTEM_IMAGEMAGICK_CONVERT + " ${fileIn} ${fileOut}";
				cmd = TemplateUtils.replace("SYSTEM_IMAGEMAGICK_CONVERT", tpl, hm);
			}

			ExecutionResult er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new ConversionException(er.getStderr());
			}
		} catch (SecurityException e) {
			throw new ConversionException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new ConversionException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new ConversionException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new ConversionException("Template exception", e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert CAD files to PDF
	 */
	public void cad2pdf(File input, String mimeType, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from {} to PDF **", mimeType);
		String cmd = null;

		try {
			// Performs conversion
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", input.getPath());
			hm.put("fileOut", output.getPath());
			String tpl = Config.SYSTEM_DWG2DXF + " /r /ad /lw 1 /f 105 /d ${fileOut} ${fileIn}";
			cmd = TemplateUtils.replace("SYSTEM_DWG2DXF", tpl, hm);
			ExecutionResult er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new ConversionException(er.getStderr());
			}
		} catch (SecurityException e) {
			throw new ConversionException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new ConversionException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new ConversionException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new ConversionException("Template exception", e);
		}
	}

	/**
	 * Convert HTML to PDF
	 */
	public void html2pdf(File input, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from HTML to PDF **");
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(output);

			// Make conversion
			Document doc = new Document(PageSize.A4);
			PdfWriter.getInstance(doc, fos);
			doc.open();
			HTMLWorker html = new HTMLWorker(doc);
			html.parse(new FileReader(input));
			doc.close();
		} catch (DocumentException e) {
			throw new ConversionException("Exception in conversion: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert TXT to PDF
	 */
	public void txt2pdf(InputStream is, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from TXT to PDF **");
		FileOutputStream fos = null;
		String line = null;

		try {
			fos = new FileOutputStream(output);

			// Make conversion
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Document doc = new Document(PageSize.A4);
			PdfWriter.getInstance(doc, fos);
			doc.open();

			while ((line = br.readLine()) != null) {
				doc.add(new Paragraph(12F, line));
			}

			doc.close();
		} catch (DocumentException e) {
			throw new ConversionException("Exception in conversion: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert ZIP to PDF
	 */
	@SuppressWarnings("rawtypes")
	public void zip2pdf(File input, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from ZIP to PDF **");
		FileOutputStream fos = null;
		ZipFile zipFile = null;

		try {
			fos = new FileOutputStream(output);

			// Make conversion
			zipFile = new ZipFile(input);
			Document doc = new Document(PageSize.A4);
			PdfWriter.getInstance(doc, fos);
			doc.open();

			for (Enumeration e = zipFile.entries(); e.hasMoreElements(); ) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				doc.add(new Paragraph(12F, entry.getName()));
			}

			doc.close();
			zipFile.close();
		} catch (ZipException e) {
			throw new ConversionException("Exception in conversion: " + e.getMessage(), e);
		} catch (DocumentException e) {
			throw new ConversionException("Exception in conversion: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert SRC to PDF
	 */
	public void src2pdf(File input, File output, String lang) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from SRC to PDF **");
		FileOutputStream fos = null;
		FileInputStream fis = null;

		try {
			fos = new FileOutputStream(output);
			fis = new FileInputStream(input);

			// Make syntax highlight
			String source = IOUtils.toString(fis);
			JaSHi jashi = new JaSHi(source, lang);
			// jashi.EnableLineNumbers(1);
			String parsed = jashi.ParseCode();

			// Make conversion to PDF
			Document doc = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(doc, fos);
			doc.open();
			HTMLWorker html = new HTMLWorker(doc);
			html.parse(new StringReader(parsed));
			doc.close();
		} catch (DocumentException e) {
			throw new ConversionException("Exception in conversion: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Convert PDF to SWF (for document preview feature).
	 */
	public void pdf2swf(File input, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from PDF to SWF **");
		BufferedReader stdout = null;
		String cmd = null;

		try {
			// Performs conversion
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", input.getPath());
			hm.put("fileOut", output.getPath());
			cmd = TemplateUtils.replace("SYSTEM_PDF2SWF", Config.SYSTEM_SWFTOOLS_PDF2SWF, hm);
			ExecutionResult er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new ConversionException(er.getStderr());
			}
		} catch (SecurityException e) {
			throw new ConversionException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new ConversionException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new ConversionException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new ConversionException("Template exception", e);
		} finally {
			IOUtils.closeQuietly(stdout);
		}
	}

	/**
	 * Convert PDF to IMG (for document preview feature).
	 */
	public void pdf2img(File input, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from PDF to IMG **");
		File tmpDir = FileUtils.createTempDir();
		String cmd = null;

		try {
			// Performs step 1: split pdf into several images
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", input.getPath());
			hm.put("fileOut", tmpDir + File.separator + "out.jpg");
			String tpl = Config.SYSTEM_IMAGEMAGICK_CONVERT + " -bordercolor #666 -border 2x2 ${fileIn} ${fileOut}";
			cmd = TemplateUtils.replace("SYSTEM_IMG2PDF", tpl, hm);
			ExecutionResult er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new ConversionException(er.getStderr());
			}

			// Performs step 2: join split images into a big one
			hm = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();
			File files[] = tmpDir.listFiles();
			Arrays.sort(files, new FileOrderComparator());

			for (File f : files) {
				sb.append(f.getPath()).append(" ");
			}

			hm.put("fileIn", sb.toString());
			hm.put("fileOut", output.getPath());
			tpl = Config.SYSTEM_IMAGEMAGICK_CONVERT + " ${fileIn}-append ${fileOut}";
			cmd = TemplateUtils.replace("SYSTEM_IMG2PDF", tpl, hm);
			er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new ConversionException(er.getStderr());
			}
		} catch (SecurityException e) {
			throw new ConversionException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new ConversionException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new ConversionException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new ConversionException("Template exception", e);
		} finally {
			org.apache.commons.io.FileUtils.deleteQuietly(tmpDir);
		}
	}

	/**
	 * User by pdf2img
	 */
	private class FileOrderComparator implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {
			// Filenames are out-1.jpg, out-2.jpg, ..., out-10.jpg, ...
			int o1Ord = Integer.parseInt((o1.getName().split("\\.")[0]).split("-")[1]);
			int o2Ord = Integer.parseInt((o2.getName().split("\\.")[0]).split("-")[1]);

			if (o1Ord > o2Ord)
				return 1;
			else if (o1Ord < o2Ord)
				return -1;
			else
				return 0;
		}
	}

	/**
	 * TIFF to PDF conversion
	 */
	public void tiff2pdf(File input, File output) throws ConversionException {
		RandomAccessFileOrArray ra = null;
		Document doc = null;

		try {
			// Open PDF
			doc = new Document();
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(output));
			PdfContentByte cb = writer.getDirectContent();
			doc.open();
			// int pages = 0;

			// Open TIFF
			ra = new RandomAccessFileOrArray(input.getPath());
			int comps = TiffImage.getNumberOfPages(ra);

			for (int c = 0; c < comps; ++c) {
				Image img = TiffImage.getTiffImage(ra, c + 1);

				if (img != null) {
					log.debug("tiff2pdf - page {}", c + 1);

					if (img.getScaledWidth() > 500 || img.getScaledHeight() > 700) {
						img.scaleToFit(500, 700);
					}

					img.setAbsolutePosition(20, 20);
					// doc.add(new Paragraph("page " + (c + 1)));
					cb.addImage(img);
					doc.newPage();
					// ++pages;
				}
			}
		} catch (FileNotFoundException e) {
			throw new ConversionException("File not found: " + e.getMessage(), e);
		} catch (DocumentException e) {
			throw new ConversionException("Document exception: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new ConversionException("IO exception: " + e.getMessage(), e);
		} finally {
			if (ra != null) {
				try {
					ra.close();
				} catch (IOException e) {
					// Ignore
				}
			}

			if (doc != null) {
				doc.close();
			}
		}
	}

	/**
	 * Convert DWG to DXF (for document preview feature).
	 * Actually only tested with Acme CAD Converter 2011 v8.2.2
	 */
	public void dwg2dxf(File input, File output) throws ConversionException, DatabaseException, IOException {
		log.debug("** Convert from DWG to DXF **");
		BufferedReader stdout = null;
		String cmd = null;

		try {
			// Performs conversion
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", input.getPath());
			hm.put("fileOut", output.getPath());
			String tpl = Config.SYSTEM_DWG2DXF + " /r /ad /x14 ${fileIn} ${fileOut}";
			cmd = TemplateUtils.replace("SYSTEM_DWG2DXF", tpl, hm);
			ExecutionResult er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new ConversionException(er.getStderr());
			}
		} catch (SecurityException e) {
			throw new ConversionException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new ConversionException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new ConversionException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new ConversionException("Template exception", e);
		} finally {
			IOUtils.closeQuietly(stdout);
		}
	}

	/**
	 * Rotate an image.
	 *
	 * @param imgIn Image to rotate.
	 * @param imgOut Image rotated.
	 * @param angle Rotation angle.
	 * @throws IOException
	 */
	public void rotateImage(File imgIn, File imgOut, double angle) throws ConversionException {
		String cmd = null;

		try {
			// Performs conversion
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", imgIn.getPath());
			hm.put("fileOut", imgOut.getPath());
			String tpl = Config.SYSTEM_IMAGEMAGICK_CONVERT + " -rotate " + angle + " ${fileIn} ${fileOut}";
			cmd = TemplateUtils.replace("SYSTEM_IMG2PDF", tpl, hm);
			ExecutionUtils.runCmd(cmd);
		} catch (SecurityException e) {
			throw new ConversionException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new ConversionException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new ConversionException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new ConversionException("Template exception", e);
		}
	}
}
