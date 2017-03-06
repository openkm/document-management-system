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

package com.openkm.extension.frontend.client.widget.wiki;

import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Status
 *
 * @author jllort
 *
 */
public class Status extends PopupPanel {
	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;

	private boolean flag_createWikiPage = false;
	private boolean flag_getWikiPage = false;
	private boolean flag_lockWikiPage = false;
	private boolean flag_updateWikiPage = false;
	private boolean flag_deleteWikiPage = false;
	private boolean flag_getHistoryWikiPage = false;
	private boolean flag_restoreWikiPage = false;

	/**
	 * The status
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
	 * Refreshing the panel
	 */
	public void refresh() {
		if (flag_createWikiPage || flag_getWikiPage || flag_lockWikiPage || flag_updateWikiPage || flag_deleteWikiPage
				|| flag_getHistoryWikiPage || flag_restoreWikiPage) {
			Widget tab = Wiki.get().getWidgetTab();

			if (tab != null) { // Can be null if desktop is not visible
				int left = tab.getAbsoluteLeft() + (tab.getOffsetWidth() - 200) / 2;
				int top = tab.getAbsoluteTop() + (tab.getOffsetHeight() - 40) / 2;

				if (left > 0 && top > 0) {
					setPopupPosition(left, top);
					super.show();
				}
			}
		} else {
			super.hide();
		}
	}

	/**
	 * Sets the create wiki page flag
	 */
	public void setCreateWikiPage() {
		msg.setHTML(GeneralComunicator.i18nExtension("wiki.status.create.page"));
		flag_createWikiPage = true;
		refresh();
	}

	/**
	 * Unset the create wiki page flag
	 */
	public void unsetCreateWikiPage() {
		flag_createWikiPage = false;
		refresh();
	}

	/**
	 * Sets the get wiki page flag
	 */
	public void setGetWikiPage() {
		msg.setHTML(GeneralComunicator.i18nExtension("wiki.status.get.page"));
		flag_getWikiPage = true;
		refresh();
	}

	/**
	 * Unset the get wiki page flag
	 */
	public void unsetGetWikiPage() {
		flag_getWikiPage = false;
		refresh();
	}

	/**
	 * Sets the lock wiki page flag
	 */
	public void setLockWikiPage() {
		msg.setHTML(GeneralComunicator.i18nExtension("wiki.status.lock.page"));
		flag_lockWikiPage = true;
		refresh();
	}

	/**
	 * Unset the lock wiki page flag
	 */
	public void unsetLockWikiPage() {
		flag_lockWikiPage = false;
		refresh();
	}

	/**
	 * Sets the update wiki page flag
	 */
	public void setUpdateWikiPage() {
		msg.setHTML(GeneralComunicator.i18nExtension("wiki.status.update.page"));
		flag_updateWikiPage = true;
		refresh();
	}

	/**
	 * Unset the update wiki page flag
	 */
	public void unsetUpdateWikiPage() {
		flag_updateWikiPage = false;
		refresh();
	}

	/**
	 * Sets the delete wiki page flag
	 */
	public void setDeleteWikiPage() {
		msg.setHTML(GeneralComunicator.i18nExtension("wiki.status.delete.page"));
		flag_deleteWikiPage = true;
		refresh();
	}

	/**
	 * Unset the delete wiki page flag
	 */
	public void unsetDeleteWikiPage() {
		flag_deleteWikiPage = false;
		refresh();
	}

	/**
	 * Sets the get history wiki page flag
	 */
	public void setGetHistoryWikiPage() {
		msg.setHTML(GeneralComunicator.i18nExtension("wiki.status.get.history.page"));
		flag_getHistoryWikiPage = true;
		refresh();
	}

	/**
	 * Unset the get history wiki page flag
	 */
	public void unsetGetHistoryWikiPage() {
		flag_getHistoryWikiPage = false;
		refresh();
	}

	/**
	 * Sets the restore wiki page flag
	 */
	public void setRestoreWikiPage() {
		msg.setHTML(GeneralComunicator.i18nExtension("wiki.status.restore.history.page"));
		flag_restoreWikiPage = true;
		refresh();
	}

	/**
	 * Unset the restore wiki page flag
	 */
	public void unsetRestoreWikiPage() {
		flag_restoreWikiPage = false;
		refresh();
	}
}