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

package com.openkm.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
import java.util.Map;

/**
 * OKMDatabaseMetadataServiceAsync
 *
 * @author jllort
 *
 */
public interface OKMDatabaseMetadataServiceAsync {
	public void executeValueQuery(String table, String filter, String order, AsyncCallback<List<Map<String, String>>> callback);

	public void updateValue(Map<String, String> map, AsyncCallback<?> callback);

	public void createValue(Map<String, String> map, AsyncCallback<Double> callback);

	public void executeMultiValueQuery(List<String> tables, String query, AsyncCallback<List<List<Map<String, String>>>> callback);

	public void getNextSequenceValue(String table, String column, AsyncCallback<Double> callback);

	public void deleteValue(Map<String, String> map, AsyncCallback<?> callback);
}