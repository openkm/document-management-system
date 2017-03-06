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


package com.openkm.extension.frontend.client.widget.forum;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.event.HasDashboardEvent;
import com.openkm.frontend.client.extension.event.HasDashboardEvent.DashboardEventConstant;
import com.openkm.frontend.client.extension.event.handler.DashboardHandlerExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarBoxExtension;


/**
 * ToolBarBoxForum
 *
 * @author jllort
 *
 */
public class ToolBarBoxForum implements DashboardHandlerExtension {

	private Panel hPanel;
	private ForumManager forumManager;
	private ForumController controller;
	private ToolBarBoxExtension toolBarBoxExtension;
	private Timer timer;

	/**
	 * ToolBarBoxForum
	 */
	public ToolBarBoxForum() {
		toolBarBoxExtension = new ToolBarBoxExtension(new Image(OKMBundleResources.INSTANCE.forum()), GeneralComunicator.i18nExtension("forum.title")) {
			@Override
			public Widget getWidget() {
				return hPanel;
			}
		};

		hPanel = new Panel();
		forumManager = new ForumManager();
		controller = forumManager.getController();
		// Refresh forums
		if (GeneralComunicator.getWorkspace() == null) {
			timer = new Timer() {
				@Override
				public void run() {
					if (GeneralComunicator.getWorkspace() == null) {
						firstTimeRefreshForum();
					} else {
						controller.refreshForums();
					}
				}
			};
			firstTimeRefreshForum();
		} else {
			controller.refreshForums();
		}
		hPanel.add(forumManager);
	}

	/**
	 * firstTimeRefreshForum
	 */
	private void firstTimeRefreshForum() {
		timer.schedule(1000);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		toolBarBoxExtension.setLabelText(GeneralComunicator.i18nExtension("forum.title"));
		forumManager.langRefresh();
	}

	/**
	 * ToolBarBoxExtension
	 *
	 * @return
	 */
	public ToolBarBoxExtension getToolBarBox() {
		return toolBarBoxExtension;
	}

	/**
	 * getManager
	 *
	 * @return
	 */
	public Widget getManager() {
		return forumManager;
	}

	@Override
	public void onChange(DashboardEventConstant event) {
		if (event.equals(HasDashboardEvent.DASHBOARD_REFRESH)) {
			controller.refreshForums();
		} else if (event.equals(HasDashboardEvent.TOOLBOX_CHANGED)) {
			forumManager.fillWidth();
		}
	}

	/**
	 * @author jllort
	 *
	 */
	class Panel extends SimplePanel {
		/**
		 * setSize
		 *
		 * @param width
		 * @param height
		 */
		public void setPixelSize(int width, int height) {
			super.setPixelSize(width, height);
			forumManager.setPixelSize(width, height);
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.user.client.ui.UIObject#setVisible(boolean)
		 */
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			forumManager.fillWidth(); // Must be done when widget is yet visible
		}
	}
}