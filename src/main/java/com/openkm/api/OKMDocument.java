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

import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.LockInfo;
import com.openkm.bean.Version;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.DocumentModule;
import com.openkm.module.ModuleManager;
import com.openkm.principal.PrincipalAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author pavila
 */
public class OKMDocument implements DocumentModule {
	private static Logger log = LoggerFactory.getLogger(OKMDocument.class);
	private static OKMDocument instance = new OKMDocument();

	private OKMDocument() {
	}

	public static OKMDocument getInstance() {
		return instance;
	}

	@Override
	public Document create(String token, Document doc, InputStream is) throws UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, ItemExistsException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("create({}, {}, {})", new Object[]{token, doc, is});
		DocumentModule dm = ModuleManager.getDocumentModule();
		Document newDocument = dm.create(token, doc, is);
		log.debug("create: {}", newDocument);
		return newDocument;
	}

	public Document createSimple(String token, String docPath, InputStream is) throws UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, ItemExistsException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("createSimple({}, {}, {})", new Object[]{token, docPath, is});
		DocumentModule dm = ModuleManager.getDocumentModule();
		Document doc = new Document();
		doc.setPath(docPath);
		Document newDocument = dm.create(token, doc, is);
		log.debug("createSimple: {}", newDocument);
		return newDocument;
	}

	@Override
	public void delete(String token, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("delete({})", docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.delete(token, docId);
		log.debug("delete: void");
	}

	@Override
	public Document getProperties(String token, String docId) throws RepositoryException, AccessDeniedException,
			PathNotFoundException, DatabaseException {
		log.debug("getProperties({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		Document doc = dm.getProperties(token, docId);
		log.debug("getProperties: {}", doc);
		return doc;
	}

	@Override
	public InputStream getContent(String token, String docId, boolean checkout) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.debug("getContent({}, {}, {})", new Object[]{token, docId, checkout});
		DocumentModule dm = ModuleManager.getDocumentModule();
		InputStream is = dm.getContent(token, docId, checkout);
		log.debug("getContent: {}", is);
		return is;
	}

	@Override
	public InputStream getContentByVersion(String token, String docId, String versionId) throws RepositoryException,
			AccessDeniedException, PathNotFoundException, IOException, DatabaseException {
		log.debug("getContentByVersion({}, {}, {})", new Object[]{token, docId, versionId});
		DocumentModule dm = ModuleManager.getDocumentModule();
		InputStream is = dm.getContentByVersion(token, docId, versionId);
		log.debug("getContentByVersion: {}", is);
		return is;
	}

	@Override
	@Deprecated
	public List<Document> getChilds(String token, String fldId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getChilds({}, {})", token, fldId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		List<Document> col = dm.getChilds(token, fldId);
		log.debug("getChilds: {}", col);
		return col;
	}

	@Override
	public List<Document> getChildren(String token, String fldId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getChildren({}, {})", token, fldId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		List<Document> col = dm.getChildren(token, fldId);
		log.debug("getChildren: {}", col);
		return col;
	}

	@Override
	public Document rename(String token, String docId, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, LockException, RepositoryException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("rename({}, {}, {})", new Object[]{token, docId, newName});
		DocumentModule dm = ModuleManager.getDocumentModule();
		Document renamedDocument = dm.rename(token, docId, newName);
		log.debug("rename: {}", renamedDocument);
		return renamedDocument;
	}

	@Override
	public void setProperties(String token, Document doc) throws LockException, VersionException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setProperties({}, {})", token, doc);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.setProperties(token, doc);
		log.debug("setProperties: void");
	}

	@Override
	public void checkout(String token, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("checkout({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.checkout(token, docId);
		log.debug("checkout: void");
	}

	@Override
	public void cancelCheckout(String token, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("cancelCheckout({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.cancelCheckout(token, docId);
		log.debug("cancelCheckout: void");
	}

	@Override
	public void forceCancelCheckout(String token, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, PrincipalAdapterException {
		log.debug("forceCancelCheckout({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.forceCancelCheckout(token, docId);
		log.debug("forceCancelCheckout: void");
	}

	@Override
	public boolean isCheckedOut(String token, String docId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("isCheckedOut({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		boolean checkedOut = dm.isCheckedOut(token, docId);
		log.debug("isCheckedOut: {}", checkedOut);
		return checkedOut;
	}

	@Override
	public Version checkin(String token, String docId, InputStream is, String comment)
			throws FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, LockException,
			VersionException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException, AutomationException {
		log.debug("checkin({}, {}, {})", new Object[]{token, docId, comment});
		DocumentModule dm = ModuleManager.getDocumentModule();
		Version version = dm.checkin(token, docId, is, comment);
		log.debug("checkin: {}", version);
		return version;
	}

	@Override
	public Version checkin(String token, String docId, InputStream is, String comment, int increment)
			throws FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, LockException,
			VersionException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException, AutomationException {
		log.debug("checkin({}, {}, {}, {})", new Object[]{token, docId, comment, increment});
		DocumentModule dm = ModuleManager.getDocumentModule();
		Version version = dm.checkin(token, docId, is, comment, increment);
		log.debug("checkin: {}", version);
		return version;
	}

	@Override
	public List<Version> getVersionHistory(String token, String docId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getVersionHistory({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		List<Version> history = dm.getVersionHistory(token, docId);
		log.debug("getVersionHistory: {}", history);
		return history;
	}

	@Override
	public LockInfo lock(String token, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("lock({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		LockInfo lock = dm.lock(token, docId);
		log.debug("lock: {}", lock);
		return lock;
	}

	@Override
	public void unlock(String token, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("unlock({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.unlock(token, docId);
		log.debug("unlock: void");
	}

	@Override
	public void forceUnlock(String token, String docId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, PrincipalAdapterException {
		log.debug("forceUnlock({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.forceUnlock(token, docId);
		log.debug("forceUnlock: void");
	}

	@Override
	public boolean isLocked(String token, String docId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("isLocked({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		boolean locked = dm.isLocked(token, docId);
		log.debug("isLocked: {}", locked);
		return locked;
	}

	@Override
	public LockInfo getLockInfo(String token, String docId) throws LockException, AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getLock({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		LockInfo lock = dm.getLockInfo(token, docId);
		log.debug("getLock: {}", lock);
		return lock;
	}

	@Override
	public void purge(String token, String docId) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException {
		log.debug("purge({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.purge(token, docId);
		log.debug("purge: void");
	}

	@Override
	public void move(String token, String docId, String dstId) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, LockException, RepositoryException, DatabaseException, ExtensionException,
			AutomationException {
		log.debug("move({}, {}, {})", new Object[]{token, docId, dstId});
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.move(token, docId, dstId);
		log.debug("move: void");
	}

	@Override
	public void copy(String token, String docId, String dstId) throws ItemExistsException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, UserQuotaExceededException,
			ExtensionException, AutomationException {
		log.debug("copy({}, {}, {})", new Object[]{token, docId, dstId});
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.copy(token, docId, dstId);
		log.debug("copy: void");
	}

	@Override
	public void extendedCopy(String token, String docId, String dstId, String docName, ExtendedAttributes extAttr)
			throws ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, UserQuotaExceededException, ExtensionException, AutomationException {
		log.debug("extendedCopy({}, {}, {}, {})", new Object[]{token, docId, dstId, docName, extAttr});
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.extendedCopy(token, docId, dstId, docName, extAttr);
		log.debug("extendedCopy: void");
	}

	@Override
	public void restoreVersion(String token, String docId, String versionId) throws PathNotFoundException,
			AccessDeniedException, LockException, RepositoryException, DatabaseException, ExtensionException {
		log.debug("restoreVersion({}, {}, {})", new Object[]{token, docId, versionId});
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.restoreVersion(token, docId, versionId);
		log.debug("restoreVersion: void");
	}

	@Override
	public void purgeVersionHistory(String token, String docId) throws PathNotFoundException, AccessDeniedException,
			LockException, RepositoryException, DatabaseException {
		log.debug("purgeVersionHistory({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.purgeVersionHistory(token, docId);
		log.debug("purgeVersionHistory: void");
	}

	@Override
	public long getVersionHistorySize(String token, String docId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getVersionHistorySize({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		long size = dm.getVersionHistorySize(token, docId);
		log.debug("getVersionHistorySize: {}", size);
		return size;
	}

	@Override
	public boolean isValid(String token, String docId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("isValid({}, {})", token, docId);
		DocumentModule dm = ModuleManager.getDocumentModule();
		boolean valid = dm.isValid(token, docId);
		log.debug("isValid: {}", valid);
		return valid;
	}

	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getPath({}, {})", token, uuid);
		DocumentModule dm = ModuleManager.getDocumentModule();
		String path = dm.getPath(token, uuid);
		log.debug("getPath: {}", path);
		return path;
	}
}
