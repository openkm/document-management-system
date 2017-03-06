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

package com.openkm.extension.frontend.client.widget.extraworkspace;

import java.util.ArrayList;
import java.util.List;

/**
 * ExtraWorkspace
 *
 * @author jllort
 *
 */
public class ExtraTabWorkspace {
	private static ExtraTabWorkspace singleton;
	private static final String UUID = "c20c69a8-7d6b-4539-9e6b-6f2c11e84168";
	private TabWorkspace tabWorkspace;

	/**
	 * ExtraWorkspace
	 */
	public ExtraTabWorkspace(List<String> uuidList) {
		singleton = this;

		if (isRegistered(uuidList)) {
			tabWorkspace = new TabWorkspace();
		}
	}

	/**
	 * getExtensions
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(tabWorkspace);
		return extensions;
	}

	/**
	 * isRegistered
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}