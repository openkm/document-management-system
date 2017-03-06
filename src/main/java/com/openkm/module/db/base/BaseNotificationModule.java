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

package com.openkm.module.db.base;

import com.openkm.bean.Permission;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeBase;
import com.openkm.module.common.CommonNotificationModule;
import com.openkm.module.db.DbAuthModule;
import com.openkm.module.db.stuff.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BaseNotificationModule {
	private static Logger log = LoggerFactory.getLogger(BaseNotificationModule.class);

	/**
	 * Check for user subscriptions and send an notification
	 *
	 * @param node Node modified (Document or Folder)
	 * @param user User who generated the modification event
	 * @param eventType Type of modification event
	 */
	public static void checkSubscriptions(NodeBase node, String user, String eventType, String comment) {
		log.debug("checkSubscriptions({}, {}, {}, {})", new Object[]{node, user, eventType, comment});
		Set<String> users = new HashSet<String>();
		Set<String> mails = new HashSet<String>();

		try {
			for (String usr : checkSubscriptionsHelper(node.getUuid())) {
				// Remove user who don't have access rights
				if (SecurityHelper.isGranted(node, usr, Permission.READ)) {
					users.add(usr);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		/**
		 * Mail notification
		 */
		try {
			for (String userId : users) {
				String mail = new DbAuthModule().getMail(null, userId);

				if (mail != null && !mail.isEmpty()) {
					mails.add(mail);
				}
			}

			if (!mails.isEmpty()) {
				String path = NodeBaseDAO.getInstance().getPathFromUuid(node.getUuid());
				CommonNotificationModule.sendMailSubscription(user, node.getUuid(), path, eventType, comment, mails);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		/**
		 * Twitter notification
		 */
		try {
			if (users != null && !users.isEmpty() && !Config.SUBSCRIPTION_TWITTER_USER.equals("")
					&& !Config.SUBSCRIPTION_TWITTER_PASSWORD.equals("")) {
				String path = NodeBaseDAO.getInstance().getPathFromUuid(node.getUuid());
				CommonNotificationModule.sendTwitterSubscription(user, path, eventType, comment, users);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		log.debug("checkSubscriptions: void");
	}

	/**
	 * Check for subscriptions recursively
	 */
	private static Set<String> checkSubscriptionsHelper(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("checkSubscriptionsHelper: {}", uuid);
		Set<String> subscriptors = NodeBaseDAO.getInstance().getSubscriptors(uuid);

		// An user shouldn't be notified twice
		String parentUuid = NodeBaseDAO.getInstance().getParentUuid(uuid);

		if (!Config.ROOT_NODE_UUID.equals(parentUuid)) {
			Set<String> tmp = checkSubscriptionsHelper(parentUuid);

			for (Iterator<String> it = tmp.iterator(); it.hasNext(); ) {
				String usr = it.next();

				if (!subscriptors.contains(usr)) {
					subscriptors.add(usr);
				}
			}
		}

		return subscriptors;
	}
}
