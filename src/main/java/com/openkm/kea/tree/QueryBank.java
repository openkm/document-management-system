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

import com.openkm.core.Config;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QueryBank
 *
 * @author jllort
 *
 */
public class QueryBank {
	private static Logger log = LoggerFactory.getLogger(QueryBank.class);
	private static QueryBank instance;

	/**
	 * QueryBank
	 */
	private QueryBank() {
	}

	/**
	 * QueryBank
	 */
	public static synchronized QueryBank getInstance() {
		if (instance == null) {
			instance = new QueryBank();
		}
		return instance;
	}

	/**
	 * getTreeTopQuery
	 *
	 * @param con Repository connection
	 *
	 * @return
	 */
	public TupleQuery getTreeTopQuery(RepositoryConnection con) {
		try {

			// query = "SELECT C, tipus FROM {C} rdfs:subClassOf {tipus} ";
			//
			// query = "SELECT C, tipus FROM {C} tipus {Z} "; // Treu totes les relacions
			//
			// // La consulta del miracle que treu els fills !!! l'invent de la comparacio esta en transformar-ho a
			// string
			// // en la comparació
			// query =
			// "SELECT C, tipus FROM {C} rdfs:subClassOf {tipus} where xsd:string(tipus) = \"http://www.fao.org/aos/agrovoc#c_50113\"";
			//
			// // Aquesta es la consulta que ens retorna els nodes que no tenen pare
			// query = "SELECT C, tipus FROM {C} Y {tipus}; [rdfs:subClassOf {classe}] where not bound(classe) ";

			// String query = "SELECT DISTINCT UID, TEXT FROM {UID} Y {OBJECT}, {UID} rdfs:label {TEXT} ; "+
			// "[rdfs:subClassOf {CLAZZ}] where not bound(CLAZZ) and lang(TEXT)=\"es\" "+
			// "USING NAMESPACE " +
			// "foaf=<http://xmlns.com/foaf/0.1/>, " +
			// "dcterms=<http://purl.org/dc/terms/>, "+
			// "rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>, " +
			// "owl=<http://www.w3.org/2002/07/owl#>, " +
			// "rdfs=<http://www.w3.org/2000/01/rdf-schema#>, " +
			// "skos=<http://www.w3.org/2004/02/skos/core#>, " +
			// "dc=<http://purl.org/dc/elements/1.1/> ";
			//
			// log.info(query);
			return con.prepareTupleQuery(QueryLanguage.SERQL, Config.KEA_THESAURUS_TREE_ROOT);
		} catch (RepositoryException e) {
			log.error("Error preparing tree top query", e);
		} catch (MalformedQueryException e) {
			log.error("malformed rMap query", e);
		}
		return null;
	}

	/**
	 * getTreeNextLayerQuery
	 *
	 * @param parentID The parent id
	 * @param con The repository connection
	 *
	 * @return
	 */
	public TupleQuery getTreeNextLayerQuery(String RDFparentID, RepositoryConnection con) {
		try {
			// String query = "SELECT DISTINCT UID, TEXT FROM {UID} rdfs:subClassOf {CLAZZ}, {UID} rdfs:label {TEXT} "+
			// "where xsd:string(CLAZZ) = \"RDFparentID\" and lang(TEXT)=\"es\" "+
			// "USING NAMESPACE " +
			// "foaf=<http://xmlns.com/foaf/0.1/>, " +
			// "dcterms=<http://purl.org/dc/terms/>, "+
			// "rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>, " +
			// "owl=<http://www.w3.org/2002/07/owl#>, " +
			// "rdfs=<http://www.w3.org/2000/01/rdf-schema#>, " +
			// "skos=<http://www.w3.org/2004/02/skos/core#>, " +
			// "dc=<http://purl.org/dc/elements/1.1/> ";
			//
			// query = query.replace("RDFparentID", RDFparentID);
			return con.prepareTupleQuery(QueryLanguage.SERQL,
					Config.KEA_THESAURUS_TREE_CHILDS.replace("RDFparentID", RDFparentID));
		} catch (RepositoryException e) {
			log.error("Error preparing rMap query", e);
		} catch (MalformedQueryException e) {
			log.error("malformed next tree layer query", e);
		}

		return null;
	}
}
