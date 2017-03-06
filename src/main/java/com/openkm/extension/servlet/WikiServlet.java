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

package com.openkm.extension.servlet;

import com.openkm.core.DatabaseException;
import com.openkm.extension.dao.WikiPageDAO;
import com.openkm.extension.dao.bean.WikiPage;
import com.openkm.extension.frontend.client.service.OKMWikiService;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTWikiPage;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * WikiServlet
 */
public class WikiServlet extends OKMRemoteServiceServlet implements OKMWikiService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(WikiServlet.class);

	@Override
	public GWTWikiPage findLatestByNode(String uuid) throws OKMException {
		log.debug("findLatestByNode({})", uuid);
		updateSessionManager();
		GWTWikiPage gWTWikiPage = null;

		try {
			WikiPage wikiPage = WikiPageDAO.findLatestByNode(uuid);

			if (wikiPage != null) {
				gWTWikiPage = GWTUtil.copy(wikiPage);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		return gWTWikiPage;
	}

	@Override
	public GWTWikiPage findLatestByTitle(String title) throws OKMException {
		log.debug("findLatestByTitle({})", title);
		updateSessionManager();
		GWTWikiPage gWTWikiPage = null;

		try {
			WikiPage wikiPage = WikiPageDAO.findLatestByTitle(title);

			if (wikiPage != null) {
				gWTWikiPage = GWTUtil.copy(wikiPage);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		return gWTWikiPage;
	}

	@Override
	public GWTWikiPage createNewWikiPage(GWTWikiPage wikiPage) throws OKMException {
		log.debug("createNewWikiPage({})", wikiPage);

		try {
			// Fix XSS issues
			wikiPage.setTitle(Encode.forHtml(wikiPage.getTitle()));
			wikiPage.setContent(Encode.forHtml(wikiPage.getContent()));

			return GWTUtil.copy(WikiPageDAO.createNewWikiPage(GWTUtil.copy(wikiPage)));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public GWTWikiPage updateWikiPage(GWTWikiPage wikiPage) throws OKMException {
		log.debug("updateWikiPage({})", wikiPage);

		try {
			// Fix XSS issues
			wikiPage.setTitle(Encode.forHtml(wikiPage.getTitle()));
			wikiPage.setContent(Encode.forHtml(wikiPage.getContent()));

			WikiPage updatedWikiPage = WikiPageDAO.updateWikiPage(GWTUtil.copy(wikiPage));

			if (updatedWikiPage == null) {
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), "Not possible doing update");
			} else {
				return GWTUtil.copy(updatedWikiPage);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public GWTWikiPage restoreWikiPage(GWTWikiPage wikiPage) throws OKMException {
		log.debug("restoreWikiPage({})", wikiPage);

		try {
			WikiPage restoredWikiPage = WikiPageDAO.restoreWikiPage(GWTUtil.copy(wikiPage));

			if (restoredWikiPage == null) {
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), "Not possible doing update");
			} else {
				return GWTUtil.copy(restoredWikiPage);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public List<GWTWikiPage> findAllByTitle(String title) throws OKMException {
		List<GWTWikiPage> wikiPages = new ArrayList<GWTWikiPage>();

		try {
			for (WikiPage wikiPage : WikiPageDAO.findAllByTitle(title)) {
				wikiPages.add(GWTUtil.copy(wikiPage));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		return wikiPages;
	}

	@Override
	public void deleteWikiPage(GWTWikiPage wikiPage) throws OKMException {
		log.debug("deleteWikiPage({})", wikiPage);

		try {
			if (!WikiPageDAO.deleteWikiPage(GWTUtil.copy(wikiPage))) {
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), "Not possible doing delete");
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}

	}

	@Override
	public void lock(GWTWikiPage wikiPage) throws OKMException {
		log.debug("lock({})", wikiPage);

		try {
			if (!WikiPageDAO.lock(GWTUtil.copy(wikiPage), getThreadLocalRequest().getRemoteUser())) {
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), "Not possible doing lock");
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void unlock(GWTWikiPage wikiPage) throws OKMException {
		log.debug("unlock({})", wikiPage);

		try {
			if (!WikiPageDAO.unlock(GWTUtil.copy(wikiPage), getThreadLocalRequest().getRemoteUser())) {
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), "Not possible doing unlock");
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public List<String> findAllLatestByTitleFiltered(String filter) throws OKMException {
		log.debug("findAllLatestByTitleFiltered({})", filter);

		try {
			return WikiPageDAO.findAllLatestByTitleFiltered(filter);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public List<GWTWikiPage> findAllHistoricByTitle(String title) throws OKMException {
		List<GWTWikiPage> wikiPages = new ArrayList<GWTWikiPage>();

		try {
			for (WikiPage wikiPage : WikiPageDAO.findAllHistoricByTitle(title)) {
				wikiPages.add(GWTUtil.copy(wikiPage));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWikiService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		return wikiPages;
	}
}