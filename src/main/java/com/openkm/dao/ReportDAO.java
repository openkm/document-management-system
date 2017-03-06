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
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.bean.Report;
import com.openkm.util.SecureStore;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ReportDAO {
	private static Logger log = LoggerFactory.getLogger(ReportDAO.class);

	private ReportDAO() {
	}

	/**
	 * Create
	 */
	public static long create(Report rp) throws DatabaseException {
		log.debug("create({})", rp);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Long id = (Long) session.save(rp);
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
	 * Create report from file
	 */
	public static long createFromFile(File repFile, String name, boolean active) throws DatabaseException, IOException {
		log.debug("createFromFile({}, {}, {})", new Object[]{repFile, name, active});
		Session session = null;
		Transaction tx = null;
		FileInputStream fis = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			fis = new FileInputStream(repFile);

			// Fill bean
			Report rp = new Report();
			rp.setName(name);
			rp.setFileName(repFile.getName());
			rp.setFileMime(MimeTypeConfig.mimeTypes.getContentType(repFile.getName()));
			rp.setFileContent(SecureStore.b64Encode(IOUtils.toByteArray(fis)));
			rp.setActive(active);

			Long id = (Long) session.save(rp);
			HibernateUtil.commit(tx);
			log.debug("createFromFile: {}", id);
			return id;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} catch (IOException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} finally {
			IOUtils.closeQuietly(fis);
			HibernateUtil.close(session);
		}
	}

	/**
	 * Update
	 */
	public static void update(Report rp) throws DatabaseException {
		log.debug("update({})", rp);
		String qs = "select rp.fileContent, rp.fileName, rp.fileMime from Report rp where rp.id=:id";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			if (rp.getFileContent() == null || rp.getFileContent().length() == 0) {
				Query q = session.createQuery(qs);
				q.setParameter("id", rp.getId());
				Object[] data = (Object[]) q.setMaxResults(1).uniqueResult();
				rp.setFileContent((String) data[0]);
				rp.setFileName((String) data[1]);
				rp.setFileMime((String) data[2]);
			}

			session.update(rp);
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
	public static void delete(long rpId) throws DatabaseException {
		log.debug("delete({})", rpId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Report rp = (Report) session.load(Report.class, rpId);
			session.delete(rp);
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
	public static Report findByPk(long rpId) throws DatabaseException {
		log.debug("findByPk({})", rpId);
		String qs = "from Report rp where rp.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", rpId);
			Report ret = (Report) q.setMaxResults(1).uniqueResult();
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
	public static List<Report> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from Report rp order by rp.name";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<Report> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
