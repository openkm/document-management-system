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

import com.openkm.cache.CacheProvider;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Statistics;
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
 * Cache statistics servlet
 */
public class CacheStatsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(CacheStatsServlet.class);
	static Map<String, Statistics> cacheStatistics = Collections.synchronizedMap(new TreeMap<String, Statistics>(Collator.getInstance()));
	private boolean statsEnabled = false;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			} else if (action.equals("reset")) {
				reset(request, response);
			} else if (action.equals("resetAll")) {
				resetAll(request, response);
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
		for (String name : CacheProvider.getInstance().getOkmCacheNames()) {
			Cache cache = CacheProvider.getInstance().getCache(name);

			if (!cache.isStatisticsEnabled()) {
				cache.setStatisticsEnabled(true);
			}

			statsEnabled = true;
		}
	}

	/**
	 * Deactivate stats
	 */
	private void deactivate(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		for (String name : CacheProvider.getInstance().getOkmCacheNames()) {
			Cache cache = CacheProvider.getInstance().getCache(name);

			if (cache.isStatisticsEnabled()) {
				cache.setStatisticsEnabled(false);
			}

			statsEnabled = false;
		}
	}

	/**
	 * Clear stats
	 */
	private void clear(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		for (String name : CacheProvider.getInstance().getOkmCacheNames()) {
			Cache cache = CacheProvider.getInstance().getCache(name);
			cache.clearStatistics();
		}

		cacheStatistics.clear();
	}

	/**
	 * Remove all elements in cache
	 */
	private void reset(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		String name = WebUtils.getString(request, "name");

		if (name != null && !name.equals("")) {
			Cache cache = CacheProvider.getInstance().getCache(name);

			if (cache != null) {
				cache.removeAll();
			}
		}
	}

	/**
	 * Remove all elements in all caches
	 */
	private void resetAll(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		CacheProvider.getInstance().clearAll();
	}

	/**
	 * View log
	 */
	private void view(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("view({}, {})", request, response);
		refresh();

		List<Map<String, String>> cacheStats = new ArrayList<Map<String, String>>();

		for (String cache : cacheStatistics.keySet()) {
			Statistics stats = cacheStatistics.get(cache);

			Map<String, String> stat = new HashMap<String, String>();
			stat.put("cache", cache);
			stat.put("cacheHits", Long.toString(stats.getCacheHits()));
			stat.put("cacheMisses", Long.toString(stats.getCacheMisses()));
			stat.put("objectCount", Long.toString(stats.getObjectCount()));
			stat.put("inMemoryHits", Long.toString(stats.getInMemoryHits()));
			stat.put("inMemoryMisses", Long.toString(stats.getInMemoryMisses()));
			stat.put("memoryStoreObjectCount", Long.toString(stats.getMemoryStoreObjectCount()));
			stat.put("onDiskHits", Long.toString(stats.getOnDiskHits()));
			stat.put("onDiskMisses", Long.toString(stats.getOnDiskMisses()));
			stat.put("diskStoreObjectCount", Long.toString(stats.getDiskStoreObjectCount()));

			cacheStats.add(stat);
		}

		ServletContext sc = getServletContext();
		sc.setAttribute("cacheStats", cacheStats);
		sc.setAttribute("statsEnabled", statsEnabled);
		sc.getRequestDispatcher("/admin/cache_stats.jsp").forward(request, response);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_CACHE_STATS", null, null, null);

		log.debug("view: void");
	}

	/**
	 * Refresh stats
	 */
	private synchronized void refresh() {
		cacheStatistics.clear();

		for (String name : CacheProvider.getInstance().getOkmCacheNames()) {
			Cache cache = CacheProvider.getInstance().getCache(name);
			cacheStatistics.put(name, cache.getStatistics());
		}
	}
}
