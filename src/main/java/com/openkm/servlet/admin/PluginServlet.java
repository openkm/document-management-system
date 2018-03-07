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

package com.openkm.servlet.admin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.automation.Action;
import com.openkm.automation.Validation;
import com.openkm.core.DatabaseException;
import com.openkm.dao.AutomationDAO;
import com.openkm.dao.PluginDAO;
import com.openkm.util.PluginUtils;
import com.openkm.util.WebUtils;

import net.xeoh.plugins.base.Plugin;

/**
 * Plugin servlet
 */
@WebServlet("/admin/Plugin")
public class PluginServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(PluginServlet.class);
	private String[] pluginList = { Action.class.getCanonicalName(), Validation.class.getCanonicalName()};

	/**
	 *
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("reloadRegisteredList")) {
				String pluginSelected = WebUtils.getString(request, "pluginToSelect");
				reloadPluginList(pluginSelected);
				pluginList(request, response, pluginSelected);
			} else if (action.equals("changeStatus")) {
				String className = WebUtils.getString(request, "className");
				String pluginSelected = WebUtils.getString(request, "pluginToSelect");
				PluginDAO.getInstance().changeStatus(className);
				reloadPluginList(pluginSelected);
				pluginList(request, response, pluginSelected);
			} else {
				String pluginSelected = WebUtils.getString(request, "pluginToSelect");
				pluginList(request, response, pluginSelected);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Reload Plugin
	 */
	private void reloadPluginList(String pluginSelected)
			throws ServletException, IOException, DatabaseException, URISyntaxException {
		log.debug("reloadPluginList({})", pluginSelected);

		if (null == pluginSelected || pluginSelected.isEmpty()) {
			pluginSelected = Action.class.getCanonicalName();
		}

		if (Validation.class.getCanonicalName().equalsIgnoreCase(pluginSelected)) {
			AutomationDAO.getInstance().findValidations(true);
		} else {
			AutomationDAO.getInstance().findActions(true);
		}

		log.debug("reloadPluginList: void");
	}

	/**
	 * Plugin list
	 */
	@SuppressWarnings("unchecked")
	private void pluginList(HttpServletRequest request, HttpServletResponse response, String pluginSelected)
			throws ServletException, IOException, DatabaseException, URISyntaxException {
		log.debug("pluginList({})", pluginSelected);
		ServletContext sc = getServletContext();
		List<Plugin> pluginsLoaded;
		List<Plugin> plugins;

		if (null == pluginSelected || pluginSelected.isEmpty()) {
			pluginSelected = Action.class.getCanonicalName();
		}

		if (Validation.class.getCanonicalName().equalsIgnoreCase(pluginSelected)) {
			plugins = (List<Plugin>) (List<?>) PluginUtils.getAllPlugins(new URI(AutomationDAO.PLUGIN_URI),
					Validation.class);
			pluginsLoaded = (List<Plugin>) (List<?>) AutomationDAO.getInstance().findValidations(false);
			Collections.sort(plugins, OrderByClassName.getInstance());		
		} else {
			plugins = (List<Plugin>) (List<?>) PluginUtils.getAllPlugins(new URI(AutomationDAO.PLUGIN_URI),
					Action.class);
			pluginsLoaded = (List<Plugin>) (List<?>) AutomationDAO.getInstance().findActions(false);
			Collections.sort(plugins, OrderByClassName.getInstance());
		}

		List<LoadedPlugin> loadedPlugins = calculateLoadedPlugins(plugins, pluginsLoaded);
		sc.setAttribute("showReloadButton", showReloadButton(loadedPlugins));
		sc.setAttribute("plugins", loadedPlugins);
		sc.setAttribute("pluginStatus", PluginDAO.getInstance().findAll());
		sc.setAttribute("pluginList", pluginList);
		sc.setAttribute("pluginSelected", pluginSelected);
		sc.getRequestDispatcher("/admin/plugin_list.jsp").forward(request, response);
		log.debug("registeredList: void");
	}

	private boolean showReloadButton(List<LoadedPlugin> loadedPlugins) {
		boolean show = false;
		for (LoadedPlugin plugin : loadedPlugins) {
			if (!plugin.isLoaded()) {
				show = true;
				break;
			}
		}
		return show;
	}

	private List<LoadedPlugin> calculateLoadedPlugins(List<Plugin> plugins, List<Plugin> loadedPlugins) {
		List<LoadedPlugin> loadedPluginsList = new ArrayList<>();

		for (Plugin plugin : plugins) {
			boolean loaded = false;
			try {
				com.openkm.dao.bean.Plugin found = PluginDAO.getInstance()
						.findByPk(plugin.getClass().getCanonicalName());
				if (!found.getActive()) {
					// This is because not active plugins are like not loaded
					// but they are loaded
					loaded = true;
				}
			} catch (DatabaseException e) {
				// Do nothing. A lot of elements are not in database. They are
				// only loaded when are disabled so if it's not
				// in the database it could be loaded.
			}

			if (!loaded) {
				for (Plugin loadedPlugin : loadedPlugins) {
					if (loadedPlugin.getClass().getName().equals(plugin.getClass().getName())) {
						loaded = true;
						break;
					}
				}
			}

			loadedPluginsList.add(new LoadedPlugin(loaded, plugin));
		}

		return loadedPluginsList;
	}

	/**
	 * Comparator to order plugin by names.
	 *
	 * @author agallego
	 */
	private static class OrderByClassName implements Comparator<Plugin> {
		private static final Comparator<Plugin> INSTANCE = new OrderByClassName();

		public static Comparator<Plugin> getInstance() {
			return INSTANCE;
		}

		@Override
		public int compare(Plugin arg0, Plugin arg1) {
			String value0 = arg0.getClass().getCanonicalName();
			String value1 = arg1.getClass().getCanonicalName();

			return value0.compareTo(value1);
		}
	}

	public class LoadedPlugin {
		private boolean loaded;
		private Plugin plugin;

		public LoadedPlugin(boolean loaded, Plugin plugin) {
			this.loaded = loaded;
			this.plugin = plugin;
		}

		public boolean isLoaded() {
			return loaded;
		}

		public Plugin getPlugin() {
			return plugin;
		}
	}
}