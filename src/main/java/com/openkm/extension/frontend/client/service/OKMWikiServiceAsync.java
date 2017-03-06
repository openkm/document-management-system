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
import com.openkm.frontend.client.bean.extension.GWTWikiPage;

import java.util.List;

/**
 * OKMWikiServiceAsync
 *
 * @author jllort
 *
 */
public interface OKMWikiServiceAsync extends RemoteService {
	public void findLatestByNode(String uuid, AsyncCallback<GWTWikiPage> callback);

	public void createNewWikiPage(GWTWikiPage wikiPage, AsyncCallback<GWTWikiPage> callback);

	public void lock(GWTWikiPage wikiPage, AsyncCallback<?> callback);

	public void updateWikiPage(GWTWikiPage wikiPage, AsyncCallback<GWTWikiPage> callback);

	public void deleteWikiPage(GWTWikiPage wikiPage, AsyncCallback<?> callback);

	public void unlock(GWTWikiPage wikiPage, AsyncCallback<?> callback);

	public void findLatestByTitle(String title, AsyncCallback<GWTWikiPage> callback);

	public void findAllByTitle(String title, AsyncCallback<List<GWTWikiPage>> callback);

	public void restoreWikiPage(GWTWikiPage wikiPage, AsyncCallback<GWTWikiPage> callback);

	public void findAllLatestByTitleFiltered(String filter, AsyncCallback<List<String>> callback);

	public void findAllHistoricByTitle(String title, AsyncCallback<List<GWTWikiPage>> callback);
}