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

import com.openkm.core.Config;
import com.openkm.util.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

/**
 * Text extractor for image documents.
 * Use OCR from https://code.launchpad.net/cuneiform-linux
 */
public class CuneiformTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(CuneiformTextExtractor.class);

	/**
	 * Creates a new <code>TextExtractor</code> instance.
	 */
	public CuneiformTextExtractor() {
		super(new String[]{"image/tiff", "image/gif", "image/jpg", "image/jpeg", "image/png"});
	}

	/**
	 * Use in AbbyTextExtractor subclass
	 */
	public CuneiformTextExtractor(String[] contentTypes) {
		super(contentTypes);
	}

	//-------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		File tmpFileIn = null;

		if (!Config.SYSTEM_OCR.equals("")) {
			try {
				// Create temp file
				tmpFileIn = FileUtils.createTempFileFromMime(type);
				FileOutputStream fos = new FileOutputStream(tmpFileIn);
				IOUtils.copy(stream, fos);
				fos.close();

				// Perform OCR
				StringBuilder sb = new StringBuilder();
				String text = doOcr(tmpFileIn);
				sb.append(text);

				if (!Config.SYSTEM_OCR_ROTATE.equals("")) {
					String[] angles = Config.SYSTEM_OCR_ROTATE.split(Config.LIST_SEPARATOR);

					for (String angle : angles) {
						// Rotate
						log.debug("Rotate image {} degrees", angle);
						double degree = Double.parseDouble(angle);
						DocConverter.getInstance().rotateImage(tmpFileIn, tmpFileIn, degree);
						text = doOcr(tmpFileIn);
						sb.append("\n-----------------------------\n");
						sb.append(text);
					}
				}

				return sb.toString();
			} catch (Exception e) {
				log.warn("Failed to extract OCR text", e);
				return "";
			} finally {
				IOUtils.closeQuietly(stream);
				FileUtils.deleteQuietly(tmpFileIn);
			}
		} else {
			log.warn("Undefined OCR application");
			return "";
		}
	}

	/**
	 * Performs OCR on image file
	 */
	public String doOcr(File tmpFileIn) throws Exception {
		BufferedReader stdout = null;
		FileInputStream fis = null;
		File tmpFileOut = null;
		String cmd = null;

		if (!Config.SYSTEM_OCR.equals("")) {
			try {
				// Create temp file
				tmpFileOut = File.createTempFile("okm", ".txt");

				// Performs OCR
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("fileIn", tmpFileIn.getPath());
				hm.put("fileOut", tmpFileOut.getPath());
				cmd = TemplateUtils.replace("SYSTEM_OCR", Config.SYSTEM_OCR, hm);
				ExecutionUtils.runCmd(cmd);

				// Read result
				fis = new FileInputStream(tmpFileOut);
				String text = IOUtils.toString(fis);

				// Spellchecker
				if (Config.SYSTEM_OPENOFFICE_DICTIONARY.equals("")) {
					log.debug("TEXT: {}", text);
					return text;
				} else {
					text = DocumentUtils.spellChecker(text);
					log.debug("TEXT: {}", text);
					return text;
				}
			} catch (SecurityException e) {
				log.warn("Security exception executing command: " + cmd, e);
				return "";
			} catch (IOException e) {
				log.warn("IO exception executing command: " + cmd, e);
				return "";
			} catch (InterruptedException e) {
				log.warn("Interrupted exception executing command: " + cmd, e);
				return "";
			} catch (Exception e) {
				log.warn("Failed to extract OCR text", e);
				return "";
			} finally {
				IOUtils.closeQuietly(fis);
				IOUtils.closeQuietly(stdout);
				FileUtils.deleteQuietly(tmpFileOut);
			}
		} else {
			log.warn("Undefined OCR application");
			return "";
		}
	}
}
