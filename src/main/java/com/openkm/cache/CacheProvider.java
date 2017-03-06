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

import com.openkm.core.Config;
import com.openkm.util.ConfigUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class CacheProvider {
	private static Logger log = LoggerFactory.getLogger(CacheProvider.class);
	private static CacheProvider singleton = null;
	private static final String CFG_FILE = "ehcache.xml";

	private CacheProvider() {
	}

	public static synchronized CacheProvider getInstance() {
		if (singleton == null) {
			InputStream is = null;

			try {
				File ehCacheCfg = new File(Config.HOME_DIR + File.separator + CFG_FILE);

				if (ehCacheCfg.exists() && ehCacheCfg.canRead()) {
					log.info("Using Ehcache config from {}", ehCacheCfg);
					is = new FileInputStream(ehCacheCfg);
				} else {
					log.warn("Using Ehcache config from ClassPath: /" + CFG_FILE);
					is = ConfigUtils.getResourceAsStream(CFG_FILE);
				}

				CacheManager manager = CacheManager.create(is);
				log.info("Cache disk store path: {}", manager.getDiskStorePath());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(is);
			}

			singleton = new CacheProvider();
		}

		return singleton;
	}

	/**
	 * Obtain CacheManager used by Hibernate
	 */
	public CacheManager getManager() {
		return CacheManager.getInstance();
	}

	/**
	 * Clear all caches
	 */
	public void clearAll() {
		getManager().clearAll();
	}

	/**
	 * Obtain all cache names
	 */
	public List<String> getAllCacheNames() {
		return Arrays.asList(getManager().getCacheNames());
	}

	/**
	 * Obtain cache names used by OpenKM
	 */
	public List<String> getOkmCacheNames() {
		String[] allCacheNames = getManager().getCacheNames();
		List<String> cacheNames = Arrays.asList(allCacheNames);
		return cacheNames;
	}

	/**
	 * Get Cache from Hibernate. If the case does not exists, create it using
	 * <defaultCache> definition specified in ehcache.xml.
	 */
	public Cache getCache(String name) {
		if (!getManager().cacheExists(name)) {
			log.warn("Add cache '{}' missing in 'ehcache.xml'", name);
			getManager().addCache(name);
		}

		return getManager().getCache(name);
	}
}
