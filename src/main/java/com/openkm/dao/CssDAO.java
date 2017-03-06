/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.dao;

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Css;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * CssDAO
 *
 * @author jllort
 *
 */
public class CssDAO extends GenericDAO<Css, Long> {
	private static Logger log = LoggerFactory.getLogger(CssDAO.class);
	private static CssDAO single = new CssDAO();

	private CssDAO() {
	}

	public static CssDAO getInstance() {
		return single;
	}

	/**
	 * Find all styles
	 */
	@SuppressWarnings("unchecked")
	public List<Css> findAll(boolean filterByActive) throws DatabaseException {
		log.debug("findAll({})", filterByActive);
		String qs = "from Css c " + (filterByActive ? "where c.active=:active" : "") + " order by c.context, c.name asc";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs).setCacheable(true);

			if (filterByActive) {
				q.setBoolean("active", true);
			}

			List<Css> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by content and name
	 */
	public Css findByContextAndName(String context, String name) throws DatabaseException {
		log.debug("findByContextAndName({},{})", context, name);
		String qs = "from Css c where c.context=:context and c.name=:name and c.active=:active";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("context", context);
			q.setString("name", name);
			q.setBoolean("active", true);
			Css ret = (Css) q.setMaxResults(1).uniqueResult();
			log.debug("findByContextAndName: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}