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

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;

public interface ScriptingModule {

	/**
	 * Add script to node.
	 *
	 * @param nodePath The complete path to the node.
	 * @param code Script BeanShell code.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void setScript(String token, String nodePath, String code) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Remove script from node.
	 *
	 * @param nodePath The complete path to the node.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void removeScript(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get node script (document or folder).
	 *
	 * @param nodePath The complete path to the node.
	 * @return A hashmap with pairs of user / permissions.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public String getScript(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;
}
