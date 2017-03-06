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

package com.openkm.module;

import com.openkm.core.Config;

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
			if (Config.REPOSITORY_NATIVE) {
				authModule = new com.openkm.module.db.DbAuthModule();
			} else {
				// Other implementation
			}
		}

		return authModule;
	}

	/**
	 *
	 */
	public static synchronized RepositoryModule getRepositoryModule() {
		if (repositoryModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				repositoryModule = new com.openkm.module.db.DbRepositoryModule();
			} else {
				// Other implementation
			}
		}

		return repositoryModule;
	}

	/**
	 *
	 */
	public static synchronized FolderModule getFolderModule() {
		if (folderModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				folderModule = new com.openkm.module.db.DbFolderModule();
			} else {
				// Other implementation
			}
		}

		return folderModule;
	}

	/**
	 *
	 */
	public static synchronized DocumentModule getDocumentModule() {
		if (documentModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				documentModule = new com.openkm.module.db.DbDocumentModule();
			} else {
				// Other implementation
			}
		}

		return documentModule;
	}

	/**
	 *
	 */
	public static synchronized NoteModule getNoteModule() {
		if (noteModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				noteModule = new com.openkm.module.db.DbNoteModule();
			} else {
				// Other implementation
			}
		}

		return noteModule;
	}

	/**
	 *
	 */
	public static synchronized SearchModule getSearchModule() {
		if (searchModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				searchModule = new com.openkm.module.db.DbSearchModule();
			} else {
				// Other implementation
			}
		}

		return searchModule;
	}

	/**
	 *
	 */
	public static synchronized PropertyGroupModule getPropertyGroupModule() {
		if (propertyGroupModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				propertyGroupModule = new com.openkm.module.db.DbPropertyGroupModule();
			} else {
				// Other implementation
			}
		}

		return propertyGroupModule;
	}

	/**
	 *
	 */
	public static synchronized NotificationModule getNotificationModule() {
		if (notificationModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				notificationModule = new com.openkm.module.db.DbNotificationModule();
			} else {
				// Other implementation
			}
		}

		return notificationModule;
	}

	/**
	 *
	 */
	public static synchronized BookmarkModule getBookmarkModule() {
		if (bookmarkModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				bookmarkModule = new com.openkm.module.db.DbBookmarkModule();
			} else {
				// Other implementation
			}
		}

		return bookmarkModule;
	}

	/**
	 *
	 */
	public static synchronized DashboardModule getDashboardModule() {
		if (dashboardModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				dashboardModule = new com.openkm.module.db.DbDashboardModule();
			} else {
				// Other implementation
			}
		}

		return dashboardModule;
	}

	/**
	 *
	 */
	public static synchronized WorkflowModule getWorkflowModule() {
		if (workflowModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				workflowModule = new com.openkm.module.db.DbWorkflowModule();
			} else {
				// Other implementation
			}
		}

		return workflowModule;
	}

	/**
	 *
	 */
	public static synchronized StatsModule getStatsModule() {
		if (statsModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				statsModule = new com.openkm.module.db.DbStatsModule();
			} else {
				// Other implementation
			}
		}

		return statsModule;
	}

	/**
	 *
	 */
	public static synchronized MailModule getMailModule() {
		if (mailModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				mailModule = new com.openkm.module.db.DbMailModule();
			} else {
				// Other implementation
			}
		}

		return mailModule;
	}

	/**
	 *
	 */
	public static synchronized PropertyModule getPropertyModule() {
		if (propertyModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				propertyModule = new com.openkm.module.db.DbPropertyModule();
			} else {
				// Other implementation
			}
		}

		return propertyModule;
	}

	/**
	 *
	 */
	public static synchronized UserConfigModule getUserConfigModule() {
		if (userConfigModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				userConfigModule = new com.openkm.module.db.DbUserConfigModule();
			} else {
				// Other implementation
			}
		}

		return userConfigModule;
	}
}
