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
@RemoteServiceRelativePath("Dashboard")
public interface OKMDashboardService extends RemoteService {
	List<GWTDashboardDocumentResult> getUserLockedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getUserCheckedOutDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getUserLastModifiedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getUserSubscribedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getUserLastUploadedDocuments() throws OKMException;

	List<GWTDashboardFolderResult> getUserSubscribedFolders() throws OKMException;

	List<GWTQueryParams> getUserSearchs() throws OKMException;

	List<GWTDashboardDocumentResult> find(int id) throws OKMException;

	List<GWTDashboardDocumentResult> getLastWeekTopDownloadedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getLastMonthTopDownloadedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getLastWeekTopModifiedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getLastMonthTopModifiedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getUserLastDownloadedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getLastModifiedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getLastUploadedDocuments() throws OKMException;

	List<GWTDashboardDocumentResult> getLastCreatedDocuments() throws OKMException;

	List<GWTDashboardFolderResult> getLastCreatedFolders() throws OKMException;

	List<GWTDashboardDocumentResult> getUserLastImportedMailAttachments() throws OKMException;

	List<GWTDashboardMailResult> getUserLastImportedMails() throws OKMException;

	void visiteNode(String source, String node, Date date) throws OKMException;
}
