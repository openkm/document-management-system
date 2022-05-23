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

package com.openkm.extension.frontend.client.widget.htmleditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.constants.ui.UIDialogConstants;
import com.openkm.frontend.client.extension.comunicator.FileBrowserCommunicator;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.widget.base.ExtendedCaption;
import com.openkm.frontend.client.widget.base.handler.CaptionHandler;

/**
 * HTMLEditorPopup
 *
 * @author jllort
 */
public class HTMLEditorPopup extends DialogBox implements CaptionHandler {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);

	// Popup size
	public static final int DEFAULT_WIDTH = 900;
	public static final int TOOLBAR_SIZE = 25;

	// Tinymce extra size
	private int BOTTOM_GREY_HEIGHT = 15;

	private Frame iframe;
	private GWTDocument doc;
	private int width;
	private int height;
	private String content;

	/**
	 * HTMLEditorPopup
	 */
	public HTMLEditorPopup() {
		super(false, false, new ExtendedCaption(true, true, false, true));
		ExtendedCaption caption = (ExtendedCaption) getCaption();
		caption.addCaptionHandler(this);

		width = DEFAULT_WIDTH;
		height = GeneralComunicator.get().mainPanel.getOffsetHeight() - 60;

		if (GeneralComunicator.get().mainPanel.getOffsetWidth() < width + UIDialogConstants.MARGIN) {
			width = GeneralComunicator.get().mainPanel.getOffsetWidth() - UIDialogConstants.MARGIN;
		}

		iframe = new Frame("about:blank");
		DOM.setElementProperty(iframe.getElement(), "id", "htmlEditor");
		DOM.setElementProperty(iframe.getElement(), "frameborder", "0");
		DOM.setElementProperty(iframe.getElement(), "marginwidth", "0");
		DOM.setElementProperty(iframe.getElement(), "marginheight", "0");
		DOM.setElementProperty(iframe.getElement(), "scrolling", "no");

		// Commented because on IE show clear if allowtransparency=true
		DOM.setElementProperty(iframe.getElement(), "allowtransparency", "false");

		iframe.setUrl(Main.CONTEXT + "/frontend/tinymce4.jsp");
		iframe.setStyleName("okm-Iframe");

		setWidget(iframe);
	}

	/**
	 * edit
	 */
	public void edit(GWTDocument doc) {
		this.doc = doc;
		edit();
	}

	/**
	 * edit
	 */
	private void edit() {
		boolean checkout = true;

		if (doc.isCheckedOut() && doc.getLockInfo().getOwner().equals(GeneralComunicator.getWorkspace().getUser().getId())) {
			checkout = false;
		}

		HTMLEditor.get().status.setEditHTML();
		documentService.getHTMLContent(doc.getPath(), checkout, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				continueEditing(doc.getName(), result);
				HTMLEditor.get().status.unsetEditHTML();
			}

			@Override
			public void onFailure(Throwable caught) {
				HTMLEditor.get().status.unsetEditHTML();
				GeneralComunicator.showError("getHTMLContent", caught);
			}
		});
	}

	/**
	 * continueEditing
	 */
	public void continueEditing(String docName, String content) {
		this.content = content;
		setText(docName);

		// Some bottom fix correction ( grey bottom is not included in textarea size )
		iframe.setPixelSize(width + 4, height - BOTTOM_GREY_HEIGHT);

		// Some bottom fix correction ( grey bottom is not included in textarea size )
		iframe.setSize((width + 4) + "px", (height - BOTTOM_GREY_HEIGHT) + "px");
		center();

		if (isShowing()) {
			iframe.setUrl(Main.CONTEXT + "/frontend/tinymce4.jsp");
		} else {
			show();
		}
	}

	public String getContent() {
		return content;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18nExtension("htmleditor.title"));
	}

	/**
	 * jsHTMLEditorCheckin
	 */
	public void jsHTMLEditorCheckin(String htmlText) {
		this.content = htmlText;
		hide();
		HTMLEditor.get().checkinPopup.center();
		HTMLEditor.get().checkinPopup.reset(doc.getPath());
	}

	/**
	 * jsCancelCheckout
	 */
	public void jsCancelCheckout() {
		HTMLEditor.get().status.setFlagCheckout();
		documentService.cancelCheckout(doc.getPath(), new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				FileBrowserCommunicator.refreshOnlyFileBrowser();
				GeneralComunicator.get().mainPanel.dashboard.userDashboard.getUserLastModifiedDocuments();
				GeneralComunicator.get().mainPanel.dashboard.userDashboard.getUserCheckedOutDocuments();
				hide();
				HTMLEditor.get().status.unsetFlagCheckout();
			}

			@Override
			public void onFailure(Throwable caught) {
				HTMLEditor.get().status.unsetFlagCheckout();
				GeneralComunicator.showError("cancelCheckout", caught);
			}
		});
	}

	/**
	 * jsDrawHTMLEditor
	 */
	public void jsDrawHTMLEditor() {
		drawHTMLEditor();
		HTMLEditor.get().status.unsetEditHTML();
	}

	/**
	 * drawHTMLEditor
	 */
	private void drawHTMLEditor() {
		String language = HTMLEditorUtils.getTinymceLang(GeneralComunicator.getLang());
		String theme = GeneralComunicator.getWorkspace().getTinymce4Theme();
		String plugins = GeneralComunicator.getWorkspace().getTinymce4Plugins();
		String toolbar1 = GeneralComunicator.getWorkspace().getTinymce4Toolbar1();
		String toolbar2 = GeneralComunicator.getWorkspace().getTinymce4Toolbar2();
		String checkinText = GeneralComunicator.i18n("general.menu.edit.checkin");
		String cancelCheckoutText = GeneralComunicator.i18n("general.menu.edit.cancel.checkout");
		String searchDocumentText = GeneralComunicator.i18n("general.menu.file.find.document");
		String searchFolderText = GeneralComunicator.i18n("general.menu.file.find.folder");
		String searchImageText = GeneralComunicator.i18n("general.menu.file.find.image");

		drawHTMLEditor(language, theme, plugins, toolbar1, toolbar2, checkinText, cancelCheckoutText, searchDocumentText, searchFolderText, searchImageText, content);
	}

	@Override
	public void onMinimize() {

	}

	@Override
	public void onMaximize() {
		// Nothing to do here
	}

	@Override
	public void onClose() {
		confirmCancelCheckout();
	}

	/**
	 * confirmCancelCheckout
	 */
	private static native void confirmCancelCheckout() /*-{
		new $wnd.confirmCancelCheckout();
	}-*/;


	/**
	 * drawHTMLEditor
	 */
	public static native void drawHTMLEditor(String t_language, String t_theme, String t_plugins, String t_toolbar1, String t_toolbar2, String checkinText, String cancelCheckoutText, String searchDocumentText, String searchFolderText, String searchImageText, String content) /*-{
		new $wnd.drawHTMLEditor(t_language, t_theme, t_plugins, t_toolbar1, t_toolbar2, checkinText, cancelCheckoutText, searchDocumentText, searchFolderText, searchImageText, content);
	}-*/;

	/**
	 * initJavaScriptApi
	 */
	public native void initJavaScriptApi(HTMLEditorPopup hTMLEditorPopup) /*-{
		$wnd.jsHTMLEditorCheckin = function (s) {
			hTMLEditorPopup.@com.openkm.extension.frontend.client.widget.htmleditor.HTMLEditorPopup::jsHTMLEditorCheckin(Ljava/lang/String;)(s);
			return true;
		}

		$wnd.jsDrawHTMLEditor = function () {
			hTMLEditorPopup.@com.openkm.extension.frontend.client.widget.htmleditor.HTMLEditorPopup::jsDrawHTMLEditor()();
			return true;
		}

		$wnd.jsCancelCheckout = function () {
			hTMLEditorPopup.@com.openkm.extension.frontend.client.widget.htmleditor.HTMLEditorPopup::jsCancelCheckout()();
			return true;
		}
	}-*/;
}
