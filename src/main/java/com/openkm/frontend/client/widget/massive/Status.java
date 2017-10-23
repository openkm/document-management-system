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

package com.openkm.frontend.client.widget.massive;

import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.OKMBundleResources;

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
	private Widget widget;
	private boolean flag_merge = false;
	private boolean flag_setCategories = false;
	private boolean flag_addKeywords = false;
	private boolean flag_addNotes = false;
	private boolean flag_removeCategories = false;
	private boolean flag_removeKeywords = false;
	private boolean flag_addPropertyGroup = false;
	private boolean flag_removePropertyGroup = false;
	private boolean flag_delete = false;
	private boolean flag_copy = false;
	private boolean flag_move = false;
	private boolean flag_omr = false;
	private boolean flag_convert = false;
	private boolean flag_lock = false;
	private boolean flag_unlock = false;

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
	 * Refreshing satus
	 */
	public void refresh() {
		if (flag_merge || flag_setCategories || flag_addKeywords || flag_addNotes || flag_removeCategories ||
				flag_removeKeywords || flag_addPropertyGroup || flag_removePropertyGroup || flag_delete ||
				flag_copy || flag_move || flag_omr || flag_convert || flag_lock || flag_unlock) {
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
	 * Set merge flag
	 */
	public void setFlagMerge() {
		msg.setHTML(Main.i18n("merge.status.mergepdf"));
		flag_merge = true;
		refresh();
	}

	/**
	 * Unset merge flag
	 */
	public void unsetFlagMerge() {
		flag_merge = false;
		refresh();
	}

	/**
	 * Set set categories flag
	 */
	public void setFlagCategories() {
		msg.setHTML(Main.i18n("	tab.document.status.set.categories"));
		flag_setCategories = true;
		refresh();
	}

	/**
	 * Unset set categories flag
	 */
	public void unsetFlagCategories() {
		flag_setCategories = false;
		refresh();
	}

	/**
	 * Set set keywords flag
	 */
	public void setFlagAddKeywords() {
		msg.setHTML(Main.i18n("tab.document.status.set.keywords"));
		flag_addKeywords = true;
		refresh();
	}

	/**
	 * Unset set keywords flag
	 */
	public void unsetFlagAddKeywords() {
		flag_addKeywords = false;
		refresh();
	}

	/**
	 * Set set notes flag
	 */
	public void setFlagAddNotes() {
		msg.setHTML(Main.i18n("note.status.add"));
		flag_addNotes = true;
		refresh();
	}

	/**
	 * Unset set notes flag
	 */
	public void unsetFlagAddNotes() {
		flag_addNotes = false;
		refresh();
	}

	/**
	 * Set set remove categories flag
	 */
	public void setFlagRemoveCategories() {
		msg.setHTML(Main.i18n("categories.status.remove"));
		flag_removeCategories = true;
		refresh();
	}

	/**
	 * Unset set remove categories flag
	 */
	public void unsetFlagRemoveCategories() {
		flag_removeCategories = false;
		refresh();
	}

	/**
	 * Set set remove keywords flag
	 */
	public void setFlagRemoveKeywords() {
		msg.setHTML(Main.i18n("keywords.status.remove"));
		flag_removeKeywords = true;
		refresh();
	}

	/**
	 * Unset set remove keywords flag
	 */
	public void unsetFlagRemoveKeywords() {
		flag_removeKeywords = false;
		refresh();
	}

	/**
	 * Set set add property group flag
	 */
	public void setFlagAddPropertyGroup() {
		msg.setHTML(Main.i18n("tab.document.status.group.properties"));
		flag_addPropertyGroup = true;
		refresh();
	}

	/**
	 * Unset set remove property group flag
	 */
	public void unsetFlagAddPropertyGroup() {
		flag_addPropertyGroup = false;
		refresh();
	}

	/**
	 * Set set remove property group flag
	 */
	public void setFlagRemovePropertyGroup() {
		msg.setHTML(Main.i18n("propertygroup.status.remove"));
		flag_removePropertyGroup = true;
		refresh();
	}

	/**
	 * Unset set remove property group flag
	 */
	public void unsetFlagRemovePropertyGroup() {
		flag_removePropertyGroup = false;
		refresh();
	}

	/**
	 * Set set delete flag
	 */
	public void setFlagDelete() {
		msg.setHTML(Main.i18n("filebrowser.status.delete"));
		flag_delete = true;
		refresh();
	}

	/**
	 * Unset set delete flag
	 */
	public void unsetFlagDelete() {
		flag_delete = false;
		refresh();
	}

	/**
	 * Set set copy flag
	 */
	public void setFlagCopy() {
		msg.setHTML(Main.i18n("filebrowser.status.copy"));
		flag_copy = true;
		refresh();
	}

	/**
	 * Unset set copy flag
	 */
	public void unsetFlagCopy() {
		flag_copy = false;
		refresh();
	}

	/**
	 * Set set move flag
	 */
	public void setFlagMove() {
		msg.setHTML(Main.i18n("filebrowser.status.move"));
		flag_move = true;
		refresh();
	}

	/**
	 * Unset set move flag
	 */
	public void unsetFlagMove() {
		flag_move = false;
		refresh();
	}

	/**
	 * Set set omr flag
	 */
	public void setFlagOmr() {
		msg.setHTML(Main.i18n("filebrowser.status.omr"));
		flag_omr = true;
		refresh();
	}

	/**
	 * Unset set omr flag
	 */
	public void unsetFlagOmr() {
		flag_omr = false;
		refresh();
	}
	
	/**
	 * Set set convert flag
	 */
	public void setFlagConvert() {
		msg.setHTML(Main.i18n("filebrowser.status.convert"));
		flag_convert = true;
		refresh();
	}

	/**
	 * Unset set convert flag
	 */
	public void unsetFlagConvert() {
		flag_convert = false;
		refresh();
	}

	/**
	 * Set set lock flag
	 */
	public void setFlagLock() {
		msg.setHTML(Main.i18n("filebrowser.status.lock"));
		flag_lock = true;
		refresh();
	}

	/**
	 * Unset set lock flag
	 */
	public void unsetFlagLock() {
		flag_lock = false;
		refresh();
	}

	/**
	 * Set set unlock flag
	 */
	public void setFlagUnlock() {
		msg.setHTML(Main.i18n("filebrowser.status.unlock"));
		flag_unlock = true;
		refresh();
	}

	/**
	 * Unset set unlock flag
	 */
	public void unsetFlagUnlock() {
		flag_unlock = false;
		refresh();
	}
}
