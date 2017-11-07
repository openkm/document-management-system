/**
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

package com.openkm.servlet.frontend;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDashboard;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.PropertyGroup;
import com.openkm.core.*;
import com.openkm.core.Config;
import com.openkm.dao.*;
import com.openkm.dao.bean.*;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMWorkspaceService;
import com.openkm.principal.DatabasePrincipalAdapter;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.GWTUtil;
import com.openkm.util.ReportUtils;
import com.openkm.util.WarUtils;
import com.openkm.validator.ValidatorException;
import com.openkm.validator.ValidatorFactory;
import com.openkm.validator.password.PasswordValidator;
import com.openkm.vernum.MajorMinorReleaseVersionNumerationAdapter;
import com.openkm.vernum.MajorMinorVersionNumerationAdapter;
import com.openkm.vernum.VersionNumerationAdapter;
import com.openkm.vernum.VersionNumerationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * WorkspaceServlet
 *
 * @author jllort
 *
 */
public class WorkspaceServlet extends OKMRemoteServiceServlet implements OKMWorkspaceService {
	private static Logger log = LoggerFactory.getLogger(WorkspaceServlet.class);
	private static final long serialVersionUID = 8673521252684830906L;

	@Override
	public GWTWorkspace getUserWorkspace() throws OKMException {
		log.debug("getUserWorkspace()");
		updateSessionManager();
		GWTWorkspace workspace = new GWTWorkspace();

		workspace.setApplicationURL(Config.APPLICATION_URL);
		workspace.setAppVersion(GWTUtil.copy(WarUtils.getAppVersion()));
		workspace.setWorkflowRunConfigForm(Config.WORKFLOW_RUN_CONFIG_FORM);
		workspace.setWorkflowProcessIntanceVariableUUID(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_UUID);
		workspace.setWorkflowProcessIntanceVariablePath(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_PATH);
		workspace.setSessionId(getThreadLocalRequest().getSession().getId());
		workspace.setMinSearchCharacters(Config.MIN_SEARCH_CHARACTERS);

		// Tinymce
		workspace.setTinymceTheme(Config.TINYMCE_THEME);
		workspace.setTinymceSkin(Config.TINYMCE_SKIN);
		workspace.setTinymceSkinVariant(Config.TINYMCE_SKIN_VARIANT);
		workspace.setTinymcePlugins(Config.TINYMCE_PLUGINS);
		workspace.setTinimceThemeButtons1(Config.TINYMCE_THEME_BUTTONS1);
		workspace.setTinimceThemeButtons2(Config.TINYMCE_THEME_BUTTONS2);
		workspace.setTinimceThemeButtons3(Config.TINYMCE_THEME_BUTTONS3);
		workspace.setTinimceThemeButtons4(Config.TINYMCE_THEME_BUTTONS4);

		// Syntax highlighter
		workspace.setHtmlSyntaxHighlighterCore(Config.HTML_SINTAXHIGHLIGHTER_CORE);
		workspace.setHtmlSyntaxHighlighterTheme(Config.HTML_SINTAXHIGHLIGHTER_THEME);

		// Security mode
		workspace.setSecurityModeMultiple(Config.SECURITY_MODE_MULTIPLE);

		// Extra Tab Workspace
		workspace.setExtraTabWorkspaceLabel(Config.EXTRA_TAB_WORKSPACE_LABEL);
		workspace.setExtraTabWorkspaceUrl(Config.EXTRA_TAB_WORKSPACE_URL);

		// Extended security
		workspace.setSecurityExtendedMask(Config.SECURITY_EXTENDED_MASK);

		// Schedule time
		workspace.setKeepAliveSchedule(TimeUnit.MINUTES.toMillis(Config.SCHEDULE_SESSION_KEEPALIVE));
		workspace.setDashboardSchedule(TimeUnit.MINUTES.toMillis(Config.SCHEDULE_DASHBOARD_REFRESH));
		workspace.setUINotificationSchedule(TimeUnit.MINUTES.toMillis(Config.SCHEDULE_UI_NOTIFICATION));

		List<GWTPropertyGroup> wizardPropGrpLst = new ArrayList<GWTPropertyGroup>();
		List<String> wizardWorkflowLst = new ArrayList<String>();
		List<String> miscWorkflowLst = new ArrayList<String>();
		Profile up = new Profile();

		try {
			// User data
			GWTUser gwtUser = new GWTUser();
			gwtUser.setId(getThreadLocalRequest().getRemoteUser());
			gwtUser.setUsername(OKMAuth.getInstance().getName(null, gwtUser.getId()));
			workspace.setUser(gwtUser);

			UserConfig uc = UserConfigDAO.findByPk(getThreadLocalRequest().getRemoteUser());
			up = uc.getProfile();

			for (String pgroup : up.getPrfWizard().getPropertyGroups()) {
				for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
					if (pg.getName().equals(pgroup) && pg.isVisible()) {
						wizardPropGrpLst.add(GWTUtil.copy(pg));
						break;
					}
				}
			}

			for (String workflow : up.getPrfWizard().getWorkflows()) {
				wizardWorkflowLst.add(workflow);
			}

			for (String workflow : up.getPrfMisc().getWorkflows()) {
				miscWorkflowLst.add(workflow);
			}

			// Previewer
			workspace.setPreviewer(Config.SYSTEM_PREVIEWER);

			// Advanced filters ( used when there a lot of users and groups )
			workspace.setAdvancedFilters(up.getPrfMisc().isAdvancedFilters());

			// Is a wizard to uploading documents
			workspace.setWizardPropertyGroups(!up.getPrfWizard().getPropertyGroups().isEmpty());
			workspace.setWizardPropertyGroupList(wizardPropGrpLst);
			workspace.setWizardWorkflows(!up.getPrfWizard().getWorkflows().isEmpty());
			workspace.setWizardWorkflowList(wizardWorkflowLst);
			workspace.setWizardCategories(up.getPrfWizard().isCategoriesEnabled());
			workspace.setWizardKeywords(up.getPrfWizard().isKeywordsEnabled());

			// acrobat plgin preview
			workspace.setAcrobatPluginPreview(up.getPrfMisc().isAcrobatPluginPreview());

			// increase version
			if (up.getPrfMisc().isIncreaseVersion()) {
				VersionNumerationAdapter vna = VersionNumerationFactory.getVersionNumerationAdapter();
				if (vna instanceof MajorMinorReleaseVersionNumerationAdapter) {
					workspace.setIncreaseVersion(2);
				} else if (vna instanceof MajorMinorVersionNumerationAdapter) {
					workspace.setIncreaseVersion(1);
				}
			}

			// Is a misc workflow list available
			workspace.setMiscWorkflowList(miscWorkflowLst);

			// Is chat enabled and autologin
			workspace.setChatEnabled(up.getPrfChat().isChatEnabled());
			workspace.setChatAutoLogin(up.getPrfChat().isAutoLoginEnabled());

			// Is admin
			workspace.setAdminRole(getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE));

