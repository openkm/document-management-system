/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;

import java.util.List;
import java.util.Map;

/**
 * OKMFolderService
 *
 * @author jllort
 *
 */
@RemoteServiceRelativePath("Folder")
public interface OKMFolderService extends RemoteService {
	List<GWTFolder> getChilds(String fldId, boolean extraColumns, Map<String, GWTFilter> mapFilter) throws OKMException;

	void delete(String fldPath) throws OKMException;

	GWTFolder create(String fldId, String fldIdParent) throws OKMException;

	GWTFolder rename(String fldId, String newName) throws OKMException;

	void move(String fldPath, String dstPath) throws OKMException;

	void purge(String fldPath) throws OKMException;

	GWTFolder getProperties(String fldPath) throws OKMException;

	void copy(String fldPath, String dstPath) throws OKMException;

	Boolean isValid(String fldPath) throws OKMException;

	List<GWTFolder> getCategorizedChilds(String fldPath, Map<String, GWTFilter> mapFilter) throws OKMException;

	List<GWTFolder> getMetadataChilds(String fldPath, Map<String, GWTFilter> mapFilter) throws OKMException;

	List<GWTFolder> getThesaurusChilds(String fldPath, Map<String, GWTFilter> mapFilter) throws OKMException;
}
