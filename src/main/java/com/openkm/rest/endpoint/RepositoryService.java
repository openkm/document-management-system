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

import bsh.Interpreter;
import com.openkm.bean.AppVersion;
import com.openkm.bean.Folder;
import com.openkm.bean.ScriptExecutionResult;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.ConfigDAO;
import com.openkm.dao.LegacyDAO;
import com.openkm.module.ModuleManager;
import com.openkm.module.RepositoryModule;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.Configuration;
import com.openkm.rest.util.HqlQueryResults;
import com.openkm.rest.util.SqlQueryResultColumns;
import com.openkm.rest.util.SqlQueryResults;
import com.openkm.spring.PrincipalUtils;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.hibernate.QueryException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description="repository-service", value="repository-service")
@Path("/repository")
public class RepositoryService {
	private static Logger log = LoggerFactory.getLogger(RepositoryService.class);

	@GET
	@Path("/getRootFolder")
	public Folder getRootFolder() throws GenericException {
		try {
			log.debug("getRootFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getRootFolder(null);
			log.debug("getRootFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getTrashFolder")
	public Folder getTrashFolder() throws GenericException {
		try {
			log.debug("getTrashFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getTrashFolder(null);
			log.debug("getTrashFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getTemplatesFolder")
	public Folder getTemplatesFolder() throws GenericException {
		try {
			log.debug("getTemplatesFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getTemplatesFolder(null);
			log.debug("getTemplatesFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getPersonalFolder")
	public Folder getPersonalFolder() throws GenericException {
		try {
			log.debug("getPersonalFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getPersonalFolder(null);
			log.debug("getPersonalFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getMailFolder")
	public Folder getMailFolder() throws GenericException {
		try {
			log.debug("getMailFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getMailFolder(null);
			log.debug("getMailFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getThesaurusFolder")
	public Folder getThesaurusFolder() throws GenericException {
		try {
			log.debug("getThesaurusFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getThesaurusFolder(null);
			log.debug("getThesaurusFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getCategoriesFolder")
	public Folder getCategoriesFolder() throws GenericException {
		try {
			log.debug("getCategoriesFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getCategoriesFolder(null);
			log.debug("getCategoriesFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/purgeTrash")
	public void purgeTrash() throws GenericException {
		try {
			log.debug("purgeTrash()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			rm.purgeTrash(null);
			log.debug("purgeTrash: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUpdateMessage")
	public String getUpdateMessage() throws GenericException {
		try {
			log.debug("getUpdateMessage()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String msg = rm.getUpdateMessage(null);
			log.debug("getUpdateMessage: {}", msg);
			return msg;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getRepositoryUuid")
	public String getRepositoryUuid() throws GenericException {
		try {
			log.debug("getRepositoryUuid()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String uuid = rm.getRepositoryUuid(null);
			log.debug("getRepositoryUuid: {}", uuid);
			return uuid;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/hasNode")
	@Produces(MimeTypeConfig.MIME_TEXT)
	public Boolean hasNode(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("hasNode()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			boolean has = rm.hasNode(null, nodeId);
			log.debug("hasNode: {}", has);
			return new Boolean(has);
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getNodePath/{uuid}")
	public String getNodePath(@PathParam("uuid") String uuid) throws GenericException {
		try {
			log.debug("getNodePath()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String path = rm.getNodePath(null, uuid);
			log.debug("getNodePath: {}", path);
			return path;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getNodeUuid")
	public String getNodeUuid(@QueryParam("nodePath") String nodePath) throws GenericException {
		try {
			log.debug("getNodeUuid()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String path = rm.getNodeUuid(null, nodePath);
			log.debug("getNodeUuid: {}", path);
			return path;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getAppVersion")
	public AppVersion getAppVersion() throws GenericException {
		try {
			log.debug("getAppVersion()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			AppVersion ver = rm.getAppVersion(null);
			log.debug("getAppVersion: {}", ver);
			return ver;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/executeScript")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "script" parameter comes in the POST request body (encoded as XML or JSON).
	public ScriptExecutionResult executeScript(List<Attachment> atts) throws GenericException {
		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		ByteArrayOutputStream baErr = new ByteArrayOutputStream();
		ScriptExecutionResult result = new ScriptExecutionResult();
		InputStream is = null;

		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				for (Attachment att : atts) {
					if ("script".equals(att.getContentDisposition().getParameter("name"))) {
						is = att.getDataHandler().getInputStream();
					}
				}

				if (is != null) {
					String script = IOUtils.toString(is);
					PrintStream psOut = new PrintStream(baOut);
					PrintStream psErr = new PrintStream(baErr);
					Interpreter bsh = new Interpreter(null, psOut, psErr, false);

					try {
						Object ret = bsh.eval(script);
						result.setResult(String.valueOf(ret));
					} finally {
						psOut.flush();
						psErr.flush();
					}

					result.setStderr(baErr.toString());
					result.setStdout(baOut.toString());
					return result;
				} else {
					throw new Exception("Missing script parameter");
				}
			} else {
				throw new AccessDeniedException("Only admin users allowed");
			}
		} catch (Exception e) {
			throw new GenericException(e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(baErr);
			IOUtils.closeQuietly(baOut);
		}
	}

	@POST
	@Path("/executeSqlQuery")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "query" parameter comes in the POST request body (encoded as XML or JSON).
	public SqlQueryResults executeSqlQuery(List<Attachment> atts) throws GenericException {
		SqlQueryResults results = new SqlQueryResults();
		InputStream is = null;

		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				for (Attachment att : atts) {
					if ("query".equals(att.getContentDisposition().getParameter("name"))) {
						is = att.getDataHandler().getInputStream();
					}
				}

				if (is != null) {
					String query = IOUtils.toString(is);

					if (query.toUpperCase().startsWith("--") || query.equals("") || query.equals("\r")) {
						// Is a comment, so ignore it
					} else {
						for (List<String> row : LegacyDAO.executeSQL(query)) {
							SqlQueryResultColumns columns = new SqlQueryResultColumns();

							for (String col : row) {
								columns.getColumns().add(col);
							}

							results.getResults().add(columns);
						}
					}

					return results;
				} else {
					throw new Exception("Missing query parameter");
				}
			} else {
				throw new AccessDeniedException("Only admin users allowed");
			}
		} catch (DatabaseException e) {
			if (e.getCause() instanceof SQLGrammarException) {
				SQLGrammarException sqlGrammar = (SQLGrammarException) e.getCause();

				if (sqlGrammar.getCause() instanceof SQLException) {
					throw new GenericException((SQLException) sqlGrammar.getCause());
				} else {
					throw new GenericException(sqlGrammar);
				}
			} else {
				throw new GenericException(e);
			}
		} catch (Exception e) {
			throw new GenericException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@POST
	@Path("/executeHqlQuery")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "query" parameter comes in the POST request body (encoded as XML or JSON).
	public HqlQueryResults executeHqlQuery(List<Attachment> atts) throws GenericException {
		HqlQueryResults results = new HqlQueryResults();
		InputStream is = null;

		try {
			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				for (Attachment att : atts) {
					if ("query".equals(att.getContentDisposition().getParameter("name"))) {
						is = att.getDataHandler().getInputStream();
					}
				}

				if (is != null) {
					String query = IOUtils.toString(is);

					if (query.toUpperCase().startsWith("--") || query.equals("") || query.equals("\r")) {
						// Is a comment, so ignore it
					} else {
						for (Object obj : LegacyDAO.executeHQL(query)) {
							if (obj instanceof Object[]) {
								Object[] ao = (Object[]) obj;

								for (int j = 0; j < ao.length; j++) {
									results.getResults().add(String.valueOf(ao[j]));
								}
							} else {
								results.getResults().add(String.valueOf(obj));
							}
						}
					}

					return results;
				} else {
					throw new Exception("Missing query parameter");
				}
			} else {
				throw new AccessDeniedException("Only admin users allowed");
			}
		} catch (DatabaseException e) {
			if (e.getCause() instanceof QueryException) {
				throw new GenericException((QueryException) e.getCause());
			} else {
				throw new GenericException(e);
			}
		} catch (Exception e) {
			throw new GenericException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@GET
	@Path("getConfiguration")
	public Configuration getConfiguration(@QueryParam("key") String key) throws GenericException {
		try {
			log.debug("getConfiguration({})", key);
			Configuration config;

			if (PrincipalUtils.hasRole(Config.DEFAULT_ADMIN_ROLE)) {
				config = new Configuration(ConfigDAO.findByPk(key));
			} else {
				if (Config.WEBSERVICES_VISIBLE_PROPERTIES.contains(key)) {
					config = new Configuration(ConfigDAO.findByPk(key));
				} else {
					throw new AccessDeniedException(key);
				}
			}

			return config;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
