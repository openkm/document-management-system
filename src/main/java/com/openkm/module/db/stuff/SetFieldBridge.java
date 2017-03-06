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

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author pavila
 */
public class SetFieldBridge implements FieldBridge {
	private static Logger log = LoggerFactory.getLogger(SetFieldBridge.class);

	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		if (value instanceof Set<?>) {
			@SuppressWarnings("unchecked")
			Set<String> set = (Set<String>) value;

			for (String elto : set) {
				if ("keywords".equals(name)) {
					name = "keyword";
				} else if ("categories".equals(name)) {
					name = "category";
				} else if ("subscriptors".equals(name)) {
					name = "subscriptor";
				}

				log.debug("Added field '{}' with value '{}'", name, elto);
				luceneOptions.addFieldToDocument(name, elto, document);
			}
		} else {
			log.warn("IllegalArgumentException: Support only Set<String>");
			throw new IllegalArgumentException("Support only Set<String>");
		}
	}
}
