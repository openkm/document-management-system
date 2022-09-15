/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.bean.GWTKeyword;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.GWTResultSet;

import java.util.List;

/**
 * @author jllort
 *
 */
public interface OKMSearchServiceAsync {
	void getAllSearchs(AsyncCallback<List<GWTQueryParams>> callback);

	void saveSearch(GWTQueryParams params, String type, AsyncCallback<Long> callback);

	void deleteSearch(long id, AsyncCallback<?> callback);

	void findPaginated(GWTQueryParams params, int offset, int limit, AsyncCallback<GWTResultSet> callback);

	void getKeywordMap(List<String> filter, AsyncCallback<List<GWTKeyword>> callback);

	void find(GWTQueryParams params, AsyncCallback<GWTResultSet> callback);

	void share(long qpId, AsyncCallback<?> callback);

	void unshare(long qpId, AsyncCallback<?> callback);

	void findSimpleQueryPaginated(String statement, int offset, int limit, AsyncCallback<GWTResultSet> callback);

	void findMoreLikeThis(String uuid, AsyncCallback<GWTResultSet> callback);
}
