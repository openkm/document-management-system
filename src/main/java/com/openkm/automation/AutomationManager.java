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

package com.openkm.automation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.AuthDAO;
import com.openkm.dao.AutomationDAO;
import com.openkm.dao.bean.Automation;
import com.openkm.dao.bean.AutomationAction;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.AutomationValidation;
import com.openkm.dao.bean.User;
import com.openkm.util.MailUtils;

public class AutomationManager {
	private static Logger log = LoggerFactory.getLogger(AutomationManager.class);
	private static AutomationManager single = new AutomationManager();

	private AutomationManager() {
	}

	public static AutomationManager getInstance() {
		return single;
	}

	/**
	 * Handle extensions
	 */
	public void fireEvent(String event, String at, Map<String, Object> env) throws AutomationException {
		log.debug("fireEvent({})", env);

		try {
			for (List<AutomationAction> actions : getValidActions(event, at, env)) {
				for (AutomationAction action : actions) {
					log.debug("Action: {}", action);
					env.put(AutomationUtils.EVENT, event);
					executeAction(action, at, env);
				}
			}
		} catch (Exception e) {
			throw new AutomationException(e.getMessage(), e);
		}

		log.debug("fireEvent: void");
	}

	/**
	 * Execute actions
	 */
	private void executeAction(AutomationAction aAct, String at, Map<String, Object> env)
			throws IllegalArgumentException, SecurityException, AutomationException {
		Action action = AutomationDAO.getInstance().findActionByClassName(aAct.getClassName());
		Object[] params = resolveParams(AutomationDAO.getInstance().convert(action), aAct.getParams());

		try {
			if (AutomationRule.AT_PRE.equals(at)) {
				log.debug("{}.executePre({})", aAct.getClass().getCanonicalName(), params);
				action.executePre(env, params);
			} else {
				log.debug("{}.executePost({})", aAct.getClass().getCanonicalName(), params);
				action.executePost(env, params);
			}
		} catch (Exception e) {
			throw new AutomationException(e.getMessage(), e);
		}
	}

