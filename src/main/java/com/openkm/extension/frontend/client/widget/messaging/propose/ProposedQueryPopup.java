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

package com.openkm.extension.frontend.client.widget.messaging.propose;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMProposedQueryService;
import com.openkm.extension.frontend.client.service.OKMProposedQueryServiceAsync;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.SearchComunicator;
import com.openkm.frontend.client.util.Util;

/**
 * ProposedQueryPopup
 *
 * @author jllort
 *
 */
public class ProposedQueryPopup extends DialogBox {

	private final OKMProposedQueryServiceAsync proposedQueryService = (OKMProposedQueryServiceAsync) GWT.create(OKMProposedQueryService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button closeButton;
	private Button sendButton;
	private TextArea message;
	private ScrollPanel messageScroll;
	private NotifyPanel notifyPanel;
	private HTML commentTXT;
	private HTML errorNotify;
	private String users;
	private String roles;
	private int type = MessagingToolBarBox.PROPOSED_QUERY_NONE;

	public ProposedQueryPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		setText(GeneralComunicator.i18nExtension("propose.query.title"));
		users = "";
		roles = "";

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		notifyPanel = new NotifyPanel();
		message = new TextArea();

		errorNotify = new HTML(GeneralComunicator.i18nExtension("messaging.label.must.select.users"));
		errorNotify.setWidth("365px");
		errorNotify.setVisible(false);
		errorNotify.setStyleName("fancyfileupload-failed");

		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + GeneralComunicator.i18nExtension("messaging.label.notify.comment"));

		closeButton = new Button(GeneralComunicator.i18nExtension("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				reset();
			}
		});

		sendButton = new Button(GeneralComunicator.i18nExtension("button.send"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Only sends if there's some user selected
				users = notifyPanel.getUsersToNotify();
				roles = notifyPanel.getRolesToNotify();
				if (!users.equals("") || !roles.equals("")) {
					errorNotify.setVisible(false);
					sendProposal();
					hide();
					reset();
				} else {
					errorNotify.setVisible(true);
				}
			}
		});

		hPanel.add(closeButton);
		HTML space = new HTML("");
		hPanel.add(space);
		hPanel.add(sendButton);

		hPanel.setCellWidth(space, "40px");

		message.setSize("375px", "60px");
		message.setStyleName("okm-TextArea");
		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		messageScroll = new ScrollPanel(message);
		messageScroll.setAlwaysShowScrollBars(false);

		vPanel.add(new HTML("<br>"));
		vPanel.add(commentTXT);
		vPanel.add(messageScroll);
		vPanel.add(errorNotify);
		vPanel.add(new HTML("<br>"));
		vPanel.add(notifyPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(errorNotify, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(messageScroll, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(notifyPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		vPanel.setWidth("100%");

		closeButton.setStyleName("okm-NoButton");
		sendButton.setStyleName("okm-YesButton");

		commentTXT.addStyleName("okm-DisableSelect");
		notifyPanel.addStyleName("okm-DisableSelect");

		setWidget(vPanel);
	}

	/**
	 * langRefresh 
	 *
	 * Refreshing lang
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18nExtension("propose.query.title"));
		closeButton.setHTML(GeneralComunicator.i18nExtension("button.close"));
		sendButton.setHTML(GeneralComunicator.i18nExtension("button.send"));
		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + GeneralComunicator.i18nExtension("messaging.label.notify.comment"));
		errorNotify.setHTML(GeneralComunicator.i18nExtension("messaging.label.must.select.users"));
		notifyPanel.langRefresh();
	}

	/**
	 * executeProposeQuery
	 *
	 * @param TYPE
	 */
	public void executeProposeQuery(int type) {
		this.type = type;
		switch (type) {
			case MessagingToolBarBox.PROPOSED_QUERY_SAVE_SEARCH:
				if (SearchComunicator.getSelectedRowSearchSaved() >= 0) {
					reset();
					super.center();
				}
				break;
			case MessagingToolBarBox.PROPOSED_QUERY_USER_NEWS:
				if (SearchComunicator.getSelectedRowUserNews() >= 0) {
					reset();
					super.center();
				}
				break;
		}
		// TODO:Solves minor bug with IE
		if (Util.getUserAgent().startsWith("ie")) {
			notifyPanel.tabPanel.setWidth("374px");
			notifyPanel.tabPanel.setWidth("375px");
		}
	}

	/**
	 * Sends proposal
	 */
	private void sendProposal() {
		GWTQueryParams params = new GWTQueryParams();
		switch (type) {
			case MessagingToolBarBox.PROPOSED_QUERY_SAVE_SEARCH:
				params = SearchComunicator.getSavedSearch();
				break;
			case MessagingToolBarBox.PROPOSED_QUERY_USER_NEWS:
				params = SearchComunicator.getSavedUserNews();
				break;
		}
		proposedQueryService.create(params.getId(), users, roles, message.getText(), new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("create", caught);
			}
		});
	}

	/**
	 * Reste values
	 */
	private void reset() {
		users = "";
		roles = "";
		message.setText("");
		notifyPanel.reset();
		notifyPanel.getAll();
		errorNotify.setVisible(false);
	}

	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		notifyPanel.enableAdvancedFilter();
	}

	/**
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		errorNotify.setVisible(false);
	}
}
