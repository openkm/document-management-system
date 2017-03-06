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

package com.openkm.api;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.module.ModuleManager;
import com.openkm.module.NotificationModule;
import com.openkm.principal.PrincipalAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author pavila
 *
 */
public class OKMNotification implements NotificationModule {
	private static Logger log = LoggerFactory.getLogger(OKMNotification.class);
	private static OKMNotification instance = new OKMNotification();

	private OKMNotification() {
	}

	public static OKMNotification getInstance() {
		return instance;
	}

	@Override
	public void subscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("subscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.subscribe(token, nodePath);
		log.debug("subscribe: void");
	}

	@Override
	public void unsubscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("unsubscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.unsubscribe(token, nodePath);
		log.debug("unsubscribe: void");
	}

	@Override
	public Set<String> getSubscriptors(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getSubscriptors({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		Set<String> users = nm.getSubscriptors(token, nodePath);
		log.debug("getSubscriptors: {}", users);
		return users;
	}

	@Override
	public void notify(String token, String nodeId, List<String> users, List<String> mails, String message, boolean attachment)
			throws PathNotFoundException, AccessDeniedException, PrincipalAdapterException, RepositoryException,
			DatabaseException, IOException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[]{token, nodeId, users, mails, message, attachment});
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.notify(token, nodeId, users, mails, message, attachment);
		log.debug("notify: void");
	}

	@Override
	public void proposedSubscription(String token, String nodeId, List<String> users, String comment)
			throws PathNotFoundException, AccessDeniedException, PrincipalAdapterException, RepositoryException,
			DatabaseException, IOException {
		log.debug("proposedSubscription({}, {}, {}, {})", new Object[]{token, nodeId, users, comment});
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.proposedSubscription(token, nodeId, users, comment);
		log.debug("proposedSubscription: void");
	}
}
