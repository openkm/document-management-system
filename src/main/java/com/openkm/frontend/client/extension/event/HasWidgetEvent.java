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

public interface HasWidgetEvent {

	/**
	 * WidgetEventConstant
	 *
	 * @author jllort
	 *
	 */
	public static class WidgetEventConstant {

		static final int EVENT_FINISH_STARTUP = 1;

		private int type = 0;

		/**
		 * WidgetEventConstant
		 *
		 * @param type
		 */
		private WidgetEventConstant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	WidgetEventConstant FINISH_STARTUP = new WidgetEventConstant(WidgetEventConstant.EVENT_FINISH_STARTUP);

	/**
	 * @param event
	 */
	void fireEvent(WidgetEventConstant event);
}