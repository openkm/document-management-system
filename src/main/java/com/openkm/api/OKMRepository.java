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

package com.openkm.api;

import com.openkm.bean.AppVersion;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.module.ModuleManager;
import com.openkm.module.RepositoryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OKMRepository implements RepositoryModule {
	private static Logger log = LoggerFactory.getLogger(OKMRepository.class);
	private static OKMRepository instance = new OKMRepository();

	private OKMRepository() {
	}

	public static OKMRepository getInstance() {
		return instance;
	}

	@Override
	public Folder getRootFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getRootFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder rootFolder = rm.getRootFolder(token);
		log.debug("getRootFolder: {}", rootFolder);
		return rootFolder;
	}

	@Override
	public Folder getTrashFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getTrashFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder trashFolder = rm.getTrashFolder(token);
		log.debug("getTrashFolder: {}", trashFolder);
		return trashFolder;
	}

	@Override
	public Folder getTrashFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getTrashFolderBase({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder trashFolder = rm.getTrashFolderBase(token);
		log.debug("getTrashFolderBase: {}", trashFolder);
		return trashFolder;
	}

	@Override
	public Folder getTemplatesFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getTemplatesFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder templatesFolder = rm.getTemplatesFolder(token);
		log.debug("getTemplatesFolder: {}", templatesFolder);
		return templatesFolder;
	}

	@Override
	public Folder getPersonalFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getPersonalFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder personalFolder = rm.getPersonalFolder(token);
		log.debug("getPersonalFolder: {}", personalFolder);
		return personalFolder;
	}

	@Override
	public Folder getPersonalFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getPersonalFolderBase({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder personalFolder = rm.getPersonalFolderBase(token);
		log.debug("getPersonalFolderBase: {}", personalFolder);
		return personalFolder;
	}

	@Override
	public Folder getMailFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getMailFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder mailFolder = rm.getMailFolder(token);
		log.debug("getMailFolder: {}", mailFolder);
		return mailFolder;
	}

	@Override
	public Folder getMailFolderBase(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getMailFolderBase({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder mailFolder = rm.getMailFolderBase(token);
		log.debug("getMailFolderBase: {}", mailFolder);
		return mailFolder;
	}

	@Override
	public Folder getThesaurusFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getThesaurusFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder thesaurusFolder = rm.getThesaurusFolder(token);
		log.debug("getThesaurusFolder: {}", thesaurusFolder);
		return thesaurusFolder;
	}

	@Override
	public Folder getCategoriesFolder(String token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getCategoriesFolder({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		Folder categoriesFolder = rm.getCategoriesFolder(token);
		log.debug("getCategoriesFolder: {}", categoriesFolder);
		return categoriesFolder;
	}

	@Override
	public void purgeTrash(String token) throws PathNotFoundException, AccessDeniedException, LockException, RepositoryException,
			DatabaseException {
		log.debug("purgeTrash({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		rm.purgeTrash(token);
		log.debug("purgeTrash: void");
	}

	@Override
	public String getUpdateMessage(String token) throws RepositoryException {
		log.debug("getUpdateMessage({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String updateMessage = rm.getUpdateMessage(token);
		log.debug("getUpdateMessage: {}", updateMessage);
		return updateMessage;
	}

	@Override
	public String getRepositoryUuid(String token) throws RepositoryException {
		log.debug("getRepositoryUuid({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String uuid = rm.getRepositoryUuid(token);
		log.debug("getRepositoryUuid: {}", uuid);
		return uuid;
	}

	@Override
	public boolean hasNode(String token, String path) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("hasNode({})", token, path);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		boolean ret = rm.hasNode(token, path);
		log.debug("hasNode: {}", ret);
		return ret;
	}

	@Override
	public String getNodePath(String token, String uuid) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getNodePath({}, {})", token, uuid);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String ret = rm.getNodePath(token, uuid);
		log.debug("getNodePath: {}", ret);
		return ret;
	}

	@Override
	public String getNodeUuid(String token, String path) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getNodeUuid({}, {})", token, path);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		String ret = rm.getNodeUuid(token, path);
		log.debug("getNodeUuid: {}", ret);
		return ret;
	}

	public AppVersion getAppVersion(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getAppVersion({})", token);
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		AppVersion ret = rm.getAppVersion(token);
		log.debug("getAppVersion: {}", ret);
		return ret;
	}

	@Override
	public void copyAttributes(String token, String srcId, String dstId, ExtendedAttributes extAttr) throws AccessDeniedException,
			PathNotFoundException, DatabaseException {
		log.debug("copyAttributes({}, {}, {}, {})", new Object[]{token, srcId, dstId, extAttr});
		RepositoryModule rm = ModuleManager.getRepositoryModule();
		rm.copyAttributes(token, srcId, dstId, extAttr);
		log.debug("copyAttributes: void");
	}
}
