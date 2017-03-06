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

package com.openkm.dao.bean;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "OKM_MIME_TYPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MimeType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MT_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "MT_NAME", length = 128, unique = true)
	private String name;

	@Column(name = "MT_DESCRIPTION", length = 128, unique = true)
	private String description;

	@Column(name = "MT_IMAGE_CONTENT")
	@Lob
	@Type(type = "org.hibernate.type.StringClobType")
	private String imageContent;

	@Column(name = "MT_IMAGE_MIME", length = 32)
	private String imageMime;

	@Column(name = "MT_SEARCH", nullable = false)
	@Type(type = "true_false")
	private boolean search = false;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "MTE_NAME")
	@CollectionTable(name = "OKM_MIME_TYPE_EXTENSION", joinColumns = {@JoinColumn(name = "MTE_ID")})
	private Set<String> extensions = new HashSet<String>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isSearch() {
		return search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", description=").append(description);
		sb.append(", search=").append(search);
		sb.append(", imageMime=").append(imageMime);
		sb.append(", imageContent=").append("[BIG]");
		sb.append(", extensions=").append(extensions);
		sb.append("}");
		return sb.toString();
	}
}
