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

package com.openkm.extension.frontend.client.widget.forum;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMForumService;
import com.openkm.extension.frontend.client.service.OKMForumServiceAsync;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.WorkspaceComunicator;

/**
 * PostEditor
 *
 * @author jllort
 */
public class PostEditor extends Composite {
	private final OKMForumServiceAsync forumService = (OKMForumServiceAsync) GWT.create(OKMForumService.class);

	public static final int NONE = 0;
	public static final int CREATE_TOPIC = 1;
	public static final int CREATE_POST = 2;
	public static final int EDIT_POST = 3;
	public static final int EDIT_TOPIC = 4;

	private VerticalPanel vPanel;
	private TextBox title;
	private TextArea textArea;
	private Button createButton;
	private Button acceptButton;
	private Button cancelButton;
	private int action = NONE;
	private long forumId = ForumScrollTable.FORUM_DOCUMENT_DISCUSSION_ID;
	private String forumName = "";
	private long topicId = 0;
	private String topicName = "";
	private HTML subject;
	private GWTForumPost post;
	private ForumToolBarEditor toolbar;

	/**
	 * TopicEditor
	 */
	public PostEditor(final ForumController controller) {
		SimplePanel sp = new SimplePanel();
		vPanel = new VerticalPanel();
		sp.add(vPanel);

		// Space
		HTML space = new HTML("");
		vPanel.add(space);

		// Subject
		subject = new HTML(GeneralComunicator.i18nExtension("forum.subject"));
		title = new TextBox();
		title.setWidth("250px");
		title.setStyleName("okm-Input");
		title.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				switch (action) {
					case CREATE_TOPIC:
						createButton.setEnabled(title.getText().length() > 0);
					case CREATE_POST:
						break;
					case EDIT_POST:
					case EDIT_TOPIC:
						acceptButton.setEnabled(title.getText().length() > 0);
						break;
				}
			}
		});
		HorizontalPanel titlePanel = new HorizontalPanel();
		HTML titleLeftSpace = new HTML("&nbsp;");
		titlePanel.add(titleLeftSpace);
		titlePanel.add(subject);
		titlePanel.add(new HTML("&nbsp;"));
		titlePanel.add(title);
		titlePanel.setCellWidth(titleLeftSpace, "5px");
		titlePanel.setCellVerticalAlignment(subject, HasAlignment.ALIGN_MIDDLE);
		vPanel.add(titlePanel);

		// Space
		HTML space2 = new HTML("");
		vPanel.add(space2);

		// TextArea
		textArea = new TextArea();
		toolbar = new ForumToolBarEditor(textArea);
		textArea.setSize("700px", "200px");
		textArea.setStyleName("okm-TextArea");
		textArea.addStyleName("okm-EnableSelect");
		HorizontalPanel textAreaPanel = new HorizontalPanel();
		HTML textAreLeftSpace = new HTML("&nbsp;");
		textAreaPanel.add(textAreLeftSpace);
		VerticalPanel editorPanel = new VerticalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space5 = new HTML();
		hPanel.add(textArea);
		hPanel.add(space5);
		Widget smilesPanel = toolbar.getSmilesPanel();
		hPanel.add(smilesPanel);
		hPanel.setCellVerticalAlignment(textArea, HasAlignment.ALIGN_TOP);
		hPanel.setCellVerticalAlignment(smilesPanel, HasAlignment.ALIGN_TOP);
		hPanel.setCellWidth(space5, "5px");
		editorPanel.add(toolbar.getColorPanel());
		editorPanel.add(toolbar);
		editorPanel.add(hPanel);
		textAreaPanel.add(editorPanel);
		textAreaPanel.setCellWidth(textAreLeftSpace, "5px");
		vPanel.add(textAreaPanel);

		// Space
		HTML space3 = new HTML("&nbsp;");
		vPanel.add(space3);

		// Create
		createButton = new Button(GeneralComunicator.i18n("button.create"));
		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (action) {
					case CREATE_TOPIC:
						action = NONE;
						GWTForumTopic topic = new GWTForumTopic();
						topic.setTitle(title.getText());
						post = new GWTForumPost();
						post.setSubject(title.getText());
						post.setMessage(textArea.getText());
						topic.getPosts().add(post);
						String uuid = null;
						if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
							uuid = Forum.get().getUuid();
						}
						Forum.get().status.setCreateTopic();
						forumService.createTopic(forumId, uuid, topic, new AsyncCallback<GWTForumTopic>() {
							@Override
							public void onSuccess(GWTForumTopic result) {
								controller.newTopicCreated(forumId, result);
								Forum.get().status.unsetCreateTopic();
							}

							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("createTopic", caught);
								Forum.get().status.unsetCreateTopic();
							}
						});
						break;

					case CREATE_POST:
						action = NONE;
						post = new GWTForumPost();
						post.setSubject(title.getText());
						post.setMessage(textArea.getText());
						Forum.get().status.setCreatePost();
						forumService.createPost(forumId, topicId, post, new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								controller.refreshPosts(topicId, topicName);
								Forum.get().status.unsetCreatePost();
							}

							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("createPost", caught);
								Forum.get().status.unsetCreatePost();
							}
						});
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}

			;
		});
		createButton.setStyleName("okm-AddButton");

		// Cancel
		cancelButton = new Button(GeneralComunicator.i18n("button.cancel"));
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (action) {
					case CREATE_TOPIC:
						action = NONE;
						if (WorkspaceComunicator.getSelectedWorkspace() == UIDockPanelConstants.DESKTOP) {
							controller.goHome();
						} else {
							controller.refreshTopics(forumId, forumName);
						}

						break;

					case CREATE_POST:
					case EDIT_POST:
					case EDIT_TOPIC:
						action = NONE;
						controller.refreshPosts(topicId, topicName);
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}
		});
		cancelButton.setStyleName("okm-NoButton");

		// update
		acceptButton = new Button(GeneralComunicator.i18n("button.accept"));
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				post.setSubject(title.getText());
				post.setMessage(textArea.getText());
				switch (action) {
					case EDIT_POST:
						action = NONE;
						Forum.get().status.setCreatePost();
						forumService.updatePost(post, new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								controller.refreshPosts(topicId, topicName);
								Forum.get().status.unsetCreatePost();
							}

							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("createPost", caught);
								Forum.get().status.unsetCreatePost();
							}
						});
						break;
					case EDIT_TOPIC:
						action = NONE;
						Forum.get().status.setUpdateTopic();
						forumService.updateTopic(topicId, post, new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								controller.refreshPosts(topicId, post.getSubject());
								Forum.get().status.unsetUpdateTopic();
							}

							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("updateTopic", caught);
								Forum.get().status.unsetUpdateTopic();
							}
						});
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}
		});
		acceptButton.setStyleName("okm-YesButton");

		// Button panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(new HTML("&nbsp;"));
		buttonPanel.add(cancelButton);
		buttonPanel.add(new HTML("&nbsp;"));
		buttonPanel.add(createButton);
		buttonPanel.add(acceptButton);
		vPanel.add(buttonPanel);

		// Space
		HTML space4 = new HTML("");
		vPanel.add(space4);

		vPanel.setCellHorizontalAlignment(buttonPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(space, "5px");
		vPanel.setCellHeight(space2, "5px");
		vPanel.setCellHeight(space3, "5px");
		vPanel.setCellHeight(space4, "5px");

		sp.setWidth("100%");

		initWidget(sp);
	}

	/**
	 * setEditorSize
	 *
	 * @param width
	 */
	public void setEditorSize(int width) {
		if (width - (ForumToolBarEditor.SMILES_TABLE_WIDTH + 45) > 700) {
			textArea.setSize("" + (width - (ForumToolBarEditor.SMILES_TABLE_WIDTH + 45)) + "px", "200px");
		} else {
			textArea.setSize("700px", "200px");
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		subject.setHTML(GeneralComunicator.i18nExtension("forum.subject"));
		createButton.setHTML(GeneralComunicator.i18n("button.create"));
		cancelButton.setHTML(GeneralComunicator.i18n("button.cancel"));
		acceptButton.setHTML(GeneralComunicator.i18n("button.accept"));
		toolbar.langRefresh();
	}

	/**
	 * reset
	 */
	public void reset() {
		title.setText("");
		textArea.setText("");
	}

	/**
	 * setPostTitle
	 *
	 * @param postTitle
	 */
	public void setPostTitle(String postTitle) {
		title.setText(postTitle);
	}

	/**
	 * setAction
	 *
	 * @param action The action
	 */
	public void setAction(int action) {
		this.action = action;
		switch (action) {
			case CREATE_TOPIC:
			case CREATE_POST:
				createButton.setEnabled(title.getText().length() > 0); // By default disables creation button
				createButton.setVisible(true);
				acceptButton.setVisible(false);
				break;
			case EDIT_POST:
			case EDIT_TOPIC:
				acceptButton.setEnabled(true); // By default is always enabled
				createButton.setVisible(false);
				acceptButton.setVisible(true);
				break;
		}
	}

	/**
	 * setPost
	 */
	public void setPost(GWTForumPost post) {
		this.post = post;
		title.setText(post.getSubject());
		textArea.setText(post.getMessage());
	}

	/**
	 * setMessage
	 */
	public void setMessage(String message) {
		textArea.setText(message);
	}

	/**
	 * setForum
	 */
	public void setForum(long id, String forumName) {
		this.forumId = id;
		this.forumName = forumName;
	}

	/**
	 * setTopic
	 */
	public void setTopic(long id, String topicName) {
		this.topicId = id;
		this.topicName = topicName;
	}
}