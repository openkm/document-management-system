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
import com.openkm.bean.nr.NodeQueryResult;
import com.openkm.bean.nr.NodeResultSet;
import com.openkm.cache.CacheProvider;
import com.openkm.cache.UserItemsManager;
import com.openkm.core.*;
import com.openkm.core.Config;
import com.openkm.dao.*;
import com.openkm.dao.bean.*;
import com.openkm.dao.bean.cache.UserItems;
import com.openkm.module.DashboardModule;
import com.openkm.module.db.base.BaseDocumentModule;
import com.openkm.module.db.base.BaseFolderModule;
import com.openkm.module.db.base.BaseMailModule;
import com.openkm.module.db.stuff.SecurityHelper;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.SystemProfiling;
import com.openkm.util.UserActivity;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.*;

public class DbDashboardModule implements DashboardModule {
	private static Logger log = LoggerFactory.getLogger(DbDashboardModule.class);
	private static final int MAX_RESULTS = 20;
	private static final String CACHE_DASHBOARD_USER_MAILS = "com.openkm.cache.dashboardUserMails";
	private static final String CACHE_DASHBOARD_USER_DOCUMENTS = "com.openkm.cache.dashboardUserDocuments";
	private static final String CACHE_DASHBOARD_TOP_DOCUMENTS = "com.openkm.cache.dashboardTopDocuments";

