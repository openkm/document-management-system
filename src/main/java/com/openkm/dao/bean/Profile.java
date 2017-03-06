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

import java.io.Serializable;

public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private boolean active;
	private ProfileChat prfChat = new ProfileChat();
	private ProfilePagination prfPagination = new ProfilePagination();
	private ProfileDashboard prfDashboard = new ProfileDashboard();
	private ProfileMenu prfMenu = new ProfileMenu();
	private ProfileMisc prfMisc = new ProfileMisc();
	private ProfileStack prfStack = new ProfileStack();
	private ProfileTab prfTab = new ProfileTab();
	private ProfileWizard prfWizard = new ProfileWizard();
	private ProfileToolbar prfToolbar = new ProfileToolbar();
	private ProfileFileBrowser prfFileBrowser = new ProfileFileBrowser();

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ProfileChat getPrfChat() {
		return prfChat;
	}

	public void setPrfChat(ProfileChat prfChat) {
		this.prfChat = prfChat;
	}

	public ProfilePagination getPrfPagination() {
		return prfPagination;
	}

	public void setPrfPagination(ProfilePagination prfPagination) {
		this.prfPagination = prfPagination;
	}

	public ProfileDashboard getPrfDashboard() {
		return prfDashboard;
	}

	public void setPrfDashboard(ProfileDashboard prfDashboard) {
		this.prfDashboard = prfDashboard;
	}

	public ProfileMenu getPrfMenu() {
		return prfMenu;
	}

	public void setPrfMenu(ProfileMenu prfMenu) {
		this.prfMenu = prfMenu;
	}

	public ProfileMisc getPrfMisc() {
		return prfMisc;
	}

	public void setPrfMisc(ProfileMisc prfMisc) {
		this.prfMisc = prfMisc;
	}

	public ProfileStack getPrfStack() {
		return prfStack;
	}

	public void setPrfStack(ProfileStack prfStack) {
		this.prfStack = prfStack;
	}

	public ProfileTab getPrfTab() {
		return prfTab;
	}

	public void setPrfTab(ProfileTab prfTab) {
		this.prfTab = prfTab;
	}

	public ProfileWizard getPrfWizard() {
		return prfWizard;
	}

	public void setPrfWizard(ProfileWizard prfWizard) {
		this.prfWizard = prfWizard;
	}

	public ProfileToolbar getPrfToolbar() {
		return prfToolbar;
	}

	public void setPrfToolbar(ProfileToolbar prfToolbar) {
		this.prfToolbar = prfToolbar;
	}

	public ProfileFileBrowser getPrfFileBrowser() {
		return prfFileBrowser;
	}

	public void setPrfFileBrowser(ProfileFileBrowser prfFileBrowser) {
		this.prfFileBrowser = prfFileBrowser;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", name=");
		sb.append(name);
		sb.append(", active=");
		sb.append(active);
		sb.append(", prfChat=");
		sb.append(prfChat);
		sb.append(", prfPagination=");
		sb.append(prfPagination);
		sb.append(", prfDashboard=");
		sb.append(prfDashboard);
		sb.append(", prfMenu=");
		sb.append(prfMenu);
		sb.append(", prfMisc=");
		sb.append(prfMisc);
		sb.append(", prfStack=");
		sb.append(prfStack);
		sb.append(", prfTab=");
		sb.append(prfTab);
		sb.append(", prfWizard=");
		sb.append(prfWizard);
		sb.append(", prfToolbar=");
		sb.append(prfToolbar);
		sb.append(", prfFileBrowser=");
		sb.append(prfFileBrowser);
		sb.append("}");
		return sb.toString();
	}
}
