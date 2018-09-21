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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.LockInfo;
import com.openkm.bean.Version;
import com.openkm.core.MimeTypeConfig;
import com.openkm.module.DocumentModule;
import com.openkm.module.ModuleManager;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.DocumentList;
import com.openkm.rest.util.VersionList;

import io.swagger.annotations.Api;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description="document-service", value="document-service")
@Path("/document")
public class DocumentService {
	private static Logger log = LoggerFactory.getLogger(DocumentService.class);

	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "doc" and "content" parameters comes in the POST request body (encoded as XML or JSON).
	public Document create(List<Attachment> atts) throws GenericException {
		try {
			log.debug("create({})", atts);
			Document doc = null;
			InputStream is = null;

			for (Attachment att : atts) {
				if ("doc".equals(att.getContentDisposition().getParameter("name"))) {
					doc = att.getObject(Document.class);
				} else if ("content".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
				}
			}

			DocumentModule dm = ModuleManager.getDocumentModule();
			Document newDocument = dm.create(null, doc, is);
			IOUtils.closeQuietly(is);
			log.debug("create: {}", newDocument);
			return newDocument;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/createSimple")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "docPath" and "content" parameters comes in the POST request body.
	public Document createSimple(List<Attachment> atts) throws GenericException {
		try {
			log.debug("createSimple({})", atts);
			String docPath = null;
			InputStream is = null;

			for (Attachment att : atts) {
				if ("docPath".equals(att.getContentDisposition().getParameter("name"))) {
					docPath = att.getObject(String.class);
				} else if ("content".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
				}
			}

			DocumentModule dm = ModuleManager.getDocumentModule();
			Document doc = new Document();
			doc.setPath(docPath);
			Document newDocument = dm.create(null, doc, is);
			IOUtils.closeQuietly(is);
			log.debug("createSimple: {}", newDocument);
			return newDocument;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/delete")
	public void delete(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("delete({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.delete(null, docId);
			log.debug("delete: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getProperties")
	public Document getProperties(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("getProperties({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			Document doc = dm.getProperties(null, docId);
			log.debug("getProperties: {}", doc);
			return doc;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getContent")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getContent(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("getContent({})", new Object[]{docId});
			DocumentModule dm = ModuleManager.getDocumentModule();
			final InputStream is = dm.getContent(null, docId, false);
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream os) throws IOException, WebApplicationException {
					IOUtils.copy(is, os);
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(os);
				}
			};

			log.debug("getContent: [BINARY]");
			return Response.ok(stream).build();
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getContentByVersion")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getContentByVersion(@QueryParam("docId") String docId, @QueryParam("versionId") String versionId) throws GenericException {
		try {
			log.debug("getContentByVersion({}, {})", new Object[]{docId, versionId});
			DocumentModule dm = ModuleManager.getDocumentModule();
			final InputStream is = dm.getContentByVersion(null, docId, versionId);
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream os) throws IOException, WebApplicationException {
					IOUtils.copy(is, os);
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(os);
				}
			};

			log.debug("getContentByVersion: [BINARY]");
			return Response.ok(stream).build();
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getChildren")
	public DocumentList getChildren(@QueryParam("fldId") String dstId) throws GenericException {
		try {
			log.debug("getChildren({})", dstId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			DocumentList docList = new DocumentList();
			docList.getList().addAll(dm.getChildren(null, dstId));
			return docList;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/rename")
	public Document rename(@QueryParam("docId") String docId, @QueryParam("newName") String newName) throws GenericException {
		try {
			log.debug("rename({}, {})", new Object[]{docId, newName});
			DocumentModule dm = ModuleManager.getDocumentModule();
			Document renamedDocument = dm.rename(null, docId, newName);
			log.debug("rename: {}", renamedDocument);
			return renamedDocument;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/setProperties")
	public void setProperties(Document doc) throws GenericException {
		try {
			log.debug("setProperties({})", doc);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.setProperties(null, doc);
			log.debug("setProperties: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/checkout")
	public void checkout(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("checkout({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.checkout(null, docId);
			log.debug("checkout: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/cancelCheckout")
	public void cancelCheckout(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("cancelCheckout({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.cancelCheckout(null, docId);
			log.debug("cancelCheckout: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/forceCancelCheckout")
	public void forceCancelCheckout(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("forceCancelCheckout({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.forceCancelCheckout(null, docId);
			log.debug("forceCancelCheckout: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/isCheckedOut")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Boolean isCheckedOut(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("cancelCheckout({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			boolean checkout = dm.isCheckedOut(null, docId);
			log.debug("cancelCheckout: void");
			return new Boolean(checkout);
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/checkin")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "docId" and "content" parameters comes in the POST request body (encoded as XML or JSON).
	public Version checkin(List<Attachment> atts) throws GenericException {
		try {
			log.debug("checkin({})", atts);
			String docId = null;
			String comment = null;
			InputStream is = null;

			for (Attachment att : atts) {
				if ("docId".equals(att.getContentDisposition().getParameter("name"))) {
					docId = att.getObject(String.class);
				} else if ("comment".equals(att.getContentDisposition().getParameter("name"))) {
					comment = att.getObject(String.class);
				} else if ("content".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
				}
			}

			DocumentModule dm = ModuleManager.getDocumentModule();
			Version version = dm.checkin(null, docId, is, comment);
			IOUtils.closeQuietly(is);
			log.debug("checkin: {}", version);
			return version;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getVersionHistory")
	public VersionList getVersionHistory(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("getVersionHistory({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			VersionList versionList = new VersionList();
			List<Version> versions = dm.getVersionHistory(null, docId);
			versionList.getList().addAll(versions);
			log.debug("getVersionHistory: {}", versions);
			return versionList;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/lock")
	public LockInfo lock(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("lock({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			LockInfo lockinfo = dm.lock(null, docId);
			log.debug("lock: {}", lockinfo);
			return lockinfo;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/unlock")
	public void unlock(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("unlock({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.unlock(null, docId);
			log.debug("unlock: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/forceUnlock")
	public void forceUnlock(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("forceUnlock({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.forceUnlock(null, docId);
			log.debug("forceUnlock: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/isLocked")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Boolean isLocked(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("isLocked({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			boolean lock = dm.isLocked(null, docId);
			log.debug("isLocked: void");
			return new Boolean(lock);
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getLockInfo")
	public LockInfo getLockInfo(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("unlock({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			LockInfo lockInfo = dm.getLockInfo(null, docId);
			log.debug("unlock: void");
			return lockInfo;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/purge")
	public void purge(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("purge({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.purge(null, docId);
			log.debug("purge: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/move")
	public void move(@QueryParam("docId") String docId, @QueryParam("dstId") String dstId) throws GenericException {
		try {
			log.debug("move({}, {})", new Object[]{docId, dstId});
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.move(null, docId, dstId);
			log.debug("move: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/copy")
	public void copy(@QueryParam("docId") String docId, @QueryParam("dstId") String dstId) throws GenericException {
		try {
			log.debug("copy({}, {})", new Object[]{docId, dstId});
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.copy(null, docId, dstId);
			log.debug("copy: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/restoreVersion")
	public void restoreVersion(@QueryParam("docId") String docId, @QueryParam("versionId") String versionId) throws GenericException {
		try {
			log.debug("restoreVersion({}, {})", new Object[]{docId, versionId});
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.restoreVersion(null, docId, versionId);
			log.debug("restoreVersion: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/purgeVersionHistory")
	public void purgeVersionHistory(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("purgeVersionHistory({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			dm.purgeVersionHistory(null, docId);
			log.debug("purgeVersionHistory: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getVersionHistorySize")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Long getVersionHistorySize(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("getVersionHistorySize({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			long size = dm.getVersionHistorySize(null, docId);
			log.debug("getVersionHistorySize: {}", size);
			return new Long(size);
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/isValid")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Boolean isValid(@QueryParam("docId") String docId) throws GenericException {
		try {
			log.debug("isValid({})", docId);
			DocumentModule dm = ModuleManager.getDocumentModule();
			boolean valid = dm.isValid(null, docId);
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
			DocumentModule dm = ModuleManager.getDocumentModule();
			String path = dm.getPath(null, uuid);
			log.debug("getPath: {}", path);
			return path;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
