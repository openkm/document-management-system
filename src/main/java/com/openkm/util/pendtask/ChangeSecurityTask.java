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

package com.openkm.util.pendtask;

import com.openkm.bean.ChangeSecurityParams;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeBase;
import com.openkm.module.db.stuff.SecurityHelper;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class ChangeSecurityTask implements ProcessInDepthTask {
	private ChangeSecurityParams params;

	public ChangeSecurityTask(ChangeSecurityParams params) {
		this.params = params;
	}

	public ChangeSecurityParams getParams() {
		return params;
	}

	@Override
	public void securityPruneNodeList(List<? extends NodeBase> nodeList) throws DatabaseException {
		SecurityHelper.pruneNodeList(nodeList);
	}

	@Override
	public Object doTask(Session session, NodeBase parentNode, NodeBase node) throws DatabaseException {
		try {
			NodeBaseDAO.getInstance().changeSecurity(session, node, params.getGrantUsers(), params.getRevokeUsers(),
					params.getGrantRoles(), params.getRevokeRoles(), true);
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new DatabaseException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new DatabaseException(e.getMessage(), e);
		}

		return null;
	}
}
