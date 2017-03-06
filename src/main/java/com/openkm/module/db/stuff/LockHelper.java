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

package com.openkm.module.db.stuff;

import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.spring.PrincipalUtils;

public class LockHelper {
	/**
	 * Check for lock
	 */
	public static void checkWriteLock(NodeDocument nDoc) throws LockException, DatabaseException {
		String user = PrincipalUtils.getUser();
		checkWriteLock(user, nDoc);
	}

	/**
	 * Check for lock. Needed by Zoho extension.
	 */
	public static void checkWriteLock(String user, NodeDocument nDoc) throws LockException, DatabaseException {
		if (nDoc.isLocked() && !user.equals(nDoc.getLock().getOwner())) {
			String docPath = NodeBaseDAO.getInstance().getParentUuid(nDoc.getUuid());
			throw new LockException(nDoc.getUuid() + " : " + docPath);
		}
	}
}
