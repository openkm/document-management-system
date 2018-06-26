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

package com.openkm.jaas;

import com.openkm.core.AccessDeniedException;
import com.openkm.module.db.stuff.DbSessionManager;
import org.springframework.security.core.Authentication;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author pavila
 */
public class PrincipalUtils {
	/**
	 * Obtain current authenticated subject
	 */
	public static Subject getSubject() throws NamingException {
		InitialContext ctx = new InitialContext();
		return (Subject) ctx.lookup("java:comp/env/security/subject");
	}

	/**
	 * Obtain the logged user.
	 */
	public static String getUser() throws NamingException {
		Subject subject = PrincipalUtils.getSubject();
		String user = null;

		for (Iterator<java.security.Principal> it = subject.getPrincipals().iterator(); it.hasNext(); ) {
			Object obj = it.next();

			if (!(obj instanceof java.security.acl.Group)) {
				java.security.Principal principal = (java.security.Principal) obj;
				user = principal.getName();
			}
		}

		return user;
	}

	/**
	 * Obtain the list of user roles.
	 */
	public static Set<String> getRoles() throws NamingException {
		Subject subject = PrincipalUtils.getSubject();
		Set<String> roles = new HashSet<String>();

		for (Iterator<java.security.Principal> it = subject.getPrincipals().iterator(); it.hasNext(); ) {
			Object obj = it.next();

			if (obj instanceof java.security.acl.Group) {
				java.security.acl.Group group = (java.security.acl.Group) obj;

				for (Enumeration<? extends java.security.Principal> groups = group.members(); groups.hasMoreElements(); ) {
					java.security.Principal rol = (java.security.Principal) groups.nextElement();
					roles.add(rol.getName());
				}
			}
		}

		return roles;
	}

	/**
	 * Check for role
	 */
	public static boolean hasRole(String role) {
		try {
			Set<String> roles = getRoles();

			if (roles != null) {
				return roles.contains(role);
			}
		} catch (NamingException e) {
			// Ignore
		}

		return false;
	}

	/**
	 * Get Authentication by token and also set it as current Authentication.
	 */
	public static Authentication getAuthenticationByToken(String token) throws AccessDeniedException {
		Authentication auth = DbSessionManager.getInstance().getAuthentication(token);

		if (auth != null) {
			return auth;
		} else {
			throw new AccessDeniedException("Invalid token: " + token);
		}
	}
}
