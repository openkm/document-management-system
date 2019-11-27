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

package com.openkm.frontend.client.widget.filebrowser;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.webkit.DirectoryCallback;
import com.akjava.gwt.html5.client.file.webkit.FileEntry;
import com.akjava.gwt.html5.client.file.webkit.FilePathCallback;
import com.akjava.gwt.html5.client.file.webkit.Item;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.widget.filebrowser.uploader.DragAndDropUploader;
import com.openkm.frontend.client.widget.filebrowser.uploader.UploaderEvent;

/**
 * DragAndDropBox
 */
public class DragAndDropBox extends Composite {
	private FocusPanel focusPanel;
	private SimplePanel dragAndDropDiv;
	private HTML dragFilesText;

	/**
	 * DragAndDropBox
	 */
	public DragAndDropBox() {
		dragFilesText = new HTML(Main.i18n("filebrowser.drag.file.here"));
		dragAndDropDiv = new SimplePanel();
		dragAndDropDiv.add(dragFilesText);
		focusPanel = new FocusPanel(dragAndDropDiv);

		dragAndDropDiv.setStyleName("okm-DragAndDropBackground");
		dragFilesText.setStyleName("okm-DragAndDropBackground-Content");

		final DragAndDropUploader uploader = createDragAndDropUploader();

		focusPanel.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				setVisible(false);
				event.preventDefault();
				Main.get().dragAndDropPopup.setUploader(uploader);
				uploader.initUploader();

				final FilePathCallback callback = new FilePathCallback() {
					@Override
					public void callback(File file, String path) {
						if (file != null) {
							String actualPath = Main.get().mainPanel.desktop.browser.fileBrowser.getActualPath();
							uploader.addToQueue(file, file.getFileName(), actualPath + path);
						}
					}
				};

				final JsArray<Item> items = FileUtils.transferToItem(event.getNativeEvent());
				Log.debug("length: " + items.length());

				if (items.length() > 0) {
					Main.get().dragAndDropPopup.show();

					for (int i = 0; i < items.length(); i++) {
						FileEntry entry = items.get(i).webkitGetAsEntry();

						if (entry != null) {
							entryCallback(entry, callback, "", uploader);
						} else {
							Main.get().showError("Drag&DropError",
								new OKMException("OKM-" + ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_DragAndDropError, ""));
							break;
						}
					}
				} else {
					Main.get().showError("Drag&DropError",
						new OKMException("OKM-" + ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_DragAndDropError, ""));
				}
			}
		});
		focusPanel.addDragOverHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				// Can not be removed otherwise addDropHandler-> onDrop does not going right
			}
		});
		focusPanel.addDragEnterHandler(new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent arg0) {
				// Can not be removed otherwise addDropHandler-> onDrop does not going right
			}
		});
		focusPanel.addDragLeaveHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				setVisible(false);
			}
		});

		initWidget(focusPanel);
	}

	public void show(int width, int height, int left, int top) {
		setVisible(true);

		// Center the widget and set size
		dragAndDropDiv.setPixelSize(width, height);
		dragAndDropDiv.getElement().getStyle().setLeft(left, Unit.PX);
		dragAndDropDiv.getElement().getStyle().setTop(top, Unit.PX);

		// Center text into the widget
		left = width - dragFilesText.getOffsetWidth();
		top = height - dragFilesText.getOffsetHeight();

		if (left > 0) {
			left = left / 2;
		} else {
			left = 0;
		}
		if (height > 0) {
			top = top / 2;
		} else {
			top = 0;
		}

		dragFilesText.getElement().getStyle().setLeft(left, Unit.PX);
		dragFilesText.getElement().getStyle().setTop(top, Unit.PX);
	}

	/**
	 * Create and configure drag and drop uploader.
	 */
	private DragAndDropUploader createDragAndDropUploader() {
		final DragAndDropUploader uploader = new DragAndDropUploader();
		uploader.setUploadedFileCallback(new AsyncCallback<UploaderEvent>() {
			@Override
			public void onSuccess(final UploaderEvent finishedResult) {
				if (finishedResult != null && finishedResult.getError() != null) {
					Main.get().dragAndDropPopup.setErrorToProgressBar(finishedResult.getFilePath(), finishedResult.getError());
				} else {
					// Refresh actual path
					Main.get().mainPanel.topPanel.toolBar.executeRefresh();
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
			}
		});
		uploader.setUpdateData(new AsyncCallback<UploaderEvent>() {
			@Override
			public void onSuccess(UploaderEvent result) {
				Main.get().dragAndDropPopup.updateProgressBar(result.getFilePath(), result.getPercentage(), result.getAction());
			}

			@Override
			public void onFailure(Throwable arg0) {
			}
		});
		return uploader;
	}

	/**
	 * Process drag and drop files and folders.
	 */
	public void entryCallback(final FileEntry entry, final FilePathCallback callback, String path, final DragAndDropUploader uploader) {
		if (entry == null) {
			return;
		}

		final String actualPath = Main.get().mainPanel.desktop.browser.fileBrowser.getActualPath();
		final String fullPath = entry.getFullPath();
		Main.get().dragAndDropPopup.addProgressBar(fullPath, actualPath);

		if (entry.isFile()) {
			entry.file(callback, path);
		} else if (entry.isDirectory()) {
			uploader.createFolder(entry, fullPath, actualPath + fullPath, new AsyncCallback<UploaderEvent>() {
				@Override
				public void onSuccess(UploaderEvent result) {
					if (result.getError() == null) {
						Main.get().dragAndDropPopup.updateProgressBar(result.getFilePath(), 100, result.getAction());
						entry.getReader().readEntries(
							new DirectoryCallback() {
								@Override
								public void callback(JsArray<FileEntry> entries) {
									callback.callback(null, fullPath);
									for (int j = 0; j < entries.length(); j++) {
										entryCallback(entries.get(j), callback, fullPath, uploader);
									}
								}
							}
						);
					} else {
						// Error in file
						Main.get().dragAndDropPopup.setErrorToProgressBar(actualPath + fullPath, result.getError());
					}
				}

				@Override
				public void onFailure(Throwable e) {
					Main.get().showError("fileBrowser.uploadFiles", e);
				}
			}, Main.CONTEXT);
		}
	}

	public void langRefresh() {
		dragFilesText.setHTML(Main.i18n("filebrowser.drag.file.here"));
	}
}
