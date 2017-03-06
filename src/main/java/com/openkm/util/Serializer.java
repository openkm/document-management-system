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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Serializer {
	private static Logger log = LoggerFactory.getLogger(Serializer.class);

	/**
	 * @param obj
	 */
	public static void write(String filename, Object obj) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(Config.HOME_DIR + File.separator + filename + ".ser");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(oos);
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * @param obj
	 */
	public static byte[] write(Object obj) throws IOException {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			baos.flush();
			return baos.toByteArray();
		} finally {
			IOUtils.closeQuietly(oos);
			IOUtils.closeQuietly(baos);
		}
	}

	/**
	 * @param obj
	 */
	public static Object read(String filename) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Object obj = null;

		try {
			fis = new FileInputStream(Config.HOME_DIR + File.separator + filename + ".ser");
			ois = new ObjectInputStream(fis);
			obj = ois.readObject();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(ois);
			IOUtils.closeQuietly(fis);
		}

		return obj;
	}

	/**
	 * @param obj
	 */
	public static Object read(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		try {
			bais = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} finally {
			IOUtils.closeQuietly(ois);
			IOUtils.closeQuietly(bais);
		}
	}
}
