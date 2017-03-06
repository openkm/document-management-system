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
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.core.PathNotFoundException;
import com.openkm.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RootResource implements PropFindableResource, GetableResource, CollectionResource, QuotaResource {
	private final Logger log = LoggerFactory.getLogger(RootResource.class);
	private final List<Folder> fldChilds = new ArrayList<Folder>();
	private Folder fld;
	private final Path path;

	public RootResource(Path path) {
		this.path = path;
		this.fld = new Folder();
		this.fld.setPath("/");
		this.fld.setUuid(Repository.getUuid());

		try {
			Folder okmRoot = OKMRepository.getInstance().getRootFolder(null);
			fldChilds.add(ResourceUtils.fixResourcePath(okmRoot));
			this.fld.setCreated(okmRoot.getCreated());

			Folder okmCategories = OKMRepository.getInstance().getCategoriesFolder(null);
			fldChilds.add(ResourceUtils.fixResourcePath(okmCategories));

			Folder okmPersonal = OKMRepository.getInstance().getPersonalFolderBase(null);
			fldChilds.add(ResourceUtils.fixResourcePath(okmPersonal));

			Folder okmTemplates = OKMRepository.getInstance().getTemplatesFolder(null);
			fldChilds.add(ResourceUtils.fixResourcePath(okmTemplates));

			Folder okmMail = OKMRepository.getInstance().getMailFolderBase(null);
			fldChilds.add(ResourceUtils.fixResourcePath(okmMail));
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException: " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
		}
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
			return ResourceUtils.getNode(path, Path.path(fld.getPath()).getStripFirst() + "/" + childName);
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
		List<Resource> resources = new ArrayList<Resource>();

		if (fldChilds != null) {
			for (Folder fld : fldChilds) {
				resources.add(new FolderResource(fld));
			}
		}

		return resources;
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType)
			throws IOException, NotAuthorizedException, BadRequestException {
		log.debug("sendContent({}, {})", params, contentType);
		ResourceUtils.createContent(out, path, fldChilds, null, null);
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
