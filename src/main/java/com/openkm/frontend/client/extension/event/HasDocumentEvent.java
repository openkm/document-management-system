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
 * HasDocumentEvent
 *
 *
 * @author jllort
 *
 */

public interface HasDocumentEvent {

	/**
	 * DocumentEventConstant
	 *
	 * @author jllort
	 *
	 */
	public static class DocumentEventConstant {

		static final int EVENT_DOCUMENT_CHANGED = 1;
		static final int EVENT_KEYWORD_ADDED = 2;
		static final int EVENT_KEYWORD_REMOVED = 3;
		static final int EVENT_CATEGORY_ADDED = 4;
		static final int EVENT_CATEGORY_REMOVED = 5;
		static final int EVENT_PANEL_RESIZED = 6;
		static final int EVENT_TAB_CHANGED = 7;
		static final int EVENT_SECURITY_CHANGED = 8;
		static final int EVENT_NOTE_ADDED = 9;
		static final int EVENT_SET_VISIBLE_BUTTONS = 10;
		static final int EVENT_DOCUMENT_DELETED = 11;

		private int type = 0;

		/**
		 * DocumentEventConstant
		 *
		 * @param type
		 */
		private DocumentEventConstant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	DocumentEventConstant DOCUMENT_CHANGED = new DocumentEventConstant(DocumentEventConstant.EVENT_DOCUMENT_CHANGED);
	DocumentEventConstant KEYWORD_ADDED = new DocumentEventConstant(DocumentEventConstant.EVENT_KEYWORD_ADDED);
	DocumentEventConstant KEYWORD_REMOVED = new DocumentEventConstant(DocumentEventConstant.EVENT_KEYWORD_REMOVED);
	DocumentEventConstant CATEGORY_ADDED = new DocumentEventConstant(DocumentEventConstant.EVENT_CATEGORY_ADDED);
	DocumentEventConstant CATEGORY_REMOVED = new DocumentEventConstant(DocumentEventConstant.EVENT_CATEGORY_REMOVED);
	DocumentEventConstant PANEL_RESIZED = new DocumentEventConstant(DocumentEventConstant.EVENT_PANEL_RESIZED);
	DocumentEventConstant TAB_CHANGED = new DocumentEventConstant(DocumentEventConstant.EVENT_TAB_CHANGED);
	DocumentEventConstant SECURITY_CHANGED = new DocumentEventConstant(DocumentEventConstant.EVENT_SECURITY_CHANGED);
	DocumentEventConstant NOTE_ADDED = new DocumentEventConstant(DocumentEventConstant.EVENT_NOTE_ADDED);
	DocumentEventConstant SET_VISIBLE_BUTTONS = new DocumentEventConstant(DocumentEventConstant.EVENT_SET_VISIBLE_BUTTONS);
	DocumentEventConstant DOCUMENT_DELETED = new DocumentEventConstant(DocumentEventConstant.EVENT_DOCUMENT_DELETED);

	/**
	 * @param event
	 */
	void fireEvent(DocumentEventConstant event);
}