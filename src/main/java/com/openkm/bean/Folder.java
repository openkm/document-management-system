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

package com.openkm.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pavila
 *
 */
@XmlRootElement(name = "folder")
public class Folder extends Node {
	private static final long serialVersionUID = 1L;
	public static final String TYPE = "okm:folder";
	private boolean hasChildren;

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("path=").append(path);
		sb.append(", permissions=").append(permissions);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", hasChildren=").append(hasChildren);
		sb.append(", subscribed=").append(subscribed);
		sb.append(", subscriptors=").append(subscriptors);
		sb.append(", uuid=").append(uuid);
		sb.append(", keywords=").append(keywords);
		sb.append(", categories=").append(categories);
		sb.append(", notes=").append(notes);
		sb.append("}");
		return sb.toString();
	}
}
