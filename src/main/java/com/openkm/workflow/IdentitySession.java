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

package com.openkm.workflow;

import com.openkm.module.common.CommonAuthModule;
import com.openkm.principal.PrincipalAdapterException;
import org.jbpm.identity.Group;
import org.jbpm.identity.Membership;
import org.jbpm.identity.User;
import org.jbpm.identity.assignment.ExpressionSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentitySession implements ExpressionSession {
	private static Logger log = LoggerFactory.getLogger(IdentitySession.class);

	/**
	 * retrieves a group from the user datastore including the membership relations.
	 */
	@Override
	public Group getGroupByName(String name) {
		log.debug("getGroupByName({})", name);
		Group group = null;

		try {
			group = new Group(name);

			for (String user : CommonAuthModule.getUsersByRole(name)) {
				Membership membership = new Membership();
				membership.setUser(new User(user));
				group.addMembership(membership);
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
		}

		log.debug("getGroupByName: {}", group);
		return group;
	}

	/**
	 * retrieves a user from the user datastore including the membership relations.
	 */
	@Override
	public User getUserByName(String userName) {
		log.debug("getUserByName({})", userName);
		User user = null;

		try {
			user = new User(userName);
			user.setEmail(CommonAuthModule.getMail(userName));

			for (String role : CommonAuthModule.getRolesByUser(userName)) {
				Membership membership = new Membership();
				membership.setGroup(new Group(role));
				user.addMembership(membership);
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
		}

		log.debug("getGroupByName: {}", user);
		return user;
	}

	/**
	 * retrieves a user from the user datastore including the membership relations.
	 */
	@Override
	public User getUserByGroupAndRole(String group, String role) {
		log.debug("getUserByGroupAndRole({}, {})", group, role);
		User user = null;

		try {
			for (String userId : CommonAuthModule.getUsersByRole(group)) {
				user = new User(userId);
				user.setEmail(CommonAuthModule.getMail(userId));
				Membership membership = new Membership();
				membership.setGroup(new Group(group));
				membership.setRole(role);
				user.addMembership(membership);
				break;
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
		}

		log.debug("getUserByGroupAndRole: {}", user);
		return user;
	}
}
