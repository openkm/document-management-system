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
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;

import java.util.List;
import java.util.Map;

/**
 * OKMFolderServiceAsync
 *
 * @author jllort
 *
 */
public interface OKMFolderServiceAsync {
	public void getChilds(String fldId, boolean extraColumns, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTFolder>> callback);

	public void delete(String fldPath, AsyncCallback<?> callback);

	public void create(String fldId, String fldIdParent, AsyncCallback<GWTFolder> callback);

	public void rename(String fldId, String newName, AsyncCallback<GWTFolder> callback);

	public void move(String fldPath, String dstPath, AsyncCallback<?> callback);

	public void purge(String fldPath, AsyncCallback<?> callback);

	public void getProperties(String fldPath, AsyncCallback<GWTFolder> callback);

	public void copy(String fldPath, String dstPath, AsyncCallback<?> callback);

	public void isValid(String fldPath, AsyncCallback<Boolean> callback);

	public void getCategorizedChilds(String fldId, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTFolder>> callback);

	public void getMetadataChilds(String fldId, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTFolder>> callback);

	public void getThesaurusChilds(String fldId, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTFolder>> callback);
}