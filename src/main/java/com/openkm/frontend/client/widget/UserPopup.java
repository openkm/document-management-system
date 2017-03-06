/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
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

package com.openkm.frontend.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTTestMail;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.service.OKMGeneralServiceAsync;
import com.openkm.frontend.client.service.OKMWorkspaceService;
import com.openkm.frontend.client.service.OKMWorkspaceServiceAsync;

/**
 * User popup
 *
 * @author jllort
 */
public class UserPopup extends DialogBox implements ClickHandler {
	private final OKMWorkspaceServiceAsync workspaceService = (OKMWorkspaceServiceAsync) GWT
			.create(OKMWorkspaceService.class);
	private final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);

	private VerticalPanel vPanel;
	private FlexTable userFlexTable;
	private FlexTable mailFlexTable;
	private HTML userId;
	private HTML userName;
	private HTML userPassword;
	private HTML userMail;
	private HTML userRoles;
	private HTML mailProtocol;
	private HTML mailHost;
	private HTML mailUser;
	private HTML mailPassword;
	private HTML mailFolder;
	private ListBox mailProtocolList;
	private TextBox mailHostText;
	private TextBox mailUserText;
	private TextBox mailFolderText;
	private HTML userNameText;
	private PasswordTextBox userPasswordText;
	private PasswordTextBox userPasswordTextVerify;
	private TextBox userMailText;
	private HTML rolesPanel;
	private PasswordTextBox mailUserPasswordText;
	private Button acceptButton;
	private Button cancelButton;
	private Button deleteButton;
	private Button testButton;
	private HorizontalPanel hPanel;
	private HTML passwordError;
	private HTML passwordValidationError;
	private HTML mailPasswordError;
	private HTML mailError;
	private HTML mailTestError;
	private HTML mailTestOK;
	private GroupBoxPanel userGroupBoxPanel;
	private GroupBoxPanel mailGroupBoxPanel;

	/**
	 * User popup
	 */
	public UserPopup() {

		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		userFlexTable = new FlexTable();
		mailFlexTable = new FlexTable();

		userGroupBoxPanel = new GroupBoxPanel();
		userGroupBoxPanel.setCaption(Main.i18n("user.preferences.user.data"));
		userGroupBoxPanel.add(userFlexTable);

		mailGroupBoxPanel = new GroupBoxPanel();
		mailGroupBoxPanel.setCaption(Main.i18n("user.preferences.mail.data"));
		mailGroupBoxPanel.add(mailFlexTable);

		userId = new HTML(Main.i18n("user.preferences.user"));
		userName = new HTML(Main.i18n("user.preferences.name"));
		userPassword = new HTML(Main.i18n("user.preferences.password"));
		userMail = new HTML(Main.i18n("user.preferences.mail"));
		userRoles = new HTML(Main.i18n("user.preferences.roles"));
		mailProtocol = new HTML(Main.i18n("user.preferences.mail.protocol"));
		mailHost = new HTML(Main.i18n("user.preferences.mail.host"));
		mailUser = new HTML(Main.i18n("user.preferences.mail.user"));
		mailPassword = new HTML(Main.i18n("user.preferences.mail.user.password"));
		mailFolder = new HTML(Main.i18n("user.preferences.mail.folder"));
		userPasswordText = new PasswordTextBox();
		userPasswordTextVerify = new PasswordTextBox();
		userNameText = new HTML("");
		userMailText = new TextBox();
		rolesPanel = new HTML();
		mailUserPasswordText = new PasswordTextBox();
		passwordError = new HTML(Main.i18n("user.preferences.password.error"));
		passwordValidationError = new HTML("");
		mailPasswordError = new HTML(Main.i18n("user.preferences.mail.password.error.void"));
		mailError = new HTML(Main.i18n("user.preferences.mail.error"));
		mailTestError = new HTML(Main.i18n("user.preferences.mail.test.error"));
		mailTestOK = new HTML(Main.i18n("user.preferences.mail.test.ok"));

		passwordError.setVisible(false);
		passwordValidationError.setVisible(false);
		mailPasswordError.setVisible(false);
		mailError.setVisible(false);
		mailTestError.setVisible(false);
		mailTestOK.setVisible(false);

		mailHostText = new TextBox();
		mailUserText = new TextBox();
		mailFolderText = new TextBox();
		mailProtocolList = new ListBox();

		mailProtocolList.addItem("POP3", "pop3");
		mailProtocolList.addItem("POP3S", "pop3s");
		mailProtocolList.addItem("IMAP", "imap");
		mailProtocolList.addItem("IMAPS", "imaps");

		acceptButton = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				passwordError.setVisible(false);
				passwordValidationError.setVisible(false);
				mailPasswordError.setVisible(false);
				mailError.setVisible(false);
				mailTestError.setVisible(false);
				mailTestOK.setVisible(false);

				// Password always must be equals
				if (!userPasswordText.getText().equals(userPasswordTextVerify.getText())) {
					passwordError.setVisible(true);
					// Case creation
				} else if (Main.get().workspaceUserProperties.getWorkspace().getMailID() < 0
						&& mailUserPasswordText.getText().equals("")
						&& (mailFolderText.getText().length() > 0 || mailUserText.getText().length() > 0 || mailHostText
						.getText().length() > 0)) {
					mailPasswordError.setVisible(true);
					// Case update
				} else if ((mailUserPasswordText.getText().length() > 0 || mailFolderText.getText().length() > 0
						|| mailUserText.getText().length() > 0 || mailHostText.getText().length() > 0)
						&& !(mailFolderText.getText().length() > 0 && mailUserText.getText().length() > 0 && mailHostText
						.getText().length() > 0)) {
					mailError.setVisible(true);
				} else {
					final GWTWorkspace workspace = new GWTWorkspace();
					String proto = mailProtocolList.getValue(mailProtocolList.getSelectedIndex());
					workspace.setUser(Main.get().workspaceUserProperties.getUser());
					workspace.setEmail(userMailText.getText());
					workspace.setMailFolder(mailFolderText.getText());
					workspace.setMailProtocol(proto);
					workspace.setMailHost(mailHostText.getText());
					workspace.setMailUser(mailUserText.getText());
					workspace.setMailPassword(mailUserPasswordText.getText());
					workspace.setPassword(userPasswordText.getText());
					workspace.setMailID(Main.get().workspaceUserProperties.getWorkspace().getMailID());

					// First must validate password
					workspaceService.isValidPassword(userPasswordText.getText(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							if (result.equals("")) {
								workspaceService.updateUserWorkspace(workspace, callbackUpdateUserWorkspace);
							} else {
								passwordValidationError.setHTML(result);
								passwordValidationError.setVisible(true);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("callbackIsValidPassword", caught);
						}
					});
				}
			}
		});

		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		testButton = new Button(Main.i18n("button.test"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mailTestError.setVisible(false);
				mailTestOK.setVisible(false);
				testButton.setEnabled(false);
				String proto = mailProtocolList.getValue(mailProtocolList.getSelectedIndex());
				generalService.testMailConnection(proto, mailHostText.getText(), mailUserText.getText(),
						mailUserPasswordText.getText(), mailFolderText.getText(), new AsyncCallback<GWTTestMail>() {
							@Override
							public void onSuccess(GWTTestMail result) {
								if (!result.isError()) {
									mailTestError.setVisible(false);
									mailTestOK.setVisible(true);
								} else {
									mailTestError.setHTML(Main.i18n("user.preferences.mail.test.error") + "<br>"
											+ result.getErrorMsg());
									mailTestError.setVisible(true);
									mailTestOK.setVisible(false);
								}

								testButton.setEnabled(true);
							}

							@Override
							public void onFailure(Throwable caught) {
								mailTestError.setVisible(false);
								mailTestOK.setVisible(false);
								testButton.setEnabled(true);
								Main.get().showError("testMailConnection", caught);
							}
						});
			}
		});

		deleteButton = new Button(Main.i18n("button.delete"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				long Id = Main.get().workspaceUserProperties.getWorkspace().getMailID();

				if (Id >= 0) {
					workspaceService.deleteMailAccount(Id, callbackDeleteMailAccount);
				}
			}
		});

		hPanel = new HorizontalPanel();
		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(acceptButton);

		userFlexTable.setCellPadding(0);
		userFlexTable.setCellSpacing(2);
		userFlexTable.setWidth("455px");

		userFlexTable.setWidget(0, 0, userId);
		userFlexTable.setWidget(1, 0, userName);
		userFlexTable.setWidget(2, 0, userPassword);
		userFlexTable.setWidget(3, 0, userMail);
		userFlexTable.setWidget(4, 0, userRoles);

		userFlexTable.setWidget(1, 1, userNameText);
		userFlexTable.setWidget(2, 1, userPasswordText);
		userFlexTable.setWidget(2, 2, userPasswordTextVerify);
		userFlexTable.setWidget(3, 1, userMailText);
		userFlexTable.setWidget(4, 1, rolesPanel);

		userFlexTable.getFlexCellFormatter().setVerticalAlignment(4, 0, HasAlignment.ALIGN_TOP);
		userFlexTable.getFlexCellFormatter().setColSpan(3, 1, 2);
		userFlexTable.getFlexCellFormatter().setColSpan(4, 1, 2);

		mailFlexTable.setCellPadding(0);
		mailFlexTable.setCellSpacing(2);
		mailFlexTable.setWidth("455px");

		mailFlexTable.setWidget(1, 0, mailProtocol);
		mailFlexTable.setWidget(2, 0, mailHost);
		mailFlexTable.setWidget(3, 0, mailUser);
		mailFlexTable.setWidget(4, 0, mailPassword);
		mailFlexTable.setWidget(5, 0, mailFolder);

		mailFlexTable.setWidget(1, 1, mailProtocolList);
		mailFlexTable.setWidget(2, 1, mailHostText);
		mailFlexTable.setWidget(3, 1, mailUserText);
		mailFlexTable.setWidget(4, 1, mailUserPasswordText);
		mailFlexTable.setWidget(5, 1, mailFolderText);

		HorizontalPanel hMailButtonPanel = new HorizontalPanel();
		hMailButtonPanel.add(deleteButton);
		hMailButtonPanel.add(new HTML("&nbsp;"));
		hMailButtonPanel.add(testButton);
		mailFlexTable.setWidget(6, 0, hMailButtonPanel);

		mailFlexTable.getFlexCellFormatter().setColSpan(1, 1, 2);
		mailFlexTable.getFlexCellFormatter().setColSpan(6, 0, 3);
		mailFlexTable.getFlexCellFormatter().setAlignment(6, 0, HasHorizontalAlignment.ALIGN_CENTER,
				HasVerticalAlignment.ALIGN_MIDDLE);

		userMailText.setWidth("200px");
		mailHostText.setWidth("200px");
		mailUserText.setWidth("150px");
		mailUserPasswordText.setWidth("150px");
		mailFolderText.setWidth("150px");
		rolesPanel.setWidth("350px");
		userGroupBoxPanel.setWidth("460px");
		mailGroupBoxPanel.setWidth("460px");

		vPanel.setWidth("470px");
		vPanel.setHeight("195px");

		vPanel.add(new HTML("<br>"));
		vPanel.add(userGroupBoxPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(mailGroupBoxPanel);
		vPanel.add(passwordError);
		vPanel.add(passwordValidationError);
		vPanel.add(mailPasswordError);
		vPanel.add(mailError);
		vPanel.add(mailTestError);
		vPanel.add(mailTestOK);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(userGroupBoxPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailGroupBoxPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(passwordError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(passwordValidationError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailPasswordError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailTestError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailTestOK, HasAlignment.ALIGN_CENTER);

		userId.addStyleName("okm-NoWrap");
		userName.addStyleName("okm-NoWrap");
		userPassword.addStyleName("okm-NoWrap");
		userMail.addStyleName("okm-NoWrap");
		mailHost.addStyleName("okm-NoWrap");
		mailUser.addStyleName("okm-NoWrap");
		mailPassword.addStyleName("okm-NoWrap");
		mailFolder.addStyleName("okm-NoWrap");
		userPasswordText.setStyleName("okm-Input");
		userPasswordTextVerify.setStyleName("okm-Input");
		userMailText.setStyleName("okm-Input");
		mailProtocolList.setStyleName("okm-Input");
		mailHostText.setStyleName("okm-Input");
		mailUserText.setStyleName("okm-Input");
		mailUserPasswordText.setStyleName("okm-Input");
		mailFolderText.setStyleName("okm-Input");
		passwordError.setStyleName("okm-Input-Error");
		passwordValidationError.setStyleName("okm-Input-Error");
		mailPasswordError.setStyleName("okm-Input-Error");
		mailError.setStyleName("okm-Input-Error");
		mailTestError.setStyleName("okm-Input-Error");
		mailTestOK.setStyleName("okm-Input-Ok");
		acceptButton.setStyleName("okm-YesButton");
		cancelButton.setStyleName("okm-NoButton");
		deleteButton.setStyleName("okm-DeleteButton");
		testButton.setStyleName("okm-YesButton");

		super.hide();
		setWidget(vPanel);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
	 */
	public void onClick(ClickEvent event) {
		super.hide();
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("user.preferences.label"));
		userId.setHTML(Main.i18n("user.preferences.user"));
		userPassword.setHTML(Main.i18n("user.preferences.password"));
		userMail.setHTML(Main.i18n("user.preferences.mail"));
		mailProtocol.setHTML(Main.i18n("user.preferences.mail.protocol"));
		mailHost.setHTML(Main.i18n("user.preferences.mail.host"));
		mailUser.setHTML(Main.i18n("user.preferences.mail.user"));
		mailPassword.setHTML(Main.i18n("user.preferences.mail.user.password"));
		mailFolder.setHTML(Main.i18n("user.preferences.mail.folder"));
		passwordError.setHTML(Main.i18n("user.preferences.password.error"));
		passwordValidationError.setHTML("");
		mailPasswordError.setHTML(Main.i18n("user.preferences.mail.password.error.void"));
		mailError.setHTML(Main.i18n("user.preferences.mail.error"));
		mailTestError.setHTML(Main.i18n("user.preferences.mail.error"));
		mailTestOK.setHTML(Main.i18n("user.preferences.mail.ok"));
		acceptButton.setText(Main.i18n("button.accept"));
		cancelButton.setText(Main.i18n("button.cancel"));
		deleteButton.setText(Main.i18n("button.delete"));
		testButton.setText(Main.i18n("button.test"));
		userGroupBoxPanel.setCaption(Main.i18n("user.preferences.user.data"));
		mailGroupBoxPanel.setCaption(Main.i18n("user.preferences.mail.data"));
	}

	/**
	 * reset
	 *
	 */
	public void reset() {
		workspaceService.getUserWorkspace(new AsyncCallback<GWTWorkspace>() {
			@Override
			public void onSuccess(GWTWorkspace workspace) {
				setText(Main.i18n("user.preferences.label"));
				userPasswordText.setText("");
				userPasswordTextVerify.setText("");
				rolesPanel.setHTML("");

				for (int i = 0; i < mailProtocolList.getItemCount(); i++) {
					if (workspace.getMailProtocol().equals(mailProtocolList.getValue(i))) {
						mailProtocolList.setSelectedIndex(i);
						break;
					}
				}

				mailHostText.setText(workspace.getMailHost());
				mailUserText.setText(workspace.getMailUser());
				mailUserPasswordText.setText(workspace.getMailPassword());
				mailFolderText.setText(workspace.getMailFolder());
				userFlexTable.setText(0, 1, workspace.getUser().getId());
				userFlexTable.getFlexCellFormatter().setColSpan(0, 1, 2);
				userNameText.setText(workspace.getUser().getUsername());
				userMailText.setText(workspace.getEmail());

				String roles = "";
				for (String role : workspace.getRoleList()) {
					if (roles.length() > 0) {
						roles += ", ";
					}
					roles += role;
				}
				rolesPanel.setHTML(roles);

				passwordError.setVisible(false);
				passwordValidationError.setVisible(false);
				mailPasswordError.setVisible(false);
				mailError.setVisible(false);
				mailTestError.setVisible(false);
				mailTestOK.setVisible(false);

				if (workspace.isChangePassword()) {
					userMail.setVisible(true);
					userMailText.setVisible(true);
					userPassword.setVisible(true);
					userPasswordText.setVisible(true);
					userPasswordTextVerify.setVisible(true);
				} else {
					userMail.setVisible(true);
					userMailText.setVisible(false);
					userPassword.setVisible(false);
					userPasswordText.setVisible(false);
					userPasswordTextVerify.setVisible(false);
				}

				// Enables delete button only if there's some mail server configured to be removed
				if (workspace.getMailID() >= 0) {
					deleteButton.setVisible(true);
				} else {
					deleteButton.setVisible(false);
				}
				center();
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getUserWorkspace", caught);
			}
		});
	}

	/**
	 * Call back update user workspace data
	 */
	final AsyncCallback<Object> callbackUpdateUserWorkspace = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().workspaceUserProperties.refreshUserWorkspace(); // Refreshing workspace saved values
			hide();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackUpdateUserWorkspace", caught);
		}
	};

	/**
	 * Call back delete mail account
	 */
	final AsyncCallback<Object> callbackDeleteMailAccount = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().workspaceUserProperties.getUserWorkspace(); // Refreshing workspace saved values
			mailProtocolList.setSelectedIndex(0);
			mailHostText.setText("");
			mailUserText.setText("");
			mailUserPasswordText.setText("");
			mailFolderText.setText("");
			deleteButton.setVisible(false);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackDeleteMailAccount", caught);
		}
	};
}