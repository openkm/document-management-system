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

package com.openkm.test.api;

import com.openkm.api.OKMAuth;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.principal.PrincipalAdapterException;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;

public class AuthTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(AuthTest.class);
	private OKMAuth okmAuth = OKMAuth.getInstance();
	private String TEST_USER = "test";
	private String TEST_PASSWORD = "test";
	private String token;

	public AuthTest(String name) {
		super(name);
	}

	@Before
	public void setUp() throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setUp()");
		token = okmAuth.login(Config.UNIT_TESTING_USER, Config.UNIT_TESTING_PASSWORD);
	}

	@After
	public void tearDown() throws PrincipalAdapterException, RepositoryException, DatabaseException {
		log.debug("tearDown()");

		// Clean folders and ignore if do no exists
		catchException(okmAuth).deleteUser(token, TEST_USER);

		okmAuth.logout(token);
	}

	@Test
	public void testCreate() throws PrincipalAdapterException, AccessDeniedException, RepositoryException, DatabaseException {
		List<String> usersPre = okmAuth.getUsers(token);
		okmAuth.createUser(token, TEST_USER, TEST_PASSWORD, "test@openkm.com", "Test User", true);
		List<String> usersPost = okmAuth.getUsers(token);
		assertEquals(usersPre.size() + 1, usersPost.size());
		assertTrue(usersPost.contains(TEST_USER));

		// Login should fail because has no ROLE_USER or ROLE_ADMIN roles
		catchException(okmAuth).login(TEST_USER, TEST_PASSWORD);
		assertTrue(caughtException() instanceof AccessDeniedException);
	}

	@Test
	public void testRoles() throws PrincipalAdapterException, AccessDeniedException, RepositoryException, DatabaseException {
		List<String> usersPre = okmAuth.getUsers(token);
		okmAuth.createUser(token, TEST_USER, TEST_PASSWORD, "test@openkm.com", "Test User", true);
		List<String> usersPost = okmAuth.getUsers(token);
		assertEquals(usersPre.size() + 1, usersPost.size());
		assertTrue(usersPost.contains(TEST_USER));

		// Assign role
		List<String> roles = okmAuth.getRolesByUser(token, TEST_USER);
		assertTrue(roles.isEmpty());
		okmAuth.assignRole(token, TEST_USER, Config.DEFAULT_USER_ROLE);
		roles = okmAuth.getRolesByUser(token, TEST_USER);
		assertFalse(roles.isEmpty());
		assertTrue(roles.contains(Config.DEFAULT_USER_ROLE));

		// Login
		String tokenUsr = okmAuth.login(TEST_USER, TEST_PASSWORD);
		assertNotNull(tokenUsr);
		okmAuth.logout(tokenUsr);

		// Remove role
		okmAuth.removeRole(token, TEST_USER, Config.DEFAULT_USER_ROLE);
		roles = okmAuth.getRolesByUser(token, TEST_USER);
		assertTrue(roles.isEmpty());
	}
}
