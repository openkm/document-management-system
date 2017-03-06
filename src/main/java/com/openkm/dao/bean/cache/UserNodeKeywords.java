package com.openkm.dao.bean.cache;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "OKM_USER_NODE")
public class UserNodeKeywords implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "UN_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "UN_USER", length = 64)
	private String user;

	@Column(name = "UN_NODE", length = 64)
	private String node;

	@ElementCollection
	@Column(name = "UNK_NAME")
	@CollectionTable(name = "OKM_USER_NODE_KEYWORD", joinColumns = {@JoinColumn(name = "UNK_ID")})
	private Set<String> keywords = new HashSet<String>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", user=");
		sb.append(user);
		sb.append(", node=");
		sb.append(node);
		sb.append(", keywords=");
		sb.append(keywords);
		sb.append("}");
		return sb.toString();
	}
}
