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

package com.openkm.util;

import com.openkm.core.Config;
import com.openkm.dao.PluginDAO;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author pavila
 */
public class PluginUtils {
	private static Logger log = LoggerFactory.getLogger(Config.class);

	/**
	 * Obtain plugin manage
	 */
	@SuppressWarnings("unchecked")
	public static PluginManagerUtil getManager(URI uri) {
		log.debug("getManager({})", uri);
		PluginManager pm = PluginManagerFactory.createPluginManager();
		pm.addPluginsFrom(uri);

		if (EnvironmentDetector.isServerJBoss()) {
			// Look for plugins in com.openkm package
			// Required by JBoss and jspf library bug: OKM-900
			Reflections reflections = new Reflections("com.openkm");
			Set<Class<?>> plugins = reflections.getTypesAnnotatedWith(PluginImplementation.class);

			for (Class<?> plugin : plugins) {
				if (Config.PLUGIN_DEBUG) {
					pm.addPluginsFrom(ClassURI.PLUGIN((Class<? extends Plugin>) plugin), new OptionReportAfter());
				} else {
					pm.addPluginsFrom(ClassURI.PLUGIN((Class<? extends Plugin>) plugin));
				}
			}
		}

		if (Config.PLUGIN_DEBUG) {
			pm.addPluginsFrom(Config.PLUGIN_DIR, new OptionReportAfter());
		} else {
			pm.addPluginsFrom(Config.PLUGIN_DIR);
		}


		return new PluginManagerUtil(pm);
	}

	/**
	 * Get all plugins
	 */
	@SuppressWarnings("unchecked")
	public static <P extends Plugin> Collection<P> getAllPlugins(final URI uri, final Class<P> plugin) {
		Collection<P> plugins = new ArrayList<>();

		for (Plugin plg : getManager(uri).getPlugins(plugin)) {
			plugins.add((P) plg);
		}

		return plugins;
	}

	/**
	 * Get all active plugins
	 */
	@SuppressWarnings("unchecked")
	public static <P extends Plugin> Collection<P> getPlugins(final URI uri, final Class<P> plugin) {
		Collection<P> plugins = new ArrayList<>();

		for (Plugin plg : getManager(uri).getPlugins(plugin)) {
			try {
				com.openkm.dao.bean.Plugin found = PluginDAO.getInstance().findByPk(plg.getClass().getCanonicalName());

				if (found.getActive()) {
					// if active --> add it.
					plugins.add((P) plg);
				}
			} catch (Exception e) {
				//if not found --> add it.
				plugins.add((P) plg);
			}
		}

		return plugins;
	}
}
