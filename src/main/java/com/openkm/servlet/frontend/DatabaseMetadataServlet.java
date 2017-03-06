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
import com.openkm.dao.DatabaseMetadataDAO;
import com.openkm.dao.bean.DatabaseMetadataValue;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMDatabaseMetadataService;
import com.openkm.util.DatabaseMetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DatabaseMetadataServlet
 *
 * @author jllort
 */
public class DatabaseMetadataServlet extends OKMRemoteServiceServlet implements OKMDatabaseMetadataService {
	private static Logger log = LoggerFactory.getLogger(DatabaseMetadataServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public List<Map<String, String>> executeValueQuery(String table, String filter, String order) throws OKMException {
		log.debug("executeValueQuery({}, {}, {})", new Object[]{table, filter, order});
		updateSessionManager();
		List<Map<String, String>> metadataValues = new ArrayList<Map<String, String>>();

		try {
			for (DatabaseMetadataValue dmv : DatabaseMetadataDAO.executeValueQuery(DatabaseMetadataUtils.buildQuery(table, filter, order))) {
				metadataValues.add(DatabaseMetadataUtils.getDatabaseMetadataValueMap(dmv));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_IllegalAccess), e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_InvocationTarget), e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_NoSuchMethod), e.getMessage());
		}

		log.debug("executeValueQuery: {}", metadataValues);
		return metadataValues;
	}

	@Override
	public void updateValue(Map<String, String> map) throws OKMException {
		log.debug("updateValue({})", map);
		updateSessionManager();

		try {
			DatabaseMetadataDAO.updateValue(DatabaseMetadataUtils.getDatabaseMetadataValueByMap(map));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_IllegalAccess), e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_InvocationTarget), e.getMessage());
		}
	}

	@Override
	public Double createValue(Map<String, String> map) throws OKMException {
		log.debug("createValue({})", map);
		updateSessionManager();

		try {
			return new Double(DatabaseMetadataDAO.createValue(DatabaseMetadataUtils.getDatabaseMetadataValueByMap(map)));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_IllegalAccess), e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_InvocationTarget), e.getMessage());
		}
	}

	@Override
	public void deleteValue(Map<String, String> map) throws OKMException {
		log.debug("deleteValue({})", map);
		updateSessionManager();

		try {
			DatabaseMetadataDAO.deleteValue(DatabaseMetadataUtils.getDatabaseMetadataValueByMap(map).getId());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_IllegalAccess), e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_InvocationTarget), e.getMessage());
		}
	}

	@Override
	public List<List<Map<String, String>>> executeMultiValueQuery(List<String> tables, String query) throws OKMException {
		log.debug("executeMultiValueQuery({})", query);
		updateSessionManager();
		List<List<Map<String, String>>> ret = new ArrayList<List<Map<String, String>>>();

		try {
			for (DatabaseMetadataValue[] dmv : DatabaseMetadataDAO.executeMultiValueQuery(DatabaseMetadataUtils.replaceVirtual(tables, query))) {
				List<Map<String, String>> dmvRow = new ArrayList<Map<String, String>>();

				for (int i = 0; i < dmv.length; i++) {
					dmvRow.add(DatabaseMetadataUtils.getDatabaseMetadataValueMap(dmv[i]));
				}

				ret.add(dmvRow);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_IllegalAccess), e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_InvocationTarget), e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_NoSuchMethod), e.getMessage());
		}
		return ret;
	}

	@Override
	public Double getNextSequenceValue(String table, String column) throws OKMException {
		log.debug("getNextSequenceValue({},{})", table, column);
		updateSessionManager();

		try {
			return new Double(DatabaseMetadataDAO.getNextSequenceValue(table, column));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDatabaseMetadataService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
}