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

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.module.ModuleManager;
import com.openkm.module.NotificationModule;
import com.openkm.principal.PrincipalAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@WebService(name = "OKMNotification", serviceName = "OKMNotification", targetNamespace = "http://ws.openkm.com")
public class NotificationService {
	private static Logger log = LoggerFactory.getLogger(NotificationService.class);

	@WebMethod
	public void subscribe(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("subscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.subscribe(token, nodePath);
		log.debug("subscribe: void");
	}

	@WebMethod
	public void unsubscribe(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("unsubscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.unsubscribe(token, nodePath);
		log.debug("unsubscribe: void");
	}

	@WebMethod
	public String[] getSubscriptors(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getSubscriptors({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		Set<String> col = nm.getSubscriptors(token, nodePath);
		String[] result = (String[]) col.toArray(new String[col.size()]);
		log.debug("getSubscriptors: {}", result);
		return result;
	}

	@WebMethod
	public void notify(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
	                   @WebParam(name = "users") String[] users, @WebParam(name = "mails") String[] mails,
	                   @WebParam(name = "message") String message, @WebParam(name = "attachment") boolean attachment) throws
			PathNotFoundException, AccessDeniedException, PrincipalAdapterException, RepositoryException,
			DatabaseException, IOException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[]{token, nodePath, users, message, attachment});
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.notify(token, nodePath, Arrays.asList(users), Arrays.asList(mails), message, attachment);
		log.debug("notify: void");
	}
}
