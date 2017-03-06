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

import com.openkm.bean.StatsInfo;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;

public interface StatsModule {

	/**
	 * Get number of documents per context
	 */
	public StatsInfo getDocumentsByContext(String token) throws RepositoryException, DatabaseException;

	/**
	 * Get number of folders per context
	 */
	public StatsInfo getFoldersByContext(String token) throws RepositoryException, DatabaseException;

	/**
	 * Get number of mails per context
	 */
	public StatsInfo getMailsByContext(String token) throws RepositoryException, DatabaseException;

	/**
	 * Get size of documents per context
	 */
	public StatsInfo getDocumentsSizeByContext(String token) throws RepositoryException, DatabaseException;
}
