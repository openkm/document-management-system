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

package com.openkm.util.impexp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLInfoDecorator implements InfoDecorator {
	private static Logger log = LoggerFactory.getLogger(HTMLInfoDecorator.class);
	private int idx;
	private int total;
	private int oldPerCent = -1;

	public HTMLInfoDecorator(int total) {
		this.total = total;
	}

	@Override
	public String print(String path, long size, String error) {
		log.debug("print({}, {}, {})", new Object[]{path, size, error});
		StringBuffer sb = new StringBuffer();
		int perCent = ++idx * 100 / total;

		if (perCent > oldPerCent) {
			sb.append(" (");
			sb.append(perCent);
			sb.append("%)<br/>\n");
			oldPerCent = perCent;
		}

		if (error != null) {
			sb.append("<div class=\"warn\">");
			sb.append(path);
			sb.append(" -> ");
			sb.append(error);
			sb.append("</div>\n");
		}

		log.debug("print: {}", sb.toString());
		return sb.toString();
	}
}
