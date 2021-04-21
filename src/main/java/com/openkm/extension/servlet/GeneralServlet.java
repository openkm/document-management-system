/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.extension.servlet;

import com.openkm.core.DatabaseException;
import com.openkm.dao.ConfigDAO;
import com.openkm.extension.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * GeneralServlet
 *
 * @author sochoa
 */
public class GeneralServlet extends OKMRemoteServiceServlet implements OKMGeneralService {
	private static final Logger log = LoggerFactory.getLogger(GeneralServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public List<String> getConfigParam(String key) throws OKMException {
		updateSessionManager();
		List<String> params;

		try {
			params = ConfigDAO.getList(key, "");
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMExtensionGeneralService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		return params;
	}
}
