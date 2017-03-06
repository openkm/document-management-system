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

package com.openkm.extension.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.openkm.frontend.client.util.metadata.DatabaseMetadataCommon;

import java.util.Map;

/**
 * GWTsecurity
 *
 * @author jllort
 *
 */
public class GWTExtendedSecurity extends DatabaseMetadataCommon implements IsSerializable {
	public static final String TYPE_USER = "user";
	public static final String TYPE_ROLE = "role";

	// Metadata Virtual Name mapping 
	public static final String MV_TABLE_NAME = "security";
	public static final String MV_COLUMN_NAME_UUID = "uuid_id";
	public static final String MV_COLUMN_NAME_TYPE = "type";
	public static final String MV_COLUMN_NAME_NAME = "name";

	private String uuid;
	private String type;
	private String name;

	@Override
	public void loadFromMap(Map<String, String> map) {
		super.loadFromMap(map);

		if (map.containsKey(MV_COLUMN_NAME_UUID)) {
			setUuid(map.get(MV_COLUMN_NAME_UUID));
		}

		if (map.containsKey(MV_COLUMN_NAME_TYPE)) {
			setType(map.get(MV_COLUMN_NAME_TYPE));
		}

		if (map.containsKey(MV_COLUMN_NAME_NAME)) {
			setName(map.get(MV_COLUMN_NAME_NAME));
		}
	}

	@Override
	public Map<String, String> restoreToMap() {
		Map<String, String> map = super.restoreToMap();

		if (uuid != null) {
			map.put(MV_COLUMN_NAME_UUID, getUuid());
		}

		if (type != null) {
			map.put(MV_COLUMN_NAME_TYPE, getType());
		}

		if (name != null) {
			map.put(MV_COLUMN_NAME_NAME, getName());
		}

		return map;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
