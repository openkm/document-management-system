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
import com.openkm.extension.dao.bean.MessageReceived;
import com.openkm.extension.dao.bean.MessageSent;
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

public class MessageDAO {
	private static Logger log = LoggerFactory.getLogger(MessageDAO.class);

	private MessageDAO() {
	}

	/**
	 * Send message
	 */
	public static void send(String from, String to, String user, String subject, String content) throws
			DatabaseException {
		log.debug("send({}, {}, {}, {}, {})", new Object[]{from, to, user, subject, content});
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			MessageSent msgSent = new MessageSent();
			msgSent.setFrom(from);
			msgSent.setTo(to);
			msgSent.setUser(user);
			msgSent.setSubject(subject);
			msgSent.setContent(content);
			msgSent.setSentDate(Calendar.getInstance());
			session.save(msgSent);

			MessageReceived msgReceived = new MessageReceived();
			msgReceived.setFrom(from);
			msgReceived.setTo(to);
			msgReceived.setUser(user);
			msgReceived.setSubject(subject);
			msgReceived.setContent(content);
			msgReceived.setSentDate(Calendar.getInstance());
			session.save(msgReceived);

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
	 * Delete sent message
	 */
	public static void deleteSent(long msgId) throws DatabaseException {
		log.debug("deleteSent({})", msgId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			MessageSent msg = (MessageSent) session.load(MessageSent.class, msgId);
			session.delete(msg);
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
	 * Delete received message
	 */
	public static void deleteReceived(long msgId) throws DatabaseException {
		log.debug("deleteReceived({})", msgId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			MessageReceived msg = (MessageReceived) session.load(MessageReceived.class, msgId);
			session.delete(msg);
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
	 * Find users whom sent an message
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findSentUsersTo(String me) throws DatabaseException {
		log.debug("findSentUsersTo({})", me);
		String qs = "select distinct(msg.user) from MessageSent msg where msg.from=:me order by msg.user";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			List<String> ret = q.list();
			log.debug("findSentUsersTo: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find sent from me to user
	 */
	@SuppressWarnings("unchecked")
	public static List<MessageSent> findSentFromMeToUser(String me, String user) throws DatabaseException {
		log.debug("findSentFromMeToUser({}, {})", me, user);
		String qs = "from MessageSent msg where msg.from=:me and msg.user=:user order by msg.id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			q.setString("user", user);
			List<MessageSent> ret = q.list();
			log.debug("findSentFromMeToUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find users who sent an message to me
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findReceivedUsersFrom(String me) throws DatabaseException {
		log.debug("findReceivedUsersFrom({})", me);
		String qs = "select distinct(msg.from) from MessageReceived msg where msg.user=:me order by msg.from";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			List<String> ret = q.list();
			log.debug("findReceivedUsersFrom: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Return a map users and number of unread received messages from them
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Long> findReceivedUsersFromUnread(String me) throws DatabaseException {
		log.debug("findReceivedUsersFromUnread({})", me);
		String qs = "select msg.from, count(msg.from) from MessageReceived msg " +
				"group by msg.from,msg.user,msg.seenDate having msg.seenDate is null and msg.user=:me";
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

			log.debug("findReceivedUsersFromUnread: {}", ret);
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
	public static List<MessageReceived> findReceivedByMeFromUser(String me, String user) throws DatabaseException {
		log.debug("findReceivedByMeFromUser({})", user);
		String qs = "from MessageReceived msg where msg.from=:user and msg.user=:me order by msg.id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("me", me);
			q.setString("user", user);
			List<MessageReceived> ret = q.list();
			log.debug("findReceivedByMeFromUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Mark message as seen
	 */
	public static void markSeen(long msgId) throws DatabaseException {
		log.debug("markSeen({})", msgId);
		String qs = "update MessageReceived msg set msg.seenDate=:seenDate where msg.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", msgId);
			q.setCalendar("seenDate", Calendar.getInstance());
			q.executeUpdate();
			log.debug("markSeen: void");
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
