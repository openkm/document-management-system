package com.openkm.util.metadata;

import java.util.Calendar;

/**
 * See also @org.apache.poi.hpsf.SummaryInformation
 *
 * @author pavila
 */
public class OfficeMetadata {
	private String title;
	private String subject;
	private String author;
	private String lastAuthor;
	private String keywords;
	private String comments;
	private String template;
	private String revNumber;
	private String applicationName;
	private Calendar lastPrinted;
	private Calendar createDateTime;
	private Calendar lastSaveDateTime;
	private long editTime;
	private int pageCount;
	private int wordCount;
	private int charCount;
	private int security;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLastAuthor() {
		return lastAuthor;
	}

	public void setLastAuthor(String lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getRevNumber() {
		return revNumber;
	}

	public void setRevNumber(String revNumber) {
		this.revNumber = revNumber;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public Calendar getLastPrinted() {
		return lastPrinted;
	}

	public void setLastPrinted(Calendar lastPrinted) {
		this.lastPrinted = lastPrinted;
	}

	public Calendar getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Calendar createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Calendar getLastSaveDateTime() {
		return lastSaveDateTime;
	}

	public void setLastSaveDateTime(Calendar lastSaveDateTime) {
		this.lastSaveDateTime = lastSaveDateTime;
	}

	public long getEditTime() {
		return editTime;
	}

	public void setEditTime(long editTime) {
		this.editTime = editTime;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getCharCount() {
		return charCount;
	}

	public void setCharCount(int charCount) {
		this.charCount = charCount;
	}

	public int getSecurity() {
		return security;
	}

	public void setSecurity(int security) {
		this.security = security;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("title=").append(title);
		sb.append(", subject=").append(subject);
		sb.append(", author=").append(author);
		sb.append(", lastAuthor=").append(lastAuthor);
		sb.append(", keywords=").append(keywords);
		sb.append(", comments=").append(comments);
		sb.append(", template=").append(template);
		sb.append(", revNumber=").append(revNumber);
		sb.append(", applicationName=").append(applicationName);
		sb.append(", lastPrinted=").append(lastPrinted == null ? null : lastPrinted.getTime());
		sb.append(", createDateTime=").append(createDateTime == null ? null : createDateTime.getTime());
		sb.append(", lastSaveDateTime=").append(lastSaveDateTime == null ? null : lastSaveDateTime.getTime());
		sb.append(", editTime=").append(editTime);
		sb.append(", pageCount=").append(pageCount);
		sb.append(", wordCount=").append(wordCount);
		sb.append(", charCount=").append(charCount);
		sb.append(", security=").append(security);
		sb.append("}");
		return sb.toString();
	}
}
