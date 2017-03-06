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

package com.openkm.extension.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.openkm.frontend.client.bean.extension.GWTProposedQueryReceived;

import java.util.List;
import java.util.Map;

/**
 * @author jllort
 *
 */
public interface OKMProposedQueryServiceAsync extends RemoteService {
	public void create(long qpId, String users, String roles, String comment, AsyncCallback<?> callback);

	public void findProposedQueriesUsersFrom(AsyncCallback<Map<String, Long>> callback);

	public void markSeen(long msgId, AsyncCallback<?> callback);

	public void markAccepted(long msgId, AsyncCallback<?> callback);

	public void deleteReceived(long msgId, AsyncCallback<?> callback);

	public void deleteSent(long msgId, AsyncCallback<?> callback);

	public void findProposedQueryByMeFromUser(String sender, AsyncCallback<List<GWTProposedQueryReceived>> callback);

	public void deleteProposedQueryByMeFromUser(String user, AsyncCallback<?> callback);
}