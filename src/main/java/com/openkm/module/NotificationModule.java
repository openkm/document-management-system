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

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.principal.PrincipalAdapterException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface NotificationModule {

	/**
	 * Add user subscription to a node.
	 *
	 * @param nodePath The complete path to the node.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void subscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Remove an user subscription from a node.
	 *
	 * @param nodePath The complete path to the node.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void unsubscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get user subscriptions from am item (document or folder).
	 *
	 * @param nodePath The complete path to the node.
	 * @return A Collection of subscribed users.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public Set<String> getSubscriptors(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Send a notification message to an user list.
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @param users Array of users to notify.
	 * @param mails Array of external mails to notify.
	 * @param message An String with the notification message.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void notify(String token, String nodeId, List<String> users, List<String> mails, String message, boolean attachment)
			throws PathNotFoundException, AccessDeniedException, PrincipalAdapterException, RepositoryException,
			DatabaseException, IOException;

	/**
	 *  Send a proposedSubscription comment to an user list.
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @param users Array of users to proposedSubscription.
	 * @param comment An String with the notification message.	 * 
	 * @throws PathNotFoundException
	 * @throws AccessDeniedException
	 * @throws PrincipalAdapterException
	 * @throws RepositoryException
	 * @throws DatabaseException
	 * @throws IOException
	 */
	public void proposedSubscription(String token, String nodeId, List<String> users, String comment)
			throws PathNotFoundException, AccessDeniedException, PrincipalAdapterException, RepositoryException,
			DatabaseException, IOException;
}
