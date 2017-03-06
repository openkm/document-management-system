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

package com.openkm.webdav.resource;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.openkm.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * You should generally avoid using any request information other then that
 * provided in the method arguments. But if you find you need to you can access
 * the request and response objects from HttpManager.request() and
 * HttpManager.response()
 *
 * @author pavila
 */
public class ResourceFactoryImpl implements ResourceFactory {
	private static final Logger log = LoggerFactory.getLogger(ResourceFactoryImpl.class);
	public static final String REALM = "OpenKM";

	@Override
	public Resource getResource(String host, String url) {
		log.debug("getResource({}, {})", host, url);
		Path srcPath = Path.path(url);
		Path path = null;

		if (url.startsWith("/" + Config.CONTEXT + "/webdav")) {
			// STRIP PRECEEDING PATH
			path = srcPath.getStripFirst().getStripFirst();
		} else {
			path = Path.path(url);
		}

		try {
			if (path.isRoot()) {
				log.debug("ROOT");
				return new RootResource(srcPath);
			} else {
				return ResourceUtils.getNode(srcPath, path.toPath());
			}
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException: " + e.getMessage());
		} catch (AccessDeniedException e) {
			log.error("AccessDeniedException: " + e.getMessage());
		} catch (RepositoryException e) {
			log.error("RepositoryException: " + e.getMessage());
		} catch (DatabaseException e) {
			log.error("DatabaseException: " + e.getMessage());
		}

		return null;
	}
}
