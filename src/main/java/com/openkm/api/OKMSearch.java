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

package com.openkm.api;

import com.openkm.bean.*;
import com.openkm.core.*;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.ModuleManager;
import com.openkm.module.SearchModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pavila
 */
public class OKMSearch implements SearchModule {
	private static Logger log = LoggerFactory.getLogger(OKMSearch.class);
	private static OKMSearch instance = new OKMSearch();

	private OKMSearch() {
	}

	public static OKMSearch getInstance() {
		return instance;
	}

	@Override
	public List<QueryResult> findByContent(String token, String words) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByContent({}, {})", token, words);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByContent(token, words);
		log.debug("findByContent: {}", col);
		return col;
	}

	@Override
	public List<QueryResult> findByName(String token, String words) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByName({}, {})", token, words);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByName(token, words);
		log.debug("findByName: {}", col);
		return col;
	}

	@Override
	public List<QueryResult> findByKeywords(String token, Set<String> words) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByKeywords({}, {})", token, words);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByKeywords(token, words);
		log.debug("findByKeywords: {}", col);
		return col;
	}

	@Override
	public List<QueryResult> find(String token, QueryParams params) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("find({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.find(token, params);
		log.debug("find: {}", col);
		return col;
	}

	@Override
	public ResultSet findPaginated(String token, QueryParams params, int offset, int limit) throws IOException,
			ParseException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findPaginated({}, {}, {}, {})", new Object[]{token, params, offset, limit});
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet rs = sm.findPaginated(token, params, offset, limit);
		log.debug("findPaginated: {}", rs);
		return rs;
	}

	@Override
	public List<QueryResult> findByQuery(String token, String query) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("findByQuery({}, {})", token, query);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByQuery(token, query);
		log.debug("findByQuery: {}", col);
		return col;
	}

	@Override
	public ResultSet findByQueryPaginated(String token, String query, int offset, int limit) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByQueryPaginated({}, {}, {}, {})", new Object[]{token, query, offset, limit});
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet rs = sm.findByQueryPaginated(token, query, offset, limit);
		log.debug("findByQueryPaginated: {}", rs);
		return rs;
	}

	@Override
	public long saveSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("saveSearch({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		long id = sm.saveSearch(token, params);
		log.debug("saveSearch: {}", id);
		return id;
	}

	@Override
	public void updateSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("updateSearch({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		sm.saveSearch(token, params);
		log.debug("updateSearch: void");
	}

	@Override
	public QueryParams getSearch(String token, int qpId) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getSearch({}, {})", token, qpId);
		SearchModule sm = ModuleManager.getSearchModule();
		QueryParams qp = sm.getSearch(token, qpId);
		log.debug("getSearch: {}", qp);
		return qp;
	}

	@Override
	public List<QueryParams> getAllSearchs(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getAllSearchs({})", token);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryParams> col = sm.getAllSearchs(token);
		log.debug("getAllSearchs: {}", col);
		return col;
	}

	@Override
	public void deleteSearch(String token, long qpId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("deleteSearch({}, {})", token, qpId);
		SearchModule sm = ModuleManager.getSearchModule();
		sm.deleteSearch(token, qpId);
		log.debug("deleteSearch: void");
	}

	@Override
	public Map<String, Integer> getKeywordMap(String token, List<String> filter) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getKeywordMap({}, {})", token, filter);
		SearchModule sm = ModuleManager.getSearchModule();
		Map<String, Integer> kmap = sm.getKeywordMap(token, filter);
		log.debug("getKeywordMap: {}", kmap);
		return kmap;
	}

	@Override
	public List<Document> getCategorizedDocuments(String token, String categoryId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getCategorizedDocuments({}, {})", token, categoryId);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Document> col = sm.getCategorizedDocuments(token, categoryId);
		log.debug("getCategorizedDocuments: {}", col);
		return col;
	}

	@Override
	public List<Folder> getCategorizedFolders(String token, String categoryId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getCategorizedFolders({}, {})", token, categoryId);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Folder> col = sm.getCategorizedFolders(token, categoryId);
		log.debug("getCategorizedFolders: {}", col);
		return col;
	}

	@Override
	public List<Mail> getCategorizedMails(String token, String categoryId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getCategorizedMails({}, {})", token, categoryId);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Mail> col = sm.getCategorizedMails(token, categoryId);
		log.debug("getCategorizedMails: {}", col);
		return col;
	}

	@Override
	public List<Document> getDocumentsByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getDocumentsByKeyword({}, {})", token, keyword);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Document> col = sm.getDocumentsByKeyword(token, keyword);
		log.debug("getDocumentsByKeyword: {}", col);
		return col;
	}

	@Override
	public List<Folder> getFoldersByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getFoldersByKeyword({}, {})", token, keyword);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Folder> col = sm.getFoldersByKeyword(token, keyword);
		log.debug("getFoldersByKeyword: {}", col);
		return col;
	}

	@Override
	public List<Mail> getMailsByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getMailsByKeyword({}, {})", token, keyword);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Mail> col = sm.getMailsByKeyword(token, keyword);
		log.debug("getMailsByKeyword: {}", col);
		return col;
	}

	@Override
	public List<Document> getDocumentsByPropertyValue(String token, String group, String property, String value)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getDocumentsByPropertyValue({}, {}, {}, {})", new Object[]{token, group, property, value});
		SearchModule sm = ModuleManager.getSearchModule();
		List<Document> col = sm.getDocumentsByPropertyValue(token, group, property, value);
		log.debug("getDocumentsByPropertyValue: {}", col);
		return col;
	}

	@Override
	public List<Folder> getFoldersByPropertyValue(String token, String group, String property, String value)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getFoldersByPropertyValue({}, {}, {}, {})", new Object[]{token, group, property, value});
		SearchModule sm = ModuleManager.getSearchModule();
		List<Folder> col = sm.getFoldersByPropertyValue(token, group, property, value);
		log.debug("getFoldersByPropertyValue: {}", col);
		return col;
	}

	@Override
	public List<Mail> getMailsByPropertyValue(String token, String group, String property, String value)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getMailsByPropertyValue({}, {}, {}, {})", new Object[]{token, group, property, value});
		SearchModule sm = ModuleManager.getSearchModule();
		List<Mail> col = sm.getMailsByPropertyValue(token, group, property, value);
		log.debug("getMailsByPropertyValue: {}", col);
		return col;
	}

	@Override
	public List<QueryResult> findSimpleQuery(String token, String statement) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("findSimpleQuery({}, {})", token, statement);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findSimpleQuery(token, statement);
		log.debug("findSimpleQuery: {}", col);
		return col;
	}

	@Override
	public ResultSet findSimpleQueryPaginated(String token, String statement, int offset, int limit) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("findSimpleQueryPaginated({})", token);
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet ret = sm.findSimpleQueryPaginated(token, statement, offset, limit);
		log.debug("findSimpleQueryPaginated: {}", ret);
		return ret;
	}

	@Override
	public ResultSet findMoreLikeThis(String token, String uuid, int maxResults) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("findMoreLikeThis({}, {}, {})", new Object[]{token, uuid, maxResults});
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet ret = sm.findMoreLikeThis(token, uuid, maxResults);
		log.debug("findMoreLikeThis: {}", ret);
		return ret;
	}
}
