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

import com.openkm.frontend.client.extension.event.HasToolBarEvent.ToolBarEventConstant;

/**
 * HasToolBarEvent
 *
 *
 * @author jllort
 *
 */

public interface HasToolBarEvent {

	/**
	 * ToolBarEventConstant
	 *
	 * @author jllort
	 *
	 */
	public static class ToolBarEventConstant {

		static final int EVENT_CHECK_DOCUMENT_PERMISSION = 1;
		static final int EVENT_CHECK_FOLDER_PERMISSION = 2;
		static final int EVENT_CHECK_MAIL_PERMISSION = 3;
		static final int EVENT_CHANGED_VIEW = 4;
		static final int EVENT_EXECUTE_CREATE_FOLDER = 5;
		static final int EVENT_EXECUTE_FIND_FOLDER = 6;
		static final int EVENT_EXECUTE_LOCK = 7;
		static final int EVENT_EXECUTE_UNLOCK = 8;
		static final int EVENT_ADD_DOCUMENT = 9;
		static final int EVENT_DELETE = 10;
		static final int EVENT_COPY = 11;
		static final int EVENT_MOVE = 12;
		static final int EVENT_RENAME = 13;
		static final int EVENT_CHECKOUT = 14;
		static final int EVENT_CHECKIN = 15;
		static final int EVENT_CANCEL_CHECKOUT = 16;
		static final int EVENT_DOWNLOAD_DOCUMENT = 17;
		static final int EVENT_DOWNLOAD_PDF_DOCUMENT = 18;
		static final int EVENT_ADD_PROPERTY_GROUP = 19;
		static final int EVENT_ADD_WORKFLOW = 20;
		static final int EVENT_REMOVE_PROPERTY_GROUP = 21;
		static final int EVENT_ADD_SUBSCRIPTION = 22;
		static final int EVENT_REMOVE_SUBSCRIPTION = 23;
		static final int EVENT_REFRESH = 24;
		static final int EVENT_SCANNER = 25;
		static final int EVENT_UPLOADER = 26;
		static final int EVENT_GO_HOME = 27;
		static final int EVENT_EXPORT_TO_ZIP = 28;
		static final int EVENT_SET_USER_HOME = 29;
		static final int EVENT_EXECUTE_FIND_DOCUMENT = 30;
		static final int EVENT_EXECUTE_FIND_SIMILAR_DOCUMENT = 31;
		static final int EVENT_OMR = 32;
		
		private int type = 0;

