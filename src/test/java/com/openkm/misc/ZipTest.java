/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2017  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.misc;

import junit.framework.TestCase;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Test zip filename encodings
 */
public class ZipTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(ZipTest.class);

	public ZipTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		ZipTest test = new ZipTest("main");
		test.setUp();
		test.testJava();
		test.testApache();
		test.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		log.debug("setUp()");
	}

	@Override
	protected void tearDown() throws Exception {
		log.debug("tearDown()");
		//FileUtils.deleteQuietly(zip);
	}

	public void testJava() throws IOException {
		log.debug("testJava()");
		File zip = File.createTempFile("java_", ".zip");
		
		// Create zip
		FileOutputStream fos = new FileOutputStream(zip);
		ZipOutputStream zos = new ZipOutputStream(fos);
		zos.putNextEntry(new ZipEntry("coñeta"));
		zos.closeEntry();
		zos.close();
		
		// Read zip
		FileInputStream fis = new FileInputStream(zip);
		ZipInputStream zis = new ZipInputStream(fis);
		ZipEntry ze = zis.getNextEntry();
		System.out.println(ze.getName());
		assertEquals(ze.getName(), "coñeta");
		zis.close();
	}

	public void testApache() throws IOException, ArchiveException {
		log.debug("testApache()");
		File zip = File.createTempFile("apache_", ".zip");
		
		// Create zip
		FileOutputStream fos = new FileOutputStream(zip);
		ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream("zip", fos);
		aos.putArchiveEntry(new ZipArchiveEntry("coñeta"));
		aos.closeArchiveEntry();
		aos.close();
		
		// Read zip
		FileInputStream fis = new FileInputStream(zip);
		ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream("zip", fis);
		ZipArchiveEntry zae = (ZipArchiveEntry) ais.getNextEntry();
		assertEquals(zae.getName(), "coñeta");
		ais.close();
	}
}
