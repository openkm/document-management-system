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
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Based on these ideas:
 *  - http://slemos.com.ar/java/2007/10/como-realizar-un-dao-sin-repetir-con-generics/
 *  - http://community.jboss.org/wiki/GenericDataAccessObjects
 *
 * @author pavila
 */
public abstract class GenericDAO<T, ID extends Serializable> {
	private static Logger log = LoggerFactory.getLogger(GenericDAO.class);
	private Class<T> persistentClass;
	private Session session;

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		ParameterizedType thisType = (ParameterizedType) getClass().getGenericSuperclass();
		this.persistentClass = (Class<T>) thisType.getActualTypeArguments()[0];
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	protected Session getSession() {
		return session;
	}

	/**
	 * Create
	 */
	@SuppressWarnings("unchecked")
	public ID create(T t) throws DatabaseException {
		log.debug("create({})", t);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			ID id = (ID) session.save(t);
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
	public void update(T t) throws DatabaseException {
		log.debug("update({})", t);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(t);
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
	@SuppressWarnings("unchecked")
	public void delete(ID id) throws DatabaseException {
		log.debug("delete({})", id);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			T t = (T) session.load(persistentClass, id);
			session.delete(t);
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
	 * Find by primary key
	 */
	@SuppressWarnings("unchecked")
	public T findByPk(ID id) throws DatabaseException {
		log.debug("findByPk({})", id);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			T ret = (T) session.load(persistentClass, id);
			Hibernate.initialize(ret);
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by primary key
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll() throws DatabaseException {
		log.debug("findAll()");
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery("from " + persistentClass.getName() + " x");
			List<T> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