			// Setting web skin
			workspace.setWebSkin(up.getPrfMisc().getWebSkin());

			// Only thesaurus keywords are allowed
			workspace.setKeywordEnabled(up.getPrfMisc().isKeywordsEnabled());

			// User quota ( limit user repository size )
			workspace.setUserQuotaEnabled(up.getPrfMisc().getUserQuota() > 0);
			workspace.setUserQuotaLimit(up.getPrfMisc().getUserQuota() * 1024 * 1024);
			workspace.setPrintPreview(up.getPrfMisc().isPrintPreview());
			workspace.setUploadNotifyUsers(up.getPrfMisc().isUploadNotifyUsers());
			workspace.setNotifyExternalUsers(up.getPrfMisc().isNotifyExternalUsers());
			workspace.setWebdavFix(Config.SYSTEM_WEBDAV_FIX);

			// Stack visibility
			workspace.setStackTaxonomy(up.getPrfStack().isTaxonomyVisible());
			workspace.setStackCategoriesVisible(up.getPrfStack().isCategoriesVisible());
			workspace.setStackMetadataVisible(up.getPrfStack().isMetadataVisible());
			workspace.setStackThesaurusVisible(up.getPrfStack().isThesaurusVisible());
			workspace.setStackTemplatesVisible(up.getPrfStack().isTemplatesVisible());
			workspace.setStackPersonalVisible(up.getPrfStack().isPersonalVisible());
			workspace.setStackMailVisible(up.getPrfStack().isMailVisible());
			workspace.setStackTrashVisible(up.getPrfStack().isTrashVisible());

