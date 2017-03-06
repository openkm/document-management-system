package com.openkm.util.metadata;

import java.util.Calendar;

/**
 * See also @com.catcode.odf.OpenDocumentMetadata
 *
 * @author pavila
 */
public class OpenOfficeMetadata {
	private String generator;
	private String title;
	private String description;
	private String subject;
	private String keyword;
	private String initialCreator;
	private String creator;
	private String printedBy;
	private Calendar creationDate;
	private Calendar date;
	private Calendar printDate;
	private String language;
	private int editingCycles;
	private long editingDuration;
	private int pageCount;
	private int tableCount;
	private int drawCount;
	private int imageCount;
	private int oleObjectCount;
	private int paragraphCount;
	private int wordCount;
	private int characterCount;
	private int frameCount;
	private int sentenceCount;
	private int syllableCount;
	private int nonWhitespaceCharacterCount;
	private int rowCount;
	private int cellCount;
	private int objectCount;

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getInitialCreator() {
		return initialCreator;
	}

	public void setInitialCreator(String initialCreator) {
		this.initialCreator = initialCreator;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPrintedBy() {
		return printedBy;
	}

	public void setPrintedBy(String printedBy) {
		this.printedBy = printedBy;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public Calendar getPrintDate() {
		return printDate;
	}

	public void setPrintDate(Calendar printDate) {
		this.printDate = printDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getEditingCycles() {
		return editingCycles;
	}

	public void setEditingCycles(int editingCycles) {
		this.editingCycles = editingCycles;
	}

	public long getEditingDuration() {
		return editingDuration;
	}

	public void setEditingDuration(long editingDuration) {
		this.editingDuration = editingDuration;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getTableCount() {
		return tableCount;
	}

	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
	}

	public int getDrawCount() {
		return drawCount;
	}

	public void setDrawCount(int drawCount) {
		this.drawCount = drawCount;
	}

	public int getImageCount() {
		return imageCount;
	}

	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}

	public int getOleObjectCount() {
		return oleObjectCount;
	}

	public void setOleObjectCount(int oleObjectCount) {
		this.oleObjectCount = oleObjectCount;
	}

	public int getParagraphCount() {
		return paragraphCount;
	}

	public void setParagraphCount(int paragraphCount) {
		this.paragraphCount = paragraphCount;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getCharacterCount() {
		return characterCount;
	}

	public void setCharacterCount(int characterCount) {
		this.characterCount = characterCount;
	}

	public int getFrameCount() {
		return frameCount;
	}

	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}

	public int getSentenceCount() {
		return sentenceCount;
	}

	public void setSentenceCount(int sentenceCount) {
		this.sentenceCount = sentenceCount;
	}

	public int getSyllableCount() {
		return syllableCount;
	}

	public void setSyllableCount(int syllableCount) {
		this.syllableCount = syllableCount;
	}

	public int getNonWhitespaceCharacterCount() {
		return nonWhitespaceCharacterCount;
	}

	public void setNonWhitespaceCharacterCount(int nonWhitespaceCharacterCount) {
		this.nonWhitespaceCharacterCount = nonWhitespaceCharacterCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getCellCount() {
		return cellCount;
	}

	public void setCellCount(int cellCount) {
		this.cellCount = cellCount;
	}

	public int getObjectCount() {
		return objectCount;
	}

	public void setObjectCount(int objectCount) {
		this.objectCount = objectCount;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("generator=").append(generator);
		sb.append(", title=").append(title);
		sb.append(", description=").append(description);
		sb.append(", subject=").append(subject);
		sb.append(", keyword=").append(keyword);
		sb.append(", initialCreator=").append(initialCreator);
		sb.append(", creator=").append(creator);
		sb.append(", creationDate=").append(creationDate == null ? null : creationDate.getTime());
		sb.append(", printDate=").append(printDate == null ? null : printDate.getTime());
		sb.append(", pageCount=").append(pageCount);
		sb.append(", paragraphCount=").append(paragraphCount);
		sb.append(", wordCount=").append(wordCount);
		sb.append(", characterCount=").append(characterCount);
		sb.append(", rowCount=").append(rowCount);
		sb.append(", cellCount=").append(cellCount);
		sb.append("}");
		return sb.toString();
	}
}
