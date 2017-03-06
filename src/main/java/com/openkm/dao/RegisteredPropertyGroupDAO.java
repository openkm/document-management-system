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
import com.openkm.dao.bean.RegisteredPropertyGroup;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map.Entry;

public class RegisteredPropertyGroupDAO extends GenericDAO<RegisteredPropertyGroup, String> {
	private static Logger log = LoggerFactory.getLogger(RegisteredPropertyGroupDAO.class);
	private static RegisteredPropertyGroupDAO single = new RegisteredPropertyGroupDAO();

	private RegisteredPropertyGroupDAO() {
	}

	public static RegisteredPropertyGroupDAO getInstance() {
		return single;
	}

	/**
	 * Create or update
	 */
	public void createOrUpdate(RegisteredPropertyGroup rpg) throws DatabaseException {
		log.debug("create({})", rpg);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(rpg);
			HibernateUtil.commit(tx);
			log.debug("create: void");
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
	public RegisteredPropertyGroup findByPk(String grpName) throws DatabaseException {
		log.debug("findByPk({})", grpName);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			RegisteredPropertyGroup ret = (RegisteredPropertyGroup) session.load(RegisteredPropertyGroup.class, grpName);
			initialize(ret);
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find all property groups
	 */
	@SuppressWarnings("unchecked")
	public List<RegisteredPropertyGroup> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from RegisteredPropertyGroup rpg";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<RegisteredPropertyGroup> ret = q.list();
			initialize(ret);
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initialize(RegisteredPropertyGroup propGroup) {
		if (propGroup != null) {
			Hibernate.initialize(propGroup);

			for (Entry<String, String> entry : propGroup.getProperties().entrySet()) {
				Hibernate.initialize(entry);
			}
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initialize(List<RegisteredPropertyGroup> propGroupList) {
		for (RegisteredPropertyGroup propGroup : propGroupList) {
			initialize(propGroup);
		}
	}
}
