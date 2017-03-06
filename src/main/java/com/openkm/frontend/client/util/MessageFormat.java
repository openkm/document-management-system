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

/**
 * MessageFormat
 *
 * @author jllort
 *
 */
public class MessageFormat extends Format {
	private static final long serialVersionUID = 1L;
	private String pattern;

	public MessageFormat(String pattern) {
		applyPattern(pattern);
	}

	public void applyPattern(String pattern) {
		this.pattern = pattern;
	}

	public static String format(String pattern, Object... arguments) {
		return doFormat(pattern, arguments);
	}

	public final String format(Object obj) {
		if (obj instanceof Object[]) {
			return doFormat(pattern, (Object[]) obj);
		}

		return doFormat(pattern, new Object[]{obj});
	}

	private static String doFormat(String s, Object[] arguments) {
		// A very simple implementation of format
		int i = 0;

		while (i < arguments.length) {
			String delimiter = "{" + i + "}";

			while (s.contains(delimiter)) {
				s = s.replace(delimiter, String.valueOf(arguments[i]));
			}

			i++;
		}

		return s;
	}
}
