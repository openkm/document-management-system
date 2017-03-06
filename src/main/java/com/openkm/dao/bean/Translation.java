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

package com.openkm.dao.bean;

import java.io.Serializable;

/**
 * Translation
 *
 * @author jllort
 */
public class Translation implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String MODULE_FRONTEND = "frontend";
	public static final String MODULE_EXTENSION = "extension";
	public static final String MODULE_MOBILE = "mobile";
	public static final String MODULE_ADMINISTRATION = "administration";

	private TranslationId translationId = new TranslationId();
	private String text = "";

	public TranslationId getTranslationId() {
		return translationId;
	}

	public void setTranslationId(TranslationId translationId) {
		this.translationId = translationId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("translationId=");
		sb.append(translationId);
		sb.append(", text=");
		sb.append(text);
		sb.append("}");
		return sb.toString();
	}
}