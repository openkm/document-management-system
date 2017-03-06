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
import com.openkm.dao.bean.PendingTask;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * PendingTaskDAO
 *
 * @author pavila
 *
 */
public class PendingTaskDAO extends GenericDAO<PendingTask, Long> {
	private static Logger log = LoggerFactory.getLogger(PendingTaskDAO.class);
	private static PendingTaskDAO single = new PendingTaskDAO();

	private PendingTaskDAO() {
	}

	public static PendingTaskDAO getInstance() {
		return single;
	}

	/**
	 * Find by content and name
	 */
	@SuppressWarnings("unchecked")
	public List<PendingTask> findByTask(String task) throws DatabaseException {
		log.debug("findByTask({})", task);
		String qs = "from PendingTask pt where pt.task=:task order by pt.created";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("task", task);
			List<PendingTask> ret = q.list();
			log.debug("findByTask: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}