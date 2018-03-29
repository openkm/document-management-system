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
import com.openkm.bean.Permission;
import com.openkm.core.Config;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.Automation;
import com.openkm.spring.PrincipalUtils;

import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * RevokeAllUsers
 *
 * @author jllort
 */
@PluginImplementation
public class RevokeAllUsers implements Action {

	@Override
	public void executePre(Map<String, Object> env, Object... params) throws Exception {
	}

	@Override
	public void executePost(Map<String, Object> env, Object... params) throws Exception {
		boolean recursive = AutomationUtils.getBoolean(0, params).booleanValue();
		boolean preserveCreator = AutomationUtils.getBoolean(1, params).booleanValue();
		String uuid = AutomationUtils.getUuid(env);
		int allGrants = Permission.ALL_GRANTS;

		if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
			allGrants = allGrants | Permission.PROPERTY_GROUP;
		}

		if ((Config.SECURITY_EXTENDED_MASK & Permission.COMPACT_HISTORY) == Permission.COMPACT_HISTORY) {
			allGrants = allGrants | Permission.COMPACT_HISTORY;
		}

		if ((Config.SECURITY_EXTENDED_MASK & Permission.START_WORKFLOW) == Permission.START_WORKFLOW) {
			allGrants = allGrants | Permission.START_WORKFLOW;
		}

		if ((Config.SECURITY_EXTENDED_MASK & Permission.DOWNLOAD) == Permission.DOWNLOAD) {
			allGrants = allGrants | Permission.DOWNLOAD;
		}

		if (uuid != null) {
			String creatorUser = PrincipalUtils.getUser();

			for (String user : NodeBaseDAO.getInstance().getUserPermissions(uuid).keySet()) {
				if (preserveCreator) {
					if (!creatorUser.equals(user)) {
						NodeBaseDAO.getInstance().revokeUserPermissions(uuid, user, allGrants, recursive);
					}
				} else {
					NodeBaseDAO.getInstance().revokeUserPermissions(uuid, user, allGrants, recursive);
				}
			}
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
		return "RevokeAllUsers";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_BOOLEAN;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc00() {
		return "Recursive";
	}

	@Override
	public String getParamType01() {
		return Automation.PARAM_TYPE_BOOLEAN;
	}

	@Override
	public String getParamSrc01() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc01() {
		return "Preserve creator";
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