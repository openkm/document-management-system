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

package com.openkm.frontend.client.widget.filebrowser;

import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.panel.top.TopPanel;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.widget.foldertree.ExtendedPopupPanel;

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
	private boolean flag_Folder_getChilds = false;
	private boolean flag_Document_getChilds = false;
	private boolean flag_Mail_getChilds = false;
	private boolean flag_Folder_delete = false;
	private boolean flag_Document_delete = false;
	private boolean flag_Checkout = false;
	private boolean flag_Lock = false;
	private boolean flag_UnLock = false;
	private boolean flag_Document_rename = false;
	private boolean flag_Folder_rename = false;
	private boolean flag_Document_purge = false;
	private boolean flag_Folder_purge = false;
	private boolean flag_GetFolder = false;
	private boolean flag_GetDocument = false;
	private boolean flag_AddSubscription = false;
	private boolean flag_RemoveSubscription = false;
	private boolean flag_Mail_delete = false;
	private boolean flag_Mail_purge = false;
	private boolean flag_Mail_getProperties = false;
	private boolean flag_Mail_rename = false;
	private boolean flag_CreateFromTemplate = false;
	private boolean flag_Ordering = false;
	private boolean flag_getChilds = false;
	private Widget widget;

	/**
	 * Status
	 */
	public Status(Widget widget) {
		super(false, true);
		this.widget = widget;
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
	 * Refresh
	 */
	public void refresh() {
		if (flag_Folder_getChilds || flag_Document_getChilds || flag_Folder_delete
				|| flag_Document_delete || flag_Checkout || flag_Lock || flag_UnLock
				|| flag_Document_rename || flag_Folder_rename || flag_Document_purge
				|| flag_Folder_purge || flag_GetFolder || flag_GetDocument
				|| flag_AddSubscription || flag_RemoveSubscription || flag_Mail_getChilds
				|| flag_Mail_delete || flag_Mail_purge || flag_Mail_getProperties
				|| flag_Mail_rename || flag_CreateFromTemplate || flag_Ordering
				|| flag_getChilds) {
			int left = ((widget.getAbsoluteLeft() + widget.getOffsetWidth() - 200) / 2) + widget.getAbsoluteLeft();
			int top = ((widget.getAbsoluteTop() + widget.getOffsetHeight()) / 2) + TopPanel.PANEL_HEIGHT;
			setPopupPosition(left, top);
			Main.get().mainPanel.desktop.browser.fileBrowser.panel.addStyleName("okm-PanelRefreshing");
			super.show();
		} else {
			super.hide();
			Main.get().mainPanel.desktop.browser.fileBrowser.panel.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Sets folder childs flag
	 */
	public void setFlagFolderChilds() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.folder"));
		flag_Folder_getChilds = true;
		refresh();
	}

	/**
	 * Unset folder childs flag
	 */
	public void unsetFlagFolderChilds() {
		flag_Folder_getChilds = false;
		refresh();
	}

	/**
	 * Set document childs flag
	 */
	public void setFlagDocumentChilds() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.document"));
		flag_Document_getChilds = true;
		refresh();
	}

	/**
	 * Unset document childs flag
	 */
	public void unsetFlagDocumentChilds() {
		flag_Document_getChilds = false;
		refresh();
	}

	/**
	 * Set mail childs flag
	 */
	public void setFlagMailChilds() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.mail"));
		flag_Mail_getChilds = true;
		refresh();
	}

	/**
	 * Unset mail childs flag
	 */
	public void unsetFlagMailChilds() {
		flag_Mail_getChilds = false;
		refresh();
	}

	/**
	 * Sets folder delete flag
	 */
	public void setFlagFolderDelete() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.delete.folder"));
		flag_Folder_delete = true;
		refresh();
	}

	/**
	 * Unset folder delete flag
	 */
	public void unsetFlagFolderDelete() {
		flag_Folder_delete = false;
		refresh();
	}

	/**
	 * Sets document delete flag
	 */
	public void setFlagDocumentDelete() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.delete.document"));
		flag_Document_delete = true;
		refresh();
	}

	/**
	 * Unset document delte flag
	 */
	public void unsetFlagDocumentDelete() {
		flag_Document_delete = false;
		refresh();
	}

	/**
	 * Sets mail delete flag
	 */
	public void setFlagMailDelete() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.delete.mail"));
		flag_Mail_delete = true;
		refresh();
	}

	/**
	 * Unset mail delte flag
	 */
	public void unsetFlagMailDelete() {
		flag_Mail_delete = false;
		refresh();
	}

	/**
	 * Sets checkout flag
	 */
	public void setFlagCheckout() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.checkout"));
		flag_Checkout = true;
		refresh();
	}

	/**
	 * Unset checkout flag
	 */
	public void unsetFlagCheckout() {
		flag_Checkout = false;
		refresh();
	}

	/**
	 * Sets lock flag
	 */
	public void setFlagLock() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.lock"));
		flag_Lock = true;
		refresh();
	}

	/**
	 * Unset checkout flag
	 */
	public void unsetFlagLock() {
		flag_Lock = false;
		refresh();
	}

	/**
	 * Sets delete lock flag
	 */
	public void setFlagUnLock() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.unlock"));
		flag_UnLock = true;
		refresh();
	}

	/**
	 * Unset delete lock flag
	 */
	public void unsetFlagUnLock() {
		flag_UnLock = false;
		refresh();
	}

	/**
	 * Sets add subscription flag
	 */
	public void setFlagAddSubscription() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.add.subscription"));
		flag_AddSubscription = true;
		refresh();
	}

	/**
	 * Unset add subscription flag
	 */
	public void unsetFlagAddSubscription() {
		flag_AddSubscription = false;
		refresh();
	}

	/**
	 * Sets remove subscription flag
	 */
	public void setFlagRemoveSubscription() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.remove.subscription"));
		flag_RemoveSubscription = true;
		refresh();
	}

	/**
	 * Unset remove subscription flag
	 */
	public void unsetFlagRemoveSubscription() {
		flag_RemoveSubscription = false;
		refresh();
	}

	/**
	 * Sets document rename flag
	 */
	public void setFlagDocumentRename() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.document.rename"));
		flag_Document_rename = true;
		refresh();
	}

	/**
	 * Unset document rename flag
	 */
	public void unsetFlagDocumentRename() {
		flag_Document_rename = false;
		refresh();
	}

	/**
	 * Sets folder rename flag
	 */
	public void setFlagFolderRename() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.folder.rename"));
		flag_Folder_rename = true;
		refresh();
	}

	/**
	 * Unset folder rename flag
	 */
	public void unsetFlagFolderRename() {
		flag_Folder_rename = false;
		refresh();
	}

	/**
	 * Sets mail rename flag
	 */
	public void setFlagMailRename() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.mail.rename"));
		flag_Mail_rename = true;
		refresh();
	}

	/**
	 * Unset mail rename flag
	 */
	public void unsetFlagMailRename() {
		flag_Mail_rename = false;
		refresh();
	}

	/**
	 * Sets document purge flag
	 */
	public void setFlagDocumentPurge() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.document.purge"));
		flag_Document_purge = true;
		refresh();
	}

	/**
	 * Unset document purge flag
	 */
	public void unsetFlagDocumentPurge() {
		flag_Document_purge = false;
		refresh();
	}

	/**
	 * Sets mail purge flag
	 */
	public void setFlagMailPurge() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.mail.purge"));
		flag_Mail_purge = true;
		refresh();
	}

	/**
	 * Unset document purge flag
	 */
	public void unsetFlagMailPurge() {
		flag_Mail_purge = false;
		refresh();
	}

	/**
	 * Sets folder purge flag
	 */
	public void setFlagFolderPurge() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.folder.purge"));
		flag_Folder_purge = true;
		refresh();
	}

	/**
	 * Unset folder purge flag
	 */
	public void unsetFlagFolderPurge() {
		flag_Folder_purge = false;
		refresh();
	}

	/**
	 * Sets get folder flag
	 */
	public void setFlagGetFolder() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.folder.get"));
		flag_GetFolder = true;
		refresh();
	}

	/**
	 * Unset get folder flag
	 */
	public void unsetFlagGetFolder() {
		flag_GetFolder = false;
		refresh();
	}

	/**
	 * Sets get document flag
	 */
	public void setFlagGetDocument() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.document.get"));
		flag_GetDocument = true;
		refresh();
	}

	/**
	 * Unset get document flag
	 */
	public void unsetFlagGetDocument() {
		flag_GetDocument = false;
		refresh();
	}

	/**
	 * Set mail properties flag
	 */
	public void setFlagMailProperties() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.mail.properties"));
		flag_Mail_getProperties = true;
		refresh();
	}

	/**
	 * Unset mail properties flag
	 */
	public void unsetFlagMailProperties() {
		flag_Mail_getProperties = false;
		refresh();
	}

	/**
	 * Set create from template flag
	 */
	public void setFlagCreateFromTemplate() {
		msg.setHTML(Main.i18n("fileupload.status.create.from.template"));
		flag_CreateFromTemplate = true;
		refresh();
	}

	/**
	 * Unset create from template flag
	 */
	public void unsetFlagCreateFromTemplate() {
		flag_CreateFromTemplate = false;
		refresh();
	}

	/**
	 * Set ordering flag
	 */
	public void setFlagOrdering() {
		msg.setHTML(Main.i18n("filebrowser.status.ordering"));
		flag_Ordering = true;
		refresh();
	}

	/**
	 * Unset ordering flag
	 */
	public void unsetFlagOrdering() {
		flag_Ordering = false;
		refresh();
	}

	/**
	 * Set getchilds flag
	 */
	public void setFlagGetChilds() {
		msg.setHTML(Main.i18n("filebrowser.controller.getchilds"));
		flag_getChilds = true;
		refresh();
	}

	/**
	 * Unset getchidls flag
	 */
	public void unsetFlagGetChilds() {
		flag_getChilds = false;
		refresh();
	}
}