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
import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.CronTabDAO;
import com.openkm.dao.bean.CronTab;
import com.openkm.module.common.CommonAuthModule;
import com.openkm.principal.PrincipalAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronTabUtils {
	private static Logger log = LoggerFactory.getLogger(CronTabUtils.class);

	/**
	 * Create internal cron tasks
	 */
	public static void createOrUpdate(String name, String expression, String content) throws DatabaseException,
			PrincipalAdapterException {
		log.debug("createOrUpdate({}, {}, {})", new Object[]{name, expression, content});
		CronTab ct = CronTabDAO.findByName(name);

		if (ct == null) {
			String mail = CommonAuthModule.getMail(Config.ADMIN_USER);
			
			ct = new CronTab();
			ct.setActive(true);
			ct.setExpression(expression);
			ct.setFileContent(SecureStore.b64Encode(content.getBytes()));
			ct.setFileMime(MimeTypeConfig.MIME_BSH);
			ct.setFileName(toFileName(name) + ".bsh");
			ct.setName(name);

			if (mail != null && !mail.equals("")) {
				ct.setMail(mail);
			} else {
				ct.setMail(Config.DEFAULT_CRONTAB_MAIL);
			}

			CronTabDAO.create(ct);
		} else {
			ct.setFileContent(SecureStore.b64Encode(content.getBytes()));
			ct.setFileMime(MimeTypeConfig.MIME_BSH);
			ct.setFileName(toFileName(name) + ".bsh");

			CronTabDAO.update(ct);
		}

		log.debug("createOrUpdate: void");
	}

	/**
	 * Change "Text Extractor Worker" or "Text extractor worker" to "TextExtractorWorker".
	 */
	private static String toFileName(String str) {
		StringBuilder sb = new StringBuilder();
		boolean toUpper = true;

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if (c == ' ') {
				toUpper = true;
				continue;
			} else if (toUpper) {
				sb.append(Character.toUpperCase(c));
				toUpper = false;
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}
}
