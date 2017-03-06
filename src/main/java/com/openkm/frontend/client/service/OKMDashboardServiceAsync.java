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
import com.openkm.frontend.client.bean.GWTDashboardDocumentResult;
import com.openkm.frontend.client.bean.GWTDashboardFolderResult;
import com.openkm.frontend.client.bean.GWTDashboardMailResult;
import com.openkm.frontend.client.bean.GWTQueryParams;

import java.util.Date;
import java.util.List;

/**
 * @author jllort
 *
 */
public interface OKMDashboardServiceAsync {
	public void getUserLockedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserCheckedOutDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserLastModifiedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserSubscribedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserLastUploadedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserSubscribedFolders(AsyncCallback<List<GWTDashboardFolderResult>> callback);

	public void getUserSearchs(AsyncCallback<List<GWTQueryParams>> callback);

	public void find(int id, AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getLastWeekTopDownloadedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getLastMonthTopDownloadedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getLastWeekTopModifiedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getLastMonthTopModifiedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserLastDownloadedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getLastModifiedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getLastUploadedDocuments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserLastImportedMailAttachments(AsyncCallback<List<GWTDashboardDocumentResult>> callback);

	public void getUserLastImportedMails(AsyncCallback<List<GWTDashboardMailResult>> callback);

	public void visiteNode(String source, String node, Date date, AsyncCallback<?> callback);
}
