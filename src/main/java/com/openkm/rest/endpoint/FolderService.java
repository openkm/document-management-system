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

import com.openkm.api.OKMFolder;
import com.openkm.bean.ContentInfo;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.core.MimeTypeConfig;
import com.openkm.module.FolderModule;
import com.openkm.module.ModuleManager;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.FolderList;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description = "folder-service", value = "folder-service")
@Path("/folder")
public class FolderService {
	private static Logger log = LoggerFactory.getLogger(FolderService.class);

	@POST
	@Path("/create")
	// The "fld" parameter comes in the POST request body (encoded as XML or JSON).
	public Folder create(Folder fld) throws GenericException {
		try {
			log.debug("create({})", fld);
			FolderModule fm = ModuleManager.getFolderModule();
			Folder newFolder = fm.create(null, fld);
			log.debug("create: {}", newFolder);
			return newFolder;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/createSimple")
	// The "fldPath" parameter comes in the POST request body.
	public Folder createSimple(String fldPath) throws GenericException {
		try {
			log.debug("createSimple({})", fldPath);
			FolderModule fm = ModuleManager.getFolderModule();
			Folder fld = new Folder();
			fld.setPath(fldPath);
			Folder newFolder = fm.create(null, fld);
			log.debug("createSimple: {}", newFolder);
			return newFolder;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getProperties")
	public Folder getProperties(@QueryParam("fldId") String fldId) throws GenericException {
		try {
			log.debug("getProperties({})", fldId);
			FolderModule fm = ModuleManager.getFolderModule();
			Folder fld = fm.getProperties(null, fldId);
			log.debug("getProperties: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/delete")
	public void delete(@QueryParam("fldId") String fldId) throws GenericException {
		try {
			log.debug("delete({})", fldId);
			FolderModule fm = ModuleManager.getFolderModule();
			fm.delete(null, fldId);
			log.debug("delete: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/purge")
	public void purge(@QueryParam("fldId") String fldId) throws GenericException {
		try {
			log.debug("purge({})", fldId);
			FolderModule fm = ModuleManager.getFolderModule();
			fm.purge(null, fldId);
			log.debug("purge: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/rename")
	public Folder rename(@QueryParam("fldId") String fldId, @QueryParam("newName") String newName) throws GenericException {
		try {
			log.debug("rename({}, {})", new Object[]{fldId, newName});
			FolderModule fm = ModuleManager.getFolderModule();
			Folder renamedFolder = fm.rename(null, fldId, newName);
			log.debug("rename: {}", renamedFolder);
			return renamedFolder;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/move")
	public void move(@QueryParam("fldId") String fldId, @QueryParam("dstId") String dstId) throws GenericException {
		try {
			log.debug("move({}, {}, {})", new Object[]{fldId, dstId});
			FolderModule fm = ModuleManager.getFolderModule();
			fm.move(null, fldId, dstId);
			log.debug("move: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/copy")
	public void copy(@QueryParam("fldId") String fldId, @QueryParam("dstId") String dstId) throws GenericException {
		try {
			log.debug("copy({},{})", fldId, dstId);
			FolderModule fm = ModuleManager.getFolderModule();
			fm.copy(null, fldId, dstId);
			log.debug("copy: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/extendedCopy")
	public void extendedCopy(@QueryParam("fldId") String fldId, @QueryParam("dstId") String dstId,
			@QueryParam("categories") boolean categories, @QueryParam("keywords") boolean keywords,
			@QueryParam("propertyGroups") boolean propertyGroups, @QueryParam("notes") boolean notes,
			@QueryParam("wiki") boolean wiki) throws GenericException {
		try {
			log.debug("extendedCopy({}, {}, {}, {}, {}, {}, {})",
					new Object[]{fldId, dstId, categories, keywords, propertyGroups, notes, wiki});
			FolderModule dm = ModuleManager.getFolderModule();
			ExtendedAttributes extAttr = new ExtendedAttributes();
			extAttr.setCategories(categories);
			extAttr.setKeywords(keywords);
			extAttr.setNotes(notes);
			extAttr.setPropertyGroups(propertyGroups);
			extAttr.setWiki(wiki);
			dm.extendedCopy(null, fldId, dstId, extAttr);
			log.debug("extendedCopy: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getChildren")
	public FolderList getChildren(@QueryParam("fldId") String fldId) throws GenericException {
		try {
			log.debug("getChildren({})", fldId);
			FolderModule fm = ModuleManager.getFolderModule();
			FolderList fl = new FolderList();
			fl.getList().addAll(fm.getChildren(null, fldId));
			log.debug("getChildren: {}", fl);
			return fl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getContentInfo")
	public ContentInfo getContentInfo(@QueryParam("fldId") String fldId) throws GenericException {
		try {
			log.debug("getContentInfo({})", fldId);
			FolderModule fm = ModuleManager.getFolderModule();
			ContentInfo contentInfo = fm.getContentInfo(null, fldId);
			log.debug("getContentInfo: {}", contentInfo);
			return contentInfo;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/isValid")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Boolean isValid(@QueryParam("fldId") String fldId) throws GenericException {
		try {
			log.debug("isValid({})", fldId);
			FolderModule fm = ModuleManager.getFolderModule();
			boolean valid = fm.isValid(null, fldId);
			log.debug("isValid: {}", valid);
			return valid;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getPath/{uuid}")
	public String getPath(@PathParam("uuid") String uuid) throws GenericException {
		try {
			log.debug("getPath({})", uuid);
			FolderModule fm = ModuleManager.getFolderModule();
			String path = fm.getPath(null, uuid);
			log.debug("getPath: {}", path);
			return path;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/createMissingFolders")
	public void createMissingFolders(@QueryParam("fldPath") String fldPath) throws GenericException {
		try {
			log.debug("createMissingFolders({})", fldPath);
			OKMFolder.getInstance().createMissingFolders(null, fldPath);
			log.debug("createMissingFolders: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}

