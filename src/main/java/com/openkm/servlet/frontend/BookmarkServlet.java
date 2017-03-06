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

package com.openkm.servlet.frontend;

import com.openkm.api.OKMBookmark;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.Bookmark;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTBookmark;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMBookmarkService;
import com.openkm.servlet.frontend.util.BookmarkComparator;
import com.openkm.util.GWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Servlet Class
 */
public class BookmarkServlet extends OKMRemoteServiceServlet implements OKMBookmarkService {
	private static Logger log = LoggerFactory.getLogger(BookmarkServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public List<GWTBookmark> getAll() throws OKMException {
		log.debug("getAll()");
		List<GWTBookmark> bookmarkList = new ArrayList<GWTBookmark>();
		updateSessionManager();

		try {
			Collection<Bookmark> col = OKMBookmark.getInstance().getAll(null);

			for (Iterator<Bookmark> it = col.iterator(); it.hasNext(); ) {
				Bookmark bookmark = it.next();
				log.debug("Bookmark: {}", bookmark);
				String path = NodeBaseDAO.getInstance().getPathFromUuid(bookmark.getNode());
				GWTBookmark bookmarkClient = GWTUtil.copy(bookmark, path);
				bookmarkList.add(bookmarkClient);
			}

			Collections.sort(bookmarkList, BookmarkComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getAll: {}", bookmarkList);
		return bookmarkList;
	}

	@Override
	public GWTBookmark add(String nodePath, String name) throws OKMException {
		log.debug("add({}, {})", nodePath, name);
		updateSessionManager();

		try {
			Bookmark bookmark = OKMBookmark.getInstance().add(null, nodePath, name);
			String path = NodeBaseDAO.getInstance().getPathFromUuid(bookmark.getNode());
			return GWTUtil.copy(bookmark, path);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}

	@Override
	public void remove(int bmId) throws OKMException {
		log.debug("remove({})", bmId);
		updateSessionManager();

		try {
			OKMBookmark.getInstance().remove(null, bmId);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("remove: void");
	}

	@Override
	public GWTBookmark rename(int bmId, String newName) throws OKMException {
		log.debug("rename({}, {})", bmId, newName);
		updateSessionManager();

		try {
			Bookmark bookmark = OKMBookmark.getInstance().rename(null, bmId, newName);
			String path = NodeBaseDAO.getInstance().getPathFromUuid(bookmark.getNode());
			return GWTUtil.copy(bookmark, path);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}

	@Override
	public GWTBookmark get(int bmId) throws OKMException {
		log.debug("get({})", bmId);
		updateSessionManager();

		try {
			Bookmark bookmark = OKMBookmark.getInstance().get(null, bmId);
			String path = NodeBaseDAO.getInstance().getPathFromUuid(bookmark.getNode());
			return GWTUtil.copy(bookmark, path);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
}
