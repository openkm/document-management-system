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

package com.openkm.util.cl;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class FilesystemClassLoader extends ClassLoader implements MultipleClassLoader {
	private static Logger log = LoggerFactory.getLogger(FilesystemClassLoader.class);
	private File file = null;

	public FilesystemClassLoader(File file) throws IOException {
		super();
		this.file = file;

	}

	public FilesystemClassLoader(File file, ClassLoader parent) throws IOException {
		super(parent);
		this.file = file;
	}

	/**
	 * Get main class name
	 */
	@Override
	public String getMainClassName() throws IOException {
		log.debug("getMainClassName()");
		File mf = new File(file, "META-INF/MANIFEST.MF");
		FileInputStream fis = null;

		try {
			if (mf.exists() && mf.canRead()) {
				fis = new FileInputStream(mf);
				Manifest manif = new Manifest(fis);
				Attributes attr = manif.getMainAttributes();
				return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}

		return null;
	}

	/**
	 * Find class
	 */
	@Override
	public Class<?> findClass(String className) {
		log.info("findClass({})", className);
		String classFile = className.replace('.', '/').concat(".class");
		File fc = new File(file, classFile);
		FileInputStream fis = null;

		// Check for system class
		try {
			return findSystemClass(className);
		} catch (ClassNotFoundException e) {
			// Ignore
		}

		try {
			if (fc.exists() && fc.canRead()) {
				fis = new FileInputStream(fc);
				byte[] classByte = IOUtils.toByteArray(fis);

				if (classByte != null) {
					return defineClass(className, classByte, 0, classByte.length, null);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fis);
		}

		return null;
	}

	/**
	 * Get resource input stream
	 */
	@Override
	public InputStream getResourceAsStream(String name) {
		log.debug("getResourceAsStream({})", name);
		File fr = new File(file, name);

		try {
			if (fr.exists() && fr.canRead()) {
				return new FileInputStream(fr);
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
}
