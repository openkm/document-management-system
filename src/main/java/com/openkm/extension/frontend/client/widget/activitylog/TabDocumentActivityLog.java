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

package com.openkm.extension.frontend.client.widget.activitylog;

import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.extension.event.HasDocumentEvent.DocumentEventConstant;
import com.openkm.frontend.client.extension.event.handler.DocumentHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabdocument.TabDocumentExtension;

/**
 * TabDocumentActivityLog
 *
 * @author jllort
 *
 */
public class TabDocumentActivityLog extends TabDocumentExtension implements DocumentHandlerExtension {

	private String title = "";
	private ActivityLogTable activityLogTable;
	private int width = 0;
	private int height = 0;

	public TabDocumentActivityLog() {
		title = GeneralComunicator.i18nExtension("activitylog.title");
		activityLogTable = new ActivityLogTable(ActivityLogTable.DOCUMENT);
		initWidget(activityLogTable);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		this.width = width;
		this.height = height;
		activityLogTable.setPixelSize((width), (height));
		activityLogTable.logScrollTable.setPixelSize(width, height - 30);
		activityLogTable.logScrollTable.fillWidth();
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		title = GeneralComunicator.i18nExtension("activitylog.title");
		activityLogTable.langRefresh();
	}

	@Override
	public void onChange(DocumentEventConstant event) {
		if (event.equals(HasDocumentEvent.DOCUMENT_CHANGED)) {
			ActivityLog.get().setTabDocumentSelected();
			activityLogTable.resetActionList();
			activityLogTable.refresh(TabDocumentComunicator.getDocument().getUuid());
		} else if (event.equals(HasDocumentEvent.TAB_CHANGED)) {
			setPixelSize(width, height); // fillwidth must be done on visible tab
		}
	}

	@Override
	public String getTabText() {
		return title;
	}
}