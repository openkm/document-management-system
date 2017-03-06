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

package com.openkm.extension.frontend.client.widget.forum;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * DashboardToolbarForum
 *
 * @author jllort
 *
 */
public class DashboardToolbarForum extends Composite {

	public static final int MODE_FORUM = 0;
	public static final int MODE_CREATE_FORUM = 1;
	public static final int MODE_TOPIC = 2;
	public static final int MODE_CREATE_TOPIC = 3;
	public static final int MODE_POST = 4;
	public static final int MODE_CREATE_POST = 5;
	public static final int MODE_EDIT_POST = 6;
	public static final int MODE_EDIT_FORUM = 7;
	public static final int MODE_EDIT_TOPIC = 8;

	private HorizontalPanel hPanel = new HorizontalPanel();

	private Button newForum;
	private Button newTopic;
	private Button postReply;
	private HTML createForum;
	private HTML editForum;
	private HTML createTopic;
	private HTML createPost;
	private HTML editPost;
	private HorizontalPanel verticalLine;
	HorizontalPanel topicPanel;
	private HTML numberOfTopicsText;
	private HTML numberOfTopics;
	HorizontalPanel postPanel;
	private HTML numberOfRepliesText;
	private HTML numberOfReplies;
	private HTML home;
	private HTML linkSeparator;
	private Anchor forumLink;
	private HTML linkSeparator2;
	private Anchor topicLink;

	private long forumId = 0;
	private String forumName = "";
	private long topicId = 0;
	private String topicName = "";

