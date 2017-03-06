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

package com.openkm.dao;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.bean.NodeBase;
import com.openkm.dao.bean.NodeNote;
import com.openkm.module.db.stuff.SecurityHelper;
import com.openkm.util.SystemProfiling;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NodeNoteDAO {
	private static Logger log = LoggerFactory.getLogger(NodeNoteDAO.class);
	private static NodeNoteDAO single = new NodeNoteDAO();

	private NodeNoteDAO() {
	}

	public static NodeNoteDAO getInstance() {
		return single;
	}

	/**
	 * Find by parent
	 */
	@SuppressWarnings("unchecked")
	public List<NodeNote> findByParent(String parentUuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByParent({})", parentUuid);
		String qs = "from NodeNote nn where nn.parent=:parent order by nn.created";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase parentNode = (NodeBase) session.load(NodeBase.class, parentUuid);
			SecurityHelper.checkRead(parentNode);

			Query q = session.createQuery(qs).setCacheable(true);
			q.setString("parent", parentUuid);
			List<NodeNote> ret = q.list();
			initialize(ret);
			HibernateUtil.commit(tx);
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
	public NodeNote findByPk(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByPk({})", uuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase parentNode = getParentNode(session, uuid);
			SecurityHelper.checkRead(parentNode);

			NodeNote ret = (NodeNote) session.load(NodeNote.class, uuid);
			initialize(ret);
			HibernateUtil.commit(tx);
			log.debug("findByPk: {}", ret);
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
	 * Create
	 */
	public void create(NodeNote nNote) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("create({})", nNote);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase parentNode = (NodeBase) session.load(NodeBase.class, nNote.getParent());
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);

			session.save(nNote);
			HibernateUtil.commit(tx);
			log.debug("create: void");
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
	 * Delete
	 */
	public void delete(String uuid) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("delete({})", uuid);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase parentNode = getParentNode(session, uuid);
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);

			NodeNote nNote = (NodeNote) session.load(NodeNote.class, uuid);
			session.delete(nNote);
			HibernateUtil.commit(tx);
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

		log.debug("delete: void");
	}

	/**
	 * Update
	 */
	public void update(NodeNote nNote) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("update({})", nNote);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// Security Check
			NodeBase parentNode = getParentNode(session, nNote.getUuid());
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);

			session.update(nNote);
			HibernateUtil.commit(tx);
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

		log.debug("update: void");
	}

	/**
	 * Purge in depth helper
	 */
	@SuppressWarnings("unchecked")
	public void purgeHelper(Session session, String parentUuid) throws HibernateException {
		String qs = "from NodeNote nn where nn.parent=:parent";
		long begin = System.currentTimeMillis();
		Query q = session.createQuery(qs);
		q.setString("parent", parentUuid);
		List<NodeNote> listNotes = q.list();

		for (NodeNote nNote : listNotes) {
			session.delete(nNote);
		}

		SystemProfiling.log(parentUuid, System.currentTimeMillis() - begin);
		log.trace("purgeHelper.Time: {}", System.currentTimeMillis() - begin);
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
			NodeBaseDAO.getInstance().initialize(parentNode);
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
	private NodeBase getParentNode(Session session, String uuid) throws HibernateException {
		log.debug("getParentNode({}, {})", session, uuid);
		String qs = "select nn.parent from NodeNote nn where nn.uuid=:uuid";
		Query q = session.createQuery(qs);
		q.setString("uuid", uuid);
		String parentUuid = (String) q.setMaxResults(1).uniqueResult();
		NodeBase parentNode = (NodeBase) session.load(NodeBase.class, parentUuid);
		log.debug("getParentNode: {}", parentNode);
		return parentNode;
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initialize(NodeNote nNote) {
		if (nNote != null) {
			Hibernate.initialize(nNote);
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initialize(List<NodeNote> nNoteList) {
		for (NodeNote nNote : nNoteList) {
			initialize(nNote);
		}
	}
}
