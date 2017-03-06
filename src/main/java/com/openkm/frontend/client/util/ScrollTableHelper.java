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

package com.openkm.frontend.client.util;

import com.google.gwt.gen2.table.client.ScrollTable;

/**
 * ScrollTableHelper
 *
 * @author jllort
 *
 */
public class ScrollTableHelper {
	public static final int LOW = 0;
	public static final int MEDIUM = 1;
	public static final int GREAT = 2;
	public static final int FIXED = 3;

	/**
	 * setColumnWidth
	 */
	public static void setColumnWidth(ScrollTable table, int col, int width, int type) {
		setColumnWidth(table, col, width, type, true, true);
	}

	/**
	 * setColumnWidth
	 */
	public static void setColumnWidth(ScrollTable table, int col, int width, int type, boolean isMin, boolean isMax) {
		table.setColumnWidth(col, width);
		table.setPreferredColumnWidth(col, width);
		int min = 0;
		int max = 0;
		switch (type) {
			case LOW:
				min = width - 15;
				max = width + 25;
				break;
			case MEDIUM:
				min = width - 35;
				max = width + 70;
				break;
			case GREAT:
				min = width - 150;
				max = width + 250;
				break;
		}
		// Correction to min always >=25 except in fixed case
		if (type == FIXED) {
			min = width;
			max = width;
		} else if (min < 25) {
			min = 25;
		}
		// Set min and max values
		if (isMin) {
			table.setMinimumColumnWidth(col, min);
		}
		if (isMax) {
			table.setMaximumColumnWidth(col, max);
		}
	}
}