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

package com.openkm.ws.endpoint;

import com.openkm.bean.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Arrays;

@WebService(name = "OKMTest", serviceName = "OKMTest", targetNamespace = "http://ws.openkm.com")
public class TestService {
	private static Logger log = LoggerFactory.getLogger(TestService.class);

	@WebMethod
	public void simple(@WebParam(name = "param") String param) {
		log.info("simple({})", param);
	}

	@WebMethod
	public void complex(@WebParam(name = "note") Note note) {
		log.info("complex({})", note);
	}

	@WebMethod
	public String[] sort1(@WebParam(name = "array") String[] array) {
		log.info("sort1({})", array);

		if (array != null) {
			log.info("sort1: a.length={}", array.length);
			Arrays.sort(array);
		}

		return array;
	}

	@WebMethod
	public String[] sort2(@WebParam(name = "array") String[] array) {
		log.info("sort2({})", array);

		if (array != null) {
			log.info("sort2: a.value={}", array);
			log.info("sort2: a.length={}", array.length);
			Arrays.sort(array);
		}

		return array;
	}

	@WebMethod
	public String greetings(@WebParam(name = "name") String name) {
		log.info("greetings({})", name);
		return "Hello, " + name + "!";
	}
}
