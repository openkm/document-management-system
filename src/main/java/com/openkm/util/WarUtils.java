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

package com.openkm.util;

import com.openkm.bean.AppVersion;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class WarUtils {
	private static Logger log = LoggerFactory.getLogger(WarUtils.class);
	private static AppVersion appVersion = new AppVersion();

	/**
	 *
	 */
	public static AppVersion getAppVersion() {
		return appVersion;
	}

	/**
	 *
	 */
	public static synchronized void setAppVersion(AppVersion newAppVersion) {
		appVersion = newAppVersion;
	}

	/**
	 *
	 */
	public static synchronized void readAppVersion(ServletContext sc) {
		String appServerHome = sc.getRealPath("/");
		File manifestFile = new File(appServerHome, "WEB-INF/classes/version.properties");
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(manifestFile);
			Properties prop = new Properties();
			prop.load(fis);
			String okmVersion = prop.getProperty("okm.version");
			String okmBuild = prop.getProperty("okm.build");
			log.info("okm.version: " + okmVersion);
			log.info("okm.build: " + okmBuild);
			appVersion = AppVersion.parse(okmVersion);

			if (okmBuild != null) {
				appVersion.setBuild(okmBuild);
			}
		} catch (IOException e) {
			log.warn(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
}
