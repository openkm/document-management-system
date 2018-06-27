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

package com.openkm.module;

import com.openkm.bean.*;
import com.openkm.core.*;
import com.openkm.dao.bean.QueryParams;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchModule {

	/**
	 * Search for documents using it indexed content.
	 *
	 * @param expression Expression to be searched.
	 * @return A collection of document which content matched the searched expression.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	List<QueryResult> findByContent(String token, String expression) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Search for documents by document name.
	 *
	 * @param expression Expression to be searched.
	 * @return A collection of document which name matched the searched expression.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	List<QueryResult> findByName(String token, String expression) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Search for documents using it associated keywords.
	 *
	 * @param expression Expression to be searched.
	 * @return A collection of document which keywords matched the searched expression.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	List<QueryResult> findByKeywords(String token, Set<String> expression) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Performs a complex search by content, name and keywords (between others).
	 *
	 * @param params The complex search elements.
	 * @return A collection of documents.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws IOException If something fails when parsing metadata.
	 */
	List<QueryResult> find(String token, QueryParams params) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Performs a complex search by content, name and keywords. Paginated version.
	 *
	 * @param params The complex search elements.
	 * @param offset Query result list offset.
	 * @param limit Query result list limit.
	 * @return A result set with the total of the results and a collection of document from the resulting query
	 *         statement.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	ResultSet findPaginated(String token, QueryParams params, int offset, int limit) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Make a search by a Lucene query
	 *
	 * @param token The session authorization token.
	 * @param query The Lucene query.
	 * @return A result set with the total of the results and a collection of document from the resulting query
	 * statement.
	 */
	List<QueryResult> findByQuery(String token, String query) throws IOException, ParseException, AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Make a search by a Lucene query
	 *
	 * @param token  The session authorization token.
	 * @param query  The Lucene query.
	 * @param offset Query result list offset.
	 * @param limit  Query result list limit.
	 * @return A result set with the total of the results and a collection of document from the resulting query
	 * statement.
	 */
	ResultSet findByQueryPaginated(String token, String query, int offset, int limit) throws IOException, ParseException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Save a search for future use.
	 *
	 * @param params The query params.
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	long saveSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Updated a saved search.
	 *
	 * @param params The query params.
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	void updateSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get a saved search.
	 *
	 * @param qpId The id of the saved search to retrieve.
	 * @return The saved search query params.
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	QueryParams getSearch(String token, int qpId) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Get all saved search.
	 *
	 * @return A collection with the names of the saved search.
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<QueryParams> getAllSearchs(String token) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Delete a saved search.
	 *
	 * @param qpId The id of the saved search
	 * @throws PathNotFoundException If there is no saved search with this name.
	 * @throws RepositoryException If there is any general repository problem or the query fails
	 */
	void deleteSearch(String token, long qpId) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Return a Keyword map. This is a hash with the keywords and the occurrence.
	 *
	 * @param filter A collection of keywords used to obtain the related document keywords.
	 * @return The keyword map.
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	Map<String, Integer> getKeywordMap(String token, List<String> filter) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get the documents within a category
	 *
	 * @param categoryId The category id (UUID)
	 * @return A Collection of documents in the category
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Document> getCategorizedDocuments(String token, String categoryId) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get the folders within a category
	 *
	 * @param categoryId The category id (UUID)
	 * @return A Collection of folders in the category
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Folder> getCategorizedFolders(String token, String categoryId) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get the mails within a category
	 *
	 * @param categoryId The category id (UUID)
	 * @return A Collection of mails in the category
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Mail> getCategorizedMails(String token, String categoryId) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get the documents with a keyword
	 *
	 * @param keyword The keyword
	 * @return A Collection of documents with the keyword
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Document> getDocumentsByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get the folders with a keyword
	 *
	 * @param keyword The keyword
	 * @return A Collection of folders with the keyword
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Folder> getFoldersByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Get the mails with a keyword
	 *
	 * @param keyword The keyword
	 * @return A Collection of mails with the keyword
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Mail> getMailsByKeyword(String token, String keyword) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Get the documents with a property value
	 *
	 * @param value The property value
	 * @return A Collection of documents with the property value
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Document> getDocumentsByPropertyValue(String token, String group, String property, String value) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get the folders with a property value
	 *
	 * @param property The property value
	 * @return A Collection of folders with the property value
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Folder> getFoldersByPropertyValue(String token, String group, String property, String value) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Get the mails with a property value
	 *
	 * @param property The property value
	 * @return A Collection of mails with the property value
	 * @throws RepositoryException If there is any general repository problem or the query fails.
	 */
	List<Mail> getMailsByPropertyValue(String token, String group, String property, String value) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Performs a simple search using on GQL language.
	 *
	 * @see http://jackrabbit.apache.org/api/1.6/org/apache/jackrabbit/commons/query/GQL.html
	 * @param statement The simple search in GQL language.
	 * @return A collection of documents.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	List<QueryResult> findSimpleQuery(String token, String statement) throws AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Performs a simple search using GQL languahe. Paginated version.
	 *
	 * @see http://jackrabbit.apache.org/api/1.6/org/apache/jackrabbit/commons/query/GQL.html
	 * @param statement The simple search in GQL language.
	 * @param offset Query result list offset.
	 * @param limit Query result list limit.
	 * @return A result set with the total of the results and a collection of document from the resulting query
	 *         statement.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	ResultSet findSimpleQueryPaginated(String token, String statement, int offset, int limit) throws AccessDeniedException,
			RepositoryException, DatabaseException;

	/**
	 * Find documents like a given one.
	 * @param uuid Uuid of the document to find other similar.
	 * @param maxResults Maximum number of returned documents. 
	 * @return A result set with the total of the results and a collection of document from the resulting query
	 *         statement.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	ResultSet findMoreLikeThis(String token, String uuid, int maxResults) throws AccessDeniedException, RepositoryException,
			DatabaseException;
}
