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

import com.openkm.bean.AppVersion;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.core.*;

public interface RepositoryModule {

	/**
	 * Obtain the root folder of the repository.
	 *
	 * @return A folder object with the repository root node properties.
	 * @throws PathNotFoundException If there is no root folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getRootFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Obtains the user trash folder.
	 *
	 * @return A folder object with the user trash node properties.
	 * @throws PathNotFoundException If there is no user trash folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getTrashFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	public Folder getTrashFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Obtain the template folder of the repository.
	 *
	 * @return A folder object with the templates node properties.
	 * @throws PathNotFoundException If there is no templates folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getTemplatesFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Obtain the personal documents folder of the repository.
	 *
	 * @return A folder object with the user documents folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getPersonalFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	public Folder getPersonalFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Obtain the personal mails folder of the repository.
	 *
	 * @return A folder object with the user mails folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getMailFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	public Folder getMailFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Obtain the thesaurus folder of the repository.
	 *
	 * @return A folder object with the thesaurus folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getThesaurusFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Obtain the categories folder of the repository.
	 *
	 * @return A folder object with the categories folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getCategoriesFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Remove all the items in the user trash folder for ever. You can't
	 * recover this items any more.
	 *
	 * @throws AccessDeniedException If there is any security problem:
	 *         you can't modify the user deleted folders and documents because
	 *         of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void purgeTrash(String token) throws PathNotFoundException, AccessDeniedException, LockException, RepositoryException,
			DatabaseException;

	/**
	 * Get the update message, if any.
	 *
	 * @return A possible update message or simple info for the application.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getUpdateMessage(String token) throws RepositoryException;

	/**
	 * Get the unique repository identifier
	 *
	 * @return The repository UUID
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getRepositoryUuid(String token) throws RepositoryException;

	/**
	 * Test if a node path exists
	 *
	 * @param nodeId The path that identifies an unique document or its UUID.
	 * @return true if the node exist or false if not
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public boolean hasNode(String token, String nodeId) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Obtain the node path with a given uuid.
	 *
	 * @param uuid An unique node identifier
	 * @return The path of the node with the given uuid
	 * @throws PathNotFoundException If there is no user node in the repository with this uuid.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getNodePath(String token, String uuid) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Obtain the node uuid with a given path.
	 *
	 * @param path An unique path node identifier
	 * @return The path of the node with the given uuid
	 * @throws PathNotFoundException If there is no user node in the repository with this uuid.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getNodeUuid(String token, String path) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Retrieve application version.
	 *
	 * @param token Security token.
	 * @return Application version.
	 */
	public AppVersion getAppVersion(String token) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Copy attributes from a node to another.
	 *
	 * @param token Security token.
	 * @param srcId The path that identifies an unique document or its UUID.
	 * @param dstId The path that identifies an unique document or its UUID.
	 * @param extAttr Which attributes need to be copied.
	 */
	void copyAttributes(String token, String srcId, String dstId, ExtendedAttributes extAttr) throws AccessDeniedException,
			PathNotFoundException, DatabaseException;
}
