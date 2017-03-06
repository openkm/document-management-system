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

import com.openkm.bean.HttpSessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Address;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author pavila
 */
public class HttpSessionManager {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HttpSessionManager.class);
	private static HttpSessionManager instance = new HttpSessionManager();
	private List<HttpSessionInfo> sessions = new ArrayList<HttpSessionInfo>();

	/**
	 * Prevents class instantiation
	 */
	private HttpSessionManager() {
	}

	/**
	 * Instantiate a SessionManager.
	 */
	public static HttpSessionManager getInstance() {
		return instance;
	}

	/**
	 * Add a new session
	 */
	public synchronized void add(HttpServletRequest request) {
		HttpSessionInfo si = new HttpSessionInfo();
		HttpSession s = request.getSession();
		boolean add = true;

		for (HttpSessionInfo rsi : sessions) {
			if (rsi.getId().equals(s.getId())) {
				add = false;
			}
		}

		if (add) {
			si.setUser(request.getRemoteUser());
			si.setIp(request.getRemoteAddr());

			try {
				InetAddress addr = Address.getByAddress(request.getRemoteAddr());
				String hostName = Address.getHostName(addr);

				if (hostName.endsWith(".")) {
					si.setHost(hostName.substring(0, hostName.length() - 1));
				} else {
					si.setHost(hostName);
				}
			} catch (UnknownHostException e) {
				si.setHost(request.getRemoteHost());
			}

			si.setId(s.getId());
			Calendar creation = Calendar.getInstance();
			creation.setTimeInMillis(s.getCreationTime());
			si.setCreation(creation);
			Calendar lastAccess = Calendar.getInstance();
			lastAccess.setTimeInMillis(s.getLastAccessedTime());
			si.setLastAccess(lastAccess);

			s.setAttribute("user", request.getRemoteUser());
			sessions.add(si);
		}
	}

	/**
	 * Update session last accessed time
	 */
	public synchronized void update(String id) {
		for (HttpSessionInfo si : sessions) {
			if (si.getId().equals(id)) {
				si.setLastAccess(Calendar.getInstance());
			}
		}
	}

	/**
	 * Remove a session
	 */
	public synchronized void remove(String id) {
		for (Iterator<HttpSessionInfo> it = sessions.iterator(); it.hasNext(); ) {
			HttpSessionInfo si = it.next();

			if (si.getId().equals(id)) {
				it.remove();
				break;
			}
		}
	}

	/**
	 * Return a session info
	 */
	public HttpSessionInfo getSession(String id) {
		for (HttpSessionInfo si : sessions) {
			if (si.getId().equals(id)) {
				return si;
			}
		}
		return null;
	}

	/**
	 * Return all active sessions
	 */
	public List<HttpSessionInfo> getSessions() {
		return sessions;
	}
}
