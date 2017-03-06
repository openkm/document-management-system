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

package com.openkm.extractor;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.util.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

/**
 * Text extractor for image documents.
 * Use OCR from http://code.google.com/p/tesseract-ocr/
 */
public class Tesseract3TextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(Tesseract3TextExtractor.class);

	/**
	 * Creates a new <code>TextExtractor</code> instance.
	 */
	public Tesseract3TextExtractor() {
		super(new String[]{"image/tiff", "image/gif", "image/jpg", "image/jpeg", "image/png"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		return extractText(Config.SYSTEM_OCR, stream, type, encoding);
	}

	/**
	 * Extract text from image using Tesseract OCR
	 */
	public String extractText(File input) throws IOException {
		return extractText(Config.SYSTEM_OCR, input);
	}

	/**
	 * {@inheritDoc}
	 */
	public String extractText(String ocr, InputStream stream, String type, String encoding) throws IOException {
		File tmpFileIn = null;

		try {
			// Create temp file
			tmpFileIn = FileUtils.createTempFileFromMime(type);
			FileOutputStream fos = new FileOutputStream(tmpFileIn);
			IOUtils.copy(stream, fos);
			fos.close();

			String text = extractText(ocr, tmpFileIn);
			return text;
		} catch (DatabaseException e) {
			log.warn("Failed to extract barcode text", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
			FileUtils.deleteQuietly(tmpFileIn);
		}
	}

	/**
	 * Extract text from image using Tesseract OCR
	 */
	public String extractText(String ocr, File input) throws IOException {
		if (!ocr.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			String text = doOcr(ocr, input);
			sb.append(text);

			if (!Config.SYSTEM_OCR_ROTATE.isEmpty()) {
				String[] angles = Config.SYSTEM_OCR_ROTATE.split(Config.LIST_SEPARATOR);

				for (String angle : angles) {
					// Rotate
					log.debug("Rotate image {} degrees", angle);
					double degree = Double.parseDouble(angle);
					ImageUtils.rotate(input, input, degree);
					text = doOcr(ocr, input);
					sb.append("\n-----------------------------\n");
					sb.append(text);
				}
			}

			return sb.toString();
		} else {
			log.warn("Undefined OCR application");
			throw new IOException("Undefined OCR application");
		}
	}

	/**
	 * Performs OCR on image file
	 */
	public String doOcr(File tmpFileIn) throws IOException {
		return doOcr(Config.SYSTEM_OCR, tmpFileIn);
	}

	/**
	 * Performs OCR on image file
	 */
	public String doOcr(String ocr, File tmpFileIn) throws IOException {
		BufferedReader stdout = null;
		FileInputStream fis = null;
		File tmpFileOut = null;
		String cmd = null;

		if (!ocr.isEmpty()) {
			try {
				// Create temp file
				tmpFileOut = File.createTempFile("okm", "");

				// Performs OCR
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("fileIn", tmpFileIn.getPath());
				hm.put("fileOut", tmpFileOut.getPath());
				cmd = TemplateUtils.replace("SYSTEM_OCR", ocr, hm);
				ExecutionUtils.runCmd(cmd);

				// Read result
				fis = new FileInputStream(tmpFileOut.getPath() + ".txt");
				String text = IOUtils.toString(fis, "UTF-8");

				// Spellchecker
				if (Config.SYSTEM_OPENOFFICE_DICTIONARY.isEmpty()) {
					log.debug("TEXT: {}", text);
					return text;
				} else {
					text = DocumentUtils.spellChecker(text);
					log.debug("TEXT: {}", text);
					return text;
				}
			} catch (SecurityException e) {
				log.warn("Security exception executing command: " + cmd, e);
				throw new IOException(e.getMessage(), e);
			} catch (IOException e) {
				log.warn("IO exception executing command: " + cmd, e);
				throw new IOException(e.getMessage(), e);
			} catch (InterruptedException e) {
				log.warn("Interrupted exception executing command: " + cmd, e);
				throw new IOException(e.getMessage(), e);
			} catch (Exception e) {
				log.warn("Failed to extract OCR text", e);
				throw new IOException(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(fis);
				IOUtils.closeQuietly(stdout);
				FileUtils.deleteQuietly(tmpFileOut);

				if (tmpFileOut != null) {
					FileUtils.deleteQuietly(new File(tmpFileOut.getPath() + ".txt"));
				}
			}
		} else {
			log.warn("Undefined OCR application");
			throw new IOException("Undefined OCR application");
		}
	}
}
