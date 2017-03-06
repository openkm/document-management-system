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
import com.openkm.util.DocumentUtils;
import com.openkm.util.ExecutionUtils;
import com.openkm.util.FileUtils;
import com.openkm.util.TemplateUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

/**
 * Text extractor for TIFF image documents.
 * Use OCR from http://code.google.com/p/tesseract-ocr/ 
 */
public class Tesseract2TextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(Tesseract2TextExtractor.class);

	/**
	 * Creates a new <code>TextExtractor</code> instance.
	 */
	public Tesseract2TextExtractor() {
		super(new String[]{"image/tiff"});
	}

	//-------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		File tmpFileIn = null;
		File tmpFilePre = null;
		File tmpFileOut = null;
		String cmd = null;

		if (!Config.SYSTEM_OCR.equals("")) {
			try {
				// Create temp file
				tmpFileIn = File.createTempFile("okm", ".tif");
				tmpFilePre = File.createTempFile("okm", ".tif");
				tmpFileOut = File.createTempFile("okm", "");
				FileOutputStream fos = new FileOutputStream(tmpFileIn);
				IOUtils.copy(stream, fos);
				fos.close();

				// Performs image pre-processing
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("fileIn", tmpFileIn.getPath());
				hm.put("fileOut", tmpFilePre.getPath());
				String tpl = Config.SYSTEM_IMAGEMAGICK_CONVERT + " -depth 8 -monochrome ${fileIn} ${fileOut}";
				cmd = TemplateUtils.replace("SYSTEM_IMG2PDF", tpl, hm);
				ExecutionUtils.runCmd(cmd);

				// Performs OCR
				hm = new HashMap<String, Object>();
				hm.put("fileIn", tmpFilePre.getPath());
				hm.put("fileOut", tmpFileOut.getPath());
				cmd = TemplateUtils.replace("SYSTEM_OCR", Config.SYSTEM_OCR, hm);
				ExecutionUtils.runCmd(cmd);

				// Read result
				String text = IOUtils.toString(new FileInputStream(tmpFileOut.getPath() + ".txt"));

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
				stream.close();
				FileUtils.deleteQuietly(tmpFileIn);
				FileUtils.deleteQuietly(tmpFilePre);
				FileUtils.deleteQuietly(tmpFileOut);

				if (tmpFileOut != null) {
					FileUtils.deleteQuietly(new File(tmpFileOut.getPath() + ".txt"));
				}
			}
		} else {
			log.warn("Undefined OCR application");
			return "";
		}
	}
}
