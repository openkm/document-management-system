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

import com.akjava.gwt.html5.client.file.File;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.constants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.widget.ConfirmPopup;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Drag and drop uploader class.
 */
public class DragAndDropUploader {
	private final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);
	private final OKMDocumentServiceAsync documentService = GWT.create(OKMDocumentService.class);

	/**
	 * Uploaded file job. Invoked when a file or directory is uploaded.
	 */
	private AsyncCallback<UploaderEvent> uploadedFileCallback;

	/**
	 * Update uploaded item state callback.
	 */
	private AsyncCallback<UploaderEvent> updateDataCallback;

	/**
	 * Upload files queue.
	 */
	private Queue<UploaderQueueElement> queue = new LinkedList<UploaderQueueElement>();

	/**
	 * Current element. Used to abort the drag and drop if needed.
	 */
	private JavaScriptObject currentElement;

	/**
	 * If the operation is cancelled by user.
	 */
	private boolean cancelled;

	/**
	 * Overwrite files;
	 */
	private Boolean overwriteFiles;

	public void setUpdateData(AsyncCallback<UploaderEvent> updateDataCallback) {
		this.updateDataCallback = updateDataCallback;
	}

	public void setUploadedFileCallback(AsyncCallback<UploaderEvent> asyncCallback) {
		this.uploadedFileCallback = asyncCallback;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Decrease counter. If queue is empty execute finished callback.
	 */
	private void decreaseCounter() {
		if (queue.isEmpty()) {
			currentElement = null;
			uploadedFileCallback.onSuccess(null);
		} else {
			final UploaderQueueElement element;

			synchronized (queue) {
				element = queue.remove();
				Main.get().mainPanel.topPanel.setDragAndDropPendingFilesToUpload(queue.size());
			}

			insertOrUpdateFile(element.getFile(), element.getName(), element.getPath());
		}
	}

	/**
	 * There was an error uploading a file or folder. Call finished callback.
	 */
	private void fileError(String filePath, String error) {
		UploaderEvent event = new UploaderEvent(filePath, 1.0, error);
		uploadedFileCallback.onSuccess(event);
		decreaseCounter();
	}

	/**
	 * Upload item progress.
	 */
	private boolean uploadProgressCallback(String filePath, double bytesComplete, double bytesTotal, int action) {
		double percentage = (bytesComplete / bytesTotal);
		Main.get().mainPanel.topPanel.setDragAndDropPercentageUploading((int) (percentage * 100));
		updateDataCallback.onSuccess(new UploaderEvent(filePath, percentage, action));
		return true;
	}

	/**
	 * Upload folder callback.
	 */
	private void callUploadedFolder(AsyncCallback<UploaderEvent> callback, String path, String error) {
		UploaderEvent event = new UploaderEvent(path, 100, error);
		callback.onSuccess(event);
	}

	/**
	 * Abort drag and drop. Establish error tu current element and to every queued element.
	 */
	private void abort(String filePath) {
		// Cancel aborted element
		UploaderEvent event = new UploaderEvent(filePath, 100, "canceled.by.user");
		uploadedFileCallback.onSuccess(event);

		synchronized (queue) {
			while (!queue.isEmpty()) {
				UploaderQueueElement element = queue.remove();
				UploaderEvent tempEvent = new UploaderEvent(element.getPath() + "/" + element.getFile().getFileName(), 1.0, "canceled.by.user");
				uploadedFileCallback.onSuccess(tempEvent);
			}

			Main.get().mainPanel.topPanel.setDragAndDropPendingFilesToUpload(0);

			// Refresh the window
			uploadedFileCallback.onSuccess(null);
		}
	}

	/**
	 * Upload file. This is a Javascript native function.
	 */
	public native JavaScriptObject uploadFile(JavaScriptObject file, String filePostName, String path, int action, boolean autoCheckOut, String context) /*-{
      var self = this;
      var xhr = new XMLHttpRequest();
      var url = context + '/frontend/FileUpload';

      // Setup the event handlers we'll need to let the consuming application know what's going on
      xhr.upload.addEventListener('progress', function (e) {
        return self.@com.openkm.frontend.client.widget.filebrowser.uploader.DragAndDropUploader::uploadProgressCallback(*)(path + '/' + file.name,
          e.loaded, e.total, action);
      }, false);

      xhr.addEventListener('abort', function (e) {
        // Cancelled by user
        self.@com.openkm.frontend.client.widget.filebrowser.uploader.DragAndDropUploader::abort(*)(path + '/' + file.name)
      }, false);

      xhr.open('POST', url, true);

      xhr.onreadystatechange = function (aEvt) {
        if (xhr.readyState == 4) {
          if ("" !== xhr.response && xhr.status == 200) {
            if ("" !== JSON.parse(xhr.response).error) {
              // Error
              self.@com.openkm.frontend.client.widget.filebrowser.uploader.DragAndDropUploader::fileError(*)(path + '/' + file.name, JSON.parse(xhr.response).error)
            } else {
              // File created
              self.@com.openkm.frontend.client.widget.filebrowser.uploader.DragAndDropUploader::decreaseCounter()()
            }
          }
        }
      };

      var formData = new FormData();
      formData.append(filePostName, file);

      if (action == 0) {
        formData.append('path', path);
      } else {
        formData.append('path', path + '/' + file.name);
      }

      formData.append('action', action);

      if (autoCheckOut) {
        formData.append('autoCheckOut', autoCheckOut);
      }

      // Kick off the multipart/form-data upload
      xhr.send(formData);

      return xhr;
    }-*/;

	/**
	 * Upload folder. This is a Javascript native function.
	 */
	public native JavaScriptObject createFolder(JavaScriptObject file, String filePostName, String path, AsyncCallback<UploaderEvent> callback, String context) /*-{
      var self = this;
      var xhr = new XMLHttpRequest();
      var url = context + '/frontend/CreateFolder';

      xhr.open('POST', url, true);

      xhr.onreadystatechange = function (aEvt) {
        if (xhr.readyState == 4) {
          if ("" !== xhr.response && "" !== JSON.parse(xhr.response).error) {
            // Error
            self.@com.openkm.frontend.client.widget.filebrowser.uploader.DragAndDropUploader::callUploadedFolder(*)(callback, path, JSON.parse(xhr.response).error)
          } else {
            // Folder created
            self.@com.openkm.frontend.client.widget.filebrowser.uploader.DragAndDropUploader::callUploadedFolder(*)(callback, path, null)
          }
        }
      };

      var formData = new FormData();
      formData.append(filePostName, file);
      formData.append('path', path);

      // Kick off the multipart/form-data upload
      xhr.send(formData);

      return xhr;
    }-*/;

	/**
	 * Check if the HTML5 Api is supported in the browser.
	 */
	public static native boolean isHTML5DragAndDropApiSupported() /*-{
      return typeof DataTransferItem !== "undefined" && DataTransferItem.prototype.webkitGetAsEntry instanceof Function;
    }-*/;

	/**
	 * Add an element to queue.
	 */
	public void addToQueue(File file, String name, String path) {
		if (cancelled) {
			// Folders continue reading but the user cancel the operation. This element must be cancelled also
			UploaderEvent event = new UploaderEvent(path + "/" + file.getFileName(), 1.0, "canceled.by.user");
			uploadedFileCallback.onSuccess(event);
		} else if (currentElement == null) {
			// Only one item could be processed at same time and insertOrUpdateFile is not setting this var until some async calls
			currentElement = JavaScriptObject.createObject();
			insertOrUpdateFile(file, name, path);
		} else {
			synchronized (queue) {
				queue.add(new UploaderQueueElement(file, name, path));
				Main.get().mainPanel.topPanel.setDragAndDropPendingFilesToUpload(queue.size());
			}
		}
	}

	/**
	 *
	 */
	private void insertOrUpdateFile(final File file, final String name, final String path) {
		final String docPath = path + "/" + file.getFileName();

		synchronized (queue) {
			repositoryService.hasNode(docPath, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						// Check if global property to ask for file updates
						if (Main.get().workspaceUserProperties.getWorkspace().isAskDragAndDropUpdates()) {
							if (overwriteFiles == null) {
								// If the checkbox for override all is not configured yet
								DragAndDropWrapper wrapper = new DragAndDropWrapper(DragAndDropUploader.this, docPath, file, name, path);
								Main.get().confirmPopup.setValue(wrapper);
								Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DRAG_AND_DROP_UPDATE);
								Main.get().confirmPopup.show();
							} else if (overwriteFiles) {
								// If checkbox value is set and cancel option was selected
								executeUpload(true, docPath, file, name, path);
							} else {
								// If checkbox value is set and accept option was selected
								fileError(docPath, "canceled.by.user");
							}
						} else {
							executeUpload(true, docPath, file, name, path);
						}
					} else {
						currentElement = uploadFile(file, name, path, UIFileUploadConstants.ACTION_INSERT, false, Main.CONTEXT);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("uploadValidation", caught);
				}
			});
		}
	}

	public void executeUpload(boolean execute, String docPath, final File file, final String name, final String path) {
		if (execute) {
			documentService.getProperties(docPath, new AsyncCallback<GWTDocument>() {
				@Override
				public void onSuccess(GWTDocument document) {
					currentElement = uploadFile(file, name, path, UIFileUploadConstants.ACTION_UPDATE,
						!document.isCheckedOut(), Main.CONTEXT);
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("insertOrUpdateFile", caught);
				}
			});
		} else {
			fileError(docPath, "canceled.by.user");
		}
	}

	/**
	 * Cancel task.
	 */
	public void cancelTask(boolean cancel) {
		this.cancelled = cancel;
		if (cancel) {
			((XMLHttpRequest) currentElement).abort();
		}
	}

	/**
	 * Initialize uploader.
	 */
	public void initUploader() {
		this.cancelled = false;
		this.currentElement = null;
		this.overwriteFiles = null;
		Main.get().confirmPopup.setCheckboxValue(false);
	}

	/**
	 * Set overwrite value
	 */
	public void setOverwriteValue(Boolean value) {
		this.overwriteFiles = value;
	}

	/**
	 * Store drag and drop information used in confirmation popup
	 */
	public class DragAndDropWrapper {
		private DragAndDropUploader uploader;
		private String docPath;
		private File file;
		private String name;
		private String path;

		public DragAndDropWrapper(DragAndDropUploader uploader, String docPath, File file, String name, String path) {
			this.uploader = uploader;
			this.docPath = docPath;
			this.file = file;
			this.name = name;
			this.path = path;
		}

		public DragAndDropUploader getUploader() {
			return uploader;
		}

		public String getDocPath() {
			return docPath;
		}

		public File getFile() {
			return file;
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			return path;
		}
	}
}
