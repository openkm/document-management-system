package com.openkm.dao.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import com.openkm.module.db.stuff.SetFieldBridge;


@Entity
@Table(name = "OKM_OMR")
public class Omr implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="OMR_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
		
	@Column(name="OMR_NAME", length=128, unique=true)
	private String name;
		
	@Column(name="OMR_TEMPLATE_FILE_CONTENT")
	@Lob
	private byte[] templateFileContent;
	
	@Column(name="OMR_ASC_FILE_CONTENT")
	@Lob 
	private byte[] ascFileContent;
	
	@Column(name="OMR_CONFIG_FILE_CONTENT")
	@Lob 
	private byte[] configFileContent;
	
	@Column(name="OMR_FIELDS_FILE_CONTENT")
	@Lob
	private byte[] fieldsFileContent;
	
	@Column(name="OMR_FILE_TEMPLATE_MIME", length=32)
	private String templateFileMime;
	
	@Column(name="OMR_FILE_ASC_MIME", length=32)
	private String ascFileMime;
	
	@Column(name="OMR_FILE_CONFIG_MIME", length=32)
	private String configFileMime;
	
	@Column(name="OMR_FILE_FIELDS_MIME", length=32)
	private String fieldsFileMime;
	
	@Column(name="OMR_TEMPLATE_FILENAME", length=128)
	private String templateFileName;
	
	@Column(name="OMR_ASC_FILENAME", length=128)
	private String ascFileName;
	
	@Column(name="OMR_CONFIG_FILENAME", length=128)
	private String configFileName;
	
	@Column(name="OMR_FIELDS_FILENAME", length=128)
	private String fieldsFileName;
	
	@ElementCollection
	@Column(name = "OMP_PROPERTY")
	@CollectionTable(name = "OKM_OMR_PROPERTY", joinColumns = { @JoinColumn(name = "OMP_OMR") })
	@Field(index = Index.UN_TOKENIZED, store = Store.YES)
	@FieldBridge(impl = SetFieldBridge.class)
	protected Set<String> properties = new HashSet<String>();
	
	@Column(name = "OMR_ACTIVE", nullable = false)
	@Type(type = "true_false")
	private boolean active;

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

	public byte[] getTemplateFileContent() {
		return templateFileContent;
	}

	public void setTemplateFilContent(byte[] templateFileContent) {
		this.templateFileContent = templateFileContent;
	}

	public String getTemplateFileMime() {
		return templateFileMime;
	}

	public void setTemplateFileMime(String fileTemplateMime) {
		this.templateFileMime = fileTemplateMime;
	}

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public byte[] getAscFileContent() {
		return ascFileContent;
	}

	public void setAscFileContent(byte[] ascFileContent) {
		this.ascFileContent = ascFileContent;
	}

	public byte[] getConfigFileContent() {
		return configFileContent;
	}

	public void setConfigFileContent(byte[] configFileContent) {
		this.configFileContent = configFileContent;
	}

	public byte[] getFieldsFileContent() {
		return fieldsFileContent;
	}

	public void setFieldsFileContent(byte[] fieldsFileContent) {
		this.fieldsFileContent = fieldsFileContent;
	}

	public String getAscFileMime() {
		return ascFileMime;
	}

	public void setAscFileMime(String ascFileMime) {
		this.ascFileMime = ascFileMime;
	}

	public String getConfigFileMime() {
		return configFileMime;
	}

	public void setConfigFileMime(String configFileMime) {
		this.configFileMime = configFileMime;
	}

	public String getFieldsFileMime() {
		return fieldsFileMime;
	}

	public void setFieldsFileMime(String fieldsFileMime) {
		this.fieldsFileMime = fieldsFileMime;
	}

	public String getAscFileName() {
		return ascFileName;
	}

	public void setAscFileName(String ascFileName) {
		this.ascFileName = ascFileName;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public String getFieldsFileName() {
		return fieldsFileName;
	}

	public void setFieldsFileName(String fieldsFileName) {
		this.fieldsFileName = fieldsFileName;
	}

	public void setTemplateFileContent(byte[] templateFileContent) {
		this.templateFileContent = templateFileContent;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Set<String> getProperties() {
		return properties;
	}

	public void setProperties(Set<String> properties) {
		this.properties = properties;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", templateFileName=").append(templateFileName);
		sb.append(", templateFileMime=").append(templateFileMime);
		sb.append(", templateFileContent=").append("[BIG]");
		sb.append(", ascFileName=").append(ascFileName);
		sb.append(", ascFileMime=").append(ascFileMime);
		sb.append(", ascFileContent=").append("[BIG]");
		sb.append(", configFileName=").append(configFileName);
		sb.append(", configFileMime=").append(configFileMime);
		sb.append(", configFileContent=").append("[BIG]");
		sb.append(", fieldsFileName=").append(fieldsFileName);
		sb.append(", fieldsFileMime=").append(fieldsFileMime);
		sb.append(", fieldsFileContent=").append("[BIG]");
		sb.append(", active=").append(active);
		sb.append("}");
		return sb.toString();
	}
}