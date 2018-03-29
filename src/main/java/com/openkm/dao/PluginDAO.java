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
package com.openkm.dao;

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sochoa
 */
public class PluginDAO extends GenericDAO<Plugin, String> {
	private static Logger log = LoggerFactory.getLogger(PluginDAO.class);
	private static PluginDAO single = new PluginDAO();

	private PluginDAO() {
	}

	public static PluginDAO getInstance() {
		return single;
	}

	/**
	 * Change Status plugin.
	 *
	 * @param pluginId
	 * @throws DatabaseException
	 */
	public void changeStatus(String pluginId) throws DatabaseException {
		log.debug("changeStatus({})", pluginId);
		Plugin plugin;

		try {
			plugin = this.findByPk(pluginId);
			plugin.setActive(!plugin.getActive());
			update(plugin);
		} catch (DatabaseException ex) {
			// In not in bbdd active==true so if add new active=false
			plugin = new Plugin();
			plugin.setClassName(pluginId);
			plugin.setActive(false);
			create(plugin);
		}

		log.debug("changeStatus: void");
	}
}
