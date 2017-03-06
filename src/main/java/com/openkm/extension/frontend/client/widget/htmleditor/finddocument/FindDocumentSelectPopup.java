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

package com.openkm.extension.frontend.client.widget.htmleditor.finddocument;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.constants.service.RPCService;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.constants.ui.UIGeneralConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.NavigatorComunicator;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;
import com.openkm.frontend.client.service.OKMSearchService;
import com.openkm.frontend.client.service.OKMSearchServiceAsync;
import com.openkm.frontend.client.util.EventUtils;
import com.openkm.frontend.client.util.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * FindDocumentSelectPopup
 *
 * @author jllort
 */
public class FindDocumentSelectPopup extends DialogBox {
	private final OKMSearchServiceAsync searchService = (OKMSearchServiceAsync) GWT.create(OKMSearchService.class);

	private static final int FIND_DOCUMENT = 0;
	private static final int FIND_IMAGE = 1;

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollDocumentPanel;
	private Button cancelButton;
	private Button actionButton;
	public Status status;
	private TextBox keyword;
	private FlexTable documentTable;
	private Anchor appearance;
	private FlexTable imageTable;
	private HTML imageAlignText;
	private ListBox imageAlign;
	private HTML imageDimensionsText;
	private TextBox imageWidth;
	private TextBox imageHeight;
	private HTML imageVSpaceText;
	private TextBox imageVSpace;
	private HTML imageHSpaceText;
	private TextBox imageHSpace;
	private HTML imageBorderText;
	private TextBox imageBorder;
	private int selectedRow = -1;
	private int type = FIND_DOCUMENT;

	public FindDocumentSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		status = new Status();
		status.setStyleName("okm-StatusPopup");

		vPanel = new VerticalPanel();
		vPanel.setWidth("700px");
		vPanel.setHeight("350px");
		hPanel = new HorizontalPanel();

		scrollDocumentPanel = new ScrollPanel();
		scrollDocumentPanel.setStyleName("okm-Popup-text");

		cancelButton = new Button(GeneralComunicator.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(GeneralComunicator.i18nExtension("htmleditor.add.link"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addDocumentToHTMLEditor();
				hide();
			}
		});

