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

package com.openkm.frontend.client.util.metadata;

import com.openkm.frontend.client.util.ISO8601;

import java.util.Date;

/**
 * DatabaseMetadataMap
 *
 * @author jllort
 */
public class DatabaseMetadataMap {
	// Metadata virtual column name mapping
	public static final String MV_NAME_TABLE = "table";
	public static final String MV_NAME_ID = "id";

	// Boolean values mapping
	public static final String BOOLEAN_TRUE = "T";
	public static final String BOOLEAN_FALSE = "F";

	/**
	 * getDoubleValue
	 */
	public static Double getDoubleValue(String value) {
		return (value != null) ? new Double(value) : null;
	}

	/**
	 * mapDoubleValue
	 */
	public static String mapDoubleValue(Double value) {
		return (value != null) ? String.valueOf(value) : null;
	}

	/**
	 * getIntValue
	 */
	public static Integer getIntegerValue(String value) {
		return (value != null) ? new Integer(value) : null;
	}

	/**
	 * mapIntValue
	 */
	public static String mapIntegerValue(Integer value) {
		return (value != null) ? String.valueOf(value) : null;
	}

	/**
	 * getBooleanValue
	 */
	public static Boolean getBooleanValue(String value) {
		if (value == null) {
			return null;
		} else {
			if (value.toUpperCase().equals(BOOLEAN_TRUE)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * mapBooleanValue
	 */
	public static String mapBooleanValue(Boolean value) {
		if (value == null) {
			return null;
		} else {
			if (value) {
				return BOOLEAN_TRUE;
			} else {
				return BOOLEAN_FALSE;
			}
		}
	}

	/**
	 * getDateValue
	 */
	public static Date getDateValue(String value) {
		return ISO8601.parseBasic(value);
	}

	/**
	 * mapDateValue
	 */
	public static String mapDateValue(Date value) {
		return ISO8601.formatBasic(value);
	}
}