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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;

import java.util.List;
import java.util.Map;

/**
 * OKMDatabaseMetadataService
 *
 * @author jllort
 *
 */
@RemoteServiceRelativePath("DatabaseMetadata")
public interface OKMDatabaseMetadataService extends RemoteService {
	public List<Map<String, String>> executeValueQuery(String table, String filter, String order) throws OKMException;

	public void updateValue(Map<String, String> map) throws OKMException;

	public Double createValue(Map<String, String> map) throws OKMException;

	public List<List<Map<String, String>>> executeMultiValueQuery(List<String> tables, String query) throws OKMException;

	public Double getNextSequenceValue(String table, String column) throws OKMException;

	public void deleteValue(Map<String, String> map) throws OKMException;
}
