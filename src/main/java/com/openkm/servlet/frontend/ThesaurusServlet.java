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

package com.openkm.servlet.frontend;

import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.service.OKMThesaurusService;
import com.openkm.kea.RDFREpository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Servlet Class
 */
public class ThesaurusServlet extends OKMRemoteServiceServlet implements OKMThesaurusService {
	private static Logger log = LoggerFactory.getLogger(ThesaurusServlet.class);
	private static final long serialVersionUID = -4436438730167948558L;

	@Override
	public List<String> getKeywords(final String filter) throws OKMException {
		log.debug("getKeywords({})", filter);
		List<String> keywordList = new ArrayList<String>();
		List<String> keywords = RDFREpository.getInstance().getKeywords();
		int index = -1;
		int size = keywords.size();
		updateSessionManager();

		// Keywords list is an ordered list
		String value = (String) CollectionUtils.find(keywords, new Predicate() {
			@Override
			public boolean evaluate(Object arg0) {
				String key = (String) arg0;
				if (key.toLowerCase().startsWith(filter)) {
					return true;
				} else {
					return false;
				}
			}
		});

		if (value != null) {
			index = keywords.indexOf(value);
		}

		if (index >= 0) {
			while (size > index && keywords.get(index).toLowerCase().startsWith(filter)) {
				keywordList.add(keywords.get(index));
				index++;
			}
		}

		log.debug("getKeywords: {}", keywordList);
		return keywordList;
	}
}
