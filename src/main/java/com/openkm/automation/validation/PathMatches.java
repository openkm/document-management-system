/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2017  Gustavo del Dago
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

import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationUtils;
import com.openkm.automation.Validation;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class PathMatches implements Validation {
	private static Logger log = LoggerFactory.getLogger(NameContains.class);

	@Override
	public boolean isValid(HashMap<String, Object> env, Object... params) {
		try {
			String regEx = AutomationUtils.getString(0, params);
			String path = AutomationUtils.getParentPath(env);
			
			if (path.matches(regEx)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return false;
	}
	
}

