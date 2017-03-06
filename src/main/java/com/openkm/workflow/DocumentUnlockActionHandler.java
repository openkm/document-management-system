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

package com.openkm.workflow;

import com.openkm.api.OKMDocument;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pavila
 *
 */
public class DocumentUnlockActionHandler implements ActionHandler {
	private static Logger log = LoggerFactory.getLogger(DocumentUnlockActionHandler.class);
	private static final long serialVersionUID = -4813518815259981308L;

	/**
	 *
	 */
	public DocumentUnlockActionHandler() {
	}

	@Override
	public void execute(ExecutionContext ctx) throws Exception {
		String path = (String) ctx.getContextInstance().getVariable("path");
		log.info("Path: " + path);

		try {
			OKMDocument.getInstance().unlock(null, path);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage());
		}
	}
}
