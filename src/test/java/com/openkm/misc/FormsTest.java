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

import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.*;
import com.openkm.util.FormUtils;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FormsTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(FormsTest.class);
	private static final String BASE_DIR = "src/test/resources";
	
	public FormsTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		FormsTest test = new FormsTest("main");
		test.setUp();
		test.testWorkflow();
		test.testPropertyGroups();
		test.tearDown();
	}

	public void testWorkflow() throws Exception {
		log.debug("testWorkflow()");
		FileInputStream fis = new FileInputStream(BASE_DIR + "/forms.xml");
		Map<String, List<FormElement>> forms = FormUtils.parseWorkflowForms(fis);
		assertFalse(forms.isEmpty());
		fis.close();
		
		// Task "start"
		List<FormElement> formStart = forms.get("start");
		assertNotNull(formStart);
		
		Iterator<FormElement> formStartIt = formStart.iterator();
		FormElement startFe = formStartIt.next();
		assertTrue(startFe instanceof Input);
		assertEquals(((Input) startFe).getLabel(), "Quantity");
		
		startFe = formStartIt.next();
		assertTrue(startFe instanceof Button);
		assertEquals(((Button) startFe).getLabel(), "Save");
		
		assertFalse(formStartIt.hasNext());
		
		// Task "user_info"
		List<FormElement> formUserInfo = forms.get("user_info");
		assertNotNull(formUserInfo);
		
		Iterator<FormElement> formUserInfoIt = formUserInfo.iterator();
		FormElement userInputFe = formUserInfoIt.next();
		assertTrue(userInputFe instanceof Input);
		assertEquals(((Input) userInputFe).getLabel(), "Name");
		assertEquals(((Input) userInputFe).getName(), "name");
		
		userInputFe = formUserInfoIt.next();
		assertTrue(userInputFe instanceof Input);
		assertEquals(((Input) userInputFe).getLabel(), "Surname");
		assertEquals(((Input) userInputFe).getName(), "surname");
		
		userInputFe = formUserInfoIt.next();
		assertTrue(userInputFe instanceof TextArea);
		assertEquals(((TextArea) userInputFe).getLabel(), "Info");
		assertEquals(((TextArea) userInputFe).getName(), "info");
		
		userInputFe = formUserInfoIt.next();
		assertTrue(userInputFe instanceof Select);
		assertEquals(((Select) userInputFe).getLabel(), "Type");
		assertEquals(((Select) userInputFe).getName(), "type");
		assertEquals(((Select) userInputFe).getType(), "simple");
		assertEquals(((Select) userInputFe).getOptions().size(), 3);
		
		Iterator<Option> it = ((Select) userInputFe).getOptions().iterator();
		Option opt = it.next();
		assertEquals(opt.getLabel(), "Type 1");
		assertEquals(opt.getValue(), "t1");
		assertFalse(opt.isSelected());
		
		opt = it.next();
		assertEquals(opt.getLabel(), "Type 2");
		assertEquals(opt.getValue(), "t2");
		assertTrue(opt.isSelected());
		
		opt = it.next();
		assertEquals(opt.getLabel(), "Type 3");
		assertEquals(opt.getValue(), "t3");
		assertFalse(opt.isSelected());
				
		userInputFe = formUserInfoIt.next();
		assertTrue(userInputFe instanceof Button);
		assertEquals(((Button) userInputFe).getLabel(), "Goto 1");
		assertEquals(((Button) userInputFe).getTransition(), "route 1");
				
		userInputFe = formUserInfoIt.next();
		assertTrue(userInputFe instanceof Button);
		assertEquals(((Button) userInputFe).getLabel(), "Goto 2");
		assertEquals(((Button) userInputFe).getTransition(), "route 2");
		
		assertFalse(formUserInfoIt.hasNext());
		
		// Task "download"
		List<FormElement> formDownload = forms.get("download");
		assertNotNull(formDownload);
		
		Iterator<FormElement> formDownloadIt = formDownload.iterator();
		FormElement downloadFe = formDownloadIt.next();
		assertTrue(downloadFe instanceof Download);
		assertEquals(((Download) downloadFe).getLabel(), "Download");
		
		Download download = (Download) downloadFe;
		assertNotNull(download.getNodes());
		assertFalse(download.getNodes().isEmpty());
		
		downloadFe = formDownloadIt.next();
		assertTrue(downloadFe instanceof Button);
		assertEquals(((Button) downloadFe).getLabel(), "Next");
		
		assertFalse(formDownloadIt.hasNext());
	}
	
	public void testPropertyGroups() throws Exception {
		String pgForm = BASE_DIR + "/PropertyGroups.xml";
		Map<PropertyGroup, List<FormElement>> pgForms = FormUtils.parsePropertyGroupsForms(pgForm);
		assertFalse(pgForms.isEmpty());
		
		List<FormElement> consulting = FormUtils.getPropertyGroupForms(pgForms, "okg:consulting");
		assertNotNull(consulting);
		List<FormElement> technology = FormUtils.getPropertyGroupForms(pgForms, "okg:technology");
		assertNotNull(technology);
		
		Iterator<FormElement> consultingIt = consulting.iterator();
		FormElement consultingFe = consultingIt.next();
		assertTrue(consultingFe instanceof Input);
		assertEquals(((Input) consultingFe).getLabel(), "Name");
		assertEquals(((Input) consultingFe).getName(), "okp:consulting.name");
		
		consultingFe = consultingIt.next();
		assertTrue(consultingFe instanceof TextArea);
		assertEquals(((TextArea) consultingFe).getLabel(), "Comment");
		assertEquals(((TextArea) consultingFe).getName(), "okp:consulting.comment");
		
		assertFalse(consultingIt.hasNext());
		
		Iterator<FormElement> technologyIt = technology.iterator();
		FormElement technologyFe = technologyIt.next();
		assertTrue(technologyFe instanceof Select);
		assertEquals(((Select) technologyFe).getLabel(), "Language");
		assertEquals(((Select) technologyFe).getName(), "okp:technology.language");
		
		technologyFe = technologyIt.next();
		assertTrue(technologyFe instanceof Input);
		assertEquals(((Input) technologyFe).getLabel(), "Comment");
		assertEquals(((Input) technologyFe).getName(), "okp:technology.comment");
		
		technologyFe = technologyIt.next();
		assertTrue(technologyFe instanceof TextArea);
		assertEquals(((TextArea) technologyFe).getLabel(), "Description");
		assertEquals(((TextArea) technologyFe).getName(), "okp:technology.description");
		
		assertFalse(technologyIt.hasNext());
	}
}
