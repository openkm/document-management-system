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

import com.openkm.api.*;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.principal.PrincipalAdapterException;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;

public class PropertyTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(PropertyTest.class);
	private OKMRepository okmRepo = OKMRepository.getInstance();
	private OKMDocument okmDocument = OKMDocument.getInstance();
	private OKMProperty okmProperty = OKMProperty.getInstance();
	private OKMFolder okmFolder = OKMFolder.getInstance();
	private OKMAuth okmAuth = OKMAuth.getInstance();
	private String BASE = Config.UNIT_TESTING_FOLDER;
	private String BASE_CAT = "/" + Repository.CATEGORIES + "/test";
	private String token;

	public PropertyTest(String name) {
		super(name);
	}

	@Before
	public void setUp() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException,
			ExtensionException, AutomationException, PrincipalAdapterException {
		log.debug("setUp()");
		token = okmAuth.login(Config.UNIT_TESTING_USER, Config.UNIT_TESTING_PASSWORD);

		// Create base folder
		catchException(okmFolder).createSimple(token, BASE);
		catchException(okmFolder).createSimple(token, BASE_CAT);
	}

	@After
	public void tearDown() throws PrincipalAdapterException, LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("tearDown()");

		// Clean folders and ignore if do no exists
		catchException(okmFolder).delete(token, BASE);
		catchException(okmRepo).purgeTrash(token);
		catchException(okmFolder).delete(token, BASE_CAT);
		catchException(okmRepo).purgeTrash(token);

		okmAuth.logout(token);
	}

	@Test
	public void testDocumentAdd() throws IOException, ParseException, PathNotFoundException, RepositoryException, DatabaseException,
			UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException,
			ItemExistsException, AccessDeniedException, ExtensionException, AutomationException, NoSuchGroupException, LockException,
			VersionException {
		Document docNew = okmDocument.createSimple(token, BASE + "/sample.txt", new ByteArrayInputStream("sample text".getBytes()));
		Folder fldCat = okmFolder.createSimple(token, BASE_CAT + "/alfa");
		okmFolder.createSimple(token, BASE_CAT + "/beta");
		okmProperty.addCategory(token, docNew.getUuid(), fldCat.getUuid());
		okmProperty.addCategory(token, docNew.getUuid(), BASE_CAT + "/beta");

		// Check properties
		Document doc = okmDocument.getProperties(token, docNew.getUuid());
		doc.getCategories();
	}

	@Test
	public void testFolderAdd() throws PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException, AutomationException, IOException, ParseException, NoSuchGroupException, LockException,
			VersionException {
		Folder fldNew = okmFolder.createSimple(token, BASE + "/sample");
		Folder fldCat = okmFolder.createSimple(token, BASE_CAT + "/alfa");
		okmFolder.createSimple(token, BASE_CAT + "/beta");
		okmProperty.addCategory(token, fldNew.getUuid(), fldCat.getUuid());
		okmProperty.addCategory(token, fldNew.getUuid(), BASE_CAT + "/beta");
	}
}