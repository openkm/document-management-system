package com.openkm.frontend.client.widget;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTUINotification;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.widget.userinfo.UserInfoExtension;
import com.openkm.frontend.client.service.OKMChatService;
import com.openkm.frontend.client.service.OKMChatServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.chat.ChatRoomDialogBox;
import com.openkm.frontend.client.widget.chat.ChatRoomPopup;
import com.openkm.frontend.client.widget.chat.OnlineUsersPopup;

import java.util.ArrayList;
import java.util.List;

public class UserInfo extends Composite {
	private final OKMChatServiceAsync chatService = (OKMChatServiceAsync) GWT.create(OKMChatService.class);

	public static final int USERS_IN_ROOM_REFRESHING_TIME = 1000;
	private static final int NEW_ROOM_REFRESHING_TIME = 1000;

	private HorizontalPanel panel;
	private Image advertisement;
	private HTML user;
	private Image img;
	private HTML userRepositorySize;
	private Image imgRepositorySize;
	private HTML lockedDocuments;
	private Image imgLockedDocuments;
	private HTML checkoutDocuments;
	private Image imgCheckoutDocuments;
	private HTML subscriptions;
	private Image imgSubscriptions;
	private HTML newDocuments;
	private Image imgNewsDocuments;
	private HTML newWorkflowTasks;
	private Image imgWorkflowTasks;
	private HTML newWorkflowPooledTasks;
	private Image imgWorkflowPooledTasks;
	private Image imgChat;
	private Image imgNewChatRoom;
	private Image imgChatSeparator;
	private boolean chatConnected = false;
	private HTML usersConnected;
	private List<GWTUser> connectUsersList;
	private List<ChatRoomDialogBox> chatRoomList;
	private Image imgUserQuota;
	private boolean userQuota = false;
	private long quotaLimit = 0;
	private boolean quotaExceeded = false;
	private HTML quotaUsed;
	private int percent = 0;
	private List<UserInfoExtension> widgetExtensionList;
	private boolean getLoggedUsers = false;
	private boolean getPendingChatRoomUser = false;
	private boolean logoutDone = true;

