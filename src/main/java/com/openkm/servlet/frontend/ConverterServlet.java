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

package com.openkm.servlet.frontend;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.automation.AutomationManager;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.Document;
import com.openkm.core.*;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.module.db.DbDocumentModule;
import com.openkm.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Document converter service
 */
public class ConverterServlet extends OKMHttpServlet {
	private static Logger log = LoggerFactory.getLogger(ConverterServlet.class);
	private static final long serialVersionUID = 1L;
	public static final String FILE_CONVERTER_STATUS = "file_converter_status";

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("service({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String uuid = WebUtils.getString(request, "uuid");
		boolean inline = WebUtils.getBoolean(request, "inline");
		boolean print = WebUtils.getBoolean(request, "print");
		boolean toPdf = WebUtils.getBoolean(request, "toPdf");
		boolean toSwf = WebUtils.getBoolean(request, "toSwf");
		CharsetDetector detector = new CharsetDetector();
		File tmp = null;
		File tmpDir = null;
		InputStream is = null;
		ConverterListener listener = new ConverterListener(ConverterListener.STATUS_LOADING);
		updateSessionManager(request);

		try {
			// Now an document can be located by UUID
			if (!uuid.equals("")) {
				// Saving listener to session
				request.getSession().setAttribute(FILE_CONVERTER_STATUS, listener);

				String path = OKMRepository.getInstance().getNodePath(null, uuid);
				Document doc = OKMDocument.getInstance().getProperties(null, path);
				String fileName = PathUtils.getName(doc.getPath());

				// Optinal append version to download
				if (Config.VERSION_APPEND_DOWNLOAD) {
					String versionToAppend = OKMDocument.getInstance().getProperties(null, uuid).getActualVersion().getName();
					String[] nameParts = fileName.split("\\.(?=[^\\.]+$)");
					fileName = nameParts[0] + (Config.VERSION_APPEND_DOWNLOAD ? (" rev " + versionToAppend) : "") + "." + nameParts[1];
				}

				// Save content to temporary file
				tmp = File.createTempFile("okm", "." + FileUtils.getFileExtension(fileName));
				if (Config.REPOSITORY_NATIVE) {
					// If is used to preview, it should workaround the DOWNLOAD extended permission.
					is = new DbDocumentModule().getContent(null, path, false, !toSwf);
				}

				// Text files may need encoding conversion
				if (doc.getMimeType().startsWith("text/")) {
					detector.setText(new BufferedInputStream(is));
					CharsetMatch cm = detector.detect();
					Reader rd = cm.getReader();

					FileUtils.copy(rd, tmp);
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(rd);
				} else {
					FileUtils.copy(is, tmp);
					IOUtils.closeQuietly(is);
				}

				// Prepare conversion
				ConversionData cd = new ConversionData();
				cd.uuid = uuid;
				cd.fileName = fileName;
				cd.mimeType = doc.getMimeType();
				cd.file = tmp;

				if (toPdf && !cd.mimeType.equals(MimeTypeConfig.MIME_PDF)) {
					try {
						listener.setStatus(ConverterListener.STATUS_CONVERTING_TO_PDF);
						toPDF(cd);
						listener.setStatus(ConverterListener.STATUS_CONVERTING_TO_PDF_FINISHED);
					} catch (ConversionException e) {
						log.error(e.getMessage(), e);
						listener.setError(e.getMessage());
						InputStream tis = ConverterServlet.class.getResourceAsStream("conversion_problem.pdf");
						FileUtils.copy(tis, cd.file);
					}
				} else if (toSwf && !cd.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
					try {
						listener.setStatus(ConverterListener.STATUS_CONVERTING_TO_SWF);
						toSWF(cd);
						listener.setStatus(ConverterListener.STATUS_CONVERTING_TO_SWF_FINISHED);
					} catch (ConversionException e) {
						log.error(e.getMessage(), e);
						listener.setError(e.getMessage());
						InputStream tis = ConverterServlet.class.getResourceAsStream("conversion_problem.swf");
						FileUtils.copy(tis, cd.file);
					}
				}

				if (toPdf && print) {
					cd.file = PDFUtils.markToPrint(cd.file);
				}

				// Send back converted document
				listener.setStatus(ConverterListener.STATUS_SENDING_FILE);
				WebUtils.sendFile(request, response, cd.fileName, cd.mimeType, inline, cd.file);
			} else {
				log.error("Missing Conversion Parameters");
				response.setContentType(MimeTypeConfig.MIME_TEXT);
				PrintWriter out = response.getWriter();
				out.print("Missing Conversion Parameters");
				out.flush();
				out.close();
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			listener.setError(e.getMessage());
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_PathNotFound), e.getMessage()));
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			listener.setError(e.getMessage());
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_AccessDenied), e.getMessage()));
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			listener.setError(e.getMessage());
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_Repository), e.getMessage()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			listener.setError(e.getMessage());
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_IO), e.getMessage()));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			listener.setError(e.getMessage());
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_Database), e.getMessage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			listener.setError(e.getMessage());
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_General), e.getMessage()));
		} finally {
			listener.setConversionFinish(true);
			org.apache.commons.io.FileUtils.deleteQuietly(tmp);
			org.apache.commons.io.FileUtils.deleteQuietly(tmpDir);
		}

		log.debug("service: void");
	}

	/**
	 * Handles PDF conversion
	 */
	private void toPDF(ConversionData cd)
			throws ConversionException, AutomationException, DatabaseException, IOException {
		log.debug("toPDF({})", cd);
		File pdfCache = new File(Config.REPOSITORY_CACHE_PDF + File.separator + cd.uuid + ".pdf");

		if (DocConverter.getInstance().convertibleToPdf(cd.mimeType)) {
			if (!pdfCache.exists()) {
				try {
					if (cd.mimeType.equals(MimeTypeConfig.MIME_PDF)) {
						// Document already in PDF format
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_ZIP)) {
						// This is an internal conversion and does not need 3er
						// party software
						DocConverter.getInstance().zip2pdf(cd.file, pdfCache);
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_XML)) {
						// This is an internal conversion and does not need 3er
						// party software
						DocConverter.getInstance().src2pdf(cd.file, pdfCache, "xml");
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_SQL)) {
						// This is an internal conversion and does not need 3er
						// party software
						DocConverter.getInstance().src2pdf(cd.file, pdfCache, "sql");
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_JAVA)
							|| cd.mimeType.equals(MimeTypeConfig.MIME_BSH)) {
						// This is an internal conversion and does not need 3er
						// party software
						DocConverter.getInstance().src2pdf(cd.file, pdfCache, "java");
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_PHP)) {
						// This is an internal conversion and does not need 3er
						// party software
						DocConverter.getInstance().src2pdf(cd.file, pdfCache, "php");
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_SH)) {
						// This is an internal conversion and does not need 3er
						// party software
						DocConverter.getInstance().src2pdf(cd.file, pdfCache, "bash");
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_HTML)) {
						// This is an internal conversion and does not need 3er party software
						DocConverter.getInstance().html2pdf(cd.file, pdfCache);
						// } else if (cd.mimeType.equals(MimeTypeConfig.MIME_TIFF)) {
						// This is an internal conversion and does not need 3er party software
						// DocConverter.getInstance().tiff2pdf(cd.file, pdfCache);
					} else if (!Config.REMOTE_CONVERSION_SERVER.equals("")) {
						DocConverter.getInstance().remoteConvert(Config.REMOTE_CONVERSION_SERVER, cd.file, cd.mimeType,
								pdfCache, MimeTypeConfig.MIME_PDF);
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_POSTSCRIPT)) {
						DocConverter.getInstance().ps2pdf(cd.file, pdfCache);
					} else if (DocConverter.validImageMagick.contains(cd.mimeType)) {
						DocConverter.getInstance().img2pdf(cd.file, cd.mimeType, pdfCache);
					} else if (DocConverter.validOpenOffice.contains(cd.mimeType)) {
						DocConverter.getInstance().doc2pdf(cd.file, cd.mimeType, pdfCache);
					} else {
						throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to PDF not available");
					}

					// AUTOMATION - POST
					Map<String, Object> env = new HashMap<String, Object>();
					env.put(AutomationUtils.DOCUMENT_FILE, pdfCache);
					env.put(AutomationUtils.DOCUMENT_UUID, cd.uuid);
					AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_CONVERSION_PDF, AutomationRule.AT_POST, env);
				} catch (ConversionException e) {
					pdfCache.delete();
					throw e;
				} finally {
					FileUtils.deleteQuietly(cd.file);
					cd.mimeType = MimeTypeConfig.MIME_PDF;
					cd.fileName = FileUtils.getFileName(cd.fileName) + ".pdf";
				}
			}

			if (pdfCache.exists()) cd.file = pdfCache;
			cd.mimeType = MimeTypeConfig.MIME_PDF;
			cd.fileName = FileUtils.getFileName(cd.fileName) + ".pdf";
		} else {
			throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to PDF not available");
		}

		log.debug("toPDF: {}", cd);
	}

	/**
	 * Handles SWF conversion
	 */
	private void toSWF(ConversionData cd) throws ConversionException, AutomationException, DatabaseException, IOException {
		log.debug("toSWF({})", cd);
		File swfCache = new File(Config.REPOSITORY_CACHE_SWF + File.separator + cd.uuid + ".swf");
		boolean delTmp = true;

		if (DocConverter.getInstance().convertibleToSwf(cd.mimeType)) {
			if (!swfCache.exists()) {
				try {
					if (cd.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
						// Document already in SWF format
					} else if (!Config.REMOTE_CONVERSION_SERVER.equals("")) {
						DocConverter.getInstance().remoteConvert(Config.REMOTE_CONVERSION_SERVER, cd.file, cd.mimeType,
								swfCache, MimeTypeConfig.MIME_SWF);
					} else if (cd.mimeType.equals(MimeTypeConfig.MIME_PDF)) {
						// AUTOMATION - PRE
						Map<String, Object> env = new HashMap<String, Object>();
						env.put(AutomationUtils.DOCUMENT_FILE, cd.file);
						env.put(AutomationUtils.DOCUMENT_UUID, cd.uuid);
						AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_CONVERSION_SWF,
								AutomationRule.AT_PRE, env);

						DocConverter.getInstance().pdf2swf(cd.file, swfCache);
					} else if (DocConverter.getInstance().convertibleToPdf(cd.mimeType)) {
						toPDF(cd);
						delTmp = false;

						// AUTOMATION - PRE
						Map<String, Object> env = new HashMap<String, Object>();
						env.put(AutomationUtils.DOCUMENT_FILE, cd.file);
						env.put(AutomationUtils.DOCUMENT_UUID, cd.uuid);
						AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_CONVERSION_SWF, AutomationRule.AT_PRE, env);

						DocConverter.getInstance().pdf2swf(cd.file, swfCache);
					} else {
						throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to SWF not available");
					}
				} catch (ConversionException e) {
					swfCache.delete();
					throw e;
				} finally {
					if (delTmp) {
						FileUtils.deleteQuietly(cd.file);
					}
					cd.mimeType = MimeTypeConfig.MIME_SWF;
					cd.fileName = FileUtils.getFileName(cd.fileName) + ".swf";
				}
			}

			if (swfCache.exists()) cd.file = swfCache;
			cd.mimeType = MimeTypeConfig.MIME_SWF;
			cd.fileName = FileUtils.getFileName(cd.fileName) + ".swf";
		} else {
			throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to SWF not available");
		}

		log.debug("toSWF: {}", cd);
	}

	/**
	 * For internal use only.
	 */
	private class ConversionData {
		private String uuid;
		private String fileName;
		private String mimeType;
		private File file;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("uuid=").append(uuid);
			sb.append(", fileName=").append(fileName);
			sb.append(", mimeType=").append(mimeType);
			sb.append(", file=").append(file);
			sb.append("}");
			return sb.toString();
		}
	}
}