			// Menus visibility
			workspace.setMenuFileVisible(up.getPrfMenu().isFileVisible());
			workspace.setMenuEditVisible(up.getPrfMenu().isEditVisible());
			workspace.setMenuBookmarksVisible(up.getPrfMenu().isBookmarksVisible());
			workspace.setMenuToolsVisible(up.getPrfMenu().isToolsVisible());
			workspace.setMenuTemplatesVisible(up.getPrfMenu().isTemplatesVisible());
			workspace.setMenuHelpVisible(up.getPrfMenu().isHelpVisible());

			// Tab visibility
			workspace.setDefaultTab(up.getPrfTab().getDefaultTab());
			workspace.setTabDesktopVisible(up.getPrfTab().isDesktopVisible());
			workspace.setTabSearchVisible(up.getPrfTab().isSearchVisible());
			workspace.setTabDashboardVisible(up.getPrfTab().isDashboardVisible());
			workspace.setTabAdminVisible(getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE)
					&& up.getPrfTab().isAdministrationVisible());

			// If there's no stack visible force Desktop to do not be visible
			if (!up.getPrfStack().isTaxonomyVisible() && !up.getPrfStack().isCategoriesVisible() && !up.getPrfStack().isMetadataVisible()
					&& !up.getPrfStack().isThesaurusVisible() && !up.getPrfStack().isTemplatesVisible()
					&& !up.getPrfStack().isPersonalVisible() && !up.getPrfStack().isMailVisible() && !up.getPrfStack().isTrashVisible()) {
				workspace.setTabDesktopVisible(false);
			}

			// Tab document visibility
			workspace.setTabDocumentPropertiesVisible(up.getPrfTab().getPrfDocument().isPropertiesVisible());
			workspace.setTabDocumentSecurityVisible(up.getPrfTab().getPrfDocument().isSecurityVisible());
			workspace.setTabDocumentNotesVisible(up.getPrfTab().getPrfDocument().isNotesVisible());
			workspace.setTabDocumentVersionVisible(up.getPrfTab().getPrfDocument().isVersionsVisible());
			workspace.setTabDocumentPreviewVisible(up.getPrfTab().getPrfDocument().isPreviewVisible());
			workspace.setTabDocumentPropertyGroupsVisible(up.getPrfTab().getPrfDocument().isPropertyGroupsVisible());
			workspace.setTabDocumentVersionDownloadVisible(up.getPrfTab().getPrfDocument().isVersionDownloadVisible());

			// Tab folder visibility
			workspace.setTabFolderPropertiesVisible(up.getPrfTab().getPrfFolder().isPropertiesVisible());
			workspace.setTabFolderSecurityVisible(up.getPrfTab().getPrfFolder().isSecurityVisible());
			workspace.setTabFolderNotesVisible(up.getPrfTab().getPrfFolder().isNotesVisible());

			// Tab mail visibility
			workspace.setTabMailPropertiesVisible(up.getPrfTab().getPrfMail().isPropertiesVisible());
			workspace.setTabMailSecurityVisible(up.getPrfTab().getPrfMail().isSecurityVisible());
			workspace.setTabMailPreviewVisible(up.getPrfTab().getPrfMail().isPreviewVisible());
			workspace.setTabMailNotesVisible(up.getPrfTab().getPrfMail().isNotesVisible());

			// Dashboard visibility
			workspace.setDashboardUserVisible(up.getPrfDashboard().isUserVisible());
			workspace.setDashboardMailVisible(up.getPrfDashboard().isMailVisible());
			workspace.setDashboardNewsVisible(up.getPrfDashboard().isNewsVisible());
			workspace.setDashboardGeneralVisible(up.getPrfDashboard().isGeneralVisible());
			workspace.setDashboardWorkflowVisible(up.getPrfDashboard().isWorkflowVisible());
			workspace.setDashboardKeywordsVisible(up.getPrfDashboard().isKeywordsVisible());

			// Available options
			GWTAvailableOption availableOption = new GWTAvailableOption();

			// Menu File
			availableOption.setCreateFolderOption(up.getPrfMenu().getPrfFile().isCreateFolderVisible());
			availableOption.setFindFolderOption(up.getPrfMenu().getPrfFile().isFindFolderVisible());
			availableOption.setFindDocumentOption(up.getPrfMenu().getPrfFile().isFindDocumentVisible());
			availableOption.setSimilarDocumentVisible(up.getPrfMenu().getPrfFile().isSimilarDocumentVisible());
			availableOption.setGotoFolderOption(up.getPrfMenu().getPrfFile().isGoFolderVisible());
			availableOption.setDownloadOption(up.getPrfMenu().getPrfFile().isDownloadVisible());
			availableOption.setDownloadPdfOption(up.getPrfMenu().getPrfFile().isDownloadPdfVisible());
			availableOption.setAddDocumentOption(up.getPrfMenu().getPrfFile().isAddDocumentVisible());
			availableOption.setWorkflowOption(up.getPrfMenu().getPrfFile().isStartWorkflowVisible());
			availableOption.setRefreshOption(up.getPrfMenu().getPrfFile().isRefreshVisible());
			availableOption.setExportOption(up.getPrfMenu().getPrfFile().isExportVisible());
			availableOption.setCreateFromTemplateOption(up.getPrfMenu().getPrfFile().isCreateFromTemplateVisible());
			availableOption.setPurgeOption(up.getPrfMenu().getPrfFile().isPurgeVisible());
			availableOption.setRestoreOption(up.getPrfMenu().getPrfFile().isRestoreVisible());
			availableOption.setPurgeTrashOption(up.getPrfMenu().getPrfFile().isPurgeTrashVisible());
			availableOption.setSendDocumentLinkOption(up.getPrfMenu().getPrfFile().isSendDocumentLinkVisible());
			availableOption.setSendDocumentAttachmentOption(up.getPrfMenu().getPrfFile().isSendDocumentAttachmentVisible());
			availableOption.setForwardMailOption(up.getPrfMenu().getPrfFile().isForwardMailVisible());

			// Menu Edit
			availableOption.setLockOption(up.getPrfMenu().getPrfEdit().isLockVisible());
			availableOption.setUnLockOption(up.getPrfMenu().getPrfEdit().isUnlockVisible());
			availableOption.setRenameOption(up.getPrfMenu().getPrfEdit().isRenameVisible());
			availableOption.setCopyOption(up.getPrfMenu().getPrfEdit().isCopyVisible());
			availableOption.setMoveOption(up.getPrfMenu().getPrfEdit().isMoveVisible());
			availableOption.setCheckinOption(up.getPrfMenu().getPrfEdit().isCheckInVisible());
			availableOption.setCheckoutOption(up.getPrfMenu().getPrfEdit().isCheckOutVisible());
			availableOption.setCancelCheckoutOption(up.getPrfMenu().getPrfEdit().isCancelCheckOutVisible());
			availableOption.setDeleteOption(up.getPrfMenu().getPrfEdit().isDeleteVisible());
			availableOption.setAddPropertyGroupOption(up.getPrfMenu().getPrfEdit().isAddPropertyGroupVisible());
			availableOption.setUpdatePropertyGroupOption(up.getPrfMenu().getPrfEdit().isUpdatePropertyGroupVisible());
			availableOption.setRemovePropertyGroupOption(up.getPrfMenu().getPrfEdit().isRemovePropertyGroupVisible());
			availableOption.setAddSubscriptionOption(up.getPrfMenu().getPrfEdit().isAddSubscriptionVisible());
			availableOption.setRemoveSubscriptionOption(up.getPrfMenu().getPrfEdit().isRemoveSubscriptionVisible());
			availableOption.setAddNoteOption(up.getPrfMenu().getPrfEdit().isAddNoteVisible());
			availableOption.setAddCategoryOption(up.getPrfMenu().getPrfEdit().isAddCategoryVisible());
			availableOption.setAddKeywordOption(up.getPrfMenu().getPrfEdit().isAddKeywordVisible());
			availableOption.setRemoveNoteOption(up.getPrfMenu().getPrfEdit().isRemoveNoteVisible());
			availableOption.setRemoveCategoryOption(up.getPrfMenu().getPrfEdit().isRemoveCategoryVisible());
			availableOption.setRemoveKeywordOption(up.getPrfMenu().getPrfEdit().isRemoveKeywordVisible());
			availableOption.setMergePdfOption(up.getPrfMenu().getPrfEdit().isMergePdfVisible());

			// Menu Bookmark
			availableOption.setManageBookmarkOption(up.getPrfMenu().getPrfBookmark().isManageBookmarksVisible());
			availableOption.setAddBookmarkOption(up.getPrfMenu().getPrfBookmark().isAddBookmarkVisible());
			availableOption.setHomeOption(up.getPrfMenu().getPrfBookmark().isGoHomeVisible());
			availableOption.setSetHomeOption(up.getPrfMenu().getPrfBookmark().isSetHomeVisible());

			// Menu Tool
			availableOption.setLanguagesOption(up.getPrfMenu().getPrfTool().isLanguagesVisible());
			availableOption.setSkinOption(up.getPrfMenu().getPrfTool().isSkinVisible());
			availableOption.setDebugOption(up.getPrfMenu().getPrfTool().isDebugVisible());
			availableOption.setAdministrationOption(up.getPrfMenu().getPrfTool().isAdministrationVisible()
					&& getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE));
			availableOption.setPreferencesOption(up.getPrfMenu().getPrfTool().isPreferencesVisible());
			availableOption.setConvertOption(up.getPrfMenu().getPrfTool().isConvertVisible());

			// Menu Help
			availableOption.setHelpOption(up.getPrfMenu().getPrfHelp().isHelpVisible());
			availableOption.setDocumentationOption(up.getPrfMenu().getPrfHelp().isDocumentationVisible());
			availableOption.setBugReportOption(up.getPrfMenu().getPrfHelp().isBugTrackingVisible());
			availableOption.setSupportRequestOption(up.getPrfMenu().getPrfHelp().isSupportVisible());
			availableOption.setPublicForumOption(up.getPrfMenu().getPrfHelp().isForumVisible());
			availableOption.setVersionChangesOption(up.getPrfMenu().getPrfHelp().isChangelogVisible());
			availableOption.setProjectWebOption(up.getPrfMenu().getPrfHelp().isWebSiteVisible());
			availableOption.setAboutOption(up.getPrfMenu().getPrfHelp().isAboutVisible());

			availableOption.setMediaPlayerOption(true);
			availableOption.setImageViewerOption(true);

			workspace.setAvailableOption(availableOption);

			// Reports
			for (Long rpId : up.getPrfMisc().getReports()) {
				Report report = ReportDAO.findByPk(rpId);

				if (report != null && report.isActive()) {
					workspace.getReports().add(GWTUtil.copy(report, ReportUtils.getReportParameters(rpId)));
				}
			}

			// Toolbar
			// Is visible on toolbar && available option too
			GWTProfileToolbar profileToolbar = new GWTProfileToolbar();
			profileToolbar.setAddDocumentVisible(up.getPrfToolbar().isAddDocumentVisible() && availableOption.isAddDocumentOption());
			profileToolbar.setAddPropertyGroupVisible(up.getPrfToolbar().isAddPropertyGroupVisible()
					&& availableOption.isAddPropertyGroupOption());
			profileToolbar.setAddSubscriptionVisible(up.getPrfToolbar().isAddSubscriptionVisible()
					&& availableOption.isAddSubscriptionOption());
			profileToolbar.setCancelCheckoutVisible(up.getPrfToolbar().isCancelCheckoutVisible()
					&& availableOption.isCancelCheckoutOption());
			profileToolbar.setCheckoutVisible(up.getPrfToolbar().isCheckoutVisible() && availableOption.isCheckoutOption());
			profileToolbar.setCheckinVisible(up.getPrfToolbar().isCheckinVisible() && availableOption.isCheckinOption());
			profileToolbar.setCreateFolderVisible(up.getPrfToolbar().isCreateFolderVisible() && availableOption.isCreateFolderOption());
			profileToolbar.setDeleteVisible(up.getPrfToolbar().isDeleteVisible() && availableOption.isDeleteOption());
			profileToolbar.setDownloadPdfVisible(up.getPrfToolbar().isDownloadPdfVisible() && availableOption.isDownloadPdfOption());
			profileToolbar.setDownloadVisible(up.getPrfToolbar().isDownloadVisible() && availableOption.isDownloadOption());
			profileToolbar.setFindDocumentVisible(up.getPrfToolbar().isFindDocumentVisible() && availableOption.isFindDocumentOption());
			profileToolbar.setFindFolderVisible(up.getPrfToolbar().isFindFolderVisible() && availableOption.isFindFolderOption());
			profileToolbar.setSimilarDocumentVisible(up.getPrfToolbar().isSimilarDocumentVisible()
					&& availableOption.isSimilarDocumentVisible());
			profileToolbar.setHomeVisible(up.getPrfToolbar().isHomeVisible() && availableOption.isHomeOption());
			profileToolbar.setLockVisible(up.getPrfToolbar().isLockVisible() && availableOption.isLockOption());
			profileToolbar.setPrintVisible(up.getPrfToolbar().isPrintVisible() && workspace.isPrintPreview());
			profileToolbar.setRefreshVisible(up.getPrfToolbar().isRefreshVisible() && availableOption.isRefreshOption());
			profileToolbar.setRemovePropertyGroupVisible(up.getPrfToolbar().isRemovePropertyGroupVisible()
					&& availableOption.isRemovePropertyGroupOption());
			profileToolbar.setRemoveSubscriptionVisible(up.getPrfToolbar().isRemoveSubscriptionVisible()
					&& availableOption.isRemoveSubscriptionOption());
			profileToolbar.setStartWorkflowVisible(up.getPrfToolbar().isStartWorkflowVisible() && availableOption.isWorkflowOption());
			profileToolbar.setUnlockVisible(up.getPrfToolbar().isUnlockVisible() && availableOption.isUnLockOption());
			profileToolbar.setSplitterResizeVisible(up.getPrfToolbar().isSplitterResizeVisible());
			profileToolbar.setOmrVisible(up.getPrfToolbar().isOmrVisible());
			workspace.setProfileToolbar(profileToolbar);

			// file broser
			GWTProfileFileBrowser profileFileBrowser = new GWTProfileFileBrowser();
			profileFileBrowser.setStatusVisible(up.getPrfFileBrowser().isStatusVisible());
			profileFileBrowser.setMassiveVisible(up.getPrfFileBrowser().isMassiveVisible());
			profileFileBrowser.setIconVisible(up.getPrfFileBrowser().isIconVisible());
			profileFileBrowser.setNameVisible(up.getPrfFileBrowser().isNameVisible());
			profileFileBrowser.setSizeVisible(up.getPrfFileBrowser().isSizeVisible());
			profileFileBrowser.setLastModifiedVisible(up.getPrfFileBrowser().isLastModifiedVisible());
			profileFileBrowser.setAuthorVisible(up.getPrfFileBrowser().isAuthorVisible());
			profileFileBrowser.setVersionVisible(up.getPrfFileBrowser().isVersionVisible());
			profileFileBrowser.setColumn0(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn0()));
			profileFileBrowser.setColumn1(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn1()));
			profileFileBrowser.setColumn2(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn2()));
			profileFileBrowser.setColumn3(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn3()));
			profileFileBrowser.setColumn4(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn4()));
			profileFileBrowser.setColumn5(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn5()));
			profileFileBrowser.setColumn6(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn6()));
			profileFileBrowser.setColumn7(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn7()));
			profileFileBrowser.setColumn8(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn8()));
			profileFileBrowser.setColumn9(GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn9()));
			profileFileBrowser.setExtraColumns(profileFileBrowser.getColumn0() != null || profileFileBrowser.getColumn1() != null
					|| profileFileBrowser.getColumn2() != null || profileFileBrowser.getColumn3() != null
					|| profileFileBrowser.getColumn4() != null || profileFileBrowser.getColumn5() != null
					|| profileFileBrowser.getColumn6() != null || profileFileBrowser.getColumn7() != null
					|| profileFileBrowser.getColumn8() != null || profileFileBrowser.getColumn9() != null);
			profileFileBrowser.setStatusWidth(up.getPrfFileBrowser().getStatusWidth());
			profileFileBrowser.setMassiveWidth(up.getPrfFileBrowser().getMassiveWidth());
			profileFileBrowser.setIconWidth(up.getPrfFileBrowser().getIconWidth());
			profileFileBrowser.setNameWidth(up.getPrfFileBrowser().getNameWidth());
			profileFileBrowser.setSizeWidth(up.getPrfFileBrowser().getSizeWidth());
			profileFileBrowser.setLastModifiedWidth(up.getPrfFileBrowser().getLastModifiedWidth());
			profileFileBrowser.setAuthorWidth(up.getPrfFileBrowser().getAuthorWidth());
			profileFileBrowser.setVersionWidth(up.getPrfFileBrowser().getVersionWidth());
			profileFileBrowser.setColumn0Width(up.getPrfFileBrowser().getColumn0Width());
			profileFileBrowser.setColumn1Width(up.getPrfFileBrowser().getColumn1Width());
			profileFileBrowser.setColumn2Width(up.getPrfFileBrowser().getColumn2Width());
			profileFileBrowser.setColumn3Width(up.getPrfFileBrowser().getColumn3Width());
			profileFileBrowser.setColumn4Width(up.getPrfFileBrowser().getColumn4Width());
			profileFileBrowser.setColumn5Width(up.getPrfFileBrowser().getColumn5Width());
			profileFileBrowser.setColumn6Width(up.getPrfFileBrowser().getColumn6Width());
			profileFileBrowser.setColumn7Width(up.getPrfFileBrowser().getColumn7Width());
			profileFileBrowser.setColumn8Width(up.getPrfFileBrowser().getColumn8Width());
			profileFileBrowser.setColumn9Width(up.getPrfFileBrowser().getColumn9Width());
			workspace.setProfileFileBrowser(profileFileBrowser);

			// pagination
			GWTProfilePagination profilePagination = new GWTProfilePagination();
			profilePagination.setMiscFilterEnabled(up.getPrfPagination().isMiscFilterEnabled());
			profilePagination.setTypeFilterEnabled(up.getPrfPagination().isTypeFilterEnabled());
			profilePagination.setPaginationEnabled(up.getPrfPagination().isPaginationEnabled());
			profilePagination.setPageList(up.getPrfPagination().getPageList());
			profilePagination.setShowFoldersEnabled(up.getPrfPagination().isShowFoldersEnabled());
			profilePagination.setShowDocumentsEnabled(up.getPrfPagination().isShowDocumentsEnabled());
			profilePagination.setShowMailsEnabled(up.getPrfPagination().isShowMailsEnabled());
			workspace.setProfilePagination(profilePagination);

			// Setting available UI languages
			List<GWTLanguage> langs = new ArrayList<GWTLanguage>();
			for (Language lang : LanguageDAO.findAll()) {
				langs.add(GWTUtil.copy(lang));
			}
			workspace.setLangs(langs);

			// Mimetypes
			List<GWTMimeType> mimeTypes = new ArrayList<GWTMimeType>();
			for (MimeType mt : MimeTypeDAO.findBySearch()) {
				mimeTypes.add(GWTUtil.copy(mt));
			}
			workspace.setMimeTypes(mimeTypes);

			User user = new User();
			if (Config.PRINCIPAL_ADAPTER.equals(DatabasePrincipalAdapter.class.getCanonicalName())) {
				user = AuthDAO.findUserByPk(getThreadLocalRequest().getRemoteUser());

				if (user != null) {
					workspace.setEmail(user.getEmail());
				}
			} else {
				user.setId(getThreadLocalRequest().getRemoteUser());
				user.setName("");
				user.setEmail("");
				user.setActive(true);
				user.setPassword("");
			}

			for (MailAccount mailAccount : MailAccountDAO.findByUser(getThreadLocalRequest().getRemoteUser(), true)) {
				workspace.setMailProtocol(mailAccount.getMailProtocol());
				workspace.setMailHost(mailAccount.getMailHost());
				workspace.setMailUser(mailAccount.getMailUser());
				workspace.setMailPassword(mailAccount.getMailPassword());
				workspace.setMailFolder(mailAccount.getMailFolder());
				workspace.setMailID(mailAccount.getId());
			}

			if (user != null) {
				workspace.setRoleList(OKMAuth.getInstance().getRolesByUser(null, user.getId()));
			} else {
				log.warn("User is null! Please, check principal.adapter={}", Config.PRINCIPAL_ADAPTER);
			}

			if (Config.PRINCIPAL_ADAPTER.equals(DatabasePrincipalAdapter.class.getCanonicalName())) {
				workspace.setChangePassword(true);
			} else {
				workspace.setChangePassword(false);
			}

			// Saving workspace to session ( will be used to get extracolumn data )
			saveUserWorkspaceSession(workspace);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		}

		return workspace;
	}

	@Override
	public Double getUserDocumentsSize() throws OKMException {
		log.debug("getUserDocumentsSize()");
		Double docSize = new Double(0);
		updateSessionManager();

		try {
			docSize = new Double(OKMDashboard.getInstance().getUserDocumentsSize(null));
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_Database), e.getMessage());
		}

		return docSize;
	}

	@Override
	public void updateUserWorkspace(GWTWorkspace workspace) throws OKMException {
		log.debug("updateUserWorkspace()");
		updateSessionManager();

		// Disable user configuration modification in demo
		if (!Config.SYSTEM_DEMO) {
			try {
				String userId = getThreadLocalRequest().getRemoteUser();
				// Can change password
				if (Config.PRINCIPAL_ADAPTER.equals(DatabasePrincipalAdapter.class.getCanonicalName())) {
					if (!workspace.getPassword().isEmpty()) {
						AuthDAO.updateUserPassword(userId, workspace.getPassword());
					}

					if (!workspace.getEmail().isEmpty()) {
						AuthDAO.updateUserEmail(userId, workspace.getEmail());
					}
				}

				if (!workspace.getMailHost().isEmpty() && !workspace.getMailUser().isEmpty() && !workspace.getMailPassword().isEmpty()) {
					MailAccount mailAccount = new MailAccount();
					mailAccount.setActive(true);
					mailAccount.setMailProtocol(workspace.getMailProtocol());
					mailAccount.setMailFolder(workspace.getMailFolder());
					mailAccount.setMailHost(workspace.getMailHost());
					mailAccount.setMailPassword(workspace.getMailPassword());
					mailAccount.setMailUser(workspace.getMailUser());
					mailAccount.setUser(userId);
					mailAccount.setId(workspace.getMailID());

					if (MailAccountDAO.findByPk(workspace.getMailID()) != null) {
						MailAccountDAO.update(mailAccount);

						if (!mailAccount.getMailPassword().isEmpty()) {
							MailAccountDAO.updatePassword(mailAccount.getId(), mailAccount.getMailPassword());
						}
					} else {
						MailAccountDAO.create(mailAccount);
					}
				}
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_SQL), e.getMessage());
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
			}
		}
	}

	@Override
	public void deleteMailAccount(long id) throws OKMException {
		log.debug("deleteMailAccount({})", id);
		updateSessionManager();

		// Disable user configuration modification in demo
		if (!Config.SYSTEM_DEMO) {
			try {
				MailAccountDAO.delete(id);
			} catch (DatabaseException e) {
				throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_SQL), e.getMessage());
			}
		}
	}

	@Override
	public String isValidPassword(String password) throws OKMException {
		log.debug("isValidPassword()");
		String msg = "";
		updateSessionManager();

		try {
			PasswordValidator passwordValidator = ValidatorFactory.getPasswordValidator();
			try {
				passwordValidator.Validate(password);
			} catch (ValidatorException e) {
				msg = e.getMessage();
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkspaceService, ErrorCode.CAUSE_Repository), e.getMessage());
		}

		return msg;
	}
}
