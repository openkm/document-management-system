/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.module;

import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.LockInfo;
import com.openkm.bean.Version;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.principal.PrincipalAdapterException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DocumentModule {

	/**
	 * Creates a new document in the repository.
	 *
	 * @param doc A document object with the new document properties.
	 * @param content The document content in bytes.
	 * @return A document object with the properties of the new created document.
	 * @throws UnsupportedMimeTypeException If the uploaded file has an unsupported MIME type.
	 * @throws FileSizeExceededException If the document content is biggest than the maximum accepted.
	 * @throws PathNotFoundException If the parent folder doesn't exist.
	 * @throws ItemExistsException If there is already a document in the repository with the same name.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the parent document folder
	 *         because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws IOException An error when inserting document data into the repository.
	 */
	public Document create(String token, Document doc, InputStream is) throws UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, ItemExistsException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			ExtensionException, AutomationException;

	/**
	 * Deletes a document from the repository. It is a logical delete, so really is moved to the user trash and can be
	 * restored.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws LockException Can't delete a locked document.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void delete(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, AutomationException, RepositoryException, DatabaseException, ExtensionException;

	/**
	 * Rename a document in the repository.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @param newName The new folder name.
	 * @return An document object with the new document properties.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws ItemExistsException If there is already a document in the repository with the same name in the same path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Document rename(String token, String docPath, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, AutomationException, LockException, RepositoryException,
			DatabaseException, ExtensionException;

	/**
	 * Obtain document properties from the repository.
	 *
	 * @param @param docId The path that identifies an unique document or its UUID.
	 * @return The document properties.
	 * @throws PathNotFoundException If there is no document in this repository path. you can't modify the document
	 *         because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Document getProperties(String token, String docId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Set document properties in the repository.
	 *
	 * @param doc An document object with the properties
	 * @throws VersionException A document checked in can't be modified.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void setProperties(String token, Document doc) throws VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Obtain document content from the repository.
	 *
	 * @param docId The path that identifies an unique document or its UUID.
	 * @return The content of the document file.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify this document because of lack of
	 *         permissions. Related to extended security.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws IOException An error when retrieving document data from the repository.
	 */
	public InputStream getContent(String token, String docId, boolean checkout) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException;

	/**
	 * Obtain document content from the repository.
	 *
	 * @param docId The path that identifies an unique document or its UUID.
	 * @param versionId The id of the version to get the content from.
	 * @return The content of the document file.
	 * @throws AccessDeniedException If there is any security problem: you can't modify this document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no folder in the repository with this path.
	 * @throws IOException An error when retrieving document data from the repository.
	 */
	public InputStream getContentByVersion(String token, String docId, String versionId) throws RepositoryException,
			AccessDeniedException, PathNotFoundException, IOException, DatabaseException;

	/**
	 * Retrieve a list of child documents from an existing folder.
	 *
	 * @param fldId The path that identifies an unique folder or its UUID.
	 * @return A Collection with the child documents.
	 * @throws PathNotFoundException If there is no folder in this repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	@Deprecated
	public List<Document> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Retrieve a list of children documents from an existing folder.
	 *
	 * @param fldId The path that identifies an unique folder or its UUID.
	 * @return A Collection with the child documents.
	 * @throws PathNotFoundException If there is no folder in this repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<Document> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Checkout the document to edit it. The document can't be edited by another user until it is checked in o the
	 * checkout is canceled.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void checkout(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Cancel a previous checked out state in a document.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void cancelCheckout(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Force to cancel a previous checked out state in a document.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void forceCancelCheckout(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, PrincipalAdapterException;

	/**
	 * Test if a node has been already checked out.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @return True if the document is in checked out state, or false if not.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public boolean isCheckedOut(String token, String docPath) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Check in the document to create a new version.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @param comment A comment for this checkin.
	 * @return A version object with the properties of the new generated version.
	 * @throws LockException A locked document can't be modified.
	 * @throws VersionException If the nodes was not previously checked out.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Version checkin(String token, String docPath, InputStream is, String comment)
			throws FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, LockException,
			VersionException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException, AutomationException;

	/**
	 * Check in the document to create a new version.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @param comment A comment for this checkin.
	 * @param increment Which increment should be increased.
	 * @return A version object with the properties of the new generated version.
	 * @throws LockException A locked document can't be modified.
	 * @throws VersionException If the nodes was not previously checked out.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Version checkin(String token, String docPath, InputStream is, String comment, int increment)
			throws FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, LockException,
			VersionException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException, AutomationException;

	/**
	 * Get the document version history.
	 *
	 * @param docId The path that identifies an unique document or its UUID.
	 * @return A Collection of Versions with every document version.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<Version> getVersionHistory(String token, String docId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException;

	/**
	 * Lock a document, so only is editable by the locker.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws LockException If the node is already locked.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public LockInfo lock(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Unlock a document, so will be editable for other users.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws LockException If the node is not locked.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void unlock(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * For document unlock, so will be editable for other users. This action need to be perfomed by and administrator.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws LockException If the node is not locked.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void forceUnlock(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, PrincipalAdapterException;

	/**
	 * Tell if a document is locked.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws AccessDeniedException If there is any security problem: you can't access this document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any repository problem.
	 * @throws PathNotFoundException If there is no document in the repository with this path.
	 * @return True if the document is locked, and False otherwise.
	 */
	public boolean isLocked(String token, String docPath) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			DatabaseException;

	/**
	 * Returns a lock information.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws AccessDeniedException If there is any security problem: you can't access this document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any repository problem.
	 * @throws PathNotFoundException If there is no document in the repository with this path.
	 * @throws LockException If the node is not locked.
	 * @return The lock info.
	 */
	public LockInfo getLockInfo(String token, String docPath) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			LockException, DatabaseException;

	/**
	 * Deletes definitively a document from the repository. It is a physical delete, so the document can't be restored.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws AccessDeniedException If there is any security problem: you can't access this document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no document in the repository with this path.
	 */
	public void purge(String token, String docPath) throws LockException, AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException, ExtensionException;

	/**
	 * Move a document to another location in the repository.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @param fldPath The destination folder path.
	 * @throws PathNotFoundException If the dstPath does not exists
	 * @throws ItemExistsException If there is already a document in the destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document's parent folder or
	 *         the destination folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void move(String token, String docPath, String fldPath) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, LockException, RepositoryException, DatabaseException, ExtensionException,
			AutomationException;

	/**
	 * Copy a document to another location in the repository.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @param dstPath The destination folder path.
	 * @throws PathNotFoundException If the dstPath does not exists
	 * @throws ItemExistsException If there is already a document in the destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document's parent folder or
	 *         the destination folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void copy(String token, String docPath, String dstPath) throws ItemExistsException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, UserQuotaExceededException,
			ExtensionException, AutomationException;

	/**
	 * Copy a document to another location in the repository.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @param dstPath The destination folder path.
	 * @param docName The name of the new document or null if not changed
	 * @param extAttr Attributes to define what need to be duplicated.
	 * @throws PathNotFoundException If the dstPath does not exists
	 * @throws ItemExistsException If there is already a document in the destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document's parent folder or
	 *         the destination folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void extendedCopy(String token, String docPath, String dstPath, String docName, ExtendedAttributes extAttr)
			throws ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, UserQuotaExceededException, ExtensionException, AutomationException;

	/**
	 * Revert the document to an specific previous version.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @param versionId The version id to revert to.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the document because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void restoreVersion(String token, String docPath, String versionId) throws PathNotFoundException,
			AccessDeniedException, LockException, RepositoryException, DatabaseException, ExtensionException;

	/**
	 * Purge a Document version history, so delete all previous versions but last one. Used to free Document version
	 * size.
	 *
	 * @param docPath The path that identifies an unique document.
	 * @throws AccessDeniedException If there is any security problem: you can't access this folder because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no folder in the repository with this path.
	 */
	public void purgeVersionHistory(String token, String docPath) throws AccessDeniedException, PathNotFoundException,
			LockException, RepositoryException, DatabaseException;

	/**
	 * Get the version size of a Document.
	 *
	 * @param docId The path that identifies an unique document or its UUID.
	 * @throws AccessDeniedException If there is any security problem: you can't access this folder because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no folder in the repository with this path.
	 */
	public long getVersionHistorySize(String token, String docId) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			DatabaseException;

	/**
	 * Test if a document path is valid.
	 *
	 * @param docId The path that identifies an unique document or its UUID.
	 * @return True if is a valid document path, otherwise false.
	 * @throws PathNotFoundException If the node does not exists.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public boolean isValid(String token, String docId) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Get the document path from a UUID
	 *
	 * @param uuid The unique document id.
	 * @return The document path
	 * @throws AccessDeniedException If there is any security problem: you can't access this node because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any problem.
	 */
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException;
}
