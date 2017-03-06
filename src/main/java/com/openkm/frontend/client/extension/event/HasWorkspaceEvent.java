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
 * HasWorkspaceEvent
 *
 *
 * @author jllort
 *
 */

public interface HasWorkspaceEvent {

	/**
	 * NavigatorEventConstant
	 *
	 * @author jllort
	 *
	 */
	public static class WorkspaceEventConstant {

		static final int EVENT_STACK_CHANGED = 1;

		private int type = 0;

		/**
		 * ToolBarEventConstant
		 *
		 * @param type
		 */
		private WorkspaceEventConstant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	WorkspaceEventConstant STACK_CHANGED = new WorkspaceEventConstant(WorkspaceEventConstant.EVENT_STACK_CHANGED);

	/**
	 * @param event
	 */
	void fireEvent(WorkspaceEventConstant event);

}