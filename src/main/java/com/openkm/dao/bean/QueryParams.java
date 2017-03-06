package com.openkm.dao.bean;

import com.openkm.extension.dao.bean.ProposedQueryReceived;
import com.openkm.extension.dao.bean.ProposedQuerySent;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

@XmlRootElement(name = "queryParams")
public class QueryParams implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int DOCUMENT = 1;
	public static final int FOLDER = 2;
	public static final int MAIL = 4;

	public static final String AND = "and";
	public static final String OR = "or";

	private long id;
	private String queryName;
	private String user;
	private String name;
	private Set<String> keywords = new HashSet<String>();
	private Set<String> categories = new HashSet<String>();
	private String content;
	private String mimeType;
	private String author;
	private String path;
	private Calendar lastModifiedFrom;
	private Calendar lastModifiedTo;
	private String mailSubject;
	private String mailFrom;
	private String mailTo;
	private String statementQuery;
	private String statementType;
	private boolean dashboard;
	private long domain = DOCUMENT;
	private String operator = AND;
	private Map<String, String> properties = new HashMap<String, String>();
	private Set<String> shared = new HashSet<String>();
	private Set<ProposedQuerySent> proposedSent = new HashSet<ProposedQuerySent>();
	private Set<ProposedQueryReceived> proposedReceived = new HashSet<ProposedQueryReceived>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Calendar getLastModifiedFrom() {
		return lastModifiedFrom;
	}

	public void setLastModifiedFrom(Calendar lastModifiedFrom) {
		this.lastModifiedFrom = lastModifiedFrom;
	}

	public Calendar getLastModifiedTo() {
		return lastModifiedTo;
	}

	public void setLastModifiedTo(Calendar lastModifiedTo) {
		this.lastModifiedTo = lastModifiedTo;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getStatementQuery() {
		return statementQuery;
	}

	public void setStatementQuery(String statementQuery) {
		this.statementQuery = statementQuery;
	}

	public String getStatementType() {
		return statementType;
	}

	public void setStatementType(String statementType) {
		this.statementType = statementType;
	}

	public boolean isDashboard() {
		return dashboard;
	}

	public void setDashboard(boolean dashboard) {
		this.dashboard = dashboard;
	}

	public long getDomain() {
		return domain;
	}

	public void setDomain(long domain) {
		this.domain = domain;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Set<String> getShared() {
		return shared;
	}

	public void setShared(Set<String> shared) {
		this.shared = shared;
	}

	public Set<ProposedQuerySent> getProposedSent() {
		return proposedSent;
	}

	public void setProposedSent(Set<ProposedQuerySent> proposedSent) {
		this.proposedSent = proposedSent;
	}

	public Set<ProposedQueryReceived> getProposedReceived() {
		return proposedReceived;
	}

	public void setProposedReceived(Set<ProposedQueryReceived> proposedReceived) {
		this.proposedReceived = proposedReceived;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", queryName=").append(queryName);
		sb.append(", user=").append(user);
		sb.append(", name=").append(name);
		sb.append(", keywords=").append(keywords);
		sb.append(", categories=").append(categories);
		sb.append(", content=").append(content);
		sb.append(", mimeType=").append(mimeType);
		sb.append(", author=").append(author);
		sb.append(", path=").append(path);
		sb.append(", dashboard=").append(dashboard);
		sb.append(", domain=").append(domain);
		sb.append(", operator=").append(operator);
		sb.append(", properties=").append(properties);
		sb.append(", shared=").append(shared);
		sb.append(", proposedSent=").append(proposedSent);
		sb.append(", proposedReceived=").append(proposedReceived);
		sb.append(", statementQuery=").append(statementQuery);
		sb.append(", statementType=").append(statementType);
		sb.append(", lastModifiedFrom=").append(lastModifiedFrom == null ? null : lastModifiedFrom.getTime());
		sb.append(", lastModifiedTo=").append(lastModifiedTo == null ? null : lastModifiedTo.getTime());
		sb.append("}");
		return sb.toString();
	}
}
