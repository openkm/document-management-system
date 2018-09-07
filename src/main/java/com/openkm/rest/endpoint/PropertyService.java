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

import com.openkm.module.ModuleManager;
import com.openkm.module.PropertyModule;
import com.openkm.rest.GenericException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description="property-service", value="property-service")
@Path("/property")
public class PropertyService {
	private static Logger log = LoggerFactory.getLogger(PropertyService.class);

	@POST
	@Path("/addCategory")
	public void addCategory(@QueryParam("nodeId") String nodeId, @QueryParam("catId") String catId) throws GenericException {
		try {
			log.debug("addCategory({}, {})", nodeId, catId);
			PropertyModule pm = ModuleManager.getPropertyModule();
			pm.addCategory(null, nodeId, catId);
			log.debug("addCategory: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/removeCategory")
	public void removeCategory(@QueryParam("nodeId") String nodeId, @QueryParam("catId") String catId) throws GenericException {
		try {
			log.debug("removeCategory({}, {})", nodeId, catId);
			PropertyModule pm = ModuleManager.getPropertyModule();
			pm.removeCategory(null, nodeId, catId);
			log.debug("removeCategory: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/addKeyword")
	public void addKeyword(@QueryParam("nodeId") String nodeId, @QueryParam("keyword") String keyword) throws GenericException {
		try {
			log.debug("addKeyword({}, {})", nodeId, keyword);
			PropertyModule pm = ModuleManager.getPropertyModule();
			pm.addKeyword(null, nodeId, keyword);
			log.debug("addKeyword: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/removeKeyword")
	public void removeKeyword(@QueryParam("nodeId") String nodeId, @QueryParam("keyword") String keyword) throws GenericException {
		try {
			log.debug("removeKeyword({}, {})", nodeId, keyword);
			PropertyModule pm = ModuleManager.getPropertyModule();
			pm.removeKeyword(null, nodeId, keyword);
			log.debug("removeKeyword: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/setEncryption")
	public void setEncryption(@QueryParam("nodeId") String nodeId, @QueryParam("cipherName") String cipherName) throws GenericException {
		try {
			log.debug("setEncryption({}, {})", nodeId, cipherName);
			PropertyModule pm = ModuleManager.getPropertyModule();
			pm.setEncryption(null, nodeId, cipherName);
			log.debug("setEncryption: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/unsetEncryption")
	public void unsetEncryption(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("unsetEncryption({})", nodeId);
			PropertyModule pm = ModuleManager.getPropertyModule();
			pm.unsetEncryption(null, nodeId);
			log.debug("unsetEncryption: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/setSigned")
	public void setSigned(@QueryParam("nodeId") String nodeId, @QueryParam("signed") boolean signed) throws GenericException {
		try {
			log.debug("setSigned({}, {})", nodeId, signed);
			PropertyModule pm = ModuleManager.getPropertyModule();
			pm.setSigned(null, nodeId, signed);
			log.debug("setSigned: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}