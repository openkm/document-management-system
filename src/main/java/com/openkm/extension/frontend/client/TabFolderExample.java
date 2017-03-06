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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.extension.event.HasFolderEvent;
import com.openkm.frontend.client.extension.event.HasFolderEvent.FolderEventConstant;
import com.openkm.frontend.client.extension.event.handler.FolderHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabfolder.TabFolderExtension;

/**
 * TabFolderExample
 *
 *
 * @author jllort
 *
 */
public class TabFolderExample extends TabFolderExtension implements FolderHandlerExtension {
	VerticalPanel vPanel;

	public TabFolderExample() {
		vPanel = new VerticalPanel();
		vPanel.add(new HTML("hello world"));

		initWidget(vPanel);
	}

	@Override
	public String getTabText() {
		return "New folder tab";
	}

	@Override
	public void set(GWTFolder doc) {
	}

	@Override
	public void setVisibleButtons(boolean visible) {
	}

	@Override
	public void onChange(FolderEventConstant event) {
		if (event.equals(HasFolderEvent.TAB_CHANGED)) {
			Window.alert("tab changed - " + event.getType());
		}
	}
}