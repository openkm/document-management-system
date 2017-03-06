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

import com.openkm.module.db.stuff.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Indexed
@FullTextFilterDef(name = "readAccess", impl = ReadAccessFilterFactory.class, cache = FilterCacheModeType.NONE)
@Table(name = "OKM_NODE_BASE")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NodeBase implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int MAX_NAME = 256;

	public static final String PARENT_FIELD = "parent";
	public static final String NAME_FIELD = "name";
	public static final String UUID_FIELD = "uuid";

	@Id
	@DocumentId
	@Column(name = "NBS_UUID", length = 64)
	protected String uuid;

	@Column(name = "NBS_PARENT", length = 64)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	// CREATE INDEX IDX_NODE_BASE_PARENT ON OKM_NODE_BASE(NBS_PARENT);
	@org.hibernate.annotations.Index(name = "IDX_NODE_BASE_PARENT")
	protected String parent;

	@Column(name = "NBS_CONTEXT")
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	protected String context;

	@Column(name = "NBS_PATH", length = 1024)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	protected String path;

	@Column(name = "NBS_AUTHOR", length = 64)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	protected String author;

	@Column(name = "NBS_CREATED")
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@CalendarBridge(resolution = Resolution.DAY)
	protected Calendar created;

	@Column(name = "NBS_NAME", length = MAX_NAME)
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@FieldBridge(impl = LowerCaseFieldBridge.class)
	protected String name;

	@Column(name = "NDC_SCRIPTING", nullable = false)
	@Type(type = "true_false")
	protected boolean scripting;

	@Column(name = "NDC_SCRIPT_CODE")
	protected String scriptCode;

	@ElementCollection
	@Column(name = "NSB_SUBSCRIPTOR")
	@CollectionTable(name = "OKM_NODE_SUBSCRIPTOR", joinColumns = {@JoinColumn(name = "NSB_NODE")})
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	protected Set<String> subscriptors = new HashSet<String>();

	@ElementCollection
	@Column(name = "NKW_KEYWORD")
	@CollectionTable(name = "OKM_NODE_KEYWORD", joinColumns = {@JoinColumn(name = "NKW_NODE")})
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	protected Set<String> keywords = new HashSet<String>();

	@ElementCollection
	@Column(name = "NCT_CATEGORY")
	@CollectionTable(name = "OKM_NODE_CATEGORY", joinColumns = {@JoinColumn(name = "NCT_NODE")})
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	protected Set<String> categories = new HashSet<String>();

	@ElementCollection
	@Column(name = "NUP_PERMISSION")
	@MapKeyColumn(name = "NUP_USER", length = 64)
	@CollectionTable(name = "OKM_NODE_USER_PERMISSION", joinColumns = {@JoinColumn(name = "NUP_NODE")})
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@FieldBridge(impl = MapFieldBridge.class)
	protected Map<String, Integer> userPermissions = new HashMap<String, Integer>();

	@ElementCollection
	@Column(name = "NRP_PERMISSION")
	@MapKeyColumn(name = "NRP_ROLE", length = 64)
	@CollectionTable(name = "OKM_NODE_ROLE_PERMISSION", joinColumns = {@JoinColumn(name = "NRP_NODE")})
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@FieldBridge(impl = MapFieldBridge.class)
	protected Map<String, Integer> rolePermissions = new HashMap<String, Integer>();

	@OneToMany(mappedBy = "node", targetEntity = NodeProperty.class, cascade = CascadeType.ALL)
	@Field(index = Index.TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetPropertiesFieldBridge.class)
	protected Set<NodeProperty> properties = new HashSet<NodeProperty>();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null && name.length() > MAX_NAME) {
			this.name = name.substring(0, MAX_NAME);
		} else {
			this.name = name;
		}
	}

	public boolean isScripting() {
		return scripting;
	}

	public void setScripting(boolean scripting) {
		this.scripting = scripting;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public Set<String> getSubscriptors() {
		return subscriptors;
	}

	public void setSubscriptors(Set<String> subscriptors) {
		this.subscriptors = subscriptors;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}

	public Set<NodeProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<NodeProperty> properties) {
		this.properties = properties;
	}

	public Map<String, Integer> getUserPermissions() {
		return userPermissions;
	}

	public void setUserPermissions(Map<String, Integer> userPermissions) {
		this.userPermissions = userPermissions;
	}

	public Map<String, Integer> getRolePermissions() {
		return rolePermissions;
	}

	public void setRolePermissions(Map<String, Integer> rolePermissions) {
		this.rolePermissions = rolePermissions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid=").append(uuid);
		sb.append(", context=").append(context);
		sb.append(", path=").append(path);
		sb.append(", parent=").append(parent);
		sb.append(", author=").append(author);
		sb.append(", name=").append(name);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", subscriptors=").append(subscriptors);
		sb.append(", keywords=").append(keywords);
		sb.append(", categories=").append(categories);
		sb.append(", properties=").append(properties);
		sb.append(", userPermissions=").append(userPermissions);
		sb.append(", rolePermissions=").append(rolePermissions);
		sb.append("}");
		return sb.toString();
	}
}
