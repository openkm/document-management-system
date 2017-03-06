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

package com.openkm.module.db.base;

import bsh.EvalError;
import bsh.Interpreter;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseScriptingModule {
	private static Logger log = LoggerFactory.getLogger(BaseScriptingModule.class);

	/**
	 * Check for scripts and evaluate
	 *
	 * @param node Node modified (Document or Folder)
	 * @param user User who generated the modification event
	 * @param eventType Type of modification event
	 */
	public static void checkScripts(String user, String scriptNodeUuid, String eventNodeUuid, String eventType) {
		log.debug("checkScripts({}, {}, {}, {})", new Object[]{user, scriptNodeUuid, eventNodeUuid, eventType});

		try {
			checkScriptsHelper(user, scriptNodeUuid, eventNodeUuid, eventType);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		}

		log.debug("checkScripts: void");
	}

	/**
	 * Check script helper method for recursion.
	 */
	private static void checkScriptsHelper(String user, String scriptNodeUuid, String eventNodeUuid, String eventType)
			throws PathNotFoundException, DatabaseException {
		log.debug("checkScriptsHelper({}, {}, {}, {})", new Object[]{user, scriptNodeUuid, eventNodeUuid, eventType});
		NodeBase scriptNode = NodeBaseDAO.getInstance().findByPk(scriptNodeUuid);

		if (scriptNode.isScripting()) {
			String code = scriptNode.getScriptCode();

			// Evaluate script
			Interpreter i = new Interpreter();
			try {
				i.set("session", user);
				i.set("scriptNode", scriptNodeUuid);
				i.set("eventNode", eventNodeUuid);
				i.set("eventType", eventType);
				i.eval(code);
			} catch (EvalError e) {
				log.warn(e.getMessage(), e);
			}

			// Check for script in parent node
			checkScriptsHelper(user, scriptNode.getParent(), eventNodeUuid, eventType);
		}

		log.debug("checkScriptsHelper: void");
	}
}
