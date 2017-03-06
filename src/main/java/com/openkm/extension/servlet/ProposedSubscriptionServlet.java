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

package com.openkm.extension.servlet;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMNotification;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.extension.dao.ProposedSubscriptionDAO;
import com.openkm.extension.dao.bean.ProposedSubscriptionReceived;
import com.openkm.extension.frontend.client.service.OKMProposedSubscriptionService;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTProposedSubscriptionReceived;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * ProposedSubscriptionServlet
 */
public class ProposedSubscriptionServlet extends OKMRemoteServiceServlet implements OKMProposedSubscriptionService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ProposedSubscriptionServlet.class);

	@Override
	public void send(String uuid, String type, String users, String roles, String comment) throws OKMException {
		log.debug("create({}, {}, {}, {}, {})", new Object[]{uuid, type, users, roles, comment});
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
				ProposedSubscriptionDAO.send(remoteUser, to, user, uuid, type, comment);
			}
			OKMNotification.getInstance().proposedSubscription(null, uuid, userNames, comment);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_PrincipalAdapter),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_IO), e.getMessage());
		}
	}

	@Override
	public Map<String, Long> findProposedSubscriptionsUsersFrom() throws OKMException {
		log.debug("findProposedSubscriptionsUsersFrom()");
		Map<String, Long> received = new HashMap<String, Long>();
		updateSessionManager();

		try {
			String user = getThreadLocalRequest().getRemoteUser();
			Map<String, Long> unreadMap = ProposedSubscriptionDAO.findProposedSubscriptionsUsersFromUnread(user);

			for (String sender : ProposedSubscriptionDAO.findProposedSubscriptionsUsersFrom(user)) {
				if (unreadMap.containsKey(sender)) {
					received.put(sender, unreadMap.get(sender));
				} else {
					received.put(sender, new Long(0));
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		log.debug("findProposedSubscriptionsUsersFrom: Map" + received);
		return received;
	}

	@Override
	public List<GWTProposedSubscriptionReceived> findProposedSubscriptionByMeFromUser(String user) throws OKMException {
		log.debug("findProposedSubscriptionByMeFromUser()");
		updateSessionManager();
		List<GWTProposedSubscriptionReceived> proposedQuerySubscriptionList = new ArrayList<GWTProposedSubscriptionReceived>();

		try {
			for (ProposedSubscriptionReceived proposedSubscriptionReceived : ProposedSubscriptionDAO.findProposedSubscriptionByMeFromUser(
					getThreadLocalRequest().getRemoteUser(), user)) {
				String path = NodeBaseDAO.getInstance().getPathFromUuid(proposedSubscriptionReceived.getNode());
				proposedQuerySubscriptionList.add(GWTUtil.copy(proposedSubscriptionReceived, path));
			}

			return proposedQuerySubscriptionList;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}
	}

	@Override
	public void markSeen(int msgId) throws OKMException {
		log.debug("markSeen({})", msgId);
		updateSessionManager();

		try {
			ProposedSubscriptionDAO.markSeen(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		log.debug("markSeen() : void");
	}

	@Override
	public void markAccepted(int msgId) throws OKMException {
		log.debug("markAccepted({})", msgId);
		updateSessionManager();

		try {
			ProposedSubscriptionDAO.markAccepted(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		log.debug("markAccepted() : void");
	}

	@Override
	public void deleteReceived(int msgId) throws OKMException {
		log.debug("deleteReceived({})", msgId);
		updateSessionManager();

		try {
			ProposedSubscriptionDAO.deleteReceived(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		log.debug("deleteReceived() : void");
	}

	@Override
	public void deleteSent(int msgId) throws OKMException {
		log.debug("deleteSent({})", msgId);
		updateSessionManager();

		try {
			ProposedSubscriptionDAO.deleteSent(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		log.debug("deleteSent() : void");
	}

	@Override
	public void deleteProposedSubscriptionByMeFromUser(String sender) throws OKMException {
		log.debug("deleteProposedSubscriptionByMeFromUser()");
		updateSessionManager();
		List<String> IdToDelete = new ArrayList<String>();

		try {
			for (ProposedSubscriptionReceived ps : ProposedSubscriptionDAO.findProposedSubscriptionByMeFromUser(getThreadLocalRequest()
					.getRemoteUser(), sender)) {
				if (ps.getFrom().equals(sender)) {
					IdToDelete.add(String.valueOf(ps.getId()));
				}
			}

			for (String id : IdToDelete) {
				ProposedSubscriptionDAO.deleteReceived(Integer.valueOf(id));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		}

		log.debug("deleteProposedSubscriptionByMeFromUser: void");
	}
}
