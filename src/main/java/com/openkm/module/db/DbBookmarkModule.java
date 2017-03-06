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

package com.openkm.module.db;

import com.openkm.core.*;
import com.openkm.dao.BookmarkDAO;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.Bookmark;
import com.openkm.module.BookmarkModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.FormatUtil;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

public class DbBookmarkModule implements BookmarkModule {
	private static Logger log = LoggerFactory.getLogger(DbBookmarkModule.class);

	@Override
	public Bookmark add(String token, String nodePath, String name) throws AccessDeniedException,
			PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("add({}, {}, {})", new Object[]{token, nodePath, name});
		Bookmark newBookmark = null;
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

			String nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			String nodeType = NodeBaseDAO.getInstance().getNodeTypeByUuid(nodeUuid);
			name = FormatUtil.sanitizeInput(name);
			newBookmark = new Bookmark();
			newBookmark.setUser(auth.getName());
			newBookmark.setName(name);
			newBookmark.setNode(nodeUuid);
			newBookmark.setType(nodeType);
			BookmarkDAO.create(newBookmark);

			// Activity log
			UserActivity.log(auth.getName(), "BOOKMARK_ADD", nodeUuid, nodePath, name);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("add: {}", newBookmark);
		return newBookmark;
	}

	@Override
	public Bookmark get(String token, int bmId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("get({}, {})", token, bmId);
		Bookmark bookmark = null;
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

			bookmark = BookmarkDAO.findByPk(bmId);

			// Activity log
			UserActivity.log(auth.getName(), "BOOKMARK_GET", Integer.toString(bmId), null, bookmark.toString());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("get: {}", bookmark);
		return bookmark;
	}

	@Override
	public void remove(String token, int bmId) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("remove({}, {})", token, bmId);
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

			BookmarkDAO.delete(bmId);

			// Activity log
			UserActivity.log(auth.getName(), "BOOKMARK_REMOVE", Integer.toString(bmId), null, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("remove: void");
	}

	@Override
	public Bookmark rename(String token, int bmId, String newName) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[]{token, bmId, newName});
		Bookmark renamedBookmark = null;
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

			newName = FormatUtil.sanitizeInput(newName);
			Bookmark bm = BookmarkDAO.findByPk(bmId);
			bm.setName(newName);
			BookmarkDAO.update(bm);
			renamedBookmark = BookmarkDAO.findByPk(bmId);

			// Activity log
			UserActivity.log(auth.getName(), "BOOKMARK_RENAME", Integer.toString(bmId), null, newName);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("rename: {}", renamedBookmark);
		return renamedBookmark;
	}

	@Override
	public List<Bookmark> getAll(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getAll({})", token);
		List<Bookmark> ret = new ArrayList<Bookmark>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			ret = BookmarkDAO.findByUser(auth.getName());

			// Activity log
			UserActivity.log(auth.getName(), "BOOKMARK_GET_ALL", null, null, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getAll: {}", ret);
		return ret;
	}
}
