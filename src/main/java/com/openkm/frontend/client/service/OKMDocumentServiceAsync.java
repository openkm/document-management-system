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
import com.openkm.frontend.client.bean.GWTExtendedAttributes;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 */
public interface OKMDocumentServiceAsync {
	void getChilds(String fldPath, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTDocument>> callback);

	void getVersionHistory(String docPath, AsyncCallback<List<GWTVersion>> callback);

	void delete(String docPath, AsyncCallback<?> callback);

	void checkout(String docPath, AsyncCallback<?> callback);

	void cancelCheckout(String docPath, AsyncCallback<?> callback);

	void lock(String docPath, AsyncCallback<?> callback);

	void unlock(String docPath, AsyncCallback<?> callback);

	void rename(String docPath, String newName, AsyncCallback<GWTDocument> callback);

	void move(String docPath, String destPath, AsyncCallback<?> callback);

	void purge(String docPath, AsyncCallback<?> callback);

	void restoreVersion(String docPath, String versionId, AsyncCallback<?> callback);

	void getProperties(String docPath, AsyncCallback<GWTDocument> callback);

	void copy(String docPath, String fldPath, AsyncCallback<?> callback);

	void isValid(String docPath, AsyncCallback<Boolean> callback);

	void getVersionHistorySize(String docPath, AsyncCallback<Long> callback);

	void purgeVersionHistory(String docPath, AsyncCallback<?> callback);

	void forceUnlock(String docPath, AsyncCallback<?> callback);

	void forceCancelCheckout(String docPath, AsyncCallback<?> callback);

	void createFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
							Map<String, List<Map<String, String>>> tableProperties, AsyncCallback<GWTDocument> callback);

	void updateFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
 							Map<String, List<Map<String, String>>> tableProperties, AsyncCallback<String> callback);

	void convertToPdf(String docPath, AsyncCallback<String> callback);

	void mergePdf(String docName, List<String> paths, AsyncCallback<?> callback);

	void getAllTemplates(AsyncCallback<List<GWTDocument>> callback);

	void createFromTemplate(String docPath, String fldPath, String name, GWTExtendedAttributes attributes,
							AsyncCallback<Object> callback);

	void getHTMLContent(String docPath, boolean checkout, AsyncCallback<String> callback);

	void setHTMLContent(String docPath, String mails, String users, String roles, String message, String content, String comment,
						int increaseVersion, AsyncCallback<String> callback);

	/*
	 * ======================== LiveEdit methods =========================
	 */
	void liveEditCheckin(String docPath, String mails, String users, String roles, String message, String comment,
						 int increaseVersion, AsyncCallback<?> callback);

	void liveEditForceCancelCheckout(String docPath, AsyncCallback<?> callback);

	void liveEditCancelCheckout(String docPath, AsyncCallback<?> callback);
}
