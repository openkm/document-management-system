/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.api;

import com.openkm.core.*;
import com.openkm.module.ModuleManager;
import com.openkm.module.PropertyModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pavila
 *
 */
public class OKMProperty implements PropertyModule {
	private static Logger log = LoggerFactory.getLogger(OKMDocument.class);
	private static OKMProperty instance = new OKMProperty();

	private OKMProperty() {
	}

	public static OKMProperty getInstance() {
		return instance;
	}

	@Override
	public void addCategory(String token, String nodeId, String catId) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addCategory({}, {}, {})", new Object[]{token, nodeId, catId});
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.addCategory(token, nodeId, catId);
		log.debug("addCategory: void");
	}

	@Override
	public void removeCategory(String token, String nodeId, String catId) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("removeCategory({}, {}, {})", new Object[]{token, nodeId, catId});
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.removeCategory(token, nodeId, catId);
		log.debug("removeCategory: void");
	}

	@Override
	public String addKeyword(String token, String nodeId, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addKeyword({}, {}, {})", new Object[]{token, nodeId, keyword});
		PropertyModule pm = ModuleManager.getPropertyModule();
		String ret = pm.addKeyword(token, nodeId, keyword);
		log.debug("addKeyword: {}", ret);
		return ret;
	}

	@Override
	public void removeKeyword(String token, String nodeId, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("removeKeyword({}, {}, {})", new Object[]{token, nodeId, keyword});
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.removeKeyword(token, nodeId, keyword);
		log.debug("removeKeyword: void");
	}

	@Override
	public void setEncryption(String token, String nodePath, String cipherName) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("setEncryption({}, {}, {})", new Object[]{token, nodePath, cipherName});
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.setEncryption(token, nodePath, cipherName);
		log.debug("setEncryption: void");
	}

	@Override
	public void unsetEncryption(String token, String nodePath) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("unsetEncryption({}, {})", new Object[]{token, nodePath});
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.unsetEncryption(token, nodePath);
		log.debug("unsetEncryption: void");
	}

	@Override
	public void setSigned(String token, String nodePath, boolean signed) throws VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setSigned({}, {}, {})", new Object[]{token, nodePath, signed});
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.setSigned(token, nodePath, signed);
		log.debug("setSigned: void");
	}
}
