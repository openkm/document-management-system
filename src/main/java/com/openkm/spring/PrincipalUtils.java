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

package com.openkm.spring;

import com.openkm.core.AccessDeniedException;
import com.openkm.module.db.stuff.DbSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * http://www.mkyong.com/spring-security/get-current-logged-in-username-in-spring-security/
 *
 * @author pavila
 */
public class PrincipalUtils {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PrincipalUtils.class);

	/**
	 * Obtain the logged user.
	 */
	public static String getUser() {
		Authentication auth = getAuthentication();
		String user = null;

		if (auth != null) {
			user = auth.getName();
		}

		return user;
	}

	/**
	 * Obtain the list of user roles.
	 */
	public static Set<String> getRoles() {
		Authentication auth = getAuthentication();
		Set<String> roles = new HashSet<String>();

		if (auth != null) {
			for (GrantedAuthority ga : auth.getAuthorities()) {
				roles.add(ga.getAuthority());
			}
		}

		return roles;
	}

	/**
	 * Check for role
	 */
	public static boolean hasRole(String role) {
		Authentication auth = getAuthentication();

		if (auth != null) {
			UserDetails user = (UserDetails) auth.getPrincipal();

			for (GrantedAuthority ga : user.getAuthorities()) {
				if (ga.getAuthority().equals(role)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get Authentication by token and also set it as current Authentication.
	 */
	public static Authentication getAuthenticationByToken(String token) throws AccessDeniedException {
		Authentication auth = DbSessionManager.getInstance().getAuthentication(token);

		if (auth != null) {
			SecurityContextHolder.getContext().setAuthentication(auth);
			return auth;
		} else {
			throw new AccessDeniedException("Invalid token: " + token);
		}
	}

	/**
	 * Obtain authentication token
	 */
	public static Authentication getAuthentication() {
		if (SecurityHolder.get() != null) {
			return SecurityHolder.get();
		} else {
			return SecurityContextHolder.getContext().getAuthentication();
		}
	}

	/**
	 * Set authentication token
	 */
	public static void setAuthentication(Authentication auth) {
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	/**
	 * Create authentication token
	 */
	public static Authentication createAuthentication(String user, Set<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}

		return new UsernamePasswordAuthenticationToken(user, null, authorities);
	}
}
