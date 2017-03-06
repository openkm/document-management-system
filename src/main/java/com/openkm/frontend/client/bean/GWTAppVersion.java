package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWTAppVersion implements IsSerializable {
	private String major = "";
	private String minor = "";
	private String maintenance = "";
	private String build = "";
	private String extension = "";

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(String maintenance) {
		this.maintenance = maintenance;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getVersion() {
		return major + "." + minor + "." + maintenance;
	}

	public String toString() {
		return major + "." + minor + "." + maintenance + " (build: " + build + ")";
	}
}