	/**
	 * UserInfo
	 */
	public UserInfo() {
		widgetExtensionList = new ArrayList<UserInfoExtension>();
		connectUsersList = new ArrayList<GWTUser>();
		chatRoomList = new ArrayList<ChatRoomDialogBox>();
		img = new Image(OKMBundleResources.INSTANCE.openkmConnected());
		panel = new HorizontalPanel();
		panel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		user = new HTML("");
		userRepositorySize = new HTML("");
		usersConnected = new HTML("");
		lockedDocuments = new HTML("");
		checkoutDocuments = new HTML("");
		subscriptions = new HTML("");
		newDocuments = new HTML("");
		newWorkflowTasks = new HTML("");
		newWorkflowPooledTasks = new HTML("");
		quotaUsed = new HTML("");
		quotaUsed.setVisible(false);
		lockedDocuments.setVisible(false);
		checkoutDocuments.setVisible(false);
		subscriptions.setVisible(false);
		newDocuments.setVisible(false);
		newWorkflowTasks.setVisible(false);
		newWorkflowPooledTasks.setVisible(false);
		imgRepositorySize = new Image(OKMBundleResources.INSTANCE.repositorySize());
		imgUserQuota = new Image(OKMBundleResources.INSTANCE.quota1());
		imgChat = new Image(OKMBundleResources.INSTANCE.chatDisconnected());
		imgChatSeparator = new Image(OKMBundleResources.INSTANCE.separator());
		imgNewChatRoom = new Image(OKMBundleResources.INSTANCE.newChatRoom());
		imgLockedDocuments = new Image(OKMBundleResources.INSTANCE.lock());
		imgCheckoutDocuments = new Image(OKMBundleResources.INSTANCE.checkout());
		imgSubscriptions = new Image(OKMBundleResources.INSTANCE.subscribed());
		imgNewsDocuments = new Image(OKMBundleResources.INSTANCE.news());
		imgWorkflowTasks = new Image(OKMBundleResources.INSTANCE.workflowTasks());
		imgWorkflowPooledTasks = new Image(OKMBundleResources.INSTANCE.workflowPooledTasks());
		imgRepositorySize.setVisible(false);
		imgUserQuota.setVisible(false);
		imgChat.setVisible(false);
		imgChatSeparator.setVisible(false);
		usersConnected.setVisible(false);
		imgNewChatRoom.setVisible(false);
		imgLockedDocuments.setVisible(false);
		imgCheckoutDocuments.setVisible(false);
		imgSubscriptions.setVisible(false);
		imgNewsDocuments.setVisible(false);
		imgWorkflowTasks.setVisible(false);
		imgWorkflowPooledTasks.setVisible(false);
		imgChat.setTitle(Main.i18n("user.info.chat.connect"));
		imgUserQuota.setTitle(Main.i18n("user.info.user.quota"));
		imgNewChatRoom.setTitle(Main.i18n("user.info.chat.new.room"));
		imgLockedDocuments.setTitle(Main.i18n("user.info.locked.actual"));
		imgCheckoutDocuments.setTitle(Main.i18n("user.info.checkout.actual"));
		imgSubscriptions.setTitle(Main.i18n("user.info.subscription.actual"));
		imgNewsDocuments.setTitle(Main.i18n("user.info.news.new"));
		imgWorkflowTasks.setTitle(Main.i18n("user.info.workflow.pending.tasks"));
		imgWorkflowPooledTasks.setTitle(Main.i18n("user.info.workflow.pending.pooled.tasks"));

		imgLockedDocuments.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showUserView();
			}
		});

		imgCheckoutDocuments.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showUserView();
			}
		});

		imgSubscriptions.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showUserView();
			}
		});

		imgNewsDocuments.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showNewsView();
			}
		});

		imgWorkflowTasks.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showWorkflowView();
			}
		});

		imgWorkflowPooledTasks.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showWorkflowView();
			}
		});

		imgChat.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isPendingToClose()) {
					loginChat(false);
				} else {
					logoutChat();
				}
			}
		});

		imgNewChatRoom.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().onlineUsersPopup.setAction(OnlineUsersPopup.ACTION_NEW_CHAT);
				Main.get().onlineUsersPopup.center();
				Main.get().onlineUsersPopup.refreshOnlineUsers();
			}
		});

		advertisement = new Image(OKMBundleResources.INSTANCE.warning());
		advertisement.setVisible(false);

		advertisement.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().msgPopup.show();
			}
		});

		panel.add(advertisement);
		panel.add(new HTML("&nbsp;"));
		panel.add(img);
		panel.add(new HTML("&nbsp;"));
		panel.add(user);
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(new HTML("&nbsp;"));
		panel.add(imgRepositorySize);
		panel.add(new HTML("&nbsp;"));
		panel.add(userRepositorySize);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgUserQuota);
		panel.add(new HTML("&nbsp;"));
		panel.add(quotaUsed);
		panel.add(new HTML("&nbsp;"));
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(new HTML("&nbsp;"));
		panel.add(imgChat);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgNewChatRoom);
		panel.add(new HTML("&nbsp;"));
		panel.add(usersConnected);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgChatSeparator);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgLockedDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(lockedDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgCheckoutDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(checkoutDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgSubscriptions);
		panel.add(new HTML("&nbsp;"));
		panel.add(subscriptions);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgNewsDocuments);
		panel.add(newDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgWorkflowTasks);
		panel.add(newWorkflowTasks);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgWorkflowPooledTasks);
		panel.add(newWorkflowPooledTasks);
		panel.add(new HTML("&nbsp;"));

		imgLockedDocuments.setStyleName("okm-Hyperlink");
		imgCheckoutDocuments.setStyleName("okm-Hyperlink");
		imgSubscriptions.setStyleName("okm-Hyperlink");
		imgNewsDocuments.setStyleName("okm-Hyperlink");
		imgWorkflowTasks.setStyleName("okm-Hyperlink");
		imgWorkflowPooledTasks.setStyleName("okm-Hyperlink");
		imgChat.setStyleName("okm-Hyperlink");
		imgNewChatRoom.setStyleName("okm-Hyperlink");

		initWidget(panel);
	}

	/**
	 * Sets the user value
	 *
	 * @param username The user value
	 */
	public void setUser(String username, boolean isAdmin) {
		this.user.setHTML("&nbsp;" + Main.i18n("general.connected") + " " + username + "&nbsp;");

		if (isAdmin) {
			this.user.addStyleName("okm-Input-System");
		}
	}

	/**
	 * Sets the repository size
	 */
	public void setUserRepositorySize(double size) {
		imgRepositorySize.setVisible(true);
		userRepositorySize.setHTML("&nbsp;" + Util.formatSize(size) + "&nbsp;");
		if (userQuota) {
			if (size > 0) {
				if (size >= quotaLimit) {
					quotaExceeded = true;
					percent = 100;
					imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota6());
				} else {
					// Calculating %
					percent = new Double((size * 100) / quotaLimit).intValue();

					if (percent == 0) {
						percent = 1;
					} else if (percent > 100) {
						percent = 100;
					}

					if (percent <= 20) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota1());
					} else if (percent <= 40) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota2());
					} else if (percent <= 60) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota3());
					} else if (percent <= 80) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota4());
					} else {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota5());
					}
				}
			} else {
				quotaExceeded = false;
				imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota1());
			}
			quotaUsed.setHTML(percent + "%");
		}
	}

	/**
	 * Sets the locked documents
	 */
	public void setLockedDocuments(int value) {
		lockedDocuments.setHTML("&nbsp;" + value + "&nbsp;");
	}

	/**
	 * e
	 * Sets the checkout documents
	 */
	public void setCheckoutDocuments(int value) {
		checkoutDocuments.setHTML("&nbsp;" + value + "&nbsp;");
	}

	/**
	 * Sets the subscriptions documents and folders
	 */
	public void setSubscriptions(int value) {
		subscriptions.setHTML("&nbsp;" + value + "&nbsp;");
	}

	/**
	 * Sets the news documents
	 */
	public void setNewsDocuments(int value) {
		newDocuments.setHTML("&nbsp;" + value + "&nbsp;");
		if (value > 0) {
			imgNewsDocuments.setResource(OKMBundleResources.INSTANCE.newsAlert());
		} else {
			imgNewsDocuments.setResource(OKMBundleResources.INSTANCE.news());
		}
	}

	/**
	 * Sets the news workflows
	 */
	public void setNewsWorkflows(int value) {
		newWorkflowTasks.setHTML("&nbsp;" + value + "&nbsp;");

		if (value > 0) {
			imgWorkflowTasks.setResource(OKMBundleResources.INSTANCE.workflowTasksAlert());
		} else {
			imgWorkflowTasks.setResource(OKMBundleResources.INSTANCE.workflowTasks());
		}
	}

	/**
	 * Sets the pooled task instances
	 */
	public void setPooledTaskInstances(int value) {
		newWorkflowPooledTasks.setHTML("&nbsp;" + value + "&nbsp;");

		if (value > 0) {
			imgWorkflowPooledTasks.setResource(OKMBundleResources.INSTANCE.workflowPooledTasksAlert());
		} else {
			imgWorkflowPooledTasks.setResource(OKMBundleResources.INSTANCE.workflowPooledTasks());
		}
	}

	/**
	 * addUINotification
	 * <p>
	 * Sets the msg value
	 */
	public void addUINotification(GWTUINotification uin) {
		advertisement.setVisible(true);
		Main.get().msgPopup.add(uin);
	}

	/**
	 * setLastUIId
	 *
	 * @param id
	 */
	public void setLastUIId(int id) {
		Main.get().msgPopup.setLastUIId(id);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		String usr = Main.get().workspaceUserProperties.getUser().getUsername();
		user.setHTML("&nbsp;" + Main.i18n("general.connected") + " " + usr + "&nbsp;");

		if (chatConnected) {
			imgChat.setTitle(Main.i18n("user.info.chat.disconnect"));
			usersConnected.setHTML(connectUsersList.size() + "");
		} else {
			imgChat.setTitle(Main.i18n("user.info.chat.connect"));
			usersConnected.setHTML("");
		}

		imgUserQuota.setTitle(Main.i18n("user.info.user.quota"));
		imgNewChatRoom.setTitle(Main.i18n("user.info.chat.new.room"));
		imgLockedDocuments.setTitle(Main.i18n("user.info.locked.actual"));
		imgCheckoutDocuments.setTitle(Main.i18n("user.info.checkout.actual"));
		imgSubscriptions.setTitle(Main.i18n("user.info.subscription.actual"));
		imgNewsDocuments.setTitle(Main.i18n("user.info.news.new"));
		imgWorkflowTasks.setTitle(Main.i18n("user.info.workflow.pending.tasks"));
		imgWorkflowPooledTasks.setTitle(Main.i18n("user.info.workflow.pending.pooled.tasks"));
		quotaUsed.setHTML(percent + "%");

		// Resfreshing actual chatrooms
		for (ChatRoomDialogBox chatRoomDialogBox : chatRoomList) {
			chatRoomDialogBox.langRefresh();
		}
	}

	/**
	 * refreshConnectedUsers
	 */
	private void refreshConnectedUsers() {
		getLoggedUsers = true;

		if (chatConnected) {
			chatService.getLoggedUsers(new AsyncCallback<List<GWTUser>>() {
				@Override
				public void onSuccess(List<GWTUser> result) {
					connectUsersList = result;
					usersConnected.setHTML(connectUsersList.size() + "");
					getLoggedUsers = false;

					new Timer() {
						@Override
						public void run() {
							refreshConnectedUsers();
						}
					}.schedule(USERS_IN_ROOM_REFRESHING_TIME);
				}

				@Override
				public void onFailure(Throwable caught) {
					Log.error(UserInfo.class + ".refreshConnectedUsers().onFailure(" + caught + ")");
					getLoggedUsers = false;

					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {
						new Timer() {
							@Override
							public void run() {
								refreshConnectedUsers();
							}
						}.schedule(USERS_IN_ROOM_REFRESHING_TIME);
					} else {
						Main.get().showError("UserInfo.refreshConnectedUsers", caught);
					}
				}
			});
		} else {
			getLoggedUsers = false;
		}
	}

	/**
	 * getPendingChatRoomUser
	 */
	private void getPendingChatRoomUser() {
		getPendingChatRoomUser = true;

		if (chatConnected) {
			chatService.getPendingChatRoomUser(new AsyncCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> result) {
					for (String room : result) {
						ChatRoomPopup chatRoomPopup = new ChatRoomPopup("", room);
						chatRoomPopup.center();
						chatRoomPopup.getPendingMessage(room);
						addChatRoom(chatRoomPopup);
					}

					getPendingChatRoomUser = false;

					new Timer() {
						@Override
						public void run() {
							getPendingChatRoomUser();
						}
					}.schedule(NEW_ROOM_REFRESHING_TIME);
				}

				@Override
				public void onFailure(Throwable caught) {
					Log.error(UserInfo.class + ".getPendingChatRoomUser().onFailure(" + caught + ")");
					getPendingChatRoomUser = false;

					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {
						new Timer() {
							@Override
							public void run() {
								getPendingChatRoomUser();
							}
						}.schedule(NEW_ROOM_REFRESHING_TIME);
					} else {
						Main.get().showError("UserInfo.getPendingChatRoomUser", caught);
					}
				}
			});
		} else {
			getPendingChatRoomUser = false;
		}
	}

	/**
	 * getConnectedUserList
	 */
	public List<GWTUser> getConnectedUserList() {
		return connectUsersList;
	}

	/**
	 * addChatRoom
	 */
	public void addChatRoom(ChatRoomDialogBox chatRoom) {
		if (!chatRoomList.contains(chatRoom)) {
			chatRoomList.add(chatRoom);
		}
	}

	/**
	 * removeChatRoom
	 */
	public void removeChatRoom(ChatRoomDialogBox chatRoom) {
		if (chatRoomList.contains(chatRoom)) {
			chatRoomList.remove(chatRoom);
		}
	}

	/**
	 * isConnectedToChat
	 */
	public boolean isConnectedToChat() {
		return chatConnected;
	}

	/**
	 * getChatRoomList
	 */
	public List<ChatRoomDialogBox> getChatRoomList() {
		return chatRoomList;
	}

	/**
	 * Used before logout ( in logout popup is made disconnection )
	 */
	public void disconnectChat() {
		chatConnected = false;
		usersConnected.setVisible(false);
		imgNewChatRoom.setVisible(false);
		usersConnected.setHTML("");
		imgChat.setResource(OKMBundleResources.INSTANCE.chatDisconnected());
	}

	/**
	 * Recursivelly disconnecting chat rooms and chat before login out
	 */
	public void logoutChat() {
		Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("chat.logout"));
		// Disconnect rooms
		if (getChatRoomList().size() > 0) {
			final ChatRoomDialogBox chatRoom = getChatRoomList().get(0);
			chatRoom.setChatRoomActive(false);
			chatService.closeRoom(chatRoom.getRoom(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object arg0) {
					removeChatRoom(chatRoom);
					logoutChat(); // Recursive call
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("CloseRoom", caught);

					// If happens some problem always we try continue disconnecting chat rooms
					removeChatRoom(chatRoom);
					logoutChat(); // Recursive call
				}
			});
		} else {
			realLogoutChat();
		}
	}

	/**
	 * realLogoutChat
	 */
	private void realLogoutChat() {
		disconnectChat(); // Only used to change view and disabling some RPC
		logoutDone = false;
		Timer timer = new Timer() {
			@Override
			public void run() {
				if (!getLoggedUsers && !getPendingChatRoomUser) {
					chatService.logout(new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							logoutDone = true;
							Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("chat.disconnected"));
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("GetLogoutChat", caught);
						}
					});
				} else {
					realLogoutChat();
				}
			}
		};

		timer.schedule(100); // Waiting for other RPC calls;
	}

	/**
	 * enableChat
	 */
	public void enableChat() {
		imgChat.setVisible(true);
		imgChatSeparator.setVisible(true);
	}

	/**
	 * enableUserQuota
	 */
	public void enableUserQuota(long quotaLimit) {
		this.quotaLimit = quotaLimit;
		imgUserQuota.setVisible(true);
		quotaUsed.setVisible(true);
		userQuota = true;
	}

	/**
	 * loginChat
	 */
	public void loginChat(final boolean autologin) {
		if (!autologin) {
			Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("chat.login"));
		}
		chatService.getLoggedUsers(new AsyncCallback<List<GWTUser>>() {
			@Override
			public void onSuccess(List<GWTUser> result) {
				boolean logged = false;
				for (GWTUser user : result) {
					if (user.getId().equals(Main.get().workspaceUserProperties.getUser().getId())) {
						logged = true;
						break;
					}
				}

				if (logged) {
					if (!autologin) {
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_FORCE_CHAT_LOGIN);
						Main.get().confirmPopup.show();
					}
				} else {
					chatService.login(new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							chatConnected = true;
							imgChat.setResource(OKMBundleResources.INSTANCE.chatConnected());
							imgChat.setTitle(Main.i18n("user.info.chat.disconnect"));
							usersConnected.setVisible(true);
							imgNewChatRoom.setVisible(true);
							refreshConnectedUsers();
							getPendingChatRoomUser();
							if (!autologin) {
								Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("chat.connected"));
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("GetLoginChat", caught);
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getLoggedUsers", caught);
			}
		});
	}

	/**
	 * isQuotaExceed
	 */
	public boolean isQuotaExceed() {
		return quotaExceeded;
	}

	/**
	 * showDashboardUser
	 */
	public void showDashboardUserIcons() {
		imgLockedDocuments.setVisible(true);
		imgCheckoutDocuments.setVisible(true);
		imgSubscriptions.setVisible(true);
		lockedDocuments.setVisible(true);
		checkoutDocuments.setVisible(true);
		subscriptions.setVisible(true);
	}

	/**
	 * showNews
	 */
	public void showDashboardNewsIcons() {
		imgNewsDocuments.setVisible(true);
		newDocuments.setVisible(true);
	}

	/**
	 * showWorkflow
	 */
	public void showDashboardWorkflowIcons() {
		imgWorkflowTasks.setVisible(true);
		imgWorkflowPooledTasks.setVisible(true);
		newWorkflowTasks.setVisible(true);
		newWorkflowPooledTasks.setVisible(true);
	}

	/**
	 * showExtensions
	 */
	public void showExtensions() {
		if (widgetExtensionList.size() > 0) {
			panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
			panel.add(new HTML("&nbsp;"));
			for (UserInfoExtension extension : widgetExtensionList) {
				panel.add(extension);
				panel.add(new HTML("&nbsp;"));
			}
		}
	}

	/**
	 * addUserInfoExtension
	 */
	public void addUserInfoExtension(UserInfoExtension extension) {
		widgetExtensionList.add(extension);
	}

	/**
	 * isPendingToClose
	 *
	 * @return
	 */
	public boolean isPendingToClose() {
		return (chatConnected || !logoutDone);
	}

	/**
	 * forceLogin
	 */
	public void forceLogin() {
		chatService.logout(new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				loginChat(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("logout", caught);
			}
		});
	}
}
