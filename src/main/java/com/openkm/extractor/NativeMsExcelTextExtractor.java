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

import com.openkm.bean.ExecutionResult;
import com.openkm.core.Config;
import com.openkm.util.ExecutionUtils;
import com.openkm.util.FileUtils;
import com.openkm.util.TemplateUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Text extractor for MS Excel.
 * Use xls2csv from http://www.wagner.pp.ru/~vitus/software/catdoc/
 */
public class NativeMsExcelTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(NativeMsExcelTextExtractor.class);

	/**
	 * Creates a new <code>TextExtractor</code> instance.
	 */
	public NativeMsExcelTextExtractor() {
		super(new String[]{"application/vnd.ms-excel", "application/msexcel", "application/excel"});
	}

	/**
	 * Use in TextExtractor subclass
	 */
	public NativeMsExcelTextExtractor(String[] contentTypes) {
		super(contentTypes);
	}

	//-------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		File tmpFileIn = null;
		String cmd = null;

		if (!Config.SYSTEM_CATDOC_XLS2CSV.equals("")) {
			try {
				// Create temp file
				tmpFileIn = FileUtils.createTempFileFromMime(type);
				FileOutputStream fos = new FileOutputStream(tmpFileIn);
				IOUtils.copy(stream, fos);
				fos.close();

				// Perform text extraction
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("fileIn", tmpFileIn.getPath());
				cmd = TemplateUtils.replace("SYSTEM_XLS2CSV", Config.SYSTEM_CATDOC_XLS2CSV, hm);
				ExecutionResult execRes = ExecutionUtils.runCmd(cmd);

				// Read result
				String text = execRes.getStdout();

				return text;
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
				IOUtils.closeQuietly(stream);
				FileUtils.deleteQuietly(tmpFileIn);
			}
		} else {
			log.warn("Undefined xls2csv application");
			return "";
		}
	}
}
