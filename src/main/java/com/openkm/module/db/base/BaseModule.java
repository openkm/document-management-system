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

package com.openkm.module.db.base;

import com.openkm.bean.LockInfo;
import com.openkm.bean.Node;
import com.openkm.bean.Permission;
import com.openkm.bean.Version;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeBase;
import com.openkm.dao.bean.NodeDocumentVersion;
import com.openkm.dao.bean.NodeLock;
import com.openkm.module.db.stuff.DbAccessManager;
import com.openkm.module.db.stuff.SecurityHelper;
import com.openkm.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseModule {
	private static Logger log = LoggerFactory.getLogger(BaseModule.class);

	/**
	 * Get properties
	 */
	public static Version getProperties(NodeDocumentVersion nDocVersion) throws DatabaseException {
		log.debug("getProperties({})", nDocVersion);
		Version ver = new Version();

		// Properties
		ver.setAuthor(nDocVersion.getAuthor());
		ver.setSize(nDocVersion.getSize());
		ver.setComment(nDocVersion.getComment());
		ver.setName(nDocVersion.getName());
		ver.setCreated(nDocVersion.getCreated());
		ver.setChecksum(nDocVersion.getChecksum());
		ver.setActual(nDocVersion.isCurrent());

		log.debug("getProperties: {}", ver);
		return ver;
	}

	/**
	 * Get properties
	 */
	public static LockInfo getProperties(NodeLock nLock, String nPath) throws DatabaseException {
		log.debug("getProperties({})", nLock);
		LockInfo lck = new LockInfo();

		// Properties
		lck.setToken(nLock.getToken());
		lck.setOwner(nLock.getOwner());
		lck.setNodePath(nPath);

		log.debug("getProperties: {}", lck);
		return lck;
	}

	/**
	 * Get permissions
	 */
	public static void setPermissions(final NodeBase nBase, Node node) throws DatabaseException {
		if (Config.SYSTEM_READONLY) {
			node.setPermissions(Permission.NONE);
		} else {
			DbAccessManager am = SecurityHelper.getAccessManager();

			if (am.isGranted(nBase, Permission.READ)) {
				node.setPermissions(Permission.READ);
			}

			if (am.isGranted(nBase, Permission.WRITE)) {
				node.setPermissions(node.getPermissions() | Permission.WRITE);
			}

			if (am.isGranted(nBase, Permission.DELETE)) {
				node.setPermissions(node.getPermissions() | Permission.DELETE);
			}

			if (am.isGranted(nBase, Permission.SECURITY)) {
				node.setPermissions(node.getPermissions() | Permission.SECURITY);
			}

			// Extended security
			if (am.isGranted(nBase, Config.SECURITY_EXTENDED_MASK)) {
				node.setPermissions(node.getPermissions() | Config.SECURITY_EXTENDED_MASK);
			}
		}
	}

	/**
	 * Get node from path or UUID.
	 */
	public static NodeBase resolveNodeById(String nodeId) throws PathNotFoundException, DatabaseException {
		NodeBase node = null;

		if (PathUtils.isPath(nodeId)) {
			String nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			node = NodeBaseDAO.getInstance().findByPk(nodeUuid);
			node.setPath(nodeId);
		} else {
			node = NodeBaseDAO.getInstance().findByPk(nodeId);
			String nodePath = NodeBaseDAO.getInstance().getPathFromUuid(node.getUuid());
			node.setPath(nodePath);
		}

		return node;
	}
}
