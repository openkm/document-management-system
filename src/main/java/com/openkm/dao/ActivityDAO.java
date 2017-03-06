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
import com.openkm.dao.bean.Activity;
import com.openkm.dao.bean.ActivityFilter;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

public class ActivityDAO {
	private static Logger log = LoggerFactory.getLogger(ActivityDAO.class);

	private ActivityDAO() {
	}

	/**
	 * Create activity
	 */
	public static void create(Activity activity) throws DatabaseException {
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(activity);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by filter
	 */
	@SuppressWarnings("unchecked")
	public static List<Activity> findByFilter(ActivityFilter filter) throws DatabaseException {
		log.debug("findByFilter({})", filter);
		String qs = "from Activity a where a.date between :begin and :end ";

		if (filter.getUser() != null && !filter.getUser().equals(""))
			qs += "and a.user=:user ";
		if (filter.getAction() != null && !filter.getAction().equals(""))
			qs += "and a.action=:action ";
		if (filter.getItem() != null && !filter.getItem().equals("")) {
			qs += "and a.item=:item ";
		}

		qs += "order by a.date";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setCalendar("begin", filter.getBegin());
			q.setCalendar("end", filter.getEnd());

			if (filter.getUser() != null && !filter.getUser().equals(""))
				q.setString("user", filter.getUser());
			if (filter.getAction() != null && !filter.getAction().equals(""))
				q.setString("action", filter.getAction());
			if (filter.getItem() != null && !filter.getItem().equals(""))
				q.setString("item", filter.getItem());

			List<Activity> ret = q.list();
			log.debug("findByFilter: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by filter
	 */
	@SuppressWarnings("unchecked")
	public static List<Activity> findByFilterByItem(ActivityFilter filter) throws DatabaseException {
		log.debug("findByFilter({})", filter);
		String qs = "from Activity a where a.item=:item ";
		if (filter.getAction() != null && !filter.getAction().equals(""))
			qs += "and a.action=:action ";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("item", filter.getItem());
			if (filter.getAction() != null && !filter.getAction().equals(""))
				q.setString("action", filter.getAction());

			List<Activity> ret = q.list();
			log.debug("findByFilterByItem: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get activity date
	 */
	public static Calendar getActivityDate(String user, String action, String item) throws
			DatabaseException {
		log.debug("getActivityDate({}, {}, {})", new Object[]{user, action, item});
		String qsAct = "select max(a.date) from Activity a " +
				"where a.user=:user and a.action=:action and a.item=:item";
		String qsNoAct = "select max(a.date) from Activity a " +
				"where (a.action='CREATE_DOCUMENT' or a.action='CHECKIN_DOCUMENT') and a.item=:item";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = null;

			if (action != null) {
				q = session.createQuery(qsAct);
				q.setString("user", user);
				q.setString("action", action);
				q.setString("item", item);
			} else {
				q = session.createQuery(qsNoAct);
				q.setString("item", item);
			}

			Calendar ret = (Calendar) q.setMaxResults(1).uniqueResult();

			if (ret == null) {
				// May be the document has been moved or renamed? 
				ret = Calendar.getInstance();
			}

			log.debug("getActivityDate: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
