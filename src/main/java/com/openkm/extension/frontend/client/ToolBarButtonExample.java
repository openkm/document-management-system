/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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

package com.openkm.extension.frontend.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.openkm.extension.frontend.client.util.OKMExtensionBundleExampleResources;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.HasToolBarEvent;
import com.openkm.frontend.client.extension.event.HasToolBarEvent.ToolBarEventConstant;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.ToolBarHandlerExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;

/**
 * ToolBarButtonExample
 *
 * @author jllort
 *
 */
public class ToolBarButtonExample {
	ToolBarButton button;
	String title = "title";
	String lang = "en-GB";

	public ToolBarButtonExample() {
		button = new ToolBarButton(new Image(OKMExtensionBundleExampleResources.INSTANCE.box()), title, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.alert("make some operation");
			}
		});
	}

	/**
	 * ToolBarButtonExtension
	 */
	public ToolBarButtonExtension getButton() {
		return button;
	}

	/**
	 * ToolBarButton
	 *
	 * @author jllort
	 *
	 */
	private class ToolBarButton extends ToolBarButtonExtension implements ToolBarHandlerExtension, LanguageHandlerExtension {

		public ToolBarButton(Image image, String title, ClickHandler handler) {
			super(image, title, handler);
		}

		@Override
		public void checkPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel) {
		}

		@Override
		public void checkPermissions(GWTDocument doc, GWTFolder folder) {
		}

		@Override
		public void checkPermissions(GWTMail mail, GWTFolder folder) {
		}

		@Override
		public void enable(boolean enable) {
		}

		@Override
		public boolean isEnabled() {
			return false;
		}

		@Override
		public void onChange(ToolBarEventConstant event) {
			if (event.equals(HasToolBarEvent.EXECUTE_ADD_DOCUMENT)) {
				Window.alert("executed add document - " + event.getType());
			}
		}

		@Override
		public void onChange(LanguageEventConstant event) {
			if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
				Window.alert("language changed");
			}
		}
	}
}