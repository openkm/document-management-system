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

import com.google.gwt.widgetideas.client.ProgressBar;
import com.openkm.frontend.client.constants.ui.UIFileUploadConstants;

public class UploaderProgressBar extends ProgressBar {
	private String error;

	public UploaderProgressBar(double minProgress, double maxProgress, double curProgress) {
		super(minProgress, maxProgress, curProgress);
		setHeight("14px");
		setWidth("150px");
	}

	@Override
	protected String generateText(double curProgress) {
		if (error != null) {
			String returnValue = "";

			if (error.contains("AutomationException")) {
				// Improve AutomationException error
				returnValue = "AutomationException";
			} else {
				returnValue = "Error: " + error;
			}

			// If error length is very big -> truncate
			if (returnValue.length() > 25) {
				returnValue = returnValue.substring(0, 25);
			}
			return returnValue;
		}
		return super.generateText(curProgress);
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isError() {
		return !error.isEmpty();
	}

	public void setAction(int action) {
		if (UIFileUploadConstants.ACTION_UPDATE == action) {
			getBarElement().addClassName("gwt-ProgressBar-bar-update");
		}
	}
}
