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

package com.openkm.servlet.admin;

import com.openkm.core.Config;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.extractor.TextExtractorWorker;
import com.openkm.util.FileLogger;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.hibernate.*;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Rebuild Lucene indexes
 */
public class RebuildIndexesServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RebuildIndexesServlet.class);
	private static final String BASE_NAME = RebuildIndexesServlet.class.getSimpleName();
	private static volatile boolean optimizeIndexesRunning = false;
	private static final String[][] breadcrumb = new String[][]{
			new String[]{"utilities.jsp", "Utilities"},
			new String[]{"rebuild_indexes.jsp", "Rebuild indexes"}
	};

	@SuppressWarnings("rawtypes")
	Class[] classes = new Class[]{NodeDocument.class, NodeFolder.class, NodeMail.class};

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String method = request.getMethod();

		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		if ("textExtractor".equals(action)) {
			textExtractor(request, response);
		} else if ("luceneIndexes".equals(action)) {
			luceneIndexes(request, response);
		} else if ("optimizeIndexes".equals(action)) {
			optimizeIndexes(request, response);
		} else {
			ServletContext sc = getServletContext();
			sc.getRequestDispatcher("/admin/rebuild_indexes.jsp").forward(request, response);
		}
	}

	/**
	 * Force text extraction
	 */
	private void textExtractor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Rebuild text extraction", breadcrumb);
		out.flush();

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_FORCE_TEXT_EXTRACTOR", null, null, null);

		try {
			Config.SYSTEM_MAINTENANCE = true;
			Config.SYSTEM_READONLY = true;
			out.println("<ul>");
			out.println("<li>System into maintenance mode</li>");
			FileLogger.info(BASE_NAME, "BEGIN - Rebuild text extraction");

			// Calculate number of documents
			out.println("<li>Calculate documents</li>");
			out.flush();
			String nodeType = NodeDocument.class.getSimpleName();
			long total = NodeBaseDAO.getInstance().getCount(nodeType);
			out.println("<li>Number of documents: " + total + "</li>");
			out.flush();

			// Rebuild indexes
			out.println("<li>Rebuilding text extractions</li>");
			ProgressMonitor monitor = new ProgressMonitor(out, "NodeDocument", total);
			new TextExtractorWorker().rebuildWorker(monitor);

			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
			out.println("<li>System out of maintenance mode</li>");
			out.flush();

			// Finalized
			FileLogger.info(BASE_NAME, "END - Rebuild text extraction");
			out.println("<li>Index rebuilding completed!</li>");
			out.println("</ul>");
			out.flush();
		} catch (Exception e) {
			out.println("<div class=\"warn\">Exception: " + e.getMessage() + "</div>");
			out.flush();
		} finally {
			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
		}

		// End page
		footer(out);
		out.flush();
		out.close();
	}

	/**
	 * Perform index rebuild
	 *
	 * @see http://docs.jboss.org/hibernate/search/3.4/reference/en-US/html/manual-index-changes.html
	 * @see http://in.relation.to/Bloggers/HibernateSearch32FastIndexRebuild
	 */
	private void luceneIndexes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (Config.HIBERNATE_INDEXER_MASS_INDEXER) {
			luceneIndexesMassIndexer(request, response);
		} else {
			luceneIndexesFlushToIndexes(request, response);
		}
	}

	/**
	 * FlushToIndexes implementation
	 */
	@SuppressWarnings("rawtypes")
	private void luceneIndexesFlushToIndexes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("luceneIndexesFlushToIndexes({}, {})", request, response);
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Rebuild Lucene indexes", breadcrumb);
		out.flush();

		FullTextSession ftSession = null;
		Session session = null;
		Transaction tx = null;

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_FORCE_REBUILD_INDEXES", null, null, null);

		try {
			Config.SYSTEM_MAINTENANCE = true;
			Config.SYSTEM_READONLY = true;
			out.println("<ul>");
			out.println("<li>System into maintenance mode</li>");
			FileLogger.info(BASE_NAME, "BEGIN - Rebuild Lucene indexes");

			session = HibernateUtil.getSessionFactory().openSession();
			ftSession = Search.getFullTextSession(session);
			ftSession.setFlushMode(FlushMode.MANUAL);
			ftSession.setCacheMode(CacheMode.IGNORE);
			tx = ftSession.beginTransaction();
			Map<String, Long> total = new HashMap<String, Long>();

			// Calculate number of entities
			for (Class cls : classes) {
				String nodeType = cls.getSimpleName();
				out.println("<li>Calculate " + nodeType + "</li>");
				out.flush();
				long partial = NodeBaseDAO.getInstance().getCount(nodeType);
				FileLogger.info(BASE_NAME, "Number of {0}: {1}", nodeType, partial);
				out.println("<li>Number of " + nodeType + ": " + partial + "</li>");
				out.flush();
				total.put(nodeType, partial);
			}

			// Rebuild indexes
			out.println("<li>Rebuilding indexes</li>");
			out.flush();

			// Scrollable results will avoid loading too many objects in memory
			for (Class cls : classes) {
				String nodeType = cls.getSimpleName();
				out.println("<li>Indexing " + nodeType + "</li>");
				out.flush();

				ProgressMonitor monitor = new ProgressMonitor(out, nodeType, total.get(nodeType));
				ScrollableResults results = ftSession.createCriteria(cls)
						.setFetchSize(Config.HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS)
						.scroll(ScrollMode.FORWARD_ONLY);
				int index = 0;

				while (results.next()) {
					monitor.documentsAdded(1);
					ftSession.index(results.get(0)); // Index each element

					if (index++ % Config.HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS == 0) {
						ftSession.flushToIndexes(); // Apply changes to indexes
						ftSession.clear(); // Free memory since the queue is processed
					}
				}
			}

			tx.commit();

			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
			out.println("<li>System out of maintenance mode</li>");
			out.flush();

			// Finalized
			FileLogger.info(BASE_NAME, "END - Rebuild Lucene indexes");
			out.println("<li>Index rebuilding completed!</li>");
			out.println("</ul>");
			out.flush();
		} catch (Exception e) {
			tx.rollback();
			out.println("<div class=\"warn\">Exception: " + e.getMessage() + "</div>");
			out.flush();
		} finally {
			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
			HibernateUtil.close(ftSession);
			HibernateUtil.close(session);
		}

		// End page
		footer(out);
		out.flush();
		out.close();

		log.debug("luceneIndexesFlushToIndexes: void");
	}

	/**
	 * MassIndexer implementation.
	 */
	@SuppressWarnings("rawtypes")
	private void luceneIndexesMassIndexer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("luceneIndexesMassIndexer({}, {})", request, response);
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Rebuild Lucene indexes", breadcrumb);
		out.flush();

		FullTextSession ftSession = null;
		Session session = null;

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_FORCE_REBUILD_INDEXES", null, null, null);

		try {
			Config.SYSTEM_MAINTENANCE = true;
			Config.SYSTEM_READONLY = true;
			out.println("<ul>");
			out.println("<li>System into maintenance mode</li>");
			FileLogger.info(BASE_NAME, "BEGIN - Rebuild Lucene indexes");

			session = HibernateUtil.getSessionFactory().openSession();
			ftSession = Search.getFullTextSession(session);
			long total = 0;

			// Calculate number of entities
			for (Class cls : classes) {
				String nodeType = cls.getSimpleName();
				out.println("<li>Calculate " + nodeType + "</li>");
				out.flush();
				long partial = NodeBaseDAO.getInstance().getCount(nodeType);
				FileLogger.info(BASE_NAME, "Number of {0}: {1}", nodeType, partial);
				out.println("<li>Number of " + nodeType + ": " + partial + "</li>");
				out.flush();
				total += partial;
			}

			// Rebuild indexes
			out.println("<li>Rebuilding indexes</li>");
			out.flush();
			ProgressMonitor monitor = new ProgressMonitor(out, "NodeBase", (int) total);
			ftSession.createIndexer()
					.batchSizeToLoadObjects(Config.HIBERNATE_INDEXER_BATCH_SIZE_LOAD_OBJECTS)
					.threadsForSubsequentFetching(Config.HIBERNATE_INDEXER_THREADS_SUBSEQUENT_FETCHING)
					.threadsToLoadObjects(Config.HIBERNATE_INDEXER_THREADS_LOAD_OBJECTS)
					.threadsForIndexWriter(Config.HIBERNATE_INDEXER_THREADS_INDEX_WRITER)
					.cacheMode(CacheMode.NORMAL) // defaults to CacheMode.IGNORE
					.progressMonitor(monitor).startAndWait();

			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
			out.println("<li>System out of maintenance mode</li>");
			out.flush();

			// Finalized
			FileLogger.info(BASE_NAME, "END - Rebuild Lucene indexes");
			out.println("<li>Index rebuilding completed!</li>");
			out.println("</ul>");
			out.flush();
		} catch (Exception e) {
			out.println("<div class=\"warn\">Exception: " + e.getMessage() + "</div>");
			out.flush();
		} finally {
			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
			HibernateUtil.close(ftSession);
			HibernateUtil.close(session);
		}

		// End page
		footer(out);
		out.flush();
		out.close();

		log.debug("luceneIndexesMassIndexer: void");
	}

	/**
	 * Perform index optimization
	 */
	private void optimizeIndexes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Optimize Lucene indexes", breadcrumb);
		out.flush();

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_FORCE_OPTIMIZE_INDEXES", null, null, null);

		try {
			Config.SYSTEM_MAINTENANCE = true;
			Config.SYSTEM_READONLY = true;
			out.println("<ul>");
			out.println("<li>System into maintenance mode</li>");
			FileLogger.info(BASE_NAME, "BEGIN - Indexes optimization");

			// Optimize indexes
			out.println("<li>Optimize indexes</li>");
			out.flush();
			optimizeIndexes();

			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
			out.println("<li>System out of maintenance mode</li>");
			out.flush();

			// Finalized
			FileLogger.info(BASE_NAME, "END - Indexes optimization");
			out.println("<li>Index optimization completed!</li>");
			out.println("</ul>");
			out.flush();
		} catch (Exception e) {
			out.println("<div class=\"warn\">Exception: " + e.getMessage() + "</div>");
			out.flush();
		} finally {
			Config.SYSTEM_READONLY = false;
			Config.SYSTEM_MAINTENANCE = false;
		}

		// End page
		footer(out);
		out.flush();
		out.close();
	}

	/**
	 * Do real indexes optimization.
	 */
	public static void optimizeIndexes() throws Exception {
		FullTextSession ftSession = null;
		Session session = null;

		if (optimizeIndexesRunning) {
			log.warn("*** Optimize indexes already running ***");
		} else {
			optimizeIndexesRunning = true;
			log.debug("*** Begin optimize indexes ***");

			try {
				session = HibernateUtil.getSessionFactory().openSession();
				ftSession = Search.getFullTextSession(session);

				// Optimize indexes
				SearchFactory searchFactory = ftSession.getSearchFactory();
				searchFactory.optimize();
			} catch (Exception e) {
				throw e;
			} finally {
				optimizeIndexesRunning = false;
				HibernateUtil.close(ftSession);
				HibernateUtil.close(session);
			}

			log.debug("*** End optimize indexes ***");
		}
	}

	/**
	 * Indexer progress monitor
	 */
	class ProgressMonitor implements MassIndexerProgressMonitor {
		private PrintWriter pw = null;
		private long count = 0;
		private long total = 0;
		private long oldPerCent = -1;
		private long oldPerMile = -1;
		private String tag = null;

		public ProgressMonitor(PrintWriter out, String tag, long total) {
			log.debug("ProgressMonitor({}, {})", out, total);
			this.total = total;
			this.tag = tag;
			this.pw = out;
		}

		@Override
		public void documentsAdded(long size) {
			log.debug("documentsAdded({})", size);
			count += size;
			long perCent = count * 100 / total;

			if (perCent > oldPerCent) {
				pw.print(" (");
				pw.print(perCent);
				pw.print("%)");
				oldPerCent = perCent;
			}

			pw.flush();

			try {
				long perMile = count * 1000 / total;

				if (perMile > oldPerMile) {
					FileLogger.info(BASE_NAME, "{0} progress {1}%%", tag, perMile);
					oldPerMile = perMile;
				}
			} catch (IOException e) {
				log.warn("IOException at FileLogger: " + e.getMessage());
			}
		}

		@Override
		public void documentsBuilt(int number) {
		}

		@Override
		public void entitiesLoaded(int size) {
		}

		@Override
		public void addToTotalCount(long count) {
		}

		@Override
		public void indexingCompleted() {
		}
	}
}