	/**
	 * DashboardToolbarForum
	 *
	 * @param controller
	 */
	public DashboardToolbarForum(final ForumController controller) {
		hPanel = new HorizontalPanel();
		HorizontalPanel buttonsPanel = new HorizontalPanel();

		// Left Space
		HTML space = new HTML("&nbsp;");
		hPanel.add(space);

		// New forum
		newForum = new Button(GeneralComunicator.i18nExtension("forum.new.forum"));
		newForum.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.createForum();
			}
		});
		newForum.setStyleName("okm-AddButton");
		buttonsPanel.add(newForum);
		buttonsPanel.setCellVerticalAlignment(newForum, HasAlignment.ALIGN_MIDDLE);

		// New topic
		newTopic = new Button(GeneralComunicator.i18nExtension("forum.new.topic"));
		newTopic.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.createNewTopic(forumId, forumName);
			}
		});
		newTopic.setStyleName("okm-AddButton");
		buttonsPanel.add(newTopic);
		buttonsPanel.setCellVerticalAlignment(newTopic, HasAlignment.ALIGN_MIDDLE);

		// Post reply
		postReply = new Button(GeneralComunicator.i18nExtension("forum.post.reply"));
		postReply.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.createNewPost();
			}
		});
		postReply.setStyleName("okm-AddButton");
		postReply.setVisible(false);
		buttonsPanel.add(postReply);
		buttonsPanel.setCellVerticalAlignment(postReply, HasAlignment.ALIGN_MIDDLE);

		// Action
		HorizontalPanel actions = new HorizontalPanel();
		createForum = new HTML(GeneralComunicator.i18nExtension("forum.create.forum").toUpperCase());
		editForum = new HTML(GeneralComunicator.i18nExtension("forum.edit.forum").toUpperCase());
		createTopic = new HTML(GeneralComunicator.i18nExtension("forum.create.topic").toUpperCase());
		createPost = new HTML(GeneralComunicator.i18nExtension("forum.create.post").toUpperCase());
		editPost = new HTML(GeneralComunicator.i18nExtension("forum.edit.post").toUpperCase());
		createForum.setStyleName("okm-Forum-Topic");
		editForum.setStyleName("okm-Forum-Topic");
		createTopic.setStyleName("okm-Forum-Topic");
		createPost.setStyleName("okm-Forum-Topic");
		editPost.setStyleName("okm-Forum-Topic");
		actions.add(createForum);
		actions.add(editForum);
		actions.add(createTopic);
		actions.add(createPost);
		actions.add(editPost);
		buttonsPanel.add(actions);

		// Vertical line
		verticalLine = new HorizontalPanel();
		HTML vertical = new HTML("&nbsp;");
		vertical.setHeight("24px");
		vertical.setStyleName("okm-Border-Right");
		verticalLine.add(vertical);
		buttonsPanel.add(verticalLine);

		// Navigator
		HorizontalPanel navigator = new HorizontalPanel();
		// Home
		home = new HTML("<b>" + GeneralComunicator.i18nExtension("forum.go.home") + "</b>");
		home.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.goHome();
			}
		});
		home.setStyleName("okm-Hyperlink");
		linkSeparator = new HTML("&nbsp;&raquo;&nbsp;");
		forumLink = new Anchor();
		forumLink.setStyleName("okm-Hyperlink");
		forumLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.refreshTopics(forumId, forumName);
			}
		});
		linkSeparator2 = new HTML("&nbsp;&raquo;&nbsp;");
		topicLink = new Anchor();
		topicLink.setStyleName("okm-Hyperlink");
		topicLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.refreshPosts(topicId, topicName);
			}
		});
		navigator.add(new HTML("&nbsp;"));
		navigator.add(home);
		navigator.add(linkSeparator);
		navigator.add(forumLink);
		navigator.add(linkSeparator2);
		navigator.add(topicLink);
		buttonsPanel.add(navigator);

		// Adding buttons panel
		hPanel.add(buttonsPanel);

		// Topics panel
		topicPanel = new HorizontalPanel();
		numberOfTopicsText = new HTML(GeneralComunicator.i18nExtension("forum.number.of.topics"));
		numberOfTopics = new HTML("");
		topicPanel.add(numberOfTopicsText);
		topicPanel.add(new HTML("&nbsp;"));
		topicPanel.add(numberOfTopics);
		hPanel.add(topicPanel);

		// Posts panel
		postPanel = new HorizontalPanel();
		numberOfRepliesText = new HTML(GeneralComunicator.i18nExtension("forum.number.of.replies"));
		numberOfReplies = new HTML("");
		postPanel.add(numberOfRepliesText);
		postPanel.add(new HTML("&nbsp;"));
		postPanel.add(numberOfReplies);
		hPanel.add(postPanel);

		// Right Space
		HTML space2 = new HTML("&nbsp;");
		hPanel.add(space2);

		buttonsPanel.setCellVerticalAlignment(actions, HasAlignment.ALIGN_MIDDLE);
		buttonsPanel.setCellVerticalAlignment(navigator, HasAlignment.ALIGN_MIDDLE);

		hPanel.setCellHorizontalAlignment(buttonsPanel, HasAlignment.ALIGN_LEFT);
		hPanel.setCellVerticalAlignment(buttonsPanel, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(topicPanel, HasAlignment.ALIGN_RIGHT);
		hPanel.setCellHorizontalAlignment(postPanel, HasAlignment.ALIGN_RIGHT);
		hPanel.setCellVerticalAlignment(topicPanel, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(postPanel, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellWidth(space, "5px");
		hPanel.setCellWidth(space2, "5px");

		hPanel.setStyleName("okm-Mail");
		hPanel.addStyleName("okm-Border-Bottom");

		initWidget(hPanel);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		home.setHTML("<b>" + GeneralComunicator.i18nExtension("forum.go.home") + "</b>");
		newForum.setHTML(GeneralComunicator.i18nExtension("forum.new.forum"));
		newTopic.setHTML(GeneralComunicator.i18nExtension("forum.new.topic"));
		postReply.setHTML(GeneralComunicator.i18nExtension("forum.post.reply"));
		numberOfTopicsText.setHTML(GeneralComunicator.i18nExtension("forum.number.of.topics"));
		numberOfRepliesText.setHTML(GeneralComunicator.i18nExtension("forum.number.of.replies"));
		createForum.setHTML(GeneralComunicator.i18nExtension("forum.create.forum").toUpperCase());
		editForum.setHTML(GeneralComunicator.i18nExtension("forum.edit.forum").toUpperCase());
		createTopic.setHTML(GeneralComunicator.i18nExtension("forum.create.topic").toUpperCase());
		createPost.setHTML(GeneralComunicator.i18nExtension("forum.create.post").toUpperCase());
		editPost.setHTML(GeneralComunicator.i18nExtension("forum.edit.post").toUpperCase());
	}

	/**
	 * switchViewMode
	 *
	 * @param mode
	 */
	public void switchViewMode(int mode) {
		switch (mode) {
			case MODE_FORUM:
				topicPanel.setVisible(false);
				postPanel.setVisible(false);
				newForum.setVisible(true && GeneralComunicator.getWorkspace().isAdminRole());
				newTopic.setVisible(false);
				postReply.setVisible(false);
				createForum.setVisible(false);
				editForum.setVisible(false);
				createTopic.setVisible(false);
				createPost.setVisible(false);
				editPost.setVisible(false);
				verticalLine.setVisible(true);
				home.setVisible(false);
				linkSeparator.setVisible(false);
				forumLink.setVisible(false);
				linkSeparator2.setVisible(false);
				topicLink.setVisible(false);

				break;

			case MODE_CREATE_FORUM:
				topicPanel.setVisible(false);
				postPanel.setVisible(false);
				newForum.setVisible(false);
				newTopic.setVisible(false);
				postReply.setVisible(false);
				createForum.setVisible(true);
				editForum.setVisible(false);
				createTopic.setVisible(false);
				createPost.setVisible(false);
				editPost.setVisible(false);
				verticalLine.setVisible(true);
				home.setVisible(false);
				linkSeparator.setVisible(false);
				forumLink.setVisible(false);
				linkSeparator2.setVisible(false);
				topicLink.setVisible(false);
				break;

			case MODE_EDIT_FORUM:
				topicPanel.setVisible(false);
				postPanel.setVisible(false);
				newForum.setVisible(false);
				newTopic.setVisible(false);
				postReply.setVisible(false);
				createForum.setVisible(false);
				editForum.setVisible(true);
				createTopic.setVisible(false);
				createPost.setVisible(false);
				editPost.setVisible(false);
				verticalLine.setVisible(true);
				home.setVisible(false);
				linkSeparator.setVisible(false);
				forumLink.setVisible(false);
				linkSeparator2.setVisible(false);
				topicLink.setVisible(false);
				break;

			case MODE_TOPIC:
				topicPanel.setVisible(true);
				postPanel.setVisible(false);
				newForum.setVisible(false);
				newTopic.setVisible(true);
				postReply.setVisible(false);
				createForum.setVisible(false);
				editForum.setVisible(false);
				createTopic.setVisible(false);
				createPost.setVisible(false);
				editPost.setVisible(false);
				verticalLine.setVisible(true);
				home.setVisible(true);
				linkSeparator.setVisible(true);
				forumLink.setVisible(true);
				linkSeparator2.setVisible(false);
				topicLink.setVisible(false);
				break;

			case MODE_CREATE_TOPIC:
				topicPanel.setVisible(false);
				postPanel.setVisible(false);
				newForum.setVisible(false);
				newTopic.setVisible(false);
				postReply.setVisible(false);
				createForum.setVisible(false);
				editForum.setVisible(false);
				createTopic.setVisible(true);
				createPost.setVisible(false);
				editPost.setVisible(false);
				verticalLine.setVisible(true);
				home.setVisible(true);
				linkSeparator.setVisible(true);
				forumLink.setVisible(true);
				linkSeparator2.setVisible(false);
				topicLink.setVisible(false);
				break;

			case MODE_POST:
				topicPanel.setVisible(false);
				postPanel.setVisible(true);
				newForum.setVisible(false);
				newTopic.setVisible(false);
				postReply.setVisible(true);
				createForum.setVisible(false);
				editForum.setVisible(false);
				createTopic.setVisible(false);
				createPost.setVisible(false);
				editPost.setVisible(false);
				verticalLine.setVisible(true);
				home.setVisible(true);
				linkSeparator.setVisible(true);
				forumLink.setVisible(true);
				linkSeparator2.setVisible(true);
				topicLink.setVisible(true);
				break;

			case MODE_CREATE_POST:
				topicPanel.setVisible(false);
				postPanel.setVisible(false);
				newForum.setVisible(false);
				newTopic.setVisible(false);
				postReply.setVisible(false);
				createForum.setVisible(false);
				editForum.setVisible(false);
				createTopic.setVisible(false);
				createPost.setVisible(true);
				editPost.setVisible(false);
				verticalLine.setVisible(true);
				home.setVisible(true);
				linkSeparator.setVisible(true);
				forumLink.setVisible(true);
				linkSeparator2.setVisible(true);
				topicLink.setVisible(true);
				break;

			case MODE_EDIT_POST:
			case MODE_EDIT_TOPIC:
				topicPanel.setVisible(false);
				postPanel.setVisible(false);
				newForum.setVisible(false);
				newTopic.setVisible(false);
				postReply.setVisible(false);
				createForum.setVisible(false);
				editForum.setVisible(false);
				createTopic.setVisible(false);
				createPost.setVisible(false);
				editPost.setVisible(true);
				verticalLine.setVisible(true);
				home.setVisible(true);
				linkSeparator.setVisible(true);
				forumLink.setVisible(true);
				linkSeparator2.setVisible(true);
				topicLink.setVisible(true);
				break;
		}
	}

	/**
	 * setForumId
	 */
	public void setForumId(final long forumId, final String forumName) {
		this.forumId = forumId;
		this.forumName = forumName;
		forumLink.setHTML("<b>" + forumName + "</b>");
	}

	/**
	 * setTopicId
	 */
	public void setTopicId(final long topicId, final String topicName) {
		this.topicId = topicId;
		this.topicName = topicName;
		topicLink.setHTML("<b>" + topicName + "</b>");
	}

	/**
	 * setNumberOfTopics
	 */
	public void setNumberOfTopics(int number) {
		numberOfTopics.setHTML("" + number);
	}

	/**
	 * setNumberOfReplies
	 */
	public void setNumberOfReplies(int number) {
		numberOfReplies.setHTML("" + number);
	}
}