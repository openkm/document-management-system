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
import com.openkm.dao.bean.Profiling;
import com.openkm.dao.bean.ProfilingStats;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProfilingDAO {
	private static Logger log = LoggerFactory.getLogger(ProfilingDAO.class);

	private ProfilingDAO() {
	}

	/**
	 * Create activity
	 */
	public static void create(Profiling profiling) throws DatabaseException {
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(profiling);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Clear table
	 */
	public static void clear() throws DatabaseException {
		String qs = "delete Profiling";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.executeUpdate();
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get statistics
	 */
	@SuppressWarnings("unchecked")
	public static List<ProfilingStats> getStatistics() throws DatabaseException {
		log.debug("getStatistics()");
		String qsClazzes = "select distinct prl.clazz, prl.method from Profiling prl order by prl.clazz, prl.method";
		String qsStats = "select max(prl.time), min(prl.time), avg(prl.time), sum(prl.time), count(prl.time) "
				+ "from Profiling prl where prl.clazz=:clazz and prl.method=:method";
		List<ProfilingStats> ret = new ArrayList<ProfilingStats>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query qClazzes = session.createQuery(qsClazzes);

			for (Object[] result : (List<Object[]>) qClazzes.list()) {
				Query qStats = session.createQuery(qsStats);
				qStats.setString("clazz", (String) result[0]);
				qStats.setString("method", (String) result[1]);
				Object[] stats = (Object[]) qStats.setMaxResults(1).uniqueResult();

				ProfilingStats ps = new ProfilingStats();
				ps.setClazz((String) result[0]);
				ps.setMethod((String) result[1]);
				ps.setMaxTime((Long) stats[0]);
				ps.setMinTime((Long) stats[1]);
				ps.setAvgTime(Math.round((Double) stats[2]));
				ps.setTotalTime((Long) stats[3]);
				ps.setExecutionCount((Long) stats[4]);

				ret.add(ps);
			}

			HibernateUtil.commit(tx);
			log.debug("getStatistics: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by class and method
	 */
	@SuppressWarnings("unchecked")
	public static List<Profiling> findByClazzMethod(String clazz, String method) throws DatabaseException {
		log.debug("findByClazzMethod()");
		String qs = "from Profiling prl where prl.clazz=:clazz and prl.method=:method";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("clazz", clazz);
			q.setString("method", method);
			List<Profiling> ret = q.list();

			HibernateUtil.commit(tx);
			log.debug("findByClazzMethod: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
