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

package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Permission
 *
 * @author jllort
 * @see com.openkm.bean.Permission
 */
public class GWTPermission implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public static final int REMOVED = 0;
	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int DELETE = 4;
	public static final int SECURITY = 8;

	// Extended security
	public static final int DOWNLOAD = 1024;
	public static final int START_WORKFLOW = 2048;
	public static final int COMPACT_HISTORY = 4096;
	public static final int PROPERTY_GROUP = 8192;

	private String item;
	private int permissions;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}
}