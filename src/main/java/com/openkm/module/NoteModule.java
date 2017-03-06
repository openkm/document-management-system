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

import com.openkm.bean.Note;
import com.openkm.core.*;

import java.util.List;

public interface NoteModule {
	/**
	 * Add a note to a document
	 *
	 * @param token The session authorization token.
	 * @param nodePath The path that identifies an unique document.
	 * @param text The message text
	 * @return A note object with the new created note properties.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in the repository with this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this document because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Note add(String token, String nodePath, String text) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Remove a note from a document
	 *
	 * @param token The session authorization token.
	 * @param notePath The path that identifies an unique document note.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in the repository with this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this document because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void delete(String token, String notePath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get note from document
	 *
	 * @param token The session authorization token.
	 * @param notePath The path that identifies an unique document note.
	 * @return The required note associated to the document.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in the repository with this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this document because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Note get(String token, String notePath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Set a new text to document note.
	 *
	 * @param token The session authorization token.
	 * @param notePath The path that identifies an unique document note.
	 * @param text The message text
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no document in the repository with this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this document because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String set(String token, String notePath, String text) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Retrieve a list of notes from a document.
	 *
	 * @param token The session authorization token.
	 * @param nodePath The path that identifies an unique document.
	 * @return A Collection with the document notes.
	 * @throws PathNotFoundException If there is no document in this repository path.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<Note> list(String token, String nodePath) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException;
}
