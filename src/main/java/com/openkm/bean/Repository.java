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

package com.openkm.bean;

import java.io.Serializable;

/**
 * @author pavila
 *
 */
public class Repository implements Serializable {
	private static final long serialVersionUID = -6920884124466924375L;

	public static final String OKM = "okm";
	public static final String OKM_URI = "http://www.openkm.org/1.0";
	public static final String ROOT = "okm:root";
	public static final String TRASH = "okm:trash";
	public static final String TEMPLATES = "okm:templates";
	public static final String THESAURUS = "okm:thesaurus";
	public static final String CATEGORIES = "okm:categories";
	public static final String SYS_CONFIG = "okm:config";
	public static final String SYS_CONFIG_TYPE = "okm:sysConfig";
	public static final String SYS_CONFIG_UUID = "okm:uuid";
	public static final String SYS_CONFIG_VERSION = "okm:version";
	public static final String PERSONAL = "okm:personal";
	public static final String MAIL = "okm:mail";
	public static final String USER_CONFIG = "okm:config";
	public static final String USER_CONFIG_TYPE = "okm:userConfig";
	public static final String LOCK_TOKENS = "okm:lockTokens";
	public static final String METADATA = "okm:metadata";

	private static String uuid;
	private static String updateMsg;
	private String id;
	private String name;
	private String description;

	public static String getUuid() {
		return uuid;
	}

	public static void setUuid(String uuid) {
		Repository.uuid = uuid;
	}

	public static String getUpdateMsg() {
		return updateMsg;
	}

	public static void setUpdateMsg(String updateMsg) {
		Repository.updateMsg = updateMsg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
