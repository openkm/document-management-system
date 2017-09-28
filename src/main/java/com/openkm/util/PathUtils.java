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

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtils {
	private static Logger log = LoggerFactory.getLogger(PathUtils.class);

	/**
	 * Get parent node.
	 */
	public static String getParent(String path) {
		log.debug("getParent({})", path);
		int lastSlash = path.lastIndexOf('/');
		String ret = (lastSlash > 0) ? path.substring(0, lastSlash) : "/";
		log.debug("getParent: {}", ret);
		return ret;
	}

	/**
	 * Get node name.
	 */
	public static String getName(String path) {
		log.debug("getName({})", path);
		String ret = path.substring(path.lastIndexOf('/') + 1);
		log.debug("getName: {}", ret);
		return ret;
	}

	/**
	 * Get path depth
	 */
	public static int getDepth(String path) {
		return path.substring(1).split("/").length;
	}

	/**
	 * Get path elements
	 */
	public static String[] getElements(String path) {
		return path.substring(1).split("/");
	}

	/**
	 * Get path context. For example "/okm:root/prueba.txt" -> "/okm:root"
	 */
	public static String getContext(String path) {
		int idx = path.indexOf('/', 1);
		return path.substring(0, idx < 0 ? path.length() : idx);
	}

	/**
	 * Eliminate dangerous chars in node name.
	 * TODO Keep on sync with uploader:com.openkm.applet.Util.escape(String)
	 * TODO Keep on sync with wsImporter:com.openkm.importer.Util.escape(String)
	 */
	public static String escape(String name) {
		String ret = cleanup(name);

		// Fix XSS issues
		ret = Encode.forHtml(ret);

		return ret;
	}

	/**
	 * Eliminate forbidden chars in node name.
	 */
	public static String cleanup(String name) {
		String ret = name.replace("/", "");
		ret = ret.replace("*", "");
		ret = ret.replaceAll("\\s+", " ").trim();
		return ret;
	}

	/**
	 * Encode entities to avoid problems with & and &amp;. For example:
	 * - "/okm:root/a & b" => "/okm:root/a &amp; b"
	 * - "/okm:root/a &amp; b" => "/okm:root/a &amp; b"
	 * - "/okm:root/a <> b" => "/okm:root/a &lt;&gt; b"
	 * - "/okm:root/a &lt;&gt; b" => "/okm:root/a &lt;&gt; b"
	 *
	 * @param path Path or string to encode.
	 * @return The string with the encoded entities.
	 */
	public static String encodeEntities(String path) {
		String tmp = path.replaceAll("&(?![alg#][mt0-9][0-9]?p?;)", "&amp;");
		tmp = tmp.replaceAll("<", "&lt;");
		tmp = tmp.replaceAll(">", "&gt;");
		tmp = tmp.replaceAll("\"", "&#34;");
		tmp = tmp.replaceAll("'", "&#39;");
		return tmp;
	}

	/**
	 * Reverse of encodeEntities. For example:
	 * - "/okm:root/a &amp; b" => "/okm:root/a & b"
	 *
	 * @param path Path or string to decode.
	 * @return The string with the decoded entities.
	 */
	public static String decodeEntities(String path) {
		String tmp = path.replaceAll("&amp;", "&");
		tmp = tmp.replaceAll("&lt;", "<");
		tmp = tmp.replaceAll("&gt;", ">");
		tmp = tmp.replaceAll("&#34;", "\"");
		tmp = tmp.replaceAll("&#39;", "'");
		return tmp;
	}

	/**
	 * Fix context definition. For example "/okm:root" -> "okm_root"
	 */
	public static String fixContext(String context) {
		return context.substring(1).replace(':', '_');
	}

	/**
	 * Test id nodeId is a path or UUID.
	 */
	public static boolean isPath(String nodeId) {
		return nodeId.startsWith("/");
	}

	/**
	 * Test correct path.
	 */
	public static boolean checkPath(String path) {
		if (path == null || path.isEmpty() || !path.startsWith("/okm:")) {
			return false;
		} else {
			return true;
		}
	}
}
