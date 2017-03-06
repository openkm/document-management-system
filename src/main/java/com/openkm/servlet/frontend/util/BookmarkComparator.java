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

package com.openkm.servlet.frontend.util;

import com.openkm.frontend.client.bean.GWTBookmark;

/**
 * BookmarkComparator
 *
 * @author jllort
 *
 */
public class BookmarkComparator extends CultureComparator<GWTBookmark> {

	protected BookmarkComparator(String locale) {
		super(locale);
	}

	public static BookmarkComparator getInstance(String locale) {
		try {
			BookmarkComparator comparator = (BookmarkComparator) CultureComparator.getInstance(BookmarkComparator.class, locale);
			return comparator;
		} catch (Exception e) {
			return new BookmarkComparator(locale);
		}
	}

	public static BookmarkComparator getInstance() {
		BookmarkComparator instance = getInstance(CultureComparator.DEFAULT_LOCALE);
		return instance;
	}

	public int compare(GWTBookmark arg0, GWTBookmark arg1) {
		GWTBookmark first = arg0;
		GWTBookmark second = arg1;

		// Compare first with type, and second for name
		if (!arg0.getType().equals(arg0.getType())) {
			return arg1.getType().compareTo(arg0.getType()); // inverse comparation
		} else {
			return collator.compare(first.getName(), second.getName());
		}
	}
}