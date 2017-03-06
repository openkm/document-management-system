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

package com.openkm.extension.dao;

import com.openkm.core.DatabaseException;
import com.openkm.dao.HibernateUtil;
import com.openkm.extension.dao.bean.WikiPage;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

/**
 * ExtensionDAO
 *
 * @author pavila
 */
public class WikiPageDAO {
	private static Logger log = LoggerFactory.getLogger(WikiPageDAO.class);

	private WikiPageDAO() {
	}

	/**
	 * Create
	 */
	public static long create(WikiPage wkp) throws DatabaseException {
		log.debug("create({})", wkp);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Long id = (Long) session.save(wkp);
			HibernateUtil.commit(tx);
			log.debug("create: {}" + id);
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
	public static void update(WikiPage wkp) throws DatabaseException {
		log.debug("update({})", wkp);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(wkp);
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
	 * Find all wiki pages
	 */
	@SuppressWarnings("unchecked")
	public static List<WikiPage> findAll() throws DatabaseException {
		log.debug("findAll({})");
		String qs = "select wkp from WikiPage wkp where wkp.deleted=false order by wkp.date desc";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<WikiPage> ret = q.list();

			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find all wiki pages by title
	 */
	@SuppressWarnings("unchecked")
	public static List<WikiPage> findAllByTitle(String title) throws DatabaseException {
		log.debug("findAllByTitle({})");
		String qs = "select wkp from WikiPage wkp where wkp.title=:title and wkp.deleted=false order by wkp.date desc";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("title", title);
			List<WikiPage> ret = q.list();

			log.debug("findAllByTitle: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find all wiki pages by title
	 */
	@SuppressWarnings("unchecked")
	public static List<WikiPage> findAllHistoricByTitle(String title) throws DatabaseException {
		log.debug("findAllHistoricByTitle({})");
		String qs = "select wkp from WikiPage wkp where wkp.title=:title order by wkp.date desc";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("title", title);
			List<WikiPage> ret = q.list();

			log.debug("findAllHistoricByTitle: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find all latest by title filtered
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findAllLatestByTitleFiltered(String filter) throws DatabaseException {
		log.debug("findAllLatestByTitleFiltered({})", filter);
		String qs = "select distinct(wp.title) from WikiPage wp where wp.title like '%' || :filter || '%' and wp.deleted=false";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("filter", filter);
			List<String> ret = q.list();
			log.debug("findLatestByTitle: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find latest by title
	 */
	public static WikiPage findLatestByTitle(String title) throws DatabaseException {
		log.debug("findLatestByTitle({})", title);
		String qs = "from WikiPage wp where wp.title=:title and wp.deleted=false order by wp.date desc";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("title", title);
			WikiPage ret = (WikiPage) q.setMaxResults(1).uniqueResult();
			log.debug("findLatestByTitle: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find latest by node
	 */
	public static WikiPage findLatestByNode(String uuid) throws DatabaseException {
		log.debug("findLatestByNode({})", uuid);
		String qs = "from WikiPage wp where wp.node=:uuid and wp.deleted=false order by wp.date desc";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("uuid", uuid);
			WikiPage ret = (WikiPage) q.setMaxResults(1).uniqueResult();
			log.debug("findLatestByNode: {}", ret);
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
	public static WikiPage findByPk(long id) throws DatabaseException {
		log.debug("findByPk({})", id);
		String qs = "from WikiPage wp where wp.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", id);
			WikiPage ret = (WikiPage) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * lock
	 */
	public static synchronized boolean lock(WikiPage wikiPage, String user) throws DatabaseException {
		// Only can locked last page
		WikiPage actualWikiPage = findLatestByTitle(wikiPage.getTitle());

		if (actualWikiPage != null && actualWikiPage.getId() == wikiPage.getId() && (actualWikiPage.getLockUser() == null || actualWikiPage.getLockUser().equals(""))) {
			wikiPage.setLockUser(user);
			update(wikiPage);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * unlock
	 */
	public static synchronized boolean unlock(WikiPage wikiPage, String user) throws DatabaseException {
		// Only can locked last page
		WikiPage actualWikiPage = findLatestByTitle(wikiPage.getTitle());

		if (actualWikiPage != null && actualWikiPage.getId() == wikiPage.getId() && (actualWikiPage.getLockUser() != null && actualWikiPage.getLockUser().equals(user))) {
			wikiPage.setLockUser(null);
			update(wikiPage);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * createNewWikiPage
	 */
	public static synchronized WikiPage createNewWikiPage(WikiPage wikiPage) throws DatabaseException {
		WikiPage latestWikiPage = findLatestByTitle(wikiPage.getTitle());

		if (latestWikiPage == null) {
			wikiPage.setDate(Calendar.getInstance());
			long id = create(wikiPage);
			return findByPk(id);
		} else {
			return latestWikiPage;
		}
	}

	/**
	 * updateWikiPage
	 */
	public static synchronized WikiPage updateWikiPage(WikiPage wikiPage) throws DatabaseException {
		// To call update wiki page before we've executed on UI the lock
		WikiPage actualWikiPage = findLatestByTitle(wikiPage.getTitle());

		if (actualWikiPage != null && actualWikiPage.getId() == wikiPage.getId() && isLockedByUser(actualWikiPage, wikiPage.getUser())) {
			wikiPage.setLockUser(null); // Unlock new register
			wikiPage.setDate(Calendar.getInstance());
			long id = create(wikiPage);
			actualWikiPage.setLockUser(null); // Unlock older register we've updated
			update(actualWikiPage);
			return findByPk(id);
		} else {
			return null;
		}
	}

	/**
	 * restoreWikiPage
	 */
	public static synchronized WikiPage restoreWikiPage(WikiPage wikiPage) throws DatabaseException {
		// To call update wiki page before we've executed on UI the lock
		WikiPage actualWikiPage = findLatestByTitle(wikiPage.getTitle());

		if (actualWikiPage != null && isLockedByUser(actualWikiPage, wikiPage.getUser())) {
			wikiPage.setLockUser(null); // Unlock new register
			wikiPage.setDate(Calendar.getInstance());
			wikiPage.setDeleted(false); // Change to false because could be restored from deleted wiki
			long id = create(wikiPage);
			actualWikiPage.setLockUser(null); // Unlock older register we've updated
			update(actualWikiPage);
			return findByPk(id);
		} else {
			return null;
		}
	}

	/**
	 * deleteWikiPage
	 */
	public static synchronized boolean deleteWikiPage(WikiPage wikiPage) throws DatabaseException {
		// To call update wiki page before we've executed on UI the lock
		WikiPage actualWikiPage = findLatestByTitle(wikiPage.getTitle());

		if (actualWikiPage != null && actualWikiPage.getId() == wikiPage.getId() &&
				(actualWikiPage.getLockUser() == null || actualWikiPage.getLockUser().equals(""))) {
			for (WikiPage wikiPageToDelete : findAllByTitle(actualWikiPage.getTitle())) {
				wikiPageToDelete.setDeleted(true);
				update(wikiPageToDelete);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * isLocked
	 */
	private static boolean isLockedByUser(WikiPage wikiPage, String user) {
		if (wikiPage.getLockUser() != null && wikiPage.getLockUser().equals(user)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Remove wiki pages by parent node
	 */
	public static void purgeWikiPagesByNode(String nodeUuid) throws DatabaseException {
		log.debug("purgeWikiPagesByNode({})", nodeUuid);
		String qs = "delete from WikiPage wp where wp.node=:uuid";
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			Query q = session.createQuery(qs);
			q.setString("uuid", nodeUuid);
			q.executeUpdate();

			HibernateUtil.commit(tx);
			log.debug("purgeWikiPagesByNode: void");
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}