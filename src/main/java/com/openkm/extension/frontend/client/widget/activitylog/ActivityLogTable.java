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

package com.openkm.extension.frontend.client.widget.activitylog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMActivityLogService;
import com.openkm.extension.frontend.client.service.OKMActivityLogServiceAsync;
import com.openkm.frontend.client.bean.extension.GWTActivity;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.List;

/**
 * ActivityLogTable
 *
 * @author jllort
 *
 */
public class ActivityLogTable extends Composite {
	public static final int DOCUMENT = 0;
	public static final int FOLDER = 1;
	public static final int MAIL = 2;
	public static final int LOG_NUMBER_OF_COLUMNS = 4;

	private final OKMActivityLogServiceAsync activityLogService = (OKMActivityLogServiceAsync) GWT
			.create(OKMActivityLogService.class);
	private ListBox actionList;
	private HTML filterText;
	private HTML filterGetChildsText;
	private CheckBox getChilds;
	public LogScrollTable logScrollTable;
	private VerticalPanel vPanel;

	public ActivityLogTable(int type) {
		vPanel = new VerticalPanel();

		actionList = new ListBox();
		actionList.addItem("", "");
		actionList.addItem("ALL_ACTIONS", "ALL_ACTIONS");

		switch (type) {
			case DOCUMENT:
				actionList.addItem("CANCEL_DOCUMENT_CHECKOUT", "CANCEL_DOCUMENT_CHECKOUT");
				actionList.addItem("CHECKIN_DOCUMENT", "CHECKIN_DOCUMENT");
				actionList.addItem("CHECKOUT_DOCUMENT", "CHECKOUT_DOCUMENT");
				actionList.addItem("CREATE_DOCUMENT", "CREATE_DOCUMENT");
				actionList.addItem("DELETE_DOCUMENT", "DELETE_DOCUMENT");
				actionList.addItem("GET_CHILDREN_DOCUMENTS", "GET_CHILDREN_DOCUMENTS");
				actionList.addItem("GET_DOCUMENT_CONTENT", "GET_DOCUMENT_CONTENT");
				actionList.addItem("GET_DOCUMENT_CONTENT_BY_VERSION", "GET_DOCUMENT_CONTENT_BY_VERSION");
				actionList.addItem("GET_DOCUMENT_PROPERTIES", "GET_DOCUMENT_PROPERTIES");
				actionList.addItem("GET_DOCUMENT_VERSION_HISTORY", "GET_DOCUMENT_VERSION_HISTORY");
				actionList.addItem("GET_PROPERTY_GROUP_PROPERTIES", "GET_PROPERTY_GROUP_PROPERTIES");
				actionList.addItem("LOCK_DOCUMENT", "LOCK_DOCUMENT");
				actionList.addItem("MOVE_DOCUMENT", "MOVE_DOCUMENT");
				actionList.addItem("PURGE_DOCUMENT", "PURGE_DOCUMENT");
				actionList.addItem("RENAME_DOCUMENT", "RENAME_DOCUMENT");
				actionList.addItem("SET_DOCUMENT_PROPERTIES", "SET_DOCUMENT_PROPERTIES");
				actionList.addItem("UNLOCK_DOCUMENT", "UNLOCK_DOCUMENT");
				break;

			case FOLDER:
				actionList.addItem("COPY_FOLDER", "COPY_FOLDER");
				actionList.addItem("CREATE_FOLDER", "CREATE_FOLDER");
				actionList.addItem("DELETE_FOLDER", "DELETE_FOLDER");
				actionList.addItem("GET_CHILDREN_FOLDERS", "GET_CHILDREN_FOLDERS");
				actionList.addItem("GET_FOLDER_CONTENT_INFO", "GET_FOLDER_CONTENT_INFO");
				actionList.addItem("GET_FOLDER_PROPERTIES", "GET_FOLDER_PROPERTIES");
				actionList.addItem("MOVE_FOLDER", "MOVE_FOLDER");
				actionList.addItem("PURGE_FOLDER", "PURGE_FOLDER");
				actionList.addItem("RENAME_FOLDER", "RENAME_FOLDER");
				break;

			case MAIL:
				actionList.addItem("CREATE_MAIL", "CREATE_MAIL");
				actionList.addItem("GET_MAIL_PROPERTIES", "GET_MAIL_PROPERTIES");
				actionList.addItem("DELETE_MAIL", "DELETE_MAIL");
				actionList.addItem("PURGE_MAIL", "PURGE_MAIL");
				actionList.addItem("RENAME_MAIL", "RENAME_MAIL");
				actionList.addItem("MOVE_MAIL", "MOVE_MAIL");
				actionList.addItem("COPY_MAIL", "COPY_MAIL");
				actionList.addItem("GET_CHILDREN_MAILS", "GET_CHILDREN_MAILS");
				break;
		}

		actionList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				refresh(ActivityLog.get().getUuid());
			}
		});
		actionList.setStyleName("okm-Input");

		filterText = new HTML(GeneralComunicator.i18nExtension("activitylog.filter"));
		filterGetChildsText = new HTML(GeneralComunicator.i18nExtension("activitylog.filter.get.childs"));
		getChilds = new CheckBox();
		getChilds.setValue(false);
		getChilds.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh(ActivityLog.get().getUuid());
			}
		});

		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML("&nbsp;");
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(filterText);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(actionList);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(filterGetChildsText);
		hPanel.add(getChilds);
		hPanel.add(space);

		hPanel.setCellVerticalAlignment(filterText, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(actionList, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(filterGetChildsText, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(getChilds, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellWidth(space, "100%");
		hPanel.setSize("100%", "30px");
		hPanel.setStyleName("okm-Mail");
		hPanel.addStyleName("okm-NoWrap");
		filterGetChildsText.setStyleName("okm-NoWrap");

		vPanel.add(hPanel);
		vPanel.setCellHeight(hPanel, "30px");
		vPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);

		logScrollTable = new LogScrollTable();
		vPanel.add(logScrollTable);

		initWidget(vPanel);
	}

	/**
	 * refresh item
	 */
	public void refresh(String uuid) {
		ActivityLog.get().status.setGetActivityLog();
		activityLogService.findByFilterByItem(uuid, actionList.getValue(actionList.getSelectedIndex()),
				getChilds.getValue(), new AsyncCallback<List<GWTActivity>>() {
					@Override
					public void onSuccess(List<GWTActivity> result) {
						logScrollTable.reset();
						logScrollTable.getDataTable().resize(0, LOG_NUMBER_OF_COLUMNS);

						for (GWTActivity activity : result) {
							logScrollTable.addRow(activity);
						}

						ActivityLog.get().status.unsetGetActivityLog();
					}

					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("findByFilterByItem", caught);
						ActivityLog.get().status.unsetGetActivityLog();
					}
				});
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		filterText.setHTML(GeneralComunicator.i18nExtension("activitylog.filter"));
		filterGetChildsText.setHTML(GeneralComunicator.i18nExtension("activitylog.filter.get.childs"));
		logScrollTable.langRefresh();
	}

	/**
	 * resetActionList
	 */
	public void resetActionList() {
		actionList.setItemSelected(0, true);
	}
}