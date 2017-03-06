package com.openkm.cache;

import com.openkm.bean.ContentInfo;
import com.openkm.bean.Repository;
import com.openkm.core.*;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.UserItemsDAO;
import com.openkm.dao.bean.cache.UserItems;
import com.openkm.module.db.base.BaseFolderModule;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.spring.PrincipalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

public class UserItemsManager {
	private static Logger log = LoggerFactory.getLogger(UserItemsManager.class);
	private static Map<String, UserItems> userItemsMgr = new HashMap<String, UserItems>();
	private static volatile boolean running = false;

	/**
	 * Get stored user item
	 */
	public static UserItems get(String uid) {
		UserItems userItems = userItemsMgr.get(uid);

		if (userItems == null) {
			userItems = new UserItems();
			userItems.setUser(uid);
			userItemsMgr.put(uid, userItems);
		}

		return userItems;
	}

	/**
	 * Increment document number
	 */
	public static synchronized void incDocuments(String uid, int value) {
		log.debug("incDocuments({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setDocuments(userItems.getDocuments() + value);
	}

	/**
	 * Decrement document number
	 */
	public static synchronized void decDocuments(String uid, int value) {
		log.debug("decDocuments({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setDocuments(userItems.getDocuments() - value);
	}

	/**
	 * Increment folder number
	 */
	public static synchronized void incFolders(String uid, int value) {
		log.debug("incFolders({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setFolders(userItems.getFolders() + value);
	}

	/**
	 * Decrement folder number
	 */
	public static synchronized void decFolders(String uid, int value) {
		log.debug("decFolders({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setFolders(userItems.getFolders() - value);
	}

	/**
	 * Increment document size
	 */
	public static synchronized void incSize(String uid, long value) {
		log.debug("incSize({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setSize(userItems.getSize() + value);
	}

	/**
	 * Decrement document size
	 */
	public static synchronized void decSize(String uid, long value) {
		log.debug("decSize({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setSize(userItems.getSize() - value);
	}

	/**
	 * Refresh user item cache from database.
	 */
	public static synchronized void refreshDbUserItems() throws AccessDeniedException, RepositoryException {
		String systemToken = null;

		if (Config.REPOSITORY_NATIVE) {
			systemToken = DbSessionManager.getInstance().getSystemToken();
		} else {
			// Other implementation
		}

		refreshDbUserItemsAs(systemToken);
	}

	/**
	 * Refresh user item cache from database.
	 */
	public static synchronized void refreshDbUserItemsAs(String token) throws AccessDeniedException, RepositoryException {
		log.debug("refreshDbUserItemsAs({})", token);
		Map<String, ContentInfo> totalUserContInfo = new HashMap<String, ContentInfo>();
		String[] bases = new String[]{Repository.ROOT, Repository.CATEGORIES, Repository.TEMPLATES,
				Repository.PERSONAL, Repository.MAIL, Repository.TRASH};
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		if (running) {
			log.warn("*** Refresh user items already running ***");
		} else {
			running = true;
			log.info("*** Begin refresh user items ***");

			try {
				if (token == null) {
					auth = PrincipalUtils.getAuthentication();
				} else {
					oldAuth = PrincipalUtils.getAuthentication();
					auth = PrincipalUtils.getAuthenticationByToken(token);
				}

				for (String base : bases) {
					log.info("Calculate user content info from '{}'...", base);
					String uuid = NodeBaseDAO.getInstance().getUuidFromPath("/" + base);
					Map<String, ContentInfo> userContInfo = BaseFolderModule.getUserContentInfo(uuid);

					for (String user : userContInfo.keySet()) {
						ContentInfo usrTotContInfo = totalUserContInfo.get(user);
						ContentInfo usrContInfo = userContInfo.get(user);

						if (usrTotContInfo == null) {
							usrTotContInfo = new ContentInfo();
						}

						usrTotContInfo.setDocuments(usrTotContInfo.getDocuments() + usrContInfo.getDocuments());
						usrTotContInfo.setFolders(usrTotContInfo.getFolders() + usrContInfo.getFolders());
						usrTotContInfo.setMails(usrTotContInfo.getMails() + usrContInfo.getMails());
						usrTotContInfo.setSize(usrTotContInfo.getSize() + usrContInfo.getSize());

						totalUserContInfo.put(user, usrTotContInfo);
					}
				}

				for (String user : totalUserContInfo.keySet()) {
					ContentInfo contInfo = totalUserContInfo.get(user);
					UserItems userItems = new UserItems();
					userItems.setDocuments(contInfo.getDocuments());
					userItems.setFolders(contInfo.getFolders());
					userItems.setSize(contInfo.getSize());
					userItems.setUser(user);
					userItemsMgr.put(user, userItems);
				}
			} catch (PathNotFoundException e) {
				throw new RepositoryException("PathNotFoundException: " + e, e);
			} catch (DatabaseException e) {
				throw new RepositoryException("DatabaseException: " + e, e);
			} finally {
				if (token != null) {
					PrincipalUtils.setAuthentication(oldAuth);
				}

				running = false;
			}

			log.info("*** End refresh user items ***");
		}

		log.debug("refreshDbUserItemsAs: void");
	}

	/**
	 * Store data in database
	 */
	public static synchronized void serialize() throws DatabaseException {
		for (String user : userItemsMgr.keySet()) {
			UserItemsDAO.update(userItemsMgr.get(user));
		}
	}

	/**
	 * Read data from database
	 */
	public static synchronized void deserialize() throws DatabaseException {
		for (UserItems ui : UserItemsDAO.findAll()) {
			userItemsMgr.put(ui.getUser(), ui);
		}
	}
}
