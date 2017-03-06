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

package com.openkm.ws.endpoint;

import com.openkm.bean.Document;
import com.openkm.bean.QueryResult;
import com.openkm.bean.ResultSet;
import com.openkm.core.*;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.ModuleManager;
import com.openkm.module.SearchModule;
import com.openkm.ws.util.IntegerPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.io.IOException;
import java.util.*;

@WebService(name = "OKMSearch", serviceName = "OKMSearch", targetNamespace = "http://ws.openkm.com")
public class SearchService {
	private static Logger log = LoggerFactory.getLogger(SearchService.class);

	@WebMethod
	public QueryResult[] findByContent(@WebParam(name = "token") String token, @WebParam(name = "content") String content)
			throws IOException, ParseException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByContent({}, {})", token, content);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByContent(token, content);
		QueryResult[] result = col.toArray(new QueryResult[col.size()]);
		log.debug("findByContent: {}", result);
		return result;
	}

	@WebMethod
	public QueryResult[] findByName(@WebParam(name = "token") String token, @WebParam(name = "name") String name) throws IOException,
			ParseException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByName({}, {})", token, name);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByName(token, name);
		QueryResult[] result = col.toArray(new QueryResult[col.size()]);
		log.debug("findByName: {}", result);
		return result;
	}

	@WebMethod
	public QueryResult[] findByKeywords(@WebParam(name = "token") String token, @WebParam(name = "keywords") String[] keywords)
			throws IOException, ParseException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByKeywords({}, {})", token, keywords);
		SearchModule sm = ModuleManager.getSearchModule();
		Set<String> set = new HashSet<String>(Arrays.asList(keywords));
		List<QueryResult> col = sm.findByKeywords(token, set);
		QueryResult[] result = col.toArray(new QueryResult[col.size()]);
		log.debug("findByKeywords: {}", result);
		return result;
	}

	@WebMethod
	public QueryResult[] find(@WebParam(name = "token") String token, @WebParam(name = "params") QueryParams params) throws IOException,
			ParseException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("find({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.find(token, params);
		QueryResult[] result = col.toArray(new QueryResult[col.size()]);
		log.debug("find: {}", result);
		return result;
	}

	@WebMethod
	public ResultSet findPaginated(@WebParam(name = "token") String token, @WebParam(name = "params") QueryParams params,
	                               @WebParam(name = "offset") int offset, @WebParam(name = "limit") int limit) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findPaginated({}, {}, {}, {})", new Object[]{token, params, offset, limit});
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet rs = sm.findPaginated(token, params, offset, limit);
		log.debug("findPaginated: {}", rs);
		return rs;
	}

	@WebMethod
	public ResultSet findSimpleQueryPaginated(@WebParam(name = "token") String token, @WebParam(name = "statement") String statement,
	                                          @WebParam(name = "offset") int offset, @WebParam(name = "limit") int limit) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findSimpleQueryPaginated({}, {}, {}, {})", new Object[]{token, statement, offset, limit});
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet rs = sm.findSimpleQueryPaginated(token, statement, offset, limit);
		log.debug("findSimpleQueryPaginated: {}", rs);
		return rs;
	}

	@WebMethod
	public ResultSet findMoreLikeThis(@WebParam(name = "token") String token, @WebParam(name = "uuid") String uuid,
	                                  @WebParam(name = "max") int max) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findMoreLikeThis({}, {}, {})", new Object[]{token, uuid, max});
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet rs = sm.findMoreLikeThis(token, uuid, max);
		log.debug("findMoreLikeThis: {}", rs);
		return rs;
	}

	@WebMethod
	public IntegerPair[] getKeywordMap(@WebParam(name = "token") String token, @WebParam(name = "filter") String[] filter)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getKeywordMap({}, {})", token, filter);
		SearchModule sm = ModuleManager.getSearchModule();
		List<String> alFilter = Arrays.asList(filter);
		Map<String, Integer> map = sm.getKeywordMap(token, alFilter);
		Set<String> keys = map.keySet();
		IntegerPair[] result = new IntegerPair[keys.size()];
		int i = 0;

		// Marshall HashMap
		for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
			String key = it.next();
			IntegerPair p = new IntegerPair();
			p.setKey(key);
			p.setValue(map.get(key));
			result[i++] = p;
		}

		log.debug("getKeywordMap: {}", result);
		return result;
	}

	@WebMethod
	public Document[] getCategorizedDocuments(@WebParam(name = "token") String token, @WebParam(name = "categoryId") String categoryId)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getCategorizedDocuments({}, {})", token, categoryId);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Document> col = sm.getCategorizedDocuments(token, categoryId);
		Document[] result = col.toArray(new Document[col.size()]);
		log.debug("getCategorizedDocuments: {}", result);
		return result;
	}

	@WebMethod
	public long saveSearch(@WebParam(name = "token") String token, @WebParam(name = "params") QueryParams params)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("saveSearch({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		long id = sm.saveSearch(token, params);
		log.debug("saveSearch: {}", id);
		return id;
	}

	@WebMethod
	public void updateSearch(@WebParam(name = "token") String token, @WebParam(name = "params") QueryParams params)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("updateSearch({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		sm.saveSearch(token, params);
		log.debug("updateSearch: void");
	}

	@WebMethod
	public QueryParams getSearch(@WebParam(name = "token") String token, @WebParam(name = "qpId") int qpId) throws AccessDeniedException,
			PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getSearch({}, {})", token, qpId);
		SearchModule sm = ModuleManager.getSearchModule();
		QueryParams qp = sm.getSearch(token, qpId);
		log.debug("getSearch: {}", qp);
		return qp;
	}

	@WebMethod
	public QueryParams[] getAllSearchs(@WebParam(name = "token") String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getAllSearchs({})", token);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryParams> col = sm.getAllSearchs(token);
		QueryParams[] result = col.toArray(new QueryParams[col.size()]);
		log.debug("getAllSearchs: {}", col);
		return result;
	}

	@WebMethod
	public void deleteSearch(@WebParam(name = "token") String token, @WebParam(name = "qpId") int qpId) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("deleteSearch({}, {})", token, qpId);
		SearchModule sm = ModuleManager.getSearchModule();
		sm.deleteSearch(token, qpId);
		log.debug("deleteSearch: void");
	}
}
