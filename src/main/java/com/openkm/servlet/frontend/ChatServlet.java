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

package com.openkm.servlet.frontend;

import com.openkm.api.OKMAuth;
import com.openkm.core.ChatManager;
import com.openkm.core.Config;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMChatService;
import com.openkm.principal.PrincipalAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 * Chat Servlet Class
 */
public class ChatServlet extends OKMRemoteServiceServlet implements OKMChatService {
	private static Logger log = LoggerFactory.getLogger(ChatServlet.class);
	private static final long serialVersionUID = 3780857624687394918L;
	private static final int DELAY = 1000; // mseg
	private static final int CYCLES = 5; // number of seconds CYCLES*DELAY
	private static final ChatManager manager = new ChatManager();

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	public void login() throws OKMException {
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();

		if (user != null) {
			manager.login(user);
		}
	}

	@Override
	public void logout() throws OKMException {
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();

		if (user != null) {
			manager.logout(user);
		}
	}

	@Override
	public List<GWTUser> getLoggedUsers() throws OKMException {
		List<GWTUser> users = new ArrayList<GWTUser>();
		updateSessionManager();

		try {
			if (!Config.SYSTEM_MAINTENANCE) {
				for (String userId : manager.getLoggedUsers()) {
					GWTUser user = new GWTUser();
					user.setId(userId);
					user.setUsername(OKMAuth.getInstance().getName(null, userId));
					users.add(user);
				}
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}

		return users;
	}

	@Override
	public String createNewChatRoom(String user) throws OKMException {
		updateSessionManager();

		try {
			String actualUser = getThreadLocalRequest().getRemoteUser();
			return manager.createNewChatRoom(actualUser, user);
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
	}

	@Override
	public List<String> getPendingMessage(String room) throws OKMException {
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();
		List<String> pendingMessages = new ArrayList<String>();

		try {
			if (user != null) {
				int countCycle = 0;

				// Persistence connection = DELAY * CYCLES
				do {
					pendingMessages = manager.getPendingMessage(user, room);
					countCycle++;

					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						// Ignore
					}
				} while (pendingMessages.isEmpty() && (countCycle < CYCLES) && manager.getLoggedUsers().contains(user));
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}

		return pendingMessages;
	}

	@Override
	public List<String> getPendingChatRoomUser() {
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();
		List<String> pendingRooms = new ArrayList<String>();

		if (user != null) {
			int countCycle = 0;

			// Persistence connection = DELAY * CYCLES
			do {
				pendingRooms = manager.getPendingChatRoomUser(user);
				countCycle++;

				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// Ignore
				}
			} while (pendingRooms.isEmpty() && (countCycle < CYCLES) && manager.getLoggedUsers().contains(user));
		}

		return pendingRooms;
	}

	@Override
	public void addMessageToRoom(String room, String msg) throws OKMException {
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();

		try {
			if (user != null) {
				manager.addMessageToRoom(user, room, msg);
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
	}

	@Override
	public void closeRoom(String room) throws OKMException {
		updateSessionManager();
		String user = getThreadLocalRequest().getRemoteUser();

		try {
			if (user != null) {
				manager.closeRoom(user, room);
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
	}

	@Override
	public void addUserToChatRoom(String room, String user) throws OKMException {
		updateSessionManager();

		try {
			manager.addUserToChatRoom(user, room);
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
	}

	@Override
	public String usersInRoom(String room) throws OKMException {
		updateSessionManager();

		try {
			return String.valueOf(manager.getNumberOfUsersInRoom(room));
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
	}

	@Override
	public List<String> getUsersInRoom(String room) throws OKMException {
		updateSessionManager();

		try {
			return manager.getUsersInRoom(room);
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMChatService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
	}

	/**
	 * getChatManager
	 */
	public static ChatManager getChatManager() {
		return manager;
	}
}
