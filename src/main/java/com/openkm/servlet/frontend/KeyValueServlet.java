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

import com.openkm.core.DatabaseException;
import com.openkm.dao.KeyValueDAO;
import com.openkm.dao.bean.KeyValue;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTKeyValue;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMKeyValueService;
import com.openkm.util.GWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * KeyValueServlet
 *
 * @author jllort
 *
 */
public class KeyValueServlet extends OKMRemoteServiceServlet implements OKMKeyValueService {
	private static Logger log = LoggerFactory.getLogger(KeyValueServlet.class);
	private static final long serialVersionUID = -7747765621446287017L;

	@Override
	public List<GWTKeyValue> getKeyValues(List<String> tables, String query) throws OKMException {
		log.debug("getKeyValues({},{}})", tables, query);
		updateSessionManager();
		List<GWTKeyValue> keyValues = new ArrayList<GWTKeyValue>();

		try {
			for (KeyValue keyValue : KeyValueDAO.getKeyValues(tables, query)) {
				keyValues.add(GWTUtil.copy(keyValue));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMKeyValueService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		log.debug("executeValueQuery: {}", keyValues);
		return keyValues;
	}
}