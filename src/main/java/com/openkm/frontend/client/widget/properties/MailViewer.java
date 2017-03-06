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
package com.openkm.frontend.client.widget.properties;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.foldertree.FolderSelectPopup;
import com.openkm.frontend.client.widget.properties.attachment.AttachmentMenuPopup;
import com.openkm.frontend.client.widget.properties.mail.MailMenuPopup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * MailViewer
 *
 * @author jllort
 *
 */
public class MailViewer extends Composite {
	private static final int DATA_TABLE_HEIGHT = 125;
	private static final int DATA_TABLE_MINIMUM_WIDTH = 300;
	private FlexTable table;
	private FlexTable dataTable;
	private HorizontalPanel contentPanel;
	private ScrollPanel scrollPanel;
	private MailPreview mailPreview;
	private FlowPanel attachmentsPanel;
	private FlowPanel toPanel;
	private FlowPanel ccPanel;
	private FlowPanel bccPanel;
	private FlowPanel extraPanel;
	private HTML sizeText;
	private HTML sizeValue;
	private HTML mimeTypeText;
	private HTML mimeTypeValue;
	private HTML sendTextText;
	private HTML sendValue;
	private HTML sendReceivedText;
	private HTML sendReceivedValue;
	private FlowPanel replyPanel;
	public AttachmentMenuPopup attamentMenuPopup;
	public MailMenuPopup optionsMenuPopup;
	private Map<Integer, GWTDocument> attachmentsList;
	private GWTMail mail;
	private GWTDocument selectedAttachment = null;
	private Button selectedButton = null;
	private Image options;
	private int rowFrom = 0;
	private int rowSubject = 0;
	private int rowTo = 0;
	private int rowCC = 0;
	private int rowBCC = 0;
	private int rowReply = 0;
	private int rowExtra = 0;
	private int rowAttachments = 0;
	private int width = 0;
	private int height = 0;

