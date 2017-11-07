/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2015  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PUomOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Omr;

public class OmrDAO {
	private static Logger log = LoggerFactory.getLogger(OmrDAO.class);
	private static OmrDAO single = new OmrDAO();

	private OmrDAO() {
	}

	public static OmrDAO getInstance() {
		return single;
	}

	/**
	 * Create
	 */
	public long create(Omr om) throws DatabaseException {
		log.debug("create({})", om);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Long id = (Long) session.save(om);
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
	 * Update template
	 */
	public void updateTemplate(Omr om) throws DatabaseException {
		log.debug("updateTemplate({})", om);
		String qs = "select om.templateFileContent, om.templateFileName, templateFileMime, " +
				"om.ascFileContent, om.ascFileName, ascFileMime, " +
				"om.configFileContent, om.configFileName, configFileMime, " +
				"om.fieldsFileContent, om.fieldsFileName, fieldsFileMime " +
				"from Omr om where om.id=:id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			Query q = session.createQuery(qs);
			q.setParameter("id", om.getId());
			Object[] data = (Object[]) q.setMaxResults(1).uniqueResult();

			if (om.getTemplateFileContent() == null || om.getTemplateFileContent().length == 0) {
				om.setTemplateFilContent((byte[]) data[0]);
				om.setTemplateFileName((String) data[1]);
				om.setTemplateFileMime((String) data[2]);
			}

			if (om.getAscFileContent() == null || om.getAscFileContent().length == 0) {
				om.setAscFileContent((byte[]) data[3]);
				om.setAscFileName((String) data[4]);
				om.setAscFileMime((String) data[5]);
			}

			if (om.getConfigFileContent() == null || om.getConfigFileContent().length == 0) {
				om.setConfigFileContent((byte[]) data[6]);
				om.setConfigFileName((String) data[7]);
				om.setConfigFileMime((String) data[8]);
			}

			if (om.getFieldsFileContent() == null || om.getFieldsFileContent().length == 0) {
				om.setFieldsFileContent((byte[]) data[9]);
				om.setFieldsFileName((String) data[10]);
				om.setFieldsFileMime((String) data[11]);
			}

			session.update(om);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("updateTemplate: void");
	}

	/**
	 * Update 
	 */
	public void update(Omr om) throws DatabaseException {
		log.debug("update({})", om);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(om);
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
	public void delete(long omId) throws DatabaseException {
		log.debug("delete({})", omId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Omr om = (Omr) session.load(Omr.class, omId);
			session.delete(om);
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
	 * Find by pk
	 */
	public Omr findByPk(long omId) throws DatabaseException {
		log.debug("findByPk({})", omId);
		String qs = "from Omr om where om.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", omId);
			Omr ret = (Omr) q.setMaxResults(1).uniqueResult();
			initializeOMR(ret);
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by pk
	 */
	@SuppressWarnings("unchecked")
	public List<Omr> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from Omr om order by om.name";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<Omr> ret = q.list();
			initializeOMR(ret);
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find all active
	 */
	@SuppressWarnings("unchecked")
	public List<Omr> findAllActive() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from Omr om where om.active=:active order by om.name";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs).setCacheable(true);
			q.setBoolean("active", true);
			List<Omr> ret = q.list();
			HibernateUtil.commit(tx);
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
	 * getProperties
	 */
	@SuppressWarnings("unchecked")
	public List<String> getProperties(long omId) throws DatabaseException {
		log.debug("getProperties({})", omId);
		String qs = "select om.properties from Omr om where om.id=:id";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", omId);
			List<String> ret = q.list();
			log.debug("getProperties: {}", ret);
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
	private void initializeOMR(List<Omr> omrList) {
		for (Omr oTemplate : omrList) {
			initializeOMR(oTemplate);
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initializeOMR(Omr omr) {
		if (omr != null) {
			Hibernate.initialize(omr);
			Hibernate.initialize(omr.getProperties());
		}
	}
}
