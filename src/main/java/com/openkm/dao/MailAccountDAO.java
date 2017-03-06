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
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.bean.MailAccount;
import com.openkm.dao.bean.MailFilter;
import com.openkm.dao.bean.MailFilterRule;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MailAccountDAO {
	private static Logger log = LoggerFactory.getLogger(MailAccountDAO.class);

	private MailAccountDAO() {
	}

	/**
	 * Create
	 */
	public static void create(MailAccount ma) throws DatabaseException {
		log.debug("create({})", ma);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(ma);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("create: void");
	}

	/**
	 * Update
	 */
	public static void update(MailAccount ma) throws DatabaseException {
		log.debug("update({})", ma);
		String qs = "select ma.mailPassword from MailAccount ma where ma.id=:id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setParameter("id", ma.getId());
			String pass = (String) q.setMaxResults(1).uniqueResult();
			ma.setMailPassword(pass);
			session.update(ma);
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
	 * Update password
	 */
	public static void updatePassword(long maId, String mailPassword) throws DatabaseException {
		log.debug("updatePassword({}, {})", maId, mailPassword);
		String qs = "update MailAccount ma set ma.mailPassword=:mailPassword where ma.id=:id";
		Session session = null;
		Transaction tx = null;

		try {
			if (mailPassword != null && mailPassword.trim().length() > 0) {
				session = HibernateUtil.getSessionFactory().openSession();
				tx = session.beginTransaction();
				Query q = session.createQuery(qs);
				q.setString("mailPassword", mailPassword);
				q.setLong("id", maId);
				q.executeUpdate();
				HibernateUtil.commit(tx);
			}
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("updatePassword: void");
	}

	/**
	 * Delete
	 */
	public static void delete(long maId) throws DatabaseException {
		log.debug("delete({})", maId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			MailAccount ma = (MailAccount) session.load(MailAccount.class, maId);
			session.delete(ma);
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
	 * Find by user
	 */
	@SuppressWarnings("unchecked")
	public static List<MailAccount> findByUser(String usrId, boolean filterByActive) throws DatabaseException {
		log.debug("findByUser({}, {})", usrId, filterByActive);
		String qs = "from MailAccount ma where ma.user=:user " +
				(filterByActive ? "and ma.active=:active" : "") + " order by ma.id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("user", usrId);

			if (filterByActive) {
				q.setBoolean("active", true);
			}

			List<MailAccount> ret = q.list();
			log.debug("findByUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * find all mail accounts
	 */
	@SuppressWarnings("unchecked")
	public static List<MailAccount> findAll(boolean filterByActive) throws DatabaseException {
		log.debug("findAll({})", filterByActive);
		String qs = "from MailAccount ma " + (filterByActive ? "where ma.active=:active" : "") +
				" order by ma.id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);

			if (filterByActive) {
				q.setBoolean("active", true);
			}

			List<MailAccount> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by pk
	 */
	public static MailAccount findByPk(long maId) throws DatabaseException {
		log.debug("findByPk({})", maId);
		String qs = "from MailAccount ma where ma.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", maId);
			MailAccount ret = (MailAccount) q.setMaxResults(1).uniqueResult();
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
	public static void updateFilter(MailFilter mf) throws DatabaseException {
		log.debug("updateFilter({})", mf);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(mf);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("updateFilter: void");
	}

	/**
	 * Delete
	 */
	public static void deleteFilter(long mfId) throws DatabaseException {
		log.debug("deleteFilter({})", mfId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			MailFilter mf = (MailFilter) session.load(MailFilter.class, mfId);
			session.delete(mf);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteFilter: void");
	}

	/**
	 * Find by pk
	 */
	public static MailFilter findFilterByPk(long mfId) throws PathNotFoundException, DatabaseException {
		log.debug("findFilterByPk({})", mfId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			MailFilter ret = (MailFilter) session.load(MailFilter.class, mfId);
			String nodePath = NodeBaseDAO.getInstance().getPathFromUuid(session, ret.getNode());

			// Always keep path in sync with uuid
			if (!nodePath.equals(ret.getPath())) {
				ret.setPath(nodePath);
				session.update(ret);
			}

			HibernateUtil.commit(tx);
			log.debug("findFilterByPk: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
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
	 * Update
	 */
	public static void updateRule(MailFilterRule fr) throws DatabaseException {
		log.debug("updateRule({})", fr);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(fr);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("updateRule: void");
	}

	/**
	 * Delete
	 */
	public static void deleteRule(long frId) throws DatabaseException {
		log.debug("deleteRule({})", frId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			MailFilterRule fr = (MailFilterRule) session.load(MailFilterRule.class, frId);
			session.delete(fr);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteRule: void");
	}

	/**
	 * Find by pk
	 */
	public static MailFilterRule findRuleByPk(long frId) throws DatabaseException {
		log.debug("findRuleByPk({})", frId);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			MailFilterRule ret = (MailFilterRule) session.load(MailFilterRule.class, frId);
			log.debug("findRuleByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
