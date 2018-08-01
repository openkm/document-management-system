/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.util.validator;

import com.google.gwt.user.client.Timer;
import eu.maydu.gwt.validation.client.DefaultValidationProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * ExtendedDefaultValidatorProcessor
 */
public class ExtendedDefaultValidatorProcessor extends DefaultValidationProcessor implements ValidatorPluginToFire {
	private int plugins = 0;
	private int pluginsValidated = 0;
	private ValidatorToFire validatorToFire;
	private boolean result = true;
	private List<String> uuids = new ArrayList<>();

	/**
	 * ExtendedValidatorProcessor
	 */
	public ExtendedDefaultValidatorProcessor(ValidatorToFire validatorToFire) {
		super();
		this.validatorToFire = validatorToFire;
	}

	@Override
	public void finished(String error) {
		if (!error.isEmpty()) {
			result = false;
		}
		pluginsValidated++;
	}

	@Override
	public boolean validate(String... names) {
		return validate(new ArrayList<String>(), names);
	}

	/**
	 * validate
	 * <p>
	 * Used while updating
	 */
	public boolean validate(List<String> uuids, String... names) {
		this.uuids = uuids;
		result = true;
		pluginsValidated = 0;
		result = result && super.validate(names);
		// Always evaluate plugins rpc calls at ends
		if (plugins > 0 && validatorToFire != null) {
			waitUntilValidatorsFinished();
		} else if (plugins == 0 && validatorToFire != null) {
			validatorToFire.validationWithPluginsFinished(result);
		}
		return result;
	}


	@Override
	public void incrementNumberOfPlugins() {
		plugins++;
	}

	@Override
	public int getNumberOfPlugins() {
		return plugins;
	}

	@Override
	public boolean hasPlugins() {
		return plugins > 0;
	}

	/**
	 * getUuids
	 */
	public List<String> getUuids() {
		return uuids;
	}

	/**
	 * waitUntilValidatorsFinished
	 */
	public void waitUntilValidatorsFinished() {
		if (pluginsValidated == plugins && validatorToFire != null) {
			validatorToFire.validationWithPluginsFinished(result);
		} else if (pluginsValidated < plugins) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					waitUntilValidatorsFinished();
				}
			};
			timer.schedule(200);
		}
	}
}