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

package com.openkm.module;

import com.openkm.automation.AutomationException;
import com.openkm.bean.ContentInfo;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;

import java.io.IOException;
import java.util.List;

public interface FolderModule {

	/**
	 * Create a new folder in the repository.
	 *
	 * @param fld A folder object with the new folder properties.
	 * @return A folder object with the new created folder properties.
	 * @throws PathNotFoundException If the parent folder doesn't exist.
	 * @throws ItemExistsException If there is already a folder in the repository with the same name in the same path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the parent folder because of
	 *         lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder create(String token, Folder fld) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException, ExtensionException, AutomationException;

	/**
	 * Obtains properties from a previously created folder.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @return A folder object with the selected folder properties.
	 * @throws PathNotFoundException If the indicated folder doesn't exist.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getProperties(String token, String fldPath) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Delete a folder the repository. It is a logical delete, so really is moved to the user trash and can be restored.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @throws LockException Can't delete a folder with locked documents.
	 * @throws PathNotFoundException If there is no folder in the repository in this path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the folder because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void delete(String token, String fldPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Deletes definitively a folder from the repository. It is a phisical delete, so the folder can't be restored.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @throws LockException Can't delete a folder with locked documents.
	 * @throws PathNotFoundException If there is no folder in the repository in this path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the folder because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void purge(String token, String fldPath) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Rename a folder in the repository.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @param newName The new folder name.
	 * @return A folder object with the new folder properties.
	 * @throws PathNotFoundException If there is no folder in the repository in this path.
	 * @throws ItemExistsException If there is already a folder in the repository with the same name in the same path.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the folder because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder rename(String token, String fldPath, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Move a folder to another location in the repository.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @param dstPath The path of the destination folder.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a folder in the destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the parent folder or the
	 *         destination folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void move(String token, String fldPath, String dstPath) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Copy a folder to another location in the repository.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @param dstPath The path of the destination folder.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a folder in the destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the parent folder or the
	 *         destination folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void copy(String token, String fldPath, String dstPath) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, IOException, AutomationException, DatabaseException,
			UserQuotaExceededException;

	/**
	 * Copy a folder to another location in the repository.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @param dstPath The path of the destination folder.
	 * @param extAttr Attributes to define what need to be duplicated.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a folder in the destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: you can't modify the parent folder or the
	 *         destination folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void extendedCopy(String token, String fldPath, String dstPath, ExtendedAttributes extAttr) throws
			PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException, IOException,
			AutomationException, DatabaseException, UserQuotaExceededException;

	/**
	 * Retrieve a list of child folders from an existing one.
	 *
	 * @param fldId The path that identifies an unique folder or its UUID.
	 * @return A Collection with the child folders.
	 * @throws PathNotFoundException If there is no folder in the repository in this path
	 * @throws RepositoryException If there is any general repository problem.
	 */
	@Deprecated
	public List<Folder> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Retrieve a list of children folders from an existing one.
	 *
	 * @param fldId The path that identifies an unique folder or its UUID.
	 * @return A Collection with the child folders.
	 * @throws PathNotFoundException If there is no folder in the repository in this path
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<Folder> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Retrive the content info of the folder: number of folders, number of documents, and total size.
	 *
	 * @param fldPath The path that identifies an unique folder.
	 * @return A ContentInfo with the number of folder, number of documents and total size.
	 * @throws AccessDeniedException If there is any security problem: you can't access this folder because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no folder in the repository with this path.
	 */
	public ContentInfo getContentInfo(String token, String fldPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException;

	/**
	 * Test if a folder path is valid.
	 *
	 * @param fldId The path that identifies an unique folder or its UUID.
	 * @return True if is a valid folder path, otherwise false.
	 * @throws PathNotFoundException If the node does not exists.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public boolean isValid(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Get the folder path from a UUID
	 *
	 * @param uuid The unique folder id.
	 * @return The folder path
	 * @throws AccessDeniedException If there is any security problem: you can't access this folder because of lack of
	 *         permissions.
	 * @throws RepositoryException If there is any problem.
	 */
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException;
}
