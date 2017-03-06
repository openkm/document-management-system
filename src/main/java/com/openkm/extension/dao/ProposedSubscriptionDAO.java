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
import com.openkm.extension.dao.bean.ProposedSubscriptionReceived;
import com.openkm.extension.dao.bean.ProposedSubscriptionSent;
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

public class ProposedSubscriptionDAO {
	private static Logger log = LoggerFactory.getLogger(ProposedSubscriptionDAO.class);

	private ProposedSubscriptionDAO() {
	}

	/**
	 * Send proposed subscription
	 */
	public static void send(String from, String to, String user, String nodeUuid, String type, String comment) throws
			DatabaseException {
		log.debug("send({}, {}, {}, {}, {})", new Object[]{from, to, user, nodeUuid, comment});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			ProposedSubscriptionSent psSent = new ProposedSubscriptionSent();
			psSent.setFrom(from);
			psSent.setTo(to);
			psSent.setUser(user);
			psSent.setNode(nodeUuid);
			psSent.setType(type);
			psSent.setComment(comment);
			psSent.setSentDate(Calendar.getInstance());
			session.save(psSent);

			ProposedSubscriptionReceived psReceived = new ProposedSubscriptionReceived();
			psReceived.setFrom(from);
			psReceived.setTo(to);
			psReceived.setUser(user);
			psReceived.setNode(nodeUuid);
			psReceived.setType(type);
			psReceived.setComment(comment);
			psReceived.setSentDate(Calendar.getInstance());
			session.save(psReceived);

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
	 * Delete sent proposed subscription
	 */
	public static void deleteSent(int psId) throws DatabaseException {
		log.debug("deleteSent({})", psId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			ProposedSubscriptionSent ps = (ProposedSubscriptionSent) session.load(ProposedSubscriptionSent.class, psId);
			session.delete(ps);
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
	 * Delete received proposed subscription
	 */
	public static void deleteReceived(int psId) throws DatabaseException {
		log.debug("deleteReceived({})", psId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			ProposedSubscriptionReceived ps = (ProposedSubscriptionReceived) session.load(ProposedSubscriptionReceived.class, psId);
			session.delete(ps);
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
	 * Find sent from me to user
	 * @throws RepositoryException
	 */
	@SuppressWarnings("unchecked")
	public static List<ProposedSubscriptionSent> findSentProposedSubscriptionFromMeToUser(String me, String user)
			throws DatabaseException, RepositoryException {
		log.debug("findSentProposedSubscriptionFromMeToUser({}, {})", me, user);
		String qs = "from ProposedSubscriptionSent ps where ps.from=:me and ps.user=:user order by ps.id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			q.setString("user", user);
			List<ProposedSubscriptionSent> ret = q.list();
			log.debug("findSentProposedSubscriptionFromMeToUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by user
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findProposedSubscriptionsUsersFrom(String me) throws DatabaseException {
		log.debug("findProposedSubscriptionsUsersFrom({})", me);
		String qs = "select distinct(ps.from) from ProposedSubscriptionReceived ps where ps.user=:me order by ps.from";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			List<String> ret = q.list();
			log.debug("findProposedSubscriptionsUsersFrom: {}", ret);
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
	public static Map<String, Long> findProposedSubscriptionsUsersFromUnread(String me) throws
			DatabaseException {
		log.debug("findProposedSubscriptionsUsersFromUnread({})", me);
		String qs = "select ps.from, count(ps.from) from ProposedSubscriptionReceived ps " +
				"group by ps.from, ps.user, ps.seenDate having ps.seenDate is null and ps.user=:me";
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

			log.debug("findProposedSubscriptionsUsersFromUnread: {}", ret);
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
	public static List<ProposedSubscriptionReceived> findProposedSubscriptionByMeFromUser(String me, String user)
			throws DatabaseException, RepositoryException {
		log.debug("findProposedSubscriptionByMeFromUser({}, {})", me, user);
		String qs = "from ProposedSubscriptionReceived ps where ps.from=:user and ps.user=:me order by ps.id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			q.setString("user", user);
			List<ProposedSubscriptionReceived> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("findProposedSubscriptionByMeFromUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find users whom sent an proposed query
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findProposedSubscriptionSentUsersTo(String me) throws DatabaseException {
		log.debug("findProposedSubscriptionSentUsersTo({})", me);
		String qs = "select distinct(ps.user) from ProposedSubscriptionSent ps where ps.from=:me order by ps.user";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			List<String> ret = q.list();
			log.debug("findProposedSubscriptionSentUsersTo: {}", ret);
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
	public static void markSeen(int psId) throws DatabaseException {
		log.debug("markSeen({})", psId);
		String qs = "update ProposedSubscriptionReceived ps set ps.seenDate=:seenDate where ps.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", psId);
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
	public static void markAccepted(int psId) throws DatabaseException {
		log.debug("markAccepted({})", psId);
		String qs = "update ProposedSubscriptionReceived ps set ps.accepted=:accepted where ps.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", psId);
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
