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
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.bean.QueryParams;
import com.openkm.extension.dao.bean.ProposedQueryReceived;
import com.openkm.extension.dao.bean.ProposedQuerySent;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProposedQueryDAO {
	private static Logger log = LoggerFactory.getLogger(ProposedQueryDAO.class);

	private ProposedQueryDAO() {
	}

	/**
	 * Send proposed query
	 */
	public static void send(long qpId, String from, String to, String user, String comment) throws
			DatabaseException {
		log.debug("send({}, {}, {}, {}, {}, {}, {})", new Object[]{qpId, from, to, user, comment});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			QueryParams qp = (QueryParams) session.get(QueryParams.class, qpId);

			ProposedQuerySent pqSent = new ProposedQuerySent();
			pqSent.setFrom(from);
			pqSent.setTo(to);
			pqSent.setUser(user);
			pqSent.setComment(comment);
			pqSent.setSentDate(Calendar.getInstance());
			qp.getProposedSent().add(pqSent);

			ProposedQueryReceived pqReceived = new ProposedQueryReceived();
			pqReceived.setFrom(from);
			pqReceived.setTo(to);
			pqReceived.setUser(user);
			pqReceived.setComment(comment);
			pqReceived.setSentDate(Calendar.getInstance());
			qp.getProposedReceived().add(pqReceived);

			session.save(qp);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("send: void");
	}

	/**
	 * Delete sent proposed query
	 */
	public static void deleteSent(long pqId) throws DatabaseException {
		log.debug("deleteSent({})", pqId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			ProposedQuerySent pq = (ProposedQuerySent) session.load(ProposedQuerySent.class, pqId);
			session.delete(pq);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteSent: void");
	}

	/**
	 * Delete received proposed query
	 */
	public static void deleteReceived(long pqId) throws DatabaseException {
		log.debug("deleteReceived({})", pqId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			ProposedQueryReceived pq = (ProposedQueryReceived) session.load(ProposedQueryReceived.class, pqId);
			session.delete(pq);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteReceived: void");
	}

	/**
	 * Find by user
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findProposedQueriesUsersFrom(String me) throws DatabaseException {
		log.debug("findProposedQueriesUsersFrom({})", me);
		String qs = "select distinct(pq.from) from ProposedQueryReceived pq where pq.user=:me order by pq.from";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			List<String> ret = q.list();
			log.debug("findProposedQueriesUsersFrom: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Return a map users and number of unread proposed queries from them
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Long> findProposedQueriesUsersFromUnread(String me) throws DatabaseException {
		log.debug("findProposedQueriesUsersFromUnread({})", me);
		String qs = "select pq.from, count(pq.from) from ProposedQueryReceived pq " +
				"group by pq.from, pq.user, pq.seenDate having pq.seenDate is null and pq.user=:me";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			List<Object[]> list = q.list();
			Map<String, Long> ret = new HashMap<String, Long>();

			for (Object[] item : list) {
				ret.put((String) item[0], (Long) item[1]);
			}

			log.debug("findProposedQueriesUsersFromUnread: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find received by user
	 */
	@SuppressWarnings("unchecked")
	public static List<ProposedQueryReceived> findProposedQueryByMeFromUser(String me, String user) throws DatabaseException {
		log.debug("findProposedQueryByMeFromUser({})", user);
		String qs = "from ProposedQueryReceived pq where pq.from=:user and pq.user=:me order by pq.id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			q.setString("user", user);
			List<ProposedQueryReceived> ret = q.list();
			log.debug("findProposedQueryByMeFromUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find users whom sent an proposed query
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findProposedQuerySentUsersTo(String me) throws DatabaseException {
		log.debug("findSentUsersTo({})", me);
		String qs = "select distinct(pq.user) from ProposedQuerySent pq where pq.from=:me order by pq.user";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			List<String> ret = q.list();
			log.debug("findProposedQuerySentUsersTo: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Mark proposed as seen
	 */
	public static void markSeen(long pqId) throws DatabaseException {
		log.debug("markSeen({})", pqId);
		String qs = "update ProposedQueryReceived pq set pq.seenDate=:seenDate where pq.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", pqId);
			q.setCalendar("seenDate", Calendar.getInstance());
			q.executeUpdate();
			log.debug("markSeen: void");
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Mark proposed as accepted
	 */
	public static void markAccepted(long pqId) throws DatabaseException {
		log.debug("markAccepted({})", pqId);
		String qs = "update ProposedQueryReceived ps set ps.accepted=:accepted where ps.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", pqId);
			q.setBoolean("accepted", true);
			q.executeUpdate();
			log.debug("markAccepted: void");
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
