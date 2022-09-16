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

import com.openkm.frontend.client.bean.GWTUser;

/**
 * GWTUserComparator
 *
 * @author jllort
 */
public class GWTUserComparator extends CultureComparator<GWTUser> {

	protected GWTUserComparator(String locale) {
		super(locale);
	}

	public static GWTUserComparator getInstance(String locale) {
		try {
			return (GWTUserComparator) CultureComparator.getInstance(GWTUserComparator.class, locale);
		} catch (Exception e) {
			return new GWTUserComparator(locale);
		}
	}

	public static GWTUserComparator getInstance() {
		return getInstance(CultureComparator.DEFAULT_LOCALE);
	}

	public int compare(GWTUser first, GWTUser second) {
		return collator.compare(first.getUsername().toLowerCase(), second.getUsername().toLowerCase());
	}
}
