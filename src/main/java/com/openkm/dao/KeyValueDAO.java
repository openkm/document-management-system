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

package com.openkm.dao;

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.KeyValue;
import com.openkm.util.DatabaseMetadataUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class KeyValueDAO {
	private static Logger log = LoggerFactory.getLogger(KeyValueDAO.class);

	private KeyValueDAO() {
	}

	/**
	 * Find key values
	 */
	public static List<KeyValue> getKeyValues(String query) throws DatabaseException {
		log.debug("getKeyValues({})", query);
		List<KeyValue> ret = new ArrayList<KeyValue>();
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(query);

			for (Object obj : q.list()) {
				if (obj instanceof Object[]) {
					Object[] ao = (Object[]) obj;
					KeyValue kv = new KeyValue();
					kv.setKey(String.valueOf(ao[0]));
					kv.setValue(String.valueOf(ao[1]));
					ret.add(kv);
				}
			}

			log.debug("getKeyValues: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find key values
	 */
	public static List<KeyValue> getKeyValues(List<String> tables, String query) throws DatabaseException {
		log.debug("getKeyValues({}, {})", tables, query);
		String realQuery = DatabaseMetadataUtils.replaceVirtual(tables, query);
		List<KeyValue> ret = getKeyValues(realQuery);
		log.debug("getKeyValues: {}", ret);
		return ret;
	}

	/**
	 * Find key values
	 */
	public static List<KeyValue> getKeyValues(String table, String query) throws DatabaseException {
		log.debug("getKeyValues({}, {})", table, query);
		List<String> tables = new ArrayList<String>();
		tables.add(table);
		List<KeyValue> ret = getKeyValues(tables, query);
		log.debug("getKeyValues: {}", ret);
		return ret;
	}
}
