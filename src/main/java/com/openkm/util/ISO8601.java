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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * The <code>ISO8601</code> utility class provides helper methods to deal with
 * date/time formatting using a specific ISO8601-compliant format (see <a
 * href="http://www.w3.org/TR/NOTE-datetime">ISO 8601</a>).
 * <p/>
 * The currently supported format is:
 *
 * <pre>
 *   YYYY-MM-DDThh:mm:ss.SSSTZD
 * </pre>
 *
 * where:
 *
 * <pre>
 *   MM    = two-digit month (01=January, etc.)
 *   DD    = two-digit day of month (01 through 31)
 *   hh    = two digits of hour (00 through 23) (am/pm NOT allowed)
 *   mm    = two digits of minute (00 through 59)
 *   ss    = two digits of second (00 through 59)
 *   SSS   = three digits of milliseconds (000 through 999)
 *   TZD   = time zone designator, Z for Zulu (i.e. UTC) or an offset from UTC
 *           in the form of +hh:mm or -hh:mm
 * </pre>
 *
 * See also http://www.pelagodesign.com/blog/2009/05/20/iso-8601-date-validation-that-doesnt-suck/
 */
public class ISO8601 {
	public static final String BASIC_PATTERN = "yyyyMMddHHmmss";
	static final Pattern EXTENDED_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T((\\d{2}):"
			+ "(\\d{2}):(\\d{2})\\.(\\d{3}))((\\+|-)(\\d{2}):(\\d{2}))");

	/**
	 * Check if the value is a valid ISO8601 extended date
	 */
	public static boolean isExtended(String value) {
		return EXTENDED_PATTERN.matcher(value).matches();
	}

	/**
	 * Parse string date in format "YYYY-MM-DDThh:mm:ss.SSSTZD"
	 */
	public static Calendar parseExtended(String value) {
		if (value == null) {
			return null;
		} else {
			try {
				DatatypeFactory dtf = DatatypeFactory.newInstance();
				XMLGregorianCalendar xml = dtf.newXMLGregorianCalendar(value);
				return xml.toGregorianCalendar();
			} catch (DatatypeConfigurationException e) {
				throw new IllegalArgumentException(value);
			}
		}
	}

	/**
	 * Format date with format "YYYY-MM-DDThh:mm:ss.SSSTZD"
	 */
	public static String formatExtended(Calendar value) {
		if (value == null) {
			return null;
		} else {
			try {
				DatatypeFactory dtf = DatatypeFactory.newInstance();
				XMLGregorianCalendar xml = dtf.newXMLGregorianCalendar((GregorianCalendar) value);
				return xml.toString();
			} catch (DatatypeConfigurationException e) {
				throw new IllegalArgumentException();
			}
		}
	}

	/**
	 * Parse string date in format "yyyyMMddHHmmss"
	 */
	public static Calendar parseBasic(String value) {
		if (value == null) {
			return null;
		} else {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(BASIC_PATTERN);
				Calendar cal = Calendar.getInstance();
				cal.setTime(sdf.parse(value));
				return cal;
			} catch (ParseException e) {
				throw new IllegalArgumentException(value);
			}
		}
	}

	/**
	 * Format date with format "yyyyMMddHHmmss"
	 */
	public static String formatBasic(Calendar value) {
		if (value == null) {
			return null;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(BASIC_PATTERN);
			return sdf.format(value.getTime());
		}
	}
}
