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

package com.openkm.frontend.client.widget.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTKeyValue;
import com.openkm.frontend.client.bean.form.GWTSuggestBox;
import com.openkm.frontend.client.service.OKMKeyValueService;
import com.openkm.frontend.client.service.OKMKeyValueServiceAsync;
import com.openkm.frontend.client.util.MessageFormat;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.searchin.HasPropertyHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DatabaseRecordSelectPopup
 *
 * @author jllort
 *
 */
public class DatabaseRecordSelectPopup extends DialogBox {
	private final OKMKeyValueServiceAsync keyValueService = (OKMKeyValueServiceAsync) GWT.create(OKMKeyValueService.class);

	private VerticalPanel vPanel;
	private ScrollPanel scrollDatabaseRecordPanel;
	private TextBox record;
	private FlexTable recordTabla;
	private Button cancelButton;
	private Button acceptButton;
	private int selectedRow = -1;
	private Map<Integer, GWTKeyValue> rowKeyValueMap;
	private List<String> tables;
	private Status status;
	private GWTSuggestBox suggestBox;

	/**
	 * DatabaseRecordSelectPopup
	 */
	public DatabaseRecordSelectPopup(final GWTSuggestBox suggestBox, final HasDatabaseRecord databaseRecord, final HasPropertyHandler propertyHandler) {
		// Establishes auto-close when click outside
		super(false, true);
		this.suggestBox = suggestBox;

		tables = new ArrayList<String>();
		if (suggestBox.getTable() != null) {
			tables.add(suggestBox.getTable());
		}
		setText(suggestBox.getDialogTitle());

		vPanel = new VerticalPanel();
		vPanel.setWidth("300px");
		vPanel.setHeight("200px");

		record = new TextBox();
		record.setWidth("292px");
		record.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (record.getText().length() >= suggestBox.getFilterMinLen()) {
					findFilteredDatabaseRecords();
				} else {
					removeAllRows();
				}
			}
		});
		record.setStyleName("okm-Input");

		recordTabla = new FlexTable();
		recordTabla.setWidth("100%");
		recordTabla.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(recordTabla.getCellForEvent(event).getRowIndex());
				acceptButton.setEnabled(true);
			}
		});

		scrollDatabaseRecordPanel = new ScrollPanel(recordTabla);
		scrollDatabaseRecordPanel.setPixelSize(290, 150);
		scrollDatabaseRecordPanel.setStyleName("okm-Popup-text");

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancelButton.setStyleName("okm-NoButton");

		acceptButton = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedRow >= 0) {
					databaseRecord.setKeyValue(rowKeyValueMap.get(selectedRow));
					// Updating suggestbox values ( for when value will be update )
					suggestBox.setValue(rowKeyValueMap.get(selectedRow).getKey());
					suggestBox.setText(rowKeyValueMap.get(selectedRow).getValue());
					if (propertyHandler != null) {
						propertyHandler.metadataValueChanged();
					}
				}
				hide();
			}
		});
		acceptButton.setEnabled(false);
		acceptButton.setStyleName("okm-YesButton");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(acceptButton);

		if (suggestBox.getFilterMinLen() > 0) {
			HorizontalPanel hInfoPanel = new HorizontalPanel();
			HTML filterInfo = new HTML(MessageFormat.format(Main.i18n("form.manager.suggestbox.min.filter"), suggestBox.getFilterMinLen()));
			HTML space = Util.hSpace("5px");
			hInfoPanel.add(filterInfo);
			hInfoPanel.add(space);
			vPanel.add(hInfoPanel);
			vPanel.setCellHorizontalAlignment(hInfoPanel, HasAlignment.ALIGN_RIGHT);
		}

		vPanel.add(record);
		vPanel.add(scrollDatabaseRecordPanel);
		vPanel.add(hPanel);

		vPanel.setCellHeight(record, "25px");
		vPanel.setCellHeight(hPanel, "25px");
		vPanel.setCellVerticalAlignment(record, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellHorizontalAlignment(record, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(scrollDatabaseRecordPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);

		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * findFilteredDatabaseRecords
	 */
	private void findFilteredDatabaseRecords() {
		removeAllRows();
		selectedRow = -1;
		record.setReadOnly(true);
		acceptButton.setEnabled(false);
		status.setGetDatabaseRecords();
		keyValueService.getKeyValues(tables, MessageFormat.format(suggestBox.getFilterQuery(), record.getText()), new AsyncCallback<List<GWTKeyValue>>() {
			@Override
			public void onSuccess(List<GWTKeyValue> result) {
				rowKeyValueMap = new HashMap<Integer, GWTKeyValue>();
				for (GWTKeyValue keyValue : result) {
					int row = recordTabla.getRowCount();
					rowKeyValueMap.put(row, keyValue);
					recordTabla.setHTML(row, 0, keyValue.getValue());
				}
				record.setReadOnly(false);
				status.unsetGetDatabaseRecords();
			}

			@Override
			public void onFailure(Throwable caught) {
				status.unsetGetDatabaseRecords();
				Main.get().showError("getKeyValues", caught);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#show()
	 */
	public void show() {
		removeAllRows();
		record.setText("");
		record.setReadOnly(false);
		acceptButton.setEnabled(false);
		rowKeyValueMap = new HashMap<Integer, GWTKeyValue>();
		super.show();
		record.setFocus(true);
		// Case must show by default all values
		if (suggestBox.getFilterMinLen() == 0) {
			findFilteredDatabaseRecords();
		}
	}

	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		recordTabla.removeAllRows();
		selectedRow = -1;
	}

	/**
	 * markSelectedRow
	 *
	 * @param row
	 */
	private void markSelectedRow(int row) {
		// And row must be other than the selected one
		if (row != selectedRow) {
			styleRow(selectedRow, false);
			styleRow(row, true);
			selectedRow = row;
		}
	}

	/**
	 * Change the style row selected or unselected
	 *
	 * @param row The row afected
	 * @param selected Indicates selected unselected row
	 */
	private void styleRow(int row, boolean selected) {
		if (row >= 0) {
			if (selected) {
				recordTabla.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				recordTabla.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
			}
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		cancelButton.setHTML(Main.i18n("button.cancel"));
		acceptButton.setHTML(Main.i18n("button.accept"));
	}
}