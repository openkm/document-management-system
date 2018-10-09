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

package com.openkm.util.tags;

import org.hibernate.collection.PersistentSet;

import com.openkm.core.Config;
import com.openkm.spring.PrincipalUtils;

import java.util.Collection;

public class UtilFunctions {
	/**
	 * Check if collection contains an element.
	 */
	public static boolean contains(Collection<?> collection, Object obj) {
		if (collection != null) {
			if (collection instanceof PersistentSet) {
				for (Object elto : collection) {
					if (elto.equals(obj)) {
						return true;
					}
				}

				return false;
			} else {
				return collection.contains(obj);
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Replace string
	 */
	public static String replace(String input, String regex, String replacement) {
		if (input != null && !input.isEmpty()) {
			return input.replaceAll(regex, replacement);
		} else {
			return null;
		}
	}
	
	/**
	 * Check for user with ROLE_ADMIN role.
	 */
	public static boolean isAdmin() {
		return PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE);
	}

	/**
	 * Make bitwise and.
	 */
	public static int bitwiseAnd(int arg0, int arg1) {
		return arg0 & arg1;
	}
}
