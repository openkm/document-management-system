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

import com.lowagie.text.DocumentException;
import com.openkm.util.OOUtils;
import com.openkm.util.PDFUtils;
import com.openkm.util.TemplateUtils;
import freemarker.template.TemplateException;
import junit.framework.TestCase;
import net.sf.jooreports.templates.DocumentTemplateException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Test template engines
 */
public class TemplateTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(TemplateTest.class);
	private static final String BASE_DIR = "src/test/resources";
	
	public TemplateTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		TemplateTest test = new TemplateTest("main");
		test.setUp();
		test.testPdf();
		test.testOpenOffice();
		test.testHtml();
		test.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		log.debug("setUp()");
	}

	@Override
	protected void tearDown() throws Exception {
		log.debug("tearDown()");
	}

	public void testPdf() throws IOException, DocumentException, TemplateException {
		log.debug("testPdf()");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("okp_tpl_name", "el nombre");
		model.put("okp_tpl_bird_date", new Date());
		model.put("okp_tpl_language", "el lenguaje");
		InputStream input = new FileInputStream(BASE_DIR + "/templates/sample.pdf");
		OutputStream output = new FileOutputStream(BASE_DIR + "/templates/sample_out.pdf");
		PDFUtils.fillForm(input, model, output);
		input.close();
		output.close();
	}

	public void testOpenOffice() throws IOException, DocumentTemplateException {
		log.debug("testOpenOffice()");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("okp_tpl_name", "el nombre");
		model.put("okp_tpl_bird_date", new Date());
		model.put("okp_tpl_language", "el lenguaje");
		InputStream input = new FileInputStream(BASE_DIR + "/templates/sample.odt");
		OutputStream output = new FileOutputStream(BASE_DIR + "/templates/sample_out.odt");
		OOUtils.fillTemplate(input, model, output);
		input.close();
		output.close();
	}
	
	public void testHtml() throws IOException, TemplateException {
		log.debug("testHtml()");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("okp_tpl_name", "el nombre");
		model.put("okp_tpl_bird_date", new Date());
		model.put("okp_tpl_language", "el lenguaje");
		InputStream input = new FileInputStream(BASE_DIR + "/templates/sample.html");
		OutputStream output = new FileOutputStream(BASE_DIR + "/templates/sample_out.html");
		String in = IOUtils.toString(input);
		String out = TemplateUtils.replace("sample", in, model);
		IOUtils.write(out, output);
		input.close();
		output.close();
	}
}
