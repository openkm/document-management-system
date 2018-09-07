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

package com.openkm.rest.endpoint;

import com.openkm.bean.Note;
import com.openkm.rest.GenericException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Calendar;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description="test-service", value="test-service")
@Path("/test")
public class TestService {
	private static Logger log = LoggerFactory.getLogger(TestService.class);

	@GET
	@Path("/getSimple")
	public String getSimple() throws GenericException {
		try {
			log.info("getSimple()");
			return "sample";
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/sendSimple/{param}")
	public void sendSimple(@PathParam("param") String param) throws GenericException {
		try {
			log.info("sendSimple({})", param);
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getComplex")
	public Note getComplex() throws GenericException {
		try {
			log.info("getComplex()");
			Note note = new Note();
			note.setAuthor("Kiko");
			note.setPath("/one/path");
			note.setText("Test content message");
			note.setDate(Calendar.getInstance());
			return note;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/sendComplex")
	public void sendComplex(Note note) throws GenericException {
		try {
			log.info("sendComplex({})", note);
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/sort1/{array}")
	public String[] sort1(@PathParam("array") String[] array) throws GenericException {
		try {
			log.info("sort1({})", (Object[]) array);

			if (array != null) {
				log.info("sort1: a.length={}", array.length);
				Arrays.sort(array);
			}

			return array;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/sort2/{array}")
	public String[] sort2(@PathParam("array") String[] array) throws GenericException {
		try {
			log.info("sort2({})", (Object[]) array);

			if (array != null) {
				log.info("sort2: a.value={}", (Object[]) array);
				log.info("sort2: a.length={}", array.length);
				Arrays.sort(array);
			}

			return array;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/greetings/{name}")
	public String greetings(@PathParam("name") String name) throws GenericException {
		try {
			log.info("greetings({})", name);
			return "Hello, " + name + "!";
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
