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

package com.openkm.frontend.client.util;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.openkm.frontend.client.bean.GWTPropertyParams;
import com.openkm.frontend.client.bean.GWTQueryParams;

/**
 * JSONUtil
 *
 * @author jllort
 *
 */
public class JSONUtil {

	/**
	 * toJson
	 *
	 */
	public static JSONObject toJson(Object obj) {
		JSONObject json = new JSONObject();
		if (obj instanceof GWTQueryParams) {
			GWTQueryParams params = (GWTQueryParams) obj;
			json.put("author", new JSONString(params.getAuthor()));
			json.put("keywords", new JSONString(params.getKeywords()));
			json.put("content", new JSONString(params.getContent()));
			json.put("name", new JSONString(params.getName()));
			json.put("path", new JSONString(URL.encodeQueryString(params.getPath())));
			json.put("mimeType", new JSONString(params.getMimeType()));
			json.put("domain", new JSONNumber(params.getDomain()));
			json.put("mailFrom", new JSONString(params.getMailFrom()));
			json.put("mailTo", new JSONString(params.getMailTo()));
			json.put("mailSubject", new JSONString(params.getMailSubject()));
			json.put("categoryUuid", new JSONString(params.getCategoryUuid()));
			json.put("categoryPath", new JSONString(URL.encodeQueryString(params.getCategoryPath())));
			json.put("operator", new JSONString(params.getOperator()));
			if (params.getLastModifiedFrom() != null) {
				json.put("lastModifiedFrom", new JSONString(ISO8601.formatBasic(params.getLastModifiedFrom())));
			}
			if (params.getLastModifiedTo() != null) {
				json.put("lastModifiedTo", new JSONString(ISO8601.formatBasic(params.getLastModifiedTo())));
			}
			if (!params.getProperties().isEmpty()) {
				JSONObject properties = new JSONObject();
				for (String key : params.getProperties().keySet()) {
					GWTPropertyParams propertyParam = params.getProperties().get(key);
					JSONObject property = new JSONObject();
					// Only is necessary groupName and value
					property.put("grpName", new JSONString(propertyParam.getGrpName()));
					property.put("value", new JSONString(propertyParam.getValue()));
					properties.put(key, property);
				}
				json.put("properties", properties);
			}
		}
		return json;
	}
}