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

import com.openkm.core.Cron;
import com.openkm.util.ExecutionUtils;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ExecutionTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(ExecutionTest.class);
	private static final String BASE_DIR = "src/test/resources/execution";
	private static final String RESULT = "Hola, mundo!";
	private static final String EMPTY = "";
	
	public ExecutionTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		ExecutionTest test = new ExecutionTest("main");
		test.setUp();
		test.testBeanShell();
		test.testJar();
		test.tearDown();
	}

	public void testBeanShell() throws Exception {
		log.debug("testBeanShell()");
		File bsh = new File(BASE_DIR + "/beanShellSample.bsh");
		Object[] result = ExecutionUtils.runScript(bsh);
		assertEquals(RESULT, result[0]);
		assertEquals(EMPTY, result[1]);
		assertEquals(EMPTY, result[2]);
	}
	
	public void testJar() throws Exception {
		log.debug("testJar()");
		File jar = new File(BASE_DIR + "/JarSample.jar");
		Object result = ExecutionUtils.getInstance().runJar(jar, Cron.CRON_TASK);
		assertEquals(RESULT, result);
	}
}
