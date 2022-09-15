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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.bean.GWTFolder;

/**
 * @author jllort
 *
 */
public interface OKMRepositoryServiceAsync {
	void getRootFolder(AsyncCallback<GWTFolder> callback);

	void getTrashFolder(AsyncCallback<GWTFolder> callback);

	void purgeTrash(AsyncCallback<?> callback);

	void getTemplatesFolder(AsyncCallback<GWTFolder> callback);

	void getPersonalFolder(AsyncCallback<GWTFolder> callback);

	void getMailFolder(AsyncCallback<GWTFolder> callback);

	void getThesaurusFolder(AsyncCallback<GWTFolder> callback);

	void getCategoriesFolder(AsyncCallback<GWTFolder> callback);

	void getMetadataFolder(AsyncCallback<GWTFolder> callback);

	void getPathByUUID(String uuid, AsyncCallback<String> callback);

	void getUUIDByPath(String path, AsyncCallback<String> callback);

	void hasNode(String path, AsyncCallback<Boolean> callback);
}
