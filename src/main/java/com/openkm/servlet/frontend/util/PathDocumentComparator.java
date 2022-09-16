/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.servlet.frontend.util;

import com.openkm.frontend.client.bean.GWTDocument;

/**
 * PathDocumentComparator
 *
 * @author jllort
 */
public class PathDocumentComparator extends CultureComparator<GWTDocument> {

	protected PathDocumentComparator(String locale) {
		super(locale);
	}

	public static PathDocumentComparator getInstance(String locale) {
		try {
			return (PathDocumentComparator) CultureComparator.getInstance(PathDocumentComparator.class, locale);
		} catch (Exception e) {
			return new PathDocumentComparator(locale);
		}
	}

	public static PathDocumentComparator getInstance() {
		return getInstance(CultureComparator.DEFAULT_LOCALE);
	}

	public int compare(GWTDocument first, GWTDocument second) {
		String[] paths1 = first.getPath().split("/");
		String[] paths2 = second.getPath().split("/");

		for (int i = 0; i < paths1.length - 2; i++) {        // Not compares document name here
			if (i != paths2.length - 2) {
				break;
			}
			if (!paths1[i].equals(paths2[i])) {            // Ordering by folder names
				return collator.compare(paths1[i], paths2[i]);
			}
		}

		if (paths1.length == paths2.length) {            // Here is compared document name
			return collator.compare(paths1[paths1.length - 1], paths2[paths2.length - 1]);
		} else {
			return paths1.length - paths2.length;        // Otherwise number of folders length
		}
	}
}
