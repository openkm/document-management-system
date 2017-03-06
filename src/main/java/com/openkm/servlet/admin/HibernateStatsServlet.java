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

package com.openkm.servlet.admin;

import com.openkm.dao.HibernateUtil;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.hibernate.stat.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.Collator;
import java.util.*;

/**
 * Hibernate statistics servlet
 */
public class HibernateStatsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(HibernateStatsServlet.class);
	static Map<String, QueryStatistics> queryStatistics = Collections.synchronizedMap(new TreeMap<String, QueryStatistics>(Collator.getInstance()));
	static Map<String, EntityStatistics> entityStatistics = Collections.synchronizedMap(new TreeMap<String, EntityStatistics>(Collator.getInstance()));
	static Map<String, CollectionStatistics> collectionStatistics = Collections.synchronizedMap(new TreeMap<String, CollectionStatistics>(Collator.getInstance()));
	static Map<String, SecondLevelCacheStatistics> secondLevelCacheStatistics = Collections.synchronizedMap(new TreeMap<String, SecondLevelCacheStatistics>(Collator.getInstance()));
	static List<Long> generalStatistics = Collections.synchronizedList(new ArrayList<Long>(18));

	static {
		for (int i = 0; i < 9; i++) {
			generalStatistics.add(new Long(-1));
		}
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("activate")) {
				activate(request, response);
			} else if (action.equals("deactivate")) {
				deactivate(request, response);
			} else if (action.equals("clear")) {
				clear(request, response);
			}

			view(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Activate stats
	 */
	private void activate(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		Statistics stats = HibernateUtil.getSessionFactory().getStatistics();

		if (!stats.isStatisticsEnabled()) {
			stats.setStatisticsEnabled(true);
		}
	}

	/**
	 * Deactivate stats
	 */
	private void deactivate(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		Statistics stats = HibernateUtil.getSessionFactory().getStatistics();

		if (stats.isStatisticsEnabled()) {
			stats.setStatisticsEnabled(false);
		}
	}

	/**
	 * Clear stats
	 */
	private void clear(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		HibernateUtil.getSessionFactory().getStatistics().clear();
		generalStatistics.set(0, new Long(0));
		generalStatistics.set(1, new Long(0));
		generalStatistics.set(2, new Long(0));
		generalStatistics.set(3, new Long(0));
		generalStatistics.set(4, new Long(0));
		generalStatistics.set(5, new Long(0));
		generalStatistics.set(6, new Long(0));
		generalStatistics.set(7, new Long(0));
		generalStatistics.set(8, new Long(0));
		queryStatistics.clear();
		entityStatistics.clear();
		collectionStatistics.clear();
		secondLevelCacheStatistics.clear();
	}

	/**
	 * View log
	 */
	private void view(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("view({}, {})", request, response);
		refresh();

		// Query Statistics
		List<Map<String, String>> qStats = new ArrayList<Map<String, String>>();
		for (String query : queryStatistics.keySet()) {
			QueryStatistics queryStats = queryStatistics.get(query);
			Map<String, String> stat = new HashMap<String, String>();
			stat.put("query", query);
			//stat.put("tquery", HibernateUtil.toSql(query));
			stat.put("executionCount", Long.toString(queryStats.getExecutionCount()));
			stat.put("executionRowCount", Long.toString(queryStats.getExecutionRowCount()));
			stat.put("executionMaxTime", Long.toString(queryStats.getExecutionMaxTime()));
			stat.put("executionMinTime", Long.toString(queryStats.getExecutionMinTime()));
			stat.put("executionAvgTime", Long.toString(queryStats.getExecutionAvgTime()));
			stat.put("executionTotalTime", Long.toString(queryStats.getExecutionAvgTime() * queryStats.getExecutionCount()));
			stat.put("cacheHitCount", Long.toString(queryStats.getCacheHitCount()));
			stat.put("cacheMissCount", Long.toString(queryStats.getCacheMissCount()));
			stat.put("cachePutCount", Long.toString(queryStats.getCachePutCount()));
			qStats.add(stat);
		}

		// Entity Statistics
		List<Map<String, String>> eStats = new ArrayList<Map<String, String>>();
		for (String entity : entityStatistics.keySet()) {
			EntityStatistics entityStats = entityStatistics.get(entity);
			Map<String, String> stat = new HashMap<String, String>();
			stat.put("entity", entity);
			stat.put("loadCount", Long.toString(entityStats.getLoadCount()));
			stat.put("fetchCount", Long.toString(entityStats.getFetchCount()));
			stat.put("insertCount", Long.toString(entityStats.getInsertCount()));
			stat.put("updateCount", Long.toString(entityStats.getUpdateCount()));
			stat.put("deleteCount", Long.toString(entityStats.getDeleteCount()));
			stat.put("optimisticFailureCount", Long.toString(entityStats.getOptimisticFailureCount()));
			eStats.add(stat);
		}

		// Collection Statistics
		List<Map<String, String>> cStats = new ArrayList<Map<String, String>>();
		for (String collection : collectionStatistics.keySet()) {
			CollectionStatistics collectionStats = collectionStatistics.get(collection);
			Map<String, String> stat = new HashMap<String, String>();
			stat.put("collection", collection);
			stat.put("loadCount", Long.toString(collectionStats.getLoadCount()));
			stat.put("fetchCount", Long.toString(collectionStats.getFetchCount()));
			stat.put("updateCount", Long.toString(collectionStats.getUpdateCount()));
			stat.put("recreateCount", Long.toString(collectionStats.getRecreateCount()));
			stat.put("removeCount", Long.toString(collectionStats.getRemoveCount()));
			cStats.add(stat);
		}

		// 2nd Level Cache Statistics
		long totalSizeInMemory = 0;
		List<Map<String, String>> slcStats = new ArrayList<Map<String, String>>();
		for (String cache : secondLevelCacheStatistics.keySet()) {
			SecondLevelCacheStatistics cacheStats = secondLevelCacheStatistics.get(cache);
			totalSizeInMemory += cacheStats.getSizeInMemory();
			Map<String, String> stat = new HashMap<String, String>();
			stat.put("cache", cache);
			stat.put("putCount", Long.toString(cacheStats.getPutCount()));
			stat.put("hitCount", Long.toString(cacheStats.getHitCount()));
			stat.put("missCount", Long.toString(cacheStats.getMissCount()));
			stat.put("elementCountInMemory", Long.toString(cacheStats.getElementCountInMemory()));
			stat.put("sizeInMemory", Long.toString(cacheStats.getSizeInMemory()));
			stat.put("elementCountOnDisk", Long.toString(cacheStats.getElementCountOnDisk()));
			slcStats.add(stat);
		}

		ServletContext sc = getServletContext();
		sc.setAttribute("generalStats", generalStatistics);
		sc.setAttribute("queryStats", qStats);
		sc.setAttribute("entityStats", eStats);
		sc.setAttribute("collectionStats", cStats);
		sc.setAttribute("secondLevelCacheStats", slcStats);
		sc.setAttribute("totalSizeInMemory", totalSizeInMemory);
		sc.setAttribute("statsEnabled", HibernateUtil.getSessionFactory().getStatistics().isStatisticsEnabled());
		sc.getRequestDispatcher("/admin/hibernate_stats.jsp").forward(request, response);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_HIBERNATE_STATS", null, null, null);

		log.debug("view: void");
	}

	/**
	 * Refresh stats
	 */
	private synchronized void refresh() {
		Statistics statistics = HibernateUtil.getSessionFactory().getStatistics();
		generalStatistics.set(0, statistics.getConnectCount());
		generalStatistics.set(1, statistics.getFlushCount());
		generalStatistics.set(2, statistics.getPrepareStatementCount());
		generalStatistics.set(3, statistics.getCloseStatementCount());
		generalStatistics.set(4, statistics.getSessionCloseCount());
		generalStatistics.set(5, statistics.getSessionOpenCount());
		generalStatistics.set(6, statistics.getTransactionCount());
		generalStatistics.set(7, statistics.getSuccessfulTransactionCount());
		generalStatistics.set(8, statistics.getOptimisticFailureCount());
		queryStatistics.clear();
		String[] names = statistics.getQueries();

		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				queryStatistics.put(names[i], statistics.getQueryStatistics(names[i]));
			}
		}

		entityStatistics.clear();
		names = statistics.getEntityNames();

		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				entityStatistics.put(names[i], statistics.getEntityStatistics(names[i]));
			}
		}

		collectionStatistics.clear();
		names = statistics.getCollectionRoleNames();

		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				collectionStatistics.put(names[i], statistics.getCollectionStatistics(names[i]));
			}
		}

		secondLevelCacheStatistics.clear();
		names = statistics.getSecondLevelCacheRegionNames();

		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				secondLevelCacheStatistics.put(names[i], statistics.getSecondLevelCacheStatistics(names[i]));
			}
		}
	}
}
