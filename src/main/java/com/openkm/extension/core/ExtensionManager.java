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

package com.openkm.extension.core;

import com.openkm.core.Config;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Base for implementing core extension managers.
 *
 * @author pavila
 */
public class ExtensionManager {
	private static Logger log = LoggerFactory.getLogger(ExtensionManager.class);
	protected static URI base = new File(Config.HOME_DIR + File.separator + "plugins").toURI();
	private static ExtensionManager em = null;
	private static PluginManager pm = null;

	private ExtensionManager() {
		log.info("Initialize and load plugins...");
		pm = PluginManagerFactory.createPluginManager();

		if (Config.PLUGIN_DEBUG) {
			pm.addPluginsFrom(base, new OptionReportAfter());
		} else {
			pm.addPluginsFrom(base);
		}
	}

	public static synchronized ExtensionManager getInstance() {
		if (em == null) {
			em = new ExtensionManager();
		}

		return em;
	}

	/**
	 * Get plugins
	 */
	public <P extends Plugin> List<P> getPlugins(final Class<P> plugin) {
		PluginManagerUtil pmu = new PluginManagerUtil(pm);
		return new ArrayList<P>(pmu.getPlugins(plugin));
	}

	/**
	 * Reset the loaded plugins and load them again
	 */
	public synchronized void reset() {
		log.info("Resetting extensions...");
		pm.shutdown();
		pm = PluginManagerFactory.createPluginManager();

		if (Config.PLUGIN_DEBUG) {
			pm.addPluginsFrom(base, new OptionReportAfter());
		} else {
			pm.addPluginsFrom(base);
		}
	}

	/**
	 * Shutdown the extension manager
	 */
	public synchronized void shutdown() {
		pm.shutdown();
	}
}
