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
import com.openkm.dao.bean.cache.UserNodeKeywords;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserNodeKeywordsDAO {
	private static Logger log = LoggerFactory.getLogger(UserNodeKeywordsDAO.class);

	private UserNodeKeywordsDAO() {
	}

	/**
	 * Remove
	 */
	public static void remove(int id) throws DatabaseException {
		log.debug("remove({})", id);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			UserNodeKeywords unk = (UserNodeKeywords) session.load(UserNodeKeywords.class, id);
			session.delete(unk);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("remove: void");
	}

	/**
	 * Update user items
	 */
	public static void create(UserNodeKeywords unk) throws DatabaseException {
		log.debug("update({})", unk);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(unk);
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
	 * Find by user
	 */
	@SuppressWarnings("unchecked")
	public static List<UserNodeKeywords> findByUser(String user) throws DatabaseException {
		log.debug("findByUser({})", user);
		String qs = "from UserNodeKeywords unk where unk.user=:user";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("user", user);
			List<UserNodeKeywords> ret = q.list();

			for (UserNodeKeywords unk : ret) {
				Hibernate.initialize(unk.getKeywords());
			}

			log.debug("findByUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find users
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findUsers() throws DatabaseException {
		log.debug("findUsers()");
		String qs = "select distinct unk.user from UserNodeKeywords unk";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<String> ret = q.list();
			log.debug("findUsers: {}", ret);
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
	public static List<UserNodeKeywords> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from UserNodeKeywords";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<UserNodeKeywords> ret = q.list();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Empty database
	 */
	@SuppressWarnings("unchecked")
	public static void clean() throws DatabaseException {
		log.debug("clean()");
		String qs = "from UserNodeKeywords";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			List<UserNodeKeywords> ret = session.createQuery(qs).list();
			for (UserNodeKeywords unk : ret) {
				session.delete(unk);
			}
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("clean: void");
	}
}
