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

import com.openkm.core.*;
import com.openkm.dao.bean.NodeBase;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.extension.dao.ForumDAO;
import com.openkm.extension.dao.StapleGroupDAO;
import com.openkm.extension.dao.WikiPageDAO;
import com.openkm.module.db.stuff.SecurityHelper;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.FormatUtil;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import org.hibernate.*;
import org.hibernate.search.FullTextSession;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeMailDAO {
	private static Logger log = LoggerFactory.getLogger(NodeMailDAO.class);
	private static NodeMailDAO single = new NodeMailDAO();
	private static final String CACHE_MAILS_BY_CATEGORY = "com.openkm.cache.mailsByCategory";
	private static final String CACHE_MAILS_BY_KEYWORD = "com.openkm.cache.mailsByKeyword";

	private NodeMailDAO() {
	}

	public static NodeMailDAO getInstance() {
		return single;
	}

	/**
	 * Create node
	 */
	public synchronized void create(NodeMail nMail) throws PathNotFoundException, AccessDeniedException, ItemExistsException,
			DatabaseException {
		log.debug("create({})", nMail);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase parentNode = (NodeBase) session.load(NodeBase.class, nMail.getParent());
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);

			// Check for same mail name in same parent
			NodeBaseDAO.getInstance().checkItemExistence(session, nMail.getParent(), nMail.getName());

			// Need to replace 0x00 because PostgreSQL does not accept string containing 0x00
			nMail.setContent(FormatUtil.fixUTF8(nMail.getContent()));

			// Need to remove Unicode surrogate because of MySQL => SQL Error: 1366, SQLState: HY000
			nMail.setContent(FormatUtil.trimUnicodeSurrogates(nMail.getContent()));

			session.save(nMail);
			HibernateUtil.commit(tx);
			log.debug("create: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (ItemExistsException e) {
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
	 * Find by parent
	 */
	@SuppressWarnings("unchecked")
	public List<NodeMail> findByParent(String parentUuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByParent({})", parentUuid);
		String qs = "from NodeMail nm where nm.parent=:parent order by nm.name";
		Session session = null;
		Transaction tx = null;

		try {
			long begin = System.currentTimeMillis();
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			if (!Config.ROOT_NODE_UUID.equals(parentUuid)) {
				NodeBase parentNode = (NodeBase) session.load(NodeBase.class, parentUuid);
				SecurityHelper.checkRead(parentNode);
			}

			Query q = session.createQuery(qs).setCacheable(true);
			q.setString("parent", parentUuid);
			List<NodeMail> ret = q.list();

			// Security Check
			SecurityHelper.pruneNodeList(ret);

			initialize(ret);
			HibernateUtil.commit(tx);

			SystemProfiling.log(parentUuid, System.currentTimeMillis() - begin);
			log.trace("findByParent.Time: {}", System.currentTimeMillis() - begin);
			log.debug("findByParent: {}", ret);
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
	 * Find by path
	 */
	public NodeMail findByPk(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByPk({})", uuid);
		String qs = "from NodeMail nm where nm.uuid=:uuid";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("uuid", uuid);
			NodeMail nMail = (NodeMail) q.setMaxResults(1).uniqueResult();

			if (nMail == null) {
				throw new PathNotFoundException(uuid);
			}

			// Security Check
			SecurityHelper.checkRead(nMail);

			initialize(nMail);
			log.debug("findByPk: {}", nMail);
			return nMail;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Check if this uuid represents a mail node.
	 *
	 * Used in SearchDAO, and should exposed in other method should make Security Check
	 */
	public boolean isMail(FullTextSession ftSession, String uuid) throws HibernateException {
		log.debug("isMail({}, {})", ftSession, uuid);
		boolean ret = ftSession.get(NodeMail.class, uuid) instanceof NodeMail;
		log.debug("isMail: {}", ret);
		return ret;
	}

	/**
	 * Search nodes by category
	 */
	@SuppressWarnings("unchecked")
	public List<NodeMail> findByCategory(String catUuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByCategory({})", catUuid);
		long begin = System.currentTimeMillis();
		final String qs = "from NodeMail nm where :category in elements(nm.categories) order by nm.name";
		final String sql = "select NBS_UUID from OKM_NODE_CATEGORY, OKM_NODE_MAIL " +
				"where NCT_CATEGORY = :catUuid and NCT_NODE = NBS_UUID";
		List<NodeMail> ret = new ArrayList<NodeMail>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase catNode = (NodeBase) session.load(NodeBase.class, catUuid);
			SecurityHelper.checkRead(catNode);

			if (Config.NATIVE_SQL_OPTIMIZATIONS) {
				SQLQuery q = session.createSQLQuery(sql);
				q.setCacheable(true);
				q.setCacheRegion(CACHE_MAILS_BY_CATEGORY);
				q.setString("catUuid", catUuid);
				q.addScalar("NBS_UUID", StandardBasicTypes.STRING);

				for (String uuid : (List<String>) q.list()) {
					NodeMail nMail = (NodeMail) session.load(NodeMail.class, uuid);
					ret.add(nMail);
				}
			} else {
				Query q = session.createQuery(qs).setCacheable(true);
				q.setString("category", catUuid);
				ret = q.list();
			}

			// Security Check
			SecurityHelper.pruneNodeList(ret);

			initialize(ret);
			HibernateUtil.commit(tx);
			SystemProfiling.log(catUuid, System.currentTimeMillis() - begin);
			log.trace("findByCategory.Time: {}", System.currentTimeMillis() - begin);
			log.debug("findByCategory: {}", ret);
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
	 * Search nodes by keyword
	 */
	@SuppressWarnings("unchecked")
	public List<NodeMail> findByKeyword(String keyword) throws DatabaseException {
		log.debug("findByKeyword({})", keyword);
		final String qs = "from NodeMail nm where :keyword in elements(nm.keywords) order by nm.name";
		final String sql = "select NBS_UUID from OKM_NODE_KEYWORD, OKM_NODE_MAIL " +
				"where NKW_KEYWORD = :keyword and NKW_NODE = NBS_UUID";
		List<NodeMail> ret = new ArrayList<NodeMail>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			if (Config.NATIVE_SQL_OPTIMIZATIONS) {
				SQLQuery q = session.createSQLQuery(sql);
				q.setCacheable(true);
				q.setCacheRegion(CACHE_MAILS_BY_KEYWORD);
				q.setString("keyword", keyword);
				q.addScalar("NBS_UUID", StandardBasicTypes.STRING);

				for (String uuid : (List<String>) q.list()) {
					NodeMail nMail = (NodeMail) session.load(NodeMail.class, uuid);
					ret.add(nMail);
				}
			} else {
				Query q = session.createQuery(qs).setCacheable(true);
				q.setString("keyword", keyword);
				ret = q.list();
			}

			// Security Check
			SecurityHelper.pruneNodeList(ret);

			initialize(ret);
			HibernateUtil.commit(tx);
			log.debug("findByKeyword: {}", ret);
			return ret;
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
	 * Search nodes by property value
	 */
	@SuppressWarnings("unchecked")
	public List<NodeMail> findByPropertyValue(String group, String property, String value) throws DatabaseException {
		log.debug("findByPropertyValue({}, {}, {})", property, value);
		String qs = "select nb from NodeMail nb join nb.properties nbp where nbp.group=:group and nbp.name=:property and nbp.value like :value";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			Query q = session.createQuery(qs);
			q.setString("group", group);
			q.setString("property", property);
			q.setString("value", "%" + value + "%");
			List<NodeMail> ret = q.list();

			// Security Check
			SecurityHelper.pruneNodeList(ret);

			initialize(ret);
			HibernateUtil.commit(tx);
			log.debug("findByPropertyValue: {}", ret);
			return ret;
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
	 * Check if folder has childs
	 */
	@SuppressWarnings("unchecked")
	public boolean hasChildren(String parentUuid) throws PathNotFoundException, DatabaseException {
		log.debug("hasChildren({})", parentUuid);
		String qs = "from NodeMail nm where nm.parent=:parent";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			if (!Config.ROOT_NODE_UUID.equals(parentUuid)) {
				NodeBase parentNode = (NodeBase) session.load(NodeBase.class, parentUuid);
				SecurityHelper.checkRead(parentNode);
			}

			Query q = session.createQuery(qs);
			q.setString("parent", parentUuid);
			List<NodeFolder> nodeList = q.list();

			// Security Check
			SecurityHelper.pruneNodeList(nodeList);

			boolean ret = !nodeList.isEmpty();
			HibernateUtil.commit(tx);
			log.debug("hasChildren: {}", ret);
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
	 * Rename mail
	 */
	public synchronized NodeMail rename(String uuid, String newName) throws PathNotFoundException, AccessDeniedException,
			ItemExistsException, DatabaseException {
		log.debug("rename({}, {})", uuid, newName);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase parentNode = NodeBaseDAO.getInstance().getParentNode(session, uuid);
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);
			NodeMail nMail = (NodeMail) session.load(NodeMail.class, uuid);
			SecurityHelper.checkRead(nMail);
			SecurityHelper.checkWrite(nMail);

			// Check for same folder name in same parent
			NodeBaseDAO.getInstance().checkItemExistence(session, nMail.getParent(), newName);

			nMail.setName(newName);

			if (Config.STORE_NODE_PATH) {
				nMail.setPath(parentNode.getPath() + "/" + newName);
			}

			session.update(nMail);
			initialize(nMail);
			HibernateUtil.commit(tx);
			log.debug("rename: {}", nMail);
			return nMail;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (ItemExistsException e) {
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
	 * Move mail
	 */
	public synchronized void move(String uuid, String dstUuid) throws PathNotFoundException, AccessDeniedException,
			ItemExistsException, DatabaseException {
		log.debug("move({}, {})", uuid, dstUuid);
		long begin = System.currentTimeMillis();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeFolder nDstFld = (NodeFolder) session.load(NodeFolder.class, dstUuid);
			SecurityHelper.checkRead(nDstFld);
			SecurityHelper.checkWrite(nDstFld);
			NodeMail nMail = (NodeMail) session.load(NodeMail.class, uuid);
			SecurityHelper.checkRead(nMail);
			SecurityHelper.checkWrite(nMail);

			// Check for same folder name in same parent
			NodeBaseDAO.getInstance().checkItemExistence(session, dstUuid, nMail.getName());

			// Check if context changes
			if (!nDstFld.getContext().equals(nMail.getContext())) {
				nMail.setContext(nDstFld.getContext());

				// Need recursive context changes
				moveHelper(session, uuid, nDstFld.getContext());
			}

			nMail.setParent(dstUuid);

			if (Config.STORE_NODE_PATH) {
				nMail.setPath(nDstFld.getPath() + "/" + nMail.getName());
			}

			session.update(nMail);
			HibernateUtil.commit(tx);
			SystemProfiling.log(uuid, System.currentTimeMillis() - begin);
			log.trace("move.Time: {}", System.currentTimeMillis() - begin);
			log.debug("move: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (ItemExistsException e) {
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
	 * Delete mail
	 */
	public void delete(String name, String uuid, String trashUuid) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		log.debug("delete({}, {}, {})", new Object[]{name, uuid, trashUuid});
		long begin = System.currentTimeMillis();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeFolder nTrashFld = (NodeFolder) session.load(NodeFolder.class, trashUuid);
			SecurityHelper.checkRead(nTrashFld);
			SecurityHelper.checkWrite(nTrashFld);
			NodeMail nMail = (NodeMail) session.load(NodeMail.class, uuid);
			SecurityHelper.checkRead(nMail);
			SecurityHelper.checkWrite(nMail);
			SecurityHelper.checkDelete(nMail);

			// Test if already exists a mail with the same name in the trash
			String testName = name;

			for (int i = 1; NodeBaseDAO.getInstance().testItemExistence(session, trashUuid, testName); i++) {
				// log.info("Trying with: {}", testName);
				testName = name + " (" + i + ")";
			}

			// Need recursive context changes
			moveHelper(session, uuid, nTrashFld.getContext());

			nMail.setContext(nTrashFld.getContext());
			nMail.setParent(trashUuid);
			nMail.setName(testName);

			if (Config.STORE_NODE_PATH) {
				nMail.setPath(nTrashFld.getPath() + "/" + testName);
			}

			session.update(nMail);
			HibernateUtil.commit(tx);
			SystemProfiling.log(uuid, System.currentTimeMillis() - begin);
			log.trace("delete.Time: {}", System.currentTimeMillis() - begin);
			log.debug("delete: void");
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

	@SuppressWarnings("unchecked")
	private void moveHelper(Session session, String parentUuid, String newContext) throws HibernateException {
		String qs = "from NodeBase nf where nf.parent=:parent";
		Query q = session.createQuery(qs);
		q.setString("parent", parentUuid);

		for (NodeBase nBase : (List<NodeBase>) q.list()) {
			nBase.setContext(newContext);
		}
	}

	/**
	 * Purge in depth
	 */
	public void purge(String uuid) throws PathNotFoundException, AccessDeniedException, LockException,
			DatabaseException, IOException {
		log.debug("purge({})", uuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeMail nMail = (NodeMail) session.load(NodeMail.class, uuid);
			SecurityHelper.checkRead(nMail);
			SecurityHelper.checkDelete(nMail);

			purgeHelper(session, nMail);
			HibernateUtil.commit(tx);
			log.debug("purge: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (IOException e) {
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
	 * Purge in depth helper
	 *
	 * @see com.openkm.dao.NodeFolderDAO.purgeHelper(Session, NodeFolder, boolean)
	 */
	@SuppressWarnings("unchecked")
	public void purgeHelper(Session session, String parentUuid) throws PathNotFoundException, AccessDeniedException,
			LockException, IOException, DatabaseException, HibernateException {
		String qs = "from NodeMail nm where nm.parent=:parent";
		long begin = System.currentTimeMillis();
		Query q = session.createQuery(qs);
		q.setString("parent", parentUuid);
		List<NodeMail> listMails = q.list();

		for (NodeMail nMail : listMails) {
			purgeHelper(session, nMail);
		}

		SystemProfiling.log(parentUuid, System.currentTimeMillis() - begin);
		log.trace("purgeHelper.Time: {}", System.currentTimeMillis() - begin);
	}

	/**
	 * Purge in depth helper
	 */
	private void purgeHelper(Session session, NodeMail nMail) throws PathNotFoundException, AccessDeniedException,
			LockException, IOException, DatabaseException, HibernateException {
		String path = NodeBaseDAO.getInstance().getPathFromUuid(session, nMail.getUuid());
		String user = PrincipalUtils.getUser();

		// Security Check
		SecurityHelper.checkRead(nMail);
		SecurityHelper.checkDelete(nMail);

		// Delete children documents
		NodeDocumentDAO.getInstance().purgeHelper(session, nMail.getUuid());

		// Delete children notes
		NodeNoteDAO.getInstance().purgeHelper(session, nMail.getUuid());

		// Delete bookmarks
		BookmarkDAO.purgeBookmarksByNode(nMail.getUuid());

		// Delete children wiki pages
		WikiPageDAO.purgeWikiPagesByNode(nMail.getUuid());

		// Delete children forum topics
		ForumDAO.purgeTopicsByNode(nMail.getUuid());

		// Delete children staples
		StapleGroupDAO.purgeStaplesByNode(nMail.getUuid());

		// Delete the node itself
		session.delete(nMail);

		// Activity log
		UserActivity.log(user, "PURGE_MAIL", nMail.getUuid(), path, null);
	}

	/**
	 * Check for a valid mail node.
	 */
	public boolean isValid(String uuid) throws DatabaseException {
		try {
			findByPk(uuid);
			return true;
		} catch (PathNotFoundException e) {
			return false;
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	public void initialize(NodeMail nMail) {
		if (nMail != null) {
			Hibernate.initialize(nMail);
			Hibernate.initialize(nMail.getTo());
			Hibernate.initialize(nMail.getCc());
			Hibernate.initialize(nMail.getBcc());
			Hibernate.initialize(nMail.getReply());
			Hibernate.initialize(nMail.getKeywords());
			Hibernate.initialize(nMail.getCategories());
			Hibernate.initialize(nMail.getSubscriptors());
			Hibernate.initialize(nMail.getUserPermissions());
			Hibernate.initialize(nMail.getRolePermissions());
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initialize(List<NodeMail> nMailList) {
		for (NodeMail nMail : nMailList) {
			initialize(nMail);
		}
	}
}
