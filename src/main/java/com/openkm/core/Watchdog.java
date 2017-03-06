/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (C) 2006-2011  Paco Avila & Josep Llort
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

import com.openkm.api.OKMAuth;
import com.openkm.bean.DbSessionInfo;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.TimerTask;

public class Watchdog extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(Watchdog.class);
	private static volatile boolean running = false;

	public void run() {
		if (running) {
			log.warn("*** Watchdog already running ***");
		} else {
			running = true;
			log.debug("*** Watchdog activated ***");

			try {
				if (Config.REPOSITORY_NATIVE) {
					DbSessionManager sm = DbSessionManager.getInstance();

					for (String token : sm.getTokens()) {
						DbSessionInfo si = sm.getInfo(token);

						if (!Config.SYSTEM_USER.equals(si.getAuth().getName())) {
							Calendar expiration = (Calendar) si.getLastAccess().clone();
							expiration.add(Calendar.SECOND, Config.SESSION_EXPIRATION);
							log.debug(si.getAuth().getName() + ", Expiration: " + expiration.getTime());

							if (Calendar.getInstance().after(expiration)) {
								try {
									// Activity log
									UserActivity.log("system", "SESSION_EXPIRATION", si.getAuth().getName(), null, token + ", IDLE FROM: " + si.getLastAccess().getTime());
									OKMAuth.getInstance().logout(token);
								} catch (RepositoryException e) {
									log.error(e.getMessage(), e);
								} catch (DatabaseException e) {
									log.error(e.getMessage(), e);
								}
							}
						}
					}
				} else {
					// Other implementation
				}
			} finally {
				running = false;
			}
		}
	}
}
