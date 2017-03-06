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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.util.OKMExtensionBundleExampleResources;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarBoxExtension;

/**
 * ToolBarBoxExample
 *
 * @author jllort
 *
 */
public class ToolBarBoxExample {
	public HorizontalPanel hPanel;
	public ToolBarBoxExtension toolBarBoxExtension;

	/**
	 * ToolBarBoxExample
	 */
	public ToolBarBoxExample() {
		toolBarBoxExtension = new ToolBarBoxExtension(new Image(OKMExtensionBundleExampleResources.INSTANCE.general()),
				"new toolbar") {
			@Override
			public Widget getWidget() {
				return hPanel;
			}
		};

		hPanel = new HorizontalPanel();
		hPanel.add(new HTML("new toolbarbox example"));
	}

	/**
	 * ToolBarBoxExtension
	 */
	public ToolBarBoxExtension getToolBarBox() {
		return toolBarBoxExtension;
	}
}