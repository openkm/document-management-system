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

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.*;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.core.Config;
import com.openkm.core.PathNotFoundException;
import com.openkm.module.db.DbDocumentModule;
import com.openkm.util.ConfigUtils;
import com.openkm.util.PathUtils;
import com.openkm.util.SystemProfiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;

public class FolderResource implements MakeCollectionableResource, PutableResource, CopyableResource,
		DeletableResource, MoveableResource, PropFindableResource, GetableResource, QuotaResource {
	private final Logger log = LoggerFactory.getLogger(FolderResource.class);
	private final List<Document> docChilds;
	private final List<Folder> fldChilds;
	private final List<Mail> mailChilds;
	private Folder fld;
	private final Path path;

	public FolderResource(Folder fld) {
		this.fldChilds = null;
		this.docChilds = null;
		this.mailChilds = null;
		this.path = null;
		this.fld = ResourceUtils.fixResourcePath(fld);
	}

	public FolderResource(Path path, Folder fld, List<Folder> fldChilds, List<Document> docChilds, List<Mail> mailChilds) {
		this.fldChilds = fldChilds;
		this.docChilds = docChilds;
		this.mailChilds = mailChilds;
		this.path = path;
		this.fld = ResourceUtils.fixResourcePath(fld);
	}

	public Folder getFolder() {
		return fld;
	}

	@Override
	public String getUniqueId() {
		return fld.getUuid();
	}

	@Override
	public String getName() {
		return PathUtils.getName(fld.getPath());
	}

	@Override
	public Object authenticate(String user, String password) {
		// log.debug("authenticate({}, {})", new Object[] { user, password });
		return ResourceFactoryImpl.REALM;
	}

	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		// log.debug("authorise({}, {}, {})", new Object[] {
		// request.getAbsolutePath(), method, auth });
		return true;
	}

	@Override
	public String getRealm() {
		return ResourceFactoryImpl.REALM;
	}

	@Override
	public Date getCreateDate() {
		return fld.getCreated().getTime();
	}

	@Override
	public Date getModifiedDate() {
		return fld.getCreated().getTime();
	}

	@Override
	public String checkRedirect(Request request) {
		return null;
	}

	@Override
	public Resource child(String childName) {
		log.debug("child({})", childName);

		try {
			return ResourceUtils.getNode(path, fld.getPath() + "/" + childName);
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException: " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
		}

		return null;
	}

	@Override
	public List<? extends Resource> getChildren() {
		log.debug("getChildren()");
		long begin = System.currentTimeMillis();
		List<Resource> resources = new ArrayList<Resource>();

		if (fldChilds != null) {
			for (Folder fld : fldChilds) {
				resources.add(new FolderResource(fld));
			}
		}

		if (docChilds != null) {
			for (Document doc : docChilds) {
				resources.add(new DocumentResource(doc));
			}
		}

		if (mailChilds != null) {
			for (Mail mail : mailChilds) {
				resources.add(new MailResource(mail));
			}
		}

		SystemProfiling.log(null, System.currentTimeMillis() - begin);
		log.trace("getChildren.Time: {}", System.currentTimeMillis() - begin);
		return resources;
	}

	@Override
	public Resource createNew(String newName, InputStream is, Long length, String contentType)
			throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
		log.debug("createNew({}, {}, {}, {})", new Object[]{newName, is, length, contentType});
		Document newDoc = new Document();
		String fixedDocPath = ResourceUtils.fixRepositoryPath(fld.getPath());
		newDoc.setPath(fixedDocPath + "/" + newName);

		try {
			if (OKMRepository.getInstance().hasNode(null, newDoc.getPath())) {
				// Already exists, so create new version
				if (Config.REPOSITORY_NATIVE) {
					new DbDocumentModule().checkout(null, newDoc.getPath());
					new DbDocumentModule().checkin(null, newDoc.getPath(), is, length, "Modified from WebDAV", null);
				} else {
					// Other implementation
				}
			} else {
				// Restrict for extension
				if (!Config.RESTRICT_FILE_NAME.isEmpty()) {
					StringTokenizer st = new StringTokenizer(Config.RESTRICT_FILE_NAME, Config.LIST_SEPARATOR);

					while (st.hasMoreTokens()) {
						String wc = st.nextToken().trim();
						String re = ConfigUtils.wildcard2regexp(wc);

						if (Pattern.matches(re, newName)) {
							log.warn("Filename BAD -> {} ({})", re, wc);
							return null;
						}
					}
				}

				// Create a new one
				if (Config.REPOSITORY_NATIVE) {
					newDoc = new DbDocumentModule().create(null, newDoc, is, length, null);
				} else {
					// Other implementation
				}
			}

			return new DocumentResource(newDoc);
		} catch (PathNotFoundException e) {
			log.warn("PathNotFoundException: " + e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException("Failed to create: " + e.getMessage(), e);
		}

		return null;
	}

	@Override
	public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException,
			BadRequestException {
		log.debug("createCollection({})", newName);
		Folder newFld = new Folder();
		String fixedFldPath = ResourceUtils.fixRepositoryPath(fld.getPath());
		newFld.setPath(fixedFldPath + "/" + newName);

		try {
			newFld = OKMFolder.getInstance().create(null, newFld);
			return new FolderResource(newFld);
		} catch (Exception e) {
			throw new ConflictException(this);
		}
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType)
			throws IOException, NotAuthorizedException, BadRequestException {
		log.debug("sendContent({}, {})", params, contentType);
		ResourceUtils.createContent(out, path, fldChilds, docChilds, mailChilds);
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		return null;
	}

	@Override
	public Long getContentLength() {
		return null;
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		log.debug("delete()");

		try {
			String fixedFldPath = ResourceUtils.fixRepositoryPath(fld.getPath());
			OKMFolder.getInstance().delete(null, fixedFldPath);
		} catch (Exception e) {
			throw new ConflictException(this);
		}
	}

	@Override
	public void moveTo(CollectionResource newParent, String newName) throws ConflictException, NotAuthorizedException,
			BadRequestException {
		log.debug("moveTo({}, {})", newParent, newName);

		if (newParent instanceof FolderResource) {
			FolderResource newFldParent = (FolderResource) newParent;
			String dstFolder = newFldParent.getFolder().getPath();
			String srcFolder = PathUtils.getParent(fld.getPath());
			String fixedFldPath = ResourceUtils.fixRepositoryPath(fld.getPath());

			if (dstFolder.equals(srcFolder)) {
				log.debug("moveTo - RENAME {} to {}", fixedFldPath, newName);

				try {
					fld = OKMFolder.getInstance().rename(null, fixedFldPath, newName);
				} catch (Exception e) {
					throw new RuntimeException("Failed to rename to: " + newName);
				}
			} else {
				String dstPath = newFldParent.getFolder().getPath();
				String fixedDstPath = ResourceUtils.fixRepositoryPath(dstPath);
				log.debug("moveTo - MOVE from {} to {}", fixedFldPath, fixedDstPath);

				try {
					OKMFolder.getInstance().move(null, fixedFldPath, fixedDstPath);
					fld.setPath(dstPath);
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
				String fixedFldPath = ResourceUtils.fixRepositoryPath(fld.getPath());
				OKMFolder.getInstance().copy(null, fixedFldPath, dstPath);
			} catch (Exception e) {
				throw new RuntimeException("Failed to copy to:" + dstPath, e);
			}
		} else {
			throw new RuntimeException("Destination is an unknown type. Must be a FolderResource, is a: "
					+ newParent.getClass());
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
		sb.append("fld=").append(fld);
		sb.append("}");
		return sb.toString();
	}
}
