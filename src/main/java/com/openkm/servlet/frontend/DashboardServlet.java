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

package com.openkm.servlet.frontend;

import com.openkm.api.OKMDashboard;
import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.core.*;
import com.openkm.dao.QueryParamsDAO;
import com.openkm.dao.bean.QueryParams;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTDashboardDocumentResult;
import com.openkm.frontend.client.bean.GWTDashboardFolderResult;
import com.openkm.frontend.client.bean.GWTDashboardMailResult;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMDashboardService;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.GWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Servlet Class
 *
 * @web.servlet name="DashboardServlet"
 *                           display-name="Directory tree service"
 *                           description="Directory tree service"
 * @web.servlet-mapping url-pattern="/DashboardServlet"
 * @web.servlet-init-param name="A parameter"
 *                           value="A value"
 */
public class DashboardServlet extends OKMRemoteServiceServlet implements OKMDashboardService {
	private static Logger log = LoggerFactory.getLogger(DashboardServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public List<GWTDashboardDocumentResult> getUserLockedDocuments() throws OKMException {
		log.debug("getUserLockedDocuments()");
		List<GWTDashboardDocumentResult> lockList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			Collection<DashboardDocumentResult> col = OKMDashboard.getInstance().getUserLockedDocuments(null);
			for (Iterator<DashboardDocumentResult> it = col.iterator(); it.hasNext(); ) {
				DashboardDocumentResult documentResult = it.next();
				GWTDashboardDocumentResult documentResultClient;
				documentResultClient = GWTUtil.copy(documentResult);
				lockList.add(documentResultClient);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}


		log.debug("getUserLockedDocuments: {}", lockList);
		return lockList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getUserCheckedOutDocuments() throws OKMException {
		log.debug("getUserCheckedOutDocuments()");
		List<GWTDashboardDocumentResult> chekoutList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			Collection<DashboardDocumentResult> col = OKMDashboard.getInstance().getUserCheckedOutDocuments(null);
			for (Iterator<DashboardDocumentResult> it = col.iterator(); it.hasNext(); ) {
				DashboardDocumentResult documentResult = it.next();
				GWTDashboardDocumentResult documentResultClient = GWTUtil.copy(documentResult);
				chekoutList.add(documentResultClient);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserCheckedOutDocuments: {}", chekoutList);
		return chekoutList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getUserLastModifiedDocuments() throws OKMException {
		log.debug("getUserLastModifiedDocuments()");
		List<GWTDashboardDocumentResult> lastModifiedList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			Collection<DashboardDocumentResult> col = OKMDashboard.getInstance().getUserLastModifiedDocuments(null);
			for (Iterator<DashboardDocumentResult> it = col.iterator(); it.hasNext(); ) {
				DashboardDocumentResult documentResult = it.next();
				GWTDashboardDocumentResult documentResultClient = GWTUtil.copy(documentResult);
				lastModifiedList.add(documentResultClient);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserLastModifiedDocuments: {}", lastModifiedList);
		return lastModifiedList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getUserSubscribedDocuments() throws OKMException {
		log.debug("getUserSubscribedDocuments()");
		List<GWTDashboardDocumentResult> subscribedList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			Collection<DashboardDocumentResult> col = OKMDashboard.getInstance().getUserSubscribedDocuments(null);
			for (Iterator<DashboardDocumentResult> it = col.iterator(); it.hasNext(); ) {
				DashboardDocumentResult documentResult = it.next();
				GWTDashboardDocumentResult documentResultClient = GWTUtil.copy(documentResult);
				subscribedList.add(documentResultClient);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserSubscribedDocuments: {}", subscribedList);
		return subscribedList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getUserLastUploadedDocuments() throws OKMException {
		log.debug("getUserLastUploadedDocuments()");
		List<GWTDashboardDocumentResult> lastUploadedList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			Collection<DashboardDocumentResult> col = OKMDashboard.getInstance().getUserLastUploadedDocuments(null);
			for (Iterator<DashboardDocumentResult> it = col.iterator(); it.hasNext(); ) {
				DashboardDocumentResult documentResult = it.next();
				GWTDashboardDocumentResult documentResultClient = GWTUtil.copy(documentResult);
				lastUploadedList.add(documentResultClient);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserLastUploadedDocuments: {}", lastUploadedList);
		return lastUploadedList;
	}

	@Override
	public List<GWTDashboardFolderResult> getUserSubscribedFolders() throws OKMException {
		log.debug("getUserSubscribedFolders()");
		List<GWTDashboardFolderResult> subscribedList = new ArrayList<GWTDashboardFolderResult>();
		updateSessionManager();

		try {
			Collection<DashboardFolderResult> col = OKMDashboard.getInstance().getUserSubscribedFolders(null);
			for (Iterator<DashboardFolderResult> it = col.iterator(); it.hasNext(); ) {
				DashboardFolderResult folderResult = it.next();
				GWTDashboardFolderResult folderResultClient;
				folderResultClient = GWTUtil.copy(folderResult);
				subscribedList.add(folderResultClient);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserSubscribedFolders: {}", subscribedList);
		return subscribedList;
	}

	@Override
	public List<GWTQueryParams> getUserSearchs() throws OKMException {
		log.debug("getUserSearchs()");
		List<GWTQueryParams> searchList = new ArrayList<GWTQueryParams>();
		updateSessionManager();

		try {
			for (Iterator<QueryParams> it = OKMDashboard.getInstance().getUserSearchs(null).iterator(); it.hasNext(); ) {
				searchList.add(GWTUtil.copy(it.next()));
			}
			for (QueryParams params : QueryParamsDAO.findShared(getThreadLocalRequest().getRemoteUser())) {
				// Only dashboard queries
				if (params.isDashboard()) {
					GWTQueryParams gWTQueryParams = GWTUtil.copy(params);
					gWTQueryParams.setShared(true);
					searchList.add(gWTQueryParams);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		}

		log.debug("getUserSearchs: {}", searchList);
		return searchList;
	}

	@Override
	public List<GWTDashboardDocumentResult> find(int id) throws OKMException {
		log.debug("find({})", id);
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().find(null, id).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("find: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getLastWeekTopDownloadedDocuments() throws OKMException {
		log.debug("getLastWeekTopDownloadedDocuments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getLastWeekTopDownloadedDocuments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getLastWeekTopDownloadedDocuments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getLastMonthTopDownloadedDocuments() throws OKMException {
		log.debug("getLastMonthTopDownloadedDocuments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getLastMonthTopDownloadedDocuments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getLastMonthTopDownloadedDocuments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getLastWeekTopModifiedDocuments() throws OKMException {
		log.debug("getLastWeekTopModifiedDocuments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getLastWeekTopModifiedDocuments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getLastWeekTopModifiedDocuments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getLastMonthTopModifiedDocuments() throws OKMException {
		log.debug("getLastMonthTopModifiedDocuments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getLastMonthTopModifiedDocuments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getLastMonthTopModifiedDocuments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getUserLastDownloadedDocuments() throws OKMException {
		log.debug("getUserLastDownloadedDocuments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getUserLastDownloadedDocuments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserLastDownloadedDocuments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getLastModifiedDocuments() throws OKMException {
		log.debug("getLastModifiedDocuments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getLastModifiedDocuments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getLastModifiedDocuments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getLastUploadedDocuments() throws OKMException {
		log.debug("getLastWeekTopUploadedDocuments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getLastUploadedDocuments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getLastWeekTopUploadedDocuments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardDocumentResult> getUserLastImportedMailAttachments() throws OKMException {
		log.debug("getUserLastImportedMailAttachments()");
		List<GWTDashboardDocumentResult> docList = new ArrayList<GWTDashboardDocumentResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardDocumentResult> it = OKMDashboard.getInstance().getUserLastImportedMailAttachments(null).iterator(); it.hasNext(); ) {
				docList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserLastImportedMailAttachments: {}", docList);
		return docList;
	}

	@Override
	public List<GWTDashboardMailResult> getUserLastImportedMails() throws OKMException {
		log.debug("getUserLastImportedMails()");
		List<GWTDashboardMailResult> mailList = new ArrayList<GWTDashboardMailResult>();
		updateSessionManager();

		try {
			for (Iterator<DashboardMailResult> it = OKMDashboard.getInstance().getUserLastImportedMails(null).iterator(); it.hasNext(); ) {
				mailList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}

		log.debug("getUserLastImportedMails: {}", mailList);
		return mailList;

	}

	@Override
	public void visiteNode(String source, String node, Date date) throws OKMException {
		log.debug("visiteNode({}, {}, {})", new Object[]{source, node, date});
		updateSessionManager();

		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			OKMDashboard.getInstance().visiteNode(null, source, node, cal);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDashboardService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		log.debug("visiteNode: void");
	}
}