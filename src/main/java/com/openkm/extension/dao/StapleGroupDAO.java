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

package com.openkm.extension.dao;

import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.HibernateUtil;
import com.openkm.extension.dao.bean.Staple;
import com.openkm.extension.dao.bean.StapleGroup;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StapleGroupDAO {
	private static Logger log = LoggerFactory.getLogger(StapleGroupDAO.class);

	private StapleGroupDAO() {
	}

	/**
	 * Create
	 */
	public static long create(StapleGroup sg) throws DatabaseException {
		log.debug("create({})", sg);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Long id = (Long) session.save(sg);
			HibernateUtil.commit(tx);
			log.debug("create: {}" + id);
			return id;
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
	public static void delete(long sgId) throws DatabaseException {
		log.debug("delete({})", sgId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			StapleGroup sg = (StapleGroup) session.load(StapleGroup.class, sgId);
			session.delete(sg);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("delete: void");
	}

	/**
	 * Delete
	 */
	public static void deleteStaple(long stId) throws DatabaseException {
		log.debug("delete({})", stId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Staple st = (Staple) session.load(Staple.class, stId);
			session.delete(st);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("delete: void");
	}

	/**
	 * Find all stapling groups
	 */
	@SuppressWarnings("unchecked")
	public static List<StapleGroup> findAll(String nodeUuid) throws DatabaseException,
			RepositoryException {
		log.debug("findAll({}, {})", nodeUuid);
		String qs = "select sg from StapleGroup sg, Staple st where st.node=:node and st in elements(sg.staples)";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("node", nodeUuid);
			List<StapleGroup> ret = q.list();

			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by pk
	 */
	public static StapleGroup findByPk(long sgId) throws DatabaseException {
		log.debug("findByPk({})", sgId);
		String qs = "from StapleGroup sg where sg.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", sgId);
			StapleGroup ret = (StapleGroup) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Update
	 */
	public static void update(StapleGroup sg) throws DatabaseException {
		log.debug("update({})", sg);
		String qs = "select sg.user from StapleGroup sg where sg.id=:id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setParameter("id", sg.getId());
			String user = (String) q.setMaxResults(1).uniqueResult();
			sg.setUser(user);
			session.update(sg);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("update: void");
	}

	/**
	 * Delete by node uuid
	 */
	@SuppressWarnings("unchecked")
	public static void purgeStaplesByNode(String nodeUuid) throws DatabaseException {
		log.debug("purgeStaplesByNode({})", nodeUuid);
		String qsStaples = "from Staple st where st.node=:uuid";
		String qsEmpty = "select sg.id from StapleGroup sg left join sg.staples st group by sg.id having count(st)=0";
		String qsDelete = "delete from StapleGroup sg where sg.id=:id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			Query qStaples = session.createQuery(qsStaples);
			qStaples.setString("uuid", nodeUuid);

			for (Staple st : (List<Staple>) qStaples.list()) {
				session.delete(st);
			}

			// Remove empty staple groups
			for (long sgId : (List<Long>) session.createQuery(qsEmpty).list()) {
				session.createQuery(qsDelete).setLong("id", sgId).executeUpdate();
			}

			HibernateUtil.commit(tx);
			log.debug("purgeStaplesByNode: void");
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}