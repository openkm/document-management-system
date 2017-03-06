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
import com.openkm.dao.bean.CronTab;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

public class CronTabDAO {
	private static Logger log = LoggerFactory.getLogger(CronTabDAO.class);

	private CronTabDAO() {
	}

	/**
	 * Create
	 */
	public static long create(CronTab ct) throws DatabaseException {
		log.debug("create({})", ct);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Long id = (Long) session.save(ct);
			HibernateUtil.commit(tx);
			log.debug("create: {}", id);
			return id;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Update
	 */
	public static void update(CronTab ct) throws DatabaseException {
		log.debug("update({})", ct);
		String qs = "select ct.fileContent, ct.fileName, ct.fileMime, ct.lastBegin, ct.lastEnd from CronTab ct where ct.id=:id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			if (ct.getFileContent() == null || ct.getFileContent().length() == 0) {
				Query q = session.createQuery(qs);
				q.setParameter("id", ct.getId());
				Object[] data = (Object[]) q.setMaxResults(1).uniqueResult();
				ct.setFileContent((String) data[0]);
				ct.setFileName((String) data[1]);
				ct.setFileMime((String) data[2]);
				ct.setLastBegin((Calendar) data[3]);
				ct.setLastEnd((Calendar) data[4]);
			}

			session.update(ct);
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
	 * Delete
	 */
	public static void delete(long ctId) throws DatabaseException {
		log.debug("delete({})", ctId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			CronTab ct = (CronTab) session.load(CronTab.class, ctId);
			session.delete(ct);
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
	 * Find by pk
	 */
	public static CronTab findByPk(long ctId) throws DatabaseException {
		log.debug("findByPk({})", ctId);
		String qs = "from CronTab ct where ct.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", ctId);
			CronTab ret = (CronTab) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by filename
	 */
	public static CronTab findByName(String name) throws DatabaseException {
		log.debug("findByName({})", name);
		String qs = "from CronTab ct where ct.name=:name";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("name", name);
			CronTab ret = (CronTab) q.setMaxResults(1).uniqueResult();
			log.debug("findByName: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find all
	 */
	@SuppressWarnings("unchecked")
	public static List<CronTab> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from CronTab ct order by ct.id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<CronTab> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Set begin time
	 */
	public static void setLastBegin(long ctId) throws DatabaseException {
		log.debug("setLastBegin({})", ctId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			CronTab ct = (CronTab) session.load(CronTab.class, ctId);
			ct.setLastBegin(Calendar.getInstance());
			session.update(ct);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("setLastBegin: void");
	}

	/**
	 * Set end time
	 */
	public static void setLastEnd(long ctId) throws DatabaseException {
		log.debug("setLastEnd({})", ctId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			CronTab ct = (CronTab) session.load(CronTab.class, ctId);
			ct.setLastEnd(Calendar.getInstance());
			session.update(ct);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("setLastEnd: void");
	}
}
