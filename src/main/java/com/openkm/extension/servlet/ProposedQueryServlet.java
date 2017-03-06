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

package com.openkm.extension.servlet;

import com.openkm.api.OKMAuth;
import com.openkm.core.*;
import com.openkm.dao.QueryParamsDAO;
import com.openkm.dao.bean.QueryParams;
import com.openkm.extension.dao.ProposedQueryDAO;
import com.openkm.extension.dao.bean.ProposedQueryReceived;
import com.openkm.extension.frontend.client.service.OKMProposedQueryService;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTProposedQueryReceived;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * ProposedQueryServlet
 */
public class ProposedQueryServlet extends OKMRemoteServiceServlet implements OKMProposedQueryService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ProposedQueryServlet.class);

	@Override
	public void create(long qpId, String users, String roles, String comment) throws OKMException {
		Object obj[] = {(Object) qpId, (Object) users, (Object) roles, (Object) comment};
		log.debug("create({}, {}, {}, {})", obj);
		updateSessionManager();

		try {
			String remoteUser = getThreadLocalRequest().getRemoteUser();
			String to = "";
			if (!users.equals("") && !roles.equals("")) {
				to = users + "," + roles;
			} else {
				to = users + roles;
			}
			List<String> userNames = new ArrayList<String>(Arrays.asList(users.isEmpty() ? new String[0] : users.split(",")));
			List<String> roleNames = new ArrayList<String>(Arrays.asList(roles.isEmpty() ? new String[0] : roles.split(",")));

			for (String role : roleNames) {
				List<String> usersInRole = OKMAuth.getInstance().getUsersByRole(null, role);

				for (String user : usersInRole) {
					if (!userNames.contains(user)) {
						userNames.add(user);
					}
				}
			}

			// You might not sending messages to youself
			if (userNames.contains(remoteUser)) {
				userNames.remove(remoteUser);
			}

			for (String user : userNames) {
				ProposedQueryDAO.send(qpId, remoteUser, to, user, comment);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
	}

	@Override
	public Map<String, Long> findProposedQueriesUsersFrom() throws OKMException {
		log.debug("findProposedQueriesUsersFrom()");
		Map<String, Long> received = new HashMap<String, Long>();
		updateSessionManager();
		try {
			String user = getThreadLocalRequest().getRemoteUser();
			Map<String, Long> unreadMap = ProposedQueryDAO.findProposedQueriesUsersFromUnread(user);
			for (String sender : ProposedQueryDAO.findProposedQueriesUsersFrom(user)) {
				if (unreadMap.containsKey(sender)) {
					received.put(sender, unreadMap.get(sender));
				} else {
					received.put(sender, new Long(0));
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("findProposedQueriesUsersFromUnread: Map" + received);
		return received;
	}

	@Override
	public void deleteReceived(long pqId) throws OKMException {
		log.debug("deleteReceived({})", pqId);
		updateSessionManager();
		try {
			ProposedQueryDAO.deleteReceived(pqId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("deleteReceived() : void");
	}

	@Override
	public void deleteSent(long pqId) throws OKMException {
		log.debug("deleteSent({})", pqId);
		updateSessionManager();
		try {
			ProposedQueryDAO.deleteSent(pqId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("deleteSent() : void");
	}

	@Override
	public List<GWTProposedQueryReceived> findProposedQueryByMeFromUser(String user) throws OKMException {
		log.debug("findProposedQueryByMeFromUser()");
		updateSessionManager();
		List<GWTProposedQueryReceived> proposedQueryReceivedList = new ArrayList<GWTProposedQueryReceived>();

		try {
			String me = getThreadLocalRequest().getRemoteUser();

			for (QueryParams queryParams : QueryParamsDAO.findProposedQueryByMeFromUser(me, user)) {
				for (ProposedQueryReceived proposedQueryReceived : queryParams.getProposedReceived()) {
					// Queries can have several proposals to other users, might only be the mine
					if (proposedQueryReceived.getUser().equals(me)) {
						proposedQueryReceivedList.add(GWTUtil.copy(proposedQueryReceived, queryParams));
					}
				}
			}

			return proposedQueryReceivedList;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMessageService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMessageService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		}
	}

	@Override
	public void markAccepted(long msgId) throws OKMException {
		log.debug("markAccepted({})", msgId);
		updateSessionManager();
		try {
			ProposedQueryDAO.markAccepted(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("markAccepted() : void");
	}

	@Override
	public void markSeen(long msgId) throws OKMException {
		log.debug("markSeen({})", msgId);
		updateSessionManager();
		try {
			ProposedQueryDAO.markSeen(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("markSeen() : void");
	}

	@Override
	public void deleteProposedQueryByMeFromUser(String user) throws OKMException {
		log.debug("deleteProposedQueryByMeFromUser({})", user);
		List<String> pqId = new ArrayList<String>();
		updateSessionManager();
		try {
			for (ProposedQueryReceived proposedQueryReceived : ProposedQueryDAO.findProposedQueryByMeFromUser(getThreadLocalRequest().getRemoteUser(), user)) {
				pqId.add(String.valueOf(proposedQueryReceived.getId()));
			}
			for (String id : pqId) {
				ProposedQueryDAO.deleteReceived(Integer.valueOf(id));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
}
	