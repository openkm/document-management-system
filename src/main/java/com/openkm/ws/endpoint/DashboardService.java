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

package com.openkm.ws.endpoint;

import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.DashboardModule;
import com.openkm.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@WebService(name = "OKMDashboard", serviceName = "OKMDashboard", targetNamespace = "http://ws.openkm.com")
public class DashboardService {
	private static Logger log = LoggerFactory.getLogger(DashboardService.class);

	@WebMethod
	public DashboardDocumentResult[] getUserCheckedOutDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserCheckedOutDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getUserCheckedOutDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getUserCheckedOutDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getUserLastModifiedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserLastModifiedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getUserLastModifiedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getUserLastModifiedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getUserLockedDocuments(@WebParam(name = "token") String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getUserLockedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getUserLockedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getUserLockedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getUserSubscribedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserSubscribedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getUserSubscribedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getUserSubscribedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardFolderResult[] getUserSubscribedFolders(@WebParam(name = "token") String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getUserSubscribedFolders({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardFolderResult> col = dm.getUserSubscribedFolders(token);
		DashboardFolderResult[] result = col.toArray(new DashboardFolderResult[col.size()]);
		log.debug("getUserSubscribedFolders: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getUserLastUploadedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserLastUploadedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getUserLastUploadedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getUserLastUploadedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getUserLastDownloadedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserLastDownloadedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getUserLastDownloadedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getUserLastDownloadedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardMailResult[] getUserLastImportedMails(@WebParam(name = "token") String token) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getUserLastImportedMails({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardMailResult> col = dm.getUserLastImportedMails(token);
		DashboardMailResult[] result = col.toArray(new DashboardMailResult[col.size()]);
		log.debug("getUserLastImportedMails: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getUserLastImportedMailAttachments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserLastImportedMailAttachments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getUserLastImportedMailAttachments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getUserLastImportedMailAttachments: {}", result);
		return result;
	}

	@WebMethod
	public long getUserDocumentsSize(@WebParam(name = "token") String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserDocumentsSize({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		long ret = dm.getUserDocumentsSize(token);
		log.debug("getUserDocumentsSize: {}", ret);
		return ret;
	}

	@WebMethod
	public QueryParams[] getUserSearchs(@WebParam(name = "token") String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserSearchs({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<QueryParams> col = dm.getUserSearchs(token);
		QueryParams[] result = col.toArray(new QueryParams[col.size()]);
		log.debug("getUserSearchs: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] find(@WebParam(name = "token") String token, @WebParam(name = "qpId") int qpId)
			throws IOException, ParseException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("find({}, {})", new Object[]{token, qpId});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.find(token, qpId);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("find: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getLastWeekTopDownloadedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getLastWeekTopDownloadedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getLastWeekTopDownloadedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getLastWeekTopDownloadedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getLastMonthTopDownloadedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getLastMonthTopDownloadedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getLastMonthTopDownloadedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getLastMonthTopDownloadedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getLastWeekTopModifiedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getLastWeekTopModifiedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getLastWeekTopModifiedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getLastWeekTopModifiedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getLastMonthTopModifiedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getLastMonthTopModifiedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getLastMonthTopModifiedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getLastMonthTopModifiedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getLastModifiedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getLastModifiedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getLastModifiedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getLastModifiedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public DashboardDocumentResult[] getLastUploadedDocuments(@WebParam(name = "token") String token)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getLastUploadedDocuments({})", new Object[]{token});
		DashboardModule dm = ModuleManager.getDashboardModule();
		List<DashboardDocumentResult> col = dm.getLastUploadedDocuments(token);
		DashboardDocumentResult[] result = col.toArray(new DashboardDocumentResult[col.size()]);
		log.debug("getLastUploadedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public void visiteNode(@WebParam(name = "token") String token, @WebParam(name = "source") String source,
	                       @WebParam(name = "node") String node, @WebParam(name = "date") Calendar date) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("visiteNode({}, {}, {}, {})", new Object[]{token, source, node, date});
		DashboardModule dm = ModuleManager.getDashboardModule();
		dm.visiteNode(token, source, node, date);
		log.debug("visiteNode: void");
	}
}
