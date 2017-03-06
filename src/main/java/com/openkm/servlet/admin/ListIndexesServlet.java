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

package com.openkm.servlet.admin;

import com.openkm.core.Config;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.bean.NodeBase;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.util.FormatUtil;
import com.openkm.util.WebUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Rebuild Lucene indexes
 */
public class ListIndexesServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ListIndexesServlet.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String method = request.getMethod();

		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("search")) {
				searchLuceneDocuments(request, response);
			} else {
				showLuceneDocument(request, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * List Lucene indexes
	 */
	@SuppressWarnings("unchecked")
	private void showLuceneDocument(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean showTerms = WebUtils.getBoolean(request, "showTerms");
		int id = WebUtils.getInt(request, "id", 0);
		FullTextSession ftSession = null;
		ReaderProvider rProv = null;
		Session session = null;
		IndexReader idx = null;
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			ftSession = Search.getFullTextSession(session);
			SearchFactory sFactory = ftSession.getSearchFactory();
			rProv = sFactory.getReaderProvider();

			DirectoryProvider<Directory>[] dirProv = sFactory.getDirectoryProviders(NodeDocument.class);
			idx = rProv.openReader(dirProv[0]);

			// Print Lucene documents
			if (!idx.isDeleted(id)) {
				Document doc = idx.document(id);
				String hibClass = null;

				for (Fieldable fld : doc.getFields()) {
					Map<String, String> field = new HashMap<String, String>();
					field.put("name", fld.name());
					field.put("value", fld.stringValue());
					fields.add(field);

					if (fld.name().equals("_hibernate_class")) {
						hibClass = fld.stringValue();
					}
				}

				/**
				 * 1) Get all the terms using indexReader.terms()
				 * 2) Process the term only if it belongs to the target field.
				 * 3) Get all the docs using indexReader.termDocs(term);
				 * 4) So, we have the term-doc pairs at this point.
				 */
				if (showTerms && NodeDocument.class.getCanonicalName().equals(hibClass)) {
					List<String> terms = new ArrayList<String>();

					for (TermEnum te = idx.terms(); te.next(); ) {
						Term t = te.term();

						if ("text".equals(t.field())) {
							for (TermDocs tds = idx.termDocs(t); tds.next(); ) {
								if (id == tds.doc()) {
									terms.add(t.text());
								}
							}
						}
					}

					Map<String, String> field = new HashMap<String, String>();
					field.put("name", "terms");
					field.put("value", terms.toString());
					fields.add(field);
				}
			}

			ServletContext sc = getServletContext();
			sc.setAttribute("fields", fields);
			sc.setAttribute("id", id);
			sc.setAttribute("max", idx.maxDoc() - 1);
			sc.setAttribute("prev", id > 0);
			sc.setAttribute("next", id < idx.maxDoc() - 1);
			sc.setAttribute("showTerms", showTerms);
			sc.getRequestDispatcher("/admin/list_indexes.jsp").forward(request, response);
		} finally {
			if (rProv != null && idx != null) {
				rProv.closeReader(idx);
			}

			HibernateUtil.close(ftSession);
			HibernateUtil.close(session);
		}
	}

	/**
	 * Search Lucene indexes
	 */
	@SuppressWarnings("unchecked")
	private void searchLuceneDocuments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			ParseException {
		String exp = WebUtils.getString(request, "exp");
		FullTextSession ftSession = null;
		Session session = null;
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			ftSession = Search.getFullTextSession(session);

			if (exp != null && !exp.isEmpty()) {
				Analyzer analyzer = new org.apache.lucene.analysis.WhitespaceAnalyzer(Config.LUCENE_VERSION);
				QueryParser parser = new QueryParser(Config.LUCENE_VERSION, NodeBase.UUID_FIELD, analyzer);
				Query query = null;

				if (FormatUtil.isValidUUID(exp)) {
					query = new TermQuery(new Term(NodeBase.UUID_FIELD, exp));
				} else {
					query = parser.parse(exp);
				}

				FullTextQuery ftq = ftSession.createFullTextQuery(query, NodeDocument.class, NodeFolder.class, NodeMail.class);
				ftq.setProjection(FullTextQuery.DOCUMENT_ID, FullTextQuery.SCORE, FullTextQuery.THIS);

				for (Iterator<Object[]> it = ftq.iterate(); it.hasNext(); ) {
					Object[] qRes = it.next();
					Integer docId = (Integer) qRes[0];
					Float score = (Float) qRes[1];
					NodeBase nBase = (NodeBase) qRes[2];

					// Add result
					Map<String, String> res = new HashMap<String, String>();
					res.put("docId", String.valueOf(docId));
					res.put("score", String.valueOf(score));
					res.put("uuid", nBase.getUuid());
					res.put("name", nBase.getName());

					if (nBase instanceof NodeDocument) {
						res.put("type", "Document");
					} else if (nBase instanceof NodeFolder) {
						res.put("type", "Folder");
					} else {
						log.warn("Unknown");
					}

					results.add(res);
				}
			}

			ServletContext sc = getServletContext();
			sc.setAttribute("results", results);
			sc.setAttribute("exp", exp.replaceAll("\"", "&quot;"));
			sc.getRequestDispatcher("/admin/search_indexes.jsp").forward(request, response);
		} finally {
			HibernateUtil.close(ftSession);
			HibernateUtil.close(session);
		}
	}
}
