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
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class TemplateUtils {
	private static Logger log = LoggerFactory.getLogger(Config.class);
	private static Configuration cfg = null;

	/**
	 * Singleton FreeMaker configuration
	 */
	public static synchronized Configuration getConfig() {
		if (cfg == null) {
			try {
				cfg = new Configuration();
				cfg.setDirectoryForTemplateLoading(new File(Config.HOME_DIR));
				cfg.setObjectWrapper(new DefaultObjectWrapper());
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return cfg;
	}

	/**
	 * Check for template existence
	 */
	public static boolean templateExists(String name) {
		try {
			getConfig().getTemplate(name);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Quick replace utility function
	 */
	public static String replace(String name, String template, Map<String, Object> model) throws
			IOException, TemplateException {
		StringReader sr = new StringReader(template);
		Template tpl = new Template(name, sr, cfg);
		StringWriter sw = new StringWriter();
		tpl.process(model, sw);
		sw.close();
		sr.close();
		return sw.toString();
	}

	/**
	 * Quick replace utility function
	 */
	public static void replace(String name, InputStream input, Map<String, Object> model,
	                           OutputStream out) throws IOException, TemplateException {
		InputStreamReader isr = new InputStreamReader(input);
		Template tpl = new Template(name, isr, cfg);
		OutputStreamWriter osw = new OutputStreamWriter(out);
		tpl.process(model, osw);
		osw.close();
		isr.close();
	}
}
