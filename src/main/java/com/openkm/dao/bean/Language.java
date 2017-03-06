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
import java.util.HashSet;
import java.util.Set;

/**
 * Language
 *
 * @author jllort
 */
public class Language implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT = "en-GB";

	private String id;
	private String name;
	private String imageContent;
	private String imageMime;
	private Set<Translation> translations = new HashSet<Translation>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Translation> getTranslations() {
		return translations;
	}

	public void setTranslations(Set<Translation> translations) {
		this.translations = translations;
	}

	public String getImageContent() {
		return imageContent;
	}

	public void setImageContent(String imageContent) {
		this.imageContent = imageContent;
	}

	public String getImageMime() {
		return imageMime;
	}

	public void setImageMime(String imageMime) {
		this.imageMime = imageMime;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", imageMime=").append(imageMime);
		sb.append(", imageContent=").append("[BIG]");
		sb.append(", translations=").append(translations);
		sb.append("}");
		return sb.toString();
	}
}