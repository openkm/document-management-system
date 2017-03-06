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

package com.openkm.kea.tree;

import com.openkm.api.OKMFolder;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.bean.kea.Term;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.kea.RDFREpository;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * KEA Tree
 *
 * @author jllort
 */
public class KEATree {
	private static Logger log = LoggerFactory.getLogger(KEATree.class);

	/**
	 * Generate tree
	 */
	public static void generateTree(String token, int levelToDraw, String parentPath, Vector<String> parentUIDs, Writer out)
			throws IOException, PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		// Remove previous thesaurus
		for (Folder fld : OKMFolder.getInstance().getChildren(token, "/" + Repository.THESAURUS)) {
			out.write("Removing " + fld.getPath() + "<br>");
			OKMFolder.getInstance().purge(token, fld.getPath());
		}

		gnerateTreeHelper(token, null, 0, levelToDraw, parentPath, parentUIDs, out);
	}

	@SuppressWarnings("unchecked")
	private static void gnerateTreeHelper(String token, String termID, int level, int levelToDraw, String parentPath,
	                                      Vector<String> parentUIDs, Writer out) throws IOException, PathNotFoundException, ItemExistsException,
			AccessDeniedException, com.openkm.core.RepositoryException, DatabaseException, ExtensionException,
			AutomationException {
		List<Term> lisTerms = getParentTerms(termID);

		if (level <= levelToDraw) {
			out.write("<br/>Found " + lisTerms.size() + " terms in level " + level + "<br/>");
			out.flush();
		}

		for (ListIterator<Term> it = lisTerms.listIterator(); it.hasNext(); ) {
			Vector<String> newParentUIDs = (Vector<String>) parentUIDs.clone();
			String path = parentPath;
			Term term = it.next();

			if (level <= levelToDraw) {
				drawTerm(term, level, out);
			}

			path += "/" + term.getText();
			Folder folder = new Folder();
			folder.setPath(path);
			OKMFolder.getInstance().create(token, folder);

			// To solve infinite loop (nodes must not be in a infinite cycle)
			if (!newParentUIDs.contains(term.getUid())) {
				newParentUIDs.add(term.getUid());

				// Recursive generation
				gnerateTreeHelper(token, term.getUid(), level + 1, levelToDraw, path, newParentUIDs, out);
			}
		}
	}

	/**
	 * drawTerm
	 *
	 * @param term The term
	 * @param level The level
	 */
	private static void drawTerm(Term term, int level, Writer out) throws IOException {
		String levelSeparator = "";

		for (int i = 0; i < level; i++) {
			levelSeparator += "-";
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		out.write(dtf.format(cal.getTime()) + " Creating term " + levelSeparator + "> [" + term.getText() + "] - with uid:"
				+ term.getUid() + "<br>");
		out.flush();
	}

	/**
	 * getParentTerms
	 *
	 * @param termID The term id
	 * @return List of child terms
	 */
	private static List<Term> getParentTerms(String termID) {
		List<Term> childTerms = new ArrayList<Term>();
		org.openrdf.repository.RepositoryConnection con = null;
		TupleQuery query;

		try {
			con = RDFREpository.getInstance().getOWLConnection();

			if (termID == null) {
				query = QueryBank.getInstance().getTreeTopQuery(con);
			} else {
				query = QueryBank.getInstance().getTreeNextLayerQuery(termID, con);
			}

			TupleQueryResult result = query.evaluate();

			while (result.hasNext()) {

				BindingSet bindingSet = result.next();
				Term term = new Term(bindingSet.getValue("UID").stringValue(), bindingSet.getValue("TEXT").stringValue());

				// need to ignore duplicates casued by grandchild problem
				if (!childTerms.contains(term)) {
					childTerms.add(term);
				}
			}
		} catch (QueryEvaluationException e) {
			log.error("Query evaluation exception", e);
		} catch (org.openrdf.repository.RepositoryException e) {
			log.error("RDFVocabulary repository exception", e);
		}

		Collections.sort(childTerms, new TermComparator());

		return childTerms;
	}
}
