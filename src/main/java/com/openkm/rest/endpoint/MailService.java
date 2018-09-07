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

package com.openkm.rest.endpoint;

import com.openkm.api.OKMMail;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Mail;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.MailModule;
import com.openkm.module.ModuleManager;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.MailList;
import com.openkm.util.PathUtils;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description="mail-service", value="mail-service")
@Path("/mail")
public class MailService {
	private static Logger log = LoggerFactory.getLogger(MailService.class);

	@POST
	@Path("/create")
	// The "mail" parameter comes in the POST request body (encoded as XML or JSON).
	public Mail create(Mail mail) throws GenericException {
		try {
			log.debug("create({})", mail);
			MailModule mm = ModuleManager.getMailModule();
			Mail newMail = mm.create(null, mail);
			log.debug("create: {}", newMail);
			return newMail;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getProperties")
	public Mail getProperties(@QueryParam("mailId") String mailId) throws GenericException {
		try {
			log.debug("getProperties({})", mailId);
			MailModule mm = ModuleManager.getMailModule();
			Mail mail = mm.getProperties(null, mailId);
			log.debug("getProperties: {}", mail);
			return mail;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/delete")
	public void delete(@QueryParam("mailId") String mailId) throws GenericException {
		try {
			log.debug("delete({})", mailId);
			MailModule mm = ModuleManager.getMailModule();
			mm.delete(null, mailId);
			log.debug("delete: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/rename")
	public Mail rename(@QueryParam("mailId") String mailId, @QueryParam("newName") String newName) throws GenericException {
		try {
			log.debug("rename({}, {})", new Object[]{mailId, newName});
			MailModule mm = ModuleManager.getMailModule();
			Mail renamedMail = mm.rename(null, mailId, newName);
			log.debug("rename: {}", renamedMail);
			return renamedMail;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/move")
	public void move(@QueryParam("mailId") String mailId, @QueryParam("dstId") String dstId) throws GenericException {
		try {
			log.debug("move({}, {}, {})", new Object[]{mailId, dstId});
			MailModule mm = ModuleManager.getMailModule();
			mm.move(null, mailId, dstId);
			log.debug("move: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getChildren")
	public MailList getChildren(@QueryParam("mailId") String mailId) throws GenericException {
		try {
			log.debug("getChildren({})", mailId);
			MailModule mm = ModuleManager.getMailModule();
			MailList ml = new MailList();
			ml.getList().addAll(mm.getChildren(null, mailId));
			log.debug("getChildren: {}", ml);
			return ml;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/isValid")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Boolean isValid(@QueryParam("mailId") String mailId) throws GenericException {
		try {
			log.debug("isValid({})", mailId);
			MailModule mm = ModuleManager.getMailModule();
			boolean valid = mm.isValid(null, mailId);
			log.debug("isValid: {}", valid);
			return new Boolean(valid);
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getPath/{uuid}")
	public String getPath(@PathParam("uuid") String uuid) throws GenericException {
		try {
			log.debug("getPath({})", uuid);
			MailModule mm = ModuleManager.getMailModule();
			String path = mm.getPath(null, uuid);
			log.debug("getPath: {}", path);
			return path;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/importEml")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "dstId" and "content" parameters comes in the POST request body.
	public Mail importEml(List<Attachment> atts) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			DatabaseException, IOException, AutomationException, UserQuotaExceededException, FileSizeExceededException,
			ExtensionException, UnsupportedMimeTypeException, ItemExistsException, VirusDetectedException, MessagingException {
		log.debug("importEml({})", atts);
		InputStream is = null;
		String dstPath = null;

		try {
			for (Attachment att : atts) {
				if ("dstId".equals(att.getContentDisposition().getParameter("name"))) {
					String dstId = att.getObject(String.class);

					if (PathUtils.isPath(dstId)) {
						dstPath = dstId;
					} else {
						dstPath = ModuleManager.getRepositoryModule().getNodePath(null, dstId);
					}
				} else if ("content".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
				}
			}

			Mail newMail = OKMMail.getInstance().importEml(dstPath, is);
			log.debug("importEml: {}", newMail);
			return newMail;
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@POST
	@Path("/importMsg")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "dstId" and "content" parameters comes in the POST request body.
	public Mail importMsg(List<Attachment> atts) throws RepositoryException, AccessDeniedException, PathNotFoundException,
			DatabaseException, IOException, AutomationException, UserQuotaExceededException, FileSizeExceededException,
			ExtensionException, UnsupportedMimeTypeException, ItemExistsException, VirusDetectedException, MessagingException {
		log.debug("importMsg({})", atts);
		InputStream is = null;
		String dstPath = null;

		try {
			for (Attachment att : atts) {
				if ("dstId".equals(att.getContentDisposition().getParameter("name"))) {
					String dstId = att.getObject(String.class);

					if (PathUtils.isPath(dstId)) {
						dstPath = dstId;
					} else {
						dstPath = ModuleManager.getRepositoryModule().getNodePath(null, dstId);
					}
				} else if ("content".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
				}
			}

			Mail newMail = OKMMail.getInstance().importMsg(dstPath, is);
			log.debug("importMsg: {}", newMail);
			return newMail;
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
