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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.constants.rpc.GWTConvertConstants;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.util.Util;

/**
 * ConvertPopup
 *
 * @author jllort
 *
 */
public class ConvertPopup extends DialogBox {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);

	private VerticalPanel vPanel;
	private CheckBox saveCheck;
	private CheckBox downloadCheck;
	private Button cancelbutton;
	private Button executeButton;
	private GWTDocument doc;
	private HTML formatText;
	private ListBox formats;
	private Status status;

	/**
	 * ConvertPopup
	 */
	public ConvertPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText(Main.i18n("convert.label"));

		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");

		vPanel = new VerticalPanel();

		saveCheck = new CheckBox(Main.i18n("convert.save"));
		saveCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (saveCheck.getValue()) {
					downloadCheck.setValue(false);
				}
				evaluateExecuteButton();
			}
		});
		downloadCheck = new CheckBox(Main.i18n("convert.download"));
		downloadCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (downloadCheck.getValue()) {
					saveCheck.setValue(false);
				}
				evaluateExecuteButton();
			}
		});

		formatText = new HTML(Main.i18n("convert.format"));
		formats = new ListBox();
		formats.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				evaluateExecuteButton();
			}
		});
		formats.setStyleName("okm-Input");

		cancelbutton = new Button(Main.i18n("button.cancel"));
		cancelbutton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancelbutton.setStyleName("okm-NoButton");
		executeButton = new Button(Main.i18n("button.execute"));
		executeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (downloadCheck.getValue()) {
					Util.downloadFilePdf(doc.getUuid());
				} else if (saveCheck.getValue()) {
					String format = formats.getValue(formats.getSelectedIndex());
					if (format.equals(GWTConvertConstants.TO_PDF)) {
						status.setFlagConvert();
						documentService.convertToPdf(doc.getPath(), new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								Main.get().mainPanel.desktop.browser.fileBrowser.refreshOnlyFileBrowser();
								status.unsetFlagConvert();
								hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								Main.get().showError("convertToPdf", caught);
								status.unsetFlagConvert();
							}
						});
					}
				}
			}
		});
		executeButton.setStyleName("okm-YesButton");

		HorizontalPanel hCheckPanel = new HorizontalPanel();
		hCheckPanel.add(saveCheck);
		hCheckPanel.add(Util.hSpace("5px"));
		hCheckPanel.add(downloadCheck);
		hCheckPanel.add(Util.hSpace("5px"));
		hCheckPanel.setStyleName("okm-NoWrap");
		hCheckPanel.setCellVerticalAlignment(saveCheck, HasAlignment.ALIGN_MIDDLE);
		hCheckPanel.setCellVerticalAlignment(downloadCheck, HasAlignment.ALIGN_MIDDLE);

		HorizontalPanel hFormatPanel = new HorizontalPanel();
		hFormatPanel.add(Util.hSpace("5px"));
		hFormatPanel.add(formatText);
		hFormatPanel.add(Util.hSpace("5px"));
		hFormatPanel.add(formats);
		hFormatPanel.add(Util.hSpace("5px"));
		hFormatPanel.setCellVerticalAlignment(formatText, HasAlignment.ALIGN_MIDDLE);
		hFormatPanel.setCellVerticalAlignment(formats, HasAlignment.ALIGN_MIDDLE);

		HorizontalPanel hButtonPanel = new HorizontalPanel();
		hButtonPanel.add(cancelbutton);
		hButtonPanel.add(Util.hSpace("5px"));
		hButtonPanel.add(executeButton);

		vPanel.add(hCheckPanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.add(hFormatPanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.add(hButtonPanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.setCellHorizontalAlignment(hButtonPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setSize("100%", "20px");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * reset
	 *
	 * @param doc
	 */
	public void reset(GWTDocument doc) {
		this.doc = doc;
		saveCheck.setValue(true);
		downloadCheck.setValue(false);
		executeButton.setEnabled(false);
		formats.clear();
		formats.addItem("", "-");
		if (doc.isConvertibleToPdf()) {
			formats.addItem("pdf", GWTConvertConstants.TO_PDF);
		}
	}

	/**
	 * evaluateExecuteButton
	 */
	private void evaluateExecuteButton() {
		if (!saveCheck.getValue() && !downloadCheck.getValue()) {
			executeButton.setEnabled(false);
		} else {
			executeButton.setEnabled(formats.getSelectedIndex() > 0);
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("convert.label"));
		saveCheck.setHTML(Main.i18n("convert.save"));
		downloadCheck.setHTML(Main.i18n("convert.download"));
		formatText.setHTML(Main.i18n("convert.format"));
		cancelbutton.setHTML(Main.i18n("button.cancel"));
		executeButton.setHTML(Main.i18n("button.execute"));
	}
}