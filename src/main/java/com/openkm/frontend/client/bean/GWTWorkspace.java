/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * GWTWorkspace
 *
 * @author jllort
 *
 */
public class GWTWorkspace implements IsSerializable {
	private String sessionId = "";
	private GWTUser user;
	private List<String> roleList = new ArrayList<String>();
	private String applicationURL = "";
	private String mailProtocol = "";
	private String mailHost = "";
	private String mailUser = "";
	private String mailPassword = "";
	private String mailFolder = "";
	private long mailID = -1;
	private String password = "";
	private boolean changePassword = true;
	private String email = "";
	private String webSkin = "";
	private boolean adminRole = false;
	private String previewer = "";
	private List<GWTReport> reports = new ArrayList<GWTReport>();
	private int minSearchCharacters = 0;
	private int securityExtendedMask = 0;

	// System wide
	private GWTAppVersion appVersion = new GWTAppVersion();
	private String workflowRunConfigForm = "";
	private String workflowProcessIntanceVariableUUID = "";
	private String workflowProcessIntanceVariablePath = "";
	private long keepAliveSchedule;
	private long dashboardSchedule;
	private long uINotificationSchedule;
	private String tinymceTheme = "";
	private String tinymcePlugins = "";
	private String tinymceSkin = "";
	private String tinymceSkinVariant = "";
	private String tinimceThemeButtons1 = "";
	private String tinimceThemeButtons2 = "";
	private String tinimceThemeButtons3 = "";
	private String tinimceThemeButtons4 = "";
	private String htmlSyntaxHighlighterCore = "";
	private String htmlSyntaxHighlighterTheme = "";
	private String extraTabWorkspaceLabel = "";
	private String extraTabWorkspaceUrl = "";
	private boolean securityModeMultiple = false;

	// User Profile
	private boolean advancedFilters;
	private boolean chatEnabled;
	private boolean chatAutoLogin;
	private long userQuotaLimit;
	private boolean printPreview;
	private boolean keywordEnabled;
	private boolean uploadNotifyUsers;
	private boolean notifyExternalUsers;
	private boolean acrobatPluginPreview;
	private int increaseVersion = 0;
	private boolean userQuotaEnabled;
	private boolean webdavFix;
	private List<GWTPropertyGroup> wizardPropertyGroupList = new ArrayList<GWTPropertyGroup>();
	private List<String> wizardWorkflowList = new ArrayList<String>();
	private List<String> miscWorkflowList = new ArrayList<String>();
	private boolean wizardPropertyGroups;
	private boolean wizardWorkflows;
	private boolean wizardKeywords;
	private boolean wizardCategories;
	private boolean stackTaxonomy;
	private boolean stackCategoriesVisible;
	private boolean stackMetadataVisible;
	private boolean stackThesaurusVisible;
	private boolean stackTemplatesVisible;
	private boolean stackPersonalVisible;
	private boolean stackMailVisible;
	private boolean stackTrashVisible;
	private boolean menuFileVisible;
	private boolean menuEditVisible;
	private boolean menuToolsVisible;
	private boolean menuBookmarksVisible;
	private boolean menuTemplatesVisible;
	private boolean menuHelpVisible;
	private String defaultTab;
	private boolean tabDesktopVisible;
	private boolean tabSearchVisible;
	private boolean tabDashboardVisible;
	private boolean tabAdminVisible;
	private boolean dashboardUserVisible;
	private boolean dashboardMailVisible;
	private boolean dashboardNewsVisible;
	private boolean dashboardGeneralVisible;
	private boolean dashboardWorkflowVisible;
	private boolean dashboardKeywordsVisible;
	private boolean tabDocumentPropertiesVisible;
	private boolean tabDocumentNotesVisible;
	private boolean tabDocumentSecurityVisible;
	private boolean tabDocumentVersionVisible;
	private boolean tabDocumentPreviewVisible;
	private boolean tabDocumentPropertyGroupsVisible;
	private boolean tabDocumentVersionDownloadVisible;
	private boolean tabFolderPropertiesVisible;
	private boolean tabFolderSecurityVisible;
	private boolean tabFolderNotesVisible;
	private boolean tabMailPropertiesVisible;
	private boolean tabMailSecurityVisible;
	private boolean tabMailPreviewVisible;
	private boolean tabMailNotesVisible;
	private GWTAvailableOption availableOption = new GWTAvailableOption();
	private List<GWTLanguage> langs = new ArrayList<GWTLanguage>();
	private GWTProfileToolbar profileToolbar = new GWTProfileToolbar();
	private GWTProfileFileBrowser profileFileBrowser = new GWTProfileFileBrowser();
	private GWTProfilePagination profilePagination = new GWTProfilePagination();
	private List<GWTMimeType> mimeTypes;

