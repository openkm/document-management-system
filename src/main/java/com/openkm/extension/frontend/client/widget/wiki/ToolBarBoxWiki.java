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

package com.openkm.extension.frontend.client.widget.wiki;

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
 * ToolBarBoxWiki
 *
 * @author jllort
 *
 */
public class ToolBarBoxWiki implements DashboardHandlerExtension {
	private Panel hPanel;
	private ToolBarBoxExtension toolBarBoxExtension;
	private WikiManager wikiManager;
	private Timer timer;

	/**
	 * ToolBarBoxWiki
	 */
	public ToolBarBoxWiki() {
		toolBarBoxExtension = new ToolBarBoxExtension(new Image(OKMBundleResources.INSTANCE.wiki()), GeneralComunicator.i18nExtension("wiki.title")) {
			@Override
			public Widget getWidget() {
				return hPanel;
			}
		};

		hPanel = new Panel();
		wikiManager = new WikiManager(true);

		// Refresh forums
		if (GeneralComunicator.getWorkspace() == null) {
			timer = new Timer() {
				@Override
				public void run() {
					if (GeneralComunicator.getWorkspace() == null) {
						firstTimeRefreshWiki();
					} else {
						wikiManager.findWikiPageByTitle(TabToolbarWiki.MAIN_PAGE_TITTLE);
					}
				}
			};
			firstTimeRefreshWiki();
		} else {
			wikiManager.findWikiPageByNode(TabToolbarWiki.MAIN_PAGE_TITTLE);
		}

		hPanel.add(wikiManager);
	}

	/**
	 * firstTimeRefreshForum
	 */
	private void firstTimeRefreshWiki() {
		timer.schedule(1000);
	}

	/**
	 * getManager
	 */
	public Widget getManager() {
		return wikiManager;
	}

	@Override
	public void onChange(DashboardEventConstant event) {
		if (event.equals(HasDashboardEvent.DASHBOARD_REFRESH)) {
			//controller.refreshForums();
		} else if (event.equals(HasDashboardEvent.TOOLBOX_CHANGED)) {
			//wikiManager.fillWidth();
		}
	}

	/**
	 * openWikiPage
	 */
	public void openWikiPage(String title) {
		wikiManager.findWikiPageByTitle(title);
	}

	/**
	 * ToolBarBoxExtension
	 */
	public ToolBarBoxExtension getToolBarBox() {
		return toolBarBoxExtension;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		toolBarBoxExtension.setLabelText(GeneralComunicator.i18nExtension("wiki.title"));
		wikiManager.langRefresh();
	}

	/**
	 * addDocumentTag
	 */
	public void addDocumentTag(String uuid, String docName) {
		wikiManager.addDocumentTag(uuid, docName);
	}

	/**
	 * addFolderTag
	 */
	public void addFolderTag(String uuid, String fldName) {
		wikiManager.addFolderTag(uuid, fldName);
	}

	/**
	 * addWigiTag
	 */
	public void addWigiTag(String wikiTitle) {
		wikiManager.addWigiTag(wikiTitle);
	}

	/**
	 * @author jllort
	 *
	 */
	class Panel extends SimplePanel {
		/**
		 * setSize
		 */
		public void setPixelSize(int width, int height) {
			super.setPixelSize(width, height);
			wikiManager.setPixelSize(width, height);
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.user.client.ui.UIObject#setVisible(boolean)
		 */
		public void setVisible(boolean visible) {
			super.setVisible(visible);
		}
	}
}