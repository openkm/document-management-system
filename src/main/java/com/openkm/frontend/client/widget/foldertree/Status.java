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

package com.openkm.frontend.client.widget.foldertree;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.panel.ExtendedDockPanel;
import com.openkm.frontend.client.panel.top.TopPanel;
import com.openkm.frontend.client.util.OKMBundleResources;

/**
 * Status
 *
 * @author jllort
 *
 */
public class Status extends ExtendedPopupPanel {

	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;
	private boolean flag_getChilds = false;
	private boolean flag_create = false;
	private boolean flag_delete = false;
	private boolean flag_rename = false;
	private boolean flag_purge = false;
	private boolean flag_purgeTrash = false;
	private boolean flag_get = false;
	private boolean flag_addSubscription = false;
	private boolean flag_removeSubscription = false;
	private boolean flag_root = false;
	private boolean flag_userHome = false;

	/**
	 * Status
	 */
	public Status() {
		super(false, true);
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");

		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);

		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");

		hPanel.setHeight("25px");

		msg.setStyleName("okm-NoWrap");

		super.hide();
		setWidget(hPanel);
	}

	/**
	 * Refreshing satus
	 */
	public void refresh() {
		if (flag_getChilds || flag_delete || flag_rename || flag_create || flag_purge || flag_purgeTrash || flag_get
				|| flag_addSubscription || flag_removeSubscription || flag_root || flag_userHome) {
			int left = ((Main.get().mainPanel.desktop.getLeft() - 200) / 2) + ExtendedDockPanel.VERTICAL_BORDER_PANEL_WIDTH;
			int top = ((Main.get().mainPanel.desktop.getHeight() - 40) / 2) + TopPanel.PANEL_HEIGHT;
			int stackIndex = Main.get().mainPanel.desktop.navigator.getStackIndex();
			setPopupPosition(left, top);
			switch (stackIndex) {
				case 0:
					Main.get().activeFolderTree.removeStyleName("okm-PanelSelected");
					Main.get().activeFolderTree.setStyleName("okm-PanelRefreshing");
					break;
				case 1:
					Main.get().activeFolderTree.removeStyleName("okm-PanelSelected");
					Main.get().activeFolderTree.setStyleName("okm-PanelRefreshing");
					break;
			}
			super.show();
		} else {
			int stackIndex = Main.get().mainPanel.desktop.navigator.getStackIndex();
			super.hide();
			switch (stackIndex) {
				case 0:
					if (Main.get().activeFolderTree != null) {
						Main.get().activeFolderTree.setStyleName("okm-Tree");
						if (Main.get().activeFolderTree.isPanelSelected()) {
							Main.get().activeFolderTree.addStyleName("okm-PanelSelected");
						} else {
							Main.get().activeFolderTree.removeStyleName("okm-PanelSelected");
						}
					}
					break;
				case 1:
					Main.get().activeFolderTree.setStyleName("okm-Tree");
					if (Main.get().activeFolderTree.isPanelSelected()) {
						Main.get().activeFolderTree.addStyleName("okm-PanelSelected");
					} else {
						Main.get().activeFolderTree.removeStyleName("okm-PanelSelected");
					}
					break;
			}
		}
	}

	/**
	 * Set childs flag
	 */
	public void setFlagChilds() {
		msg.setHTML(Main.i18n("tree.status.refresh.folder"));
		flag_getChilds = true;
		refresh();
	}

	/**
	 * Unset childs flag
	 */
	public void unsetFlagChilds() {
		flag_getChilds = false;
		refresh();
	}

	/**
	 * Set create flag
	 */
	public void setFlagCreate() {
		msg.setHTML(Main.i18n("tree.status.refresh.create"));
		flag_create = true;
		refresh();
	}

	/**
	 * Unset create flag
	 */
	public void unsetFlagCreate() {
		flag_create = false;
		refresh();
	}

	/**
	 * Set delete flag
	 */
	public void setFlagDelete() {
		msg.setHTML(Main.i18n("tree.status.refresh.delete"));
		flag_delete = true;
		refresh();
	}

	/**
	 * Unset delete flag
	 */
	public void unsetFlagDelete() {
		flag_delete = false;
		refresh();
	}

	/**
	 * Set rename flag
	 */
	public void setFlagRename() {
		msg.setHTML(Main.i18n("tree.status.refresh.rename"));
		flag_rename = true;
		refresh();
	}

	/**
	 * Unset rename flag
	 */
	public void unsetFlagRename() {
		flag_rename = false;
		refresh();
	}

	/**
	 * Set purge flag
	 */
	public void setFlagPurge() {
		msg.setHTML(Main.i18n("tree.status.refresh.purge"));
		flag_purge = true;
		refresh();
	}

	/**
	 * Unset purge flag
	 */
	public void unsetFlagPurge() {
		flag_purge = false;
		refresh();
	}

	/**
	 * Set purge trash flag
	 */
	public void setFlagPurgeTrash() {
		msg.setHTML(Main.i18n("tree.status.refresh.purge.trash"));
		flag_purgeTrash = true;
		refresh();
	}

	/**
	 * Unset purge trash flag
	 */
	public void unsetFlagPurgeTrash() {
		flag_purgeTrash = false;
		refresh();
	}

	/**
	 * Set get flag
	 */
	public void setFlagGet() {
		msg.setHTML(Main.i18n("tree.status.refresh.get"));
		flag_get = true;
		refresh();
	}

	/**
	 * Unset get flag
	 */
	public void unsetFlagGet() {
		flag_get = false;
		refresh();
	}

	/**
	 * Set add subscription flag
	 */
	public void setFlagAddSubscription() {
		msg.setHTML(Main.i18n("tree.status.refresh.add.subscription"));
		flag_addSubscription = true;
		refresh();
	}

	/**
	 * Unset add subscription flag
	 */
	public void unsetFlagAddSubscription() {
		flag_addSubscription = false;
		refresh();
	}

	/**
	 * Set remove subscription flag
	 */
	public void setFlagRemoveSubscription() {
		msg.setHTML(Main.i18n("tree.status.refresh.remove.subscription"));
		flag_removeSubscription = true;
		refresh();
	}

	/**
	 * Unset remove subscription flag
	 */
	public void unsetFlagRemoveSubscription() {
		flag_removeSubscription = false;
		refresh();
	}

	/**
	 * Set remove root flag
	 */
	public void setFlagRoot() {
		msg.setHTML(Main.i18n("tree.status.refresh.get.root"));
		flag_root = true;
		refresh();
	}

	/**
	 * Unset remove root flag
	 */
	public void unsetFlagRoot() {
		flag_root = false;
		refresh();
	}

	/**
	 * Set remove user home flag
	 */
	public void setFlagUserHome() {
		msg.setHTML(Main.i18n("tree.status.refresh.get.user.home"));
		flag_userHome = true;
		refresh();
	}

	/**
	 * Unset remove user home flag
	 */
	public void unsetFlagUserHome() {
		flag_userHome = false;
		refresh();
	}
}