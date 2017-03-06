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

package com.openkm.frontend.client.extension.event;


/**
 * HasFolderEvent
 *
 *
 * @author jllort
 *
 */

public interface HasFolderEvent {

	/**
	 * DocumentEventConstant
	 *
	 * @author jllort
	 *
	 */
	public static class FolderEventConstant {

		static final int EVENT_FOLDER_CHANGED = 1;
		static final int EVENT_PANEL_RESIZED = 2;
		static final int EVENT_TAB_CHANGED = 3;
		static final int EVENT_SECURITY_CHANGED = 4;
		static final int EVENT_SET_VISIBLE_BUTTON = 5;
		static final int EVENT_NOTE_ADDED = 6;
		static final int EVENT_FOLDER_DELETED = 7;
		static final int EVENT_KEYWORD_REMOVED = 8;
		static final int EVENT_KEYWORD_ADDED = 9;
		static final int EVENT_CATEGORY_ADDED = 10;
		static final int EVENT_CATEGORY_REMOVED = 11;

		private int type = 0;

		/**
		 * DocumentEventConstant
		 *
		 * @param type
		 */
		private FolderEventConstant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	FolderEventConstant FOLDER_CHANGED = new FolderEventConstant(FolderEventConstant.EVENT_FOLDER_CHANGED);
	FolderEventConstant PANEL_RESIZED = new FolderEventConstant(FolderEventConstant.EVENT_PANEL_RESIZED);
	FolderEventConstant TAB_CHANGED = new FolderEventConstant(FolderEventConstant.EVENT_TAB_CHANGED);
	FolderEventConstant SECURITY_CHANGED = new FolderEventConstant(FolderEventConstant.EVENT_SECURITY_CHANGED);
	FolderEventConstant SET_VISIBLE_BUTTON = new FolderEventConstant(FolderEventConstant.EVENT_SET_VISIBLE_BUTTON);
	FolderEventConstant NOTE_ADDED = new FolderEventConstant(FolderEventConstant.EVENT_NOTE_ADDED);
	FolderEventConstant FOLDER_DELETED = new FolderEventConstant(FolderEventConstant.EVENT_FOLDER_DELETED);
	FolderEventConstant KEYWORD_REMOVED = new FolderEventConstant(FolderEventConstant.EVENT_KEYWORD_REMOVED);
	FolderEventConstant KEYWORD_ADDED = new FolderEventConstant(FolderEventConstant.EVENT_KEYWORD_ADDED);
	FolderEventConstant CATEGORY_ADDED = new FolderEventConstant(FolderEventConstant.EVENT_CATEGORY_ADDED);
	FolderEventConstant CATEGORY_REMOVED = new FolderEventConstant(FolderEventConstant.EVENT_CATEGORY_REMOVED);

	/**
	 * @param event
	 */
	void fireEvent(FolderEventConstant event);
}