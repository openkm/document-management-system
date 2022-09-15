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
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 */
public interface OKMMailServiceAsync {
	void getChilds(String fldPath, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTMail>> callback);

	void delete(String mailPath, AsyncCallback<?> callback);

	void move(String docPath, String destPath, AsyncCallback<?> callback);

	void purge(String mailPath, AsyncCallback<?> callback);

	void copy(String mailPath, String fldPath, AsyncCallback<?> callback);

	void getProperties(String mailPath, AsyncCallback<GWTMail> callback);

	void rename(String mailId, String newName, AsyncCallback<GWTMail> callback);

	void isValid(String mailPath, AsyncCallback<Boolean> callback);

	void forwardMail(String mailPath, String mails, String users, String roles, String message, AsyncCallback<?> callback);

	void sendMail(List<String> uuidList, Map<String, List<String>> recipients, String subject, String message,
				  boolean attachment, AsyncCallback<GWTMail> callback);

	void sendMail(List<String> uuidList, Map<String, List<String>> recipients, String subject, String message,
				  boolean attachment, String storePath, AsyncCallback<GWTMail> callback);

	void getAttachments(String uuid, AsyncCallback<List<GWTDocument>> callback);
}
