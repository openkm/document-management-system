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
import com.openkm.dao.bean.DatabaseMetadataSequence;
import com.openkm.dao.bean.DatabaseMetadataType;
import com.openkm.dao.bean.DatabaseMetadataValue;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ExtensionMetadataDAO
 *
 * @author pavila
 */
public class DatabaseMetadataDAO {
	private static Logger log = LoggerFactory.getLogger(DatabaseMetadataDAO.class);

	private DatabaseMetadataDAO() {
	}

	/**
	 * Create
	 */
	public static long createValue(DatabaseMetadataValue dmv) throws DatabaseException {
		log.debug("createValue({})", dmv);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Long id = (Long) session.save(dmv);
			HibernateUtil.commit(tx);
			log.debug("createValue: {}", id);
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
	public static void updateValue(DatabaseMetadataValue dmv) throws DatabaseException {
		log.debug("updateValue({})", dmv);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(dmv);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("updateValue: void");
	}

	/**
	 * Delete
	 */
	public static void deleteValue(long dmvId) throws DatabaseException {
		log.debug("deleteValue({})", dmvId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			DatabaseMetadataValue dmv = (DatabaseMetadataValue) session.load(DatabaseMetadataValue.class, dmvId);
			session.delete(dmv);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteValue: void");
	}

	/**
	 * Find all wiki pages
	 */
	@SuppressWarnings("unchecked")
	public static List<DatabaseMetadataValue> findAllValues(String table) throws DatabaseException {
		log.debug("findAllValues({})", table);
		String qs = "from DatabaseMetadataValue dmv where dmv.table=:table order by dmv.id asc";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("table", table);
			List<DatabaseMetadataValue> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("findAllValues: {}", ret);
			return ret;
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
	public static DatabaseMetadataValue findValueByPk(String table, long id) throws DatabaseException {
		log.debug("findValueByPk({}, {})", table, id);
		String qs = "from DatabaseMetadataValue dmv where dmv.table=:table and dmv.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("table", table);
			q.setLong("id", id);
			DatabaseMetadataValue ret = (DatabaseMetadataValue) q.setMaxResults(1).uniqueResult();
			log.debug("findValueByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Execute update
	 */
	public static int executeValueUpdate(String query) throws DatabaseException {
		log.debug("executeValueUpdate({})", query);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(query);
			int ret = q.executeUpdate();
			HibernateUtil.commit(tx);
			log.debug("executeValueUpdate: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Execute query
	 */
	@SuppressWarnings("unchecked")
	public static List<DatabaseMetadataValue> executeValueQuery(String query) throws DatabaseException {
		log.debug("executeValueQuery({})", query);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(query);
			List<DatabaseMetadataValue> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("executeValueQuery: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Execute query
	 */
	public static DatabaseMetadataValue executeValueQueryUnique(String query) throws DatabaseException {
		log.debug("executeValueQueryUnique({})", query);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(query);
			DatabaseMetadataValue ret = (DatabaseMetadataValue) q.uniqueResult();
			HibernateUtil.commit(tx);
			log.debug("executeValueQueryUnique: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Execute query
	 */
	public static List<DatabaseMetadataValue[]> executeMultiValueQuery(String query) throws DatabaseException {
		log.debug("executeMultiValueQuery({})", query);
		List<DatabaseMetadataValue[]> ret = new ArrayList<DatabaseMetadataValue[]>();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(query);

			for (Object obj : q.list()) {
				if (obj instanceof Object[]) {
					Object[] objAry = (Object[]) obj;
					DatabaseMetadataValue[] tmp = new DatabaseMetadataValue[objAry.length];

					for (int i = 0; i < objAry.length; i++) {
						if (objAry[i] instanceof DatabaseMetadataValue) {
							tmp[i] = (DatabaseMetadataValue) objAry[i];
						}
					}

					ret.add(tmp);
				} else if (obj instanceof DatabaseMetadataValue) {
					DatabaseMetadataValue[] tmp = new DatabaseMetadataValue[1];
					tmp[0] = (DatabaseMetadataValue) obj;
					ret.add(tmp);
				}
			}

			HibernateUtil.commit(tx);
			log.debug("executeMultiValueQuery: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Create
	 */
	public static long createType(DatabaseMetadataType dmt) throws DatabaseException {
		log.debug("createType({})", dmt);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Long id = (Long) session.save(dmt);
			HibernateUtil.commit(tx);
			log.debug("createType: {}", id);
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
	public static void updateType(DatabaseMetadataType dmt) throws DatabaseException {
		log.debug("updateType({})", dmt);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(dmt);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("updateType: void");
	}

	/**
	 * Delete
	 */
	public static void deleteType(long dmtId) throws DatabaseException {
		log.debug("deleteType({})", dmtId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			DatabaseMetadataType emt = (DatabaseMetadataType) session.load(DatabaseMetadataType.class, dmtId);
			session.delete(emt);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteType: void");
	}

	/**
	 * Find all wiki pages
	 */
	@SuppressWarnings("unchecked")
	public static List<DatabaseMetadataType> findAllTypes(String table) throws DatabaseException {
		log.debug("findAllTypes({})", table);
		String qs = "from DatabaseMetadataType dmt where dmt.table=:table order by dmt.id asc";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("table", table);
			List<DatabaseMetadataType> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("findAllTypes: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get next sequence number
	 */
	public static long getNextSequenceValue(String table, String column) throws DatabaseException {
		log.debug("getNextSequenceValue({}, {})", table, column);
		String qs = "from DatabaseMetadataSequence dms where dms.table=:table and dms.column=:column";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("table", table);
			q.setString("column", column);
			DatabaseMetadataSequence dms = (DatabaseMetadataSequence) q.setMaxResults(1).uniqueResult();

			if (dms != null) {
				// Update already created sequence
				dms.setValue(dms.getValue() + 1);
				session.update(dms);
			} else {
				// First sequence use: starts with 1
				dms = new DatabaseMetadataSequence();
				dms.setTable(table);
				dms.setColumn(column);
				dms.setValue(1);
				session.save(dms);
			}

			HibernateUtil.commit(tx);
			log.debug("getNextSequenceValue: {}", dms.getValue());
			return dms.getValue();
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
