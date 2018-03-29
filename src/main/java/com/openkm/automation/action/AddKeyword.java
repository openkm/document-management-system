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

package com.openkm.automation.action;

import java.util.Map;

import com.openkm.automation.Action;
import com.openkm.automation.AutomationUtils;
import com.openkm.core.Config;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.Automation;

import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * AddKeyword
 *
 * @author sochoa
 */
@PluginImplementation
public class AddKeyword implements Action {

	@Override
	public void executePre(Map<String, Object> env, Object... params) throws Exception {
	}

	@Override
	public void executePost(Map<String, Object> env, Object... params) throws Exception {
		String keyword = AutomationUtils.getString(0, params);
		String uuid = AutomationUtils.getUuid(env);

		if (uuid != null && keyword != null && !keyword.isEmpty()) {
			if (Config.SYSTEM_KEYWORD_LOWERCASE) {
				keyword = keyword.toLowerCase();
			}

			NodeBaseDAO.getInstance().addKeyword(uuid, keyword);
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
		return "AddKeyword";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_TEXT;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc00() {
		return "Keyword";
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