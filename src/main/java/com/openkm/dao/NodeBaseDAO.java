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

package com.openkm.dao;

import com.openkm.bean.*;
import com.openkm.core.*;
import com.openkm.core.Config;
import com.openkm.dao.bean.*;
import com.openkm.extension.dao.WikiPageDAO;
import com.openkm.extension.dao.bean.WikiPage;
import com.openkm.module.db.base.BaseNoteModule;
import com.openkm.module.db.stuff.SecurityHelper;
import com.openkm.util.CloneUtils;
import com.openkm.util.FormatUtil;
import com.openkm.util.PathUtils;
import com.openkm.util.SystemProfiling;
import org.hibernate.*;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class NodeBaseDAO {
	private static Logger log = LoggerFactory.getLogger(NodeBaseDAO.class);
	private static NodeBaseDAO single = new NodeBaseDAO();
	private static final String CACHE_PARENT_NODE_PERMISSIONS = "com.openkm.cache.parentNodePermissions";

	private NodeBaseDAO() {
	}

	public static NodeBaseDAO getInstance() {
		return single;
	}

	/**
	 * Find by pk
	 */
	public NodeBase findByPk(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByPk({})", uuid);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			NodeBase nBase = (NodeBase) session.get(NodeBase.class, uuid);

			if (nBase == null) {
				throw new PathNotFoundException(uuid);
			}

			// Security Check
			SecurityHelper.checkRead(nBase);

			initialize(nBase);
			log.debug("findByPk: {}", nBase);
			return nBase;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get node path from UUID
	 */
	public String getPathFromUuid(String uuid) throws PathNotFoundException, DatabaseException {
		return calculatePathFromUuid(uuid);
	}

	/**
	 * Get node path from UUID
	 */
	public String getPathFromUuid(Session session, String uuid) throws PathNotFoundException, HibernateException {
		return calculatePathFromUuid(session, uuid);
	}

	/**
	 * Get node UUID from path
	 */
	public String getUuidFromPath(String path) throws PathNotFoundException, DatabaseException {
		return calculateUuidFromPath(path);
	}

	/**
	 * Get node UUID from path
	 */
	public String getUuidFromPath(Session session, String path) throws PathNotFoundException, DatabaseException {
		return calculateUuidFromPath(session, path);
	}

	/**
	 * Check for item existence.
	 */
	public boolean itemPathExists(String path) throws DatabaseException {
		try {
			getUuidFromPath(path);
			return true;
		} catch (PathNotFoundException e) {
			return false;
		}
	}

	/**
	 * Check for item existence.
	 */
	public boolean itemUuidExists(String uuid) throws DatabaseException {
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			NodeBase nBase = (NodeBase) session.get(NodeBase.class, uuid);

			if (nBase == null) {
				return false;
			}

			// Security Check
			SecurityHelper.checkRead(nBase);

			return true;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			return false;
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get node path from UUID. This is the old one which calculates the path.
	 */
	private String calculatePathFromUuid(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("calculatePathFromUuid({})", uuid);
		Session session = null;

		if (Config.ROOT_NODE_UUID.equals(uuid)) {
			return "/";
		}

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String path = getPathFromUuid(session, uuid);
			log.debug("calculatePathFromUuid: {}", path);
			return path;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get node path from UUID. This is the old one which calculates the path.
	 */
	private String calculatePathFromUuid(Session session, String uuid) throws PathNotFoundException, HibernateException {
		log.debug("calculatePathFromUuid({}, {})", session, uuid);
		String childUuid = null;
		String path = "";

		do {
			NodeBase node = (NodeBase) session.get(NodeBase.class, uuid);

			if (node == null) {
				throw new PathNotFoundException(uuid);
			} else {
				path = "/".concat(node.getName()).concat(path);
				childUuid = uuid;
				uuid = node.getParent();

				if (uuid.equals(childUuid)) {
					log.warn("*** Node is its own parent: {} -> {} ***", uuid, path);
					break;
				}
			}
		} while (!Config.ROOT_NODE_UUID.equals(uuid));

		log.debug("calculatePathFromUuid: {}", path);
		return path;
	}

	/**
	 * Get node UUID from path. This is the old one which calculates the uuid.
	 */
	private String calculateUuidFromPath(String path) throws PathNotFoundException, DatabaseException {
		log.debug("calculateUuidFromPath({})", path);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String uuid = calculateUuidFromPath(session, path);
			log.debug("calculateUuidFromPath: {}", uuid);
			return uuid;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get node UUID from path. This is the old one which calculates the uuid.
	 */
	private String calculateUuidFromPath(Session session, String path) throws PathNotFoundException, HibernateException {
		log.debug("calculateUuidFromPath({}, {})", session, path);
		String qs = "select nb.uuid from NodeBase nb where nb.parent=:parent and nb.name=:name";
		Query q = session.createQuery(qs).setCacheable(true);
		String uuid = Config.ROOT_NODE_UUID;
		String name = "";

		// Fix for & and &amp; strings in the path
		path = PathUtils.encodeEntities(path);

		for (StringTokenizer st = new StringTokenizer(path, "/"); st.hasMoreTokens(); ) {
			name = st.nextToken();
			q.setString("name", name);
			q.setString("parent", uuid);
			uuid = (String) q.setMaxResults(1).uniqueResult();

			if (uuid == null) {
				throw new PathNotFoundException(path);
			}
		}

		log.debug("calculateUuidFromPath: {}", uuid);
		return uuid;
	}

	/**
	 * Get node path from UUID
	 */
	@SuppressWarnings("unused")
	private String searchPathFromUuid(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("searchPathFromUuid({})", uuid);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String path = getPathFromUuid(session, uuid);

			if (path == null) {
				throw new PathNotFoundException(uuid);
			}

			log.debug("searchPathFromUuid: {}", path);
			return path;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get node path from UUID
	 */
	@SuppressWarnings("unused")
	private String searchPathFromUuid(Session session, String uuid) throws PathNotFoundException, HibernateException {
		log.debug("searchPathFromUuid({})", uuid);
		String qs = "select nb.path from NodeBase nb where nb.uuid=:uuid";
		Query q = session.createQuery(qs);
		q.setString("uuid", uuid);
		String path = (String) q.setMaxResults(1).uniqueResult();
		log.debug("searchPathFromUuid: {}", path);
		return path;
	}

	/**
	 * Get node UUID from path. This is the old one which calculates the uuid.
	 */
	@SuppressWarnings("unused")
	private String searchUuidFromPath(String path) throws PathNotFoundException, DatabaseException {
		log.debug("searchUuidFromPath({})", path);
		String qs = "select nb.uuid from NodeBase nb where nb.path=:path";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("path", path);
			String uuid = (String) q.setMaxResults(1).uniqueResult();

			if (uuid == null) {
				throw new PathNotFoundException(path);
			}

			log.debug("searchUuidFromPath: {}", uuid);
			return uuid;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get user permissions
	 */
	public Map<String, Integer> getUserPermissions(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("getUserPermissions({})", uuid);
		Map<String, Integer> ret = new HashMap<String, Integer>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			if (node != null) {
				Hibernate.initialize(ret = node.getUserPermissions());
			}

			HibernateUtil.commit(tx);
			log.debug("getUserPermissions: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Grant user permissions
	 */
	public void grantUserPermissions(String uuid, String user, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		log.debug("grantUserPermissions({})", uuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Root node
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);

			if (recursive) {
				long begin = System.currentTimeMillis();
				int total = grantUserPermissionsInDepth(session, node, user, permissions);
				SystemProfiling.log(uuid + ", " + user + ", " + permissions + ", " + recursive, System.currentTimeMillis() - begin);
				log.trace("grantUserPermissions.Total: {}", total);
				log.info("grantUserPermissions.Time: {}", FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
			} else {
				grantUserPermissions(session, node, user, permissions, false);
			}

			HibernateUtil.commit(tx);
			log.debug("grantUserPermissions: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Grant user permissions
	 */
	private int grantUserPermissions(Session session, NodeBase node, String user, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, DatabaseException, HibernateException {
		// log.info("grantUserPermissions({})", node.getUuid());
		boolean canModify = true;

		// Security Check
		if (recursive) {
			canModify = SecurityHelper.isGranted(node, Permission.READ) && SecurityHelper.isGranted(node, Permission.SECURITY);
		} else {
			SecurityHelper.checkRead(node);
			SecurityHelper.checkSecurity(node);
		}

		if (canModify) {
			Integer currentPermissions = node.getUserPermissions().get(user);

			if (currentPermissions == null) {
				node.getUserPermissions().put(user, permissions);
			} else {
				node.getUserPermissions().put(user, permissions | currentPermissions);
			}

			session.update(node);
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Grant recursively
	 */
	@SuppressWarnings("unchecked")
	private int grantUserPermissionsInDepth(Session session, NodeBase node, String user, int permissions) throws PathNotFoundException,
			AccessDeniedException, DatabaseException, HibernateException {
		int total = grantUserPermissions(session, node, user, permissions, true);

		// Calculate children nodes
		String qs = "from NodeBase nb where nb.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", node.getUuid());
		List<NodeBase> ret = q.list();

		// Security Check
		SecurityHelper.pruneNodeList(ret);

		for (NodeBase child : ret) {
			total += grantUserPermissionsInDepth(session, child, user, permissions);
		}

		return total;
	}

	/**
	 * Revoke user permissions
	 */
	public void revokeUserPermissions(String uuid, String user, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		log.debug("revokeUserPermissions({})", uuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Root node
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);

			if (recursive) {
				long begin = System.currentTimeMillis();
				int total = revokeUserPermissionsInDepth(session, node, user, permissions);
				SystemProfiling.log(uuid + ", " + user + ", " + permissions + ", " + recursive, System.currentTimeMillis() - begin);
				log.trace("revokeUserPermissions.Time: {}", FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
				log.info("revokeUserPermissions.Total: {}", total);
			} else {
				revokeUserPermissions(session, node, user, permissions, false);
			}

			HibernateUtil.commit(tx);
			log.debug("revokeUserPermissions: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Revoke user permissions
	 */
	private int revokeUserPermissions(Session session, NodeBase node, String user, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, DatabaseException, HibernateException {
		// log.info("revokeUserPermissions({})", node.getUuid());
		boolean canModify = true;

		// Security Check
		if (recursive) {
			canModify = SecurityHelper.isGranted(node, Permission.READ) && SecurityHelper.isGranted(node, Permission.SECURITY);
		} else {
			SecurityHelper.checkRead(node);
			SecurityHelper.checkSecurity(node);
		}

		if (canModify) {
			Integer currentPermissions = node.getUserPermissions().get(user);

			if (currentPermissions != null) {
				Integer perms = ~permissions & currentPermissions;

				if (perms == Permission.NONE) {
					node.getUserPermissions().remove(user);
				} else {
					node.getUserPermissions().put(user, perms);
				}
			}

			session.update(node);
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Revoke recursively
	 */
	@SuppressWarnings("unchecked")
	private int revokeUserPermissionsInDepth(Session session, NodeBase node, String user, int permissions) throws PathNotFoundException,
			AccessDeniedException, DatabaseException, HibernateException {
		int total = revokeUserPermissions(session, node, user, permissions, true);

		// Calculate children nodes
		String qs = "from NodeBase nb where nb.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", node.getUuid());
		List<NodeBase> ret = q.list();

		// Security Check
		SecurityHelper.pruneNodeList(ret);

		for (NodeBase child : ret) {
			total += revokeUserPermissionsInDepth(session, child, user, permissions);
		}

		return total;
	}

	/**
	 * Get role permissions
	 */
	public Map<String, Integer> getRolePermissions(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("getRolePermissions({})", uuid);
		Map<String, Integer> ret = new HashMap<String, Integer>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			if (node != null) {
				Hibernate.initialize(ret = node.getRolePermissions());
			}

			HibernateUtil.commit(tx);
			log.debug("getRolePermissions: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Grant role permissions
	 */
	public void grantRolePermissions(String uuid, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		log.debug("grantRolePermissions({}, {}, {}, {})", new Object[]{uuid, role, permissions, recursive});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Root node
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);

			if (recursive) {
				long begin = System.currentTimeMillis();
				int total = grantRolePermissionsInDepth(session, node, role, permissions);
				SystemProfiling.log(uuid + ", " + role + ", " + permissions + ", " + recursive, System.currentTimeMillis() - begin);
				log.trace("grantRolePermissions.Time: {}", FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
				log.info("grantRolePermissions.Total: {}", total);
			} else {
				grantRolePermissions(session, node, role, permissions, false);
			}

			HibernateUtil.commit(tx);
			log.debug("grantRolePermissions: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Grant role permissions
	 */
	private int grantRolePermissions(Session session, NodeBase node, String role, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, DatabaseException, HibernateException {
		// log.info("grantRolePermissions({})", node.getUuid());
		boolean canModify = true;

		// Security Check
		if (recursive) {
			canModify = SecurityHelper.isGranted(node, Permission.READ) && SecurityHelper.isGranted(node, Permission.SECURITY);
		} else {
			SecurityHelper.checkRead(node);
			SecurityHelper.checkSecurity(node);
		}

		if (canModify) {
			Integer currentPermissions = node.getRolePermissions().get(role);

			if (currentPermissions == null) {
				node.getRolePermissions().put(role, permissions);
			} else {
				node.getRolePermissions().put(role, permissions | currentPermissions);
			}

			session.update(node);
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Grant recursively
	 */
	@SuppressWarnings("unchecked")
	private int grantRolePermissionsInDepth(Session session, NodeBase node, String role, int permissions) throws PathNotFoundException,
			AccessDeniedException, DatabaseException, HibernateException {
		int total = grantRolePermissions(session, node, role, permissions, true);

		// Calculate children nodes
		String qs = "from NodeBase nb where nb.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", node.getUuid());
		List<NodeBase> ret = q.list();

		// Security Check
		SecurityHelper.pruneNodeList(ret);

		for (NodeBase child : ret) {
			total += grantRolePermissionsInDepth(session, child, role, permissions);
		}

		return total;
	}

	/**
	 * Revoke role permissions
	 */
	public void revokeRolePermissions(String uuid, String role, int permissions, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		log.debug("revokeRolePermissions({}, {}, {}, {})", new Object[]{uuid, role, permissions, recursive});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Root node
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);

			if (recursive) {
				long begin = System.currentTimeMillis();
				int total = revokeRolePermissionsInDepth(session, node, role, permissions);
				SystemProfiling.log(uuid + ", " + role + ", " + permissions + ", " + recursive, System.currentTimeMillis() - begin);
				log.trace("revokeRolePermissions.Time: {}", FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
				log.info("revokeRolePermissions.Total: {}", total);
			} else {
				revokeRolePermissions(session, node, role, permissions, false);
			}

			HibernateUtil.commit(tx);
			log.debug("revokeRolePermissions: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Revoke role permissions
	 */
	private int revokeRolePermissions(Session session, NodeBase node, String role, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, DatabaseException, HibernateException {
		// log.info("revokeRolePermissions({})", node.getUuid());
		boolean canModify = true;

		// Security Check
		if (recursive) {
			canModify = SecurityHelper.isGranted(node, Permission.READ) && SecurityHelper.isGranted(node, Permission.SECURITY);
		} else {
			SecurityHelper.checkRead(node);
			SecurityHelper.checkSecurity(node);
		}

		if (canModify) {
			Integer currentPermissions = node.getRolePermissions().get(role);

			if (currentPermissions != null) {
				Integer perms = ~permissions & currentPermissions;

				if (perms == Permission.NONE) {
					node.getRolePermissions().remove(role);
				} else {
					node.getRolePermissions().put(role, perms);
				}
			}

			session.update(node);
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Revoke recursively
	 */
	@SuppressWarnings("unchecked")
	private int revokeRolePermissionsInDepth(Session session, NodeBase node, String role, int permissions) throws PathNotFoundException,
			AccessDeniedException, DatabaseException, HibernateException {
		int total = revokeRolePermissions(session, node, role, permissions, true);

		// Calculate children nodes
		String qs = "from NodeBase nb where nb.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", node.getUuid());
		List<NodeBase> ret = q.list();

		// Security Check
		SecurityHelper.pruneNodeList(ret);

		for (NodeBase child : ret) {
			total += revokeRolePermissionsInDepth(session, child, role, permissions);
		}

		return total;
	}

	/**
	 * Change security of multiples nodes
	 */
	public void changeSecurity(String uuid, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                           Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		log.debug("changeSecurity({}, {}, {}, {})", new Object[]{uuid, grantUsers, revokeUsers, grantRoles, revokeRoles, recursive});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Root node
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);

			if (recursive) {
				long begin = System.currentTimeMillis();
				int total = changeSecurityInDepth(session, node, grantUsers, revokeUsers, grantRoles, revokeRoles);
				SystemProfiling.log(uuid + ", " + grantUsers + ", " + revokeUsers + ", " + grantRoles + ", " + revokeRoles + ", "
						+ recursive, System.currentTimeMillis() - begin);
				log.trace("changeSecurity.Time: {}", FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
				log.info("changeSecurity.Total: {}", total);
			} else {
				changeSecurity(session, node, grantUsers, revokeUsers, grantRoles, revokeRoles, false);
			}

			HibernateUtil.commit(tx);
			log.debug("grantRolePermissions: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Change security.
	 *
	 * @see com.openkm.util.pendtask.ChangeSecurityTask
	 */
	public int changeSecurity(Session session, NodeBase node, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                          Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles, boolean recursive) throws PathNotFoundException,
			AccessDeniedException, DatabaseException, HibernateException {
		log.debug("changeSecurity({}, {}, {}, {}, {})", new Object[]{node.getUuid(), grantUsers, revokeUsers, grantRoles, revokeRoles});
		boolean canModify = true;

		// Security Check
		if (recursive) {
			canModify = SecurityHelper.isGranted(node, Permission.READ) && SecurityHelper.isGranted(node, Permission.SECURITY);
		} else {
			SecurityHelper.checkRead(node);
			SecurityHelper.checkSecurity(node);
		}

		if (canModify) {
			// Grant Users
			for (Entry<String, Integer> userGrant : grantUsers.entrySet()) {
				Integer currentPermissions = node.getUserPermissions().get(userGrant.getKey());

				if (currentPermissions == null) {
					node.getUserPermissions().put(userGrant.getKey(), userGrant.getValue());
				} else {
					node.getUserPermissions().put(userGrant.getKey(), userGrant.getValue() | currentPermissions);
				}
			}

			// Revoke Users
			for (Entry<String, Integer> userRevoke : revokeUsers.entrySet()) {
				Integer currentPermissions = node.getUserPermissions().get(userRevoke.getKey());

				if (currentPermissions != null) {
					Integer newPermissions = ~userRevoke.getValue() & currentPermissions;

					if (newPermissions == Permission.NONE) {
						node.getUserPermissions().remove(userRevoke.getKey());
					} else {
						node.getUserPermissions().put(userRevoke.getKey(), newPermissions);
					}
				}
			}

			// Grant Roles
			for (Entry<String, Integer> roleGrant : grantRoles.entrySet()) {
				Integer currentPermissions = node.getRolePermissions().get(roleGrant.getKey());

				if (currentPermissions == null) {
					node.getRolePermissions().put(roleGrant.getKey(), roleGrant.getValue());
				} else {
					node.getRolePermissions().put(roleGrant.getKey(), roleGrant.getValue() | currentPermissions);
				}
			}

			// Revoke Roles
			for (Entry<String, Integer> roleRevoke : revokeRoles.entrySet()) {
				Integer currentPermissions = node.getRolePermissions().get(roleRevoke.getKey());

				if (currentPermissions != null) {
					Integer newPermissions = ~roleRevoke.getValue() & currentPermissions;

					if (newPermissions == Permission.NONE) {
						node.getRolePermissions().remove(roleRevoke.getKey());
					} else {
						node.getRolePermissions().put(roleRevoke.getKey(), newPermissions);
					}
				}
			}

			session.update(node);
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Change security recursively
	 */
	@SuppressWarnings("unchecked")
	public int changeSecurityInDepth(Session session, NodeBase node, Map<String, Integer> grantUsers, Map<String, Integer> revokeUsers,
	                                 Map<String, Integer> grantRoles, Map<String, Integer> revokeRoles) throws PathNotFoundException, AccessDeniedException,
			DatabaseException, HibernateException {
		int total = changeSecurity(session, node, grantUsers, revokeUsers, grantRoles, revokeRoles, true);

		// Calculate children nodes
		String qs = "from NodeBase nb where nb.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", node.getUuid());
		List<NodeBase> ret = q.list();

		// Security Check
		SecurityHelper.pruneNodeList(ret);

		for (NodeBase child : ret) {
			total += changeSecurityInDepth(session, child, grantUsers, revokeUsers, grantRoles, revokeRoles);
		}

		return total;
	}

	/**
	 * Add category to node
	 */
	public void addCategory(String uuid, String catUuid) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("addCategory({}, {})", uuid, catUuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			if (!node.getCategories().contains(catUuid)) {
				node.getCategories().add(catUuid);
			}

			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("addCategory: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Remove category from node
	 */
	public void removeCategory(String uuid, String catUuid) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("removeCategory({}, {})", uuid, catUuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			node.getCategories().remove(catUuid);
			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("removeCategory: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Test for category in a node
	 */
	public boolean hasCategory(String uuid, String catId) throws PathNotFoundException, DatabaseException {
		log.debug("hasCategory({}, {})", uuid, catId);
		Session session = null;
		Transaction tx = null;
		boolean check;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			check = node.getCategories().contains(catId);
			HibernateUtil.commit(tx);

			log.debug("hasCategory: {}", check);
			return check;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Test for category in use
	 */
	public boolean isCategoryInUse(String catUuid) throws DatabaseException {
		log.debug("isCategoryInUse({}, {})", catUuid);
		final String qs = "from NodeBase nb where :category in elements(nb.categories)";
		final String sql = "select NCT_NODE from OKM_NODE_CATEGORY where NCT_CATEGORY = :catUuid";
		Session session = null;
		Transaction tx = null;
		boolean check;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			if (Config.NATIVE_SQL_OPTIMIZATIONS) {
				SQLQuery q = session.createSQLQuery(sql);
				// q.setCacheable(true);
				q.setString("catUuid", catUuid);
				q.addScalar("NCT_NODE", StandardBasicTypes.STRING);
				check = !q.list().isEmpty();
			} else {
				Query q = session.createQuery(qs).setCacheable(true);
				q.setString("category", catUuid);
				check = !q.list().isEmpty();
			}

			HibernateUtil.commit(tx);
			log.debug("isCategoryInUse: {}", check);
			return check;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Add keyword to node
	 */
	public void addKeyword(String uuid, String keyword) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("addKeyword({}, {})", uuid, keyword);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			if (!node.getKeywords().contains(keyword)) {
				node.getKeywords().add(keyword);
			}

			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("addKeyword: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Remove keyword from node
	 */
	public void removeKeyword(String uuid, String keyword) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("removeCategory({}, {})", uuid, keyword);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			node.getKeywords().remove(keyword);
			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("removeCategory: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Test for category in a node
	 */
	public boolean hasKeyword(String uuid, String keyword) throws PathNotFoundException, DatabaseException {
		log.debug("hasKeyword({}, {})", uuid, keyword);
		Session session = null;
		Transaction tx = null;
		boolean check;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			check = node.getKeywords().contains(keyword);
			HibernateUtil.commit(tx);

			log.debug("hasKeyword: {}", check);
			return check;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Subscribe user to node
	 */
	public void subscribe(String uuid, String user) throws PathNotFoundException, DatabaseException {
		log.debug("subscribe({}, {})", uuid, user);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			if (!node.getSubscriptors().contains(user)) {
				node.getSubscriptors().add(user);
			}

			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("subscribe: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Remove user subscription
	 */
	public void unsubscribe(String uuid, String user) throws PathNotFoundException, DatabaseException {
		log.debug("unsubscribe({}, {})", uuid, user);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			node.getSubscriptors().remove(user);
			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("unsubscribe: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get node subscriptors
	 */
	public Set<String> getSubscriptors(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("getSubscriptors({})", uuid);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			Set<String> subscriptors = node.getSubscriptors();
			Hibernate.initialize(subscriptors);
			log.debug("getSubscriptors: {}", subscriptors);
			return subscriptors;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Set node script
	 */
	public void setScript(String uuid, String code) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("setScript({}, {})", uuid, code);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			node.setScripting(true);
			node.setScriptCode(code);
			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("setScript: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Remove node script
	 */
	public void removeScript(String uuid) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("removeScript({}, {})", uuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			node.setScripting(false);
			node.setScriptCode(null);
			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("removeScript: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Obtain script code
	 */
	public String getScript(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("setScript({}, {})", uuid);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			String code = node.getScriptCode();
			log.debug("setScript: {}", code);
			return code;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get parent node uuid
	 */
	public String getParentUuid(String uuid) throws DatabaseException {
		log.debug("getParentUuid({})", uuid);
		String qs = "select nb.parent from NodeBase nb where nb.uuid=:uuid";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("uuid", uuid);
			String parent = (String) q.setMaxResults(1).uniqueResult();
			log.debug("getParentUuid: {}", parent);
			return parent;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get parent node
	 */
	public NodeBase getParentNode(String uuid) throws DatabaseException {
		log.debug("getParentNode({})", uuid);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			NodeBase parentNode = getParentNode(session, uuid);
			initializeSecurity(parentNode);
			log.debug("getParentNode: {}", parentNode);
			return parentNode;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get parent node
	 */
	public NodeBase getParentNode(Session session, String uuid) throws HibernateException {
		log.debug("getParentNode({}, {})", session, uuid);
		String qs = "from NodeBase nb1 where nb1.uuid = (select nb2.parent from NodeBase nb2 where nb2.uuid=:uuid)";
		Query q = session.createQuery(qs);
		q.setString("uuid", uuid);
		NodeBase parentNode = (NodeBase) q.setMaxResults(1).uniqueResult();
		log.debug("getParentNode: {}", parentNode);
		return parentNode;
	}

	/**
	 * Get parent node permissions
	 */
	@SuppressWarnings("unchecked")
	public NodeBase getParentNodePermissions(Session session, String uuid) throws HibernateException {
		log.debug("getParentNodePermissions({}, {})", session, uuid);
		String qs = "select nb1.uuid, index(userPermissions), userPermissions, index(rolePermissions), rolePermissions "
				+ "from NodeBase nb1 join nb1.userPermissions userPermissions join nb1.rolePermissions rolePermissions "
				+ "where nb1.uuid = (select nb2.parent from NodeBase nb2 where nb2.uuid=:uuid)";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setCacheRegion(CACHE_PARENT_NODE_PERMISSIONS);
		q.setString("uuid", uuid);
		List<Object[]> perms = (List<Object[]>) q.list();
		NodeBase nBase = null;

		if (!perms.isEmpty()) {
			nBase = new NodeBase();

			for (Object[] tupla : (List<Object[]>) q.list()) {
				if (nBase.getUuid() == null) {
					nBase.setUuid((String) tupla[0]);
				}

				if (!nBase.getUserPermissions().containsKey((String) tupla[1])) {
					nBase.getUserPermissions().put((String) tupla[1], (Integer) tupla[2]);
				}

				if (!nBase.getRolePermissions().containsKey((String) tupla[3])) {
					nBase.getRolePermissions().put((String) tupla[3], (Integer) tupla[4]);
				}
			}
		}

		log.debug("getParentNodePermissions: {}", nBase);
		return nBase;
	}

	/**
	 * Get result node count.
	 *
	 * @see com.openkm.module.db.DbStatsModule
	 */
	public long getCount(String nodeType) throws PathNotFoundException, DatabaseException {
		log.debug("getCount({})", new Object[]{nodeType});
		String qs = "select count(*) from " + nodeType + " nt";
		long begin = System.currentTimeMillis();
		Session session = null;
		Transaction tx = null;
		long total = 0;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			Query q = session.createQuery(qs);
			total = (Long) q.setMaxResults(1).uniqueResult();

			HibernateUtil.commit(tx);
			SystemProfiling.log(nodeType, System.currentTimeMillis() - begin);
			log.trace("getCount.Time: {}", System.currentTimeMillis() - begin);
			log.debug("getCount: {}", total);
			return total;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get result node count.
	 *
	 * @see com.openkm.module.db.DbStatsModule
	 */
	public long getCount(String nodeType, String context) throws PathNotFoundException, DatabaseException {
		log.debug("getCount({}, {})", new Object[]{nodeType, context});
		String qs = "select count(*) from " + nodeType + " nt where nt.context = :context";
		long begin = System.currentTimeMillis();
		Session session = null;
		Transaction tx = null;
		long total = 0;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			Query q = session.createQuery(qs);
			q.setString("context", PathUtils.fixContext(context));
			total = (Long) q.setMaxResults(1).uniqueResult();

			HibernateUtil.commit(tx);
			SystemProfiling.log(nodeType + ", " + context, System.currentTimeMillis() - begin);
			log.trace("Context: {}, Time: {}", context, System.currentTimeMillis() - begin);
			log.debug("getCount: {}", total);
			return total;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get result node count.
	 *
	 * @see com.openkm.module.db.DbStatsModule
	 */
	public long getBaseCount(String nodeType, String path) throws PathNotFoundException, DatabaseException {
		log.debug("getBaseCount({}, {})", new Object[]{nodeType, path});
		String qs = "select coalesce(count(*), 0) from NodeFolder n where n.parent=:parent";
		long begin = System.currentTimeMillis();
		Session session = null;
		Transaction tx = null;
		long total = 0;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			String uuid = getUuidFromPath(path);
			Query q = session.createQuery(qs).setCacheable(true);
			q.setString("parent", uuid);
			total = (Long) q.setMaxResults(1).uniqueResult();

			HibernateUtil.commit(tx);
			SystemProfiling.log(nodeType + ", " + path, System.currentTimeMillis() - begin);
			log.trace("getBaseCount.Path: {}, Time: {}", path, System.currentTimeMillis() - begin);
			log.debug("getBaseCount: {}", total);
			return total;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get result node count.
	 *
	 * @see com.openkm.module.db.DbStatsModule
	 */
	public long getSubtreeCount(String nodeType, String path, int depth) throws PathNotFoundException, DatabaseException {
		log.debug("getSubtreeCount({}, {}, {})", new Object[]{nodeType, path, depth});
		long begin = System.currentTimeMillis();
		Session session = null;
		Transaction tx = null;
		long total = 0;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			String uuid = getUuidFromPath(path);
			total = getSubtreeCountHelper(session, nodeType, uuid, depth, 1);

			HibernateUtil.commit(tx);
			SystemProfiling.log(nodeType + ", " + path + ", " + depth, System.currentTimeMillis() - begin);
			log.trace("getSubtreeCount.Path: {}, Time: {}", path, System.currentTimeMillis() - begin);
			log.debug("getSubtreeCount: {}", total);
			return total;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Helper method.
	 */
	@SuppressWarnings("unchecked")
	private long getSubtreeCountHelper(Session session, String nodeType, String parentUuid, int depth, int level)
			throws HibernateException, DatabaseException {
		log.debug("getSubtreeCountHelper({}, {}, {},  {})", new Object[]{nodeType, parentUuid, depth, level});
		String qs = "from NodeBase n where n.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", parentUuid);
		List<NodeBase> nodes = q.list();
		long total = 0;

		for (NodeBase nBase : nodes) {
			if (nBase instanceof NodeFolder) {
				total += getSubtreeCountHelper(session, nodeType, nBase.getUuid(), depth, level + 1);

				if (NodeFolder.class.getSimpleName().equals(nodeType)) {
					if (level >= depth) {
						total += 1;
					}
				}
			} else if (NodeDocument.class.getSimpleName().equals(nodeType) && nBase instanceof NodeDocument) {
				if (level >= depth) {
					total += 1;
				}
			}
		}

		return total;
	}

	/**
	 * Check if a subtree contains more than maxNodes nodes
	 */
	public boolean subTreeHasMoreThanNodes(String path, long maxNodes) throws PathNotFoundException, DatabaseException {
		log.debug("subTreeHasMoreThanNodes({}, {})", path, maxNodes);
		long begin = System.currentTimeMillis();
		Session session = null;
		Transaction tx = null;
		boolean ret = false;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			String uuid = getUuidFromPath(path);
			long count = subTreeHasMoreThanNodesHelper(session, uuid, maxNodes, 0);
			ret = count > maxNodes;

			HibernateUtil.commit(tx);
			SystemProfiling.log(path + ", " + maxNodes, System.currentTimeMillis() - begin);
			log.trace("subTreeHasMoreThanNodes.Path: {}, Time: {}", path, System.currentTimeMillis() - begin);
			log.debug("subTreeHasMoreThanNodes: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Helper method.
	 */
	@SuppressWarnings("unchecked")
	private long subTreeHasMoreThanNodesHelper(Session session, String parentUuid, long maxNodes, long curNodes) throws HibernateException,
			DatabaseException {
		log.debug("getSubtreeCountHelper({}, {}, {})", new Object[]{parentUuid, maxNodes, curNodes});
		String qs = "from NodeBase n where n.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", parentUuid);
		List<NodeBase> nodes = q.list();
		long total = 0;

		for (NodeBase nBase : nodes) {
			if (nBase instanceof NodeDocument) {
				total += 1;

				if (total + curNodes > maxNodes) {
					return total;
				}
			} else if (nBase instanceof NodeMail) {
				total += 1;

				if (total + curNodes > maxNodes) {
					return total;
				}
			} else if (nBase instanceof NodeFolder) {
				total += subTreeHasMoreThanNodesHelper(session, nBase.getUuid(), maxNodes, total + curNodes + 1) + 1;

				if (total + curNodes > maxNodes) {
					return total;
				}
			}
		}

		return total;
	}

	/**
	 * Check for same node name in same parent
	 *
	 * @param session Hibernate session.
	 * @param parent Parent node uuid.
	 * @param name Name of the child node to test.
	 * @return true if child item exists or false otherwise.
	 */
	public boolean testItemExistence(Session session, String parent, String name) throws HibernateException, DatabaseException {
		String qs = "from NodeBase nb where nb.parent=:parent and nb.name=:name";
		Query q = session.createQuery(qs);
		q.setString("parent", parent);
		q.setString("name", name);

		return !q.list().isEmpty();
	}

	/**
	 * Check for same node name in same parent
	 *
	 * @param session Hibernate session.
	 * @param parent Parent node uuid.
	 * @param name Name of the child node to test.
	 */
	public void checkItemExistence(Session session, String parent, String name) throws PathNotFoundException, HibernateException,
			DatabaseException, ItemExistsException {
		if (testItemExistence(session, parent, name)) {
			String path = getPathFromUuid(session, parent);
			throw new ItemExistsException(path + "/" + name);
		}
	}

	/**
	 * Get node type by UUID
	 */
	public String getNodeTypeByUuid(String uuid) throws RepositoryException, PathNotFoundException, DatabaseException {
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			NodeBase nBase = (NodeBase) session.get(NodeBase.class, uuid);

			if (nBase == null) {
				throw new PathNotFoundException(uuid);
			}

			// Security Check
			SecurityHelper.checkRead(nBase);

			if (nBase instanceof NodeFolder) {
				return Folder.TYPE;
			} else if (nBase instanceof NodeDocument) {
				return Document.TYPE;
			} else if (nBase instanceof NodeMail) {
				return Mail.TYPE;
			} else {
				throw new RepositoryException("Unknown node type");
			}
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Add property group
	 */
	public void addPropertyGroup(String uuid, String grpName) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.info("addPropertyGroup({}, {})", uuid, grpName);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
				SecurityHelper.checkExtended(node, Permission.PROPERTY_GROUP);
			}

			RegisteredPropertyGroup rpg = (RegisteredPropertyGroup) session.get(RegisteredPropertyGroup.class, grpName);

			if (rpg != null) {
				for (String propName : rpg.getProperties().keySet()) {
					NodeProperty nodProp = new NodeProperty();
					nodProp.setNode(node);
					nodProp.setGroup(rpg.getName());
					nodProp.setName(propName);
					boolean alreadyAssigned = false;

					for (NodeProperty np : node.getProperties()) {
						if (np.getGroup().equals(nodProp.getGroup()) && np.getName().equals(nodProp.getName())) {
							alreadyAssigned = true;
							break;
						}
					}

					if (!alreadyAssigned) {
						node.getProperties().add(nodProp);
					}
				}
			} else {
				HibernateUtil.rollback(tx);
				throw new RepositoryException("Property Group not registered: " + grpName);
			}

			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("addPropertyGroup: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Remove property group
	 */
	public void removePropertyGroup(String uuid, String grpName) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("removePropertyGroup({}, {})", uuid, grpName);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
				SecurityHelper.checkExtended(node, Permission.PROPERTY_GROUP);
			}

			for (Iterator<NodeProperty> it = node.getProperties().iterator(); it.hasNext(); ) {
				NodeProperty nodProp = it.next();

				if (grpName.equals(nodProp.getGroup())) {
					it.remove();
					session.delete(nodProp);
				}
			}

			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("removePropertyGroup: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get assigned property groups
	 */
	@SuppressWarnings("unchecked")
	public List<String> getPropertyGroups(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("getPropertyGroups({}, {})", uuid);
		String qs = "select distinct(nbp.group) from NodeBase nb join nb.properties nbp where nb.uuid=:uuid";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			Query q = session.createQuery(qs);
			q.setString("uuid", uuid);
			List<String> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("getPropertyGroups: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get properties from property group
	 */
	public Map<String, String> getProperties(String uuid, String grpName) throws PathNotFoundException, DatabaseException {
		log.debug("getProperties({}, {})", uuid, grpName);
		long begin = System.currentTimeMillis();
		Map<String, String> ret = new HashMap<String, String>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			for (NodeProperty nodProp : node.getProperties()) {
				if (grpName.equals(nodProp.getGroup())) {
					ret.put(nodProp.getName(), nodProp.getValue());
				}
			}

			HibernateUtil.commit(tx);
			SystemProfiling.log(uuid + ", " + grpName, System.currentTimeMillis() - begin);
			log.trace("getProperties.Time: {}", System.currentTimeMillis() - begin);
			log.debug("getProperties: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get single property value from property group
	 */
	public String getProperty(String uuid, String grpName, String propName) throws PathNotFoundException, DatabaseException {
		log.debug("getProperty({}, {}, {})", new Object[]{uuid, grpName, propName});
		long begin = System.currentTimeMillis();
		String propValue = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);

			for (NodeProperty nodProp : node.getProperties()) {
				if (grpName.equals(nodProp.getGroup()) && propName.equals(nodProp.getName())) {
					propValue = nodProp.getValue();
					break;
				}
			}

			HibernateUtil.commit(tx);
			SystemProfiling.log(uuid + ", " + grpName + ", " + propName, System.currentTimeMillis() - begin);
			log.trace("getProperty.Time: {}", System.currentTimeMillis() - begin);
			log.debug("getProperty: {}", propValue);
			return propValue;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Set common node properties
	 */
	public void setProperties(Session session, NodeBase nBase, Node props) throws PathNotFoundException, DatabaseException {
		// Set keywords
		if (props.getKeywords() != null && !props.getKeywords().isEmpty()) {
			nBase.setKeywords(props.getKeywords());
		}

		// Set categories
		if (props.getCategories() != null && !props.getCategories().isEmpty()) {
			Set<String> catIds = new HashSet<>();

			for (Folder fld : props.getCategories()) {
				if (fld.getUuid() != null && !fld.getUuid().isEmpty()) {
					catIds.add(fld.getUuid());
				} else if (fld.getPath() != null && !fld.getPath().isEmpty()) {
					if (fld.getPath().startsWith("/" + Repository.CATEGORIES)) {
						// Categories should be at /okm:categories
						catIds.add(getUuidFromPath(session, fld.getPath()));
					}
				}
			}

			nBase.setCategories(catIds);
		}
	}
	
	/**
	 * Set properties from property group
	 */
	public Map<String, String> setProperties(String uuid, String grpName, Map<String, String> properties) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setProperties({}, {}, {})", new Object[]{uuid, grpName, properties});
		Map<String, String> ret = new HashMap<String, String>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase node = (NodeBase) session.load(NodeBase.class, uuid);
			SecurityHelper.checkRead(node);
			SecurityHelper.checkWrite(node);

			if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
				SecurityHelper.checkExtended(node, Permission.PROPERTY_GROUP);
			}

			Set<NodeProperty> tmp = new HashSet<NodeProperty>();

			for (Entry<String, String> prop : properties.entrySet()) {
				boolean alreadyAssigned = false;

				// Set new property group values
				for (NodeProperty nodProp : node.getProperties()) {
					if (grpName.equals(nodProp.getGroup()) && prop.getKey().equals(nodProp.getName())) {
						log.debug("UPDATE - Group: {}, Property: {}, Value: {}", new Object[]{grpName, prop.getKey(), prop.getValue()});
						nodProp.setValue(prop.getValue());
						alreadyAssigned = true;

						// TODO: Workaround for Hibernate Search
						tmp.add(nodProp);
					} else if (nodProp.getValue() != null && !nodProp.getValue().isEmpty()) {
						if (!tmp.contains(nodProp)) {
							log.debug("KEEP - Group: {}, Property: {}, Value: {}", new Object[]{nodProp.getGroup(), nodProp.getName(), nodProp.getValue()});

							// TODO: Workaround for Hibernate Search
							tmp.add(nodProp);
						}
					}
				}

				if (!alreadyAssigned) {
					log.debug("ADD - Group: {}, Property: {}, Value: {}", new Object[]{grpName, prop.getKey(), prop.getValue()});
					NodeProperty nodProp = new NodeProperty();
					nodProp.setNode(node);
					nodProp.setGroup(grpName);
					nodProp.setName(prop.getKey());
					nodProp.setValue(prop.getValue());

					// TODO: Workaround for Hibernate Search
					tmp.add(nodProp);
				}
			}

			node.setProperties(tmp);
			session.update(node);
			HibernateUtil.commit(tx);
			log.debug("setProperties: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Fix node stored path. Also valid for initialize when upgrading.
	 */
	@SuppressWarnings("unchecked")
	public void fixNodePath() throws DatabaseException {
		log.debug("fixNodePath()");
		String qs = "from NodeBase nb where nb.parent=:parent";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// First level nodes
			Query q = session.createQuery(qs).setCacheable(true);
			q.setString("parent", Config.ROOT_NODE_UUID);

			for (NodeBase nb : (List<NodeBase>) q.list()) {
				nb.setPath("/" + nb.getName());
				session.update(nb);

				// Process in depth
				fixNodePathHelper(session, nb);
			}

			HibernateUtil.commit(tx);
			log.debug("fixNodePath: void");
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	@SuppressWarnings("unchecked")
	private void fixNodePathHelper(Session session, NodeBase parentNode) throws HibernateException {
		String qs = "from NodeBase nb where nb.parent=:parent";
		Query q = session.createQuery(qs).setCacheable(true);
		q.setString("parent", parentNode.getUuid());

		for (NodeBase nb : (List<NodeBase>) q.list()) {
			nb.setPath(parentNode.getPath() + "/" + nb.getName());
			session.update(nb);

			// Process in depth
			fixNodePathHelper(session, nb);
		}
	}

	public void copyAttributes(String srcUuid, String dstUuid, ExtendedAttributes extAttr) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		log.info("copyAttributes({}, {}, {})", new Object[]{srcUuid, dstUuid, extAttr});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase srcNode = (NodeBase) session.load(NodeBase.class, srcUuid);
			SecurityHelper.checkRead(srcNode);

			NodeBase dstNode = (NodeBase) session.load(NodeBase.class, dstUuid);
			SecurityHelper.checkRead(dstNode);
			SecurityHelper.checkWrite(dstNode);

			if (extAttr != null) {
				if (extAttr.isKeywords()) {
					Set<String> keywords = srcNode.getKeywords();
					dstNode.setKeywords(CloneUtils.clone(keywords));
				}

				if (extAttr.isCategories()) {
					Set<String> categories = srcNode.getCategories();
					dstNode.setCategories(CloneUtils.clone(categories));
				}

				if (extAttr.isPropertyGroups()) {
					Set<NodeProperty> propertyGroups = srcNode.getProperties();

					for (NodeProperty nProp : CloneUtils.clone(propertyGroups)) {
						nProp.setNode(dstNode);
						dstNode.getProperties().add(nProp);
					}
				}

				if (extAttr.isNotes()) {
					List<NodeNote> notes = NodeNoteDAO.getInstance().findByParent(srcNode.getUuid());

					for (NodeNote nNote : CloneUtils.clone(notes)) {
						BaseNoteModule.create(dstNode.getUuid(), nNote.getAuthor(), nNote.getText());
					}
				}

				if (extAttr.isWiki()) {
					WikiPage wiki = WikiPageDAO.findLatestByNode(srcNode.getUuid());

					if (wiki != null) {
						wiki.setNode(dstNode.getUuid());
						wiki.setTitle(dstNode.getUuid());
						wiki.setDate(Calendar.getInstance());
						wiki.setLockUser(null);
						wiki.setDeleted(false);
						WikiPageDAO.create(wiki);
					}
				}
			}

			session.update(dstNode);
			HibernateUtil.commit(tx);
			log.debug("copyAttributes: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	public void initialize(NodeBase nBase) {
		if (nBase != null) {
			Hibernate.initialize(nBase);
			Hibernate.initialize(nBase.getKeywords());
			Hibernate.initialize(nBase.getCategories());
			Hibernate.initialize(nBase.getSubscriptors());
			Hibernate.initialize(nBase.getUserPermissions());
			Hibernate.initialize(nBase.getRolePermissions());
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	public void initializeSecurity(NodeBase nBase) {
		if (nBase != null) {
			Hibernate.initialize(nBase);
			Hibernate.initialize(nBase.getUserPermissions());
			Hibernate.initialize(nBase.getRolePermissions());
		}
	}
}
