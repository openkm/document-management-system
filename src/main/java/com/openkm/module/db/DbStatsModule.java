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

package com.openkm.module.db;

import com.openkm.bean.Repository;
import com.openkm.bean.StatsInfo;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.NodeDocumentDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.module.StatsModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.spring.SecurityHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbStatsModule implements StatsModule {
	private static Logger log = LoggerFactory.getLogger(DbStatsModule.class);

	@Override
	public StatsInfo getDocumentsByContext(String token) throws RepositoryException, DatabaseException {
		return getNodesByContext(token, NodeDocument.class.getSimpleName());
	}

	@Override
	public StatsInfo getFoldersByContext(String token) throws RepositoryException, DatabaseException {
		return getNodesByContext(token, NodeFolder.class.getSimpleName());
	}

	@Override
	public StatsInfo getMailsByContext(String token) throws RepositoryException, DatabaseException {
		return getNodesByContext(token, NodeMail.class.getSimpleName());
	}

	/**
	 * Get stats by node.
	 */
	private StatsInfo getNodesByContext(String token, String nodeType) throws RepositoryException, DatabaseException {
		log.debug("getNodesByContext({}, {})", token, nodeType);
		StatsInfo si = new StatsInfo();
		double[] percents = new double[4];
		long[] sizes = new long[4];

		try {
			if (token != null) {
				SecurityHolder.set(PrincipalUtils.getAuthenticationByToken(token));
			}

			long taxonomyNodes = 0;
			long personalNodes = 0;
			long templatesNodes = 0;
			long trashNodes = 0;

			if (Config.REPOSITORY_STATS_OPTIMIZATION) {
				if (NodeFolder.class.getSimpleName().equals(nodeType)) {
					taxonomyNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.ROOT) - 1;
					personalNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.PERSONAL) - 1
							- NodeBaseDAO.getInstance().getBaseCount(nodeType, "/" + Repository.PERSONAL);
					templatesNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.TEMPLATES) - 1;
					trashNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.TRASH) - 1
							- NodeBaseDAO.getInstance().getBaseCount(nodeType, "/" + Repository.TRASH);
				} else {
					taxonomyNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.ROOT);
					personalNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.PERSONAL);
					templatesNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.TEMPLATES);
					trashNodes = NodeBaseDAO.getInstance().getCount(nodeType, "/" + Repository.TRASH);
				}
			} else {
				if (NodeFolder.class.getSimpleName().equals(nodeType)) {
					taxonomyNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.ROOT, 1);
					personalNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.PERSONAL, 2);
					templatesNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.TEMPLATES, 1);
					trashNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.TRASH, 2);
				} else {
					taxonomyNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.ROOT, 1);
					personalNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.PERSONAL, 1);
					templatesNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.TEMPLATES, 1);
					trashNodes = NodeBaseDAO.getInstance().getSubtreeCount(nodeType, "/" + Repository.TRASH, 1);
				}
			}

			long totalNodes = taxonomyNodes + personalNodes + templatesNodes + trashNodes;
			si.setTotal(totalNodes);

			// Fill sizes
			sizes[0] = taxonomyNodes;
			sizes[1] = personalNodes;
			sizes[2] = templatesNodes;
			sizes[3] = trashNodes;
			si.setSizes(sizes);

			// Compute percents
			percents[0] = (totalNodes > 0) ? ((double) taxonomyNodes / totalNodes) : 0;
			percents[1] = (totalNodes > 0) ? ((double) personalNodes / totalNodes) : 0;
			percents[2] = (totalNodes > 0) ? ((double) templatesNodes / totalNodes) : 0;
			percents[3] = (totalNodes > 0) ? ((double) trashNodes / totalNodes) : 0;
			si.setPercents(percents);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token != null) {
				SecurityHolder.unset();
			}
		}

		log.debug("getNodesByContext: {}", si);
		return si;
	}

	@Override
	public StatsInfo getDocumentsSizeByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getDocumentsSizeByContext({})", token);
		StatsInfo si = new StatsInfo();
		double[] percents = new double[4];
		long[] sizes = new long[4];

		try {
			if (token != null) {
				SecurityHolder.set(PrincipalUtils.getAuthenticationByToken(token));
			}

			long taxonomyDocumentSize = 0;
			long personalDocumentSize = 0;
			long templatesDocumentSize = 0;
			long trashDocumentSize = 0;

			if (Config.REPOSITORY_STATS_OPTIMIZATION) {
				taxonomyDocumentSize = NodeDocumentDAO.getInstance().getSize("/" + Repository.ROOT);
				personalDocumentSize = NodeDocumentDAO.getInstance().getSize("/" + Repository.PERSONAL);
				templatesDocumentSize = NodeDocumentDAO.getInstance().getSize("/" + Repository.TEMPLATES);
				trashDocumentSize = NodeDocumentDAO.getInstance().getSize("/" + Repository.TRASH);
			} else {
				taxonomyDocumentSize = NodeDocumentDAO.getInstance().getSubtreeSize("/" + Repository.ROOT);
				personalDocumentSize = NodeDocumentDAO.getInstance().getSubtreeSize("/" + Repository.PERSONAL);
				templatesDocumentSize = NodeDocumentDAO.getInstance().getSubtreeSize("/" + Repository.TEMPLATES);
				trashDocumentSize = NodeDocumentDAO.getInstance().getSubtreeSize("/" + Repository.TRASH);
			}

			long totalDocumentSize = taxonomyDocumentSize + personalDocumentSize + templatesDocumentSize + trashDocumentSize;
			si.setTotal(totalDocumentSize);

			// Fill sizes
			sizes[0] = taxonomyDocumentSize;
			sizes[1] = personalDocumentSize;
			sizes[2] = templatesDocumentSize;
			sizes[3] = trashDocumentSize;
			si.setSizes(sizes);

			// Compute percents
			percents[0] = (totalDocumentSize > 0) ? ((double) taxonomyDocumentSize / totalDocumentSize) : 0;
			percents[1] = (totalDocumentSize > 0) ? ((double) personalDocumentSize / totalDocumentSize) : 0;
			percents[2] = (totalDocumentSize > 0) ? ((double) templatesDocumentSize / totalDocumentSize) : 0;
			percents[3] = (totalDocumentSize > 0) ? ((double) trashDocumentSize / totalDocumentSize) : 0;
			si.setPercents(percents);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token != null) {
				SecurityHolder.unset();
			}
		}

		log.debug("getDocumentsSizeByContext: {}", si);
		return si;
	}
}
