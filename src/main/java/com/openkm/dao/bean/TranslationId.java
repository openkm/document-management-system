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
 * @author pavila
 */
public class TranslationId implements Serializable {
	private static final long serialVersionUID = 1L;
	private String module = "";
	private String key = "";
	private String language = "";

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof TranslationId)) return false;

		final TranslationId transId = (TranslationId) o;

		if (!module.equals(transId.getModule()))
			return false;

		if (!key.equals(transId.getKey()))
			return false;

		if (!language.equals(transId.getLanguage()))
			return false;

		return true;
	}

	public int hashCode() {
		return (module + key + language).hashCode();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("module=");
		sb.append(module);
		sb.append(", key=");
		sb.append(key);
		sb.append(", language=");
		sb.append(language);
		sb.append("}");
		return sb.toString();
	}
}