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

import com.openkm.util.FormatUtil;

public class HTMLDetailedInfoDecorator implements InfoDecorator {
	private int idx;
	private int total;

	public HTMLDetailedInfoDecorator(int total) {
		this.total = total;
	}

	@Override
	public String print(String path, long size, String error) {
		StringBuffer sb = new StringBuffer();

		if (idx++ % 2 == 0) {
			sb.append("<tr class=\"even\">");
		} else {
			sb.append("<tr class=\"odd\">");
		}

		sb.append("<td nowrap>");
		sb.append(idx);
		sb.append(" (");
		sb.append(idx * 100 / total);
		sb.append("%)</td><td>");

		if (error != null) {
			sb.append("<div class=\"warn\">");
		} else {
			sb.append("<div>");
		}

		sb.append(path);

		if (error != null) {
			sb.append(" -> ");
			sb.append(error);
		}

		sb.append("</div></td><td nowrap>");
		sb.append(FormatUtil.formatSize(size));
		sb.append("</td></tr>");

		return sb.toString();
	}
}
