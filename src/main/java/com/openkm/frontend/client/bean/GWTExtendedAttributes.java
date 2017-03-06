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

package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author jllort
 */
public class GWTExtendedAttributes implements IsSerializable {
	private boolean categories = false;
	private boolean keywords = false;
	private boolean propertyGroups = false;
	private boolean notes = false;
	private boolean wiki = false;

	public boolean isCategories() {
		return categories;
	}

	public void setCategories(boolean categories) {
		this.categories = categories;
	}

	public boolean isKeywords() {
		return keywords;
	}

	public void setKeywords(boolean keywords) {
		this.keywords = keywords;
	}

	public boolean isPropertyGroups() {
		return propertyGroups;
	}

	public void setPropertyGroups(boolean propertyGroups) {
		this.propertyGroups = propertyGroups;
	}

	public boolean isNotes() {
		return notes;
	}

	public void setNotes(boolean notes) {
		this.notes = notes;
	}

	public boolean isWiki() {
		return wiki;
	}

	public void setWiki(boolean wiki) {
		this.wiki = wiki;
	}
}
