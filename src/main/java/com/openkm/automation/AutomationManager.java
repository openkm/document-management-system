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

import com.openkm.core.DatabaseException;
import com.openkm.dao.AutomationDAO;
import com.openkm.dao.bean.AutomationAction;
import com.openkm.dao.bean.AutomationMetadata;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.AutomationValidation;
import com.openkm.util.cl.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutomationManager {
	private static Logger log = LoggerFactory.getLogger(AutomationManager.class);
	private static AutomationManager single = new AutomationManager();
	;

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
			throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalArgumentException,
			SecurityException, InstantiationException, DatabaseException {
		AutomationMetadata amd = AutomationDAO.getInstance().findMetadataByPk(aAct.getType());
		Object[] params = resolveParams(amd, aAct.getParams());

		if (AutomationRule.AT_PRE.equals(at)) {
			ClassLoaderUtils.invokeAutomationMethod(amd.getClassName(), Action.METHOD_PRE, env, params);
		} else {
			ClassLoaderUtils.invokeAutomationMethod(amd.getClassName(), Action.METHOD_POST, env, params);
		}
	}

	/**
	 * Check for validations
	 */
	private List<List<AutomationAction>> getValidActions(String event, String at, Map<String, Object> env)
			throws DatabaseException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			IllegalArgumentException, SecurityException, InstantiationException {
		List<List<AutomationAction>> actionsList = new ArrayList<List<AutomationAction>>();

		for (AutomationRule aRule : AutomationDAO.getInstance().findByEvent(event, at)) {
			if (aRule.isActive()) {
				boolean execute = true;

				for (AutomationValidation aVal : aRule.getValidations()) {
					if (aVal.isActive()) {
						// Check validations
						AutomationMetadata amd = AutomationDAO.getInstance().findMetadataByPk(aVal.getType());
						Object[] params = resolveParams(amd, aVal.getParams());
						boolean isValid = (Boolean) ClassLoaderUtils.invokeAutomationMethod(amd.getClassName(),
								Validation.METHOD, env, params);
						execute &= isValid;
					}
				}

				if (execute) {
					List<AutomationAction> actions = new ArrayList<AutomationAction>();

					for (AutomationAction aAct : aRule.getActions()) {
						if (aAct.isActive()) {
							actions.add(aAct);
						}
					}

					actionsList.add(actions);
				}
			}
		}

		return actionsList;
	}

	/**
	 * hasAutomation
	 *
	 * @return True id there are defined automation actions, else false.
	 *
	 * @throws DatabaseException In case of any database error.
	 */
	public boolean hasAutomation() throws DatabaseException {
		return (AutomationDAO.getInstance().findAll().size() > 0);
	}

	/**
	 * Resolve method parameters.
	 *
	 * Convert automation action parameters to the needed type based on
	 * the AutomationMetadata information.
	 */
	private Object[] resolveParams(AutomationMetadata amd, List<String> params) {
		List<Object> retParams = new ArrayList<Object>();

		if (AutomationMetadata.TYPE_TEXT.equals(amd.getType00()) || AutomationMetadata.TYPE_TEXTAREA.equals(amd.getType00())) {
			retParams.add(String.valueOf(params.get(0)));
		} else if (AutomationMetadata.TYPE_INTEGER.equals(amd.getType00())) {
			try {
				retParams.add(Integer.valueOf(params.get(0)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 0: {}", params.get(0));
			}
		} else if (AutomationMetadata.TYPE_BOOLEAN.equals(amd.getType00())) {
			try {
				retParams.add(Boolean.valueOf(params.get(0)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Boolean for Parameter 0: {}", params.get(0));
			}
		}

		if (AutomationMetadata.TYPE_TEXT.equals(amd.getType01()) || AutomationMetadata.TYPE_TEXTAREA.equals(amd.getType01())) {
			retParams.add(String.valueOf(params.get(1)));
		} else if (AutomationMetadata.TYPE_INTEGER.equals(amd.getType01())) {
			try {
				retParams.add(Integer.valueOf(params.get(1)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 1: {}", params.get(1));
			}
		} else if (AutomationMetadata.TYPE_BOOLEAN.equals(amd.getType01())) {
			try {
				retParams.add(Boolean.valueOf(params.get(1)));
			} catch (NumberFormatException e) {
				log.warn("Error parsing Integer for Parameter 1: {}", params.get(1));
			}
		}

		return retParams.toArray();
	}
}
