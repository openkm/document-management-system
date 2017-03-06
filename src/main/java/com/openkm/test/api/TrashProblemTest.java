package com.openkm.test.api;

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

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;

public class TrashProblemTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(TrashProblemTest.class);
	private OKMRepository okmRepo = OKMRepository.getInstance();
	private OKMDocument okmDocument = OKMDocument.getInstance();
	private OKMFolder okmFolder = OKMFolder.getInstance();
	private OKMAuth okmAuth = OKMAuth.getInstance();
	private String BASE = Config.UNIT_TESTING_FOLDER;
	private String token;

	public TrashProblemTest(String name) {
		super(name);
	}

	@Before
	public void setUp() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("setUp()");
		token = okmAuth.login(Config.UNIT_TESTING_USER, Config.UNIT_TESTING_PASSWORD);

		// Create base folder
		catchException(okmFolder).createSimple(token, BASE);
	}

	@After
	public void tearDown() throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("tearDown()");

		// Clean folders and ignore if do no exists
		catchException(okmFolder).delete(token, BASE);
		catchException(okmRepo).purgeTrash(token);

		okmAuth.logout(token);
	}

	@Test
	public void testTrashProblem() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException, AutomationException, IOException, UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, LockException, VersionException {
		log.info("Create base folder");
		okmFolder.createSimple(token, BASE + "/alpha");

		log.info("Create document 'one.txt' and version");
		okmDocument.createSimple(token, BASE + "/alpha/one.txt", new ByteArrayInputStream("Sample text for one".getBytes()));
		okmDocument.checkout(token, BASE + "/alpha/one.txt");
		okmDocument.checkin(token, BASE + "/alpha/one.txt", new ByteArrayInputStream("new version 1".getBytes()), "version 1");

		log.info("Create document 'two.txt' and versions");
		okmDocument.createSimple(token, BASE + "/alpha/two.txt", new ByteArrayInputStream("Sample text for two".getBytes()));
		okmDocument.checkout(token, BASE + "/alpha/two.txt");
		okmDocument.checkin(token, BASE + "/alpha/two.txt", new ByteArrayInputStream("new version 1".getBytes()), "version 1");
		okmDocument.checkout(token, BASE + "/alpha/two.txt");
		okmDocument.checkin(token, BASE + "/alpha/two.txt", new ByteArrayInputStream("new version 2".getBytes()), "version 2");

		log.info("Delete base folder and purge trash");
		okmFolder.delete(token, BASE + "/alpha");
		okmRepo.purgeTrash(token);
	}
}
