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

package com.openkm.webdav.resource;

import com.bradmcevoy.http.*;
import com.bradmcevoy.http.LockInfo.LockDepth;
import com.bradmcevoy.http.LockInfo.LockScope;
import com.bradmcevoy.http.LockInfo.LockType;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.*;
import com.bradmcevoy.http.webdav.PropPatchHandler.Fields;
import com.openkm.api.OKMDocument;
import com.openkm.bean.Document;
import com.openkm.core.*;
import com.openkm.util.PathUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

public class DocumentResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource,
		PropFindableResource, PropPatchableResource, LockableResource, QuotaResource {
	private static final Logger log = LoggerFactory.getLogger(DocumentResource.class);
	private Document doc;
	private LockToken lt;

	public DocumentResource(Document doc) {
		this.doc = ResourceUtils.fixResourcePath(doc);
	}

	@Override
	public String getUniqueId() {
		return doc.getUuid();
	}

	@Override
	public String getName() {
		return PathUtils.getName(doc.getPath());
	}

	@Override
	public Object authenticate(String user, String password) {
		// log.debug("authenticate({}, {})", new Object[] { user, password });
		return "OpenKM";
	}

	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		// log.debug("authorise({}, {}, {})", new Object[] { request.getAbsolutePath(), method.toString(),
		// auth.getUser() });
		return true;
	}

	@Override
	public String getRealm() {
		return ResourceFactoryImpl.REALM;
	}

	@Override
	public Date getCreateDate() {
		return doc.getCreated().getTime();
	}

	@Override
	public Date getModifiedDate() {
		return doc.getLastModified().getTime();
	}

	@Override
	public String checkRedirect(Request request) {
		return null;
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		return doc.getMimeType();
	}

	@Override
	public Long getContentLength() {
		return doc.getActualVersion().getSize();
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType)
			throws IOException, NotAuthorizedException, BadRequestException {
		log.debug("sendContent({}, {})", params, contentType);
		InputStream is = null;

		try {
			String fixedDocPath = ResourceUtils.fixRepositoryPath(doc.getPath());
			is = OKMDocument.getInstance().getContent(null, fixedDocPath, false);
			IOUtils.copy(is, out);
			out.flush();
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException: " + e.getMessage(), e);
			throw new RuntimeException("Failed to update content: " + doc.getPath());
		} catch (AccessDeniedException e) {
			log.error("AccessDeniedException: " + e.getMessage(), e);
			throw new RuntimeException("Failed to update content: " + doc.getPath());
		} catch (RepositoryException e) {
			log.error("RepositoryException: " + e.getMessage(), e);
			throw new RuntimeException("Failed to update content: " + doc.getPath());
		} catch (DatabaseException e) {
			log.error("DatabaseException: " + e.getMessage(), e);
			throw new RuntimeException("Failed to update content: " + doc.getPath());
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		log.debug("delete()");

		try {
			String fixedDocPath = ResourceUtils.fixRepositoryPath(doc.getPath());
			OKMDocument.getInstance().delete(null, fixedDocPath);
		} catch (Exception e) {
			throw new ConflictException(this);
		}
	}

	@Override
	public void setProperties(Fields fields) {
		// MIL-50: not implemented. Just to keep MS Office sweet
	}

	@Override
	public void moveTo(CollectionResource newParent, String newName) throws ConflictException, NotAuthorizedException,
			BadRequestException {
		log.debug("moveTo({}, {})", newParent, newName);

		if (newParent instanceof FolderResource) {
			FolderResource newFldParent = (FolderResource) newParent;
			String dstFolder = newFldParent.getFolder().getPath();
			String srcFolder = PathUtils.getParent(doc.getPath());
			String fixedDocPath = ResourceUtils.fixRepositoryPath(doc.getPath());

			if (dstFolder.equals(srcFolder)) {
				log.debug("moveTo - RENAME {} to {}", fixedDocPath, newName);

				try {
					doc = OKMDocument.getInstance().rename(null, fixedDocPath, newName);
				} catch (Exception e) {
					throw new RuntimeException("Failed to rename to: " + newName);
				}
			} else {
				String dstPath = newFldParent.getFolder().getPath();
				String fixedDstPath = ResourceUtils.fixRepositoryPath(dstPath);
				log.debug("moveTo - MOVE from {} to {}", fixedDocPath, fixedDstPath);

				try {
					OKMDocument.getInstance().move(null, fixedDocPath, fixedDstPath);
					doc.setPath(dstPath);
				} catch (Exception e) {
					throw new RuntimeException("Failed to move to: " + dstPath);
				}
			}
		} else {
			throw new RuntimeException("Destination is an unknown type. Must be a FsDirectoryResource, is a: "
					+ newParent.getClass());
		}
	}

	@Override
	public void copyTo(CollectionResource newParent, String newName) throws NotAuthorizedException,
			BadRequestException, ConflictException {
		log.debug("copyTo({}, {})", newParent, newName);

		if (newParent instanceof FolderResource) {
			FolderResource newFldParent = (FolderResource) newParent;
			String dstPath = newFldParent.getFolder().getPath() + "/" + newName;

			try {
				String fixedDocPath = ResourceUtils.fixRepositoryPath(doc.getPath());
				OKMDocument.getInstance().copy(null, fixedDocPath, dstPath);
			} catch (Exception e) {
				throw new RuntimeException("Failed to copy to:" + dstPath, e);
			}
		} else {
			throw new RuntimeException("Destination is an unknown type. Must be a FolderResource, is a: "
					+ newParent.getClass());
		}
	}

	@Override
	public LockResult lock(LockTimeout timeout, LockInfo lockInfo) throws NotAuthorizedException,
			PreConditionFailedException, LockedException {
		String fixedDocPath = ResourceUtils.fixRepositoryPath(doc.getPath());

		try {
			if (OKMDocument.getInstance().isLocked(null, fixedDocPath)) {
				throw new LockedException(this);
			} else {
				com.openkm.bean.LockInfo lock = OKMDocument.getInstance().lock(null, fixedDocPath);
				lt = new LockToken();
				lt.tokenId = lock.getToken();
				lt.tokenId = lock.getToken();
				lt.info = new LockInfo(LockScope.EXCLUSIVE, LockType.WRITE, lock.getOwner(), LockDepth.INFINITY);
				lt.timeout = new LockTimeout(Long.MAX_VALUE);
				return LockResult.success(lt);
			}
		} catch (LockException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (PathNotFoundException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (AccessDeniedException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (RepositoryException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (DatabaseException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		}
	}

	@Override
	public LockResult refreshLock(String token) throws NotAuthorizedException, PreConditionFailedException {
		return LockResult.success(lt);
	}

	@Override
	public void unlock(String tokenId) throws NotAuthorizedException, PreConditionFailedException {
		String fixedDocPath = ResourceUtils.fixRepositoryPath(doc.getPath());

		try {
			OKMDocument.getInstance().unlock(null, fixedDocPath);
		} catch (LockException e) {
			throw new PreConditionFailedException(this);
		} catch (Exception e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		}
	}

	@Override
	public LockToken getCurrentLock() {
		String fixedDocPath = ResourceUtils.fixRepositoryPath(doc.getPath());

		try {
			if (OKMDocument.getInstance().isLocked(null, fixedDocPath)) {
				com.openkm.bean.LockInfo lock = OKMDocument.getInstance().getLockInfo(null, fixedDocPath);
				lt = new LockToken();
				lt.tokenId = lock.getToken();
				lt.tokenId = lock.getToken();
				lt.info = new LockInfo(LockScope.EXCLUSIVE, LockType.WRITE, lock.getOwner(), LockDepth.INFINITY);
				lt.timeout = new LockTimeout(Long.MAX_VALUE);
				return lt;
			} else {
				return null;
			}
		} catch (LockException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (AccessDeniedException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (PathNotFoundException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (RepositoryException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		} catch (DatabaseException e) {
			throw new RuntimeException("Failed to lock: " + fixedDocPath);
		}
	}

	@Override
	public Long getQuotaUsed() {
		return new Long(0);
	}

	@Override
	public Long getQuotaAvailable() {
		return Long.MAX_VALUE;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("doc=").append(doc);
		sb.append("}");
		return sb.toString();
	}
}
