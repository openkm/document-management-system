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
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.constants.ui.UIDialogConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.util.Util;

/**
 * HTMLEditorPopup
 *
 * @author jllort
 *
 */
public class HTMLEditorPopup extends DialogBox {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);

	// Popup size
	public static final int DEFAULT_WIDTH = 1020;
	public static final int DEFAULT_HEIGHT = 720;
	public static final int TOOLBAR_SIZE = 25;

	// Tinymce extra size
	private int BOTTOM_GREY_HEIGHT = 15;

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HTML topSpace = Util.hSpace("5px");
	private HTML bottomSpace = Util.hSpace("5px");
	private HTML html;
	private TextArea htmlEditor;
	private GWTDocument doc;
	private int width;
	private int height;
	private String htmlText = "";


	/**
	 * HTMLEditorPopup
	 */
	public HTMLEditorPopup() {
		super(false, false); // Modal = true indicates popup is centered

		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;

		// Calculate size
		if (GeneralComunicator.get().mainPanel.getOffsetHeight() < height + UIDialogConstants.MARGIN) {
			height = GeneralComunicator.get().mainPanel.getOffsetHeight() - UIDialogConstants.MARGIN;
		}

		if (GeneralComunicator.get().mainPanel.getOffsetWidth() < width + UIDialogConstants.MARGIN) {
			width = GeneralComunicator.get().mainPanel.getOffsetWidth() - UIDialogConstants.MARGIN;
		}

		setText(GeneralComunicator.i18nExtension("htmleditor.title"));

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();

		topSpace = Util.vSpace("5px");
		bottomSpace = Util.vSpace("5px");
		html = new HTML("");
		html.setStyleName("okm-EnableSelect");

		HTML leftSpace = Util.hSpace("5px");
		HTML rightSpace = Util.hSpace("5px");
		hPanel.add(leftSpace);
		hPanel.add(html);
		hPanel.add(rightSpace);

		hPanel.setCellWidth(leftSpace, "5px");
		hPanel.setCellWidth(rightSpace, "5px");
		hPanel.setSize("100%", "100%");
		html.setSize("100%", "100%");

		vPanel.setWidth(String.valueOf(width) + "px");
		vPanel.setHeight(String.valueOf(height - UIDialogConstants.DIALOG_TOP) + "px");

		setWidget(vPanel);
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
		vPanel.clear();
		boolean checkout = true;

		if (doc.isCheckedOut() && doc.getLockInfo().getOwner().equals(GeneralComunicator.getWorkspace().getUser().getId())) {
			checkout = false;
		}

		HTMLEditor.get().status.setEditHTML();
		documentService.getHTMLContent(doc.getPath(), checkout, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				TextArea editorPanel = rebuildFormEditor();
				htmlEditor.setText(result);
				htmlEditor.setPixelSize(width + 4, height - BOTTOM_GREY_HEIGHT); // Some bottom fix correction ( grey bottom is not included in textarea size )
				htmlEditor.setSize("" + (width + 4) + "px", "" + (height - BOTTOM_GREY_HEIGHT) + "px"); // Some bottom fix correction ( grey bottom is not included in textarea size )
				vPanel.add(editorPanel);
				drawHTMLEditor();
				HTMLEditor.get().status.unsetEditHTML();
				center();
			}

			@Override
			public void onFailure(Throwable caught) {
				HTMLEditor.get().status.unsetEditHTML();
				GeneralComunicator.showError("getHTMLContent", caught);
			}
		});
	}

	/**
	 * rebuildFormEditor
	 */
	private TextArea rebuildFormEditor() {
		htmlEditor = new TextArea(); // textarea must be recreated each time
		htmlEditor.setName("elm1");
		htmlEditor.setStyleName("tinymce");
		DOM.setElementProperty(htmlEditor.getElement(), "id", "elm1");

		return htmlEditor;
	}

	/**
	 * getHTMLText
	 */
	public String getTexteAreaText() {
		return htmlText;
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
		this.htmlText = htmlText;
		hide();
		HTMLEditor.get().checkinPopup.center();
		HTMLEditor.get().checkinPopup.reset(doc.getPath());
	}

	/**
	 * drawHTMLEditor
	 */
	private void drawHTMLEditor() {
		String language = HTMLEditorUtils.getTinymceLang(GeneralComunicator.getLang());
		String theme = GeneralComunicator.getWorkspace().getTinymceTheme();
		String skin = GeneralComunicator.getWorkspace().getTinymceSkin();
		String skinVariant = GeneralComunicator.getWorkspace().getTinymceSkinVariant();
		String plugins = GeneralComunicator.getWorkspace().getTinymcePlugins();
		String themeButtons1 = GeneralComunicator.getWorkspace().getTinimceThemeButtons1();
		String themeButtons2 = GeneralComunicator.getWorkspace().getTinimceThemeButtons2();
		String themeButtons3 = GeneralComunicator.getWorkspace().getTinimceThemeButtons3();
		String themeButtons4 = GeneralComunicator.getWorkspace().getTinimceThemeButtons4();
		String checkinText = GeneralComunicator.i18n("general.menu.edit.checkin");
		String cancelCheckoutText = GeneralComunicator.i18n("general.menu.edit.cancel.checkout");
		String searchDocumentText = GeneralComunicator.i18n("general.menu.file.find.document");
		String searchFolderText = GeneralComunicator.i18n("general.menu.file.find.folder");
		String searchImageText = GeneralComunicator.i18n("general.menu.file.find.image");

		drawHTMLEditor(language, theme, skin, skinVariant, plugins, themeButtons1, themeButtons2, themeButtons3,
				themeButtons4, checkinText, cancelCheckoutText, searchDocumentText, searchFolderText, searchImageText);
	}

	/**
	 * drawHTMLEditor
	 */
	public static native void drawHTMLEditor(String t_language, String t_theme, String t_skin,
	                                         String t_skin_variant, String t_plugins, String t_buttons1, String t_buttons2, String t_buttons3,
	                                         String t_buttons4, String checkinText, String cancelCheckoutText, String searchDocumentText,
	                                         String searchFolderText, String searchImageText) /*-{
        new $wnd.drawHTMLEditor(t_language, t_theme, t_skin, t_skin_variant, t_plugins, t_buttons1, t_buttons2,
            t_buttons3, t_buttons4, checkinText, cancelCheckoutText, searchDocumentText, searchFolderText, searchImageText);
    }-*/;

	/**
	 * initJavaScriptApi
	 */
	public native void initJavaScriptApi(HTMLEditorPopup hTMLEditorPopup) /*-{
        $wnd.jsHideHTMLEditorPopup = function () {
            hTMLEditorPopup.@com.openkm.extension.frontend.client.widget.htmleditor.HTMLEditorPopup::hide()();
            return true;
        }

        $wnd.jsHTMLEditorCheckin = function (s) {
            hTMLEditorPopup.@com.openkm.extension.frontend.client.widget.htmleditor.HTMLEditorPopup::jsHTMLEditorCheckin(Ljava/lang/String;)(s);
            return true;
        }
    }-*/;
}