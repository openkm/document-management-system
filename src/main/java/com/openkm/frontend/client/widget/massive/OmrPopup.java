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

package com.openkm.frontend.client.widget.massive;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTOmr;
import com.openkm.frontend.client.service.OKMOmrService;
import com.openkm.frontend.client.service.OKMOmrServiceAsync;
import com.openkm.frontend.client.util.Util;

/**
 * OmrPopup popup
 *
 * @author jllort
 */
public class OmrPopup extends DialogBox {
	private final OKMOmrServiceAsync omrService = (OKMOmrServiceAsync) GWT.create(OKMOmrService.class);

	private FlexTable table;
	private Button cancelButton;
	private Button executeButton;
	private HTML templateNameText;
	private ListBox listBox;
	private Status status;
	private GWTDocument doc;

	/**
	 * OmrPopup popup
	 */
	public OmrPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText(Main.i18n("omr.label"));

		table = new FlexTable();
		table.setCellSpacing(0);
		table.setCellPadding(4);

		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		templateNameText = new HTML(Main.i18n("omr.template.name"));
		listBox = new ListBox();
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				if (listBox.getSelectedIndex() > 0) {
					executeButton.setEnabled(true);
				} else {
					executeButton.setEnabled(false);
				}
			}
		});
		listBox.setStyleName("okm-Select");

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
					Main.get().mainPanel.topPanel.toolBar.executeRefresh();
				}
				hide();
			}
		});
		cancelButton.setStyleName("okm-NoButton");

		executeButton = new Button(Main.i18n("button.execute"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				process();
			}
		});
		executeButton.setStyleName("okm-AddButton");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(cancelButton);
		hPanel.add(Util.hSpace("5px"));
		hPanel.add(executeButton);

		hPanel.setCellHorizontalAlignment(cancelButton, VerticalPanel.ALIGN_CENTER);
		hPanel.setCellHorizontalAlignment(executeButton, VerticalPanel.ALIGN_CENTER);

		table.setWidget(0, 0, templateNameText);
		table.setWidget(0, 1, listBox);
		table.setWidget(1, 0, hPanel);
		table.getFlexCellFormatter().setColSpan(1, 0, 2);
		table.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasAlignment.ALIGN_CENTER);

		super.hide();
		setWidget(table);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("omr.label"));
		cancelButton.setText(Main.i18n("button.cancel"));
		executeButton.setText(Main.i18n("button.execute"));
		templateNameText.setHTML(Main.i18n("omr.template.name"));
	}

	/**
	 * reset
	 */
	public void reset(GWTDocument doc) {
		this.doc = doc;
		executeButton.setEnabled(false);
		getOmr();
	}

	/**
	 * getOmr
	 */
	private void getOmr() {
		omrService.getAllOmr(new AsyncCallback<List<GWTOmr>>() {
			@Override
			public void onSuccess(List<GWTOmr> result) {
				listBox.clear();
				listBox.addItem("", ""); // Adds empty value
				for (GWTOmr omr : result) {
					listBox.addItem(omr.getName(), String.valueOf(omr.getId()));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getAllOmr", caught);
			}
		});
	}

	/**
	 * process
	 */
	private void process() {
		if (listBox.getSelectedIndex() > 0) {
			long omId = Long.parseLong(listBox.getValue(listBox.getSelectedIndex()));
			status.setFlagOmr();
			omrService.process(omId, doc.getUuid(), new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					Main.get().mainPanel.topPanel.toolBar.executeRefresh();
					hide();
					status.unsetFlagOmr();
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("process", caught);
					status.unsetFlagOmr();
				}
			});
		}
	}
}