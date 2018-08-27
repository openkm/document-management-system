/**
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
package com.openkm.frontend.client.widget.upload;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.widgetideas.client.ProgressBar;
import com.google.gwt.widgetideas.client.ProgressBar.TextFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.FileToUpload;
import com.openkm.frontend.client.bean.GWTFileUploadResponse;
import com.openkm.frontend.client.bean.GWTFileUploadingStatus;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.constants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.service.OKMGeneralServiceAsync;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.notify.NotifyPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * FancyFileUpload
 *
 * @author jllort
 */
public class FancyFileUpload extends Composite implements HasText, HasChangeHandlers {
	private final OKMGeneralServiceAsync generalService = GWT.create(OKMGeneralService.class);
	private final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);

	/**
	 * State definitions
	 */
	public static final int EMPTY_STATE = 1;
	public static final int PENDING_STATE = 2;
	public static final int UPLOADING_STATE = 3;
	public static final int UPLOADED_STATE = 4;
	public static final int FAILED_STATE = 5;
	public static final int MAX_FILENAME_LENGHT = 60;

	/**
	 * OK message expected from file upload servlet to indicate successful upload.
	 */
	private static final String returnErrorMessage = "OKM-";

	/**
	 * Default delay for pending state, when delay over the form is submitted.
	 */
	private static final int PENDING_UPDATE_DELAY = 1000;
	private static final int REFRESH_STATUS_DELAY = 250;

	/**
	 * Initial State of the widget.
	 */
	private int widgetState = EMPTY_STATE;

	private VerticalPanel mainPanel = new VerticalPanel();
	public CheckBox notifyToUser = new CheckBox();
	private HorizontalPanel hIncreaseVersionPanel = new HorizontalPanel();
	private CheckBox increaseMajorVersion = new CheckBox();
	private CheckBox increaseMinorVersion = new CheckBox();
	private CheckBox importZip = new CheckBox();
	private HTML versionCommentText = new HTML();
	private HorizontalPanel hNotifyPanel = new HorizontalPanel();
	private HorizontalPanel hUnzipPanel = new HorizontalPanel();
	public NotifyPanel notifyPanel = new NotifyPanel();
	private HTML versionHTMLBR;
	private TextArea versionComment;
	private ScrollPanel versionCommentScrollPanel;
	public TextBox mails;
	public TextBox users;
	public TextBox roles;
	private TextArea message;
	private VerticalPanel vNotifyPanel = new VerticalPanel();
	private VerticalPanel vVersionCommentPanel = new VerticalPanel();
	private HTML commentTXT;
	private ScrollPanel messageScroll;
	public HTML errorNotify;
	private ProgressBar progressBar;
	private TextFormatter progressiveFormater;
	private TextFormatter finalFormater;
	private boolean wizard = false;
	private int action = UIFileUploadConstants.ACTION_NONE;
	private FileUploadForm uploadForm;
	private List<FileToUpload> filesToUpload = new ArrayList<FileToUpload>();
	private List<FileToUpload> pendingFileToUpload = new ArrayList<FileToUpload>();
	private FileToUpload actualFileToUpload;
	private List<FileToUpload> uploadedWorkflowFiles = new ArrayList<FileToUpload>();
	private DisclosurePanel diclousureFilesPanel;
	private VerticalPanel pendingFilePanel;

	/**
	 * Internal timer for checking if pending delay is over.
	 */
	private Timer p;

	/**
	 * Widget representing file to be uploaded.
	 */
	private UploadDisplay uploadItem;

	/**
	 * FileName to be uploaded
	 */
	String fileName = "";

	/**
	 * Uploading status
	 */
	private GWTFileUploadingStatus fileUploadingStatus = new GWTFileUploadingStatus();
	private boolean fileUplodingStartedFlag = false;

	/**
	 * Class used for the display of filename to be uploaded, and handling the update of the display states.
	 */
	protected class UploadDisplay extends Composite {

		/**
		 * Label to display after file widget is filled with a filename
		 */
		HTML status = new HTML();

		/**
		 * Label to display if some error on unzip uplaoded file
		 */
		HTML statusZipNotify;
		ScrollPanel statusZipNotifyScroll;

		/**
		 * Panel to hold the widget
		 */
		FlowPanel mainPanel = new FlowPanel();

		/**
		 * Panel to hold pending, loading, loaded or failed state details.
		 */
		VerticalPanel pendingPanel = new VerticalPanel();

		VerticalPanel hFileUpload = new VerticalPanel();

		/**
		 * Constructor
		 */
		public UploadDisplay() {
			hFileUpload.setWidth("350px");

			status.setWidth("100%");
			status.setWordWrap(true);
			status.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

			// Adds error panel when zip file is uploaded
			statusZipNotify = new HTML();
			statusZipNotify.setSize("100%", "100%");
			statusZipNotify.setVisible(true);
			statusZipNotifyScroll = new ScrollPanel(statusZipNotify);
			statusZipNotifyScroll.setAlwaysShowScrollBars(false);
			statusZipNotifyScroll.setVisible(false);
			statusZipNotifyScroll.setSize("375px", "100px");
			statusZipNotifyScroll.setStyleName("okm-Bookmark-Panel");
			statusZipNotifyScroll.addStyleName("okm-Input");

			progressiveFormater = new TextFormatter() {
				@Override
				protected String getText(ProgressBar bar, double curProgress) {
					String text = "";
					text += Util.formatSize(curProgress);
					text += " " + Main.i18n("fileupload.status.of") + " ";
					text += Util.formatSize(progressBar.getMaxProgress());
					text += " " + (int) (100 * progressBar.getPercent()) + "% ";
					return text;
				}
			};

			finalFormater = new TextFormatter() {
				@Override
				protected String getText(ProgressBar bar, double curProgress) {
					String text = " " + (int) (100 * progressBar.getPercent()) + "% ";
					return text;
				}
			};

			progressBar = new ProgressBar();
			progressBar.setTextFormatter(progressiveFormater);

			HorizontalPanel hPBPanel = new HorizontalPanel();
			hPBPanel.add(progressBar);
			hPBPanel.setCellVerticalAlignment(progressBar, HasAlignment.ALIGN_MIDDLE);

			// Corrects some problem with centering progress status
			hPBPanel.setCellHorizontalAlignment(progressBar, HasAlignment.ALIGN_LEFT);
			progressBar.setSize("360px", "20px");

			pendingPanel.setWidth("375px");
			pendingPanel.setVisible(true);
			pendingPanel.add(status);
			pendingPanel.add(hPBPanel);

			pendingPanel.setCellHorizontalAlignment(hPBPanel, HasAlignment.ALIGN_CENTER);

			diclousureFilesPanel = new DisclosurePanel(Main.i18n("fileupload.label.pending.queue"));
			pendingFilePanel = new VerticalPanel();
			diclousureFilesPanel.add(pendingFilePanel);
			diclousureFilesPanel.setVisible(false);

			mainPanel.add(pendingPanel);
			mainPanel.add(diclousureFilesPanel);
			mainPanel.add(hFileUpload);

			mainPanel.add(statusZipNotifyScroll);

			initWidget(mainPanel);
		}

		/**
		 * Set the widget into pending mode by altering style of pending panel and displaying it. Hide the FileUpload
		 * widget and finally set the state to Pending.
		 */
		private void setPending() {
			if (fileName.length() > (MAX_FILENAME_LENGHT - 20)) {
				status.setHTML(Main.i18n("fileupload.status.sending") + " "
						+ fileName.substring(0, (MAX_FILENAME_LENGHT - 20)) + " ...");
			} else {
				status.setHTML(Main.i18n("fileupload.status.sending") + " " + fileName);
			}

			pendingPanel.setStyleName("fancyfileupload-pending");
			widgetState = PENDING_STATE;
			fireChange();
		}

		/**
		 * Set the widget into Loading mode by changing the style name and updating the widget State to Uploading.
		 */
		public void setLoading() {
			pendingPanel.setStyleName("fancyfileupload-loading");
			pendingPanel.setVisible(true);
			widgetState = UPLOADING_STATE;
			fileUplodingStartedFlag = true; // Active flash uploading is started
			getFileUploadStatus();
			fireChange();
		}

		/**
		 * Set the widget into pending mode by altering style of pending panel and displaying it. Hide the FileUpload
		 * widget and finally set the state to Pending.
		 */
		private void setProcessing() {
			status.setHTML(Main.i18n("fileupload.status.processing"));
		}

		/**
		 * Set the widget to Loaded mode by changing the style name and updating the widget State to Loaded.
		 */
		private void setLoaded() {
			// Sometimes if upload is fast, has no time to getting file uploading status
			// information on this cases must be setting it directly ( simulating )
			progressBar.setTextFormatter(finalFormater);
			progressBar.setMaxProgress(100);
			progressBar.setProgress(100);

			pendingPanel.setStyleName("fancyfileupload-loaded");
			status.setHTML(Main.i18n("fileupload.status.ok"));
			widgetState = UPLOADED_STATE;
			fileUplodingStartedFlag = false;

			// normal case is not a workflow
			if (!wizard && actualFileToUpload.getWorkflow() == null) {
				refresh(true);
			}

			fireChange();
			Main.get().mainPanel.dashboard.userDashboard.getUserLastModifiedDocuments();
			Main.get().mainPanel.dashboard.userDashboard.getUserCheckedOutDocuments();
			Main.get().mainPanel.dashboard.userDashboard.getUserLastUploadedDocuments();
			Main.get().workspaceUserProperties.getUserDocumentsSize();
			uploadNewPendingFile();
		}

		/**
		 * Set the widget to Failed mode by changing the style name and updating the widget State to Failed.
		 * Additionally, hide the pending panel and display the FileUpload widget.
		 */
		private void setFailed(String msg) {
			// Sometimes if upload is fast, has no time to getting file uploading status
			// information on this cases must be setting it directly ( simulating )
			if (fileUploadingStatus.getContentLength() == 0) {
				progressBar.setTextFormatter(finalFormater);
				progressBar.setMaxProgress(100);
				progressBar.setProgress(100);
			}

			status.setHTML(Main.i18n("fileupload.status.error"));

			if (importZip.getValue()) {
				statusZipNotify.setHTML(msg.replaceAll("\n", "<br/>"));
				statusZipNotifyScroll.setVisible(true);
				pendingPanel.setVisible(true);
				Main.get().showError(fileName, new Throwable(Main.i18n("fileupload.label.error.importing.zip")));
			} else if (msg.contains(returnErrorMessage)) {
				Main.get().showError(fileName,
						new Throwable(Main.i18n(msg.substring(msg.indexOf("OKM"), msg.indexOf("OKM") + 10))));
			} else {
				Main.get().showError(fileName,
						new Throwable(Main.i18n("fileupload.label.error.importing.zip") + " (" + msg + ")"));
			}

			pendingPanel.setStyleName("fancyfileupload-failed");
			widgetState = FAILED_STATE;
			fileUplodingStartedFlag = false;
			refresh(true);
			fireChange();
			uploadNewPendingFile();
		}

		/**
		 * Reset the display
		 */
		private void reset(boolean enableImport, boolean enableNotifyButton) {
			widgetState = EMPTY_STATE;
			fireChange();

			// Reseting values
			increaseMajorVersion.setValue(false);
			increaseMinorVersion.setValue(false);
			increaseMajorVersion.setHTML(Main.i18n("fileupload.increment.major.version"));
			increaseMinorVersion.setHTML(Main.i18n("fileupload.increment.minor.version"));
			fileName = "";
			status.setText("");
			statusZipNotify.setText("");
			statusZipNotifyScroll.setVisible(false);
			message.setText("");
			versionComment.setText("");
			mails.setText("");
			users.setText("");
			roles.setText("");
			notifyPanel.reset();
			getAllUsers();

			// On on root stack panel enabled must be enabled notify to user option
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TAXONOMY) {
				hNotifyPanel.setVisible(enableNotifyButton);
			} else {
				hNotifyPanel.setVisible(enableNotifyButton);
			}

			errorNotify.setVisible(false);
			vNotifyPanel.setVisible(false);
			notifyToUser.setValue(false);
			importZip.setValue(false);
			hFileUpload.setVisible(true);
			pendingPanel.setVisible(false);
			hUnzipPanel.setVisible(enableImport);
			hIncreaseVersionPanel.setVisible(action == UIFileUploadConstants.ACTION_UPDATE);

			resetProgressBar();
		}

		/**
		 * resetWhileUploading
		 */
		public void resetWhileUploading(boolean enableImport, boolean enableNotifyButton) {
			// Reseting values
			increaseMajorVersion.setValue(false);
			increaseMinorVersion.setValue(false);
			statusZipNotify.setText("");
			statusZipNotifyScroll.setVisible(false);
			message.setText("");
			versionComment.setText("");
			users.setText("");
			roles.setText("");
			notifyPanel.reset();
			getAllUsers();

			// On on root stack panel enabled must be enabled notify to user option
			if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TAXONOMY) {
				hNotifyPanel.setVisible(enableNotifyButton);
			} else {
				hNotifyPanel.setVisible(enableNotifyButton);
			}

			vNotifyPanel.setVisible(false);
			notifyToUser.setValue(false);
			importZip.setValue(false);
			hFileUpload.setVisible(true);
			hUnzipPanel.setVisible(enableImport);
			hIncreaseVersionPanel.setVisible(action == UIFileUploadConstants.ACTION_UPDATE);
		}

		/**
		 * Inits values before reset ( used to correct center panel )
		 */
		private void init() {
			vNotifyPanel.setVisible(true);
		}
	}

	/**
	 * Refresh folders and documents
	 */
	public void refresh(boolean refresh) {
		if (refresh) {
			if (importZip.getValue()) {
				Main.get().activeFolderTree.refresh(true);
			} else {
				Main.get().mainPanel.desktop.browser.fileBrowser.refresh(Main.get().activeFolderTree.getActualPath());
			}
		}
	}

	/**
	 * Perform the uploading of a file by changing state of display widget and then calling form.submit() method.
	 */
	private void uploadFiles() {
		FileUploadForm uploadForm = pendingFileToUpload.get(0).getUploadForm();

		// Store some values to uploadForm
		uploadForm.setNotifyToUser(notifyToUser.getValue());
		uploadForm.setImportZip(importZip.getValue());
		uploadForm.setVersionCommnent(versionComment.getText());
		uploadForm.setMails(mails.getText());
		uploadForm.setUsers(users.getText());
		uploadForm.setRoles(roles.getText());
		uploadForm.setMessage(message.getText());

		if (increaseMajorVersion.getValue()) {
			uploadForm.setIncreaseVersion(1);
		} else if (increaseMinorVersion.getValue()) {
			uploadForm.setIncreaseVersion(2);
		} else {
			uploadForm.setIncreaseVersion(0);
		}

		uploadForm.setVisible(false);
		enqueueFileToUpload(new ArrayList<FileToUpload>(Arrays.asList(pendingFileToUpload.remove(0))));

		// Ensure comment is hidden ( we're comming from updating file )
		versionComment.setVisible(false);
		versionCommentText.setVisible(false);
		versionHTMLBR.setVisible(false);
		hIncreaseVersionPanel.setVisible(false);
	}

	/**
	 * Put the widget into a Pending state, set the Pending delay timer to call the upload file method when ran out.
	 */
	public void pendingUpload() {
		// Fire an onChange event to anyone who is listening
		uploadFiles();
	}

	/**
	 * FancyFileUpload.
	 */
	public FancyFileUpload() {
		// Create a new upload display widget
		uploadItem = new UploadDisplay();

		// Add the new widget to the panel.
		mainPanel.add(uploadItem);

		// Adds error panel, whem user select notify but not select any user
		errorNotify = new HTML(Main.i18n("fileupload.label.must.select.users"));
		errorNotify.setWidth("370px");
		errorNotify.setVisible(false);
		errorNotify.setStyleName("fancyfileupload-failed");
		mainPanel.add(errorNotify);

		// Adds version comment
		versionHTMLBR = new HTML("<br/>");
		mainPanel.add(versionHTMLBR);
		versionComment = new TextArea();
		versionComment.setWidth("375px");
		versionComment.setHeight("50px");
		versionComment.setName("comment");
		versionComment.setStyleName("okm-TextArea");
		versionCommentText = new HTML(Main.i18n("fileupload.label.comment"));

		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		// Table for solve some visualization problems
		versionCommentScrollPanel = new ScrollPanel(versionComment);
		versionCommentScrollPanel.setAlwaysShowScrollBars(false);
		versionCommentScrollPanel.setSize("100%", "100%");
		vVersionCommentPanel.add(versionCommentText);
		vVersionCommentPanel.add(versionCommentScrollPanel);
		mainPanel.add(vVersionCommentPanel);

		// Increase version
		increaseMajorVersion.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				increaseMinorVersion.setValue(false);
			}
		});
		increaseMinorVersion.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				increaseMajorVersion.setValue(false);
			}
		});
		hIncreaseVersionPanel = new HorizontalPanel();
		hIncreaseVersionPanel.add(increaseMajorVersion);
		hIncreaseVersionPanel.add(Util.hSpace("5px"));
		hIncreaseVersionPanel.add(increaseMinorVersion);
		mainPanel.add(hIncreaseVersionPanel);

		// Ads unzip file
		importZip = new CheckBox(Main.i18n("fileupload.label.importZip"));
		importZip.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (importZip.getValue()) {
					notifyToUser.setValue(false);
					vNotifyPanel.setVisible(false);
				} else {
				}
			}
		});

		importZip.setName("importZip");
		hUnzipPanel = new HorizontalPanel();
		hUnzipPanel.add(importZip);
		mainPanel.add(new HTML("<br/>"));
		mainPanel.add(hUnzipPanel);

		// Adds the notify checkbox
		mails = new TextBox();
		mails.setName("mails");
		mails.setVisible(false);
		users = new TextBox();
		users.setName("users");
		users.setVisible(false);
		roles = new TextBox();
		roles.setName("roles");
		roles.setVisible(false);
		notifyToUser = new CheckBox(Main.i18n("fileupload.label.users.notify"));
		notifyToUser.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (notifyToUser.getValue()) {
					vNotifyPanel.setVisible(true);
					importZip.setValue(false);

					// TODO:Solves minor bug with IE
					if (Util.getUserAgent().startsWith("ie")) {
						notifyPanel.tabPanel.setWidth("374px");
						notifyPanel.tabPanel.setWidth("375px");
						notifyPanel.correcIEBug();
					}
				} else {
					errorNotify.setVisible(false);
					vNotifyPanel.setVisible(false);
				}
			}
		});

		notifyToUser.setName("notify");
		hNotifyPanel = new HorizontalPanel();
		hNotifyPanel.add(notifyToUser);
		mainPanel.add(hNotifyPanel);
		mainPanel.add(new HTML("<br/>"));

		// The notify user tables
		message = new TextArea();
		commentTXT = new HTML(Main.i18n("fileupload.label.notify.comment"));
		message.setName("message");
		message.setSize("375px", "60px");
		message.setStyleName("okm-TextArea");

		vNotifyPanel = new VerticalPanel();
		vNotifyPanel.add(commentTXT);

		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		messageScroll = new ScrollPanel(message);
		messageScroll.setAlwaysShowScrollBars(false);

		vNotifyPanel.add(messageScroll);
		vNotifyPanel.add(new HTML("<br/>"));
		vNotifyPanel.add(notifyPanel);
		vNotifyPanel.add(new HTML("<br/>"));

		mainPanel.add(mails);
		mainPanel.add(users);
		mainPanel.add(roles);
		mainPanel.add(vNotifyPanel);

		// Set align to panels
		mainPanel.setCellHorizontalAlignment(hNotifyPanel, HorizontalPanel.ALIGN_LEFT);
		mainPanel.setCellHorizontalAlignment(hUnzipPanel, HorizontalPanel.ALIGN_LEFT);
		mainPanel.setCellHorizontalAlignment(vNotifyPanel, HorizontalPanel.ALIGN_CENTER);
		mainPanel.setCellHorizontalAlignment(vVersionCommentPanel, HorizontalPanel.ALIGN_CENTER);

		// Initialize users
		getAllUsers();

		// Initialize the widget.
		initWidget(mainPanel);
	}

	/**
	 * Reset he upload
	 */
	public void reset(boolean enableImport, boolean enableNotifyButton) {
		uploadItem.reset(enableImport, enableNotifyButton);
	}

	/**
	 * Init he upload
	 */
	public void init() {
		uploadItem.init();
	}

	/**
	 * Get the text from the widget - which in reality will be retrieving any value set in the Label element of the
	 * display widget.
	 */
	@Override
	public String getText() {
		return uploadItem.status.getText();
	}

	/**
	 * Cannot set the text of a File Upload Widget, so raise an exception.
	 */
	@Override
	public void setText(String text) {
		throw new RuntimeException("Cannot set text of a FileUpload Widget");
	}

	/**
	 * Retrieve the status of the upload widget.
	 *
	 * @return Status of upload widget.
	 */
	public int getUploadState() {
		return widgetState;
	}

	/**
	 * isWizard
	 */
	public boolean isWizard() {
		return wizard;
	}

	/**
	 * fire a change event
	 */
	private void fireChange() {
		NativeEvent nativeEvent = Document.get().createChangeEvent();
		ChangeEvent.fireNativeEvent(nativeEvent, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasChangeHandlers#addChangeHandler(com
	 * .google.gwt.event.dom.client.ChangeHandler)
	 */
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	/**
	 * getAction
	 */
	public int getAction() {
		return action;
	}

	/**
	 * setAction
	 */
	public void setAction(int action) {
		this.action = action;
		switch (action) {
			case UIFileUploadConstants.ACTION_INSERT:
				versionComment.setVisible(false);
				versionCommentText.setVisible(false);
				versionHTMLBR.setVisible(false);
				hIncreaseVersionPanel.setVisible(false);
				break;

			case UIFileUploadConstants.ACTION_UPDATE:
				versionComment.setVisible(true);
				versionCommentText.setVisible(true);
				versionHTMLBR.setVisible(true);
				hIncreaseVersionPanel.setVisible(true);
				break;
		}
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		notifyToUser.setText(Main.i18n("fileupload.label.users.notify"));
		increaseMajorVersion.setHTML(Main.i18n("fileupload.increment.major.version"));
		increaseMinorVersion.setHTML(Main.i18n("fileupload.increment.minor.version"));
		importZip.setText(Main.i18n("fileupload.label.importZip"));
		versionCommentText.setHTML(Main.i18n("fileupload.label.comment"));
		commentTXT.setHTML(Main.i18n("fileupload.label.notify.comment"));
		diclousureFilesPanel.getHeaderTextAccessor().setText(Main.i18n("fileupload.label.pending.queue"));
		notifyPanel.langRefresh();
	}

	/**
	 * Call back get file upload status
	 */
	final AsyncCallback<GWTFileUploadingStatus> callbackGetFileUploadStatus = new AsyncCallback<GWTFileUploadingStatus>() {
		@Override
		public void onSuccess(GWTFileUploadingStatus result) {
			fileUploadingStatus = result;

			if (fileUplodingStartedFlag) {
				if (result.isStarted()) {
					if (result.getContentLength() != 0 && result.getContentLength() == result.getBytesRead()) {
						result.setUploadFinish(true);
						uploadItem.setProcessing();
					}

					if (result.isUploadFinish()) {
						progressBar.setTextFormatter(finalFormater);
					}

					progressBar.setMaxProgress(fileUploadingStatus.getContentLength());
					progressBar.setProgress(fileUploadingStatus.getBytesRead());
					Main.get().mainPanel.topPanel.setPercentageUploading((int) (100 * progressBar.getPercent()));
				}

				if (!result.isUploadFinish()) {
					refreshStatus();
				}
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			// Main.get().showError("getFileUploadStatus", caught);
			// Silent error ( if bandwidth is full used by uploading treat could cause RPC error while updating, that's
			// more visible when uploading file is bigger ).
			refreshStatus();
		}
	};

	/**
	 *
	 */
	private void refreshStatus() {
		Timer refreshStatus = new Timer() {
			@Override
			public void run() {
				getFileUploadStatus();
			}
		};

		refreshStatus.schedule(REFRESH_STATUS_DELAY);
	}

	/**
	 * Resets the progress bar and all related values
	 */
	private void resetProgressBar() {
		fileUplodingStartedFlag = false;
		fileUploadingStatus = new GWTFileUploadingStatus();
		progressBar.setMinProgress(0);
		progressBar.setMaxProgress(0);
		progressBar.setProgress(0);
		progressBar.setTextFormatter(progressiveFormater);
	}

	/**
	 * Gets all users
	 */
	private void getAllUsers() {
		notifyPanel.getAll();
	}

	private void getFileUploadStatus() {
		generalService.getFileUploadStatus(callbackGetFileUploadStatus);
	}

	/**
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		errorNotify.setVisible(false);
	}

	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		notifyPanel.enableAdvancedFilter();
	}

	/**
	 * enableNotifyExternalUsers
	 */
	public void enableNotifyExternalUsers() {
		notifyPanel.enableNotifyExternalUsers();
	}

	/**
	 * setIncrementalVersion
	 */
	public void setIncreaseVersion(int incrementVersion) {
		if (incrementVersion == 0) {
			mainPanel.remove(hIncreaseVersionPanel);
		} else if (incrementVersion == 1) {
			hIncreaseVersionPanel.remove(increaseMinorVersion);
		}
	}

	/**
	 * getFileName
	 */
	public String getFilename() {
		if (pendingFileToUpload.size() > 0) {
			return pendingFileToUpload.get(0).getUploadForm().getFileName();
		} else {
			return null;
		}
	}

	/**
	 * getUploadForm
	 */
	public FileUploadForm getUploadForm() {
		return uploadForm;
	}

	/**
	 * @param filesToUpload
	 */
	public void enqueueFileToUpload(Collection<FileToUpload> filesToUpload) {
		this.filesToUpload.addAll(filesToUpload);

		for (FileToUpload fileToUpload : filesToUpload) {
			if (fileToUpload.getUploadForm() != null) {
				addFileNameToPendingPanel(fileToUpload.getUploadForm().getFileName());
			} else {
				addFileNameToPendingPanel(fileToUpload.getFileUpload().getFilename());
			}
		}

		if (actualFileToUpload == null) {
			uploadNewPendingFile();
		} else {
			setAction(actualFileToUpload.getAction());

			if (widgetState == UPLOADING_STATE) {
				uploadItem.hFileUpload.setVisible(false);
				uploadItem.pendingPanel.setVisible(true);
			}

			Main.get().mainPanel.topPanel.setPendingFilesToUpload(calculatePendingFilesToUpload());
			diclousureFilesPanel.setVisible(filesToUpload.size() > 0);
		}
	}

	/**
	 * addFileNameToPendingPanel
	 */
	private void addFileNameToPendingPanel(String fileName) {
		// Get name from linux or windows path
		if (fileName.contains("/")) {
			fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
		} else if (fileName.contains("\\")) {
			fileName = fileName.substring(fileName.lastIndexOf('\\') + 1);
		}
		if (fileName.length() > MAX_FILENAME_LENGHT) {
			pendingFilePanel.add(new HTML(fileName.substring(0, MAX_FILENAME_LENGHT) + " ..."));
		} else {
			pendingFilePanel.add(new HTML(fileName));
		}
	}

	/**
	 * calculatePendingFilesToUpload
	 */
	private int calculatePendingFilesToUpload() {
		if (actualFileToUpload != null) {
			return 1 + filesToUpload.size();
		} else {
			return filesToUpload.size();
		}
	}

	/**
	 * addPendingFileToUpload
	 */
	public void addPendingFileToUpload(FileToUpload pendingFileToUpload) {
		this.pendingFileToUpload.clear(); // There's only one element in list ( because we don't want to not
		// implementing clone in FileToUpload )
		this.pendingFileToUpload.add(pendingFileToUpload);
		// pendingFileToUpload.getFileUpload().getElement().setPropertyBoolean("multiple", true); // Test to select
		// several documents at same time
		pendingFileToUpload.setUploadForm(new FileUploadForm(pendingFileToUpload.getFileUpload(),
				FileToUpload.DEFAULT_SIZE));
		uploadItem.hFileUpload.add(pendingFileToUpload.getUploadForm());
		setAction(pendingFileToUpload.getAction()); // Action show / hides some panels

		if (actualFileToUpload == null && filesToUpload.size() == 0) {
			Main.get().fileUpload.showPopup(pendingFileToUpload.isEnableAddButton(),
					pendingFileToUpload.isEnableImport(), true);
			Main.get().fileUpload.setModal(true);
		} else {
			uploadItem.resetWhileUploading(pendingFileToUpload.isEnableAddButton(),
					pendingFileToUpload.isEnableImport());
			Main.get().fileUpload.center();
			Main.get().fileUpload.setModal(true);

			// TODO:Solves minor bug with IE
			if (Util.getUserAgent().startsWith("ie")) {
				notifyPanel.tabPanel.setWidth("374px");
				notifyPanel.tabPanel.setWidth("375px");
				notifyPanel.correcIEBug();
			}
		}
	}

	/**
	 * isPendingFileToUpload
	 */
	public boolean isPendingFileToUpload() {
		return pendingFileToUpload.size() > 0;
	}

	/**
	 * isPendingOnFileUploadQueue
	 */
	public boolean isPendingOnFileUploadQueue() {
		return filesToUpload.size() > 0;
	}

	/**
	 * isActualFileUplading
	 */
	public boolean isActualFileUploading() {
		return actualFileToUpload != null;
	}

	/**
	 * uploadPendingFile
	 */
	public void uploadNewPendingFile() {
		// Execute pending workflows
		if (actualFileToUpload != null && actualFileToUpload.getWorkflow() != null
				&& actualFileToUpload.isLastToBeUploaded()) {
			uploadedWorkflowFiles.add(actualFileToUpload.clone());
			executeWorkflow(actualFileToUpload.getWorkflowTaskId());
		}

		if (!filesToUpload.isEmpty()) {
			actualFileToUpload = filesToUpload.remove(0);
			pendingFilePanel.remove(0);

			// Here always with default size
			if (actualFileToUpload.getUploadForm() == null) {
				actualFileToUpload.setUploadForm(new FileUploadForm(actualFileToUpload.getFileUpload(),
						FileToUpload.DEFAULT_SIZE));
			}

			final FileUploadForm uploadForm = actualFileToUpload.getUploadForm();
			uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
			uploadItem.hFileUpload.add(uploadForm);
			uploadForm.setVisible(false);
			actualFileToUpload.getUploadForm().setPath(actualFileToUpload.getPath());
			actualFileToUpload.getUploadForm().setAction(String.valueOf(actualFileToUpload.getAction()));
			actualFileToUpload.getUploadForm().setRename(actualFileToUpload.getDesiredDocumentName());

			setAction(actualFileToUpload.getAction());
			addSubmitCompleteHandler(uploadForm);

			// Case fileupload is workflow notify to users must be disabled ( popup hidden then (modal==false) )
			if (!Main.get().fileUpload.isModal() && actualFileToUpload.getWorkflow() != null) {
				Main.get().fileUpload.showPopup(actualFileToUpload.isEnableAddButton(), actualFileToUpload.isEnableImport(), Main.get().workspaceUserProperties.getWorkspace().isUploadNotifyUsers());
				reset(actualFileToUpload.isEnableImport(), Main.get().workspaceUserProperties.getWorkspace().isUploadNotifyUsers()); // force reset
			}

			fileName = uploadForm.getFileName();
			uploadItem.setPending();
			Main.get().mainPanel.topPanel.setPercentageUploading(0);

			p = new Timer() {
				@Override
				public void run() {
					uploadItem.setLoading();
					uploadForm.submit();

				}
			};

			p.schedule(PENDING_UPDATE_DELAY);
		} else {
			if (actualFileToUpload != null && actualFileToUpload.getWorkflow() != null) {
				Main.get().fileUpload.executeClose();
			}
			actualFileToUpload = null;
		}

		Main.get().mainPanel.topPanel.setPendingFilesToUpload(calculatePendingFilesToUpload());
		diclousureFilesPanel.setVisible(filesToUpload.size() > 0);
	}

	/**
	 * executeWorkflow
	 */
	private void executeWorkflow(double taskId) {
		List<FileToUpload> uploadedFiles = new ArrayList<FileToUpload>();

		for (FileToUpload uploaded : uploadedWorkflowFiles) {
			if (uploaded.getWorkflowTaskId() == taskId) {
				uploadedFiles.add(uploaded);
			}
		}

		actualFileToUpload.getWorkflow().setTaskInstanceValues(actualFileToUpload.getWorkflowTaskId(),
				actualFileToUpload.getWorkflowTransition(), uploadedFiles);
	}

	/**
	 * cancel
	 */
	public void close() {
		// Clean empty upload forms
		int i = 0;

		while (i < uploadItem.hFileUpload.getWidgetCount()) {
			FileUploadForm uploadForm = (FileUploadForm) uploadItem.hFileUpload.getWidget(i);
			if (uploadForm.isVisible()) {
				uploadItem.hFileUpload.remove(uploadForm);
			} else {
				i++;
			}
		}
	}

	/**
	 * resetOnlyShowUploading
	 */
	public void resetOnlyShowUploading() {
		hNotifyPanel.setVisible(false);
		hUnzipPanel.setVisible(false);
		hIncreaseVersionPanel.setVisible(false);
	}

	/**
	 * addSubmitCompleteHandler
	 */
	private FileUploadForm addSubmitCompleteHandler(final FileUploadForm uploadForm) {
		// Add an event handler to the form.
		uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// Fire an onChange Event
				fireChange();

				// Cancel all timers to be absolutely sure nothing is going on.
				p.cancel();

				// Ensure that the form encoding is set correctly.
				uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);

				// Check the result to see if an OK message is returned from the server.
				// Return params could be <pre> or <pre style=""> with some IE and chrome
				GWTFileUploadResponse fuResponse = new GWTFileUploadResponse(event.getResults());

				if (fuResponse.getError().equals("")) {
					String docPath = fuResponse.getPath();

					// Normal case document uploaded is not a workflow
					if (actualFileToUpload.getWorkflow() == null) {
						wizard = false;
						boolean fuResponseWizard = false;
						
						// Case is not importing a zip and wizard is enabled
						if (fuResponse.isHasAutomation()) {
							// If is importing file as zip wizard should be disabled
							if (!uploadForm.isImportZip()
									&& (fuResponse.isDigitalSignature() || fuResponse.isShowWizardCategories()
									|| fuResponse.isShowWizardKeywords()
									|| fuResponse.getGroupsList().size() > 0 || fuResponse.getWorkflowList()
									.size() > 0)) {
								fuResponseWizard = true;
								wizard = true;
							} 
						} else {
							if (!uploadForm.isImportZip()
									&& action == UIFileUploadConstants.ACTION_INSERT
									&& (Main.get().workspaceUserProperties.getWorkspace().isWizardPropertyGroups()
									|| Main.get().workspaceUserProperties.getWorkspace().isWizardWorkflows()
									|| Main.get().workspaceUserProperties.getWorkspace().isWizardCategories() || Main
									.get().workspaceUserProperties.getWorkspace().isWizardKeywords())) {
								wizard = true;
							}
						}
						
						if (wizard && docPath != null) {
							if (!fuResponseWizard) {
								Main.get().wizardPopup.start(docPath, false);
							} else {
								Main.get().wizardPopup.start(docPath, fuResponse, false);
							}
						}

						// By default selected row after uploading is uploaded file
						if (docPath != null && !docPath.equals("")) {
							Main.get().mainPanel.desktop.browser.fileBrowser.mantainSelectedRowByPath(docPath);
						}

						uploadItem.setLoaded();
					} else {
						actualFileToUpload.setDocumentPath(docPath);
						repositoryService.getUUIDByPath(docPath, new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								actualFileToUpload.setDocumentUUID(result);
								uploadItem.setLoaded();
							}

							@Override
							public void onFailure(Throwable caught) {
								Main.get().showError("getUUIDByPath", caught);
							}
						});
					}
				} else {
					uploadItem.setFailed(fuResponse.getError());
				}

				// Remove upload form
				uploadItem.hFileUpload.remove(uploadForm);
			}
		});

		return uploadForm;
	}

	/**
	 * This method will be invoked from client applets.
	 *
	 * @param docPath Related document path.
	 * @param result json encoded response.
	 */
	public void jsWizard(String docPath, String result) {
		Log.debug("jsWizard(" + docPath + ", " + result + ")");

		// Check the result to see if an OK message is returned from the server.
		// Return params could be <pre> or <pre style=""> with some IE and chrome
		GWTFileUploadResponse fuResponse = new GWTFileUploadResponse(result);

		if (fuResponse.getError().equals("")) {
			// TODO: Posible problem if actualFileToUpload != null ( other file is uploading and the queue is not repected

			// Case is not importing a zip and wizard is enabled
			if (fuResponse.isHasAutomation()) {
				// If is importing file as zip wizard should be disabled
				if ((fuResponse.isDigitalSignature() || fuResponse.isShowWizardCategories()
						|| fuResponse.isShowWizardKeywords() || fuResponse.getGroupsList().size() > 0 || fuResponse
						.getWorkflowList().size() > 0)) {
					Main.get().wizardPopup.start(docPath, fuResponse, true);
				}
			} else {
				if (Main.get().workspaceUserProperties.getWorkspace().isWizardPropertyGroups()
						|| Main.get().workspaceUserProperties.getWorkspace().isWizardWorkflows()
						|| Main.get().workspaceUserProperties.getWorkspace().isWizardCategories()
						|| Main.get().workspaceUserProperties.getWorkspace().isWizardKeywords()) {

					wizard = true;
				}

				if (wizard && docPath != null) {
					Main.get().wizardPopup.start(docPath, true);
				} else if (!wizard) {
					if (docPath != null && !docPath.equals("")) {
						Main.get().mainPanel.desktop.browser.fileBrowser.mantainSelectedRowByPath(docPath);
					}
					Main.get().mainPanel.desktop.browser.fileBrowser.refresh(Main.get().activeFolderTree.getActualPath());
				}
			}

		} else {
			Main.get().showError(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_General),
					new Throwable(fuResponse.getError()));
		}
	}

	/**
	 * setUploadNotifyUsers
	 */
	public void setUploadNotifyUsers(boolean visible) {
		if (!visible) {
			mainPanel.remove(hNotifyPanel);
		}
	}

	/**
	 * initJavaScriptApi
	 */
	public native void initJavaScriptApi(FancyFileUpload ffu) /*-{
        $wnd.jsWizard = function (docPath, result) {
            ffu.@com.openkm.frontend.client.widget.upload.FancyFileUpload::jsWizard(Ljava/lang/String;Ljava/lang/String;)(docPath, result);
            return true;
        }
    }-*/;
}
