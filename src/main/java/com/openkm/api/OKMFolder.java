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

import com.openkm.automation.AutomationException;
import com.openkm.bean.ContentInfo;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.FolderModule;
import com.openkm.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author pavila
 */
public class OKMFolder implements FolderModule {
	private static Logger log = LoggerFactory.getLogger(OKMFolder.class);
	private static OKMFolder instance = new OKMFolder();

	private OKMFolder() {
	}

	public static OKMFolder getInstance() {
		return instance;
	}

	@Override
	public Folder create(String token, Folder fld) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("create({}, {})", token, fld);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder newFld = fm.create(token, fld);
		log.debug("create: {}", newFld);
		return newFld;
	}

	public Folder createSimple(String token, String fldId) throws PathNotFoundException, ItemExistsException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("createSimple({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder fld = new Folder();
		fld.setPath(fldId);
		Folder newFolder = fm.create(token, fld);
		log.debug("createSimple: {}", newFolder);
		return newFolder;
	}

	@Override
	public Folder getProperties(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getProperties({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder fld = fm.getProperties(token, fldId);
		log.debug("getProperties: {}", fld);
		return fld;
	}

	@Override
	public void delete(String token, String fldId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("delete({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		fm.delete(token, fldId);
		log.debug("delete: void");
	}

	@Override
	public void purge(String token, String fldId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("purge({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		fm.purge(token, fldId);
		log.debug("purge: void");
	}

	@Override
	public Folder rename(String token, String fldId, String newName) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[]{token, fldId, newName});
		FolderModule fm = ModuleManager.getFolderModule();
		Folder renamedFolder = fm.rename(token, fldId, newName);
		log.debug("rename: {}", renamedFolder);
		return renamedFolder;
	}

	@Override
	public void move(String token, String fldId, String dstId) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[]{token, fldId, dstId});
		FolderModule fm = ModuleManager.getFolderModule();
		fm.move(token, fldId, dstId);
		log.debug("move: void");
	}

	@Override
	public void copy(String token, String fldId, String dstId) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, IOException, AutomationException, DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[]{token, fldId, dstId});
		FolderModule fm = ModuleManager.getFolderModule();
		fm.copy(token, fldId, dstId);
		log.debug("copy: void");
	}

	@Override
	public void extendedCopy(String token, String fldId, String dstId, ExtendedAttributes extAttr) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, AutomationException, DatabaseException,
			UserQuotaExceededException {
		log.debug("extendedCopy({}, {}, {}, {})", new Object[]{token, fldId, dstId, extAttr});
		FolderModule fm = ModuleManager.getFolderModule();
		fm.extendedCopy(token, fldId, dstId, extAttr);
		log.debug("extendedCopy: void");
	}

	@Override
	@Deprecated
	public List<Folder> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getChilds({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		List<Folder> col = fm.getChilds(token, fldId);
		log.debug("getChilds: {}", col);
		return col;
	}

	@Override
	public List<Folder> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getChildren({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		List<Folder> col = fm.getChildren(token, fldId);
		log.debug("getChildren: {}", col);
		return col;
	}

	@Override
	public ContentInfo getContentInfo(String token, String fldId) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException {
		log.debug("getContentInfo({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		ContentInfo contentInfo = fm.getContentInfo(token, fldId);
		log.debug("getContentInfo: {}", contentInfo);
		return contentInfo;
	}

	@Override
	public boolean isValid(String token, String fldId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("isValid({}, {})", token, fldId);
		FolderModule fm = ModuleManager.getFolderModule();
		boolean valid = fm.isValid(token, fldId);
		log.debug("isValid: {}", valid);
		return valid;
	}

	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getPath({})", uuid);
		FolderModule fm = ModuleManager.getFolderModule();
		String path = fm.getPath(token, uuid);
		log.debug("getPath: {}", path);
		return path;
	}

	/**
	 * Create missing folders.
	 */
	public void createMissingFolders(String token, String fldPath) throws RepositoryException, DatabaseException, PathNotFoundException,
			ItemExistsException, AccessDeniedException, ExtensionException, AutomationException {
		String checkPath = "";

		for (String elto : fldPath.substring(1).split("/")) {
			checkPath = checkPath.concat("/").concat(elto);

			if (!OKMRepository.getInstance().hasNode(token, checkPath)) {
				createSimple(token, checkPath);
			}
		}
	}
}
