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

package com.openkm.frontend.client.widget.sendmail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.widget.htmleditor.HTMLEditorUtils;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.constants.rpc.GWTMailConstants;
import com.openkm.frontend.client.constants.ui.UIDialogConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.service.OKMMailService;
import com.openkm.frontend.client.service.OKMMailServiceAsync;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.base.ExtendedCaption;
import com.openkm.frontend.client.widget.base.handler.CaptionHandler;
import com.openkm.frontend.client.widget.dashboard.ImageHover;
import com.openkm.frontend.client.widget.finddocument.FindDocumentSelectPopup;

import java.util.*;

/**
 * MailEditorPopup
 *
 * @author sochoa
 */
public class MailEditorPopup extends DialogBox implements CaptionHandler {
	private final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);
	private final OKMMailServiceAsync mailService = GWT.create(OKMMailService.class);

	// Popup size
	public static final int DEFAULT_WIDTH = 900;
	public static final int DEFAULT_HEIGHT = 720;

	// Tinymce extra size
	private int BOTTOM_GREY_HEIGHT = 15;

	private VerticalPanel vPanel;
	private HTML html;
	private Frame mailEditor;
	private GWTDocument doc;
	private int width;
	private int height;
	private String htmlText = "";
	private FlexTable mailTable;

	public RecipientsPopup recipientsPopup;
	private WarningMailPopup warningMailPopup;

	private HorizontalPanel toHPanel;
	private HorizontalPanel bccHPanel;
	private HorizontalPanel ccHPanel;
	private HorizontalPanel replyToHPanel;
	private HorizontalPanel subjectHPanel;
	private HorizontalPanel attachmentHPanel;
	private HorizontalPanel storageHPanel;

	private HTML toText;
	private HTML bccText;
	private HTML ccText;
	private HTML replyToText;
	private HTML subjectText;
	private HTML attachmentText;
	private HTML storageText;

	private TextBox subject;
	private Image toImage;
	private Image bccImage;
	private Image ccImage;
	private Image replyToImage;
	private Image attachmentImage;
	private ListBox storageList;

	public Map<String, Widget> toRecipientMap;
	public Map<String, Widget> bccRecipientMap;
	public Map<String, Widget> ccRecipientMap;
	public Map<String, Widget> replyToRecientMap;
	public Map<String, Widget> attachmentMap;

	public FlowPanel toFPanel;
	public ScrollPanel toScrollPanel;
	public FlowPanel bccFPanel;
	public ScrollPanel bccScrollPanel;
	public FlowPanel ccFPanel;
	public ScrollPanel ccScrollPanel;
	public FlowPanel replyToFPanel;
	public ScrollPanel replyToScrollPanel;
	public FlowPanel attachmentFPanel;
	private ScrollPanel attachmentScrollPanel;

	public List<String> toUsers;
	public List<String> toRoles;
	public List<String> toMails;
	public List<String> bccUsers;
	public List<String> bccRoles;
	public List<String> bccMails;
	public List<String> ccUsers;
	public List<String> ccRoles;
	public List<String> ccMails;
	public List<String> replyToUsers;
	public List<String> replyToRoles;
	public List<String> replyToMails;
	public List<String> uuidList;

	public boolean attachment = false;

	/**
	 * MailEditorPopup
	 */
	public MailEditorPopup() {
		super(false, false, new ExtendedCaption(true, true, false, true));
		ExtendedCaption caption = (ExtendedCaption) getCaption();
		caption.addCaptionHandler(this);

		setText(GeneralComunicator.i18n("maileditor.title"));

		recipientsPopup = new RecipientsPopup();
		recipientsPopup.setWidth("400px");
		recipientsPopup.setStyleName("okm-Popup");

		warningMailPopup = new WarningMailPopup();
		warningMailPopup.setWidth("300px");
		warningMailPopup.setHeight("50px");
		warningMailPopup.setStyleName("okm-Popup");
		warningMailPopup.addStyleName("okm-DisableSelect");

		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;

		vPanel = new VerticalPanel();
		setWidget(vPanel);
	}

	private void initComponents() {
		toRecipientMap = new HashMap<>();
		bccRecipientMap = new HashMap<>();
		ccRecipientMap = new HashMap<>();
		replyToRecientMap = new HashMap<>();
		attachmentMap = new HashMap<>();

		toHPanel = new HorizontalPanel();
		bccHPanel = new HorizontalPanel();
		ccHPanel = new HorizontalPanel();
		replyToHPanel = new HorizontalPanel();
		subjectHPanel = new HorizontalPanel();
		attachmentHPanel = new HorizontalPanel();
		storageHPanel = new HorizontalPanel();

		toText = new HTML(GeneralComunicator.i18n("mail.to"));
		bccText = new HTML(GeneralComunicator.i18n("mail.bcc"));
		ccText = new HTML(GeneralComunicator.i18n("mail.cc"));
		replyToText = new HTML(GeneralComunicator.i18n("mail.reply.to"));
		subjectText = new HTML(GeneralComunicator.i18n("mail.subject"));
		attachmentText = new HTML(GeneralComunicator.i18n("mail.attachment"));
		storageText = new HTML(GeneralComunicator.i18n("mail.storage"));

		toHPanel.add(Util.hSpace("5px"));
		bccHPanel.add(Util.hSpace("5px"));
		ccHPanel.add(Util.hSpace("5px"));
		replyToHPanel.add(Util.hSpace("5px"));
		subjectHPanel.add(Util.hSpace("5px"));
		attachmentHPanel.add(Util.hSpace("5px"));
		storageHPanel.add(Util.hSpace("5px"));

		toHPanel.add(toText);
		bccHPanel.add(bccText);
		ccHPanel.add(ccText);
		replyToHPanel.add(replyToText);
		subjectHPanel.add(subjectText);
		attachmentHPanel.add(attachmentText);
		storageHPanel.add(storageText);

		toImage = new Image(OKMBundleResources.INSTANCE.user());
		toFPanel = new FlowPanel();
		toScrollPanel = new ScrollPanel();
		FlexTable toTable = new FlexTable();
		init(toFPanel, toScrollPanel, toTable, toImage);

		ccImage = new Image(OKMBundleResources.INSTANCE.user());
		ccFPanel = new FlowPanel();
		ccScrollPanel = new ScrollPanel();
		FlexTable ccTable = new FlexTable();
		init(ccFPanel, ccScrollPanel, ccTable, ccImage);

		bccImage = new Image(OKMBundleResources.INSTANCE.user());
		bccFPanel = new FlowPanel();
		bccScrollPanel = new ScrollPanel();
		FlexTable bccTable = new FlexTable();
		init(bccFPanel, bccScrollPanel, bccTable, bccImage);

		replyToImage = new Image(OKMBundleResources.INSTANCE.user());
		replyToFPanel = new FlowPanel();
		replyToScrollPanel = new ScrollPanel();
		FlexTable replyToTable = new FlexTable();
		init(replyToFPanel, replyToScrollPanel, replyToTable, replyToImage);

		storageList = new ListBox();
		storageList.setStyleName("okm-Input");
		storageList.addItem(GeneralComunicator.i18n("mail.storage.default.folder"), GWTWorkspace.MAIL_STORAGE_MAIL_FOLDER);
		storageList.addItem(GeneralComunicator.i18n("mail.storage.current.folder"), GWTWorkspace.MAIL_STORAGE_CURRENT_FOLDER);

		for (int i = 0; i < storageList.getItemCount(); i++) {
			if (storageList.getValue(i).equalsIgnoreCase(GeneralComunicator.getWorkspace().getSentMailStorage())) {
				storageList.setItemSelected(i, true);
				break;
			}
		}

		FlexTable storageTable = new FlexTable();
		storageTable.setCellPadding(2);
		storageTable.setCellSpacing(2);
		storageTable.setWidth((width - 50) + "px");
		storageTable.setWidget(0, 1, storageList);
		storageTable.getFlexCellFormatter().setWidth(0, 0, "15px");
		storageTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);

		// subject
		subject = new TextBox();
		subject.setWidth("100%");
		subject.setStyleName("okm-Input");

		FlexTable subjectTable = new FlexTable();
		subjectTable.setCellPadding(2);
		subjectTable.setCellSpacing(2);
		subjectTable.setWidth((width - 50) + "px");
		subjectTable.setWidget(0, 1, subject);
		subjectTable.getFlexCellFormatter().setWidth(0, 0, "15px");
		subjectTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);

		attachmentImage = new Image(OKMBundleResources.INSTANCE.attachment());
		attachmentImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().findDocumentSelectPopup.show(FindDocumentSelectPopup.ORIGIN_MAIL_EDITOR_ATTACHMENT);
				attachment = true;
			}
		});
		attachmentImage.addStyleName("okm-Hyperlink");

		attachmentFPanel = new FlowPanel();
		attachmentFPanel.setWidth("100%");

		attachmentScrollPanel = new ScrollPanel();
		attachmentScrollPanel.setStyleName("gwt-ScrollTable");
		attachmentScrollPanel.setWidth("100%");
		attachmentScrollPanel.add(attachmentFPanel);

		FlexTable attachmentTable = new FlexTable();
		attachmentTable.setCellPadding(2);
		attachmentTable.setCellSpacing(2);
		attachmentTable.setWidth("100%");
		attachmentTable.setWidget(0, 0, attachmentImage);
		attachmentTable.setWidget(0, 1, attachmentScrollPanel);
		attachmentTable.getFlexCellFormatter().setWidth(0, 0, "10px");
		attachmentTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);

		mailTable = new FlexTable();
		mailTable.setCellPadding(0);
		mailTable.setCellSpacing(0);
		mailTable.setWidth("100%");
		mailTable.setWidget(0, 0, toHPanel);
		mailTable.setWidget(0, 1, toTable);
		mailTable.setWidget(1, 0, ccHPanel);
		mailTable.setWidget(1, 1, ccTable);
		mailTable.setWidget(2, 0, bccHPanel);
		mailTable.setWidget(2, 1, bccTable);
		mailTable.setWidget(3, 0, replyToHPanel);
		mailTable.setWidget(3, 1, replyToTable);
		mailTable.setWidget(4, 0, subjectHPanel);
		mailTable.setWidget(4, 1, subjectTable);
		mailTable.setWidget(5, 0, attachmentHPanel);
		mailTable.setWidget(5, 1, attachmentTable);
		mailTable.setWidget(6, 0, storageHPanel);
		mailTable.setWidget(6, 1, storageTable);
		mailTable.getFlexCellFormatter().setWidth(0, 0, "50px");
		mailTable.getFlexCellFormatter().setWidth(1, 0, "50px");
		mailTable.getFlexCellFormatter().setWidth(2, 0, "50px");
		mailTable.getFlexCellFormatter().setWidth(3, 0, "50px");
		mailTable.getFlexCellFormatter().setWidth(4, 0, "50px");
		mailTable.getFlexCellFormatter().setWidth(5, 0, "50px");
		mailTable.getFlexCellFormatter().setWidth(6, 0, "70px");
		mailTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_MIDDLE);
		mailTable.getFlexCellFormatter().setVerticalAlignment(1, 0, HasAlignment.ALIGN_MIDDLE);
		mailTable.getFlexCellFormatter().setVerticalAlignment(2, 0, HasAlignment.ALIGN_MIDDLE);
		mailTable.getFlexCellFormatter().setVerticalAlignment(3, 0, HasAlignment.ALIGN_MIDDLE);
		mailTable.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_LEFT);
		mailTable.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasAlignment.ALIGN_LEFT);
		mailTable.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasAlignment.ALIGN_LEFT);
		mailTable.getFlexCellFormatter().setHorizontalAlignment(3, 0, HasAlignment.ALIGN_LEFT);
		mailTable.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasAlignment.ALIGN_LEFT);
		mailTable.getFlexCellFormatter().setHorizontalAlignment(5, 0, HasAlignment.ALIGN_LEFT);
		mailTable.getFlexCellFormatter().setHorizontalAlignment(6, 0, HasAlignment.ALIGN_LEFT);
	}

	/**
	 * rebuildFormEditor
	 */
	private Frame rebuildFormEditor() {
		mailEditor = new Frame("about:blank");
		DOM.setElementProperty(mailEditor.getElement(), "id", "okm_mail_tinymce");
		DOM.setElementProperty(mailEditor.getElement(), "frameborder", "0");
		DOM.setElementProperty(mailEditor.getElement(), "marginwidth", "0");
		DOM.setElementProperty(mailEditor.getElement(), "marginheight", "0");
		DOM.setElementProperty(mailEditor.getElement(), "scrolling", "no");

		// Commented because on IE show clear if allowtransparency=true
		DOM.setElementProperty(mailEditor.getElement(), "allowtransparency", "false");

		mailEditor.setUrl(Main.CONTEXT + "/frontend/okm_mail_tinymce4.jsp");
		mailEditor.setStyleName("okm-Iframe");
		return mailEditor;
	}

	/**
	 * getMailText
	 */
	public String getTextAreaText() {
		return htmlText;
	}

	/**
	 * jsSendMail
	 */
	public void jsSendMail(String htmlText) {
		this.htmlText = htmlText;
		if (toUsers.size() == 0 && toRoles.size() == 0 && toMails.size() == 0 &&
				ccUsers.size() == 0 && ccRoles.size() == 0 && ccMails.size() == 0 &&
				bccUsers.size() == 0 && bccRoles.size() == 0 && bccMails.size() == 0) {
			warningMailPopup.getTextWarning().setText(GeneralComunicator.i18n("maileditor.warning.message.recipient"));
			warningMailPopup.center();
		} else if (subject.getText().equals("")) {
			warningMailPopup.getTextWarning().setText(GeneralComunicator.i18n("maileditor.warning.message.subject"));
			warningMailPopup.center();
		} else {
			final Map<String, List<String>> recipientsMap = new HashMap<>();
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_TO_USER, toUsers);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_TO_ROLE, toRoles);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_EXTERNAL_TO_MAIL, toMails);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_CC_USER, ccUsers);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_CC_ROLE, ccRoles);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_EXTERNAL_CC_MAIL, ccMails);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_BCC_USER, bccUsers);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_BCC_ROLE, bccRoles);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_EXTERNAL_REPLY_MAIL, bccMails);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_REPLY_USER, replyToUsers);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_REPLY_ROLE, replyToRoles);
			recipientsMap.put(GWTMailConstants.RECIPIENT_TYPE_EXTERNAL_REPLY_MAIL, replyToMails);

			// Check where to store mail
			if (storageList.getValue(storageList.getSelectedIndex()).equalsIgnoreCase(GWTWorkspace.MAIL_STORAGE_MAIL_FOLDER)) {
				repositoryService.getMailFolder(new AsyncCallback<GWTFolder>() {
					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("MailEditorPopup", caught);
					}

					@Override
					public void onSuccess(GWTFolder result) {
						String storePath = result.getPath() + "/" + GWTMail.SENT;
						mailService.sendMail(uuidList, recipientsMap, subject.getText(), getTextAreaText(), attachment, storePath, new AsyncCallback<GWTMail>() {
							public void onSuccess(GWTMail result) {
								CommonUI.openPathByUuid(result.getUuid());
								hide();
							}

							public void onFailure(Throwable caught) {
								Main.get().showError("MailEditorPopup", caught);
							}
						});
					}
				});
			} else {
				String storePath = Main.get().activeFolderTree.getActualPath();
				mailService.sendMail(uuidList, recipientsMap, subject.getText(), getTextAreaText(), attachment, storePath, new AsyncCallback<GWTMail>() {
					public void onSuccess(GWTMail result) {
						CommonUI.openPathByUuid(result.getUuid());
						hide();
					}

					public void onFailure(Throwable caught) {
						Main.get().showError("MailEditorPopup", caught);
					}
				});
			}
		}
	}

	/**
	 * addRecipients
	 */
	public void addRecipients(String recipients, FlowPanel fPanel, ScrollPanel scrollPanel, Map<String, Widget> recipientMap, List<String> recipientList) {
		// Remove white spaces ( mail case ).
		List<String> recipientNames = new ArrayList<>(Arrays.asList(recipients.isEmpty() ? new String[0] : recipients.replaceAll(" ", "").split(",")));
		if (!recipientNames.isEmpty()) {
			for (String recipient : recipientNames) {
				if (!recipientMap.containsKey(recipient) && recipient.length() > 0) {
					recipientList.add(recipient);
					Widget recipientButton = getRecipientWidget(recipient, fPanel, scrollPanel, recipientMap, recipientList);
					recipientMap.put(recipient, recipientButton);
					fPanel.add(recipientButton);
				}
			}
		}
	}

	/**
	 * addAttachment
	 */
	public void addAttachment(String uuid, String mimeType, String name, FlowPanel fPanel, ScrollPanel scrollPanel) {
		if (uuid != null && !uuid.equals("")) {
			if (!attachmentMap.containsKey(uuid) && uuid.length() > 0) {
				uuidList.add(uuid);
				Widget attachButton = getAttachmentWidget(uuid, mimeType, name, fPanel, scrollPanel);
				attachmentMap.put(uuid, attachButton);
				fPanel.add(attachButton);
			}
		}
	}

	/**
	 * Get a new widget attachment
	 */
	private HorizontalPanel getAttachmentWidget(final String uuid, String mimeType, String name, final FlowPanel fPanel,
												final ScrollPanel scrollPanel) {
		final HorizontalPanel externalPanel = new HorizontalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML();
		ImageHover delete = new ImageHover("img/icon/actions/delete_disabled.gif", "img/icon/actions/delete.gif");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (attachmentMap.containsKey(uuid)) {
					uuidList.remove(uuid);
					attachmentMap.remove(uuid);
				}
				fPanel.remove(externalPanel);

				// Restore height for empty values
				if (fPanel.getWidgetCount() == 0 && scrollPanel != null) {
					scrollPanel.setHeight("20px");
				}
			}
		});
		delete.setStyleName("okm-KeyMap-ImageHover");
		HTML icon = new HTML(Util.mimeImageHTML(mimeType));
		HTML htmlName = new HTML(name);
		hPanel.add(icon);
		hPanel.add(htmlName);
		hPanel.add(space);
		hPanel.add(delete);
		hPanel.setCellWidth(space, "3px");
		hPanel.setCellVerticalAlignment(icon, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(htmlName, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(delete, HasAlignment.ALIGN_MIDDLE);
		hPanel.setStyleName("okm-KeyMap-Gray");
		HTML space1 = new HTML();
		externalPanel.add(hPanel);
		externalPanel.add(space1);
		externalPanel.setCellWidth(space1, "3px");
		externalPanel.setStylePrimaryName("okm-cloudTags");
		return externalPanel;
	}

	/**
	 * Get a new widget recipient
	 */
	private HorizontalPanel getRecipientWidget(final String recipient, final FlowPanel fPanel, final ScrollPanel scrollPanel,
											   final Map<String, Widget> recipientMap, final List<String> recipientList) {
		final HorizontalPanel externalPanel = new HorizontalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML();
		ImageHover delete = new ImageHover("img/icon/actions/delete_disabled.gif", "img/icon/actions/delete.gif");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (recipientMap.containsKey(recipient)) {
					recipientMap.remove(recipient);
					recipientList.remove(recipient);
				}
				fPanel.remove(externalPanel);

				// Restore height for empty values
				if (fPanel.getWidgetCount() == 0 && scrollPanel != null) {
					scrollPanel.setHeight("20px");
				}
			}
		});
		delete.setStyleName("okm-KeyMap-ImageHover");
		hPanel.add(new HTML(recipient));
		hPanel.add(space);
		hPanel.add(delete);
		hPanel.setCellWidth(space, "3px");
		hPanel.setStyleName("okm-KeyMap-Gray");
		HTML space1 = new HTML();
		externalPanel.add(hPanel);
		externalPanel.add(space1);
		externalPanel.setCellWidth(space1, "3px");
		externalPanel.setStylePrimaryName("okm-cloudTags");
		return externalPanel;
	}

	/**
	 * init
	 */
	private void init(FlowPanel fPanel, ScrollPanel scrollPanel, FlexTable table, Image image) {
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int type = RecipientsPopup.NONE;
				Object aux = event.getSource();
				if (aux.equals(toImage)) {
					type = RecipientsPopup.TO;
				} else if (aux.equals(bccImage)) {
					type = RecipientsPopup.BCC;
				} else if (aux.equals(ccImage)) {
					type = RecipientsPopup.CC;
				} else if (aux.equals(replyToImage)) {
					type = RecipientsPopup.REPLY_TO;
				}
				recipientsPopup.reset(type);
				recipientsPopup.center();
			}
		});
		image.addStyleName("okm-Hyperlink");

		fPanel.setWidth("100%");

		scrollPanel.setStyleName("gwt-ScrollTable");
		scrollPanel.addStyleName("okm-Input");
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("20px");
		scrollPanel.add(fPanel);

		table.setCellPadding(2);
		table.setCellSpacing(2);
		table.setWidth((width - 50) + "px");
		table.setWidget(0, 0, image);
		table.setWidget(0, 1, scrollPanel);
		table.getFlexCellFormatter().setWidth(0, 0, "10px");
		table.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
	}

	/**
	 * drawMailEditor
	 */
	public void drawMailEditor() {
		// Evaluate screen max size
		height = GeneralComunicator.get().mainPanel.getOffsetHeight() - 60;

		if (Main.get().mainPanel.getOffsetWidth() < width + UIDialogConstants.MARGIN) {
			width = Main.get().mainPanel.getOffsetWidth() - UIDialogConstants.MARGIN;
		}

		initComponents();
		vPanel.clear();
		toUsers = new ArrayList<>();
		toRoles = new ArrayList<>();
		toMails = new ArrayList<>();
		ccUsers = new ArrayList<>();
		ccRoles = new ArrayList<>();
		ccMails = new ArrayList<>();
		bccUsers = new ArrayList<>();
		bccRoles = new ArrayList<>();
		bccMails = new ArrayList<>();
		replyToUsers = new ArrayList<>();
		replyToRoles = new ArrayList<>();
		replyToMails = new ArrayList<>();
		uuidList = new ArrayList<>();

		mailEditor = rebuildFormEditor();

		vPanel.add(mailTable);
		vPanel.add(mailEditor);

		center();

		// Some bottom fix correction ( grey bottom is not included in textarea size )
		mailEditor.setPixelSize(mailTable.getOffsetWidth(), height - BOTTOM_GREY_HEIGHT - mailTable.getOffsetHeight());

		// Some bottom fix correction ( grey bottom is not included in textarea size )
		mailEditor.setSize("" + (mailTable.getOffsetWidth()) + "px", "" + (height - BOTTOM_GREY_HEIGHT - mailTable.getOffsetHeight()) + "px");
	}

	/**
	 * Set mail to reply
	 */
	public void setMailToReply(GWTMail mail) {
		// To
		String mails = mail.getFrom();
		toMails.clear();
		addRecipients(mails, toFPanel, toScrollPanel, toRecipientMap, toMails);

		// Subject
		subject.setText("Re: " + mail.getSubject());
		replyMail(mail);
	}

	/**
	 * Set mail to reply to all
	 */
	public void setMailToReplyAll(GWTMail mail) {
		// TO
		String mails = mail.getFrom();
		toMails.clear();
		addRecipients(mails, toFPanel, toScrollPanel, toRecipientMap, toMails);

		// Add others recipients
		for (String mailTo : mail.getTo()) {
			addRecipients(mailTo, toFPanel, toScrollPanel, toRecipientMap, toMails);
		}

		// CC
		toMails.clear();
		for (String mailCc : mail.getCc()) {
			addRecipients(mailCc, toFPanel, toScrollPanel, toRecipientMap, toMails);
		}

		// BCC:
		toMails.clear();
		for (String mailBcc : mail.getBcc()) {
			addRecipients(mailBcc, toFPanel, toScrollPanel, toRecipientMap, toMails);
		}

		// Subject
		subject.setText("Re: " + mail.getSubject());
		replyMail(mail);
	}

	/**
	 * Set mail to forward
	 */
	public void setMailToForward(GWTMail mail) {
		mailService.getAttachments(mail.getUuid(), new AsyncCallback<List<GWTDocument>>() {
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("MailEditorPopup", caught);
			}

			@Override
			public void onSuccess(List<GWTDocument> result) {
				for (GWTDocument document : result) {
					addAttachment(document.getUuid(), document.getMimeType(), Util.getName(document.getPath()), attachmentFPanel, null);
				}
			}
		});

		// Subject
		subject.setText("Fwd: " + mail.getSubject());
		replyMail(mail);
	}

	/**
	 *
	 */
	private void replyMail(GWTMail mail) {
		String cc = "";
		if (mail.getCc().length > 0) {
			cc = "<b>Cc:</b> " + Arrays.toString(mail.getCc()) + "<br>";
		}

		String reply = "<br><br><hr>" +
				"<b>From:</b> " + mail.getFrom() + " <br>" +
				"<b>Date:</b> " + mail.getReceivedDate() + " <br>" +
				"<b>To:</b> " + Arrays.toString(mail.getTo()) + " <br>" +
				cc +
				"<b>Subject</b>: " + mail.getSubject() + " <br><br><br>" +
				mail.getContent();
		// Text
		loadContent(reply);
	}

	@Override
	public void onMinimize() {
		// Nothing to do here
	}

	@Override
	public void onMaximize() {
		// Nothing to do here
	}

	@Override
	public void onClose() {
		confirmCancelMailEditor();
	}

	/**
	 * confirmCancelCheckout
	 */
	private static native void confirmCancelMailEditor() /*-{
		new $wnd.cancelMailEditor();
	}-*/;

	public void jsDrawMailEditor4() {
		String language = HTMLEditorUtils.getTinymceLang(GeneralComunicator.getLang());
		String theme = GeneralComunicator.getWorkspace().getTinymce4Theme();
		String plugins = GeneralComunicator.getWorkspace().getTinymce4Plugins();
		String toolbar1 = "okm_sendMail, okm_cancelSendMail, okm_searchDocument, okm_searchFolder, | ,styleselect, |, bold, italic, |, alignleft, aligncenter, alignright, alignjustify, | ,bullist, numlist, outdent, indent, | forecolor, backcolor, emoticons,";
		String toolbar2 = "";
		String sendMailText = GeneralComunicator.i18n("general.menu.file.send.mail");
		String cancelText = GeneralComunicator.i18n("button.cancel");
		String searchDocumentText = GeneralComunicator.i18n("general.menu.file.find.document");
		String searchFolderText = GeneralComunicator.i18n("general.menu.file.find.folder");
		String searchImageText = GeneralComunicator.i18n("general.menu.file.find.image");

		drawMailEditor(language, theme, plugins, toolbar1, toolbar2, sendMailText, cancelText, searchDocumentText, searchFolderText, searchImageText);
	}

	/**
	 * drawMailEditor
	 */
	public static native void drawMailEditor(String t_language, String t_theme, String t_plugins,
											 String toolbar1, String toolbar2, String sendMailText, String cancelText,
											 String searchDocumentText, String searchFolderText, String searchImageText) /*-{
		new $wnd.drawMailEditor4(t_language, t_theme, t_plugins, toolbar1, toolbar2, sendMailText, cancelText, searchDocumentText, searchFolderText, searchImageText);
	}-*/;

	/**
	 * Load Content data
	 */
	public static native void loadContent(String htmlText) /*-{
		new $wnd.loadContent(htmlText);
	}-*/;

	/**
	 * setEditorText
	 */
	public static native void setEditorText(String text) /*-{
		new $wnd.setEditorText(text);
	}-*/;

	/**
	 * initJavaScriptApi
	 */
	public native void initJavaScriptApi(MailEditorPopup mailEditorPopup) /*-{
		$wnd.jsDrawMailEditor4 = function () {
			mailEditorPopup.@com.openkm.frontend.client.widget.sendmail.MailEditorPopup::jsDrawMailEditor4()();
			return true;
		}

		$wnd.jsHideMailEditorPopup = function () {
			mailEditorPopup.@com.openkm.frontend.client.widget.sendmail.MailEditorPopup::hide()();
			return true;
		}

		$wnd.jsSendMail = function (s) {
			mailEditorPopup.@com.openkm.frontend.client.widget.sendmail.MailEditorPopup::jsSendMail(Ljava/lang/String;)(s);
			return true;
		}
	}-*/;
}
