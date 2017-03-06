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

import junit.framework.TestCase;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @see http://www.theserverside.com/news/1365222/Unit-Testing-Hibernate-With-HSQLDB
 * @author pavila
 */
public class SimpleTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(SimpleTest.class);
	private static String uuid = UUID.randomUUID().toString();
	
	public SimpleTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) throws Exception {
		SimpleTest test = new SimpleTest("main");
		test.setUp();
		test.testCreate();
		test.testUpdate();
		test.testSelect();
		test.tearDown();
	}
	
	public void testCreate() {
		log.info("testCreate()");
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			Document doc = new Document();
			doc.setUuid(uuid);
			doc.setName("pruebas.txt");
			session.save(doc);
			
			Document dbDoc = (Document) session.get(Document.class, uuid); 
			assertNotNull(dbDoc);
			assertEquals(dbDoc.getName(), doc.getName());
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
			HibernateUtil.rollback(tx);
			throw e;
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	public void testUpdate() {
		log.info("testUpdate()");
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Document doc = (Document) session.load(Document.class, uuid);
			assertNotNull(doc);
			
			doc.getKeywords().add("alfa");
			doc.getKeywords().add("beta");
			session.update(doc);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
			HibernateUtil.rollback(tx);
			throw e;
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	public void testSelect() {
		log.info("testSelect()");
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Document doc = (Document) session.load(Document.class, uuid);
			assertNotNull(doc);
			assertEquals(2, doc.getKeywords().size());
			log.info("Keywords: {}", doc.getKeywords());
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			HibernateUtil.close(session);
		}
	}

	@Override
	protected void setUp() {
		log.debug("setUp()");
		Configuration cfg = new Configuration();
		cfg.addAnnotatedClass(Document.class);
		SessionFactory sf = HibernateUtil.getSessionFactory(cfg);
		assertNotNull(sf);
	}

	@Override
	protected void tearDown() {
		log.debug("tearDown()");
	}
}
