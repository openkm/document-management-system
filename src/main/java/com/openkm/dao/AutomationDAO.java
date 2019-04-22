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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.automation.Action;
import com.openkm.automation.AutomationException;
import com.openkm.automation.Validation;
import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Automation;
import com.openkm.dao.bean.AutomationAction;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.AutomationValidation;
import com.openkm.util.PluginUtils;

import net.xeoh.plugins.base.Plugin;

/**
 * AutomationDAO
 *
 * @author jllort
 */
public class AutomationDAO {
	private static Logger log = LoggerFactory.getLogger(AutomationDAO.class);
	private static AutomationDAO single = new AutomationDAO();
	private static List<Validation> validatorsList;
	private static List<Action> actionsList;
	public static final String PLUGIN_URI = "classpath://com.openkm.automation.**";

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
	public List<Automation> findMetadataValidationsByAt(String at) throws URISyntaxException {
		log.debug("findMetadataValidationsByAt()");
		List<Automation> amList = new ArrayList<Automation>();

		for (Validation val : findValidations(false)) {
			if (at.equals(Automation.AT_PRE)) {
				if (val.hasPre()) {
					amList.add(convert(val));
				}
			} else if (at.equals(Automation.AT_POST)) {
				if (val.hasPost()) {
					amList.add(convert(val));
				}
			}
		}

		return amList;
	}

	/**
	 * Get all metadata actions
	 */
	public List<Automation> findMetadataActionsByAt(String at)
			throws DatabaseException, IllegalArgumentException, SecurityException, URISyntaxException,
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		log.debug("findMetadataActionsByAt()");
		List<Automation> amList = new ArrayList<Automation>();

		for (Action act : findActions(false)) {
			if (at.equals(Automation.AT_PRE)) {
				if (act.hasPre()) {
					amList.add(convert(act));
				}
			} else if (at.equals(Automation.AT_POST)) {
				if (act.hasPost()) {
					amList.add(convert(act));
				}
			}
		}

		return amList;
	}

	/**
	 * findValidations
	 */
	public synchronized List<Validation> findValidations(boolean reload) throws URISyntaxException {
		log.debug("findValidations({})", reload);

		if (validatorsList == null || reload) {
			if (validatorsList == null) {
				validatorsList = new ArrayList<>();
			}

			validatorsList.clear();
			URI uri = new URI(PLUGIN_URI);

			for (Plugin plg : PluginUtils.getPlugins(uri, Validation.class)) {
				validatorsList.add((Validation) plg);
			}

			Collections.sort(validatorsList, new ValidationComparator());
		}

		log.debug("findValidations: {}", validatorsList);
		return validatorsList;
	}

	/**
	 * findActions
	 */
	public synchronized List<Action> findActions(boolean reload) throws URISyntaxException {
		log.debug("findActions({})", reload);

		if (actionsList == null || reload) {
			if (actionsList == null) {
				actionsList = new ArrayList<>();
			}

			actionsList.clear();
			URI uri = new URI(PLUGIN_URI);

			for (Plugin plg : PluginUtils.getPlugins(uri, Action.class)) {
				actionsList.add((Action) plg);
			}

			Collections.sort(actionsList, new ActionComparator());
		}

		log.debug("findActions: {}", actionsList);
		return actionsList;
	}

	/**
	 * findValidationByClassName
	 */
	public Validation findValidationByClassName(String className) throws AutomationException {
		if (validatorsList == null) {
			try {
				findValidations(true);
			} catch (URISyntaxException e) {
				throw new AutomationException(e);
			}
		}
		for (Validation validation : validatorsList) {
			if (validation.getClass().getName().equals(className)) {
				return validation;
			}
		}

		throw new AutomationException("Class not found exception: " + className);
	}

	/**
	 * findActionByClassName
	 */
	public Action findActionByClassName(String className) throws AutomationException {
		if (actionsList == null) {
			try {
				findActions(true);
			} catch (URISyntaxException e) {
				throw new AutomationException(e);
			}
		}
		for (Action action : actionsList) {
			if (action.getClass().getName().equals(className)) {
				return action;
			}
		}

		throw new AutomationException("Class not found exception: " + className);
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
	public Automation findMetadataByPk(String className) throws DatabaseException, IllegalArgumentException, SecurityException,
			URISyntaxException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {
		log.debug("findMetadataByPk({})", className);

		for (Action action : findActions(false)) {
			if (action.getClass().getName().equals(className)) {
				return convert(action);
			}
		}

		for (Validation validation : findValidations(false)) {
			if (validation.getClass().getName().equals(className)) {
				return convert(validation);
			}
		}

		return null;
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

	private class ActionComparator implements Comparator<Action> {
		public int compare(Action arg0, Action arg1) {
			return (arg0.getName()).compareTo(arg1.getName());
		}
	}

	private class ValidationComparator implements Comparator<Validation> {
		public int compare(Validation arg0, Validation arg1) {
			return (arg0.getName()).compareTo(arg1.getName());
		}
	}

	/**
	 * convert
	 */
	public Automation convert(Action action) {
		return new Automation(action);
	}

	/**
	 * convert
	 */
	public Automation convert(Validation validation) {
		return new Automation(validation);
	}
}
