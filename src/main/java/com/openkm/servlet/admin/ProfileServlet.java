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

package com.openkm.servlet.admin;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMWorkflow;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.core.*;
import com.openkm.dao.ProfileDAO;
import com.openkm.dao.ReportDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.extension.dao.ExtensionDAO;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * User profiles servlet
 */
public class ProfileServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ProfileServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();
		updateSessionManager(request);

		try {
			if (action.equals("create")) {
				create(userId, request, response);
			} else if (action.equals("edit")) {
				edit(userId, request, response);
			} else if (action.equals("delete")) {
				delete(userId, request, response);
			} else if (action.equals("clone")) {
				clone(userId, request, response);
			}

			if (action.equals("") || WebUtils.getBoolean(request, "persist")) {
				list(userId, request, response);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * New user
	 */
	private void create(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			AccessDeniedException, RepositoryException, ParseException, WorkflowException {
		log.debug("create({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			Profile prf = getUserProfile(request);
			long id = ProfileDAO.create(prf);

			// Activity log
			UserActivity.log(userId, "ADMIN_USER_PROFILE_CREATE", Long.toString(id), null, prf.toString());
		} else {
			ServletContext sc = getServletContext();
			Profile prf = new Profile();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("exts", ExtensionDAO.findAll());
			sc.setAttribute("reps", ReportDAO.findAll());
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.setAttribute("prf", prf);
			sc.getRequestDispatcher("/admin/profile_edit.jsp").forward(request, response);
		}

		log.debug("create: void");
	}

	/**
	 * Edit user
	 */
	private void edit(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException, NoSuchAlgorithmException, AccessDeniedException, RepositoryException, ParseException, WorkflowException {
		log.debug("edit({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			Profile prf = getUserProfile(request);
			ProfileDAO.update(prf);

			// Activity log
			UserActivity.log(userId, "ADMIN_USER_PROFILE_EDIT", Long.toString(prf.getId()), null, prf.toString());
		} else {
			ServletContext sc = getServletContext();
			int prfId = WebUtils.getInt(request, "prf_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("exts", ExtensionDAO.findAll());
			sc.setAttribute("reps", ReportDAO.findAll());
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findLatestProcessDefinitions(null));
			sc.setAttribute("prf", ProfileDAO.findByPk(prfId));
			List<String> pgProperties = new ArrayList<String>();

			for (PropertyGroup pgrp : OKMPropertyGroup.getInstance().getAllGroups(null)) {
				for (FormElement fe : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, pgrp.getName())) {
					pgProperties.add(fe.getName());
				}
			}

			sc.setAttribute("pgprops", pgProperties);
			sc.getRequestDispatcher("/admin/profile_edit.jsp").forward(request, response);
		}

		log.debug("edit: void");
	}

	/**
	 * Update user
	 */
	private void delete(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException, RepositoryException, ParseException,
			WorkflowException {
		log.debug("delete({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			int prfId = WebUtils.getInt(request, "prf_id");
			ProfileDAO.delete(prfId);

			// Activity log
			UserActivity.log(userId, "ADMIN_USER_PROFILE_DELETE", Integer.toString(prfId), null, null);
		} else {
			ServletContext sc = getServletContext();
			int prfId = WebUtils.getInt(request, "prf_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("exts", ExtensionDAO.findAll());
			sc.setAttribute("reps", ReportDAO.findAll());
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.setAttribute("prf", ProfileDAO.findByPk(prfId));
			sc.getRequestDispatcher("/admin/profile_edit.jsp").forward(request, response);
		}

		log.debug("delete: void");
	}

	/**
	 * Clone profile
	 */
	private void clone(String userId, HttpServletRequest request, HttpServletResponse response) throws DatabaseException,
			IOException, ParseException, AccessDeniedException, RepositoryException, WorkflowException, ServletException {
		log.debug("clone({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			Profile prf = getUserProfile(request);
			long id = ProfileDAO.create(prf);

			// Activity log
			UserActivity.log(userId, "ADMIN_USER_PROFILE_CLONE", Long.toString(id), null, prf.toString());
		} else {
			ServletContext sc = getServletContext();
			int prfId = WebUtils.getInt(request, "prf_id");
			Profile prf = ProfileDAO.findByPk(prfId);
			prf.setName(prf.getName().concat(" (cloned)"));
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("exts", ExtensionDAO.findAll());
			sc.setAttribute("reps", ReportDAO.findAll());
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.setAttribute("prf", prf);
			sc.getRequestDispatcher("/admin/profile_edit.jsp").forward(request, response);
		}

		log.debug("edit: void");
	}

	/**
	 * List user profiles
	 */
	private void list(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		sc.setAttribute("userProfiles", ProfileDAO.findAll(false));
		sc.getRequestDispatcher("/admin/profile_list.jsp").forward(request, response);
		log.debug("list: void");
	}

	/**
	 * Fille user profile object
	 */
	private Profile getUserProfile(HttpServletRequest request) {
		Profile prf = new Profile();

		prf.setId(WebUtils.getInt(request, "prf_id"));
		prf.setName(WebUtils.getString(request, "prf_name"));
		prf.setActive(WebUtils.getBoolean(request, "prf_active"));

		// Misc
		prf.getPrfMisc().setUserQuota(WebUtils.getLong(request, "prf_misc_user_quota"));
		prf.getPrfMisc().setAdvancedFilters(WebUtils.getBoolean(request, "prf_misc_advanced_filter"));
		prf.getPrfMisc().setWebSkin(WebUtils.getString(request, "prf_misc_web_skin"));
		prf.getPrfMisc().setPrintPreview(WebUtils.getBoolean(request, "prf_misc_print_preview"));
		prf.getPrfMisc().setKeywordsEnabled(WebUtils.getBoolean(request, "prf_misc_keywords_enabled"));
		prf.getPrfMisc().setUploadNotifyUsers(WebUtils.getBoolean(request, "prf_misc_upload_notify_users"));
		prf.getPrfMisc().setNotifyExternalUsers(WebUtils.getBoolean(request, "prf_misc_notify_external_users"));
		prf.getPrfMisc().setAcrobatPluginPreview(WebUtils.getBoolean(request, "prf_misc_acrobat_plugin_preview"));
		prf.getPrfMisc().setIncreaseVersion(WebUtils.getBoolean(request, "prf_misc_increase_version"));
		prf.getPrfMisc().setExtensions(new HashSet<String>(WebUtils.getStringList(request, "prf_misc_extensions")));
		prf.getPrfMisc().setReports(new HashSet<Long>(WebUtils.getLongList(request, "prf_misc_reports")));
		prf.getPrfMisc().setWorkflows(new HashSet<String>(WebUtils.getStringList(request, "prf_misc_workflows")));

		// Wizard
		prf.getPrfWizard().setKeywordsEnabled(WebUtils.getBoolean(request, "prf_wizard_keywords"));
		prf.getPrfWizard().setCategoriesEnabled(WebUtils.getBoolean(request, "prf_wizard_categories"));
		prf.getPrfWizard().setPropertyGroups(new HashSet<String>(WebUtils.getStringList(request, "prf_wizard_property_groups")));
		prf.getPrfWizard().setWorkflows(new HashSet<String>(WebUtils.getStringList(request, "prf_wizard_workflows")));

		// Chat
		prf.getPrfChat().setChatEnabled(WebUtils.getBoolean(request, "prf_chat_enabled"));
		prf.getPrfChat().setAutoLoginEnabled(WebUtils.getBoolean(request, "prf_chat_auto_login"));

		// Pagination
		prf.getPrfPagination().setPaginationEnabled(WebUtils.getBoolean(request, "prf_pagination_enabled"));
		prf.getPrfPagination().setPageList(WebUtils.getString(request, "prf_pagination_page_list"));
		prf.getPrfPagination().setTypeFilterEnabled(WebUtils.getBoolean(request, "prf_pagination_type_filter_enabled"));
		prf.getPrfPagination().setMiscFilterEnabled(WebUtils.getBoolean(request, "prf_pagination_misc_filter_enabled"));
		prf.getPrfPagination().setShowFoldersEnabled(WebUtils.getBoolean(request, "prf_pagination_show_folders_enabled"));
		prf.getPrfPagination().setShowDocumentsEnabled(WebUtils.getBoolean(request, "prf_pagination_show_documents_enabled"));
		prf.getPrfPagination().setShowMailsEnabled(WebUtils.getBoolean(request, "prf_pagination_show_mails_enabled"));

		// Stack
		prf.getPrfStack().setTaxonomyVisible(WebUtils.getBoolean(request, "prf_stack_taxonomy_visible"));
		prf.getPrfStack().setCategoriesVisible(WebUtils.getBoolean(request, "prf_stack_categories_visible"));
		prf.getPrfStack().setThesaurusVisible(WebUtils.getBoolean(request, "prf_stack_thesaurus_visible"));
		prf.getPrfStack().setTemplatesVisible(WebUtils.getBoolean(request, "prf_stack_templates_visible"));
		prf.getPrfStack().setPersonalVisible(WebUtils.getBoolean(request, "prf_stack_personal_visible"));
		prf.getPrfStack().setMailVisible(WebUtils.getBoolean(request, "prf_stack_mail_visible"));
		prf.getPrfStack().setMetadataVisible(WebUtils.getBoolean(request, "prf_stack_metadata_visible"));
		prf.getPrfStack().setTrashVisible(WebUtils.getBoolean(request, "prf_stack_trash_visible"));

		// Menu
		prf.getPrfMenu().setFileVisible(WebUtils.getBoolean(request, "prf_menu_file_visible"));
		prf.getPrfMenu().setEditVisible(WebUtils.getBoolean(request, "prf_menu_edit_visible"));
		prf.getPrfMenu().setToolsVisible(WebUtils.getBoolean(request, "prf_menu_tools_visible"));
		prf.getPrfMenu().setBookmarksVisible(WebUtils.getBoolean(request, "prf_menu_bookmarks_visible"));
		prf.getPrfMenu().setTemplatesVisible(WebUtils.getBoolean(request, "prf_menu_templates_visible"));
		prf.getPrfMenu().setHelpVisible(WebUtils.getBoolean(request, "prf_menu_help_visible"));

		// Menu File
		prf.getPrfMenu().getPrfFile().setCreateFolderVisible(WebUtils.getBoolean(request, "prf_menu_file_create_folder_visible"));
		prf.getPrfMenu().getPrfFile().setFindFolderVisible(WebUtils.getBoolean(request, "prf_menu_file_find_folder_visible"));
		prf.getPrfMenu().getPrfFile().setFindDocumentVisible(WebUtils.getBoolean(request, "prf_menu_file_find_document_visible"));
		prf.getPrfMenu().getPrfFile().setSimilarDocumentVisible(WebUtils.getBoolean(request, "prf_menu_file_similar_document_visible"));
		prf.getPrfMenu().getPrfFile().setGoFolderVisible(WebUtils.getBoolean(request, "prf_menu_file_go_folder_visible"));
		prf.getPrfMenu().getPrfFile().setDownloadVisible(WebUtils.getBoolean(request, "prf_menu_file_download_visible"));
		prf.getPrfMenu().getPrfFile().setDownloadPdfVisible(WebUtils.getBoolean(request, "prf_menu_file_download_pdf_visible"));
		prf.getPrfMenu().getPrfFile().setAddDocumentVisible(WebUtils.getBoolean(request, "prf_menu_file_add_document_visible"));
		prf.getPrfMenu().getPrfFile().setStartWorkflowVisible(WebUtils.getBoolean(request, "prf_menu_file_start_workflow_visible"));
		prf.getPrfMenu().getPrfFile().setRefreshVisible(WebUtils.getBoolean(request, "prf_menu_file_refresh_visible"));
		prf.getPrfMenu().getPrfFile().setExportVisible(WebUtils.getBoolean(request, "prf_menu_file_export_visible"));
		prf.getPrfMenu().getPrfFile().setCreateFromTemplateVisible(WebUtils.getBoolean(request, "prf_menu_file_create_from_template_visible"));
		prf.getPrfMenu().getPrfFile().setPurgeVisible(WebUtils.getBoolean(request, "prf_menu_file_purge_visible"));
		prf.getPrfMenu().getPrfFile().setPurgeTrashVisible(WebUtils.getBoolean(request, "prf_menu_file_purge_trash_visible"));
		prf.getPrfMenu().getPrfFile().setRestoreVisible(WebUtils.getBoolean(request, "prf_menu_file_restore_visible"));
		prf.getPrfMenu().getPrfFile().setSendDocumentLinkVisible(WebUtils.getBoolean(request, "prf_menu_file_send_document_link_visible"));
		prf.getPrfMenu().getPrfFile().setSendDocumentAttachmentVisible(WebUtils.getBoolean(request, "prf_menu_file_send_document_attachment_visible"));
		prf.getPrfMenu().getPrfFile().setForwardMailVisible(WebUtils.getBoolean(request, "prf_menu_file_forward_mail_visible"));

		// Menu Bookmarks
		prf.getPrfMenu().getPrfBookmark().setManageBookmarksVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_manage_bookmarks_visible"));
		prf.getPrfMenu().getPrfBookmark().setAddBookmarkVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_add_bookmark_visible"));
		prf.getPrfMenu().getPrfBookmark().setSetHomeVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_set_home_visible"));
		prf.getPrfMenu().getPrfBookmark().setGoHomeVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_go_home_visible"));

		// Menu Tools
		prf.getPrfMenu().getPrfTool().setLanguagesVisible(WebUtils.getBoolean(request, "prf_menu_tool_languages_visible"));
		prf.getPrfMenu().getPrfTool().setSkinVisible(WebUtils.getBoolean(request, "prf_menu_tool_skin_visible"));
		prf.getPrfMenu().getPrfTool().setDebugVisible(WebUtils.getBoolean(request, "prf_menu_tool_debug_visible"));
		prf.getPrfMenu().getPrfTool().setAdministrationVisible(WebUtils.getBoolean(request, "prf_menu_tool_administration_visible"));
		prf.getPrfMenu().getPrfTool().setPreferencesVisible(WebUtils.getBoolean(request, "prf_menu_tool_preferences_visible"));
		prf.getPrfMenu().getPrfTool().setOmrVisible(WebUtils.getBoolean(request, "prf_menu_tool_omr_visible"));
		prf.getPrfMenu().getPrfTool().setConvertVisible(WebUtils.getBoolean(request, "prf_menu_tool_convert_visible"));

		// Menu Edit
		prf.getPrfMenu().getPrfEdit().setRenameVisible(WebUtils.getBoolean(request, "prf_menu_edit_rename_visible"));
		prf.getPrfMenu().getPrfEdit().setCopyVisible(WebUtils.getBoolean(request, "prf_menu_edit_copy_visible"));
		prf.getPrfMenu().getPrfEdit().setMoveVisible(WebUtils.getBoolean(request, "prf_menu_edit_move_visible"));
		prf.getPrfMenu().getPrfEdit().setLockVisible(WebUtils.getBoolean(request, "prf_menu_edit_lock_visible"));
		prf.getPrfMenu().getPrfEdit().setUnlockVisible(WebUtils.getBoolean(request, "prf_menu_edit_unlock_visible"));
		prf.getPrfMenu().getPrfEdit().setCheckInVisible(WebUtils.getBoolean(request, "prf_menu_edit_check_in_visible"));
		prf.getPrfMenu().getPrfEdit().setCheckOutVisible(WebUtils.getBoolean(request, "prf_menu_edit_check_out_visible"));
		prf.getPrfMenu().getPrfEdit().setCancelCheckOutVisible(WebUtils.getBoolean(request, "prf_menu_edit_cancel_check_out_visible"));
		prf.getPrfMenu().getPrfEdit().setDeleteVisible(WebUtils.getBoolean(request, "prf_menu_edit_delete_visible"));
		prf.getPrfMenu().getPrfEdit().setAddSubscriptionVisible(WebUtils.getBoolean(request, "prf_menu_edit_add_subscription_visible"));
		prf.getPrfMenu().getPrfEdit().setRemoveSubscriptionVisible(WebUtils.getBoolean(request, "prf_menu_edit_remove_subscription_visible"));
		prf.getPrfMenu().getPrfEdit().setAddPropertyGroupVisible(WebUtils.getBoolean(request, "prf_menu_edit_add_property_group_visible"));
		prf.getPrfMenu().getPrfEdit().setUpdatePropertyGroupVisible(WebUtils.getBoolean(request, "prf_menu_edit_update_property_group_visible"));
		prf.getPrfMenu().getPrfEdit().setRemovePropertyGroupVisible(WebUtils.getBoolean(request, "prf_menu_edit_remove_property_group_visible"));
		prf.getPrfMenu().getPrfEdit().setAddNoteVisible(WebUtils.getBoolean(request, "prf_menu_edit_add_note_visible"));
		prf.getPrfMenu().getPrfEdit().setRemoveNoteVisible(WebUtils.getBoolean(request, "prf_menu_edit_remove_note_visible"));
		prf.getPrfMenu().getPrfEdit().setAddCategoryVisible(WebUtils.getBoolean(request, "prf_menu_edit_add_category_visible"));
		prf.getPrfMenu().getPrfEdit().setRemoveCategoryVisible(WebUtils.getBoolean(request, "prf_menu_edit_remove_category_visible"));
		prf.getPrfMenu().getPrfEdit().setAddKeywordVisible(WebUtils.getBoolean(request, "prf_menu_edit_add_keyword_visible"));
		prf.getPrfMenu().getPrfEdit().setRemoveKeywordVisible(WebUtils.getBoolean(request, "prf_menu_edit_remove_keyword_visible"));
		prf.getPrfMenu().getPrfEdit().setMergePdfVisible(WebUtils.getBoolean(request, "prf_menu_edit_merge_pdf_visible"));

		// Menu Help
		prf.getPrfMenu().getPrfHelp().setHelpVisible(WebUtils.getBoolean(request, "prf_menu_help_help_visible"));
		prf.getPrfMenu().getPrfHelp().setDocumentationVisible(WebUtils.getBoolean(request, "prf_menu_help_documentation_visible"));
		prf.getPrfMenu().getPrfHelp().setBugTrackingVisible(WebUtils.getBoolean(request, "prf_menu_help_bug_tracking_visible"));
		prf.getPrfMenu().getPrfHelp().setSupportVisible(WebUtils.getBoolean(request, "prf_menu_help_support_visible"));
		prf.getPrfMenu().getPrfHelp().setForumVisible(WebUtils.getBoolean(request, "prf_menu_help_forum_visible"));
		prf.getPrfMenu().getPrfHelp().setChangelogVisible(WebUtils.getBoolean(request, "prf_menu_help_changelog_visible"));
		prf.getPrfMenu().getPrfHelp().setWebSiteVisible(WebUtils.getBoolean(request, "prf_menu_help_web_site_visible"));
		prf.getPrfMenu().getPrfHelp().setAboutVisible(WebUtils.getBoolean(request, "prf_menu_help_about_visible"));

		// Tab
		prf.getPrfTab().setDefaultTab(WebUtils.getString(request, "prf_tab_default"));
		prf.getPrfTab().setDesktopVisible(WebUtils.getBoolean(request, "prf_tab_desktop_visible"));
		prf.getPrfTab().setSearchVisible(WebUtils.getBoolean(request, "prf_tab_search_visible"));
		prf.getPrfTab().setDashboardVisible(WebUtils.getBoolean(request, "prf_tab_dashboard_visible"));
		prf.getPrfTab().setAdministrationVisible(WebUtils.getBoolean(request, "prf_tab_administration_visible"));

		// Tab Document
		prf.getPrfTab().getPrfDocument().setPropertiesVisible(WebUtils.getBoolean(request, "prf_tab_document_properties_visible"));
		prf.getPrfTab().getPrfDocument().setSecurityVisible(WebUtils.getBoolean(request, "prf_tab_document_security_visible"));
		prf.getPrfTab().getPrfDocument().setNotesVisible(WebUtils.getBoolean(request, "prf_tab_document_notes_visible"));
		prf.getPrfTab().getPrfDocument().setVersionsVisible(WebUtils.getBoolean(request, "prf_tab_document_versions_visible"));
		prf.getPrfTab().getPrfDocument().setVersionDownloadVisible(WebUtils.getBoolean(request, "prf_tab_document_version_download_visible"));
		prf.getPrfTab().getPrfDocument().setPreviewVisible(WebUtils.getBoolean(request, "prf_tab_document_preview_visible"));
		prf.getPrfTab().getPrfDocument().setPropertyGroupsVisible(WebUtils.getBoolean(request, "prf_tab_document_property_groups_visible"));

		// Tab Folder
		prf.getPrfTab().getPrfFolder().setPropertiesVisible(WebUtils.getBoolean(request, "prf_tab_folder_properties_visible"));
		prf.getPrfTab().getPrfFolder().setSecurityVisible(WebUtils.getBoolean(request, "prf_tab_folder_security_visible"));
		prf.getPrfTab().getPrfFolder().setNotesVisible(WebUtils.getBoolean(request, "prf_tab_folder_notes_visible"));

		// Tab Mail
		prf.getPrfTab().getPrfMail().setPropertiesVisible(WebUtils.getBoolean(request, "prf_tab_mail_properties_visible"));
		prf.getPrfTab().getPrfMail().setSecurityVisible(WebUtils.getBoolean(request, "prf_tab_mail_security_visible"));
		prf.getPrfTab().getPrfMail().setPreviewVisible(WebUtils.getBoolean(request, "prf_tab_mail_preview_visible"));
		prf.getPrfTab().getPrfMail().setNotesVisible(WebUtils.getBoolean(request, "prf_tab_mail_notes_visible"));

		// Dashboard
		prf.getPrfDashboard().setUserVisible(WebUtils.getBoolean(request, "prf_dashboard_user_visible"));
		prf.getPrfDashboard().setMailVisible(WebUtils.getBoolean(request, "prf_dashboard_mail_visible"));
		prf.getPrfDashboard().setNewsVisible(WebUtils.getBoolean(request, "prf_dashboard_news_visible"));
		prf.getPrfDashboard().setGeneralVisible(WebUtils.getBoolean(request, "prf_dashboard_general_visible"));
		prf.getPrfDashboard().setWorkflowVisible(WebUtils.getBoolean(request, "prf_dashboard_workflow_visible"));
		prf.getPrfDashboard().setKeywordsVisible(WebUtils.getBoolean(request, "prf_dashboard_keywords_visible"));

		// Toolbar
		prf.getPrfToolbar().setCreateFolderVisible(WebUtils.getBoolean(request, "prf_toolbar_create_folder_visible"));
		prf.getPrfToolbar().setFindFolderVisible(WebUtils.getBoolean(request, "prf_toolbar_find_folder_visible"));
		prf.getPrfToolbar().setFindDocumentVisible(WebUtils.getBoolean(request, "prf_toolbar_find_document_visible"));
		prf.getPrfToolbar().setSimilarDocumentVisible(WebUtils.getBoolean(request, "prf_toolbar_similar_document_visible"));
		prf.getPrfToolbar().setDownloadVisible(WebUtils.getBoolean(request, "prf_toolbar_download_visible"));
		prf.getPrfToolbar().setDownloadPdfVisible(WebUtils.getBoolean(request, "prf_toolbar_download_pdf_visible"));
		prf.getPrfToolbar().setPrintVisible(WebUtils.getBoolean(request, "prf_toolbar_print_visible"));
		prf.getPrfToolbar().setLockVisible(WebUtils.getBoolean(request, "prf_toolbar_lock_visible"));
		prf.getPrfToolbar().setUnlockVisible(WebUtils.getBoolean(request, "prf_toolbar_unlock_visible"));
		prf.getPrfToolbar().setAddDocumentVisible(WebUtils.getBoolean(request, "prf_toolbar_add_document_visible"));
		prf.getPrfToolbar().setCheckoutVisible(WebUtils.getBoolean(request, "prf_toolbar_checkout_visible"));
		prf.getPrfToolbar().setCheckinVisible(WebUtils.getBoolean(request, "prf_toolbar_checkin_visible"));
		prf.getPrfToolbar().setCancelCheckoutVisible(WebUtils.getBoolean(request, "prf_toolbar_cancel_checkout_visible"));
		prf.getPrfToolbar().setDeleteVisible(WebUtils.getBoolean(request, "prf_toolbar_delete_visible"));
		prf.getPrfToolbar().setAddPropertyGroupVisible(WebUtils.getBoolean(request, "prf_toolbar_add_property_visible"));
		prf.getPrfToolbar().setRemovePropertyGroupVisible(WebUtils.getBoolean(request, "prf_toolbar_remove_property_visible"));
		prf.getPrfToolbar().setStartWorkflowVisible(WebUtils.getBoolean(request, "prf_toolbar_start_workflow_visible"));
		prf.getPrfToolbar().setAddSubscriptionVisible(WebUtils.getBoolean(request, "prf_toolbar_add_subscription_visible"));
		prf.getPrfToolbar().setRemoveSubscriptionVisible(WebUtils.getBoolean(request, "prf_toolbar_remove_subscription_visible"));
		prf.getPrfToolbar().setRefreshVisible(WebUtils.getBoolean(request, "prf_toolbar_refresh_visible"));
		prf.getPrfToolbar().setHomeVisible(WebUtils.getBoolean(request, "prf_toolbar_home_visible"));
		prf.getPrfToolbar().setSplitterResizeVisible(WebUtils.getBoolean(request, "prf_toolbar_splitter_resize_visible"));
		prf.getPrfToolbar().setOmrVisible(WebUtils.getBoolean(request, "prf_toolbar_omr_visible"));

		// File browser
		prf.getPrfFileBrowser().setStatusVisible(WebUtils.getBoolean(request, "prf_filebrowser_status_visible"));
		prf.getPrfFileBrowser().setMassiveVisible(WebUtils.getBoolean(request, "prf_filebrowser_massive_visible"));
		prf.getPrfFileBrowser().setIconVisible(WebUtils.getBoolean(request, "prf_filebrowser_icon_visible"));
		prf.getPrfFileBrowser().setNameVisible(WebUtils.getBoolean(request, "prf_filebrowser_name_visible"));
		prf.getPrfFileBrowser().setSizeVisible(WebUtils.getBoolean(request, "prf_filebrowser_size_visible"));
		prf.getPrfFileBrowser().setLastModifiedVisible(WebUtils.getBoolean(request, "prf_filebrowser_lastmod_visible"));
		prf.getPrfFileBrowser().setAuthorVisible(WebUtils.getBoolean(request, "prf_filebrowser_author_visible"));
		prf.getPrfFileBrowser().setVersionVisible(WebUtils.getBoolean(request, "prf_filebrowser_version_visible"));
		prf.getPrfFileBrowser().setColumn0(WebUtils.getString(request, "prf_filebrowser_column0"));
		prf.getPrfFileBrowser().setColumn1(WebUtils.getString(request, "prf_filebrowser_column1"));
		prf.getPrfFileBrowser().setColumn2(WebUtils.getString(request, "prf_filebrowser_column2"));
		prf.getPrfFileBrowser().setColumn3(WebUtils.getString(request, "prf_filebrowser_column3"));
		prf.getPrfFileBrowser().setColumn4(WebUtils.getString(request, "prf_filebrowser_column4"));
		prf.getPrfFileBrowser().setColumn5(WebUtils.getString(request, "prf_filebrowser_column5"));
		prf.getPrfFileBrowser().setColumn6(WebUtils.getString(request, "prf_filebrowser_column6"));
		prf.getPrfFileBrowser().setColumn7(WebUtils.getString(request, "prf_filebrowser_column7"));
		prf.getPrfFileBrowser().setColumn8(WebUtils.getString(request, "prf_filebrowser_column8"));
		prf.getPrfFileBrowser().setColumn9(WebUtils.getString(request, "prf_filebrowser_column9"));

		prf.getPrfFileBrowser().setStatusWidth(WebUtils.getString(request, "prf_filebrowser_status_width"));
		prf.getPrfFileBrowser().setMassiveWidth(WebUtils.getString(request, "prf_filebrowser_massive_width"));
		prf.getPrfFileBrowser().setIconWidth(WebUtils.getString(request, "prf_filebrowser_icon_width"));
		prf.getPrfFileBrowser().setNameWidth(WebUtils.getString(request, "prf_filebrowser_name_width"));
		prf.getPrfFileBrowser().setSizeWidth(WebUtils.getString(request, "prf_filebrowser_size_width"));
		prf.getPrfFileBrowser().setLastModifiedWidth(WebUtils.getString(request, "prf_filebrowser_lastmod_width"));
		prf.getPrfFileBrowser().setAuthorWidth(WebUtils.getString(request, "prf_filebrowser_author_width"));
		prf.getPrfFileBrowser().setVersionWidth(WebUtils.getString(request, "prf_filebrowser_version_width"));
		prf.getPrfFileBrowser().setColumn0Width(WebUtils.getString(request, "prf_filebrowser_column0_width"));
		prf.getPrfFileBrowser().setColumn1Width(WebUtils.getString(request, "prf_filebrowser_column1_width"));
		prf.getPrfFileBrowser().setColumn2Width(WebUtils.getString(request, "prf_filebrowser_column2_width"));
		prf.getPrfFileBrowser().setColumn3Width(WebUtils.getString(request, "prf_filebrowser_column3_width"));
		prf.getPrfFileBrowser().setColumn4Width(WebUtils.getString(request, "prf_filebrowser_column4_width"));
		prf.getPrfFileBrowser().setColumn5Width(WebUtils.getString(request, "prf_filebrowser_column5_width"));
		prf.getPrfFileBrowser().setColumn6Width(WebUtils.getString(request, "prf_filebrowser_column6_width"));
		prf.getPrfFileBrowser().setColumn7Width(WebUtils.getString(request, "prf_filebrowser_column7_width"));
		prf.getPrfFileBrowser().setColumn8Width(WebUtils.getString(request, "prf_filebrowser_column8_width"));
		prf.getPrfFileBrowser().setColumn9Width(WebUtils.getString(request, "prf_filebrowser_column9_width"));

		return prf;
	}
}
