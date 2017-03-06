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

package com.openkm.extension.servlet;

import com.openkm.core.*;
import com.openkm.extension.dao.StapleGroupDAO;
import com.openkm.extension.dao.bean.Staple;
import com.openkm.extension.dao.bean.StapleGroup;
import com.openkm.extension.frontend.client.service.OKMStaplingService;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTStapleGroup;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet Class
 */
public class StaplingServlet extends OKMRemoteServiceServlet implements OKMStaplingService {
	private static Logger log = LoggerFactory.getLogger(StaplingServlet.class);
	private static final long serialVersionUID = 395857404418870245L;

	@Override
	public String create(String user, String nodeUuid, String type, String nodeUuid2, String type2)
			throws OKMException {
		log.debug("create({}, {})", user, nodeUuid);
		updateSessionManager();
		StapleGroup stapleGroup = new StapleGroup();
		stapleGroup.setUser(user);

		try {
			// Creating stapling group
			long id = StapleGroupDAO.create(stapleGroup);

			// Adding stapling elements
			stapleGroup = StapleGroupDAO.findByPk(id);

			Staple staple = new Staple();
			staple.setNode(nodeUuid);
			staple.setType(type);
			stapleGroup.getStaples().add(staple); // Add first

			staple = new Staple();
			staple.setNode(nodeUuid2);
			staple.setType(type2);
			stapleGroup.getStaples().add(staple); // Add second

			StapleGroupDAO.update(stapleGroup); // Update
			return String.valueOf(id);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void add(String id, String nodeUuid, String type) throws OKMException {
		log.debug("add({}, {})", id, nodeUuid);
		updateSessionManager();
		try {
			StapleGroup stapleGroup = StapleGroupDAO.findByPk(new Long(id).longValue());
			boolean found = false;

			for (Staple st : stapleGroup.getStaples()) {
				if (st.getNode().equals(nodeUuid)) {
					found = true;
					break;
				}
			}

			// Only we add if document not exists
			if (!found) {
				Staple staple = new Staple();
				staple.setNode(nodeUuid);
				staple.setType(type);
				stapleGroup.getStaples().add(staple); // Add first
				StapleGroupDAO.update(stapleGroup); // Update
			}
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_NumberFormat), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public List<GWTStapleGroup> getAll(String uuid) throws OKMException {
		log.debug("getAll({})", uuid);
		updateSessionManager();
		List<GWTStapleGroup> stapList = new ArrayList<GWTStapleGroup>();

		try {
			for (StapleGroup sg : StapleGroupDAO.findAll(uuid)) {
				stapList.add(GWTUtil.copy(sg));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		}

		return stapList;
	}

	@Override
	public void removeAllStapleByUuid(String nodeUuid) throws OKMException {
		log.debug("removeAllStapleByUuid({})", nodeUuid);
		updateSessionManager();
		try {
			List<Long> idToDelete = new ArrayList<Long>();

			for (StapleGroup sg : StapleGroupDAO.findAll(nodeUuid)) {
				for (Staple staple : sg.getStaples()) {
					if (staple.getNode().equals(nodeUuid)) {
						idToDelete.add(new Long(staple.getId()));
					}
				}
			}

			for (Long id : idToDelete) {
				StapleGroupDAO.deleteStaple(id.longValue());
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Repository), e.getMessage());
		}
	}

	@Override
	public void remove(String id) throws OKMException {
		log.debug("remove({})", id);
		updateSessionManager();
		try {
			StapleGroupDAO.delete(new Long(id).longValue());
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_NumberFormat), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void removeStaple(String id) throws OKMException {
		log.debug("removeStaple({})", id);
		updateSessionManager();
		try {
			StapleGroupDAO.deleteStaple(new Long(id).longValue());
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_NumberFormat), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
}
