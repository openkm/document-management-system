/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (C) 2006-2011 Paco Avila & Josep Llort
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

package com.openkm.core;

import com.openkm.servlet.frontend.UINotificationServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * UINotification
 *
 * @author jllort
 */
public class UINotification extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(UINotification.class);

	public void run() {
		log.debug("*** Clean UI notification ***");
		UINotificationServlet.clean();
	}
}
