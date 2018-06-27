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

package com.openkm.module.db;

import com.openkm.bean.*;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.TextArea;
import com.openkm.bean.nr.NodeQueryResult;
import com.openkm.bean.nr.NodeResultSet;
import com.openkm.cache.UserNodeKeywordsManager;
import com.openkm.core.*;
import com.openkm.dao.*;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.dao.bean.QueryParams;
import com.openkm.dao.bean.cache.UserNodeKeywords;
import com.openkm.module.SearchModule;
import com.openkm.module.db.base.BaseDocumentModule;
import com.openkm.module.db.base.BaseFolderModule;
import com.openkm.module.db.base.BaseMailModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class DbSearchModule implements SearchModule {
	private static Logger log = LoggerFactory.getLogger(DbSearchModule.class);
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");

	@Override
	public List<QueryResult> findByContent(String token, String expression) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("findByContent({}, {})", token, expression);
		QueryParams params = new QueryParams();
		params.setContent(expression);
		List<QueryResult> ret = find(token, params);
		log.debug("findByContent: {}", ret);
		return ret;
	}

	@Override
	public List<QueryResult> findByName(String token, String expression) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("findByName({}, {})", token, expression);
		QueryParams params = new QueryParams();
		params.setName(expression);
		List<QueryResult> ret = find(token, params);
		log.debug("findByName: {}", ret);
		return ret;
	}

	@Override
	public List<QueryResult> findByKeywords(String token, Set<String> expression) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("findByKeywords({}, {})", token, expression);
		QueryParams params = new QueryParams();
		params.setKeywords(expression);
		List<QueryResult> ret = find(token, params);
		log.debug("findByKeywords: {}", ret);
		return ret;
	}

	@Override
	public List<QueryResult> find(String token, QueryParams params) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("find({}, {})", token, params);
		List<QueryResult> ret = findPaginated(token, params, 0, Config.MAX_SEARCH_RESULTS).getResults();
		log.debug("find: {}", ret);
		return ret;
	}

	@Override
	public ResultSet findPaginated(String token, QueryParams params, int offset, int limit) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findPaginated({}, {}, {}, {})", new Object[]{token, params, offset, limit});
		Authentication auth = null, oldAuth = null;
		Query query = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (params.getStatementQuery() != null && !params.getStatementQuery().equals("")) {
				// query = params.getStatementQuery();
			} else {
				query = prepareStatement(params);
			}

			ResultSet rs = findByStatementPaginated(auth, query, offset, limit);
			log.debug("findPaginated: {}", rs);
			return rs;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public List<QueryResult> findByQuery(String token, String query) throws ParseException, AccessDeniedException,
			RepositoryException, DatabaseException {
		return findByQueryPaginated(token, query, 0, Config.MAX_SEARCH_RESULTS).getResults();
	}

	@Override
	public ResultSet findByQueryPaginated(String token, String query, int offset, int limit) throws ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("findByQueryPaginated({}, {}, {}, {})", new Object[]{token, query, offset, limit});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			QueryParser qp = new QueryParser(Config.LUCENE_VERSION, "text", SearchDAO.analyzer);
			Query q = qp.parse(query);
			ResultSet rs = findByStatementPaginated(auth, q, offset, limit);
			log.debug("findByQueryPaginated: {}", rs);
			return rs;
		} catch (org.apache.lucene.queryParser.ParseException e) {
			throw new ParseException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Prepare statement
	 */
	public Query prepareStatement(QueryParams params) throws IOException, ParseException, RepositoryException, DatabaseException {
		log.debug("prepareStatement({})", params);
		BooleanQuery query = new BooleanQuery();

		// Clean params
		params.setName(params.getName() != null ? params.getName().trim() : "");
		params.setContent(params.getContent() != null ? params.getContent().trim() : "");
		params.setKeywords(params.getKeywords() != null ? params.getKeywords() : new HashSet<String>());
		params.setCategories(params.getCategories() != null ? params.getCategories() : new HashSet<String>());
		params.setMimeType(params.getMimeType() != null ? params.getMimeType().trim() : "");
		params.setAuthor(params.getAuthor() != null ? params.getAuthor().trim() : "");
		params.setPath(params.getPath() != null ? params.getPath().trim() : "");
		params.setMailSubject(params.getMailSubject() != null ? params.getMailSubject().trim() : "");
		params.setMailFrom(params.getMailFrom() != null ? params.getMailFrom().trim() : "");
		params.setMailTo(params.getMailTo() != null ? params.getMailTo().trim() : "");
		params.setProperties(params.getProperties() != null ? params.getProperties() : new HashMap<String, String>());

		// Domains
		boolean document = (params.getDomain() & QueryParams.DOCUMENT) != 0;
		boolean folder = (params.getDomain() & QueryParams.FOLDER) != 0;
		boolean mail = (params.getDomain() & QueryParams.MAIL) != 0;
		log.debug("doc={}, fld={}, mail={}", new Object[]{document, folder, mail});

		/**
		 * DOCUMENT
		 */
		if (document) {
			BooleanQuery queryDocument = new BooleanQuery();
			Term tEntity = new Term("_hibernate_class", NodeDocument.class.getCanonicalName());
			queryDocument.add(new TermQuery(tEntity), BooleanClause.Occur.MUST);

			if (!params.getContent().isEmpty()) {
				Query parsedQuery = SearchDAO.getInstance().parseQuery(params.getContent(), "text");
				queryDocument.add(parsedQuery, BooleanClause.Occur.MUST);
			}

			if (!params.getName().isEmpty()) {
				if (!params.getName().contains("*") && !params.getName().contains("?")) {
					params.setName("*" + params.getName() + "*");
				}

				Term t = new Term("name", PathUtils.encodeEntities(params.getName().toLowerCase()));
				queryDocument.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
			}

			if (!params.getMimeType().isEmpty()) {
				Term t = new Term("mimeType", params.getMimeType());
				queryDocument.add(new TermQuery(t), BooleanClause.Occur.MUST);
			}

			if (!params.getAuthor().isEmpty()) {
				Term t = new Term("author", params.getAuthor());
				queryDocument.add(new TermQuery(t), BooleanClause.Occur.MUST);
			}

			if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null) {
				Date from = params.getLastModifiedFrom().getTime();
				String sFrom = DAY_FORMAT.format(from);
				Date to = params.getLastModifiedTo().getTime();
				String sTo = DAY_FORMAT.format(to);
				queryDocument.add(new TermRangeQuery("lastModified", sFrom, sTo, true, true), BooleanClause.Occur.MUST);
			}

			appendCommon(params, queryDocument);
			query.add(queryDocument, BooleanClause.Occur.SHOULD);
		}

		/**
		 * FOLDER
		 */
		if (folder) {
			BooleanQuery queryFolder = new BooleanQuery();
			Term tEntity = new Term("_hibernate_class", NodeFolder.class.getCanonicalName());
			queryFolder.add(new TermQuery(tEntity), BooleanClause.Occur.MUST);

			if (!params.getName().isEmpty()) {
				Term t = new Term("name", PathUtils.encodeEntities(params.getName().toLowerCase()));
				queryFolder.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
			}

			appendCommon(params, queryFolder);
			query.add(queryFolder, BooleanClause.Occur.SHOULD);
		}

		/**
		 * MAIL
		 */
		if (mail) {
			BooleanQuery queryMail = new BooleanQuery();
			Term tEntity = new Term("_hibernate_class", NodeMail.class.getCanonicalName());
			queryMail.add(new TermQuery(tEntity), BooleanClause.Occur.MUST);

			if (!params.getContent().isEmpty()) {
				Query parsedQuery = SearchDAO.getInstance().parseQuery(params.getContent(), "content");
				queryMail.add(parsedQuery, BooleanClause.Occur.MUST);
			}

			if (!params.getMailSubject().isEmpty()) {
				Term t = new Term("subject", params.getMailSubject().toLowerCase());
				queryMail.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
			}

			if (!params.getMailFrom().isEmpty()) {
				Term t = new Term("from", params.getMailFrom().toLowerCase());
				queryMail.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
			}

			if (!params.getMailTo().isEmpty()) {
				Term t = new Term("to", params.getMailTo().toLowerCase());
				queryMail.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
			}

			if (!params.getMimeType().isEmpty()) {
				Term t = new Term("mimeType", params.getMimeType());
				queryMail.add(new TermQuery(t), BooleanClause.Occur.MUST);
			}

			// We also use the getLastModifiedFrom and getLastModifiedTo also to filter mails by sentDate
			if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null) {
				Date from = params.getLastModifiedFrom().getTime();
				String sFrom = DAY_FORMAT.format(from);
				Date to = params.getLastModifiedTo().getTime();
				String sTo = DAY_FORMAT.format(to);
				queryMail.add(new TermRangeQuery("sentDate", sFrom, sTo, true, true), BooleanClause.Occur.MUST);
			}

			appendCommon(params, queryMail);
			query.add(queryMail, BooleanClause.Occur.SHOULD);
		}

		log.debug("prepareStatement: {}", query.toString());
		return query;
	}

	/**
	 * Add common fields
	 */
	private void appendCommon(QueryParams params, BooleanQuery query) throws IOException, ParseException, DatabaseException, RepositoryException {
		if (!params.getPath().equals("")) {
			if (Config.STORE_NODE_PATH) {
				Term t = new Term("path", params.getPath() + "/");
				query.add(new PrefixQuery(t), BooleanClause.Occur.MUST);
			} else {
				String context = PathUtils.getContext(params.getPath());
				Term t = new Term("context", PathUtils.fixContext(context));
				query.add(new TermQuery(t), BooleanClause.Occur.MUST);

				if (!params.getPath().equals(context)) {
					try {
						String parentUuid = NodeBaseDAO.getInstance().getUuidFromPath(params.getPath());
						BooleanQuery parent = new BooleanQuery();
						Term tFld = new Term("parent", parentUuid);
						parent.add(new TermQuery(tFld), BooleanClause.Occur.SHOULD);

						for (String uuidChild : SearchDAO.getInstance().findFoldersInDepth(parentUuid)) {
							Term tChild = new Term("parent", uuidChild);
							parent.add(new TermQuery(tChild), BooleanClause.Occur.SHOULD);
						}

						query.add(parent, BooleanClause.Occur.MUST);
					} catch (BooleanQuery.TooManyClauses e) {
						throw new RepositoryException("Max clauses reached, please search from a deeper folder", e);
					} catch (PathNotFoundException e) {
						throw new RepositoryException("Path Not Found: " + e.getMessage());
					}
				}
			}
		}

		if (!params.getKeywords().isEmpty()) {
			for (String keyword : params.getKeywords()) {
				Term t = new Term("keyword", PathUtils.encodeEntities(keyword));
				query.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
			}
		}

		if (!params.getCategories().isEmpty()) {
			for (String category : params.getCategories()) {
				Term t = new Term("category", category);
				query.add(new TermQuery(t), BooleanClause.Occur.MUST);
			}
		}

		if (!params.getProperties().isEmpty()) {
			Map<PropertyGroup, List<FormElement>> formsElements = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);

			for (Entry<String, String> ent : params.getProperties().entrySet()) {
				FormElement fe = FormUtils.getFormElement(formsElements, ent.getKey());

				if (fe != null && ent.getValue() != null) {
					String valueTrimmed = ent.getValue().trim().toLowerCase();

					if (!valueTrimmed.isEmpty()) {
						if (fe instanceof Select) {
							if (((Select) fe).getType().equals(Select.TYPE_SIMPLE)) {
								Term t = new Term(ent.getKey(), valueTrimmed);
								query.add(new TermQuery(t), BooleanClause.Occur.MUST);
							} else {
								String[] options = valueTrimmed.split(",");

								for (String option : options) {
									Term t = new Term(ent.getKey(), option);
									query.add(new TermQuery(t), BooleanClause.Occur.MUST);
								}
							}
						} else if (fe instanceof Input && ((Input) fe).getType().equals(Input.TYPE_DATE)) {
							String[] date = valueTrimmed.split(",");

							if (date.length == 2) {
								Calendar from = ISO8601.parseBasic(date[0]);
								Calendar to = ISO8601.parseBasic(date[1]);

								if (from != null && to != null) {
									String sFrom = DAY_FORMAT.format(from.getTime());
									String sTo = DAY_FORMAT.format(to.getTime());
									query.add(new TermRangeQuery(ent.getKey(), sFrom, sTo, true, true), BooleanClause.Occur.MUST);
								}
							}
						} else if (fe instanceof Input && ((Input) fe).getType().equals(Input.TYPE_TEXT) || fe instanceof TextArea) {
							for (StringTokenizer st = new StringTokenizer(valueTrimmed, " "); st.hasMoreTokens(); ) {
								Term t = new Term(ent.getKey(), st.nextToken().toLowerCase());
								query.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
							}
						} else {
							Term t = new Term(ent.getKey(), valueTrimmed);
							query.add(new WildcardQuery(t), BooleanClause.Occur.MUST);
						}
					}
				}
			}
		}
	}

	/**
	 * Find by statement
	 */
	private ResultSet findByStatementPaginated(Authentication auth, Query query, int offset, int limit) throws RepositoryException,
			DatabaseException {
		log.debug("findByStatementPaginated({}, {}, {}, {}, {})", new Object[]{auth, query, offset, limit});
		long begin = System.currentTimeMillis();
		List<QueryResult> results = new ArrayList<QueryResult>();
		ResultSet rs = new ResultSet();

		try {
			if (query != null) {
				NodeResultSet nrs = SearchDAO.getInstance().findByQuery(query, offset, limit);
				rs.setTotal(nrs.getTotal());

				for (NodeQueryResult nqr : nrs.getResults()) {
					QueryResult qr = new QueryResult();
					qr.setExcerpt(nqr.getExcerpt());
					qr.setScore((long) (100 * nqr.getScore()));

					if (nqr.getDocument() != null) {
						qr.setNode(BaseDocumentModule.getProperties(auth.getName(), nqr.getDocument()));
					} else if (nqr.getFolder() != null) {
						qr.setNode(BaseFolderModule.getProperties(auth.getName(), nqr.getFolder()));
					} else if (nqr.getMail() != null) {
						qr.setNode(BaseMailModule.getProperties(auth.getName(), nqr.getMail()));					
					} else if (nqr.getAttachment() != null) {
						qr.setNode(BaseDocumentModule.getProperties(auth.getName(), nqr.getAttachment()));
						qr.setAttachment(true);
					}

					results.add(qr);
				}

				rs.setResults(results);
			}

			// Activity log
			UserActivity.log(auth.getName(), "FIND_BY_STATEMENT_PAGINATED", null, null, offset + ", " + limit + ", " + query);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		}

		SystemProfiling.log(query + ", " + offset + ", " + limit, System.currentTimeMillis() - begin);
		log.trace("findByStatementPaginated.Time: {}", FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
		log.debug("findByStatementPaginated: {}", rs);
		return rs;
	}

	@Override
	public long saveSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("saveSearch({}, {})", token, params);
		Authentication auth = null, oldAuth = null;
		long id = 0;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			params.setUser(auth.getName());
			id = QueryParamsDAO.create(params);

			// Activity log
			UserActivity.log(auth.getName(), "SAVE_SEARCH", params.getName(), null, params.toString());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("saveSearch: {}", id);
		return id;
	}

	@Override
	public void updateSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("updateSearch({}, {})", token, params);
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			params.setUser(auth.getName());
			QueryParamsDAO.update(params);

			// Activity log
			UserActivity.log(auth.getName(), "UPDATE_SEARCH", params.getName(), null, params.toString());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("updateSearch: void");
	}

	@Override
	public QueryParams getSearch(String token, int qpId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getSearch({}, {})", token, qpId);
		QueryParams qp = new QueryParams();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			qp = QueryParamsDAO.findByPk(qpId);

			// If this is a dashboard user search, dates are used internally
			if (qp.isDashboard()) {
				qp.setLastModifiedFrom(null);
				qp.setLastModifiedTo(null);
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_SAVED_SEARCH", Integer.toString(qpId), null, qp.toString());
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getSearch: {}", qp);
		return qp;
	}

	@Override
	public List<QueryParams> getAllSearchs(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getAllSearchs({})", token);
		List<QueryParams> ret = new ArrayList<QueryParams>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<QueryParams> qParams = QueryParamsDAO.findByUser(auth.getName());

			for (Iterator<QueryParams> it = qParams.iterator(); it.hasNext(); ) {
				QueryParams qp = it.next();

				if (!qp.isDashboard()) {
					ret.add(qp);
				}
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_ALL_SEARCHS", null, null, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getAllSearchs: {}", ret);
		return ret;
	}

	@Override
	public void deleteSearch(String token, long qpId) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("deleteSearch({}, {})", token, qpId);
		Authentication auth = null, oldAuth = null;

		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			QueryParams qp = QueryParamsDAO.findByPk(qpId);
			QueryParamsDAO.delete(qpId);

			// Purge visited nodes table
			if (qp.isDashboard()) {
				DashboardDAO.deleteVisitedNodes(auth.getName(), qp.getName());
			}

			// Activity log
			UserActivity.log(auth.getName(), "DELETE_SAVED_SEARCH", Long.toString(qpId), null, null);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("deleteSearch: void");
	}

	@Override
	public Map<String, Integer> getKeywordMap(String token, List<String> filter) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getKeywordMap({}, {})", token, filter);
		Map<String, Integer> cloud = null;

		if (Config.USER_KEYWORDS_CACHE) {
			cloud = getKeywordMapCached(token, filter);
		} else {
			cloud = getKeywordMapLive(token, filter);
		}

		log.debug("getKeywordMap: {}", cloud);
		return cloud;
	}

	/**
	 * Get keyword map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> getKeywordMapLive(String token, List<String> filter) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getKeywordMapLive({}, {})", token, filter);
		String qs = "select elements(nb.keywords) from NodeBase nb";
		HashMap<String, Integer> cloud = new HashMap<String, Integer>();
		org.hibernate.Session hSession = null;
		Transaction tx = null;
		@SuppressWarnings("unused")
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			hSession = HibernateUtil.getSessionFactory().openSession();
			tx = hSession.beginTransaction();
			org.hibernate.Query hq = hSession.createQuery(qs);
			List<String> nodeKeywords = hq.list();

			if (filter != null && nodeKeywords.containsAll(filter)) {
				for (String keyword : nodeKeywords) {
					if (!filter.contains(keyword)) {
						Integer occurs = cloud.get(keyword) != null ? cloud.get(keyword) : 0;
						cloud.put(keyword, occurs + 1);
					}
				}
			}

			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(hSession);

			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getKeywordMapLive: {}", cloud);
		return cloud;
	}

	/**
	 * Get keyword map
	 */
	private Map<String, Integer> getKeywordMapCached(String token, List<String> filter) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getKeywordMapCached({}, {})", token, filter);
		HashMap<String, Integer> keywordMap = new HashMap<String, Integer>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			Collection<UserNodeKeywords> userDocKeywords = UserNodeKeywordsManager.get(auth.getName()).values();

			for (Iterator<UserNodeKeywords> kwIt = userDocKeywords.iterator(); kwIt.hasNext(); ) {
				Set<String> docKeywords = kwIt.next().getKeywords();

				if (filter != null && docKeywords.containsAll(filter)) {
					for (Iterator<String> itDocKeywords = docKeywords.iterator(); itDocKeywords.hasNext(); ) {
						String keyword = itDocKeywords.next();

						if (!filter.contains(keyword)) {
							Integer occurs = keywordMap.get(keyword) != null ? keywordMap.get(keyword) : 0;
							keywordMap.put(keyword, occurs + 1);
						}
					}
				}
			}
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getKeywordMapCached: {}", keywordMap);
		return keywordMap;
	}

	@Override
	public List<Document> getCategorizedDocuments(String token, String categoryId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getCategorizedDocuments({}, {})", token, categoryId);
		long begin = System.currentTimeMillis();
		List<Document> documents = new ArrayList<Document>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByCategory(categoryId)) {
				documents.add(BaseDocumentModule.getProperties(auth.getName(), nDoc));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(categoryId, System.currentTimeMillis() - begin);
		log.trace("getCategorizedDocuments.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getCategorizedDocuments: {}", documents);
		return documents;
	}

	@Override
	public List<Folder> getCategorizedFolders(String token, String categoryId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getCategorizedFolders({}, {})", token, categoryId);
		long begin = System.currentTimeMillis();
		List<Folder> folders = new ArrayList<Folder>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeFolder nFld : NodeFolderDAO.getInstance().findByCategory(categoryId)) {
				folders.add(BaseFolderModule.getProperties(auth.getName(), nFld));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(categoryId, System.currentTimeMillis() - begin);
		log.trace("getCategorizedFolders.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getCategorizedFolders: {}", folders);
		return folders;
	}

	@Override
	public List<Mail> getCategorizedMails(String token, String categoryId) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getCategorizedMails({}, {})", token, categoryId);
		long begin = System.currentTimeMillis();
		List<Mail> mails = new ArrayList<Mail>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeMail nMail : NodeMailDAO.getInstance().findByCategory(categoryId)) {
				mails.add(BaseMailModule.getProperties(auth.getName(), nMail));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(categoryId, System.currentTimeMillis() - begin);
		log.trace("getCategorizedMails.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getCategorizedMails: {}", mails);
		return mails;
	}

	@Override
	public List<Document> getDocumentsByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getDocumentsByKeyword({}, {})", token, keyword);
		long begin = System.currentTimeMillis();
		List<Document> documents = new ArrayList<Document>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByKeyword(keyword)) {
				documents.add(BaseDocumentModule.getProperties(auth.getName(), nDoc));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(keyword, System.currentTimeMillis() - begin);
		log.trace("getDocumentsByKeyword.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getDocumentsByKeyword: {}", documents);
		return documents;
	}

	@Override
	public List<Folder> getFoldersByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getFoldersByKeyword({}, {})", token, keyword);
		long begin = System.currentTimeMillis();
		List<Folder> folders = new ArrayList<Folder>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeFolder nFld : NodeFolderDAO.getInstance().findByKeyword(keyword)) {
				folders.add(BaseFolderModule.getProperties(auth.getName(), nFld));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(keyword, System.currentTimeMillis() - begin);
		log.trace("getFoldersByKeyword.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getFoldersByKeyword: {}", folders);
		return folders;
	}

	@Override
	public List<Mail> getMailsByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getMailsByKeyword({}, {})", token, keyword);
		long begin = System.currentTimeMillis();
		List<Mail> mails = new ArrayList<Mail>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeMail nMail : NodeMailDAO.getInstance().findByKeyword(keyword)) {
				mails.add(BaseMailModule.getProperties(auth.getName(), nMail));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(keyword, System.currentTimeMillis() - begin);
		log.trace("getMailsByKeyword.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getMailsByKeyword: {}", mails);
		return mails;
	}

	@Override
	public List<Document> getDocumentsByPropertyValue(String token, String group, String property, String value)
			throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getDocumentsByPropertyValue({}, {}, {}, {})", new Object[]{token, group, property, value});
		long begin = System.currentTimeMillis();
		List<Document> documents = new ArrayList<Document>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByPropertyValue(group, property, value)) {
				documents.add(BaseDocumentModule.getProperties(auth.getName(), nDoc));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(group + ", " + property + ", " + value, System.currentTimeMillis() - begin);
		log.trace("getDocumentsByPropertyValue.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getDocumentsByPropertyValue: {}", documents);
		return documents;
	}

	@Override
	public List<Folder> getFoldersByPropertyValue(String token, String group, String property, String value) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getFoldersByPropertyValue({}, {}, {}, {})", new Object[]{token, group, property, value});
		long begin = System.currentTimeMillis();
		List<Folder> folders = new ArrayList<Folder>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeFolder nFld : NodeFolderDAO.getInstance().findByPropertyValue(group, property, value)) {
				folders.add(BaseFolderModule.getProperties(auth.getName(), nFld));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(group + ", " + property + ", " + value, System.currentTimeMillis() - begin);
		log.trace("getFoldersByPropertyValue.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getFoldersByPropertyValue: {}", folders);
		return folders;
	}

	@Override
	public List<Mail> getMailsByPropertyValue(String token, String group, String property, String value) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("getMailsByPropertyValue({}, {}, {}, {})", new Object[]{token, group, property, value});
		long begin = System.currentTimeMillis();
		List<Mail> mails = new ArrayList<Mail>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			for (NodeMail nMail : NodeMailDAO.getInstance().findByPropertyValue(group, property, value)) {
				mails.add(BaseMailModule.getProperties(auth.getName(), nMail));
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(group + ", " + property + ", " + value, System.currentTimeMillis() - begin);
		log.trace("getMailsByPropertyValue.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getMailsByPropertyValue: {}", mails);
		return mails;
	}

	@Override
	public List<QueryResult> findSimpleQuery(String token, String statement) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("findSimpleQuery({}, {})", token, statement);
		List<QueryResult> ret = findSimpleQueryPaginated(token, statement, 0, Config.MAX_SEARCH_RESULTS).getResults();
		log.debug("findSimpleQuery: {}", ret);
		return ret;
	}

	@Override
	public ResultSet findSimpleQueryPaginated(String token, String statement, int offset, int limit) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("findSimpleQueryPaginated({}, {}, {}, {})", new Object[]{token, statement, offset, limit});
		long begin = System.currentTimeMillis();
		List<QueryResult> results = new ArrayList<QueryResult>();
		ResultSet rs = new ResultSet();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (statement != null && !statement.equals("")) {
				// Only search in Taxonomy
				statement = statement.concat(" AND context:okm_root");

				NodeResultSet nrs = SearchDAO.getInstance().findBySimpleQuery(statement, offset, limit);
				rs.setTotal(nrs.getTotal());

				for (NodeQueryResult nqr : nrs.getResults()) {
					QueryResult qr = new QueryResult();
					qr.setExcerpt(nqr.getExcerpt());
					qr.setScore((long) (100 * nqr.getScore()));

					if (nqr.getDocument() != null) {
						qr.setNode(BaseDocumentModule.getProperties(auth.getName(), nqr.getDocument()));
					} else if (nqr.getFolder() != null) {
						qr.setNode(BaseFolderModule.getProperties(auth.getName(), nqr.getFolder()));
					} else if (nqr.getMail() != null) {
						qr.setNode(BaseMailModule.getProperties(auth.getName(), nqr.getMail()));
					} else if (nqr.getAttachment() != null) {						
						qr.setNode(BaseDocumentModule.getProperties(auth.getName(), nqr.getAttachment()));
						qr.setAttachment(true);
					}

					results.add(qr);
				}

				rs.setResults(results);
			}

			// Activity log
			UserActivity.log(auth.getName(), "FIND_SIMPLE_QUERY_PAGINATED", null, null, offset + ", " + limit + ", " + statement);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(statement + ", " + offset + ", " + limit, System.currentTimeMillis() - begin);
		log.trace("findSimpleQueryPaginated.Time: {}", System.currentTimeMillis() - begin);
		log.debug("findSimpleQueryPaginated: {}", rs);
		return rs;
	}

	@Override
	public ResultSet findMoreLikeThis(String token, String uuid, int maxResults) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("findMoreLikeThis({}, {}, {})", new Object[]{token, uuid, maxResults});
		long begin = System.currentTimeMillis();
		List<QueryResult> results = new ArrayList<QueryResult>();
		ResultSet rs = new ResultSet();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			NodeResultSet nrs = SearchDAO.getInstance().moreLikeThis(uuid, maxResults);
			rs.setTotal(nrs.getTotal());

			for (NodeQueryResult nqr : nrs.getResults()) {
				QueryResult qr = new QueryResult();
				qr.setExcerpt(nqr.getExcerpt());
				qr.setScore((long) (100 * nqr.getScore()));

				if (nqr.getDocument() != null) {
					qr.setNode(BaseDocumentModule.getProperties(auth.getName(), nqr.getDocument()));
				} else if (nqr.getFolder() != null) {
					qr.setNode(BaseFolderModule.getProperties(auth.getName(), nqr.getFolder()));
				} else if (nqr.getMail() != null) {
					qr.setNode(BaseMailModule.getProperties(auth.getName(), nqr.getMail()));
				}

				results.add(qr);
			}

			rs.setResults(results);

			// Activity log
			UserActivity.log(auth.getName(), "FIND_MORE_LIKE_THIS", uuid, null, Integer.toString(maxResults));
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(uuid + ", " + maxResults, System.currentTimeMillis() - begin);
		log.trace("findMoreLikeThis.Time: {}", System.currentTimeMillis() - begin);
		log.debug("findMoreLikeThis: {}", rs);
		return rs;
	}
}
