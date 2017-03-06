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

import com.openkm.automation.AutomationException;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.FolderModule;
import com.openkm.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(name = "OKMFolder", serviceName = "OKMFolder", targetNamespace = "http://ws.openkm.com")
public class FolderService {
	private static Logger log = LoggerFactory.getLogger(FolderService.class);

	@WebMethod
	public Folder create(@WebParam(name = "token") String token, @WebParam(name = "fld") Folder fld)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("create({}, {})", token, fld);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder newFolder = fm.create(token, fld);
		log.debug("create: {}", newFolder);
		return newFolder;
	}

	@WebMethod
	public Folder createSimple(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("createSimple({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder fld = new Folder();
		fld.setPath(fldPath);
		Folder newFolder = fm.create(token, fld);
		log.debug("createSimple: {}", newFolder);
		return newFolder;
	}

	@WebMethod
	public Folder getProperties(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, DatabaseException {
		log.debug("getProperties({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder fld = fm.getProperties(token, fldPath);
		log.debug("getProperties: {}", fld);
		return fld;
	}

	@WebMethod
	public void delete(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath)
			throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("delete({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		fm.delete(token, fldPath);
		log.debug("delete: void");
	}

	@WebMethod
	public Folder rename(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath,
	                     @WebParam(name = "newName") String newName) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[]{token, fldPath, newName});
		FolderModule fm = ModuleManager.getFolderModule();
		Folder renamedFolder = fm.rename(token, fldPath, newName);
		log.debug("rename: {}", renamedFolder);
		return renamedFolder;
	}

	@WebMethod
	public void move(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath,
	                 @WebParam(name = "dstPath") String dstPath) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[]{token, fldPath, dstPath});
		FolderModule fm = ModuleManager.getFolderModule();
		fm.move(token, fldPath, dstPath);
		log.debug("move: void");
	}

	@WebMethod
	@Deprecated
	public Folder[] getChilds(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath)
			throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getChilds({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		List<Folder> col = fm.getChilds(token, fldPath);
		Folder[] result = col.toArray(new Folder[col.size()]);
		log.debug("getChilds: {}", result);
		return result;
	}

	@WebMethod
	public Folder[] getChildren(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath)
			throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getChildren({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		List<Folder> col = fm.getChildren(token, fldPath);
		Folder[] result = col.toArray(new Folder[col.size()]);
		log.debug("getChildren: {}", result);
		return result;
	}

	@WebMethod
	public boolean isValid(@WebParam(name = "token") String token, @WebParam(name = "fldPath") String fldPath)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("isValid({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		boolean valid = fm.isValid(token, fldPath);
		log.debug("isValid: {}", valid);
		return valid;
	}

	@WebMethod
	public String getPath(@WebParam(name = "token") String token, @WebParam(name = "uuid") String uuid)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getPath({}, {})", token, uuid);
		FolderModule fm = ModuleManager.getFolderModule();
		String path = fm.getPath(token, uuid);
		log.debug("getPath: {}", path);
		return path;
	}
}
