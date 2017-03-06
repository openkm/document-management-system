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

import java.util.Comparator;

/**
 * StringIgnoreCaseComparator
 *
 * @author jllort
 *
 */
public class StringIgnoreCaseComparator implements Comparator<String> {

	public int compare(String arg0, String arg1) {
		// Only compares initial terms
		int length = 0;
		if (arg0.length() > arg1.length()) {
			length = arg1.length();
		} else {
			length = arg0.length();
		}
		return arg0.substring(0, length).toLowerCase().compareTo(arg1.substring(0, length).toLowerCase());
	}
}