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
	public void getChilds(String fldPath, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTDocument>> callback);

	public void getVersionHistory(String docPath, AsyncCallback<List<GWTVersion>> callback);

	public void delete(String docPath, AsyncCallback<?> callback);

	public void checkout(String docPath, AsyncCallback<?> callback);

	public void cancelCheckout(String docPath, AsyncCallback<?> callback);

	public void lock(String docPath, AsyncCallback<?> callback);

	public void unlock(String docPath, AsyncCallback<?> callback);

	public void rename(String docPath, String newName, AsyncCallback<GWTDocument> callback);

	public void move(String docPath, String destPath, AsyncCallback<?> callback);

	public void purge(String docPath, AsyncCallback<?> callback);

	public void restoreVersion(String docPath, String versionId, AsyncCallback<?> callback);

	public void get(String docPath, AsyncCallback<GWTDocument> callback);

	public void copy(String docPath, String fldPath, AsyncCallback<?> callback);

	public void isValid(String docPath, AsyncCallback<Boolean> callback);

	public void getVersionHistorySize(String docPath, AsyncCallback<Long> callback);

	public void purgeVersionHistory(String docPath, AsyncCallback<?> callback);

	public void forceUnlock(String docPath, AsyncCallback<?> callback);

	public void forceCancelCheckout(String docPath, AsyncCallback<?> callback);

	public void createFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
	                               Map<String, List<Map<String, String>>> tableProperties, AsyncCallback<GWTDocument> callback);

	public void updateFromTemplate(String docPath, String destinationPath, List<GWTFormElement> formProperties,
	                               Map<String, List<Map<String, String>>> tableProperties, AsyncCallback<String> callback);

	public void convertToPdf(String docPath, AsyncCallback<String> callback);

	public void mergePdf(String docName, List<String> paths, AsyncCallback<?> callback);

	public void getAllTemplates(AsyncCallback<List<GWTDocument>> callback);

	public void createFromTemplate(String docPath, String fldPath, String name, GWTExtendedAttributes attributes,
	                               AsyncCallback<Object> callback);

	public void getHTMLContent(String docPath, boolean checkout, AsyncCallback<String> callback);

	public void setHTMLContent(String docPath, String mails, String users, String roles, String message, String content, String comment,
	                           int increaseVersion, AsyncCallback<String> callback);

	/*
	 * ======================== LiveEdit methods =========================
	 */
	public void liveEditCheckin(String docPath, String mails, String users, String roles, String message, String comment,
	                            int increaseVersion, AsyncCallback<?> callback);

	public void liveEditForceCancelCheckout(String docPath, AsyncCallback<?> callback);

	public void liveEditCancelCheckout(String docPath, AsyncCallback<?> callback);
}