		keyword = new TextBox();
		keyword.setWidth("692");
		keyword.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (keyword.getText().length() >= 3 && !EventUtils.isNavigationKey(event.getNativeKeyCode()) &&
						!EventUtils.isModifierKey(event.getNativeKeyCode())) {
					GWTQueryParams gwtParams = new GWTQueryParams();
					int actualView = NavigatorComunicator.getStackIndex();

					switch (actualView) {
						case UIDesktopConstants.NAVIGATOR_TAXONOMY:
							gwtParams.setPath(NavigatorComunicator.getRootFolder().getPath());
							break;

						case UIDesktopConstants.NAVIGATOR_CATEGORIES:
							gwtParams.setPath(NavigatorComunicator.getCategoriesRootFolder().getPath());
							break;

						case UIDesktopConstants.NAVIGATOR_THESAURUS:
							gwtParams.setPath(NavigatorComunicator.getThesaurusRootFolder().getPath());
							break;

						case UIDesktopConstants.NAVIGATOR_TEMPLATES:
							gwtParams.setPath(NavigatorComunicator.getTemplatesRootFolder().getPath());
							break;

						case UIDesktopConstants.NAVIGATOR_PERSONAL:
							gwtParams.setPath(NavigatorComunicator.getPersonalRootFolder().getPath());
							break;

						case UIDesktopConstants.NAVIGATOR_MAIL:
							gwtParams.setPath(NavigatorComunicator.getMailRootFolder().getPath());
							break;

						case UIDesktopConstants.NAVIGATOR_TRASH:
							gwtParams.setPath(NavigatorComunicator.getTrashRootFolder().getPath());
							break;
					}

					gwtParams.setMimeType("");
					gwtParams.setKeywords("");
					gwtParams.setMimeType("");
					gwtParams.setName(keyword.getText() + "*");
					gwtParams.setAuthor("");
					gwtParams.setMailFrom("");
					gwtParams.setMailTo("");
					gwtParams.setMailSubject("");
					gwtParams.setOperator(GWTQueryParams.OPERATOR_AND);
					gwtParams.setLastModifiedFrom(null);
					gwtParams.setLastModifiedTo(null);
					gwtParams.setDomain(GWTQueryParams.DOCUMENT);
					gwtParams.setProperties(new HashMap<String, GWTPropertyParams>());

					find(gwtParams);
				} else {
					removeAllRows();
				}
			}
		});

		documentTable = new FlexTable();
		documentTable.setWidth("100%");
		documentTable.setCellPadding(2);
		documentTable.setCellSpacing(0);
		documentTable.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(documentTable.getCellForEvent(event).getRowIndex());
				evaluateEnableAction();
			}
		});

		documentTable.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				addDocumentToHTMLEditor();
				hide();
			}
		});

		scrollDocumentPanel.add(documentTable);
		scrollDocumentPanel.setPixelSize(690, 300);

		// Image table
		appearance = new Anchor(GeneralComunicator.i18nExtension("htmleditor.appearance"));
		appearance.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				imageTable.setVisible(!imageTable.isVisible());
			}
		});
		HorizontalPanel hAppearancePanel = new HorizontalPanel();
		hAppearancePanel.add(appearance);
		hAppearancePanel.add(Util.hSpace("4px"));

		imageTable = new FlexTable();
		imageTable.setCellPadding(4);
		imageTable.setCellSpacing(0);
		imageAlignText = new HTML(GeneralComunicator.i18nExtension("htmleditor.image.alignment"));
		imageAlign = new ListBox();
		imageAlign.addItem("", "");
		imageAlign.addItem("top", "top");
		imageAlign.addItem("bottom", "bottom");
		imageAlign.addItem("middle", "middle");
		imageAlign.addItem("left", "left");
		imageAlign.addItem("right", "right");
		imageAlign.setStyleName("okm-Input");
		imageDimensionsText = new HTML(GeneralComunicator.i18nExtension("htmleditor.image.dimensions"));
		HorizontalPanel hDimensionsPanel = new HorizontalPanel();
		imageWidth = new TextBox();
		imageWidth.setWidth("50px");
		imageWidth.setMaxLength(5);
		imageWidth.setStyleName("okm-Input");
		imageHeight = new TextBox();
		imageHeight.setWidth("50px");
		imageHeight.setMaxLength(5);
		imageHeight.setStyleName("okm-Input");
		hDimensionsPanel.add(imageWidth);
		hDimensionsPanel.add(new HTML("&nbsp;x&nbsp;"));
		hDimensionsPanel.add(imageHeight);
		hDimensionsPanel.add(new HTML("&nbsp;px"));

		imageVSpaceText = new HTML(GeneralComunicator.i18nExtension("htmleditor.image.vspace"));
		imageVSpace = new TextBox();
		imageVSpace.setWidth("30px");
		imageVSpace.setMaxLength(3);
		imageVSpace.setStyleName("okm-Input");
		imageHSpaceText = new HTML(GeneralComunicator.i18nExtension("htmleditor.image.hspace"));
		imageHSpace = new TextBox();
		imageHSpace.setWidth("30px");
		imageHSpace.setMaxLength(3);
		imageHSpace.setStyleName("okm-Input");
		imageBorderText = new HTML(GeneralComunicator.i18nExtension("htmleditor.image.border"));
		imageBorder = new TextBox();
		imageBorder.setWidth("30px");
		imageBorder.setMaxLength(3);
		imageBorder.setStyleName("okm-Input");

		imageTable.setWidget(0, 0, imageAlignText);
		imageTable.setWidget(0, 1, imageAlign);
		imageTable.setWidget(0, 2, imageDimensionsText);
		imageTable.setWidget(0, 3, hDimensionsPanel);
		HorizontalPanel hExtra = new HorizontalPanel();
		hExtra.add(imageVSpaceText);
		hExtra.add(Util.hSpace("4px"));
		hExtra.add(imageVSpace);
		hExtra.add(Util.hSpace("4px"));
		hExtra.add(imageHSpaceText);
		hExtra.add(Util.hSpace("4px"));
		hExtra.add(imageHSpace);
		hExtra.add(Util.hSpace("4px"));
		hExtra.add(imageBorderText);
		hExtra.add(Util.hSpace("4px"));
		hExtra.add(imageBorder);
		hExtra.add(Util.hSpace("4px"));
		hExtra.setCellVerticalAlignment(imageVSpaceText, HasAlignment.ALIGN_MIDDLE);
		hExtra.setCellVerticalAlignment(imageVSpace, HasAlignment.ALIGN_MIDDLE);
		hExtra.setCellVerticalAlignment(imageHSpaceText, HasAlignment.ALIGN_MIDDLE);
		hExtra.setCellVerticalAlignment(imageHSpace, HasAlignment.ALIGN_MIDDLE);
		hExtra.setCellVerticalAlignment(imageBorderText, HasAlignment.ALIGN_MIDDLE);
		hExtra.setCellVerticalAlignment(imageBorder, HasAlignment.ALIGN_MIDDLE);

		imageTable.setWidget(1, 0, hExtra);
		imageTable.getFlexCellFormatter().setColSpan(1, 0, 4);

		vPanel.add(keyword);
		vPanel.add(scrollDocumentPanel);
		vPanel.add(hAppearancePanel);
		vPanel.add(imageTable);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50px");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(keyword, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(keyword, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellHorizontalAlignment(scrollDocumentPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hAppearancePanel, HasAlignment.ALIGN_RIGHT);
		vPanel.setCellHeight(keyword, "25px");
		vPanel.setCellHeight(scrollDocumentPanel, "300px");

		cancelButton.setStyleName("okm-NoButton");
		actionButton.setStyleName("okm-YesButton");
		documentTable.setStyleName("okm-NoWrap");
		documentTable.addStyleName("okm-Table-Row");
		keyword.setStyleName("okm-Input");

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18n("search.document.filter"));
		cancelButton.setText(GeneralComunicator.i18n("button.close"));
		imageAlignText.setHTML(GeneralComunicator.i18nExtension("htmleditor.image.alignment"));
		imageDimensionsText.setHTML(GeneralComunicator.i18nExtension("htmleditor.iamge.dimensions"));
		imageVSpaceText.setHTML(GeneralComunicator.i18nExtension("htmleditor.image.vspace"));
		imageHSpaceText.setHTML(GeneralComunicator.i18nExtension("htmleditor.image.hspace"));
		imageBorderText.setHTML(GeneralComunicator.i18nExtension("htmleditor.image.border"));
		appearance.setHTML(GeneralComunicator.i18nExtension("htmleditor.appearance") + "&nbsp;&nbsp;");
	}

	/**
	 * Shows the popup
	 */
	public void show() {
		initButtons();
		// int left = (Window.getClientWidth() - 700) / 2;
		// int top = (Window.getClientHeight() - 350) / 2;
		// setPopupPosition(left, top);

		switch (type) {
			case FIND_DOCUMENT:
				setText(GeneralComunicator.i18n("search.document.filter"));
				break;
			case FIND_IMAGE:
				setText(GeneralComunicator.i18n("search.image.filter"));
				break;
		}

		// Resets to initial tree value
		appearance.setVisible(false);
		imageTable.setVisible(false);
		imageAlign.setSelectedIndex(0);
		imageWidth.setText("");
		imageHeight.setText("");
		imageVSpace.setText("");
		imageHSpace.setText("");
		imageBorder.setText("");
		removeAllRows();
		keyword.setText("");
		evaluateEnableAction();
		super.show();
		keyword.setFocus(true);
	}

	/**
	 * Enables or disables move button
	 */
	public void enable(boolean enable) {
		actionButton.setEnabled(enable);

		if (enable && type == FIND_IMAGE) {
			appearance.setVisible(true);
		} else {
			appearance.setVisible(false);
		}

		imageTable.setVisible(false);
	}

	/**
	 * Enables all button
	 */
	private void initButtons() {
		cancelButton.setEnabled(true);
		actionButton.setEnabled(false);
	}

	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		selectedRow = -1;
		evaluateEnableAction();

		while (documentTable.getRowCount() > 0) {
			documentTable.removeRow(0);
		}
	}

	/**
	 * markSelectedRow
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
				documentTable.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
			} else {
				documentTable.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
			}
		}
	}

	/**
	 * evaluateEnableAction
	 */
	private void evaluateEnableAction() {
		enable(selectedRow >= 0);
	}

	/**
	 * Call Back find
	 */
	final AsyncCallback<GWTResultSet> callbackFind = new AsyncCallback<GWTResultSet>() {
		public void onSuccess(GWTResultSet result) {
			GWTResultSet resultSet = result;
			removeAllRows();

			for (Iterator<GWTQueryResult> it = resultSet.getResults().iterator(); it.hasNext(); ) {
				GWTQueryResult gwtQueryResult = it.next();

				if (gwtQueryResult.getDocument() != null) {
					GWTDocument doc = gwtQueryResult.getDocument();
					boolean add = false;

					switch (type) {
						case FIND_DOCUMENT:
							add = true;
							break;

						case FIND_IMAGE:
							add = Arrays.binarySearch(UIGeneralConstants.VALID_IMAGE_MIMETYPES, doc.getMimeType()) >= 0;
							break;
					}

					if (add) {
						int rows = documentTable.getRowCount();
						documentTable.setHTML(rows, 0, UtilComunicator.mimeImageHTML(doc.getMimeType()));
						documentTable.setHTML(rows, 1, doc.getPath());
						documentTable.setHTML(rows, 2, doc.getUuid());
						documentTable.getCellFormatter().setVisible(rows, 2, false);
						documentTable.getCellFormatter().setWidth(rows, 0, "30px");
						documentTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
					}
				}
			}

			status.unsetFlagChilds();
		}

		public void onFailure(Throwable caught) {
			status.unsetFlagChilds();
			GeneralComunicator.showError("Find", caught);
		}
	};

	/**
	 * Find
	 */
	private void find(GWTQueryParams params) {
		status.setFlagChilds();
		searchService.find(params, callbackFind);
	}

	/**
	 * showFindDocument
	 */
	public void showFindDocument() {
		type = FIND_DOCUMENT;
		actionButton.setText(GeneralComunicator.i18nExtension("htmleditor.add.link"));
		center();
	}

	/**
	 * getParameters
	 */
	public String getParameters() {
		String params = "";

		if (imageAlign.getSelectedIndex() > 0) {
			params += " align=\"" + imageAlign.getValue(imageAlign.getSelectedIndex()) + "\"";
		}

		if (!imageWidth.getText().equals("")) {
			params += " width=\"" + imageWidth.getText() + "\"";
		}

		if (!imageHeight.getText().equals("")) {
			params += " height=\"" + imageHeight.getText() + "\"";
		}

		if (!imageHSpace.getText().equals("")) {
			params += " hspace=\"" + imageHSpace.getText() + "\"";
		}

		if (!imageVSpace.getText().equals("")) {
			params += " vspace=\"" + imageVSpace.getText() + "\"";
		}

		if (!imageBorder.getText().equals("")) {
			params += " border=\"" + imageBorder.getText() + "\"";
		}

		return params;
	}

	/**
	 * addDocument
	 */
	private void addDocumentToHTMLEditor() {
		if (selectedRow >= 0) {
			String uuid = documentTable.getText(selectedRow, 2);
			String name = UtilComunicator.getName(documentTable.getText(selectedRow, 1));

			switch (type) {
				case FIND_DOCUMENT:
					addDocumentHTMLEditor(uuid, name);
					break;

				case FIND_IMAGE:
					String downloadServletName = RPCService.DownloadServlet.substring(RPCService.DownloadServlet.lastIndexOf("/") + 1);
					String url = "./" + downloadServletName;
					url += "?uuid=" + URL.encodeQueryString(uuid);
					addImageHTMLEditor(url, getParameters());
					break;
			}
		}
	}

	/**
	 * showFindImage
	 */
	public void showFindImage() {
		type = FIND_IMAGE;
		actionButton.setText(GeneralComunicator.i18nExtension("htmleditor.add.image"));
		center();
	}

	/**
	 * addDocumentHTMLEditor
	 */
	public static native void addDocumentHTMLEditor(String uuid, String name) /*-{
        new $wnd.addDocumentHTMLEditor(uuid, name);
    }-*/;

	/**
	 * addImageHTMLEditor
	 */
	public static native void addImageHTMLEditor(String src, String params) /*-{
        new $wnd.addImageHTMLEditor(src, params);
    }-*/;

	/**
	 * initJavaScriptApi
	 */
	public native void initJavaScriptApi(FindDocumentSelectPopup findDocumentSelectPopup) /*-{
        $wnd.jsSearchDocumentHTMLEditorPopup = function () {
            findDocumentSelectPopup.@com.openkm.extension.frontend.client.widget.htmleditor.finddocument.FindDocumentSelectPopup::showFindDocument()();
            return true;
        }
        $wnd.jsSearchImageHTMLEditorPopup = function () {
            findDocumentSelectPopup.@com.openkm.extension.frontend.client.widget.htmleditor.finddocument.FindDocumentSelectPopup::showFindImage()();
            return true;
        }
    }-*/;
}