	/**
	 * MailViewer
	 */
	public MailViewer() {
		table = new FlexTable();
		dataTable = new FlexTable();
		contentPanel = new HorizontalPanel();
		attamentMenuPopup = new AttachmentMenuPopup();
		attamentMenuPopup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				unselectAttachment();
			}
		});
		optionsMenuPopup = new MailMenuPopup();
		attachmentsList = new HashMap<Integer, GWTDocument>();

		options = new Image(OKMBundleResources.INSTANCE.options());
		options.setTitle(Main.i18n("general.options"));
		options.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				optionsMenuPopup.showRelativeTo(options);
			}
		});
		options.setStyleName("okm-Hyperlink");

		table.setCellPadding(0);
		table.setCellSpacing(0);
		dataTable.setCellPadding(3);
		dataTable.setCellSpacing(2);
		dataTable.setWidth("100%");
		scrollPanel = new ScrollPanel(dataTable);

		int row = table.getRowCount();
		rowFrom = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.from") + "</b>");
		dataTable.setHTML(row, 1, "");
		dataTable.setWidget(row++, 2, options);
		rowSubject = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.subject") + "</b>");
		dataTable.setHTML(row++, 1, "");
		rowTo = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.to") + "</b>");
		dataTable.setHTML(row++, 1, "");
		rowCC = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.cc") + "</b>");
		dataTable.setHTML(row++, 1, "");
		rowBCC = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.bcc") + "</b>");
		dataTable.setHTML(row++, 1, "");
		rowReply = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.reply") + "</b>");
		dataTable.setHTML(row++, 1, "");
		rowExtra = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.extra") + "</b>");
		dataTable.setHTML(row++, 1, "");
		rowAttachments = row;
		dataTable.setHTML(row, 0, "<b>" + Main.i18n("mail.attachment") + "</b>");

		attachmentsPanel = new FlowPanel();
		dataTable.setWidget(rowAttachments, 1, attachmentsPanel);
		toPanel = new FlowPanel();
		dataTable.setWidget(rowTo, 1, toPanel);
		ccPanel = new FlowPanel();
		dataTable.setWidget(rowCC, 1, ccPanel);
		bccPanel = new FlowPanel();
		dataTable.setWidget(rowBCC, 1, bccPanel);
		replyPanel = new FlowPanel();
		dataTable.setWidget(rowReply, 1, replyPanel);
		extraPanel = new FlowPanel();
		extraPanel.setStyleName("okm-NoWrap");
		sizeText = new HTML("<b>" + Main.i18n("mail.size") + "</b>");
		sizeText.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		sizeValue = new HTML("");
		sizeValue.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		mimeTypeText = new HTML("<b>" + Main.i18n("mail.mimetype") + "</b>");
		mimeTypeText.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		mimeTypeValue = new HTML("");
		mimeTypeValue.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		sendTextText = new HTML("<b>" + Main.i18n("mail.date.send") + "</b>");
		sendTextText.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		sendValue = new HTML("");
		sendValue.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		sendReceivedText = new HTML("<b>" + Main.i18n("mail.date.received"));
		sendReceivedText.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		sendReceivedValue = new HTML("");
		sendReceivedValue.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		HTML space = Util.hSpace("5px");
		space.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		HTML space1 = Util.hSpace("10px");
		space1.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		HTML space2 = Util.hSpace("5px");
		space2.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		HTML space3 = Util.hSpace("10px");
		space3.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		HTML space4 = Util.hSpace("5px");
		space4.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		HTML space5 = Util.hSpace("10px");
		space5.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		HTML space6 = Util.hSpace("5px");
		space6.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		extraPanel.add(sizeText);
		extraPanel.add(space);
		extraPanel.add(sizeValue);
		extraPanel.add(space1);
		extraPanel.add(mimeTypeText);
		extraPanel.add(space2);
		extraPanel.add(mimeTypeValue);
		extraPanel.add(space3);
		extraPanel.add(sendTextText);
		extraPanel.add(space4);
		extraPanel.add(sendValue);
		extraPanel.add(space5);
		extraPanel.add(sendReceivedText);
		extraPanel.add(space6);
		extraPanel.add(sendReceivedValue);
		dataTable.setWidget(rowExtra, 1, extraPanel);

		dataTable.getCellFormatter().setWidth(rowFrom, 1, "100%");
		dataTable.getCellFormatter().setWidth(rowFrom, 2, "20px");
		dataTable.getCellFormatter().setVerticalAlignment(rowFrom, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowSubject, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowTo, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowCC, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowBCC, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowReply, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowExtra, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowAttachments, 0, HasAlignment.ALIGN_TOP);
		dataTable.getCellFormatter().setVerticalAlignment(rowFrom, 2, HasAlignment.ALIGN_TOP);

		dataTable.getCellFormatter().setStyleName(rowFrom, 0, "okm-NoWrap");
		dataTable.getCellFormatter().setStyleName(rowSubject, 0, "okm-NoWrap");
		dataTable.getCellFormatter().setStyleName(rowTo, 0, "okm-NoWrap");
		dataTable.getCellFormatter().setStyleName(rowCC, 0, "okm-NoWrap");
		dataTable.getCellFormatter().setStyleName(rowBCC, 0, "okm-NoWrap");
		dataTable.getCellFormatter().setStyleName(rowReply, 0, "okm-NoWrap");
		dataTable.getCellFormatter().setStyleName(rowExtra, 0, "okm-NoWrap");
		dataTable.getCellFormatter().setStyleName(rowAttachments, 0, "okm-NoWrap");

		dataTable.getRowFormatter().setVisible(rowCC, false);
		dataTable.getRowFormatter().setVisible(rowBCC, false);
		dataTable.getRowFormatter().setVisible(rowReply, false);
		dataTable.getRowFormatter().setVisible(rowExtra, false);

		// Sets wordWrap for al rows except last
		for (int i = 0; i < (dataTable.getRowCount() - 1); i++) {
			setRowWordWarp(i, 1, true, dataTable);
		}

		mailPreview = new MailPreview();
		contentPanel.add(mailPreview);
		contentPanel.setWidth("100%");

		table.setWidget(0, 0, scrollPanel);
		table.setWidget(1, 0, contentPanel);

		table.getCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		table.getCellFormatter().setVerticalAlignment(0, 1, HasAlignment.ALIGN_TOP);
		table.getCellFormatter().setHorizontalAlignment(0, 1, HasAlignment.ALIGN_RIGHT);

		table.getFlexCellFormatter().setHeight(1, 0, "10px");
		table.getRowFormatter().setStyleName(1, "okm-Mail-White");
		table.setWidth("100%");


		dataTable.setStyleName("okm-DisableSelect");
		table.setStyleName("okm-Mail");
		attamentMenuPopup.setStyleName("okm-MenuPopup");
		optionsMenuPopup.setStyleName("okm-MenuPopup");

		initWidget(table);
	}

	@Override
	public void setPixelSize(int width, int height) {
		this.width = width;
		this.height = height;
		table.setPixelSize(width, height);
		dataTable.setWidth("" + (width > 15 ? (width - 15) : "0") + "px");
		scrollPanel.setPixelSize(width, DATA_TABLE_HEIGHT - 2);

		if (width - 200 > DATA_TABLE_MINIMUM_WIDTH) {
			attachmentsPanel.setWidth("" + (width - 200) + "px");
			toPanel.setWidth("95%");
			ccPanel.setWidth("95%");
			bccPanel.setWidth("95%");
			replyPanel.setWidth("95%");
			extraPanel.setWidth("95%");
		} else {
			attachmentsPanel.setWidth("" + DATA_TABLE_MINIMUM_WIDTH + "px");
			toPanel.setWidth("" + DATA_TABLE_MINIMUM_WIDTH + "px");
			ccPanel.setWidth("" + DATA_TABLE_MINIMUM_WIDTH + "px");
			bccPanel.setWidth("" + DATA_TABLE_MINIMUM_WIDTH + "px");
			replyPanel.setWidth("" + DATA_TABLE_MINIMUM_WIDTH + "px");
			extraPanel.setWidth("" + DATA_TABLE_MINIMUM_WIDTH + "px");
		}
		contentPanel.setWidth("" + width + "px");
		mailPreview.setPixelSize(width, ((height - DATA_TABLE_HEIGHT) > 0) ? (height - DATA_TABLE_HEIGHT) : 1);
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 * @param table The table to change word wrap
	 */
	private void setRowWordWarp(int row, int columns, boolean warp, FlexTable table) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, warp);
		}
	}

	/**
	 * get
	 *
	 * @return
	 */
	public GWTMail get() {
		return mail;
	}

	/**
	 * Sets the mail values
	 *
	 * @param mail The document object
	 */
	public void set(GWTMail mail) {
		this.mail = mail;

		Anchor hFrom = new Anchor();
		final String mailFrom = mail.getFrom().contains("<") ? mail.getFrom().substring(mail.getFrom().indexOf("<") + 1, mail.getFrom().indexOf(">")) : mail.getFrom();
		hFrom.setHTML(mail.getFrom().replace("<", "&lt;").replace(">", "&gt;"));
		hFrom.setTitle("mailto:" + mailFrom);
		hFrom.setStyleName("okm-Mail-Link");
		hFrom.addStyleName("okm-NoWrap");
		hFrom.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("mailto:" + mailFrom, "_blank", "");
			}
		});
		dataTable.setWidget(rowFrom, 1, hFrom);

		dataTable.setHTML(rowSubject, 1, mail.getSubject());
		mailPreview.showContent(mail);

		toPanel.clear();
		for (String mailTo : mail.getTo()) {
			toPanel.add(getMailWidget(mailTo, toPanel));
		}

		ccPanel.clear();
		for (String mailCC : mail.getCc()) {
			ccPanel.add(getMailWidget(mailCC, ccPanel));
		}

		bccPanel.clear();
		for (String mailBCC : mail.getBcc()) {
			bccPanel.add(getMailWidget(mailBCC, bccPanel));
		}

		replyPanel.clear();
		for (String mailReply : mail.getReply()) {
			replyPanel.add(getMailWidget(mailReply, replyPanel));
		}

		sizeValue.setHTML(Util.formatSize(mail.getSize()));
		mimeTypeValue.setHTML(mail.getMimeType());
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		sendValue.setHTML(mail.getSentDate() != null ? dtf.format(mail.getSentDate()) : "");
		sendReceivedValue.setHTML(mail.getReceivedDate() != null ? dtf.format(mail.getReceivedDate()) : "");

		// Enable select
		dataTable.getFlexCellFormatter().setStyleName(rowFrom, 1, "okm-EnableSelect");
		dataTable.getFlexCellFormatter().setStyleName(rowSubject, 1, "okm-EnableSelect");
		dataTable.getFlexCellFormatter().setStyleName(rowTo, 1, "okm-EnableSelect");
		dataTable.getFlexCellFormatter().setStyleName(rowCC, 1, "okm-EnableSelect");
		dataTable.getFlexCellFormatter().setStyleName(rowBCC, 1, "okm-EnableSelect");
		dataTable.getFlexCellFormatter().setStyleName(rowExtra, 1, "okm-EnableSelect");
		dataTable.getFlexCellFormatter().setStyleName(rowReply, 1, "okm-EnableSelect");

		attachmentsList = new HashMap<Integer, GWTDocument>();
		int count = 0;

		attachmentsPanel.clear();
		for (Iterator<GWTDocument> it = mail.getAttachments().iterator(); it.hasNext(); ) {
			final GWTDocument attach = it.next();
			attachmentsList.put(new Integer(count), attach);
			count++;
			attachmentsPanel.add(getAttachmentWidget(attach));
		}
	}

	/**
	 * downloadAttachment
	 */
	public void downloadAttachment() {
		if (selectedAttachment != null) {
			Util.downloadFileByUUID(selectedAttachment.getUuid(), "");
		}
	}

	/**
	 * copyAttachment
	 */
	public void copyAttachment() {
		if (selectedAttachment != null) {
			Main.get().activeFolderTree.folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_MAIL_ATTACH);
			Main.get().activeFolderTree.folderSelectPopup.setToCopy(selectedAttachment);
			Main.get().activeFolderTree.showDirectorySelectPopup();
		}
	}

	/**
	 * getAttachmentWidget
	 */
	private Button getAttachmentWidget(final GWTDocument attach) {
		String html = Util.mimeImageHTML(attach.getMimeType()) + "<span>" + attach.getName() + " (" + Util.formatSize(attach.getActualVersion().getSize()) + ")</span>";
		final Button button = new Button();
		button.setHTML(html);
		button.setTitle(attach.getName());
		button.setStyleName("okm-Button-Mail");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Util.downloadFileByUUID(attach.getUuid(), "");
			}
		});
		button.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				int buttonPressed = event.getNativeEvent().getButton();
				if (buttonPressed == NativeEvent.BUTTON_RIGHT) {
					unselectAttachment();
					selectedAttachment = attach;
					selectedButton = button;
					button.addStyleName("okm-Button-Mail-selected");
					event.preventDefault();
					event.stopPropagation();
					int mouseX = event.getClientX();
					int mouseY = event.getClientY();
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.attamentMenuPopup.setPopupPosition(mouseX, mouseY);
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.attamentMenuPopup.show();
				}
			}
		});
		return button;
	}

	/**
	 * getMailWidget
	 */
	private Button getMailWidget(final String mail, FlowPanel flowPanel) {
		final Button button = new Button();
		final String mailTo = mail.contains("<") ? mail.substring(mail.indexOf("<") + 1, mail.indexOf(">")).trim() : mail.trim();
		String mailHTML = mail.trim().replace("<", "&lt;").replace(">", "&gt;");
		button.setHTML(mailHTML);
		button.setTitle(mailHTML);
		button.setStyleName("okm-Button-Mail");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("mailto:" + mailTo, "_blank", "");
			}
		});
		return button;
	}

	/**
	 * unselectAttachment
	 */
	public void unselectAttachment() {
		if (selectedButton != null) {
			selectedButton.removeStyleName("okm-Button-Mail-selected");
		}
		selectedAttachment = null;
	}

	/**
	 * showCC
	 */
	public void showCC(boolean visible) {
		dataTable.getRowFormatter().setVisible(rowCC, visible);
		resizeHeaderPanel();
	}

	/**
	 * showBCC
	 */
	public void showBCC(boolean visible) {
		dataTable.getRowFormatter().setVisible(rowBCC, visible);
		resizeHeaderPanel();
	}

	/**
	 * showReply
	 */
	public void showReply(boolean visible) {
		dataTable.getRowFormatter().setVisible(rowReply, visible);
		resizeHeaderPanel();
	}

	/**
	 * showExtra
	 */
	public void showExtra(boolean visible) {
		dataTable.getRowFormatter().setVisible(rowExtra, visible);
		resizeHeaderPanel();
	}

	/**
	 * showAll
	 */
	public void showHideAll(boolean visible) {
		dataTable.getRowFormatter().setVisible(rowCC, visible);
		dataTable.getRowFormatter().setVisible(rowBCC, visible);
		dataTable.getRowFormatter().setVisible(rowReply, visible);
		dataTable.getRowFormatter().setVisible(rowExtra, visible);
		resizeHeaderPanel();
	}

	/**
	 * resizeHeaderPanel
	 */
	private void resizeHeaderPanel() {
		int extraHeight = 0;
		if (dataTable.getRowFormatter().isVisible(rowCC)) {
			extraHeight += 20;
		}
		if (dataTable.getRowFormatter().isVisible(rowBCC)) {
			extraHeight += 20;
		}
		if (dataTable.getRowFormatter().isVisible(rowReply)) {
			extraHeight += 20;
		}
		if (dataTable.getRowFormatter().isVisible(rowReply)) {
			extraHeight += 20;
		}
		scrollPanel.setPixelSize(width, (DATA_TABLE_HEIGHT - 2) + extraHeight);
		mailPreview.setPixelSize(width - 2, ((height - DATA_TABLE_HEIGHT - extraHeight) > 0) ? (height - DATA_TABLE_HEIGHT - extraHeight) : 1);
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		dataTable.setHTML(rowFrom, 0, "<b>" + Main.i18n("mail.from") + "</b>");
		dataTable.setHTML(rowSubject, 0, "<b>" + Main.i18n("mail.subject") + "</b>");
		dataTable.setHTML(rowTo, 0, "<b>" + Main.i18n("mail.to") + "</b>");
		dataTable.setHTML(rowCC, 0, "<b>" + Main.i18n("mail.cc") + "</b>");
		dataTable.setHTML(rowBCC, 0, "<b>" + Main.i18n("mail.bcc") + "</b>");
		dataTable.setHTML(rowReply, 0, "<b>" + Main.i18n("mail.reply") + "</b>");
		dataTable.setHTML(rowExtra, 0, "<b>" + Main.i18n("mail.extra") + "</b>");
		dataTable.setHTML(rowAttachments, 0, "<b>" + Main.i18n("mail.attachment") + "</b>");
		sizeText.setHTML("<b>" + Main.i18n("mail.size") + "</b>");
		mimeTypeText.setHTML("<b>" + Main.i18n("mail.mimetype") + "</b>");
		sendTextText.setHTML("<b>" + Main.i18n("mail.date.send") + "</b>");
		sendReceivedText.setHTML("<b>" + Main.i18n("mail.date.received") + "</b>");
		options.setTitle(Main.i18n("general.options"));
		attamentMenuPopup.langRefresh();
	}
}
