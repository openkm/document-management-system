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

package com.openkm.frontend.client.widget.chat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.service.OKMChatService;
import com.openkm.frontend.client.service.OKMChatServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.Iterator;
import java.util.List;

/**
 * Chat room popup
 *
 * @author jllort
 */
public class ChatRoomPopup extends ChatRoomDialogBox {
	private final OKMChatServiceAsync chatService = (OKMChatServiceAsync) GWT.create(OKMChatService.class);

	private final static int DELAY_PENDING_MESSAGE = 1000; // 1 seg
	private final static int DELAY_USERS_IN_ROOM = 3 * 1000; // 3 seg

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button close;
	private TextArea textArea;
	private FlexTable table;
	private ScrollPanel scrollPanel;
	private HTML usersInRoomText;
	private ChatRoomDialogBox singleton;
	private boolean chatRoomActive = true;
	private Image addUserToChatRoom;
	private String connectedRoom = "";

	/**
	 * Chat room popup
	 */
	public ChatRoomPopup(String user, final String room) {
		// Establishes auto-close when click outside
		super(false, false);
		setText(Main.i18n("chat.room"));
		singleton = this;
		chatRoomActive = true;
		connectedRoom = room;
		usersInRoomText = new HTML("");
		addUserToChatRoom = new Image(OKMBundleResources.INSTANCE.addUserToChatRoom());
		addUserToChatRoom.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				chatService.getUsersInRoom(room, new AsyncCallback<List<String>>() {

					@Override
					public void onSuccess(List<String> result) {
						Main.get().onlineUsersPopup.setAction(OnlineUsersPopup.ACTION_ADD_USER_TO_ROOM, room);
						Main.get().onlineUsersPopup.setUsersInChat(result);
						Main.get().onlineUsersPopup.center();
						Main.get().onlineUsersPopup.refreshOnlineUsers();
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("Logout", caught);
					}
				});
			}
		});

		close = new Button(Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				chatRoomActive = false;
				chatService.closeRoom(room, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object arg0) {
						Main.get().mainPanel.bottomPanel.userInfo.removeChatRoom(singleton);
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("Logout", caught);
					}
				});

				hide();
			}
		});

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();

		HTML space4 = new HTML("");
		hPanel.add(space4);
		hPanel.add(addUserToChatRoom);
		hPanel.add(usersInRoomText);
		HTML space5 = new HTML("");
		hPanel.add(space5);

		hPanel.setCellHorizontalAlignment(addUserToChatRoom, HasAlignment.ALIGN_LEFT);
		hPanel.setCellHorizontalAlignment(usersInRoomText, HasAlignment.ALIGN_RIGHT);
		hPanel.setCellWidth(space4, "5px");
		hPanel.setCellWidth(addUserToChatRoom, "189px");
		hPanel.setCellWidth(usersInRoomText, "189px");
		hPanel.setCellWidth(space5, "5px");
		hPanel.setWidth("100%");

		table = new FlexTable();
		table.setBorderWidth(0);
		table.setCellPadding(2);
		table.setCellSpacing(0);
		table.setWidth("100%");

		scrollPanel = new ScrollPanel(table);
		scrollPanel.setSize("388px", "225px");

		textArea = new TextArea();
		textArea.setSize("390px", "50px");
		textArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeKeyCode() && textArea.getText().length() == 1) {
					// Case only has typewrite a enter character, reset textArea values
					textArea.setText("");
				} else if (KeyCodes.KEY_ENTER == event.getNativeKeyCode() && textArea.getText().length() > 1) {
					textArea.setEnabled(false);
					chatService.addMessageToRoom(room, formatingMessage(textArea.getText()), new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							addMessage("<b>" + Main.get().workspaceUserProperties.getUser().getUsername() + "</b>: "
									+ formatingMessage(textArea.getText()));
							textArea.setText("");
							textArea.setEnabled(true);
						}

						@Override
						public void onFailure(Throwable caught) {
							textArea.setEnabled(true);
							Main.get().showError("AddMessageToRoom", caught);
						}
					});
				}
			}
		});

		vPanel.add(hPanel);
		HTML space = new HTML();
		vPanel.add(space);
		vPanel.add(scrollPanel);
		HTML space2 = new HTML();
		vPanel.add(space2);
		vPanel.add(textArea);
		HTML space3 = new HTML();
		vPanel.add(space3);
		vPanel.add(close);

		vPanel.setCellHeight(hPanel, "25px");
		vPanel.setCellHeight(scrollPanel, "225px");
		vPanel.setCellHeight(textArea, "50px");
		vPanel.setCellHeight(space, "5px");
		vPanel.setCellHeight(space2, "5px");
		vPanel.setCellHeight(space3, "5px");
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(scrollPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(textArea, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(close, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellVerticalAlignment(scrollPanel, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellVerticalAlignment(textArea, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellVerticalAlignment(close, HasAlignment.ALIGN_MIDDLE);

		scrollPanel.setStyleName("okm-PanelSelected");
		scrollPanel.addStyleName("okm-Input");
		textArea.setStyleName("okm-TextArea");
		close.setStyleName("okm-NoButton");

		vPanel.setWidth("400px");
		vPanel.setHeight("350px");

		setStyleName("okm-Popup");

		refreshUsersInRoom(room); // Refresh users in room

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("chat.room"));
		close.setHTML(Main.i18n("button.close"));
	}

	/**
	 * refreshUsersInRoom()
	 */
	private void refreshUsersInRoom(final String room) {
		if (chatRoomActive) {
			chatService.usersInRoom(room, new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					usersInRoomText.setHTML("(" + result + ") " + Main.i18n("chat.users.in.room"));
					Timer timer = new Timer() {
						@Override
						public void run() {
							refreshUsersInRoom(room);
						}
					};

					timer.schedule(DELAY_USERS_IN_ROOM);
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("UsersInRoom", caught);
				}
			});
		}
	}

	/**
	 * getPendingMessage
	 */
	public void getPendingMessage(final String room) {
		if (chatRoomActive) {
			chatService.getPendingMessage(room, new AsyncCallback<List<String>>() {

				@Override
				public void onSuccess(List<String> result) {
					for (Iterator<String> it = result.iterator(); it.hasNext(); ) {
						addMessage(it.next());
					}

					Timer timer = new Timer() {
						@Override
						public void run() {
							getPendingMessage(room);
						}
					};

					timer.schedule(DELAY_PENDING_MESSAGE);
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("getPendingMessage", caught);
				}
			});
		}
	}

	/**
	 * addMessage
	 */
	private void addMessage(String msg) {
		table.setHTML(table.getRowCount(), 0, msg);

		if (table.getOffsetHeight() > scrollPanel.getOffsetHeight()) {
			int position = table.getOffsetHeight() - scrollPanel.getOffsetHeight();
			scrollPanel.setVerticalScrollPosition(position);
		}
	}

	/**
	 * formatingMessage
	 */
	private String formatingMessage(String msg) {
		return msg.replaceAll("\\n", "</br>");
	}

	/**
	 * getRoom
	 */
	public String getRoom() {
		return connectedRoom;
	}

	/**
	 * setChatRoomActive
	 */
	public void setChatRoomActive(boolean active) {
		hide();
		chatRoomActive = active;
	}
}
