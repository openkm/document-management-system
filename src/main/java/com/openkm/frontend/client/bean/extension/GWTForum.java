package com.openkm.frontend.client.bean.extension;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * GWTForum
 *
 * @author jllort
 */
public class GWTForum implements IsSerializable {
	private long id;
	private String name;
	private String description;
	private Date date;
	private String lastPostUser;
	private Date lastPostDate;
	private int numTopics;
	private int numPosts;
	private boolean active;
	private Set<GWTForumTopic> topics = new LinkedHashSet<GWTForumTopic>();

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<GWTForumTopic> getTopics() {
		return topics;
	}

	public void setTopics(Set<GWTForumTopic> topics) {
		this.topics = topics;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLastPostUser() {
		return lastPostUser;
	}

	public void setLastPostUser(String lastPostUser) {
		this.lastPostUser = lastPostUser;
	}

	public Date getLastPostDate() {
		return lastPostDate;
	}

	public void setLastPostDate(Date lastPostDate) {
		this.lastPostDate = lastPostDate;
	}

	public int getNumTopics() {
		return numTopics;
	}

	public void setNumTopics(int numTopics) {
		this.numTopics = numTopics;
	}

	public int getNumPosts() {
		return numPosts;
	}

	public void setNumPosts(int numPosts) {
		this.numPosts = numPosts;
	}
}
