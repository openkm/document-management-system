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

package com.openkm.module.db.stuff;

import com.openkm.bean.Permission;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeBase;
import com.openkm.principal.PrincipalAdapterException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class SecurityHelper {
	private static Logger log = LoggerFactory.getLogger(SecurityHelper.class);
	private static DbAccessManager accessManager = null;

	static {
		if (DbSimpleAccessManager.NAME.equals(Config.SECURITY_ACCESS_MANAGER)) {
			log.info("Configuring AccessManager with {}", DbSimpleAccessManager.class.getCanonicalName());
			accessManager = new DbSimpleAccessManager();
		} else if (DbRecursiveAccessManager.NAME.equals(Config.SECURITY_ACCESS_MANAGER)) {
			log.info("Configuring AccessManager with {}", DbRecursiveAccessManager.class.getCanonicalName());
			accessManager = new DbRecursiveAccessManager();
		} else if (DbReadRecursiveAccessManager.NAME.equals(Config.SECURITY_ACCESS_MANAGER)) {
			log.info("Configuring AccessManager with {}", DbReadRecursiveAccessManager.class.getCanonicalName());
			accessManager = new DbReadRecursiveAccessManager();
		}
	}

	/**
	 * Return current access manager
	 */
	public static DbAccessManager getAccessManager() {
		return accessManager;
	}

	/**
	 * Prune not accessible nodes
	 */
	public static void pruneNodeList(List<? extends NodeBase> nodeList) throws DatabaseException {
		for (Iterator<? extends NodeBase> it = nodeList.iterator(); it.hasNext(); ) {
			NodeBase node = it.next();

			if (!accessManager.isGranted(node, Permission.READ)) {
				it.remove();
			}
		}
	}

	/**
	 * Check for node permissions
	 */
	public static boolean isGranted(NodeBase node, int permission) throws DatabaseException {
		return accessManager.isGranted(node, permission);
	}

	/**
	 * Check for node permissions
	 */
	public static boolean isGranted(NodeBase node, String user, int permission) throws PrincipalAdapterException, DatabaseException {
		return accessManager.isGranted(node, user, permission);
	}

	/**
	 * Check for node read access
	 */
	public static void checkRead(NodeBase node) throws PathNotFoundException, DatabaseException {
		log.debug("checkRead({})", node);

		if (!accessManager.isGranted(node, Permission.READ)) {
			String path = NodeBaseDAO.getInstance().getPathFromUuid(node.getUuid());
			throw new PathNotFoundException(node.getUuid() + " : " + path);
		}
	}

	/**
	 * Check for node write
	 */
	public static void checkWrite(NodeBase node) throws AccessDeniedException, PathNotFoundException, DatabaseException {
		log.debug("checkWrite({})", node);

		if (!accessManager.isGranted(node, Permission.WRITE)) {
			String path = NodeBaseDAO.getInstance().getPathFromUuid(node.getUuid());
			throw new AccessDeniedException(node.getUuid() + " : " + path);
		}
	}

	/**
	 * Check for node delete
	 */
	public static void checkDelete(NodeBase node) throws AccessDeniedException, PathNotFoundException, DatabaseException {
		log.debug("checkDelete({})", node);

		if (!accessManager.isGranted(node, Permission.DELETE)) {
			String path = NodeBaseDAO.getInstance().getPathFromUuid(node.getUuid());
			throw new AccessDeniedException(node.getUuid() + " : " + path);
		}
	}

	/**
	 * Check for node security
	 */
	public static void checkSecurity(NodeBase node) throws AccessDeniedException, PathNotFoundException, DatabaseException {
		log.debug("checkSecurity({})", node);

		if (!accessManager.isGranted(node, Permission.SECURITY)) {
			String path = NodeBaseDAO.getInstance().getPathFromUuid(node.getUuid());
			throw new AccessDeniedException(node.getUuid() + " : " + path);
		}
	}

	/**
	 * Check for extended node security
	 */
	public static void checkExtended(NodeBase node, int permission) throws AccessDeniedException, DatabaseException {
		log.debug("checkExtended({}, {})", node, permission);

		if (!accessManager.isGranted(node, permission)) {
			String path = NodeBaseDAO.getInstance().getParentUuid(node.getUuid());
			throw new AccessDeniedException(node.getUuid() + " : " + path);
		}
	}

	/**
	 * Check for node security
	 */
	public static void checkExtended(String uuid, int permission) throws AccessDeniedException, DatabaseException {
		log.debug("checkExtended({}, {})", uuid, permission);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);

			if (!accessManager.isGranted(node, permission)) {
				String path = NodeBaseDAO.getInstance().getParentUuid(node.getUuid());
				throw new AccessDeniedException(node.getUuid() + " : " + path);
			}
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
