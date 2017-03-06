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

package com.openkm.cache;

import com.openkm.core.DatabaseException;
import com.openkm.dao.UserNodeKeywordsDAO;
import com.openkm.dao.bean.cache.UserNodeKeywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UserNodeKeywordsManager {
	private static Logger log = LoggerFactory.getLogger(UserNodeKeywordsManager.class);
	private static Map<String, Map<String, UserNodeKeywords>> userNodeKeywordsMgr = new HashMap<String, Map<String, UserNodeKeywords>>();

	/**
	 * Get user document keywords
	 *
	 * @param user The user which have the document keywords cached.
	 * @return A Map which key is the node UUID and the value is UserNodeKeywords. 
	 */
	public static Map<String, UserNodeKeywords> get(String user) {
		Map<String, UserNodeKeywords> userDocKeywords = userNodeKeywordsMgr.get(user);

		if (userDocKeywords == null) {
			userDocKeywords = new HashMap<String, UserNodeKeywords>();
		}

		return userDocKeywords;
	}

	/**
	 * Add keyword to the user document cache.
	 *
	 * @param user user The user which will have the document keywords cached.
	 * @param nodeUuid The node UUID which keyword need to be cache.
	 * @param keyword The keyword to be cached.
	 */
	public static synchronized void add(String user, String nodeUuid, String keyword) {
		log.info("add({}, {}, {})", new Object[]{user, nodeUuid, keyword});
		Map<String, UserNodeKeywords> usrDocs = get(user);
		UserNodeKeywords udk = usrDocs.get(nodeUuid);

		if (udk == null) {
			udk = new UserNodeKeywords();
			udk.setUser(user);
			udk.setNode(nodeUuid);
			usrDocs.put(nodeUuid, udk);
			userNodeKeywordsMgr.put(user, usrDocs);
		}

		udk.getKeywords().add(keyword);
	}

	/**
	 * Remove keyword from the user document cache.
	 */
	public static synchronized void remove(String user, String nodeUuid, String keyword) {
		Map<String, UserNodeKeywords> usrDocs = get(user);
		UserNodeKeywords udk = usrDocs.get(nodeUuid);

		if (udk == null) {
			udk = new UserNodeKeywords();
			udk.setUser(user);
			udk.setNode(nodeUuid);
			usrDocs.put(nodeUuid, udk);
			userNodeKeywordsMgr.put(user, usrDocs);
		}

		udk.getKeywords().remove(keyword);

		if (udk.getKeywords().isEmpty()) {
			usrDocs.remove(nodeUuid);
		}
	}

	/**
	 *
	 */
	public static synchronized void serialize() throws DatabaseException {
		UserNodeKeywordsDAO.clean();

		for (String user : userNodeKeywordsMgr.keySet()) {
			log.info("User: {}", user);

			for (UserNodeKeywords udk : userNodeKeywordsMgr.get(user).values()) {
				log.info("Document: {}", udk);
				UserNodeKeywordsDAO.create(udk);
			}
		}
	}

	/**
	 *
	 */
	public static synchronized void deserialize() throws DatabaseException {
		for (String user : UserNodeKeywordsDAO.findUsers()) {
			Map<String, UserNodeKeywords> udkMap = new HashMap<String, UserNodeKeywords>();

			for (UserNodeKeywords udk : UserNodeKeywordsDAO.findByUser(user)) {
				udkMap.put(udk.getNode(), udk);
			}

			userNodeKeywordsMgr.put(user, udkMap);
		}
	}
}
