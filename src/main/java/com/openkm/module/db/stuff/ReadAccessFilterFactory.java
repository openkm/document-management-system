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

package com.openkm.module.db.stuff;

import com.openkm.core.Config;
import com.openkm.dao.SearchDAO;
import com.openkm.spring.PrincipalUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.hibernate.search.annotations.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ReadAccessFilterFactory {
	private static Logger log = LoggerFactory.getLogger(SearchDAO.class);

	@Factory
	public Filter buildFilter() {
		log.debug("buildFilter()");

		if (SearchDAO.SEARCH_LUCENE.equals(Config.SECURITY_SEARCH_EVALUATION)) {
			String user = PrincipalUtils.getUser();
			Set<String> roles = PrincipalUtils.getRoles();

			if (roles.contains(Config.DEFAULT_ADMIN_ROLE)) {
				// An user with AdminRole has total access
				return null;
			} else if (Config.ADMIN_USER.equals(user) || Config.SYSTEM_USER.equals(user)) {
				// An "okmAdmin" or "system" user has total access
				return null;
			} else {
				BooleanQuery query = new BooleanQuery();
				Term termUser = new Term("userPermission", user);
				query.add(new TermQuery(termUser), BooleanClause.Occur.SHOULD);

				for (String role : roles) {
					Term termRole = new Term("rolePermission", role);
					query.add(new TermQuery(termRole), BooleanClause.Occur.SHOULD);
				}

				log.info("buildFilter: {}", query);
				Filter filter = new QueryWrapperFilter(query);
				return filter;
			}
		} else {
			return null;
		}
	}
}
