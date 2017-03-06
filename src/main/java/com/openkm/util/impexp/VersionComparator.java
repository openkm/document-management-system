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

package com.openkm.util.impexp;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {
	private static final Comparator<String> INSTANCE = new VersionComparator();

	public static Comparator<String> getInstance() {
		return INSTANCE;
	}

	public int compare(String ver0, String ver1) {
		ver0 = ver0.startsWith("v") ? ver0.substring(1) : ver0;
		ver1 = ver1.startsWith("v") ? ver1.substring(1) : ver1;

		return compare(ver0.split("\\."), ver1.split("\\."), 0);
	}

	private int compare(String[] ary1, String[] ary2, int index) {
		// if arrays do not have equal size then and comparison reached the upper bound of one of them
		// then the longer array is considered the bigger ( --> 2.2.0 is bigger then 2.2)
		if (ary1.length <= index || ary2.length <= index) return ary1.length - ary2.length;
		int result = Integer.parseInt(ary1[index]) - Integer.parseInt(ary2[index]);
		return result == 0 ? compare(ary1, ary2, ++index) : result;
	}
}
