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

package com.openkm.module.db;

import com.openkm.core.*;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.UserConfigDAO;
import com.openkm.dao.bean.UserConfig;
import com.openkm.module.UserConfigModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public class DbUserConfigModule implements UserConfigModule {
	private static Logger log = LoggerFactory.getLogger(DbUserConfigModule.class);

	@Override
	public void setHome(String token, String nodePath) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("setHome({}, {})", token, nodePath);
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			String nodeType = NodeBaseDAO.getInstance().getNodeTypeByUuid(nodeUuid);
			UserConfig uc = new UserConfig();
			uc.setHomePath(nodePath);
			uc.setHomeNode(nodeUuid);
			uc.setHomeType(nodeType);
			uc.setUser(auth.getName());
			UserConfigDAO.setHome(uc);

			// Activity log
			UserActivity.log(auth.getName(), "USER_CONFIG_SET_HOME", nodeUuid, nodePath, null);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("setHome: void");
	}

	@Override
	public UserConfig getConfig(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getConfig({})", token);
		UserConfig ret = new UserConfig();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			ret = UserConfigDAO.findByPk(auth.getName());

			// Activity log
			UserActivity.log(auth.getName(), "USER_CONFIG_GET_CONFIG", null, null, null);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getConfig: {}", ret);
		return ret;
	}
}
