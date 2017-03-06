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

package com.openkm.automation.validation;

import com.openkm.automation.AutomationUtils;
import com.openkm.automation.Validation;
import com.openkm.dao.NodeFolderDAO;
import com.openkm.dao.bean.NodeFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Check if the current folder contains a child folder with the designed name.
 * The first and only parameter is an string with the name of the brother folder.
 * A brother folder is a folder with the same parent as this one.
 *
 * @author pavila
 */
public class ContainsFolder implements Validation {
	private static Logger log = LoggerFactory.getLogger(ContainsFolder.class);

	@Override
	public boolean isValid(HashMap<String, Object> env, Object... params) {
		String name = AutomationUtils.getString(0, params);
		String parentUuid = AutomationUtils.getParentUuid(env);

		try {
			List<NodeFolder> childs = NodeFolderDAO.getInstance().findByParent(parentUuid);

			for (NodeFolder nFld : childs) {
				if (nFld.getName().equals(name)) {
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
}
