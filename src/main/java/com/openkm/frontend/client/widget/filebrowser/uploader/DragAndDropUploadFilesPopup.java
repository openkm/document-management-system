/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.frontend.client.widget.filebrowser.uploader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;
import com.openkm.frontend.client.util.Util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Drag and drop popup.
 */
public class DragAndDropUploadFilesPopup extends DialogBox {
	private VerticalPanel vPanel;
	private Button buttonClose;
	private Button buttonCancel;
	private Button buttonClean;
	private ScrollPanel sPanel;
	private FlexTable innerTable;

	private final Map<String, UploaderProgressBar> progressBars = new LinkedHashMap<>();
	private DragAndDropUploader uploader;

	public DragAndDropUploadFilesPopup() {
		super(false, true);
		setText(Main.i18n("popup.fileupload"));
		setStyleName("okm-Popup");

		vPanel = new VerticalPanel();
		sPanel = new ScrollPanel();
		sPanel.setWidth("600px");
		sPanel.setHeight("350px");
		innerTable = new FlexTable();
		innerTable.setCellPadding(1);
		innerTable.setCellSpacing(0);
		innerTable.setWidth("100%");
		sPanel.add(innerTable);
		buttonClose = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		buttonCancel = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				uploader.cancelTask(true);
			}
		});
		buttonClean = new Button(Main.i18n("button.clean.completed"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeCompletedTasks();
			}

		});
		buttonClose.setStyleName("okm-YesButton");
		buttonCancel.setStyleName("okm-DeleteButton");
		buttonClean.setStyleName("okm-CleanButton");
		vPanel.add(sPanel);
		vPanel.add(Util.vSpace("5px"));
		HorizontalPanel hPanelLegend = new HorizontalPanel();
		hPanelLegend.add(new DragAndDropLegendItem(Main.i18n("drag.legend.inserted"), "gwt-ProgressBar-bar"));
		hPanelLegend.add(UtilComunicator.hSpace("15px"));
		hPanelLegend.add(new DragAndDropLegendItem(Main.i18n("drag.legend.updated"), "gwt-ProgressBar-bar-update"));
		hPanelLegend.add(UtilComunicator.hSpace("15px"));
		hPanelLegend.add(new DragAndDropLegendItem(Main.i18n("drag.legend.canceled.error"), "okm-progressBarError"));
		vPanel.add(hPanelLegend);
		vPanel.add(Util.vSpace("5px"));
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(buttonClean);
		hPanel.add(UtilComunicator.hSpace("5px"));
		hPanel.add(buttonClose);
		hPanel.add(UtilComunicator.hSpace("5px"));
		hPanel.add(buttonCancel);
		vPanel.add(hPanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.setCellHorizontalAlignment(innerTable, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(sPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanelLegend, VerticalPanel.ALIGN_CENTER);
		hide();
		setWidget(vPanel);
	}

	private void removeCompletedTasks() {
		synchronized (innerTable) {
			for (int row = 0; row < innerTable.getRowCount(); ++row) {
				try {
					HorizontalPanel panel = (HorizontalPanel) innerTable.getWidget(row, 0);
					UploaderProgressBar progressBar = (UploaderProgressBar) panel.getWidget(0);
					if (uploader.isCancelled() || 1.0d == progressBar.getPercent() || progressBar.isError()) {
						innerTable.removeCell(row, 0);
					}
				} catch (Exception e) {
					// The item was previous deleted. Nothing to do
				}
			}
		}
	}

	@Override
	public void show() {
		super.show();
		center();
	}

	public void addPanel(Panel panel) {
		synchronized (innerTable) {
			innerTable.setWidget(innerTable.getRowCount(), 0, panel);
		}
	}

	public void addProgressBar(String entryPath, String actualPath) {
		final String filePath = actualPath + entryPath;
		final UploaderProgressBar progressBar = new UploaderProgressBar(0.0, 1.0, 0.0);
		progressBars.put(filePath, progressBar);

		HorizontalPanel progressBarAndButtonPanel = new HorizontalPanel();
		progressBarAndButtonPanel.add(progressBar);
		progressBarAndButtonPanel.add(Util.hSpace("5px"));
		Label nameLabel = new Label(Util.shortenPath(Util.decodeEntities(filePath), 80));
		progressBarAndButtonPanel.add(nameLabel);
		addPanel(progressBarAndButtonPanel);
	}

	public void setErrorToProgressBar(String finishedResult, String error) {
		UploaderProgressBar progressBar = progressBars.get(finishedResult);
		if (progressBar != null) {
			progressBar.setStyleName("okm-progressBarError");
			progressBar.setTitle(Main.i18n(error));
			progressBar.setError(error);
			progressBar.setProgress(0);
		}
	}

	public void updateProgressBar(String filePath, double percentage, int action) {
		UploaderProgressBar progressBar = progressBars.get(filePath);
		progressBar.setProgress(percentage);
		progressBar.setAction(action);
	}

	public void setUploader(DragAndDropUploader uploader) {
		this.uploader = uploader;
	}

	private class DragAndDropLegendItem extends SimplePanel {
		public DragAndDropLegendItem(String label, String className) {
			HorizontalPanel panel = new HorizontalPanel();
			SimplePanel colourPanel = new SimplePanel();
			colourPanel.addStyleName(className);
			colourPanel.addStyleName("gwt-ProgressBar-bar-legend");
			colourPanel.setWidth("10px");
			colourPanel.setHeight("10px");
			panel.add(colourPanel);
			panel.add(Util.hSpace("5px"));
			panel.add(new Label(label));
			setWidget(panel);
		}
	}
}
