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

package com.openkm.extension.frontend.client.widget.htmleditor;

import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.widget.foldertree.ExtendedPopupPanel;

/**
 * Status
 *
 * @author jllort
 */
public class Status extends ExtendedPopupPanel {

	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;

	private boolean flag_setHTML = false;
	private boolean flag_editHTML = false;

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
		if (flag_setHTML || flag_editHTML) {
			Widget widget = HTMLEditor.get().hTMLEditorPopup;
			int left = widget.getAbsoluteLeft() + (widget.getOffsetWidth() - 200) / 2;
			int top = widget.getAbsoluteTop() + (widget.getOffsetHeight() - 40) / 2;

			if (left > 0 && top > 0) {
				setPopupPosition(left, top);
				super.show();
			}
		} else {
			super.hide();
		}
	}

	/**
	 * Sets the set HTML flag
	 */
	public void setSetHTML() {
		msg.setHTML(GeneralComunicator.i18nExtension("htmleditor.status.set.html"));
		flag_setHTML = true;
		refresh();
	}

	/**
	 * Unset the set HTML flag
	 */
	public void unsetSetHTML() {
		flag_setHTML = false;
		refresh();
	}

	/**
	 * Sets edit HTML flag
	 */
	public void setEditHTML() {
		msg.setHTML(GeneralComunicator.i18nExtension("htmleditor.status.edit.html"));
		flag_editHTML = true;
		refresh();
	}

	/**
	 * Unset edit HTML flag
	 */
	public void unsetEditHTML() {
		flag_editHTML = false;
		refresh();
	}
}