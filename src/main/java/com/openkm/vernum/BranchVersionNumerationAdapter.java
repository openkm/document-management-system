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

package com.openkm.vernum;

import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author pavila
 */
public class BranchVersionNumerationAdapter implements VersionNumerationAdapter {

	@Override
	public String getInitialVersionNumber() {
		return "1";
	}

	@Override
	public String getNextVersionNumber(Session session, NodeDocument nDoc, NodeDocumentVersion nDocVer, int increment) {
		String curVersionName = nDocVer.getName();
		String nextVersionName = curVersionName;
		Query q = session.createQuery(qs);
		NodeDocumentVersion ndv = null;

		do {
			nextVersionName = buildVersionName(nextVersionName);
			q.setString("parent", nDoc.getUuid());
			q.setString("name", nextVersionName);
			ndv = (NodeDocumentVersion) q.setMaxResults(1).uniqueResult();

			if (ndv != null) {
				nextVersionName = curVersionName + ".0";
			}
		} while (ndv != null);

		return nextVersionName;
	}

	/**
	 * Build version name
	 */
	private String buildVersionName(String versionNumber) {
		String ver[] = versionNumber.split("\\.");
		StringBuilder sb = new StringBuilder();

		if (ver.length > 1) {
			int verNum = Integer.parseInt(ver[ver.length - 1]);

			for (int i = 0; i < ver.length - 1; i++) {
				sb.append(ver[i]).append(".");
			}

			return sb.toString() + ++verNum;
		} else {
			int verNum = Integer.parseInt(versionNumber);
			return Integer.toString(++verNum);
		}
	}
}
