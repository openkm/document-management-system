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

public class ProfileStack implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean taxonomyVisible;
	private boolean categoriesVisible;
	private boolean thesaurusVisible;
	private boolean templatesVisible;
	private boolean personalVisible;
	private boolean mailVisible;
	private boolean metadataVisible;
	private boolean trashVisible;

	public boolean isTaxonomyVisible() {
		return taxonomyVisible;
	}

	public void setTaxonomyVisible(boolean taxonomyVisible) {
		this.taxonomyVisible = taxonomyVisible;
	}

	public boolean isCategoriesVisible() {
		return categoriesVisible;
	}

	public void setCategoriesVisible(boolean categoriesVisible) {
		this.categoriesVisible = categoriesVisible;
	}

	public boolean isThesaurusVisible() {
		return thesaurusVisible;
	}

	public void setThesaurusVisible(boolean thesaurusVisible) {
		this.thesaurusVisible = thesaurusVisible;
	}

	public boolean isTemplatesVisible() {
		return templatesVisible;
	}

	public void setTemplatesVisible(boolean templatesVisible) {
		this.templatesVisible = templatesVisible;
	}

	public boolean isPersonalVisible() {
		return personalVisible;
	}

	public void setPersonalVisible(boolean personalVisible) {
		this.personalVisible = personalVisible;
	}

	public boolean isMailVisible() {
		return mailVisible;
	}

	public void setMailVisible(boolean mailVisible) {
		this.mailVisible = mailVisible;
	}

	public boolean isMetadataVisible() {
		return metadataVisible;
	}

	public void setMetadataVisible(boolean metadataVisible) {
		this.metadataVisible = metadataVisible;
	}

	public boolean isTrashVisible() {
		return trashVisible;
	}

	public void setTrashVisible(boolean trashVisible) {
		this.trashVisible = trashVisible;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("taxonomyVisible=");
		sb.append(taxonomyVisible);
		sb.append(", categoriesVisible=");
		sb.append(categoriesVisible);
		sb.append(", thesaurusVisible=");
		sb.append(thesaurusVisible);
		sb.append(", templatesVisible=");
		sb.append(templatesVisible);
		sb.append(", personalVisible=");
		sb.append(personalVisible);
		sb.append(", mailVisible=");
		sb.append(mailVisible);
		sb.append(", metadataVisible=");
		sb.append(metadataVisible);
		sb.append(", trashVisible=");
		sb.append(trashVisible);
		sb.append("}");
		return sb.toString();
	}
}
