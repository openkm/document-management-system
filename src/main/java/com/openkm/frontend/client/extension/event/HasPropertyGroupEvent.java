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
 * HasPropertyGroupEvent
 *
 *
 * @author jllort
 *
 */

public interface HasPropertyGroupEvent {

	/**
	 * PropertyGroupEventConstant
	 *
	 * @author jllort
	 *
	 */
	public static class PropertyGroupEventConstant {

		static final int EVENT_PROPERTYGROUP_CHANGED = 1;
		static final int EVENT_PROPERTYGROUP_GET_PROPERTIES = 2;
		static final int EVENT_PROPERTYGROUP_REMOVED = 3;
		static final int EVENT_PROPERTYGROUP_EDIT = 4;
		static final int EVENT_PROPERTYGROUP_CANCEL_EDIT = 5;

		private int type = 0;

		/**
		 * PropertyGroupEventConstant
		 *
		 * @param type
		 */
		private PropertyGroupEventConstant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	PropertyGroupEventConstant PROPERTYGROUP_CHANGED = new PropertyGroupEventConstant(PropertyGroupEventConstant.EVENT_PROPERTYGROUP_CHANGED);
	PropertyGroupEventConstant PROPERTYGROUP_GET_PROPERTIES = new PropertyGroupEventConstant(PropertyGroupEventConstant.EVENT_PROPERTYGROUP_GET_PROPERTIES);
	PropertyGroupEventConstant PROPERTYGROUP_REMOVED = new PropertyGroupEventConstant(PropertyGroupEventConstant.EVENT_PROPERTYGROUP_REMOVED);
	PropertyGroupEventConstant PROPERTYGROUP_EDIT = new PropertyGroupEventConstant(PropertyGroupEventConstant.EVENT_PROPERTYGROUP_EDIT);
	PropertyGroupEventConstant PROPERTYGROUP_CANCEL_EDIT = new PropertyGroupEventConstant(PropertyGroupEventConstant.EVENT_PROPERTYGROUP_CANCEL_EDIT);

	/**
	 * @param event
	 */
	void fireEvent(PropertyGroupEventConstant event);
}