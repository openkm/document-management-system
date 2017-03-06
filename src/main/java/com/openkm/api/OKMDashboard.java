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

package com.openkm.api;

import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.DashboardModule;
import com.openkm.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * @author pavila
 *
 */
public class OKMDashboard implements DashboardModule {
	private static Logger log = LoggerFactory.getLogger(OKMDashboard.class);
	private static OKMDashboard instance = new OKMDashboard();

	private OKMDashboard() {
	}

	public static OKMDashboard getInstance() {
		return instance;
	}

	@Override
	public List<DashboardDocumentResult> getUserCheckedOutDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserCheckedOutDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getUserCheckedOutDocuments(token);
		log.debug("getUserCheckedOutDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastModifiedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getUserLastModifiedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getUserLastModifiedDocuments(token);
		log.debug("getUserLastModifiedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getUserLockedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLockedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getUserLockedDocuments(token);
		log.debug("getUserLockedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getUserSubscribedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserSubscribedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getUserSubscribedDocuments(token);
		log.debug("getUserSubscribedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardFolderResult> getUserSubscribedFolders(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserSubscribedFolders({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardFolderResult> result = dm.getUserSubscribedFolders(token);
		log.debug("getUserSubscribedFolders: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastUploadedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getUserLastUploadedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getUserLastUploadedDocuments(token);
		log.debug("getUserLastUploadedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastDownloadedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getUserLastDownloadedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getUserLastDownloadedDocuments(token);
		log.debug("getUserLastDownloadedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardMailResult> getUserLastImportedMails(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLastImportedMails({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardMailResult> result = dm.getUserLastImportedMails(token);
		log.debug("getUserLastImportedMails: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastImportedMailAttachments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getUserLastImportedMailAttachments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getUserLastImportedMailAttachments(token);
		log.debug("getUserLastImportedMailAttachments: {}", result);
		return result;
	}

	@Override
	public long getUserDocumentsSize(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserDocumentsSize({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		long size = dm.getUserDocumentsSize(token);
		log.debug("getUserDocumentsSize: {}", size);
		return size;
	}

	@Override
	public List<QueryParams> getUserSearchs(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserSearchs({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<QueryParams> searchs = dm.getUserSearchs(token);
		log.debug("getUserSearchs: {}", searchs);
		return searchs;
	}

	@Override
	public List<DashboardDocumentResult> find(String token, int qpId) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("find({}, {})", token, qpId);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> documents = dm.find(token, qpId);
		log.debug("find: {}", documents);
		return documents;
	}

	@Override
	public List<DashboardDocumentResult> getLastWeekTopDownloadedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getLastWeekTopDownloadedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getLastWeekTopDownloadedDocuments(token);
		log.debug("getLastWeekTopDownloadedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getLastMonthTopDownloadedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getLastMonthTopDownloadedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getLastMonthTopDownloadedDocuments(token);
		log.debug("getLastMonthTopDownloadedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getLastWeekTopModifiedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getLastWeekTopModifiedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getLastWeekTopModifiedDocuments(token);
		log.debug("getLastWeekTopModifiedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getLastMonthTopModifiedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getLastMonthTopModifiedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getLastMonthTopModifiedDocuments(token);
		log.debug("getLastMonthTopModifiedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getLastModifiedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getLastModifiedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getLastModifiedDocuments(token);
		log.debug("getLastModifiedDocuments: {}", result);
		return result;
	}

	@Override
	public List<DashboardDocumentResult> getLastUploadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getLastUploadedDocuments({})", token);
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> result = dm.getLastUploadedDocuments(token);
		log.debug("getLastUploadedDocuments: {}", result);
		return result;
	}

	@Override
	public void visiteNode(String token, String source, String node, Calendar date) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("visiteNode({}, {}, {}, {})", new Object[]{token, source, node, date});
		DashboardModule dm = ModuleManager.getDashboardModule();
		dm.visiteNode(token, source, node, date);
		log.debug("visiteNode: void");
	}
}
