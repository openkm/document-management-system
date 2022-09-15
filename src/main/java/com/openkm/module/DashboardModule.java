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

package com.openkm.module;

import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.QueryParams;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public interface DashboardModule {

	/**
	 * Get list of locked documents by user
	 */
	List<DashboardDocumentResult> getUserLockedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get list of checked-out documents by user
	 */
	List<DashboardDocumentResult> getUserCheckedOutDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user subscribed documents
	 */
	List<DashboardDocumentResult> getUserSubscribedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user subscribed folders
	 */
	List<DashboardFolderResult> getUserSubscribedFolders(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user last uploaded documents
	 */
	List<DashboardDocumentResult> getUserLastUploadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user last modified documents
	 */
	List<DashboardDocumentResult> getUserLastModifiedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user last downloaded documents
	 */
	List<DashboardDocumentResult> getUserLastDownloadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user last imported mails
	 */
	List<DashboardMailResult> getUserLastImportedMails(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user last imported mail attachments
	 */
	List<DashboardDocumentResult> getUserLastImportedMailAttachments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get user documents size
	 */
	long getUserDocumentsSize(String token) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get user searchs
	 */
	List<QueryParams> getUserSearchs(String token) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Find
	 */
	List<DashboardDocumentResult> find(String token, int pqId) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get last week top downloaded documents
	 */
	List<DashboardDocumentResult> getLastWeekTopDownloadedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get last month downloaded documents
	 */
	List<DashboardDocumentResult> getLastMonthTopDownloadedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get last week top modified documents
	 */
	List<DashboardDocumentResult> getLastWeekTopModifiedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get las month top modified documentd
	 */
	List<DashboardDocumentResult> getLastMonthTopModifiedDocuments(String token) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get last modified documents
	 */
	List<DashboardDocumentResult> getLastModifiedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get last uploaded documents
	 */
	List<DashboardDocumentResult> getLastCreatedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get last uploaded documents
	 */
	List<DashboardDocumentResult> getLastUploadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get last created folders
	 */
	List<DashboardFolderResult> getLastCreatedFolders(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Visite node
	 */
	void visiteNode(String token, String source, String node, Calendar date) throws AccessDeniedException, RepositoryException,
			DatabaseException;
}