		/**
		 * ToolBarEventConstant
		 *
		 * @param type
		 */
		private ToolBarEventConstant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	ToolBarEventConstant EXECUTE_CHECK_DOCUMENT_PERMISSION = new ToolBarEventConstant(ToolBarEventConstant.EVENT_CHECK_DOCUMENT_PERMISSION);
	ToolBarEventConstant EXECUTE_CHECK_FOLDER_PERMISSION = new ToolBarEventConstant(ToolBarEventConstant.EVENT_CHECK_FOLDER_PERMISSION);
	ToolBarEventConstant EXECUTE_CHECK_MAIL_PERMISSION = new ToolBarEventConstant(ToolBarEventConstant.EVENT_CHECK_MAIL_PERMISSION);
	ToolBarEventConstant EXECUTE_CHANGED_VIEW = new ToolBarEventConstant(ToolBarEventConstant.EVENT_CHANGED_VIEW);
	ToolBarEventConstant EXECUTE_CREATE_FOLDER = new ToolBarEventConstant(ToolBarEventConstant.EVENT_EXECUTE_CREATE_FOLDER);
	ToolBarEventConstant EXECUTE_FIND_FOLDER = new ToolBarEventConstant(ToolBarEventConstant.EVENT_EXECUTE_FIND_FOLDER);
	ToolBarEventConstant EXECUTE_FIND_DOCUMENT = new ToolBarEventConstant(ToolBarEventConstant.EVENT_EXECUTE_FIND_DOCUMENT);
	ToolBarEventConstant EXECUTE_FIND_SIMILAR_DOCUMENT = new ToolBarEventConstant(ToolBarEventConstant.EVENT_EXECUTE_FIND_SIMILAR_DOCUMENT);
	ToolBarEventConstant EXECUTE_LOCK = new ToolBarEventConstant(ToolBarEventConstant.EVENT_EXECUTE_LOCK);
	ToolBarEventConstant EXECUTE_UNLOCK = new ToolBarEventConstant(ToolBarEventConstant.EVENT_EXECUTE_UNLOCK);
	ToolBarEventConstant EXECUTE_ADD_DOCUMENT = new ToolBarEventConstant(ToolBarEventConstant.EVENT_ADD_DOCUMENT);
	ToolBarEventConstant EXECUTE_DELETE = new ToolBarEventConstant(ToolBarEventConstant.EVENT_DELETE);
	ToolBarEventConstant EXECUTE_COPY = new ToolBarEventConstant(ToolBarEventConstant.EVENT_COPY);
	ToolBarEventConstant EXECUTE_MOVE = new ToolBarEventConstant(ToolBarEventConstant.EVENT_MOVE);
	ToolBarEventConstant EXECUTE_RENAME = new ToolBarEventConstant(ToolBarEventConstant.EVENT_RENAME);
	ToolBarEventConstant EXECUTE_CHECKOUT = new ToolBarEventConstant(ToolBarEventConstant.EVENT_CHECKOUT);
	ToolBarEventConstant EXECUTE_CHECKIN = new ToolBarEventConstant(ToolBarEventConstant.EVENT_CHECKIN);
	ToolBarEventConstant EXECUTE_CANCEL_CHECKOUT = new ToolBarEventConstant(ToolBarEventConstant.EVENT_CANCEL_CHECKOUT);
	ToolBarEventConstant EXECUTE_DOWNLOAD_DOCUMENT = new ToolBarEventConstant(ToolBarEventConstant.EVENT_DOWNLOAD_DOCUMENT);
	ToolBarEventConstant EXECUTE_DOWNLOAD_PDF_DOCUMENT = new ToolBarEventConstant(ToolBarEventConstant.EVENT_DOWNLOAD_PDF_DOCUMENT);
	ToolBarEventConstant EXECUTE_ADD_PROPERTY_GROUP = new ToolBarEventConstant(ToolBarEventConstant.EVENT_ADD_PROPERTY_GROUP);
	ToolBarEventConstant EXECUTE_ADD_WORKFLOW = new ToolBarEventConstant(ToolBarEventConstant.EVENT_ADD_WORKFLOW);
	ToolBarEventConstant EXECUTE_REMOVE_PROPERTY_GROUP = new ToolBarEventConstant(ToolBarEventConstant.EVENT_REMOVE_PROPERTY_GROUP);
	ToolBarEventConstant EXECUTE_ADD_SUBSCRIPTION = new ToolBarEventConstant(ToolBarEventConstant.EVENT_ADD_SUBSCRIPTION);
	ToolBarEventConstant EXECUTE_REMOVE_SUBSCRIPTION = new ToolBarEventConstant(ToolBarEventConstant.EVENT_REMOVE_SUBSCRIPTION);
	ToolBarEventConstant EXECUTE_REFRESH = new ToolBarEventConstant(ToolBarEventConstant.EVENT_REFRESH);
	ToolBarEventConstant EXECUTE_GO_HOME = new ToolBarEventConstant(ToolBarEventConstant.EVENT_GO_HOME);
	ToolBarEventConstant EXECUTE_EXPORT_TO_ZIP = new ToolBarEventConstant(ToolBarEventConstant.EVENT_EXPORT_TO_ZIP);
	ToolBarEventConstant EXECUTE_SET_USER_HOME = new ToolBarEventConstant(ToolBarEventConstant.EVENT_SET_USER_HOME);
	ToolBarEventConstant EXECUTE_OMR = new ToolBarEventConstant(ToolBarEventConstant.EVENT_OMR);

	/**
	 * @param event
	 */
	void fireEvent(ToolBarEventConstant event);
}