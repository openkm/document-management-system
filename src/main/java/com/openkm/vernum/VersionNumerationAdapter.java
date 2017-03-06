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

package com.openkm.vernum;

import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import org.hibernate.Session;

/**
 * @author pavila
 * @see PlainVersionNumerationAdapter
 * @see BranchVersionNumerationAdapter
 * @see MajorMinorVersionNumerationAdapter
 * @see MajorMinorReleaseVersionNumerationAdapter
 */
public interface VersionNumerationAdapter {
	final String qs = "from NodeDocumentVersion ndv where ndv.parent=:parent and ndv.name=:name";

	/**
	 * Obtain the initial version number to be set when creating a new document
	 *
	 * @return This first version number.
	 */
	public String getInitialVersionNumber();

	/**
	 * Calculate the next version number from a given one.
	 *
	 * @param session Hibernate session.
	 * @param nDoc Document which will increase the revision number.
	 * @param nDocVer Current document version node.
	 * @return The new calculated version numbering.
	 */
	public String getNextVersionNumber(Session session, NodeDocument nDoc, NodeDocumentVersion nDocVer, int increment);
}
