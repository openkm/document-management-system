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
import com.openkm.bean.Note;
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
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;

public class NoteTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(NoteTest.class);
	private OKMRepository okmRepo = OKMRepository.getInstance();
	private OKMDocument okmDocument = OKMDocument.getInstance();
	private OKMFolder okmFolder = OKMFolder.getInstance();
	private OKMNote okmNote = OKMNote.getInstance();
	private OKMAuth okmAuth = OKMAuth.getInstance();
	private String BASE = Config.UNIT_TESTING_FOLDER;
	private String TEST_USER = "test";
	private String TEST_PASSWORD = "test";
	private String token;
	private String tokenUsr;

	public NoteTest(String name) {
		super(name);
	}

	@Before
	public void setUp() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException,
			ExtensionException, AutomationException, PrincipalAdapterException {
		log.debug("setUp()");
		token = okmAuth.login(Config.UNIT_TESTING_USER, Config.UNIT_TESTING_PASSWORD);
		okmAuth.createUser(token, TEST_USER, TEST_PASSWORD, "test@openkm.com", "Test User", true);
		okmAuth.assignRole(token, TEST_USER, Config.DEFAULT_USER_ROLE);
		tokenUsr = okmAuth.login(TEST_USER, TEST_PASSWORD);

		// Create base folder
		catchException(okmFolder).createSimple(token, BASE);
	}

	@After
	public void tearDown() throws PrincipalAdapterException, LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("tearDown()");

		// Clean folders and ignore if do no exists
		catchException(okmFolder).delete(token, BASE);
		catchException(okmRepo).purgeTrash(token);

		okmAuth.deleteUser(token, TEST_USER);
		okmAuth.logout(tokenUsr);
		okmAuth.logout(token);
	}

	@Test
	public void testDocumentCreate() throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		Document docNew = okmDocument.createSimple(token, BASE + "/sample.txt", new ByteArrayInputStream("sample text".getBytes()));
		Note noteNew = okmNote.add(token, docNew.getUuid(), "This is a note");
		assertNotNull(noteNew);
		assertEquals("This is a note", noteNew.getText());
		assertEquals(Config.UNIT_TESTING_USER, noteNew.getAuthor());

		Note note = okmNote.get(token, noteNew.getPath());
		assertNotNull(note);
		assertEquals(noteNew.getDate(), note.getDate());
		assertEquals("This is a note", note.getText());

		// Check note list
		List<Note> notes = okmNote.list(token, docNew.getUuid());
		assertNotNull(notes);
		assertEquals(1, notes.size());
		assertNotNull(notes.get(0));
		assertEquals(Config.UNIT_TESTING_USER, notes.get(0).getAuthor());
		assertEquals("This is a note", notes.get(0).getText());

		// Delete
		okmNote.delete(token, note.getPath());
		notes = okmNote.list(token, docNew.getUuid());
		assertNotNull(notes);
		assertEquals(0, notes.size());
	}

	@Test
	public void testDocumentLockCreate() throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		Document docNew = okmDocument.createSimple(token, BASE + "/sample.txt", new ByteArrayInputStream("sample text".getBytes()));
		okmDocument.lock(token, docNew.getUuid());
		Note noteNew = okmNote.add(token, docNew.getUuid(), "This is a note");
		assertNotNull(noteNew);
		assertEquals("This is a note", noteNew.getText());
		assertEquals(Config.UNIT_TESTING_USER, noteNew.getAuthor());

		// Finally unlock document
		okmDocument.unlock(token, docNew.getUuid());
	}

	@Test
	public void testFolderCreate() throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		Folder fldNew = okmFolder.createSimple(token, BASE + "/sample");
		Note noteNew = okmNote.add(token, fldNew.getUuid(), "This is a note");
		assertNotNull(noteNew);
		assertEquals("This is a note", noteNew.getText());
		assertEquals(Config.UNIT_TESTING_USER, noteNew.getAuthor());

		Note note = okmNote.get(token, noteNew.getPath());
		assertNotNull(note);
		assertEquals(noteNew.getDate(), note.getDate());
		assertEquals("This is a note", note.getText());

		// Check note list
		List<Note> notes = okmNote.list(token, fldNew.getUuid());
		assertNotNull(notes);
		assertEquals(1, notes.size());
		assertNotNull(notes.get(0));
		assertEquals(Config.UNIT_TESTING_USER, notes.get(0).getAuthor());
		assertEquals("This is a note", notes.get(0).getText());

		// Delete note
		okmNote.delete(token, note.getPath());
		notes = okmNote.list(token, fldNew.getUuid());
		assertNotNull(notes);
		assertEquals(0, notes.size());
	}
}