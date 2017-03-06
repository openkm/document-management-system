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

package com.openkm.principal;

import java.util.List;

public interface PrincipalAdapter {

	/**
	 * Method to retrieve all users from a authentication source.
	 *
	 * @return A Collection with all the users.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getUsers() throws PrincipalAdapterException;

	/**
	 * Method to retrieve all roles from a authentication source.
	 *
	 * @return A Collection with all the roles.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getRoles() throws PrincipalAdapterException;

	/**
	 * Method to retrieve all users from a role.
	 *
	 * @return A Collection with all the users within a role.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getUsersByRole(String role) throws PrincipalAdapterException;

	/**
	 * Method to retrieve all roles from a user.
	 *
	 * @return A Collection with all the roles of the user.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getRolesByUser(String user) throws PrincipalAdapterException;

	/**
	 * Method to retrieve the mail from a user.
	 *
	 * @param user A user id.
	 * @return The email of the user.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public String getMail(String user) throws PrincipalAdapterException;

	/**
	 * Method to retrieve the name from a user.
	 *
	 * @param user A user id.
	 * @return The name of the user.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public String getName(String user) throws PrincipalAdapterException;

	/**
	 * Method to retrieve the user password
	 *
	 * @param user A user id.
	 * @return The password of the user.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public String getPassword(String user) throws PrincipalAdapterException;
	
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
	public void createUser(String user, String password, String email, String name, boolean active) throws PrincipalAdapterException;

	/**
	 * Method to create a delete a user
	 *
	 * @param user A user id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void deleteUser(String user) throws PrincipalAdapterException;

	/**
	 * Update user information
	 *
	 * @param user A user id.
	 * @param password The password of the user.
	 * @param email The user mail.
	 * @param name The full user name.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void updateUser(String user, String password, String email, String name, boolean active) throws PrincipalAdapterException;

	/**
	 * Method to create a new role
	 *
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void createRole(String role, boolean active) throws PrincipalAdapterException;

	/**
	 * Method to create a delete a role
	 *
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void deleteRole(String role) throws PrincipalAdapterException;

	/**
	 * Update role information
	 *
	 * @param role A role id..
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void updateRole(String role, boolean active) throws PrincipalAdapterException;

	/**
	 * Method to assign a role
	 *
	 * @param user A user id.
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void assignRole(String user, String role) throws PrincipalAdapterException;

	/**
	 * Method to remove a role
	 *
	 * @param user A user id.
	 * @param role A role id.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public void removeRole(String user, String role) throws PrincipalAdapterException;
}
