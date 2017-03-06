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

package com.openkm.api;

import com.openkm.bean.StatsInfo;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.module.ModuleManager;
import com.openkm.module.StatsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pavila
 *
 */
public class OKMStats implements StatsModule {
	private static Logger log = LoggerFactory.getLogger(OKMStats.class);
	private static OKMStats instance = new OKMStats();

	private OKMStats() {
	}

	public static OKMStats getInstance() {
		return instance;
	}

	@Override
	public StatsInfo getDocumentsByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getDocumentsByContext({})", token);
		StatsModule sm = ModuleManager.getStatsModule();
		StatsInfo stats = sm.getDocumentsByContext(token);
		log.debug("getDocumentsByContext: {}", stats);
		return stats;
	}

	@Override
	public StatsInfo getDocumentsSizeByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getDocumentsSizeByContext({})", token);
		StatsModule sm = ModuleManager.getStatsModule();
		StatsInfo stats = sm.getDocumentsSizeByContext(token);
		log.debug("getDocumentsSizeByContext: {}", stats);
		return stats;
	}

	@Override
	public StatsInfo getFoldersByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getFoldersByContext({})", token);
		StatsModule sm = ModuleManager.getStatsModule();
		StatsInfo stats = sm.getFoldersByContext(token);
		log.debug("getFoldersByContext: {}", stats);
		return stats;
	}

	@Override
	public StatsInfo getMailsByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getMailsByContext({})", token);
		StatsModule sm = ModuleManager.getStatsModule();
		StatsInfo stats = sm.getMailsByContext(token);
		log.debug("getMailsByContext: {}", stats);
		return stats;
	}
}
