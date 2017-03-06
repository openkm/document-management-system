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
import com.openkm.dao.bean.AutomationAction;
import com.openkm.dao.bean.AutomationMetadata;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.AutomationValidation;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AutomationDAO
 *
 * @author jllort
 */
public class AutomationDAO {
	private static Logger log = LoggerFactory.getLogger(AutomationDAO.class);
	private static AutomationDAO single = new AutomationDAO();

	private AutomationDAO() {
	}

	public static AutomationDAO getInstance() {
		return single;
	}

	/**
	 * Create
	 */
	public void create(AutomationRule ar) throws DatabaseException {
		log.debug("create({})", ar);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(ar);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("create: void");
	}

	/**
	 * Update
	 */
	public void update(AutomationRule ar) throws DatabaseException {
		log.debug("update({})", ar);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(ar);
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
	 * Update action
	 */
	public void updateAction(AutomationAction aa) throws DatabaseException {
		log.debug("updateAction({})", aa);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(aa);
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
	 * Update validation
	 */
	public void updateValidation(AutomationValidation av) throws DatabaseException {
		log.debug("updateAction({})", av);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(av);
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
	public void delete(long raId) throws DatabaseException {
		log.debug("delete({})", raId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			AutomationRule ra = (AutomationRule) session.load(AutomationRule.class, raId);
			session.delete(ra);
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
	 * Delete action
	 */
	public void deleteAction(long aaId) throws DatabaseException {
		log.debug("deleteAction({})", aaId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			AutomationAction aa = (AutomationAction) session.load(AutomationAction.class, aaId);
			session.delete(aa);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteAction: void");
	}

	/**
	 * Delete validation
	 */
	public void deleteValidation(long avId) throws DatabaseException {
		log.debug("deleteValidation({})", avId);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			AutomationValidation av = (AutomationValidation) session.load(AutomationValidation.class, avId);
			session.delete(av);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("deleteAction: void");
	}

	/**
	 * Create
	 */
	public void createAction(AutomationAction aa) throws DatabaseException {
		log.debug("createAction({})", aa);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(aa);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("createAction: void");
	}

	/**
	 * Create
	 */
	public void createValidation(AutomationValidation av) throws DatabaseException {
		log.debug("createValidation({})", av);
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(av);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		log.debug("createValidation: void");
	}

	/**
	 * find all rules
	 */
	@SuppressWarnings("unchecked")
	public List<AutomationRule> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from AutomationRule ar order by ar.order";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<AutomationRule> ret = q.list();
			initializeRules(ret);
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * find filtered riles
	 */
	@SuppressWarnings("unchecked")
	public List<AutomationRule> findByEvent(String event, String at) throws DatabaseException {
		log.debug("findByEvent({}, {})", event, at);
		String qs = "from AutomationRule ar where ar.event=:event and ar.at=:at order by ar.order";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("event", event);
			q.setString("at", at);
			List<AutomationRule> ret = q.list();
			initializeRules(ret);
			log.debug("findByEvent: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get all metadata actions
	 */
	@SuppressWarnings("unchecked")
	public List<AutomationMetadata> findMetadataValidationsByAt(String at) throws DatabaseException {
		log.debug("findAllMetadataValidations()");
		String qs = "from AutomationMetadata am where am.group=:group and am.at=:at order by am.name";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("group", AutomationMetadata.GROUP_VALIDATION);
			q.setString("at", at);
			List<AutomationMetadata> ret = q.list();
			log.debug("findAllMetadataValidations: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get all metadata actions
	 */
	@SuppressWarnings("unchecked")
	public List<AutomationMetadata> findMetadataActionsByAt(String at) throws DatabaseException {
		log.debug("findAllMetadataActions()");
		String qs = "from AutomationMetadata am where am.group=:group and am.at=:at order by am.name";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("group", AutomationMetadata.GROUP_ACTION);
			q.setString("at", at);
			List<AutomationMetadata> ret = q.list();
			log.debug("findAllMetadataActions: {}", ret);
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
	public AutomationRule findByPk(long arId) throws DatabaseException {
		log.debug("findByPk({})", arId);
		String qs = "from AutomationRule ar where ar.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", arId);
			AutomationRule ret = (AutomationRule) q.setMaxResults(1).uniqueResult();
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
	 * Get metadata by pk
	 */
	public AutomationMetadata findMetadataByPk(long amId) throws DatabaseException {
		log.debug("findMetadataByPk({})", amId);
		String qs = "from AutomationMetadata am where am.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", amId);
			AutomationMetadata ret = (AutomationMetadata) q.setMaxResults(1).uniqueResult();
			log.debug("findMetadataByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get validation by pk
	 */
	public AutomationValidation findValidationByPk(long avId) throws DatabaseException {
		log.debug("findValidationByPk({})", avId);
		String qs = "from AutomationValidation av where av.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", avId);
			AutomationValidation ret = (AutomationValidation) q.setMaxResults(1).uniqueResult();
			initialize(ret);
			log.debug("findValidationByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get action by pk
	 */
	public AutomationAction findActionByPk(long aaId) throws DatabaseException {
		log.debug("findActionByPk({})", aaId);
		String qs = "from AutomationAction aa where aa.id=:id";
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", aaId);
			AutomationAction ret = (AutomationAction) q.setMaxResults(1).uniqueResult();
			initialize(ret);
			log.debug("findActionByPk: {}", ret);
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
	private void initialize(AutomationRule aRule) {
		if (aRule != null) {
			Hibernate.initialize(aRule);
			initializeActions(aRule.getActions());
			initializeValidations(aRule.getValidations());
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initialize(AutomationValidation aValidation) {
		if (aValidation != null) {
			Hibernate.initialize(aValidation);
			Hibernate.initialize(aValidation.getParams());
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initialize(AutomationAction aAction) {
		if (aAction != null) {
			Hibernate.initialize(aAction);
			Hibernate.initialize(aAction.getParams());
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initializeRules(List<AutomationRule> nRuleList) {
		for (AutomationRule aRule : nRuleList) {
			initialize(aRule);
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initializeValidations(List<AutomationValidation> nValidationList) {
		for (AutomationValidation aValidation : nValidationList) {
			initialize(aValidation);
		}
	}

	/**
	 * Force initialization of a proxy
	 */
	private void initializeActions(List<AutomationAction> nActionList) {
		for (AutomationAction aAction : nActionList) {
			initialize(aAction);
		}
	}
}
