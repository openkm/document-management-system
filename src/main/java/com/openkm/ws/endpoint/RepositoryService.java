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

import com.openkm.bean.AppVersion;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.module.ModuleManager;
import com.openkm.module.RepositoryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name = "OKMRepository", serviceName = "OKMRepository", targetNamespace = "http://ws.openkm.com")
public class RepositoryService {
	private static Logger log = LoggerFactory.getLogger(RepositoryService.class);

	@WebMethod
	public Folder getRootFolder(@WebParam(name = "token") String token) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getRootFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder root = rm.getRootFolder(token);
		log.debug("getRootFolder: {}", root);
		return root;
	}

	@WebMethod
	public Folder getTrashFolder(@WebParam(name = "token") String token) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getTrashFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder trash = rm.getTrashFolder(token);
		log.debug("getTrashFolder: {}", trash);
		return trash;
	}

	@WebMethod
	public Folder getTemplatesFolder(@WebParam(name = "token") String token) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getTemplatesFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder templatesFolder = rm.getTemplatesFolder(token);
		log.debug("getTemplatesFolder: {}", templatesFolder);
		return templatesFolder;
	}

	@WebMethod
	public Folder getPersonalFolder(@WebParam(name = "token") String token) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getPersonalFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder personalFolder = rm.getPersonalFolder(token);
		log.debug("getPersonalFolder: {}", personalFolder);
		return personalFolder;
	}

	@WebMethod
	public Folder getMailFolder(@WebParam(name = "token") String token) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getMailFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder mailFolder = rm.getMailFolder(token);
		log.debug("getMailFolder: {}", mailFolder);
		return mailFolder;
	}

	@WebMethod
	public Folder getThesaurusFolder(@WebParam(name = "token") String token) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getThesaurusFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder thesaurusFolder = rm.getThesaurusFolder(token);
		log.debug("getThesaurusFolder: {}", thesaurusFolder);
		return thesaurusFolder;
	}

	@WebMethod
	public Folder getCategoriesFolder(@WebParam(name = "token") String token) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getCategoriesFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder categoriesFolder = rm.getCategoriesFolder(token);
		log.debug("getCategoriesFolder: {}", categoriesFolder);
		return categoriesFolder;
	}

	@WebMethod
	public void purgeTrash(@WebParam(name = "token") String token) throws PathNotFoundException, AccessDeniedException,
			LockException, RepositoryException, DatabaseException {
		log.debug("purgeTrash({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		rm.purgeTrash(token);
		log.debug("purgeTrash: void");
	}

	@WebMethod
	public String getUpdateMessage(@WebParam(name = "token") String token) throws RepositoryException, DatabaseException {
		log.debug("getUpdateMessage({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String msg = rm.getUpdateMessage(null);
		log.debug("getUpdateMessage: void");
		return msg;
	}

	@WebMethod
	public String getRepositoryUuid() throws RepositoryException, DatabaseException {
		log.debug("getRepositoryUuid({})");
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String uuid = rm.getRepositoryUuid(null);
		log.debug("getRepositoryUuid: void");
		return uuid;
	}

	@WebMethod
	public boolean hasNode(@WebParam(name = "token") String token, @WebParam(name = "path") String path)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("hasNode({}, {})", token, path);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		boolean ret = rm.hasNode(token, path);
		log.debug("hasNode: {}", ret);
		return ret;
	}

	@WebMethod
	public String getNodePath(@WebParam(name = "token") String token, @WebParam(name = "uuid") String uuid)
			throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getNodePath({}, {})", token, uuid);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String path = rm.getNodePath(token, uuid);
		log.debug("getNodePath: {}", path);
		return path;
	}

	@WebMethod
	public String getNodeUuid(@WebParam(name = "token") String token, @WebParam(name = "path") String path)
			throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getNodeUuid({}, {})", token, path);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String uuid = rm.getNodeUuid(token, path);
		log.debug("getNodeUuid: {}", uuid);
		return uuid;
	}

	@WebMethod
	public AppVersion getAppVersion(@WebParam(name = "token") String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getAppVersion({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		AppVersion appVer = rm.getAppVersion(token);
		log.debug("getAppVersion: {}", appVer);
		return appVer;
	}
}
