/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.automation.validation;

import java.util.Map;

import com.openkm.automation.AutomationUtils;
import com.openkm.automation.Validation;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.Automation;

import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * HasCategory
 *
 * @author jllort
 */
@PluginImplementation
public class HasCategory implements Validation {

	@Override
	public boolean isValid(Map<String, Object> env, Object... params) throws Exception {
		String catId = AutomationUtils.getString(0, params);
		String uuid = AutomationUtils.getUuid(env);

		if (uuid != null && !uuid.isEmpty()) {
			return NodeBaseDAO.getInstance().hasCategory(uuid, catId);
		} else {
			return false;
		}
	}

	@Override
	public boolean hasPost() {
		return true;
	}

	@Override
	public boolean hasPre() {
		return false;
	}

	@Override
	public String getName() {
		return "HasCategory";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_TEXT;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_FOLDER;
	}

	@Override
	public String getParamDesc00() {
		return "String";
	}

	@Override
	public String getParamType01() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc01() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc01() {
		return "";
	}

	@Override
	public String getParamType02() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc02() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc02() {
		return "";
	}
}
