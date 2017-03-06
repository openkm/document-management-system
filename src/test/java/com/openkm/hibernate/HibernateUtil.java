/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2017  Paco Avila & Josep Llort
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
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.hibernate;

import org.apache.lucene.util.Version;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Show SQL => Logger.getLogger("org.hibernate.SQL").setThreshold(Level.INFO);
 * JBPM Integration => org.jbpm.db.JbpmSessionFactory
 * 
 * @author pavila
 */
public class HibernateUtil {
	private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);
	private static SessionFactory sessionFactory;
	public static Version LUCENE_VERSION = Version.LUCENE_31;
	
	/**
	 * Disable constructor to guaranty a single instance
	 */
	private HibernateUtil() {}
	
	/**
	 * Get instance
	 */
	public static SessionFactory getSessionFactory() {
		Configuration cfg = new Configuration();
		return getSessionFactory(cfg);
	}
	
	/**
	 * Get instance
	 */
	public static SessionFactory getSessionFactory(Configuration cfg) {
		if (sessionFactory == null) {
			try {
				// Configuration
				cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
				cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
				cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:openkm");
				cfg.setProperty("hibernate.connection.username", "sa");
				cfg.setProperty("hibernate.connection.password", "");
				cfg.setProperty("hibernate.connection.pool_size", "1");
				cfg.setProperty("hibernate.connection.autocommit", "true");
				cfg.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
				cfg.setProperty("hibernate.hbm2ddl.auto", "create");
				cfg.setProperty("hibernate.show_sql", "false");
				cfg.setProperty("hibernate.format_sql", "true");
				cfg.setProperty("hibernate.use_sql_comments", "true");
				
				// Hibernate Search
				cfg.setProperty("hibernate.search.default.directory_provider", "org.hibernate.search.store.FSDirectoryProvider");
				cfg.setProperty("hibernate.search.default.indexBase", "indexes");
				
				sessionFactory = cfg.buildSessionFactory();
			} catch (HibernateException e) {
				log.error(e.getMessage(), e);
				throw new ExceptionInInitializerError(e);
			}
		}
		
		return sessionFactory;
	}
	
	/**
	 * Close factory
	 */
	public static void closeSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}
	
	/**
	 * Close session
	 */
	public static void close(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
	
	/**
	 * Commit transaction
	 */
	public static void commit(Transaction tx) {
		if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
			tx.commit();
		}
	}
	
	/**
	 * Rollback transaction
	 */
	public static void rollback(Transaction tx) {
		if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
			tx.rollback();
		}
	}
}
