package com.openkm.util.metadata;

import java.util.Calendar;

public class PdfMetadata {
	private int numberOfPages;
	private String title;
	private String author;
	private String subject;
	private String keywords;
	private String creator;
	private String producer;
	private Calendar creationDate;
	private Calendar modificationDate;
	private String trapped;

	public int getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Calendar modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getTrapped() {
		return trapped;
	}

	public void setTrapped(String trapped) {
		this.trapped = trapped;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("numberOfPages=").append(numberOfPages);
		sb.append(", title=").append(title);
		sb.append(", author=").append(author);
		sb.append(", subject=").append(subject);
		sb.append(", keywords=").append(keywords);
		sb.append(", creator=").append(creator);
		sb.append(", producer=").append(producer);
		sb.append(", trapped=").append(trapped);
		sb.append(", creationDate=").append(creationDate == null ? null : creationDate.getTime());
		sb.append(", modificationDate=").append(modificationDate == null ? null : modificationDate.getTime());
		sb.append("}");
		return sb.toString();
	}
}
