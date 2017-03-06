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

package com.openkm.frontend.client.util.metadata;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * DatabaseMetadataCommon
 *
 * @author jllort
 *
 */
public abstract class DatabaseMetadataCommon implements IsSerializable {
	private Double realId;
	private String realTable;

	/**
	 * loadFromMap
	 */
	public void loadFromMap(Map<String, String> map) {
		if (map.containsKey(DatabaseMetadataMap.MV_NAME_ID)) {
			setRealId(DatabaseMetadataMap.getDoubleValue(map.get(DatabaseMetadataMap.MV_NAME_ID)));
		}

		if (map.containsKey(DatabaseMetadataMap.MV_NAME_TABLE)) {
			setRealTable(map.get(DatabaseMetadataMap.MV_NAME_TABLE));
		}
	}

	/**
	 * restoreToMap
	 */
	public Map<String, String> restoreToMap() {
		Map<String, String> map = new HashMap<String, String>();

		if (realId != null) {
			map.put(DatabaseMetadataMap.MV_NAME_ID, DatabaseMetadataMap.mapDoubleValue(getRealId()));
		}

		if (realTable != null) {
			map.put(DatabaseMetadataMap.MV_NAME_TABLE, getRealTable());
		}

		return map;
	}

	public Double getRealId() {
		return realId;
	}

	public void setRealId(Double realId) {
		this.realId = realId;
	}

	public String getRealTable() {
		return realTable;
	}

	public void setRealTable(String realTable) {
		this.realTable = realTable;
	}
}