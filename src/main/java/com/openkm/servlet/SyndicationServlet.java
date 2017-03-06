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

package com.openkm.servlet;

import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.QueryParamsDAO;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.db.DbDashboardModule;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Syndication Servlet
 */
public class SyndicationServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(SyndicationServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String FEED_TYPE = "rss_2.0"; // atom_0.3

	/**
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String action = request.getPathInfo();
		String userId = request.getRemoteUser();
		log.debug("action: {}", action);

		try {
			SyndFeed feed = null;

			if ("/userLockedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getUserLockedDocumentsSrv(userId));
				feed.setTitle("OpenKM: user locked documents");
			} else if ("/userCheckedOutDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getUserCheckedOutDocumentsSrv(userId));
				feed.setTitle("OpenKM: user checked-out documents");
			} else if ("/userSubscribedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getUserSubscribedDocumentsSrv(userId));
				feed.setTitle("OpenKM: user subscribed documents");
			} else if ("/userSubscribedFolders".equals(action)) {
				feed = getFeedFolders(new DbDashboardModule().getUserSubscribedFoldersSrv(userId));
				feed.setTitle("OpenKM: user subscribed folders");
			} else if ("/userLastUploadedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getUserLastUploadedDocumentsSrv(userId));
				feed.setTitle("OpenKM: user last uploaded documents");
			} else if ("/userLastModifiedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getUserLastModifiedDocumentsSrv(userId));
				feed.setTitle("OpenKM: user last modified documents");
			} else if ("/userLastDownloadedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getUserLastDownloadedDocumentsSrv(userId));
				feed.setTitle("OpenKM: user last downloaded documents");
			} else if ("/userLastImportedMails".equals(action)) {
				feed = getFeedMails(new DbDashboardModule().getUserLastImportedMailsSrv(userId));
				feed.setTitle("OpenKM: user last imported mails");
			} else if ("/userLastImportedMailAttachments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getUserLastImportedMailAttachmentsSrv(userId));
				feed.setTitle("OpenKM: user last imported mail attachments");
			} else if ("/lastWeekTopDownloadedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getLastWeekTopDownloadedDocumentsSrv(userId));
				feed.setTitle("OpenKM: last week top downloaded documents");
			} else if ("/lastMonthTopDownloadedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getLastMonthTopDownloadedDocumentsSrv(userId));
				feed.setTitle("OpenKM: last month top downloaded documents");
			} else if ("/lastWeekTopModifiedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getLastWeekTopModifiedDocumentsSrv(userId));
				feed.setTitle("OpenKM: last week top modified documents");
			} else if ("/lastMonthTopModifiedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getLastMonthTopModifiedDocumentsSrv(userId));
				feed.setTitle("OpenKM: last month top modified documents");
			} else if ("/lastModifiedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getLastModifiedDocumentsSrv(userId));
				feed.setTitle("OpenKM: last modified documents");
			} else if ("/lastUploadedDocuments".equals(action)) {
				feed = getFeedDocuments(new DbDashboardModule().getLastUploadedDocumentsSrv(userId));
				feed.setTitle("OpenKM: last uploaded documents");
			} else if (action != null && action.startsWith("/news_")) {
				String qpStr = action.substring(6);
				int qpId = Integer.parseInt(qpStr);
				QueryParams qp = QueryParamsDAO.findByPk(qpId);
				feed = getFeedDocuments(new DbDashboardModule().findSrv(userId, qpId));
				feed.setTitle("OpenKM: " + qp.getQueryName());
			}

			if (feed != null) {
				response.setContentType("application/xml; charset=UTF-8");
				SyndFeedOutput output = new SyndFeedOutput();
				SyndImage img = new SyndImageImpl();
				img.setTitle(feed.getTitle());
				img.setUrl(Config.APPLICATION_BASE + "/img/logo_short.gif");
				img.setLink(Config.APPLICATION_BASE);
				feed.setImage(img);
				feed.setLanguage("en");
				feed.setFeedType(FEED_TYPE);
				feed.setDescription(feed.getTitle());
				feed.setLink(Config.APPLICATION_BASE);
				output.output(feed, response.getWriter());
			} else {
				response.setContentType("text/plain; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("Unknown syndicantion feed");
				out.close();
			}
		} catch (DatabaseException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (FeedException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * Get feed documents
	 */
	private SyndFeed getFeedDocuments(List<DashboardDocumentResult> result) throws FeedException, SQLException,
			IOException {
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		SyndFeed feed = new SyndFeedImpl();

		for (DashboardDocumentResult item : result) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(item.getDocument().getPath());
			entry.setAuthor(item.getDocument().getActualVersion().getAuthor());
			entry.setPublishedDate(item.getDate().getTime());
			entry.setLink(Config.APPLICATION_URL + "?docPath="
					+ URLEncoder.encode(item.getDocument().getPath(), "UTF-8"));
			entries.add(entry);
		}

		feed.setEntries(entries);
		return feed;
	}

	/**
	 * Get feed folders
	 */
	private SyndFeed getFeedFolders(List<DashboardFolderResult> result) throws FeedException, SQLException, IOException {
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		SyndFeed feed = new SyndFeedImpl();

		for (DashboardFolderResult item : result) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(item.getFolder().getPath());
			entry.setAuthor(item.getFolder().getAuthor());
			entry.setPublishedDate(item.getDate().getTime());
			entry.setLink(Config.APPLICATION_URL + "?fldPath=" + URLEncoder.encode(item.getFolder().getPath(), "UTF-8"));
			entries.add(entry);
		}

		feed.setEntries(entries);
		return feed;
	}

	/**
	 * Get feed mails
	 */
	private SyndFeed getFeedMails(List<DashboardMailResult> result) throws FeedException, SQLException, IOException {
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		SyndFeed feed = new SyndFeedImpl();

		for (DashboardMailResult item : result) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(item.getMail().getPath());
			entry.setAuthor(item.getMail().getFrom());
			entry.setPublishedDate(item.getDate().getTime());
			entry.setLink(Config.APPLICATION_URL + "?docPath=" + URLEncoder.encode(item.getMail().getPath(), "UTF-8"));
			entries.add(entry);
		}

		feed.setEntries(entries);
		return feed;
	}
}
