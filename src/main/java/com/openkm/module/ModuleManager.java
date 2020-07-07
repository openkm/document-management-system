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

package com.openkm.module;

/**
 * Choose between Native Repository or Jackrabbit implementations.
 *
 * @author pavila
 */
public class ModuleManager {
	private static AuthModule authModule = null;
	private static RepositoryModule repositoryModule = null;
	private static FolderModule folderModule = null;
	private static DocumentModule documentModule = null;
	private static NoteModule noteModule = null;
	private static SearchModule searchModule = null;
	private static PropertyGroupModule propertyGroupModule = null;
	private static NotificationModule notificationModule = null;
	private static BookmarkModule bookmarkModule = null;
	private static DashboardModule dashboardModule = null;
	private static WorkflowModule workflowModule = null;
	private static ScriptingModule scriptingModule = null;
	private static StatsModule statsModule = null;
	private static MailModule mailModule = null;
	private static PropertyModule propertyModule = null;
	private static UserConfigModule userConfigModule = null;

	/**
	 *
	 */
	public static synchronized AuthModule getAuthModule() {
		if (authModule == null) {
			authModule = new com.openkm.module.db.DbAuthModule();
		}

		return authModule;
	}

	/**
	 *
	 */
	public static synchronized RepositoryModule getRepositoryModule() {
		if (repositoryModule == null) {
			repositoryModule = new com.openkm.module.db.DbRepositoryModule();
		}

		return repositoryModule;
	}

	/**
	 *
	 */
	public static synchronized FolderModule getFolderModule() {
		if (folderModule == null) {
			folderModule = new com.openkm.module.db.DbFolderModule();
		}

		return folderModule;
	}

	/**
	 *
	 */
	public static synchronized DocumentModule getDocumentModule() {
		if (documentModule == null) {
			documentModule = new com.openkm.module.db.DbDocumentModule();
		}

		return documentModule;
	}

	/**
	 *
	 */
	public static synchronized NoteModule getNoteModule() {
		if (noteModule == null) {
			noteModule = new com.openkm.module.db.DbNoteModule();
		}

		return noteModule;
	}

	/**
	 *
	 */
	public static synchronized SearchModule getSearchModule() {
		if (searchModule == null) {
			searchModule = new com.openkm.module.db.DbSearchModule();
		}

		return searchModule;
	}

	/**
	 *
	 */
	public static synchronized PropertyGroupModule getPropertyGroupModule() {
		if (propertyGroupModule == null) {
			propertyGroupModule = new com.openkm.module.db.DbPropertyGroupModule();
		}

		return propertyGroupModule;
	}

	/**
	 *
	 */
	public static synchronized NotificationModule getNotificationModule() {
		if (notificationModule == null) {
			notificationModule = new com.openkm.module.db.DbNotificationModule();
		}

		return notificationModule;
	}

	/**
	 *
	 */
	public static synchronized BookmarkModule getBookmarkModule() {
		if (bookmarkModule == null) {
			bookmarkModule = new com.openkm.module.db.DbBookmarkModule();
		}

		return bookmarkModule;
	}

	/**
	 *
	 */
	public static synchronized DashboardModule getDashboardModule() {
		if (dashboardModule == null) {
			dashboardModule = new com.openkm.module.db.DbDashboardModule();
		}

		return dashboardModule;
	}

	/**
	 *
	 */
	public static synchronized WorkflowModule getWorkflowModule() {
		if (workflowModule == null) {
			workflowModule = new com.openkm.module.db.DbWorkflowModule();
		}

		return workflowModule;
	}

	/**
	 *
	 */
	public static synchronized StatsModule getStatsModule() {
		if (statsModule == null) {
			statsModule = new com.openkm.module.db.DbStatsModule();
		}

		return statsModule;
	}

	/**
	 *
	 */
	public static synchronized MailModule getMailModule() {
		if (mailModule == null) {
			mailModule = new com.openkm.module.db.DbMailModule();
		}

		return mailModule;
	}

	/**
	 *
	 */
	public static synchronized PropertyModule getPropertyModule() {
		if (propertyModule == null) {
			propertyModule = new com.openkm.module.db.DbPropertyModule();
		}

		return propertyModule;
	}

	/**
	 *
	 */
	public static synchronized UserConfigModule getUserConfigModule() {
		if (userConfigModule == null) {
			userConfigModule = new com.openkm.module.db.DbUserConfigModule();
		}

		return userConfigModule;
	}
}
