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

package com.openkm.core;

import com.openkm.api.OKMStats;
import com.openkm.bean.StatsInfo;
import com.openkm.module.db.stuff.DbSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class RepositoryInfo extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(RepositoryInfo.class);
	private static StatsInfo documentsByContext = new StatsInfo();
	private static StatsInfo mailsByContext = new StatsInfo();
	private static StatsInfo foldersByContext = new StatsInfo();
	private static StatsInfo documentsSizeByContext = new StatsInfo();
	private static volatile boolean running = false;

	@Override
	public void run() {
		String systemToken = null;

		if (Config.REPOSITORY_NATIVE) {
			systemToken = DbSessionManager.getInstance().getSystemToken();
		} else {
			// Other implementation
		}

		runAs(systemToken);
	}

	public void runAs(String token) {
		if (running) {
			log.warn("*** Repository info already running ***");
		} else {
			running = true;
			log.debug("*** Begin repository info ***");

			try {
				OKMStats okmStats = OKMStats.getInstance();

				try {
					mailsByContext = okmStats.getMailsByContext(token);
					foldersByContext = okmStats.getFoldersByContext(token);
					documentsByContext = okmStats.getDocumentsByContext(token);
					documentsSizeByContext = okmStats.getDocumentsSizeByContext(token);
				} catch (RepositoryException e) {
					log.error(e.getMessage(), e);
				} catch (DatabaseException e) {
					log.error(e.getMessage(), e);
				}
			} finally {
				running = false;
			}

			log.debug("*** End repository info ***");
		}
	}

	/**
	 * @return Documents grouped by context.
	 */
	public static StatsInfo getDocumentsByContext() {
		return documentsByContext;
	}

	/**
	 * @return Folders grouped by context.
	 */
	public static StatsInfo getFoldersByContext() {
		return foldersByContext;
	}

	/**
	 * @return Mails grouped by context.
	 */
	public static StatsInfo getMailsByContext() {
		return mailsByContext;
	}

	/**
	 * @return Documents size by context.
	 */
	public static StatsInfo getDocumentsSizeByContext() {
		return documentsSizeByContext;
	}
}
