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

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Dashboard;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

public class DashboardDAO {
	private static Logger log = LoggerFactory.getLogger(DashboardDAO.class);

	private DashboardDAO() {
	}

	/**
	 * Get dashboard stats
	 */
	@SuppressWarnings("unchecked")
	public Dashboard findByPk(int dsId) throws DatabaseException {
		log.debug("findByPk({})", dsId);
		String qs = "from Dashboard db where db.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", dsId);
			List<Dashboard> results = q.list(); // uniqueResult
			Dashboard ret = null;

			if (results.size() == 1) {
				ret = results.get(0);
			}

			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Create dashboard stats
	 */
	public static void createIfNew(Dashboard db) throws DatabaseException {
		String qs = "from Dashboard db where db.user=:user and db.source=:source " +
				"and db.node=:node and db.date=:date";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("user", db.getUser());
			q.setString("source", db.getSource());
			q.setString("node", db.getNode());
			q.setCalendar("date", db.getDate());

			if (q.list().isEmpty()) {
				session.save(db);
			}

			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Delete dashboard stats
	 */
	public void delete(int dsId) throws DatabaseException {
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Dashboard ds = (Dashboard) session.load(Dashboard.class, dsId);
			session.delete(ds);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by user source
	 */
	@SuppressWarnings("unchecked")
	public static List<Dashboard> findByUserSource(String user, String source) throws
			DatabaseException {
		log.debug("findByUserSource({}, {})", user, source);
		String qs = "from Dashboard db where db.user=:user and db.source=:source";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("user", user);
			q.setString("source", source);
			List<Dashboard> ret = q.list();
			log.debug("findByUserSource: " + ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Delete visited nodes
	 */
	public static void deleteVisitedNodes(String user, String source) throws DatabaseException {
		log.debug("deleteVisitedNodes({}, {})", user, source);
		String qs = "delete from Dashboard db where db.user=:user and db.source=:source";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("user", user);
			q.setString("source", source);
			q.executeUpdate();
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteVisitedNodes: void");
	}

	/**
	 * Delete old visited node
	 */
	public static void purgeOldVisitedNode(String user, String source, String node, Calendar date) throws
			DatabaseException {
		log.debug("purgeOldVisitedNode({}, {}, {}, {})", new Object[]{user, source, node, date});
		String qs = "delete from Dashboard db where db.user=:user and db.source=:source " +
				"and db.node=:node and db.date=:date";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("user", user);
			q.setString("source", source);
			q.setString("node", node);
			q.setCalendar("date", date);
			q.executeUpdate();
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("purgeOldVisitedNode: void");
	}
}
