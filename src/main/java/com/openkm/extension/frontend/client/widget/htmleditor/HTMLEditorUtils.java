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

package com.openkm.extension.frontend.client.widget.htmleditor;

/**
 * HTMLEditorUtils
 *
 * @author jllort
 *
 */
public class HTMLEditorUtils {

	/**
	 * getOpenMeetingsLang
	 *
	 * take a look http://www.tinymce.com/i18n/index.php?ctrl=lang&act=download&pr_id=1
	 */
	public static String getTinymceLang(String lang) {
		String lg = "en";

		if (lang.equals("ar-PS")) {
			lg = "ar";
		} else if (lang.equals("nl-BE")) {
			lg = "nl";
		} else if (lang.equals("bs-BS")) {
			lg = "bs";
		} else if (lang.equals("bg-BG")) {
			lg = "bg";
		} else if (lang.equals("bg-BG")) {
			lg = "bg";
		} else if (lang.equals("zh-CN")) {
			lg = "zh-cn";
		} else if (lang.equals("zh-TW")) {
			lg = "zh-tw";
		} else if (lang.equals("cs-CZ")) {
			lg = "cs";
		} else if (lang.equals("dk-DK")) {
			lg = "da";
		} else if (lang.equals("de-DE")) {
			lg = "de";
		} else if (lang.equals("de-AT")) {
			lg = "en"; // NOT exist in tinymce
		} else if (lang.equals("nl-NL")) {
			lg = "nl";
		} else if (lang.equals("en-GB")) {
			lg = "en";
		} else if (lang.equals("en-US")) {
			lg = "en"; // NOT exist in tinymce
		} else if (lang.equals("es-ES")) {
			lg = "es";
		} else if (lang.equals("co-ES")) {
			lg = "es"; // NOT exist in tinymce
		} else if (lang.equals("eu-ES")) {
			lg = "eu";
		} else if (lang.equals("fa-FA")) {
			lg = "fa";
		} else if (lang.equals("fr-FR")) {
			lg = "fr";
		} else if (lang.equals("gl-ES")) {
			lg = "gl";
		} else if (lang.equals("el-GR")) {
			lg = "el";
		} else if (lang.equals("hu-HU")) {
			lg = "hu";
		} else if (lang.equals("id-ID")) {
			lg = "id";
		} else if (lang.equals("it-IT")) {
			lg = "it";
		} else if (lang.equals("jp-JP")) {
			lg = "ja";
		} else if (lang.equals("lv-LV")) {
			lg = "lv";
		} else if (lang.equals("lt-LT")) {
			lg = "lt";
		} else if (lang.equals("mk-MK")) {
			lg = "mk";
		} else if (lang.equals("pt-PT")) {
			lg = "pt";
		} else if (lang.equals("pt-BR")) {
			lg = "pt"; // NOT exist in tinymce
		} else if (lang.equals("ro-RO")) {
			lg = "ro";
		} else if (lang.equals("ru-RU")) {
			lg = "ru";
		} else if (lang.equals("sr-SR")) {
			lg = "sr";
		} else if (lang.equals("sk-SK")) {
			lg = "sk";
		} else if (lang.equals("sv-SE")) {
			lg = "sv";
		} else if (lang.equals("th-TH")) {
			lg = "th";
		} else if (lang.equals("tr-TR")) {
			lg = "tr";
		} else if (lang.equals("zh-VN")) {
			lg = "vi";
		}

		return lg;
	}
}