	/**
	 * Check for validations
	 * 
	 * @throws Exception
	 */
	private List<List<AutomationAction>> getValidActions(String event, String at, Map<String, Object> env)
			throws IllegalArgumentException, SecurityException, AutomationException {
		List<List<AutomationAction>> actionsList = new ArrayList<List<AutomationAction>>();

		try {
			for (AutomationRule aRule : AutomationDAO.getInstance().findByEvent(event, at)) {
				if (aRule.getActive()) {
					boolean execute = true;

					for (AutomationValidation aVal : aRule.getValidations()) {
						try {
							if (aVal.getActive()) {
								// Check validations
								Validation validation = AutomationDAO.getInstance()
										.findValidationByClassName(aVal.getClassName());
								Object[] params = resolveParams(AutomationDAO.getInstance().convert(validation),
										aVal.getParams());
								log.debug("{}.isValid({})", validation.getClass().getCanonicalName(), params);
								boolean isValid = validation.isValid(env, params);
								execute &= isValid;
							}
						} catch (PathNotFoundException e) {
							// node does not exist, rule shouldn't be executed
							execute = false;
							disableRuleAndNotifyAdmin(aRule);
						}
					}

					if (execute) {
						List<AutomationAction> actions = new ArrayList<AutomationAction>();

						for (AutomationAction aAct : aRule.getActions()) {
							if (aAct.getActive()) {
								actions.add(aAct);
							}
						}

						actionsList.add(actions);
						
						// Stop processing rules if rule is exclusive
						if (aRule.getExclusive()) {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new AutomationException(e.getMessage(), e);
		}

		return actionsList;
	}

	private void disableRuleAndNotifyAdmin(AutomationRule rule) {
		try {
			disableRule(rule);
			notifyToAdminDisabledRule(rule);
		} catch (DatabaseException e) {
			log.error("Can't disable rule with id '{}'", rule.getId());
		} catch (MessagingException e) {
			log.error("Can't notify admin user about disabled rule", e);
		}
	}

	private void notifyToAdminDisabledRule(AutomationRule rule) throws DatabaseException, MessagingException {
		User adminUser = AuthDAO.findUserByPk(Config.ADMIN_USER);
		String subject = String.format("Rule %d disabled", rule.getId());
		String content = String.format(
				"Automation for rule %d was disabled. Rule was associated with a node which no longer exists",
				rule.getId());
		MailUtils.sendMessage(Config.DEFAULT_CRONTAB_MAIL, adminUser.getEmail(), subject, content);
	}

	private void disableRule(AutomationRule rule) throws DatabaseException {
		rule.setActive(false);
		AutomationDAO.getInstance().update(rule);
	}

	/**
	 * hasAutomation
	 *
	 * @return True if there are defined automation actions, else false.
	 * @throws DatabaseException
	 *             In case of any database error.
	 */
	public boolean hasAutomation() throws DatabaseException {
		return (AutomationDAO.getInstance().findAll().size() > 0);
	}

	/**
	 * Resolve method parameters. Convert automation action parameters to the
	 * needed type based on the AutomationMetadata information.
	 */
	private Object[] resolveParams(Automation amd, List<String> params) {
		List<Object> retParams = new ArrayList<>();

		// param0
		if (Automation.PARAM_TYPE_TEXT.equals(amd.getType00()) || Automation.PARAM_TYPE_TEXTAREA.equals(amd.getType00())
				|| Automation.PARAM_TYPE_CODE.equals(amd.getType00())) {
			retParams.add(String.valueOf(params.get(0)));
		} else if (Automation.PARAM_TYPE_INTEGER.equals(amd.getType00())) {
			try {
				retParams.add(Integer.valueOf(params.get(0)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 0: {}", params.get(0));
			}
		} else if (Automation.PARAM_TYPE_LONG.equals(amd.getType00())) {
			try {
				retParams.add(Long.valueOf(params.get(0)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 0: {}", params.get(0));
			}
		} else if (Automation.PARAM_TYPE_BOOLEAN.equals(amd.getType00())) {
			try {
				retParams.add(Boolean.valueOf(params.get(0)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Boolean for Parameter 0: {}", params.get(0));
			}
		} else if (Automation.PARAM_TYPE_USER.equals(amd.getType00())
				|| Automation.PARAM_TYPE_ROLE.equals(amd.getType00())) {
			retParams.add(new ArrayList<>(Arrays.asList(String.valueOf(params.get(0)).split(","))));
		}

		// param1
		if (Automation.PARAM_TYPE_TEXT.equals(amd.getType01()) || Automation.PARAM_TYPE_TEXTAREA.equals(amd.getType01())
				|| Automation.PARAM_TYPE_CODE.equals(amd.getType01())) {
			retParams.add(String.valueOf(params.get(1)));
		} else if (Automation.PARAM_TYPE_INTEGER.equals(amd.getType01())) {
			try {
				retParams.add(Integer.valueOf(params.get(1)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 1: {}", params.get(1));
			}
		} else if (Automation.PARAM_TYPE_LONG.equals(amd.getType01())) {
			try {
				retParams.add(Long.valueOf(params.get(1)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 1: {}", params.get(1));
			}
		} else if (Automation.PARAM_TYPE_BOOLEAN.equals(amd.getType01())) {
			try {
				retParams.add(Boolean.valueOf(params.get(1)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 1: {}", params.get(1));
			}
		} else if (Automation.PARAM_TYPE_USER.equals(amd.getType01())
				|| Automation.PARAM_TYPE_ROLE.equals(amd.getType01())) {
			retParams.add(new ArrayList<>(Arrays.asList(String.valueOf(params.get(1)).split(","))));
		}

		// param2
		if (Automation.PARAM_TYPE_TEXT.equals(amd.getType02()) || Automation.PARAM_TYPE_TEXTAREA.equals(amd.getType02())
				|| Automation.PARAM_TYPE_CODE.equals(amd.getType02())) {
			retParams.add(String.valueOf(params.get(2)));
		} else if (Automation.PARAM_TYPE_INTEGER.equals(amd.getType02())) {
			try {
				retParams.add(Integer.valueOf(params.get(2)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 2: {}", params.get(2));
			}
		} else if (Automation.PARAM_TYPE_LONG.equals(amd.getType02())) {
			try {
				retParams.add(Long.valueOf(params.get(2)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 2: {}", params.get(2));
			}
		} else if (Automation.PARAM_TYPE_BOOLEAN.equals(amd.getType02())) {
			try {
				retParams.add(Boolean.valueOf(params.get(2)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 2: {}", params.get(2));
			}
		} else if (Automation.PARAM_TYPE_USER.equals(amd.getType02())
				|| Automation.PARAM_TYPE_ROLE.equals(amd.getType02())) {
			retParams.add(new ArrayList<>(Arrays.asList(String.valueOf(params.get(2)).split(","))));
		}

		return retParams.toArray();
	}
}
