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

import java.util.List;
import java.util.Map;

public interface AuthModule {

	/**
	 * Logins into the repository.
	 */
	public void login() throws RepositoryException, DatabaseException;

	public String login(String user, String password) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Log out from the repository.
	 */
	public void logout(String token) throws RepositoryException, DatabaseException;

	/**
	 * Add user permissions to a node.
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @param user User name which permissions are changed.
	 * @param permissions A mask with the permissions to be added.
	 * @param recursive recursive – If the nodePath indicated a folder,
	 *        the permissions can be applied recursively.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void grantUser(String token, String nodeId, String user, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Revoke user permissions from a node.
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @param user User name which permissions are changed.
	 * @param permissions A mask with the permissions to be removed.
	 * @param recursive If the nodePath indicates a folder, the
	 *        permissions can be revoked recursively.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void revokeUser(String token, String nodeId, String user, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get user permissions from am item (document or folder).
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @return A hashmap with pairs of user / permissions.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public Map<String, Integer> getGrantedUsers(String token, String nodeId) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Grant role permissions for a node.
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @param role Role name which permissions are changed.
	 * @param permissions A mask with the permissions to be added.
	 * @param recursive If the nodePath indicates a folder, the permissions can
	 *        be applied recursively.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void grantRole(String token, String nodeId, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Revoke role permissions from a node.
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @param role Role name which permissions are changed.
	 * @param permissions A mask with the permissions to be removed.
	 * @param recursive If the nodePath indicates a folder, the
	 *        permissions can be applied recursively.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void revokeRole(String token, String nodeId, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get roles permissions from am item (document or folder).
	 *
	 * @param nodeId The complete path to the node or its UUID.
	 * @return A hashmap with pairs of role / permissions.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public Map<String, Integer> getGrantedRoles(String token, String nodeId) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Retrieves a list of repository users
	 *
	 * @return A collection of repository users.
	 * @throws RepositoryException If there is any error retrieving the users list.
	 */
	public List<String> getUsers(String token) throws PrincipalAdapterException;

	/**
	 * Retrieves a list of repository roles.
	 *
	 * @return A collection of repository roles.
	 * @throws RepositoryException If there is any error retrieving the roles list.
	 */
	public List<String> getRoles(String token) throws PrincipalAdapterException;

	/**
	 * Retrieves a list of users by role.
	 *
	 * @return A repository role.
	 * @throws RepositoryException If there is any error retrieving the user list.
	 */
	public List<String> getUsersByRole(String token, String role) throws PrincipalAdapterException;

	/**
	 * Retrieves a list of roles by user.
	 *
	 * @return A repository user.
	 * @throws RepositoryException If there is any error retrieving the roles list.
	 */
	public List<String> getRolesByUser(String token, String user) throws PrincipalAdapterException;

	/**
	 * Retrieves the mail from an user.
	 *
	 * @param user The user id.
	 * @return A email of the user.
	 * @throws RepositoryException If there is any error retrieving the mail.
	 */
	public String getMail(String token, String user) throws PrincipalAdapterException;

	/**
	 * Retrieves the name from an user.
	 *
	 * @param user The user id.
	 * @return A name of the user.
	 * @throws RepositoryException If there is any error retrieving the mail.
	 */
	public String getName(String token, String user) throws PrincipalAdapterException;

	/**
	 * Change several security permissions in the same call.
	 */
	public void changeSecurity(String token, String nodePath, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;
	
	/*
	 * ------------------------------------------------------------------
	 * These methods only works if using the OpenKM user database.
	 * ------------------------------------------------------------------
	 */

	/**
	 * Method to create a new user
	 *
	 * @param user A user id.
	 * @param password The password of the user.
	 * @param email The user mail.
	 * @param name The full user name.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void createUser(String token, String user, String password, String email, String name, boolean active)
			throws PrincipalAdapterException;

	/**
	 * Method to create a delete a user
	 *
	 * @param user A user id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void deleteUser(String token, String user) throws PrincipalAdapterException;

	/**
	 * Update user information
	 *
	 * @param user A user id.
	 * @param password The password of the user.
	 * @param email The user mail.
	 * @param name The full user name.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void updateUser(String token, String user, String password, String email, String name, boolean active) throws PrincipalAdapterException;

	/**
	 * Method to create a new role
	 *
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void createRole(String token, String role, boolean active) throws PrincipalAdapterException;

	/**
	 * Method to create a delete a role
	 *
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void deleteRole(String token, String role) throws PrincipalAdapterException;

	/**
	 * Update role information
	 *
	 * @param role A role id..
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void updateRole(String token, String role, boolean active) throws PrincipalAdapterException;

	/**
	 * Method to assign a role
	 *
	 * @param user A user id.
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void assignRole(String token, String user, String role) throws PrincipalAdapterException;

	/**
	 * Method to remove a role
	 *
	 * @param user A user id.
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void removeRole(String token, String user, String role) throws PrincipalAdapterException;
}
