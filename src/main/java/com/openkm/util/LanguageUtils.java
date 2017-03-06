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

import com.openkm.core.DatabaseException;
import com.openkm.dao.LanguageDAO;
import com.openkm.dao.bean.Language;
import com.openkm.dao.bean.Translation;

import java.util.HashMap;
import java.util.Map;

/**
 * LanguageUtils
 *
 * @author jllort
 *
 */
public class LanguageUtils {

	/**
	 * getTranslations
	 * @throws Exception
	 */
	public static Map<String, String> getTranslations(String lang, String module[]) throws DatabaseException {
		Map<String, String> translations = new HashMap<String, String>();

		// By default english is used to complete non defined translations
		Language baseLang = LanguageDAO.findByPk(Language.DEFAULT);
		Language language = LanguageDAO.findByPk(lang);
		Map<String, String> keys = new HashMap<String, String>();

		// Getting keys
		if (language != null) {
			for (Translation translation : language.getTranslations()) {
				String key = translation.getTranslationId().getModule() + "." + translation.getTranslationId().getKey();
				keys.put(key, translation.getText());
			}
		}

		if (baseLang != null) {
			for (Translation translation : baseLang.getTranslations()) {
				boolean found = false;
				for (int i = 0; i < module.length; i++) {
					if (translation.getTranslationId().getModule().equals(module[i])) {
						found = true;
						break;
					}
				}
				if (found) {
					// Module is added module name as starting translation key
					String key = translation.getTranslationId().getModule() + "." + translation.getTranslationId().getKey();

					if (keys.keySet().contains(key)) {
						translations.put(key, keys.get(key));
					} else {
						// If translation no exist set english translation
						translations.put(key, translation.getText());
					}
				}
			}
		} else {
			throw new DatabaseException("English traslation is mandatory can not be deleted");
		}

		return translations;
	}

}