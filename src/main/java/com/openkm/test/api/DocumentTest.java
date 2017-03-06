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
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.Version;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.vernum.MajorMinorVersionNumerationAdapter;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;

public class DocumentTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(DocumentTest.class);
	private OKMRepository okmRepo = OKMRepository.getInstance();
	private OKMDocument okmDocument = OKMDocument.getInstance();
	private OKMFolder okmFolder = OKMFolder.getInstance();
	private OKMAuth okmAuth = OKMAuth.getInstance();
	private String BASE = Config.UNIT_TESTING_FOLDER;
	private String token;

	public DocumentTest(String name) {
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
	public void testCreate() throws IOException, UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException, AutomationException {
		Document roDoc = new Document();
		roDoc.setPath(BASE + "/ro.txt");
		Document roNew = okmDocument.create(token, roDoc, new ByteArrayInputStream("sample text".getBytes()));
		assertNotNull(roNew);
		assertEquals(roDoc.getPath(), roNew.getPath());
		assertEquals(Config.UNIT_TESTING_USER, roNew.getAuthor());
		assertEquals("text/plain", roNew.getMimeType());
		catchException(okmDocument).create(token, roDoc, new ByteArrayInputStream("sample text".getBytes()));
		assertTrue(caughtException() instanceof ItemExistsException);
	}

	@Test
	public void testCreateSimple() throws IOException, UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException, AutomationException {
		String locContent = "Sample text";
		Document piNew = okmDocument.createSimple(token, BASE + "/pi.txt", new ByteArrayInputStream(locContent.getBytes()));
		assertNotNull(piNew);
		assertEquals(BASE + "/pi.txt", piNew.getPath());
		assertEquals(Config.UNIT_TESTING_USER, piNew.getAuthor());
		assertEquals("text/plain", piNew.getMimeType());
		InputStream is = okmDocument.getContent(token, BASE + "/pi.txt", false);
		String remContent = IOUtils.toString(is);
		assertEquals(locContent, remContent);

		catchException(okmDocument).createSimple(token, BASE + "/pi.txt", new ByteArrayInputStream("none".getBytes()));
		assertTrue(caughtException() instanceof ItemExistsException);
	}

	@Test
	public void testVersions() throws IOException, UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException, AutomationException, LockException, VersionException, InterruptedException {
		Document omegaNew = okmDocument.createSimple(token, BASE + "/omega.txt", new ByteArrayInputStream("sample text".getBytes()));
		assertNotNull(omegaNew);
		assertEquals(Config.UNIT_TESTING_USER, omegaNew.getAuthor());
		assertNotNull(omegaNew.getActualVersion());
		assertEquals(Config.UNIT_TESTING_USER, omegaNew.getActualVersion().getAuthor());
		assertEquals("1.0", omegaNew.getActualVersion().getName());

		List<Version> verHist = okmDocument.getVersionHistory(token, BASE + "/omega.txt");
		assertNotNull(verHist);
		assertFalse(verHist.isEmpty());
		assertEquals(1, verHist.size());

		// New version
		okmDocument.checkout(token, BASE + "/omega.txt");
		Thread.sleep(1000); // Version date need to be different
		Version ver = okmDocument.checkin(token, BASE + "/omega.txt", new ByteArrayInputStream("sample text 1.1".getBytes()),
				"New version 1.1");
		assertEquals(Config.UNIT_TESTING_USER, ver.getAuthor());
		assertEquals("1.1", ver.getName());
		assertEquals("New version 1.1", ver.getComment());

		// Check versions
		verHist = okmDocument.getVersionHistory(token, BASE + "/omega.txt");
		log.info("{}", verHist);
		assertNotNull(verHist);
		assertFalse(verHist.isEmpty());
		assertEquals(2, verHist.size());
		assertEquals("1.0", verHist.get(0).getName());
		assertEquals("1.1", verHist.get(1).getName());

		// Clean history
		okmDocument.purgeVersionHistory(token, BASE + "/omega.txt");
		verHist = okmDocument.getVersionHistory(token, BASE + "/omega.txt");
		assertNotNull(verHist);
		assertFalse(verHist.isEmpty());
		assertEquals(1, verHist.size());
		assertEquals("1.1", verHist.get(0).getName());

		// New version
		okmDocument.checkout(token, BASE + "/omega.txt");
		Thread.sleep(1000); // Version date need to be different
		ver = okmDocument.checkin(token, BASE + "/omega.txt", new ByteArrayInputStream("sample text 1.2".getBytes()), "New version 1.2");
		assertEquals(Config.UNIT_TESTING_USER, ver.getAuthor());
		assertEquals("1.2", ver.getName());
		assertEquals("New version 1.2", ver.getComment());

		// Check versions
		verHist = okmDocument.getVersionHistory(token, BASE + "/omega.txt");
		log.info("{}", verHist);
		assertNotNull(verHist);
		assertFalse(verHist.isEmpty());
		assertEquals(2, verHist.size());
		assertEquals("1.1", verHist.get(0).getName());
		assertEquals("1.2", verHist.get(1).getName());

		// New version
		okmDocument.checkout(token, BASE + "/omega.txt");
		Thread.sleep(1000); // Version date need to be different
		ver = okmDocument.checkin(token, BASE + "/omega.txt", new ByteArrayInputStream("sample text 2.0".getBytes()), "New version 2.0",
				MajorMinorVersionNumerationAdapter.MAJOR);
		assertEquals(Config.UNIT_TESTING_USER, ver.getAuthor());
		assertEquals("2.0", ver.getName());
		assertEquals("New version 2.0", ver.getComment());

		// New version
		okmDocument.checkout(token, BASE + "/omega.txt");
		Thread.sleep(1000); // Version date need to be different
		ver = okmDocument.checkin(token, BASE + "/omega.txt", new ByteArrayInputStream("sample text 2.1".getBytes()), "New version 2.1");
		assertEquals(Config.UNIT_TESTING_USER, ver.getAuthor());
		assertEquals("2.1", ver.getName());
		assertEquals("New version 2.1", ver.getComment());

		// Check versions
		verHist = okmDocument.getVersionHistory(token, BASE + "/omega.txt");
		log.info("{}", verHist);
		assertNotNull(verHist);
		assertFalse(verHist.isEmpty());
		assertEquals(4, verHist.size());
		assertEquals("1.1", verHist.get(0).getName());
		assertEquals("1.2", verHist.get(1).getName());
		assertEquals("2.0", verHist.get(2).getName());
		assertEquals("2.1", verHist.get(3).getName());

		// Clean history
		okmDocument.purgeVersionHistory(token, BASE + "/omega.txt");
		verHist = okmDocument.getVersionHistory(token, BASE + "/omega.txt");
		assertNotNull(verHist);
		assertFalse(verHist.isEmpty());
		assertEquals(1, verHist.size());
		assertEquals("2.1", verHist.get(0).getName());
	}

	/**
	 * Path with dangerous characters are encoded as entities.
	 */
	@Test
	public void testCharactersAmp() throws IOException, UnsupportedMimeTypeException, FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException {
		Document piNew = okmDocument.createSimple(token, BASE + "/pi & ro.txt", new ByteArrayInputStream("sample text".getBytes()));
		assertNotNull(piNew);
		assertEquals(BASE + "/pi &amp; ro.txt", piNew.getPath());
		assertTrue(okmRepo.hasNode(token, BASE + "/pi &amp; ro.txt"));
		assertTrue(okmRepo.hasNode(token, BASE + "/pi & ro.txt"));
		catchException(okmDocument).createSimple(token, BASE + "/pi & ro.txt", new ByteArrayInputStream("sample text".getBytes()));
		assertTrue(caughtException() instanceof ItemExistsException);

		List<Document> children = okmDocument.getChildren(token, BASE);
		assertFalse(children.isEmpty());
		assertEquals(1, children.size());
		assertNotNull(children.get(0));
		assertEquals(BASE + "/pi &amp; ro.txt", children.get(0).getPath());
	}

	/**
	 * Path with dangerous characters are encoded as entities.
	 */
	@Test
	public void testCharactersLtGt() throws IOException, UnsupportedMimeTypeException, FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException {
		Document piNew = okmDocument.createSimple(token, BASE + "/pi <> ro.txt", new ByteArrayInputStream("sample text".getBytes()));
		assertNotNull(piNew);
		assertEquals(BASE + "/pi &lt;&gt; ro.txt", piNew.getPath());
		assertTrue(okmRepo.hasNode(token, BASE + "/pi &lt;&gt; ro.txt"));
		assertTrue(okmRepo.hasNode(token, BASE + "/pi <> ro.txt"));
		catchException(okmDocument).createSimple(token, BASE + "/pi <> ro.txt", new ByteArrayInputStream("sample text".getBytes()));
		assertTrue(caughtException() instanceof ItemExistsException);

		List<Document> children = okmDocument.getChildren(token, BASE);
		assertFalse(children.isEmpty());
		assertEquals(1, children.size());
		assertNotNull(children.get(0));
		assertEquals(BASE + "/pi &lt;&gt; ro.txt", children.get(0).getPath());
	}
}