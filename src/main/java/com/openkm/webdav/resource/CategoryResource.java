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
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
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

public class CategoryResource implements CollectionResource, PropFindableResource, GetableResource, QuotaResource {
	private final Logger log = LoggerFactory.getLogger(CategoryResource.class);
	private final List<Document> docChilds;
	private final List<Folder> fldChilds;
	private final List<Mail> mailChilds;
	private Folder cat;
	private final Path path;

	public CategoryResource(Folder cat) {
		this.fldChilds = null;
		this.docChilds = null;
		this.mailChilds = null;
		this.path = null;
		this.cat = ResourceUtils.fixResourcePath(cat);
	}

	public CategoryResource(Path path, Folder cat, List<Folder> fldChilds, List<Document> docChilds,
	                        List<Mail> mailChilds) {
		this.fldChilds = fldChilds;
		this.docChilds = docChilds;
		this.mailChilds = mailChilds;
		this.path = path;
		this.cat = ResourceUtils.fixResourcePath(cat);
	}

	public Folder getFolder() {
		return cat;
	}

	@Override
	public String getUniqueId() {
		return cat.getUuid();
	}

	@Override
	public String getName() {
		return PathUtils.getName(cat.getPath());
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
		return cat.getCreated().getTime();
	}

	@Override
	public Date getModifiedDate() {
		return cat.getCreated().getTime();
	}

	@Override
	public String checkRedirect(Request request) {
		return null;
	}

	@Override
	public Resource child(String childName) {
		log.debug("child({})", childName);

		try {
			return ResourceUtils.getNode(path, cat.getPath() + "/" + childName);
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException: " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
		}

		return null;
	}

	@Override
	public List<? extends Resource> getChildren() {
		log.info("getChildren()");
		List<Resource> resources = new ArrayList<Resource>();

		if (fldChilds != null) {
			for (Folder fld : fldChilds) {
				resources.add(new CategoryResource(fld));
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

		return resources;
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
		sb.append("cat=").append(cat);
		sb.append("}");
		return sb.toString();
	}
}
