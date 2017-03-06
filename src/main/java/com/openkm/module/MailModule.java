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
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Mail;
import com.openkm.core.*;

import java.io.IOException;
import java.util.List;

public interface MailModule {

	/**
	 * Create a new mail in the repository.
	 *
	 * @param mail A mail object with the new mail properties.
	 * @return A mail object with the new created mail properties.
	 * @throws PathNotFoundException If the parent mail doesn't exist.
	 * @throws ItemExistsException If there is already a mail in the
	 * repository with the same name in the same path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the parent mail because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Mail create(String token, Mail mail) throws PathNotFoundException, ItemExistsException,
			VirusDetectedException, AccessDeniedException, RepositoryException, DatabaseException,
			UserQuotaExceededException, AutomationException;

	/**
	 * Obtains properties from a previously created mail.
	 *
	 * @param mailId The path that identifies an unique mail, or its UUID. 
	 * @return A mail object with the selected mail properties.
	 * @throws PathNotFoundException If the indicated mail doesn't exist.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Mail getProperties(String token, String mailId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException;

	/**
	 * Delete a mail the repository. It is a logical delete,
	 * so really is moved to the user trash and can be restored.
	 *
	 * @param mailPath The path that identifies an unique mail.  
	 * @throws LockException Can't delete a mail with locked documents.
	 * @throws PathNotFoundException If there is no mail in the repository in this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the mail because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void delete(String token, String mailPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Deletes definitively a mail from the repository. It is a physical delete, so
	 * the mail can't be restored.
	 *
	 * @param mailPath The path that identifies an unique mail.  
	 * @throws LockException Can't delete a mail with locked documents.
	 * @throws PathNotFoundException If there is no mail in the repository in this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the mail because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void purge(String token, String mailPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Rename a mail in the repository.
	 *
	 * @param mailPath The path that identifies an unique mail.  
	 * @param newName The new mail name.
	 * @return A mail object with the new mail properties.
	 * @throws PathNotFoundException If there is no mail in the repository in this path.
	 * @throws ItemExistsException If there is already a mail in the
	 * repository with the same name in the same path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the mail because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Mail rename(String token, String mailPath, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Move a mail to another location in the repository.
	 *
	 * @param mailPath The path that identifies an unique mail.
	 * @param dstPath The path of the destination mail.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a mail in the
	 * destination mail with the same name.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the parent mail or the destination mail
	 * because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void move(String token, String mailPath, String dstPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Copy a mail to another location in the repository.
	 *
	 * @param mailPath The path that identifies an unique mail.
	 * @param dstPath The path of the destination mail.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a mail in the
	 * destination mail with the same name.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the parent mail or the destination mail
	 * because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void copy(String token, String mailPath, String dstPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, AutomationException,
			DatabaseException, UserQuotaExceededException;

	/**
	 * Copy a mail to another location in the repository.
	 *
	 * @param mailPath The path that identifies an unique mail.
	 * @param dstPath The path of the destination mail.
	 * @param extAttr Attributes to define what need to be duplicated.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a mail in the
	 * destination mail with the same name.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the parent mail or the destination mail
	 * because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void extendedCopy(String token, String mailPath, String dstPath, ExtendedAttributes extAttr) throws
			PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException, IOException,
			AutomationException, DatabaseException, UserQuotaExceededException;

	/**
	 * Retrieve a list of child mails from an existing folder.
	 *
	 * @param fldId The path that identifies an unique folder or its UUID.
	 * @return A Collection with the child folders.
	 * @throws PathNotFoundException If there is no folder in the repository in this path
	 * @throws RepositoryException If there is any general repository problem.
	 */
	@Deprecated
	public List<Mail> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException;

	/**
	 * Retrieve a list of children mails from an existing folder.
	 *
	 * @param fldId The path that identifies an unique folder or its UUID.
	 * @return A Collection with the child folders.
	 * @throws PathNotFoundException If there is no folder in the repository in this path
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<Mail> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException;

	/**
	 * Test if a mail path is valid.
	 *
	 * @param mailId The path that identifies an unique mail or its UUID.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this mail because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no mail in the repository with this path.
	 */
	public boolean isValid(String token, String mailId) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get the mail path from a UUID
	 *
	 * @param uuid The unique mail id.
	 * @return The mail path
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this folder because of lack of permissions.
	 * @throws RepositoryException If there is any problem.
	 */
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException;
}