	/**
	 * GWTWorkspace
	 */
	public GWTWorkspace() {
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public GWTUser getUser() {
		return user;
	}

	public void setUser(GWTUser user) {
		this.user = user;
	}

	public String getApplicationURL() {
		return applicationURL;
	}

	public void setApplicationURL(String applicationURL) {
		this.applicationURL = applicationURL;
	}

	public String getMailProtocol() {
		return mailProtocol;
	}

	public void setMailProtocol(String mailProtocol) {
		this.mailProtocol = mailProtocol;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public String getMailFolder() {
		return mailFolder;
	}

	public void setMailFolder(String mailFolder) {
		this.mailFolder = mailFolder;
	}

	public long getMailID() {
		return mailID;
	}

	public void setMailID(long mailID) {
		this.mailID = mailID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * System Wide
	 */
	public GWTAppVersion getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(GWTAppVersion appVersion) {
		this.appVersion = appVersion;
	}

	public String getWorkflowRunConfigForm() {
		return workflowRunConfigForm;
	}

	public void setWorkflowRunConfigForm(String workflowRunConfigForm) {
		this.workflowRunConfigForm = workflowRunConfigForm;
	}

	public String getWorkflowProcessIntanceVariableUUID() {
		return workflowProcessIntanceVariableUUID;
	}

	public void setWorkflowProcessIntanceVariableUUID(String workflowProcessIntanceVariableUUID) {
		this.workflowProcessIntanceVariableUUID = workflowProcessIntanceVariableUUID;
	}

	public String getWorkflowProcessIntanceVariablePath() {
		return workflowProcessIntanceVariablePath;
	}

	public void setWorkflowProcessIntanceVariablePath(String workflowProcessIntanceVariablePath) {
		this.workflowProcessIntanceVariablePath = workflowProcessIntanceVariablePath;
	}

	public boolean isChatEnabled() {
		return chatEnabled;
	}

	public void setChatEnabled(boolean chatEnabled) {
		this.chatEnabled = chatEnabled;
	}

	public boolean isChatAutoLogin() {
		return chatAutoLogin;
	}

	public void setChatAutoLogin(boolean chatAutoLogin) {
		this.chatAutoLogin = chatAutoLogin;
	}

	public long getKeepAliveSchedule() {
		return keepAliveSchedule;
	}

	public void setKeepAliveSchedule(long keepAliveSchedule) {
		this.keepAliveSchedule = keepAliveSchedule;
	}

	public long getDashboardSchedule() {
		return dashboardSchedule;
	}

	public void setDashboardSchedule(long dashboardSchedule) {
		this.dashboardSchedule = dashboardSchedule;
	}

	public long getUINotificationSchedule() {
		return uINotificationSchedule;
	}

	public void setUINotificationSchedule(long uINotificationSchedule) {
		this.uINotificationSchedule = uINotificationSchedule;
	}

	/**
	 * User Profile
	 */
	public boolean isAdvancedFilters() {
		return advancedFilters;
	}

	public void setAdvancedFilters(boolean advancedFilters) {
		this.advancedFilters = advancedFilters;
	}

	public long getUserQuotaLimit() {
		return userQuotaLimit;
	}

	public void setUserQuotaLimit(long userQuotaLimit) {
		this.userQuotaLimit = userQuotaLimit;
	}

	public boolean isPrintPreview() {
		return printPreview;
	}

	public void setPrintPreview(boolean printPreview) {
		this.printPreview = printPreview;
	}

	public boolean isKeywordEnabled() {
		return keywordEnabled;
	}

	public void setKeywordEnabled(boolean keywordEnabled) {
		this.keywordEnabled = keywordEnabled;
	}

	public boolean isUploadNotifyUsers() {
		return uploadNotifyUsers;
	}

	public void setUploadNotifyUsers(boolean uploadNotifyUsers) {
		this.uploadNotifyUsers = uploadNotifyUsers;
	}

	public boolean isNotifyExternalUsers() {
		return notifyExternalUsers;
	}

	public void setNotifyExternalUsers(boolean notifyExternalUsers) {
		this.notifyExternalUsers = notifyExternalUsers;
	}

	public boolean isAcrobatPluginPreview() {
		return acrobatPluginPreview;
	}

	public void setAcrobatPluginPreview(boolean acrobatPluginPreview) {
		this.acrobatPluginPreview = acrobatPluginPreview;
	}

	public int getIncreaseVersion() {
		return increaseVersion;
	}

	public void setIncreaseVersion(int increaseVersion) {
		this.increaseVersion = increaseVersion;
	}

	public boolean isUserQuotaEnabled() {
		return userQuotaEnabled;
	}

	public void setUserQuotaEnabled(boolean userQuotaEnabled) {
		this.userQuotaEnabled = userQuotaEnabled;
	}

	public boolean isWebdavFix() {
		return webdavFix;
	}

	public void setWebdavFix(boolean webdavFix) {
		this.webdavFix = webdavFix;
	}

	public boolean isWizardPropertyGroups() {
		return wizardPropertyGroups;
	}

	public void setWizardPropertyGroups(boolean wizardPropertyGroups) {
		this.wizardPropertyGroups = wizardPropertyGroups;
	}

	public List<GWTPropertyGroup> getWizardPropertyGroupList() {
		return wizardPropertyGroupList;
	}

	public void setWizardPropertyGroupList(List<GWTPropertyGroup> wizardPropertyGroupList) {
		this.wizardPropertyGroupList = wizardPropertyGroupList;
	}

	public List<String> getWizardWorkflowList() {
		return wizardWorkflowList;
	}

	public void setWizardWorkflowList(List<String> wizardWorkflowsList) {
		this.wizardWorkflowList = wizardWorkflowsList;
	}

	public boolean isWizardWorkflows() {
		return wizardWorkflows;
	}

	public void setWizardWorkflows(boolean wizardWorkflows) {
		this.wizardWorkflows = wizardWorkflows;
	}

	public boolean isWizardKeywords() {
		return wizardKeywords;
	}

	public void setWizardKeywords(boolean wizardKeywords) {
		this.wizardKeywords = wizardKeywords;
	}

	public boolean isWizardCategories() {
		return wizardCategories;
	}

	public void setWizardCategories(boolean wizardCategories) {
		this.wizardCategories = wizardCategories;
	}

	public boolean isStackTaxonomy() {
		return stackTaxonomy;
	}

	public void setStackTaxonomy(boolean stackTaxonomy) {
		this.stackTaxonomy = stackTaxonomy;
	}

	public boolean isStackCategoriesVisible() {
		return stackCategoriesVisible;
	}

	public void setStackCategoriesVisible(boolean stackCategoriesVisible) {
		this.stackCategoriesVisible = stackCategoriesVisible;
	}

	public boolean isStackMetadataVisible() {
		return stackMetadataVisible;
	}

	public void setStackMetadataVisible(boolean stackMetadataVisible) {
		this.stackMetadataVisible = stackMetadataVisible;
	}

	public boolean isStackThesaurusVisible() {
		return stackThesaurusVisible;
	}

	public void setStackThesaurusVisible(boolean stackThesaurusVisible) {
		this.stackThesaurusVisible = stackThesaurusVisible;
	}

	public boolean isStackTemplatesVisible() {
		return stackTemplatesVisible;
	}

	public void setStackTemplatesVisible(boolean stackTemplatesVisible) {
		this.stackTemplatesVisible = stackTemplatesVisible;
	}

	public boolean isStackPersonalVisible() {
		return stackPersonalVisible;
	}

	public void setStackPersonalVisible(boolean stackPersonalVisible) {
		this.stackPersonalVisible = stackPersonalVisible;
	}

	public boolean isStackMailVisible() {
		return stackMailVisible;
	}

	public void setStackMailVisible(boolean stackMailVisible) {
		this.stackMailVisible = stackMailVisible;
	}

	public boolean isStackTrashVisible() {
		return stackTrashVisible;
	}

	public void setStackTrashVisible(boolean stackTrashVisible) {
		this.stackTrashVisible = stackTrashVisible;
	}

	public boolean isMenuFileVisible() {
		return menuFileVisible;
	}

	public void setMenuFileVisible(boolean menuFileVisible) {
		this.menuFileVisible = menuFileVisible;
	}

	public boolean isMenuEditVisible() {
		return menuEditVisible;
	}

	public void setMenuEditVisible(boolean menuEditVisible) {
		this.menuEditVisible = menuEditVisible;
	}

	public boolean isMenuToolsVisible() {
		return menuToolsVisible;
	}

	public void setMenuToolsVisible(boolean menuToolsVisible) {
		this.menuToolsVisible = menuToolsVisible;
	}

	public boolean isMenuBookmarksVisible() {
		return menuBookmarksVisible;
	}

	public void setMenuBookmarksVisible(boolean menuBookmarksVisible) {
		this.menuBookmarksVisible = menuBookmarksVisible;
	}

	public boolean isMenuTemplatesVisible() {
		return menuTemplatesVisible;
	}

	public void setMenuTemplatesVisible(boolean menuTemplatesVisible) {
		this.menuTemplatesVisible = menuTemplatesVisible;
	}

	public boolean isMenuHelpVisible() {
		return menuHelpVisible;
	}

	public void setMenuHelpVisible(boolean menuHelpVisible) {
		this.menuHelpVisible = menuHelpVisible;
	}

	public String getDefaultTab() {
		return defaultTab;
	}

	public void setDefaultTab(String defaultTab) {
		this.defaultTab = defaultTab;
	}

	public boolean isTabDesktopVisible() {
		return tabDesktopVisible;
	}

	public void setTabDesktopVisible(boolean tabDesktopVisible) {
		this.tabDesktopVisible = tabDesktopVisible;
	}

	public boolean isTabSearchVisible() {
		return tabSearchVisible;
	}

	public void setTabSearchVisible(boolean tabSearchVisible) {
		this.tabSearchVisible = tabSearchVisible;
	}

	public boolean isTabDashboardVisible() {
		return tabDashboardVisible;
	}

	public void setTabDashboardVisible(boolean tabDashboardVisible) {
		this.tabDashboardVisible = tabDashboardVisible;
	}

	public boolean isTabAdminVisible() {
		return tabAdminVisible;
	}

	public void setTabAdminVisible(boolean tabAdminVisible) {
		this.tabAdminVisible = tabAdminVisible;
	}

	public boolean isDashboardUserVisible() {
		return dashboardUserVisible;
	}

	public void setDashboardUserVisible(boolean dashboardUserVisible) {
		this.dashboardUserVisible = dashboardUserVisible;
	}

	public boolean isDashboardMailVisible() {
		return dashboardMailVisible;
	}

	public void setDashboardMailVisible(boolean dashboardMailVisible) {
		this.dashboardMailVisible = dashboardMailVisible;
	}

	public boolean isDashboardNewsVisible() {
		return dashboardNewsVisible;
	}

	public void setDashboardNewsVisible(boolean dashboardNewsVisible) {
		this.dashboardNewsVisible = dashboardNewsVisible;
	}

	public boolean isDashboardGeneralVisible() {
		return dashboardGeneralVisible;
	}

	public void setDashboardGeneralVisible(boolean dashboardGeneralVisible) {
		this.dashboardGeneralVisible = dashboardGeneralVisible;
	}

	public boolean isDashboardWorkflowVisible() {
		return dashboardWorkflowVisible;
	}

	public void setDashboardWorkflowVisible(boolean dashboardWorkflowVisible) {
		this.dashboardWorkflowVisible = dashboardWorkflowVisible;
	}

	public boolean isDashboardKeywordsVisible() {
		return dashboardKeywordsVisible;
	}

	public void setDashboardKeywordsVisible(boolean dashboardKeywordsVisible) {
		this.dashboardKeywordsVisible = dashboardKeywordsVisible;
	}

	public GWTAvailableOption getAvailableOption() {
		return availableOption;
	}

	public void setAvailableOption(GWTAvailableOption availableOption) {
		this.availableOption = availableOption;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	public boolean isTabDocumentPropertiesVisible() {
		return tabDocumentPropertiesVisible;
	}

	public void setTabDocumentPropertiesVisible(boolean tabDocumentProperties) {
		this.tabDocumentPropertiesVisible = tabDocumentProperties;
	}

	public boolean isTabDocumentNotesVisible() {
		return tabDocumentNotesVisible;
	}

	public void setTabDocumentNotesVisible(boolean tabDocumentNotes) {
		this.tabDocumentNotesVisible = tabDocumentNotes;
	}

	public boolean isTabDocumentSecurityVisible() {
		return tabDocumentSecurityVisible;
	}

	public void setTabDocumentSecurityVisible(boolean tabDocumentSecurity) {
		this.tabDocumentSecurityVisible = tabDocumentSecurity;
	}

	public boolean isTabDocumentVersionVisible() {
		return tabDocumentVersionVisible;
	}

	public void setTabDocumentVersionVisible(boolean tabDocumentVersion) {
		this.tabDocumentVersionVisible = tabDocumentVersion;
	}

	public boolean isTabDocumentPreviewVisible() {
		return tabDocumentPreviewVisible;
	}

	public void setTabDocumentPreviewVisible(boolean tabDocumentPreview) {
		this.tabDocumentPreviewVisible = tabDocumentPreview;
	}

	public boolean isTabDocumentPropertyGroupsVisible() {
		return tabDocumentPropertyGroupsVisible;
	}

	public void setTabDocumentPropertyGroupsVisible(boolean tabDocumentPropertyGroups) {
		this.tabDocumentPropertyGroupsVisible = tabDocumentPropertyGroups;
	}

	public boolean isTabDocumentVersionDownloadVisible() {
		return tabDocumentVersionDownloadVisible;
	}

	public void setTabDocumentVersionDownloadVisible(boolean tabDocumentVersionDownloadVisible) {
		this.tabDocumentVersionDownloadVisible = tabDocumentVersionDownloadVisible;
	}

	public boolean isTabFolderPropertiesVisible() {
		return tabFolderPropertiesVisible;
	}

	public void setTabFolderPropertiesVisible(boolean tabFolderPropertiesVisible) {
		this.tabFolderPropertiesVisible = tabFolderPropertiesVisible;
	}

	public boolean isTabFolderSecurityVisible() {
		return tabFolderSecurityVisible;
	}

	public void setTabFolderSecurityVisible(boolean tabFolderSecurityVisible) {
		this.tabFolderSecurityVisible = tabFolderSecurityVisible;
	}

	public boolean isTabFolderNotesVisible() {
		return tabFolderNotesVisible;
	}

	public void setTabFolderNotesVisible(boolean tabFolderNotesVisible) {
		this.tabFolderNotesVisible = tabFolderNotesVisible;
	}

	public boolean isTabMailPropertiesVisible() {
		return tabMailPropertiesVisible;
	}

	public void setTabMailPropertiesVisible(boolean tabMailPropertiesVisible) {
		this.tabMailPropertiesVisible = tabMailPropertiesVisible;
	}

	public boolean isTabMailSecurityVisible() {
		return tabMailSecurityVisible;
	}

	public void setTabMailSecurityVisible(boolean tabMailSecurityVisible) {
		this.tabMailSecurityVisible = tabMailSecurityVisible;
	}

	public String getWebSkin() {
		return webSkin;
	}

	public void setWebSkin(String webSkin) {
		this.webSkin = webSkin;
	}

	public boolean isAdminRole() {
		return adminRole;
	}

	public void setAdminRole(boolean adminRole) {
		this.adminRole = adminRole;
	}

	public String getPreviewer() {
		return previewer;
	}

	public void setPreviewer(String previewer) {
		this.previewer = previewer;
	}

	public List<GWTLanguage> getLangs() {
		return langs;
	}

	public void setLangs(List<GWTLanguage> langs) {
		this.langs = langs;
	}

	public GWTProfileToolbar getProfileToolbar() {
		return profileToolbar;
	}

	public void setProfileToolbar(GWTProfileToolbar profileToolbar) {
		this.profileToolbar = profileToolbar;
	}

	public GWTProfileFileBrowser getProfileFileBrowser() {
		return profileFileBrowser;
	}

	public void setProfileFileBrowser(GWTProfileFileBrowser profileFileBrowser) {
		this.profileFileBrowser = profileFileBrowser;
	}

	public List<GWTReport> getReports() {
		return reports;
	}

	public void setReports(List<GWTReport> reports) {
		this.reports = reports;
	}

	public int getMinSearchCharacters() {
		return minSearchCharacters;
	}

	public void setMinSearchCharacters(int minSearchCharacters) {
		this.minSearchCharacters = minSearchCharacters;
	}

	public int getSecurityExtendedMask() {
		return securityExtendedMask;
	}

	public void setSecurityExtendedMask(int securityExtendedMask) {
		this.securityExtendedMask = securityExtendedMask;
	}

	public boolean isTabMailPreviewVisible() {
		return tabMailPreviewVisible;
	}

	public void setTabMailPreviewVisible(boolean tabMailPreviewVisible) {
		this.tabMailPreviewVisible = tabMailPreviewVisible;
	}

	public boolean isTabMailNotesVisible() {
		return tabMailNotesVisible;
	}

	public void setTabMailNotesVisible(boolean tabMailNotesVisible) {
		this.tabMailNotesVisible = tabMailNotesVisible;
	}

	public long getuINotificationSchedule() {
		return uINotificationSchedule;
	}

	public void setuINotificationSchedule(long uINotificationSchedule) {
		this.uINotificationSchedule = uINotificationSchedule;
	}

	public List<String> getMiscWorkflowList() {
		return miscWorkflowList;
	}

	public void setMiscWorkflowList(List<String> miscWorkflowList) {
		this.miscWorkflowList = miscWorkflowList;
	}

	public String getTinymceTheme() {
		return tinymceTheme;
	}

	public void setTinymceTheme(String tinymceTheme) {
		this.tinymceTheme = tinymceTheme;
	}

	public String getTinymcePlugins() {
		return tinymcePlugins;
	}

	public void setTinymcePlugins(String tinymcePlugins) {
		this.tinymcePlugins = tinymcePlugins;
	}

	public String getTinymceSkin() {
		return tinymceSkin;
	}

	public void setTinymceSkin(String tinymceSkin) {
		this.tinymceSkin = tinymceSkin;
	}

	public String getTinymceSkinVariant() {
		return tinymceSkinVariant;
	}

	public void setTinymceSkinVariant(String tinymceSkinVariant) {
		this.tinymceSkinVariant = tinymceSkinVariant;
	}

	public String getTinimceThemeButtons1() {
		return tinimceThemeButtons1;
	}

	public void setTinimceThemeButtons1(String tinimceThemeButtons1) {
		this.tinimceThemeButtons1 = tinimceThemeButtons1;
	}

	public String getTinimceThemeButtons2() {
		return tinimceThemeButtons2;
	}

	public void setTinimceThemeButtons2(String tinimceThemeButtons2) {
		this.tinimceThemeButtons2 = tinimceThemeButtons2;
	}

	public String getTinimceThemeButtons3() {
		return tinimceThemeButtons3;
	}

	public void setTinimceThemeButtons3(String tinimceThemeButtons3) {
		this.tinimceThemeButtons3 = tinimceThemeButtons3;
	}

	public String getTinimceThemeButtons4() {
		return tinimceThemeButtons4;
	}

	public void setTinimceThemeButtons4(String tinimceThemeButtons4) {
		this.tinimceThemeButtons4 = tinimceThemeButtons4;
	}

	public String getHtmlSyntaxHighlighterCore() {
		return htmlSyntaxHighlighterCore;
	}

	public void setHtmlSyntaxHighlighterCore(String htmlSyntaxHighlighterCore) {
		this.htmlSyntaxHighlighterCore = htmlSyntaxHighlighterCore;
	}

	public String getHtmlSyntaxHighlighterTheme() {
		return htmlSyntaxHighlighterTheme;
	}

	public void setHtmlSyntaxHighlighterTheme(String htmlSyntaxHighlighterTheme) {
		this.htmlSyntaxHighlighterTheme = htmlSyntaxHighlighterTheme;
	}

	public String getExtraTabWorkspaceLabel() {
		return extraTabWorkspaceLabel;
	}

	public void setExtraTabWorkspaceLabel(String extraTabWorkspaceLabel) {
		this.extraTabWorkspaceLabel = extraTabWorkspaceLabel;
	}

	public String getExtraTabWorkspaceUrl() {
		return extraTabWorkspaceUrl;
	}

	public void setExtraTabWorkspaceUrl(String extraTabWorkspaceUrl) {
		this.extraTabWorkspaceUrl = extraTabWorkspaceUrl;
	}

	public boolean isSecurityModeMultiple() {
		return securityModeMultiple;
	}

	public void setSecurityModeMultiple(boolean securityModeMultiple) {
		this.securityModeMultiple = securityModeMultiple;
	}

	public GWTProfilePagination getProfilePagination() {
		return profilePagination;
	}

	public void setProfilePagination(GWTProfilePagination profilePagination) {
		this.profilePagination = profilePagination;
	}

	public List<GWTMimeType> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(List<GWTMimeType> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
}
