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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TimeHelper
 *
 * @author jllort
 *
 */
public class TimeHelper {
	private static Map<String, Date> timeMap = new HashMap<String, Date>();

	/**
	 * changeControlTime
	 */
	public static void changeControlTime(String key) {
		timeMap.put(key, new Date());
	}

	/**
	 * hasElapsedEnoughtTime
	 */
	public static boolean hasElapsedEnoughtTime(String key, int milis, boolean reset) {
		if (timeMap.containsKey(key)) {
			boolean enought = ((new Date().getTime() - timeMap.get(key).getTime()) > milis);
			if (reset && !enought) {
				timeMap.put(key, new Date()); // Start again
			}
			return enought;
		} else {
			timeMap.put(key, new Date());
			return true;
		}
	}

	/**
	 * hasElapsedEnoughtTime
	 */
	public static boolean hasElapsedEnoughtTime(String key, int milis) {
		return hasElapsedEnoughtTime(key, milis, false);
	}

	/**
	 * hasControlTime
	 */
	public static boolean hasControlTime(String key) {
		return timeMap.containsKey(key);
	}

	/**
	 * clean
	 */
	public static void removeControlTime(String key) {
		timeMap.remove(key);
	}
}