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

package com.openkm.servlet.frontend;

import com.openkm.api.OKMRepository;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTUINotification;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMUINotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UINotificationServlet
 *
 * Servlet Class
 */
public class UINotificationServlet extends OKMRemoteServiceServlet implements OKMUINotificationService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(UINotificationServlet.class);
	private static int count = 0;
	private static List<GWTUINotification> notifications = new ArrayList<GWTUINotification>();
	private static int indexToDelete = 0;
	private static String msg = "";

	@Override
	public List<GWTUINotification> get() throws OKMException {
		log.debug("get()");

		try {
			// Add new updated message
			String uMsg = OKMRepository.getInstance().getUpdateMessage(null);
			if (uMsg != null && !uMsg.equals("") && !msg.equals(uMsg)) {
				msg = uMsg;
				add(GWTUINotification.ACTION_NONE, uMsg, GWTUINotification.TYPE_PERMANENT, false);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		}

		log.debug("get: {}", notifications);
		return notifications;
	}

	/**
	 * Add notification message.
	 */
	public static void add(int action, String message, int type, boolean show) {
		log.debug("add({}, {}, {}, {})", new Object[]{action, message, type, show});
		GWTUINotification uin = new GWTUINotification();
		uin.setId(getId());
		uin.setDate(new Date());
		uin.setAction(action);
		uin.setMessage(message);
		uin.setType(type);
		uin.setShow(show);
		notifications.add(uin);
	}

	/**
	 * DElete notification message.
	 */
	public static void delete(int id) {
		log.debug("delete({})", id);

		for (GWTUINotification uin : notifications) {
			if (uin.getId() == id) {
				notifications.remove(uin);
				break;
			}
		}
	}

	/**
	 * clean
	 */
	public static void clean() {
		log.debug("clean()");

		if (indexToDelete > 0) {
			List<GWTUINotification> toDelete = new ArrayList<GWTUINotification>();

			for (int i = 0; (i < notifications.size() && i < indexToDelete); i++) {
				// Deleting temporal notifications
				if (notifications.get(i).getType() == GWTUINotification.TYPE_TEMPORAL) {
					toDelete.add(notifications.get(i));
				}
			}

			notifications.removeAll(toDelete);
		}

		indexToDelete = notifications.size(); // Mark position to be deleted next time
	}

	/**
	 * Get all notification messages
	 */
	public static List<GWTUINotification> getAll() {
		log.debug("getAll()");
		return notifications;
	}

	/**
	 * Find notification message.
	 */
	public static GWTUINotification findById(int id) {
		log.debug("findById({})", id);

		for (GWTUINotification uin : notifications) {
			if (uin.getId() == id) {
				return uin;
			}
		}

		return null;
	}

	/**
	 * Get notification message id.
	 */
	private synchronized static int getId() {
		return ++count;
	}
}
