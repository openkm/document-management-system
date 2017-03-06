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

import com.openkm.core.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ConfigUtils {

	/**
	 * Read a boolean property.
	 */
	public static boolean getBooleanProperty(Properties config, String key, boolean defaultValue) {
		String property = config.getProperty(key, String.valueOf(defaultValue));
		return Boolean.parseBoolean(property);
	}

	/**
	 * Resource locator helper
	 *
	 * http://www.thinkplexx.com/learn/howto/java/system/java-resource-loading-explained-absolute-and-relative-names-difference-between-classloader-and-class-resource-loading
	 */
	public static InputStream getResourceAsStream(String resource) throws IOException {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = null;

		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}

		if (stream == null) {
			stream = Config.class.getResourceAsStream(resource);
		}

		if (stream == null) {
			stream = Config.class.getClassLoader().getResourceAsStream(stripped);
		}

		if (stream == null) {
			throw new IOException(resource + " not found");
		}

		return stream;
	}

	/**
	 * Resource locator helper
	 */
	public static List<String> getResources(String resourceBase) throws URISyntaxException, IOException {
		String stripped = resourceBase.startsWith("/") ? resourceBase.substring(1) : resourceBase;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		List<String> resources = null;

		if (classLoader != null) {
			resources = getResourceListing(classLoader, stripped);
		}

		if (resources == null) {
			resources = getResourceListing(Config.class.getClassLoader(), stripped);
		}

		if (resources == null) {
			throw new IOException(resourceBase + " not found");
		}

		return resources;
	}

	public static List<String> getResourceListing(ClassLoader cl, String path) throws URISyntaxException, IOException {
		URL dirUrl = cl.getResource(path);

		if (dirUrl != null && dirUrl.getProtocol().equals("file")) {
			/* A file path: easy enough */
			return Arrays.asList(new File(dirUrl.toURI()).list());
		}

		if (dirUrl == null) {
		    /*
	         * In case of a jar file, we can't actually find a directory.
	         * Have to assume the same jar as clazz.
	         */
			// String me = clazz.getName().replace(".", "/")+".class";
			// dirUrl = clazz.getClassLoader().getResource(me);
		}

		if (dirUrl != null && dirUrl.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!")); // strip out only the JAR
			// file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory

			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();

				if (name.startsWith(path)) { // filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");

					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}

					result.add(entry);
				}
			}

			return new ArrayList<String>(result);
		}

		throw new UnsupportedOperationException("Cannot list files for URL " + dirUrl);
	}

	/**
	 * Compile wildcard to regexp
	 */
	public static String wildcard2regexp(String wildcard) {
		StringBuffer sb = new StringBuffer("^");

		for (int i = 0; i < wildcard.length(); i++) {
			char c = wildcard.charAt(i);

			switch (c) {
				case '.':
					sb.append("\\.");
					break;

				case '*':
					sb.append(".*");
					break;

				case '?':
					sb.append(".");
					break;

				default:
					sb.append(c);
					break;
			}
		}

		return sb.toString();
	}
}
