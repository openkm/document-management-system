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

package com.openkm.module.db;

import com.openkm.core.*;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.module.NotificationModule;
import com.openkm.module.common.CommonNotificationModule;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.PathUtils;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbNotificationModule implements NotificationModule {
	private static Logger log = LoggerFactory.getLogger(DbNotificationModule.class);

	@Override
	public void subscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("subscribe({}, {})", token, nodePath);
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			NodeBaseDAO.getInstance().subscribe(uuid, auth.getName());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("subscribe: void");
	}

	@Override
	public void unsubscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("unsubscribe({}, {})", token, nodePath);
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			NodeBaseDAO.getInstance().unsubscribe(uuid, auth.getName());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("unsubscribe: void");
	}

	@Override
	public Set<String> getSubscriptors(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getSusbcriptions({}, {})", token, nodePath);
		Set<String> users = new HashSet<String>();
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			users = NodeBaseDAO.getInstance().getSubscriptors(uuid);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getSusbcriptions: {}", users);
		return users;
	}

	@Override
	public void notify(String token, String nodeId, List<String> users, List<String> mails, String message, boolean attachment)
			throws PathNotFoundException, AccessDeniedException, PrincipalAdapterException, RepositoryException, DatabaseException,
			IOException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[]{token, nodeId, users, mails, message});
		List<String> nodesIds = new ArrayList<String>();
		nodesIds.add(nodeId);
		notify(token, nodesIds, users, mails, message, attachment);
	}

	public void notify(String token, List<String> nodesIds, List<String> users, List<String> mails, String message, boolean attachment)
			throws PathNotFoundException, AccessDeniedException, PrincipalAdapterException, RepositoryException, DatabaseException,
			IOException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[]{token, nodesIds, users, mails, message});
		List<String> to = new ArrayList<String>(mails);
		Authentication auth = null, oldAuth = null;

		if (!users.isEmpty() || !mails.isEmpty()) {
			try {
				log.debug("Nodo: {}, Message: {}", nodesIds, message);

				if (token == null) {
					auth = PrincipalUtils.getAuthentication();
				} else {
					oldAuth = PrincipalUtils.getAuthentication();
					auth = PrincipalUtils.getAuthenticationByToken(token);
				}

				for (String usr : users) {
					String mail = new DbAuthModule().getMail(token, usr);

					if (mail != null) {
						to.add(mail);
					}
				}

				// Get session user email address && send notification
				String from = new DbAuthModule().getMail(token, auth.getName());

				if (!to.isEmpty() && from != null && !from.isEmpty()) {
					ArrayList<CommonNotificationModule.NodeInfo> nodesInfo = new ArrayList<CommonNotificationModule.NodeInfo>();
					String nodePath = null;
					String nodeUuid = null;

					for (String nodeId : nodesIds) {
						if (PathUtils.isPath(nodeId)) {
							nodePath = nodeId;
							nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
						} else {
							nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
							nodeUuid = nodeId;
						}

						CommonNotificationModule.NodeInfo nodeInfo = new CommonNotificationModule.NodeInfo();
						nodeInfo.setUuid(nodeUuid);
						nodeInfo.setPath(nodePath);
						nodesInfo.add(nodeInfo);
					}

					CommonNotificationModule.sendNotification(auth.getName(), nodesInfo, from, to, message, attachment);
				} else {
					throw new PrincipalAdapterException("Can't send notification because 'from' or 'to' is empty");
				}
			} catch (TemplateException e) {
				throw new IOException("TemplateException: " + e.getMessage(), e);
			} catch (MessagingException e) {
				throw new IOException("MessagingException: " + e.getMessage(), e);
			} finally {
				if (token != null) {
					PrincipalUtils.setAuthentication(oldAuth);
				}
			}
		}
	}

	@Override
	public void proposedSubscription(String token, String nodeId, List<String> users, String comment) throws PathNotFoundException,
			AccessDeniedException, PrincipalAdapterException, RepositoryException, DatabaseException, IOException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[]{token, nodeId, users, comment});
		List<String> nodesIds = new ArrayList<String>();
		nodesIds.add(nodeId);
		proposedSubscription(token, nodesIds, users, comment);

	}

	public void proposedSubscription(String token, List<String> nodesIds, List<String> users, String comment) throws PathNotFoundException,
			AccessDeniedException, PrincipalAdapterException, RepositoryException, DatabaseException, IOException {
		log.debug("proposedSubscription({},{}, {}, {})", new Object[]{token, nodesIds, users, comment});
		List<String> to = new ArrayList<String>();
		Authentication auth = null, oldAuth = null;

		if (!users.isEmpty()) {
			try {
				log.debug("Nodo: {}, Comment: {}", nodesIds, comment);

				if (token == null) {
					auth = PrincipalUtils.getAuthentication();
				} else {
					oldAuth = PrincipalUtils.getAuthentication();
					auth = PrincipalUtils.getAuthenticationByToken(token);
				}

				for (String usr : users) {
					String mail = new DbAuthModule().getMail(token, usr);

					if (mail != null) {
						to.add(mail);
					}
				}

				// Get session user email address && send notification
				String from = new DbAuthModule().getMail(token, auth.getName());

				if (!to.isEmpty() && from != null && !from.isEmpty()) {
					ArrayList<CommonNotificationModule.NodeInfo> nodesInfo = new ArrayList<CommonNotificationModule.NodeInfo>();
					String nodePath = null;
					String nodeUuid = null;

					for (String nodeId : nodesIds) {
						if (PathUtils.isPath(nodeId)) {
							nodePath = nodeId;
							nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
						} else {
							nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
							nodeUuid = nodeId;
						}

						CommonNotificationModule.NodeInfo nodeInfo = new CommonNotificationModule.NodeInfo();
						nodeInfo.setUuid(nodeUuid);
						nodeInfo.setPath(nodePath);
						nodesInfo.add(nodeInfo);
					}

					CommonNotificationModule.sendProposedSubscription(auth.getName(), nodesInfo, from, to, comment);
				} else {
					throw new PrincipalAdapterException("Can't send notification because 'from' or 'to' is empty");
				}
			} catch (TemplateException e) {
				throw new IOException("TemplateException: " + e.getMessage(), e);
			} catch (MessagingException e) {
				throw new IOException("MessagingException: " + e.getMessage(), e);
			} finally {
				if (token != null) {
					PrincipalUtils.setAuthentication(oldAuth);
				}
			}
		}
	}
}
