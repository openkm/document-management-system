/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.widget.filebrowser.uploader;

/**
 * Drag and drop upload event.
 */
public class UploaderEvent {

	/**
	 * File path.
	 */
	private String filePath;

	/**
	 * Uploaded percentage.
	 */
	private double percentage;

	/**
	 * Error code.
	 */
	private String error;

	/**
	 * Action type.
	 */
	private int action;

	public UploaderEvent(String filePath, double percentage, int action) {
		this(filePath, percentage, null);
		this.action = action;
	}

	public UploaderEvent(String filePath, double percentage, String error) {
		this.filePath = filePath;
		this.percentage = percentage;
		this.error = error;
	}

	public String getFilePath() {
		return filePath;
	}

	public double getPercentage() {
		return percentage;
	}

	public String getError() {
		return error;
	}

	public int getAction() {
		return action;
	}
}
