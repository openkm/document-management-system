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

package com.openkm.servlet;

import com.openkm.bean.HttpSessionInfo;
import com.openkm.core.HttpSessionManager;
import com.openkm.util.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Date;

/**
 * Session Listener
 */
public class SessionListener implements HttpSessionListener {
	private static Logger log = LoggerFactory.getLogger(SessionListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		//log.debug("New session created on {} with id {}", new Date(), se.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		log.debug("Session destroyed on {} with id {}", new Date(), se.getSession().getId());
		HttpSession session = se.getSession();
		HttpSessionInfo si = HttpSessionManager.getInstance().getSession(session.getId());
		HttpSessionManager.getInstance().remove(session.getId());

		// Activity log
		if (si != null) {
			UserActivity.log(si.getUser(), "SESSION_DESTROYED", si.getId(), null, null);
		}
	}
}
