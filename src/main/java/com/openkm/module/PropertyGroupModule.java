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
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.form.suggestion.SuggestionException;

import java.io.IOException;
import java.util.List;

public interface PropertyGroupModule {

	/**
	 * Add a property group to a document.
	 *
	 * @param token The session authorization token.
	 * @param nodeId The path that identifies an unique node.
	 * @param grpName The group name previously registered in the system.
	 * @throws NoSuchGroupException If there is no such registered group name.
	 * @throws LockException Can't modify a locked document.
	 * @throws PathNotFoundException If there is no document in this
	 *         repository path.
	 * @throws AccessDeniedException If there is any security problem:
	 *         you can't modify the document because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void addGroup(String token, String nodeId, String grpName) throws NoSuchGroupException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, ExtensionException, AutomationException;

	/**
	 * Removes a property group from a document.
	 *
	 * @param token The session authorization token.
	 * @param nodeId The path that identifies an unique node.
	 * @param grpName The group name previously registered in the system.
	 * @throws NoSuchGroupException If there is no such registered group name.
	 * @throws LockException Can't modify a locked document.
	 * @throws PathNotFoundException If there is no document in this
	 *         repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void removeGroup(String token, String nodeId, String grpName) throws AccessDeniedException, NoSuchGroupException,
			LockException, PathNotFoundException, RepositoryException, DatabaseException, ExtensionException, AutomationException;

	/**
	 * Get groups assigned to a document.
	 *
	 * @param token The session authorization token.
	 * @param nodeId The path that identifies an unique node.
	 * @return A list with all groups assigned to the document.
	 * @throws PathNotFoundException If there is no document in this
	 *         repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<PropertyGroup> getGroups(String token, String nodeId) throws IOException, ParseException, AccessDeniedException,
			PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Get all groups defined in the system.
	 *
	 * @param token The session authorization token.
	 * @return A list with all groups registered in the system.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<PropertyGroup> getAllGroups(String token) throws IOException, ParseException, AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get all properties defined in a document by group.
	 *
	 * @param token The session authorization token.
	 * @param nodeId The path that identifies an unique node.
	 * @param grpName The group name previously registered in the system.
	 * @return A list with all properties defined in the group and its actual values.
	 * @throws NoSuchGroupException If there is no such registered group name.
	 * @throws PathNotFoundException If there is no document in this
	 *         repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<FormElement> getProperties(String token, String nodeId, String grpName) throws IOException, ParseException,
			NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Set group properties to a document.
	 *
	 * @param token The session authorization token.
	 * @param nodeId The path that identifies an unique node or its UUID.
	 * @param grpName The group name previously registered in the system.
	 * @param properties The property values.
	 * @throws NoSuchPropertyException If there is no such registered category property.
	 * @throws NoSuchGroupException If there is no such registered group name.
	 * @throws LockException Can't modify a locked document.
	 * @throws PathNotFoundException If there is no document in this
	 *         repository path.
	 * @throws AccessDeniedException If there is any security problem:
	 *         you can't modify the document because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void setProperties(String token, String nodeId, String grpName, List<FormElement> properties) throws IOException,
			ParseException, NoSuchPropertyException, NoSuchGroupException, LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException;

	/**
	 * Get all possible values which can have a property.
	 *
	 * @param token The session authorization token.
	 * @param grpName The group name previously registered in the system.
	 * @throws IOException If there is any problem reading the property values.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<FormElement> getPropertyGroupForm(String token, String grpName) throws ParseException, IOException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Test if a node has a property group assigned
	 *
	 * @param token The session authorization token.
	 * @param nodeId The path that identifies an unique node or its UUID.
	 * @param grpName The group name previously registered in the system.
	 * @return True if the document has the given property group assigned, and false otherwise.
	 * @throws NoSuchGroupException If there is no such registered group name.
	 * @throws PathNotFoundException If there is no document in this
	 *         repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public boolean hasGroup(String token, String nodeId, String grpName) throws IOException, ParseException, AccessDeniedException,
			PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Obtain suggestion from a property in a document
	 *
	 * @param token The session authorization token.
	 * @param nodeId The path that identifies an unique node or its UUID.
	 * @param grpName The group name previously registered in the system.
	 * @param propName The group name previously registered in the system.
	 */
	public List<String> getSuggestions(String token, String nodeId, String grpName, String propName) throws AccessDeniedException,
			PathNotFoundException, IOException, ParseException, NoSuchGroupException, SuggestionException, DatabaseException;

	/**
	 * Register Property Group definition.
	 *
	 * @param token The session authorization token.
	 * @param pgDef The XML with the Property Group Definition
	 */
	public void registerDefinition(String token, String pgDef) throws ParseException, AccessDeniedException, DatabaseException, IOException;
}
