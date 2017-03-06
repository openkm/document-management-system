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

package com.openkm.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTKeyword;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.GWTResultSet;

import java.util.List;

/**
 * OKMSearchService
 *
 * @author jllort
 *
 */
@RemoteServiceRelativePath("Search")
public interface OKMSearchService extends RemoteService {
	public List<GWTQueryParams> getAllSearchs() throws OKMException;

	public Long saveSearch(GWTQueryParams params, String type) throws OKMException;

	public void deleteSearch(long id) throws OKMException;

	public GWTResultSet findPaginated(GWTQueryParams params, int offset, int limit) throws OKMException;

	public List<GWTKeyword> getKeywordMap(List<String> filter) throws OKMException;

	public GWTResultSet find(GWTQueryParams params) throws OKMException;

	public void share(long qpId) throws OKMException;

	public void unshare(long qpId) throws OKMException;

	public GWTResultSet findSimpleQueryPaginated(String statement, int offset, int limit) throws OKMException;

	public GWTResultSet findMoreLikeThis(String uuid) throws OKMException;
}