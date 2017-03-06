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

package com.openkm.frontend.client.util;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import java.util.Date;

/**
 * ISO8601
 *
 * @author jllort
 *
 */
public class ISO8601 {
	public static final String BASIC_PATTER = "yyyyMMddHHmmss";

	/**
	 * Parse string date in format "YYYY-MM-DDThh:mm:ss.SSSTZD"
	 */
	public static Date parseExtended(String value) {
		if (value == null) {
			return null;
		} else {
			DateTimeFormat dtf = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);
			return dtf.parse(value);
		}
	}

	/**
	 * Format date with format "YYYY-MM-DDThh:mm:ss.SSSTZD"
	 */
	public static String formatExtended(Date value) {
		if (value == null) {
			return null;
		} else {
			DateTimeFormat dtf = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);
			return dtf.format(value);
		}
	}

	/**
	 * Parse string date in format "yyyyMMddHHmmss"
	 */
	public static Date parseBasic(String value) {
		if (value == null) {
			return null;
		} else {
			DateTimeFormat dtf = DateTimeFormat.getFormat(BASIC_PATTER);
			return dtf.parse(value);
		}
	}

	/**
	 * Format date with format "yyyyMMddHHmmss"
	 */
	public static String formatBasic(Date value) {
		if (value == null) {
			return null;
		} else {
			DateTimeFormat dtf = DateTimeFormat.getFormat(BASIC_PATTER);
			return dtf.format(value);
		}
	}
}