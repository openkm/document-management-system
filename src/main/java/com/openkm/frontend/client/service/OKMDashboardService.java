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
	public List<GWTDashboardDocumentResult> getUserLockedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getUserCheckedOutDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getUserLastModifiedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getUserSubscribedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getUserLastUploadedDocuments() throws OKMException;

	public List<GWTDashboardFolderResult> getUserSubscribedFolders() throws OKMException;

	public List<GWTQueryParams> getUserSearchs() throws OKMException;

	public List<GWTDashboardDocumentResult> find(int id) throws OKMException;

	public List<GWTDashboardDocumentResult> getLastWeekTopDownloadedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getLastMonthTopDownloadedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getLastWeekTopModifiedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getLastMonthTopModifiedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getUserLastDownloadedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getLastModifiedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getLastUploadedDocuments() throws OKMException;

	public List<GWTDashboardDocumentResult> getUserLastImportedMailAttachments() throws OKMException;

	public List<GWTDashboardMailResult> getUserLastImportedMails() throws OKMException;

	public void visiteNode(String source, String node, Date date) throws OKMException;
}
