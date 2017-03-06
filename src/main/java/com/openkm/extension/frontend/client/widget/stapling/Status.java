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

package com.openkm.extension.frontend.client.widget.stapling;

import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.extension.comunicator.FileBrowserComunicator;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.TabDocumentComunicator;

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

	private boolean flag_getStaplets = false;
	private boolean flag_deleteStaplingGroup = false;
	private boolean flag_addStapling = false;

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
	 * isPanelRefreshing
	 */
	public boolean isPanelRefreshing() {
		return (flag_getStaplets || flag_deleteStaplingGroup || flag_addStapling);
	}

	/**
	 * Refreshing the panel
	 */
	public void refresh() {
		if (flag_getStaplets || flag_deleteStaplingGroup || flag_addStapling) {
			int left = 0;
			int top = 0;

			if (FileBrowserComunicator.isDocumentSelected()) {
				left = ((Stapling.get().tabDocument.getOffsetWidth() - 220) / 2) + Stapling.get().tabDocument.getAbsoluteLeft();
				top = ((Stapling.get().tabDocument.getOffsetHeight() - 40) / 2) + Stapling.get().tabDocument.getAbsoluteTop();
			} else if (FileBrowserComunicator.isFolderSelected()) {
				left = ((Stapling.get().tabFolder.getOffsetWidth() - 220) / 2) + Stapling.get().tabFolder.getAbsoluteLeft();
				top = ((Stapling.get().tabFolder.getOffsetHeight() - 40) / 2) + Stapling.get().tabFolder.getAbsoluteTop();
			} else if (FileBrowserComunicator.isMailSelected()) {
				left = ((Stapling.get().tabMail.getOffsetWidth() - 220) / 2) + Stapling.get().tabMail.getAbsoluteLeft();
				top = ((Stapling.get().tabMail.getOffsetHeight() - 40) / 2) + Stapling.get().tabMail.getAbsoluteTop();
			}

			if (left > 0 && top > 0) {
				setPopupPosition(left, top);
				TabDocumentComunicator.setRefreshingStyle();
				super.show();
			}
		} else {
			super.hide();
			TabDocumentComunicator.unsetRefreshingStyle();
		}
	}

	/**
	 * Sets the get stapleds flag
	 */
	public void setGetStapleds() {
		msg.setHTML(GeneralComunicator.i18nExtension("stapling.get.stapling.groups"));
		flag_getStaplets = true;
		refresh();
	}

	/**
	 * Unset the get stapleds flag
	 */
	public void unsetGetStapleds() {
		flag_getStaplets = false;
		refresh();
	}

	/**
	 * Sets the delete stapling group flag
	 */
	public void setDeleteStaplingGroup() {
		msg.setHTML(GeneralComunicator.i18nExtension("stapling.deleting.stapling.group"));
		flag_deleteStaplingGroup = true;
		refresh();
	}

	/**
	 * Unset the delete statpling group flag
	 */
	public void unsetDeleteStaplingGroup() {
		flag_deleteStaplingGroup = false;
		refresh();
	}

	/**
	 * Sets the add stapling flag
	 */
	public void setAddStapling() {
		msg.setHTML(GeneralComunicator.i18nExtension("stapling.add.stapling"));
		flag_addStapling = true;
		refresh();
	}

	/**
	 * Unset the add stapling flag
	 */
	public void unsetAddStapling() {
		flag_addStapling = false;
		refresh();
	}
}