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

package com.openkm.automation.action;

import com.openkm.automation.Action;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.Permission;
import com.openkm.core.Config;
import com.openkm.dao.NodeBaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * RevokeAllRoles
 *
 * @author jllort
 *
 */
public class RevokeAllRoles implements Action {
	private static Logger log = LoggerFactory.getLogger(RevokeAllRoles.class);

	@Override
	public void executePre(HashMap<String, Object> env, Object... params) {
	}

	@Override
	public void executePost(HashMap<String, Object> env, Object... params) {
		boolean recursive = AutomationUtils.getBoolean(0, params).booleanValue();
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

		try {
			if (uuid != null) {
				for (String role : NodeBaseDAO.getInstance().getRolePermissions(uuid).keySet()) {
					NodeBaseDAO.getInstance().revokeRolePermissions(uuid, role, allGrants, recursive);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