	@Override
	public List<DashboardDocumentResult> getUserLockedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLockedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getUserLockedDocumentsSrv(auth.getName());
			log.debug("getUserLockedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getUserLockedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getUserLockedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "from NodeDocument nd where nd.checkedOut='F' and nd.lock.owner=:user";
		List<DashboardDocumentResult> al = executeQueryDocument(user, qs, "LOCK_DOCUMENT", Integer.MAX_VALUE);

		// Check for already visited results
		checkVisitedDocuments(user, "UserLockedDocuments", al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserLockedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserLockedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getUserCheckedOutDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserCheckedOutDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getUserCheckedOutDocumentsSrv(auth.getName());
			log.debug("getUserCheckedOutDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getUserCheckedOutDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getUserCheckedOutDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "from NodeDocument nd where nd.checkedOut='T' and nd.lock.owner=:user";
		List<DashboardDocumentResult> al = executeQueryDocument(user, qs, "CHECKOUT_DOCUMENT", Integer.MAX_VALUE);

		// Check for already visited results
		checkVisitedDocuments(user, "UserCheckedOutDocuments", al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserCheckedOutDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserCheckedOutDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getUserSubscribedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserSubscribedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getUserSubscribedDocumentsSrv(auth.getName());
			log.debug("getUserSubscribedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getUserSubscribedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getUserSubscribedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "from NodeDocument nd where :user in elements(nd.subscriptors)";
		List<DashboardDocumentResult> al = executeQueryDocument(user, qs, "SUBSCRIBE_USER", Integer.MAX_VALUE);

		// Check for already visited results
		checkVisitedDocuments(user, "UserSubscribedDocuments", al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserSubscribedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserSubscribedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardFolderResult> getUserSubscribedFolders(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserSubscribedFolders({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardFolderResult> al = getUserSubscribedFoldersSrv(auth.getName());
			log.debug("getUserSubscribedFolders: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardFolderResult> getUserSubscribedFoldersSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getUserSubscribedFoldersSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "from NodeFolder nf where '" + user + "' in elements(nf.subscriptors)";
		List<DashboardFolderResult> al = executeQueryFolder(user, qs, "SUBSCRIBE_USER", Integer.MAX_VALUE);

		// Check for already visited results
		checkVisitedFolders(user, "UserSubscribedFolders", al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserSubscribedFoldersSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserSubscribedFoldersSrv: {}", al);
		return al;
	}

	/**
	 * Execute query with documents
	 */
	@SuppressWarnings("unchecked")
	private List<DashboardDocumentResult> executeQueryDocument(String user, String qs, String action, int maxResults)
			throws RepositoryException, DatabaseException {
		log.debug("executeQueryDocument({}, {}, {}, {})", new Object[]{user, qs, action, maxResults});
		List<DashboardDocumentResult> al = new ArrayList<DashboardDocumentResult>();
		Session session = null;
		int i = 0;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs).setCacheable(true);
			q.setString("user", user);
			List<NodeDocument> results = q.list();

			for (Iterator<NodeDocument> it = results.iterator(); it.hasNext() && i < maxResults; ) {
				NodeDocument nDoc = it.next();

				if (SecurityHelper.getAccessManager().isGranted(nDoc, Permission.READ)) {
					NodeDocumentDAO.getInstance().initialize(nDoc, false);
					Document doc = BaseDocumentModule.getProperties(user, nDoc);
					DashboardDocumentResult vo = new DashboardDocumentResult();
					vo.setDocument(doc);
					vo.setDate(ActivityDAO.getActivityDate(user, action, nDoc.getUuid()));
					vo.setVisited(false);
					al.add(vo);
					i++;
				}
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		// Sort results
		Collections.sort(al, new Comparator<DashboardDocumentResult>() {
			public int compare(DashboardDocumentResult doc1, DashboardDocumentResult doc2) {
				return doc2.getDate().compareTo(doc1.getDate());
			}
		});

		return al;
	}

	/**
	 * Execute query with folders
	 */
	@SuppressWarnings("unchecked")
	private List<DashboardFolderResult> executeQueryFolder(String user, String qs, String action, int maxResults)
			throws RepositoryException, DatabaseException {
		List<DashboardFolderResult> al = new ArrayList<DashboardFolderResult>();
		Session session = null;
		int i = 0;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<NodeFolder> results = q.list();

			for (Iterator<NodeFolder> it = results.iterator(); it.hasNext() && i < maxResults; ) {
				NodeFolder nFld = it.next();

				if (SecurityHelper.getAccessManager().isGranted(nFld, Permission.READ)) {
					NodeFolderDAO.getInstance().initialize(nFld);
					Folder fld = BaseFolderModule.getProperties(user, nFld);
					DashboardFolderResult vo = new DashboardFolderResult();
					vo.setFolder(fld);
					vo.setDate(ActivityDAO.getActivityDate(user, action, nFld.getUuid()));
					vo.setVisited(false);
					al.add(vo);
					i++;
				}
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}

		// Order results
		Collections.sort(al, new Comparator<DashboardFolderResult>() {
			public int compare(DashboardFolderResult fld1, DashboardFolderResult fld2) {
				return fld2.getDate().compareTo(fld1.getDate());
			}
		});

		return al;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastUploadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLastUploadedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getUserLastUploadedDocumentsSrv(auth.getName());
			log.debug("getUserLastUploadedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getUserLastUploadedDocumentsSrv(String user) throws DatabaseException {
		log.debug("getUserLastUploadedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select a.item, a.date from DashboardActivity a " +
				"where a.action='CREATE_DOCUMENT' and a.user= :user " +
				"order by a.date desc";
		final String SOURCE = "UserLastUploadedDocuments";
		List<DashboardDocumentResult> al = getUserDocuments(user, SOURCE, qs);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserLastUploadedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserLastUploadedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastModifiedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLastModifiedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getUserLastModifiedDocumentsSrv(auth.getName());
			log.debug("getUserLastModifiedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getUserLastModifiedDocumentsSrv(String user) throws DatabaseException {
		log.debug("getUserLastModifiedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select distinct a.item, max(a.date) from DashboardActivity a " +
				"where a.action='CHECKIN_DOCUMENT' and a.user= :user " +
				"group by a.item order by max(a.date) desc";
		final String SOURCE = "UserLastModifiedDocuments";
		List<DashboardDocumentResult> al = getUserDocuments(user, SOURCE, qs);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserLastModifiedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserLastModifiedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastDownloadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLastDownloadedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getUserLastDownloadedDocumentsSrv(auth.getName());
			log.debug("getUserLastDownloadedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getUserLastDownloadedDocumentsSrv(String user) throws DatabaseException {
		log.debug("getUserLastDownloadedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select distinct a.item, max(a.date) from DashboardActivity a " +
				"where a.action='GET_DOCUMENT_CONTENT' and a.user= :user " +
				"group by a.item order by max(a.date) desc";
		final String SOURCE = "UserLastDownloadedDocuments";
		List<DashboardDocumentResult> al = getUserDocuments(user, SOURCE, qs);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserLastDownloadedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserLastDownloadedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardMailResult> getUserLastImportedMails(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLastImportedMails({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardMailResult> al = getUserLastImportedMailsSrv(auth.getName());
			log.debug("getUserLastImportedMails: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardMailResult> getUserLastImportedMailsSrv(String user) throws DatabaseException {
		log.debug("getUserLastImportedMailsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "from DashboardActivity a " +
				"where a.action='CREATE_MAIL' and a.user= :user " +
				"order by a.date desc";
		final String SOURCE = "UserLastImportedMails";
		List<DashboardMailResult> al = getUserMails(user, SOURCE, qs);

		// Check for already visited results
		checkVisitedMails(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserLastImportedMailsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserLastImportedMailsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getUserLastImportedMailAttachments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getUserLastImportedMailAttachments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getUserLastImportedMailAttachmentsSrv(auth.getName());
			log.debug("getUserLastImportedMailAttachments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	public List<DashboardDocumentResult> getUserLastImportedMailAttachmentsSrv(String user) throws DatabaseException {
		log.debug("getUserLastImportedMailAttachmentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select a.item, a.date from DashboardActivity a " +
				"where a.action='CREATE_MAIL_ATTACHMENT' and a.user= :user " +
				"order by a.date desc";
		final String SOURCE = "UserLastImportedMailAttachments";
		List<DashboardDocumentResult> al = getUserDocuments(user, SOURCE, qs);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserLastImportedMailAttachmentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserLastImportedMailAttachmentsSrv: {}", al);
		return al;
	}

	@Override
	public long getUserDocumentsSize(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserDocumentsSize({})", token);
		long begin = System.currentTimeMillis();
		Authentication auth = null, oldAuth = null;
		long size = 0;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			if (Config.USER_ITEM_CACHE) {
				UserItems usrItems = UserItemsManager.get(auth.getName());
				size = usrItems.getSize();
			} else {
				// Other implementation
			}

			SystemProfiling.log(null, System.currentTimeMillis() - begin);
			log.trace("getUserDocumentsSize.Time: {}", System.currentTimeMillis() - begin);
			log.debug("getUserDocumentsSize: {}", size);
			return size;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	@Override
	public List<QueryParams> getUserSearchs(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getUserSearchs({})", token);
		long begin = System.currentTimeMillis();
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

				// If this is a dashboard user search, dates are used internally
				if (qp.isDashboard()) {
					qp.setLastModifiedFrom(null);
					qp.setLastModifiedTo(null);
					ret.add(qp);
				}
			}

			// Activity log
			UserActivity.log(auth.getName(), "GET_DASHBOARD_USER_SEARCHS", null, null, null);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		SystemProfiling.log(null, System.currentTimeMillis() - begin);
		log.trace("getUserSearchs.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserSearchs: {}", ret);
		return ret;
	}

	@Override
	public List<DashboardDocumentResult> find(String token, int qpId) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("find({}, {})", token, qpId);
		List<DashboardDocumentResult> al = new ArrayList<DashboardDocumentResult>();
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			al = findSrv(auth.getName(), qpId);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("find: {}", al);
		return al;
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> findSrv(String user, int qpId) throws RepositoryException,
			DatabaseException, ParseException, IOException {
		log.debug("findSrv({}, {})", user, qpId);
		List<DashboardDocumentResult> al = new ArrayList<DashboardDocumentResult>();
		DbSearchModule directSearch = new DbSearchModule();

		// Get the saved query params
		QueryParams params = QueryParamsDAO.findByPk(qpId);
		log.debug("PARAMS: {}", params.toString());

		// Set query date (first time)
		if (params.getLastModifiedTo() == null) {
			Calendar firstExecution = Calendar.getInstance();
			firstExecution.add(Calendar.MONTH, -1);
			params.setLastModifiedTo(firstExecution);
		}

		Calendar lastExecution = resetHours(params.getLastModifiedTo());
		Calendar actualDate = resetHours(Calendar.getInstance());
		log.debug("lastExecution -> {}", lastExecution.getTime());
		log.debug("actualDate -> {}", actualDate.getTime());

		if (lastExecution.before(actualDate)) {
			params.setLastModifiedFrom(params.getLastModifiedTo());
		}

		params.setLastModifiedTo(Calendar.getInstance());

		// Prepare statement
		log.debug("PARAMS {}", params);
		org.apache.lucene.search.Query query = directSearch.prepareStatement(params);
		log.debug("STATEMENT {}", query);

		// Execute query
		al = executeQueryDocument(user, query, MAX_RESULTS);

		// Update query params
		QueryParamsDAO.update(params);

		// Check for already visited results
		checkVisitedDocuments(user, Long.toString(params.getId()), al);
		log.debug("findSrv: {}", al);
		return al;
	}

	/**
	 * Reset calendar hours
	 */
	private Calendar resetHours(Calendar cal) {
		Calendar tmp = (Calendar) cal.clone();
		tmp.set(Calendar.HOUR_OF_DAY, 0);
		tmp.set(Calendar.MINUTE, 0);
		tmp.set(Calendar.SECOND, 0);
		tmp.set(Calendar.MILLISECOND, 0);
		return tmp;
	}

	/**
	 * Execute Lucene query with documents
	 */
	private List<DashboardDocumentResult> executeQueryDocument(String user, org.apache.lucene.search.Query query,
	                                                           int maxResults) throws RepositoryException, DatabaseException {
		List<DashboardDocumentResult> al = new ArrayList<DashboardDocumentResult>();

		try {
			NodeResultSet nrs = SearchDAO.getInstance().findByQuery(query, 0, maxResults);

			for (NodeQueryResult nqr : nrs.getResults()) {
				DashboardDocumentResult vo = new DashboardDocumentResult();
				NodeDocument nDoc = nqr.getDocument();
				Document doc = BaseDocumentModule.getProperties(user, nDoc);
				vo.setDocument(doc);
				vo.setDate(ActivityDAO.getActivityDate(user, null, nDoc.getUuid()));
				vo.setVisited(false);
				al.add(vo);
			}
		} catch (ParseException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		}

		return al;
	}

	@Override
	public List<DashboardDocumentResult> getLastWeekTopDownloadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getLastWeekTopDownloadedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getLastWeekTopDownloadedDocumentsSrv(auth.getName());
			log.debug("getLastWeekTopDownloadedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getLastWeekTopDownloadedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getUserLastImportedMailAttachmentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select a.item, max(a.date) from DashboardActivity a " +
				"where a.action='GET_DOCUMENT_CONTENT' and a.path like '/" + Repository.ROOT + "/%' and a.date>:date " +
				"group by a.item " +
				"order by count(a.item) desc";
		final String SOURCE = "LastWeekTopDownloadedDocuments";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		List<DashboardDocumentResult> al = getTopDocuments(user, SOURCE, qs, cal);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getUserLastImportedMailAttachmentsByUser.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getUserLastImportedMailAttachmentsByUser: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getLastMonthTopDownloadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getLastMonthTopDownloadedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getLastMonthTopDownloadedDocumentsSrv(auth.getName());
			log.debug("getLastMonthTopDownloadedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getLastMonthTopDownloadedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getLastMonthTopDownloadedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select a.item, max(a.date) from DashboardActivity a " +
				"where a.action='GET_DOCUMENT_CONTENT' and a.path like '/" + Repository.ROOT + "/%' and a.date>:date " +
				"group by a.item " +
				"order by count(a.item) desc";
		final String SOURCE = "LastMonthTopDownloadedDocuments";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		List<DashboardDocumentResult> al = getTopDocuments(user, SOURCE, qs, cal);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getLastMonthTopDownloadedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getLastMonthTopDownloadedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getLastWeekTopModifiedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getLastWeekTopModifiedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getLastWeekTopModifiedDocumentsSrv(auth.getName());
			log.debug("getLastWeekTopModifiedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getLastWeekTopModifiedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getLastWeekTopModifiedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select a.item, max(a.date) from DashboardActivity a " +
				"where a.action='CHECKIN_DOCUMENT' and a.path like '/" + Repository.ROOT + "/%' and a.date>:date " +
				"group by a.item " +
				"order by count(a.item) desc";
		final String SOURCE = "LastWeekTopModifiedDocuments";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		List<DashboardDocumentResult> al = getTopDocuments(user, SOURCE, qs, cal);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getLastWeekTopModifiedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getLastWeekTopModifiedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getLastMonthTopModifiedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getLastMonthTopModifiedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getLastMonthTopModifiedDocumentsSrv(auth.getName());
			log.debug("getLastMonthTopModifiedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getLastMonthTopModifiedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getLastMonthTopModifiedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select a.item, max(a.date) from DashboardActivity a " +
				"where a.action='CHECKIN_DOCUMENT' and a.path like '/" + Repository.ROOT + "/%' and a.date>:date " +
				"group by a.item " +
				"order by count(a.item) desc";
		final String SOURCE = "LastMonthTopModifiedDocuments";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		List<DashboardDocumentResult> al = getTopDocuments(user, SOURCE, qs, cal);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getLastMonthTopModifiedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getLastMonthTopModifiedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getLastModifiedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getLastModifiedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getLastModifiedDocumentsSrv(auth.getName());
			log.debug("getLastModifiedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getLastModifiedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getLastModifiedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select distinct a.item, max(a.date) from DashboardActivity a " +
				"where a.action='CHECKIN_DOCUMENT' and a.path like '/" + Repository.ROOT + "/%' " +
				"group by a.item " +
				"order by max(a.date) desc";
		final String SOURCE = "LastModifiedDocuments";
		List<DashboardDocumentResult> al = getTopDocuments(user, SOURCE, qs, null);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getLastModifiedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getLastModifiedDocumentsSrv: {}", al);
		return al;
	}

	@Override
	public List<DashboardDocumentResult> getLastUploadedDocuments(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getLastUploadedDocuments({})", token);
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			List<DashboardDocumentResult> al = getLastUploadedDocumentsSrv(auth.getName());
			log.debug("getLastUploadedDocuments: {}", al);
			return al;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
	}

	/**
	 * Convenient method for syndication
	 */
	public List<DashboardDocumentResult> getLastUploadedDocumentsSrv(String user) throws RepositoryException,
			DatabaseException {
		log.debug("getLastUploadedDocumentsSrv({})", user);
		long begin = System.currentTimeMillis();
		String qs = "select distinct a.item, max(a.date) from DashboardActivity a " +
				"where a.action='CREATE_DOCUMENT' and a.path like '/" + Repository.ROOT + "/%' " +
				"group by a.item " +
				"order by max(a.date) desc";
		final String SOURCE = "LastUploadedDocuments";
		List<DashboardDocumentResult> al = getTopDocuments(user, SOURCE, qs, null);

		// Check for already visited results
		checkVisitedDocuments(user, SOURCE, al);
		SystemProfiling.log(user, System.currentTimeMillis() - begin);
		log.trace("getLastUploadedDocumentsSrv.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getLastUploadedDocumentsSrv: {}", al);
		return al;
	}

	/**
	 * Get top documents
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<DashboardDocumentResult> getTopDocuments(String user, String source, String qs, Calendar date)
			throws RepositoryException, DatabaseException {
		log.debug("getTopDocuments({}, {}, {}, {})", new Object[]{user, source, qs, (date != null ? date.getTime() : "null")});
		ArrayList<DashboardDocumentResult> al = new ArrayList<DashboardDocumentResult>();
		Cache docResultCache = CacheProvider.getInstance().getCache(CACHE_DASHBOARD_TOP_DOCUMENTS);
		String key = source + ":" + Config.SYSTEM_USER;
		Element elto = docResultCache.get(key);

		if (elto != null) {
			log.debug("Get '{}' from cache", source);
			al = (ArrayList<DashboardDocumentResult>) elto.getValue();
		} else {
			log.debug("Get '{}' from database", source);
			Session session = null;
			int cont = 0;

			try {
				session = HibernateUtil.getSessionFactory().openSession();
				Query q = session.createQuery(qs).setFetchSize(MAX_RESULTS);

				if (date != null) {
					q.setCalendar("date", date);
				}

				// While there is more query results and the MAX_RESULT limit has reached
				for (Iterator<Object[]> it = q.iterate(); it.hasNext() && cont < MAX_RESULTS; cont++) {
					Object[] obj = it.next();
					String resItem = (String) obj[0];
					Calendar resDate = (Calendar) obj[1];

					try {
						NodeDocument nDoc = NodeDocumentDAO.getInstance().findByPk(resItem);
						// String docPath = NodeBaseDAO.getInstance().getPathFromUuid(nDoc.getUuid());

						// Only documents from taxonomy
						// Already filtered in the query
						// if (docPath.startsWith("/okm:root")) {
						Document doc = BaseDocumentModule.getProperties(user, nDoc);
						DashboardDocumentResult vo = new DashboardDocumentResult();
						vo.setDocument(doc);
						vo.setDate(resDate);
						vo.setVisited(false);
						al.add(vo);
						// }
					} catch (PathNotFoundException e) {
						// Do nothing
					}
				}

				docResultCache.put(new Element(key, al));
			} catch (HibernateException e) {
				throw new DatabaseException(e.getMessage(), e);
			} finally {
				HibernateUtil.close(session);
			}
		}

		log.debug("getTopDocuments: {}", al);
		return al;
	}

	@Override
	public void visiteNode(String token, String source, String node, Calendar date) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("visiteNode({}, {}, {}, {})", new Object[]{token, source, node, (date == null ? null : date.getTime())});
		Authentication auth = null, oldAuth = null;

		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}

			Dashboard vo = new Dashboard();
			vo.setUser(auth.getName());
			vo.setSource(source);
			vo.setNode(node);
			vo.setDate(date);
			DashboardDAO.createIfNew(vo);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("visiteNode: void");
	}

	/**
	 * Check visited documents
	 */
	private void checkVisitedDocuments(String user, String source, List<DashboardDocumentResult> docResult)
			throws DatabaseException {
		List<Dashboard> visitedNodes = DashboardDAO.findByUserSource(user, source);

		// Set already visited nodes
		for (Iterator<DashboardDocumentResult> itDocs = docResult.iterator(); itDocs.hasNext(); ) {
			DashboardDocumentResult dsDocResult = itDocs.next();

			for (Iterator<Dashboard> itVisited = visitedNodes.iterator(); itVisited.hasNext(); ) {
				Dashboard visitedNode = itVisited.next();

				// Same node path and same activity log date ?
				if (visitedNode.getNode().equals(dsDocResult.getDocument().getUuid())
						&& visitedNode.getDate().equals(dsDocResult.getDate())) {
					dsDocResult.setVisited(true);
				}
			}
		}

		for (Iterator<Dashboard> itVisited = visitedNodes.iterator(); itVisited.hasNext(); ) {
			Dashboard visitedNode = itVisited.next();
			boolean old = true;

			for (Iterator<DashboardDocumentResult> itDocs = docResult.iterator(); itDocs.hasNext(); ) {
				DashboardDocumentResult dsDocResult = itDocs.next();

				// Same node path and same activity log date ?
				if (visitedNode.getNode().equals(dsDocResult.getDocument().getUuid())
						&& visitedNode.getDate().equals(dsDocResult.getDate())) {
					old = false;
				}
			}

			if (old) {
				DashboardDAO.purgeOldVisitedNode(user, source, visitedNode.getNode(), visitedNode.getDate());
			}
		}
	}

	/**
	 * Check visited folders
	 */
	private void checkVisitedFolders(String user, String source, List<DashboardFolderResult> fldResult)
			throws DatabaseException {
		List<Dashboard> visitedNodes = DashboardDAO.findByUserSource(user, source);

		// Set already visited nodes
		for (Iterator<DashboardFolderResult> itFlds = fldResult.iterator(); itFlds.hasNext(); ) {
			DashboardFolderResult dsFldResult = itFlds.next();

			for (Iterator<Dashboard> itVisited = visitedNodes.iterator(); itVisited.hasNext(); ) {
				Dashboard visitedNode = itVisited.next();

				if (visitedNode.getNode().equals(dsFldResult.getFolder().getUuid())
						&& visitedNode.getDate().equals(dsFldResult.getDate())) {
					dsFldResult.setVisited(true);
				}
			}
		}

		for (Iterator<Dashboard> itVisited = visitedNodes.iterator(); itVisited.hasNext(); ) {
			Dashboard visitedNode = itVisited.next();
			boolean old = true;

			for (Iterator<DashboardFolderResult> itFlds = fldResult.iterator(); itFlds.hasNext(); ) {
				DashboardFolderResult dsFldResult = itFlds.next();

				// Same node path and same activity log date ?
				if (visitedNode.getNode().equals(dsFldResult.getFolder().getUuid())
						&& visitedNode.getDate().equals(dsFldResult.getDate())) {
					old = false;
				}
			}

			if (old) {
				DashboardDAO.purgeOldVisitedNode(user, source, visitedNode.getNode(), visitedNode.getDate());
			}
		}
	}

	/**
	 * Check visited mails
	 */
	private void checkVisitedMails(String user, String source, List<DashboardMailResult> mailResult)
			throws DatabaseException {
		List<Dashboard> visitedNodes = DashboardDAO.findByUserSource(user, source);

		// Set already visited nodes
		for (Iterator<DashboardMailResult> itMails = mailResult.iterator(); itMails.hasNext(); ) {
			DashboardMailResult dsMailResult = itMails.next();

			for (Iterator<Dashboard> itVisited = visitedNodes.iterator(); itVisited.hasNext(); ) {
				Dashboard visitedNode = itVisited.next();

				// Same node path and same activity log date ?
				if (visitedNode.getNode().equals(dsMailResult.getMail().getUuid())
						&& visitedNode.getDate().equals(dsMailResult.getDate())) {
					dsMailResult.setVisited(true);
				}
			}
		}

		for (Iterator<Dashboard> itVisited = visitedNodes.iterator(); itVisited.hasNext(); ) {
			Dashboard visitedNode = itVisited.next();
			boolean old = true;

			for (Iterator<DashboardMailResult> itMails = mailResult.iterator(); itMails.hasNext(); ) {
				DashboardMailResult dsMailResult = itMails.next();

				// Same node path and same activity log date ?
				if (visitedNode.getNode().equals(dsMailResult.getMail().getUuid())
						&& visitedNode.getDate().equals(dsMailResult.getDate())) {
					old = false;
				}
			}

			if (old) {
				DashboardDAO.purgeOldVisitedNode(user, source, visitedNode.getNode(), visitedNode.getDate());
			}
		}
	}

	/**
	 * Get documents from statement
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<DashboardDocumentResult> getUserDocuments(String user, String source, String qs) throws
			DatabaseException {
		log.debug("getUserDocuments({}, {}, {})", new Object[]{user, source, qs});
		ArrayList<DashboardDocumentResult> al = new ArrayList<DashboardDocumentResult>();
		Cache docResultCache = CacheProvider.getInstance().getCache(CACHE_DASHBOARD_USER_DOCUMENTS);
		String key = source + ":" + user;
		Element elto = docResultCache.get(key);

		if (elto != null) {
			log.debug("Get '{}' from cache", source);
			al = (ArrayList<DashboardDocumentResult>) elto.getValue();
		} else {
			log.debug("Get '{}' from database", source);
			org.hibernate.Session hSession = null;

			try {
				hSession = HibernateUtil.getSessionFactory().openSession();
				org.hibernate.Query q = hSession.createQuery(qs);
				q.setString("user", user);
				q.setMaxResults(MAX_RESULTS);

				for (Iterator<Object[]> it = q.list().iterator(); it.hasNext(); ) {
					Object[] actData = it.next();
					String actItem = (String) actData[0];
					Calendar actDate = (Calendar) actData[1];

					try {
						NodeDocument nDoc = NodeDocumentDAO.getInstance().findByPk(actItem);
						Document doc = BaseDocumentModule.getProperties(user, nDoc);
						DashboardDocumentResult vo = new DashboardDocumentResult();
						vo.setDocument(doc);
						vo.setDate(actDate);
						vo.setVisited(false);
						al.add(vo);
					} catch (PathNotFoundException e) {
						// Do nothing
					}
				}

				docResultCache.put(new Element(key, al));
			} catch (HibernateException e) {
				throw new DatabaseException(e.getMessage(), e);
			} finally {
				HibernateUtil.close(hSession);
			}
		}

		log.debug("getUserDocuments: {}", al);
		return al;
	}

	/**
	 * Get mails from statement
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<DashboardMailResult> getUserMails(String user, String source, String qs) throws
			DatabaseException {
		log.debug("getUserMails({}, {}, {})", new Object[]{user, source, qs});
		ArrayList<DashboardMailResult> al = new ArrayList<DashboardMailResult>();
		Cache mailResultCache = CacheProvider.getInstance().getCache(CACHE_DASHBOARD_USER_MAILS);
		String key = source + ":" + user;
		Element elto = mailResultCache.get(key);

		if (elto != null) {
			log.debug("Get '{}' from cache", source);
			al = (ArrayList<DashboardMailResult>) elto.getValue();
		} else {
			log.debug("Get '{}' from database", source);
			org.hibernate.Session hSession = null;

			try {
				hSession = HibernateUtil.getSessionFactory().openSession();
				org.hibernate.Query q = hSession.createQuery(qs);
				q.setString("user", user);
				q.setMaxResults(MAX_RESULTS);

				for (Iterator<DashboardActivity> it = q.list().iterator(); it.hasNext(); ) {
					DashboardActivity da = it.next();

					try {
						NodeMail nMail = NodeMailDAO.getInstance().findByPk(da.getItem());
						Mail mail = BaseMailModule.getProperties(user, nMail);
						DashboardMailResult vo = new DashboardMailResult();
						vo.setMail(mail);
						vo.setDate(da.getDate());
						vo.setVisited(false);
						al.add(vo);
					} catch (PathNotFoundException e) {
						// Do nothing
					}
				}

				mailResultCache.put(new Element(key, al));
			} catch (HibernateException e) {
				throw new DatabaseException(e.getMessage(), e);
			} finally {
				HibernateUtil.close(hSession);
			}
		}

		log.debug("getUserMails: {}", al);
		return al;
	}
}
