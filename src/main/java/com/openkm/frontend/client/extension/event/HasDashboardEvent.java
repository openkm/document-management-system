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
 * HasDashboardEvent
 *
 *
 * @author jllort
 *
 */

public interface HasDashboardEvent {

	/**
	 * DashboardEventConstant
	 *
	 * @author jllort
	 *
	 */
	public static class DashboardEventConstant {

		static final int EVENT_TOOLBOX_CHANGED = 1;
		static final int EVENT_DASHBOARD_REFRESH = 2;

		private int type = 0;

		/**
		 * DocumentEventConstant
		 *
		 * @param type
		 */
		private DashboardEventConstant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	DashboardEventConstant TOOLBOX_CHANGED = new DashboardEventConstant(DashboardEventConstant.EVENT_TOOLBOX_CHANGED);
	DashboardEventConstant DASHBOARD_REFRESH = new DashboardEventConstant(DashboardEventConstant.EVENT_DASHBOARD_REFRESH);

	/**
	 * @param event
	 */
	void fireEvent(DashboardEventConstant event);
}