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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Text extractor for image documents.
 * Use OCR from Abby (http://www.ocr4linux.com/en:documentation)
 */
public class AbbyTextExtractor extends CuneiformTextExtractor {

	/**
	 * Logger instance.
	 */
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(AbbyTextExtractor.class);

	/**
	 * Creates a new <code>TextExtractor</code> instance.
	 */
	public AbbyTextExtractor() {
		super(new String[]{"application/pdf", "image/bmp", "image/pcx", "image/jpeg", "image/tiff", "image/png"});
	}
}
