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

import com.google.gwt.core.client.GWT;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.service.OKMForumService;
import com.openkm.extension.frontend.client.service.OKMForumServiceAsync;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.Util;

import java.util.List;

/**
 * ForumManager
 *
 * @author jllort
 */
public class ForumManager extends Composite implements ForumController {
	private final OKMForumServiceAsync forumService = (OKMForumServiceAsync) GWT.create(OKMForumService.class);
	private final static int IE_SIZE_RECTIFICATION = (Util.getUserAgent().startsWith("ie") ? 2 : 0);

	// Toolbar height
	public static final int TOOLBAR_HEADER = 25;

	// Number of columns
	public static final int NUMBER_OF_COLUMNS = 4;

	private VerticalPanel vPanel;
	private DashboardToolbarForum toolbar;

	// Forum list
	private ForumScrollTable forumTable;
	private FixedWidthFlexTable forumHeaderTable;
	private FixedWidthGrid forumDataTable;

	// Topic list
	private TopicScrollTable topicTable;
	private FixedWidthFlexTable topicHeaderTable;
	private FixedWidthGrid topicDataTable;

	// Editor & Post  
	private ScrollPanel scrollPanelPost;
	private VerticalPanel vPostPanel;
	private ForumEditor forumEditor;
	private PostEditor postEditor;
	private Post post;

	// identifiers
	private long forumId = 0;
	private long topicId = 0;
	private String forumName = "";
	private String topicName = "";

	/**
	 * ForumManager
	 */
	public ForumManager() {
		vPanel = new VerticalPanel();
		toolbar = new DashboardToolbarForum(this);

		// Forum
		forumHeaderTable = new FixedWidthFlexTable();
		forumDataTable = new FixedWidthGrid();
		forumTable = new ForumScrollTable(forumDataTable, forumHeaderTable, new TableImages(), this);
		forumTable.setCellSpacing(0);
		forumTable.setCellPadding(2);
		forumTable.setSize("740px", "140px");
		forumDataTable.removeStyleName("dataTable"); // removed to not show selected row

		// Level 1 headers
		forumHeaderTable.setHTML(0, 0, GeneralComunicator.i18nExtension("forum.title"));
		forumHeaderTable.setHTML(0, 1, GeneralComunicator.i18nExtension("forum.topic"));
		forumHeaderTable.setHTML(0, 2, GeneralComunicator.i18nExtension("forum.post"));
		forumHeaderTable.setHTML(0, 3, GeneralComunicator.i18nExtension("forum.last.post"));

		// Format
		forumTable.setColumnWidth(0, 500);
		forumTable.setColumnWidth(1, 80);
		forumTable.setColumnWidth(2, 80);
		forumTable.setColumnWidth(3, 170);

		forumTable.setPreferredColumnWidth(0, 500);
		forumTable.setPreferredColumnWidth(1, 80);
		forumTable.setPreferredColumnWidth(2, 80);
		forumTable.setPreferredColumnWidth(3, 170);

		forumTable.setColumnSortable(0, false);
		forumTable.setColumnSortable(1, false);
		forumTable.setColumnSortable(2, false);
		forumTable.setColumnSortable(3, false);


		// Topic
		topicHeaderTable = new FixedWidthFlexTable();
		topicDataTable = new FixedWidthGrid();
		topicTable = new TopicScrollTable(topicDataTable, topicHeaderTable, new TableImages(), this);
		topicTable.setCellSpacing(0);
		topicTable.setCellPadding(2);
		topicTable.setSize("740px", "140px");
		topicDataTable.removeStyleName("dataTable"); // removed to not show selected row

		// Level 1 headers
		topicHeaderTable.setHTML(0, 0, GeneralComunicator.i18nExtension("forum.topics"));
		topicHeaderTable.setHTML(0, 1, GeneralComunicator.i18nExtension("forum.replies"));
		topicHeaderTable.setHTML(0, 2, GeneralComunicator.i18nExtension("forum.views"));
		topicHeaderTable.setHTML(0, 3, GeneralComunicator.i18nExtension("forum.last.post"));

		// Format
		topicTable.setColumnWidth(0, 500);
		topicTable.setColumnWidth(1, 80);
		topicTable.setColumnWidth(2, 80);
		topicTable.setColumnWidth(3, 170);

		topicTable.setPreferredColumnWidth(0, 500);
		topicTable.setPreferredColumnWidth(1, 80);
		topicTable.setPreferredColumnWidth(2, 80);
		topicTable.setPreferredColumnWidth(3, 170);

		topicTable.setColumnSortable(0, false);
		topicTable.setColumnSortable(1, false);
		topicTable.setColumnSortable(2, false);
		topicTable.setColumnSortable(3, false);

		// Editors
		vPostPanel = new VerticalPanel();
		forumEditor = new ForumEditor(this);
		forumEditor.setStyleName("okm-Mail");
		postEditor = new PostEditor(this);
		postEditor.setStyleName("okm-Mail");
		post = new Post(this);
		scrollPanelPost = new ScrollPanel(vPostPanel);

		vPanel.add(toolbar); //always visible

		toolbar.setHeight("" + TOOLBAR_HEADER + "px");
		toolbar.setWidth("100%");
		vPanel.setCellHeight(toolbar, "" + TOOLBAR_HEADER + "px");

		vPostPanel.setWidth("100%");
		forumEditor.setWidth("100%");
		postEditor.setWidth("100%");

		initWidget(vPanel);
	}

	/**
	 * hasWidget
	 *
	 * @param widget
	 * @return
	 */
	private boolean hasWidget(Widget widget) {
		return (vPanel.getWidgetIndex(widget) >= 0);
	}

	/**
	 * resetVPanel
	 */
	private void resetVPanel() {
		while (vPanel.getWidgetCount() > 1) {
			vPanel.remove(1);
		}
	}

	/**
	 * resetPostPanel
	 */
	private void resetPostPanel() {
		while (vPostPanel.getWidgetCount() > 0) {
			vPostPanel.remove(0);
		}
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		vPanel.setPixelSize(width, height);
		forumTable.setPixelSize(width, height - (TOOLBAR_HEADER + IE_SIZE_RECTIFICATION));
		forumTable.fillWidth();
		topicTable.setPixelSize(width, height - (TOOLBAR_HEADER + IE_SIZE_RECTIFICATION));
		topicTable.fillWidth();
		scrollPanelPost.setPixelSize(width, height - (TOOLBAR_HEADER + IE_SIZE_RECTIFICATION));
		forumEditor.setEditorSize(width);
		postEditor.setEditorSize(width);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		// Level 1 headers
		forumHeaderTable.setHTML(0, 0, GeneralComunicator.i18nExtension("forum.title"));
		forumHeaderTable.setHTML(0, 1, GeneralComunicator.i18nExtension("forum.topic"));
		forumHeaderTable.setHTML(0, 2, GeneralComunicator.i18nExtension("forum.post"));
		forumHeaderTable.setHTML(0, 3, GeneralComunicator.i18nExtension("forum.last.post"));
		topicHeaderTable.setHTML(0, 0, GeneralComunicator.i18nExtension("forum.topics"));
		topicHeaderTable.setHTML(0, 1, GeneralComunicator.i18nExtension("forum.replies"));
		topicHeaderTable.setHTML(0, 2, GeneralComunicator.i18nExtension("forum.views"));
		topicHeaderTable.setHTML(0, 3, GeneralComunicator.i18nExtension("forum.last.post"));
		toolbar.langRefresh();
		postEditor.langRefresh();
		forumEditor.langRefresh();
		if (hasWidget(forumTable)) {
			refreshForums();
		}
		if (hasWidget(topicTable) && forumId != 0) {
			refreshTopics(forumId, forumName);
		}
		if (hasWidget(post) && topicId != 0) {
			refreshPosts(topicId, topicName);
		}
	}

	/**
	 * Removes all rows except the first
	 */
	private void removeAllForumRows() {
		// Purge all rows 
		while (forumDataTable.getRowCount() > 0) {
			forumDataTable.removeRow(0);
		}

		forumTable.getDataTable().resize(0, NUMBER_OF_COLUMNS);
	}

	/**
	 * Removes all rows except the first
	 */
	private void removeAllTopicRows() {
		// Purge all rows 
		while (topicDataTable.getRowCount() > 0) {
			topicDataTable.removeRow(0);
		}

		topicTable.getDataTable().resize(0, NUMBER_OF_COLUMNS);
	}

	@Override
	public void createNewPost() {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_CREATE_POST);
		postEditor.reset();
		postEditor.setPostTitle("Re: " + post.getFirstTopicTitle());
		postEditor.setAction(PostEditor.CREATE_POST);
		postEditor.setForum(forumId, forumName);
		postEditor.setTopic(topicId, topicName);
		resetPostPanel();
		vPostPanel.add(postEditor);
		vPostPanel.add(post);
		post.actionsVisible(false);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
	}

	@Override
	public void createNewTopic(long forumId, String forumName) {
		this.forumId = forumId;
		this.forumName = forumName;
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_CREATE_TOPIC);
		toolbar.setForumId(forumId, forumName);
		resetPostPanel();
		vPostPanel.add(postEditor);
		GeneralComunicator.disableKeyShorcuts();
		postEditor.reset();
		postEditor.setAction(PostEditor.CREATE_TOPIC);
		postEditor.setForum(forumId, forumName);
		postEditor.setTopic(topicId, topicName);
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}

	@Override
	public void deletePost(long id) {
		Forum.get().status.setDeletePost();
		forumService.deletePost(forumId, topicId, id, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result.booleanValue()) {
					refreshPosts(topicId, topicName);
				} else {
					refreshTopics(forumId, forumName);
				}
				Forum.get().status.unsetDeletePost();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("deletePost", caught);
				Forum.get().status.unsetDeletePost();
			}
		});
	}

	@Override
	public void editPost(GWTForumPost fp) {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_EDIT_POST);
		resetPostPanel();
		vPostPanel.add(postEditor);
		postEditor.reset();
		postEditor.setAction(PostEditor.EDIT_POST);
		postEditor.setPost(fp);
		postEditor.setForum(forumId, forumName);
		postEditor.setTopic(topicId, topicName);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}

	@Override
	public void goHome() {
		refreshForums();
	}

	@Override
	public void newTopicCreated(long id, GWTForumTopic topic) {
		this.forumId = id;
		this.topicId = topic.getId();
		this.topicName = topic.getTitle();
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_POST);
		toolbar.setNumberOfReplies(topic.getReplies());
		toolbar.setTopicId(topicId, topicName);
		resetPostPanel();
		vPostPanel.add(post);
		post.removeAllRows();

		for (GWTForumPost fPost : topic.getPosts()) {
			post.addRow(fPost);
		}

		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
	}

	@Override
	public void refreshPosts(final long topicId, String topicName) {
		this.topicId = topicId;
		this.topicName = topicName;
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_POST);
		toolbar.setTopicId(topicId, topicName);
		resetPostPanel();
		vPostPanel.add(post);
		post.removeAllRows();
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
		Forum.get().status.setGetPosts();

		forumService.findTopicByPK(topicId, new AsyncCallback<GWTForumTopic>() {
			@Override
			public void onSuccess(GWTForumTopic result) {
				forumService.increaseTopicView(topicId, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						// Nothing to be done
					}

					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("increaseTopicView", caught);
					}
				});

				toolbar.setNumberOfReplies(result.getReplies());
				for (GWTForumPost fPost : result.getPosts()) {
					post.addRow(fPost);
				}

				Forum.get().status.unsetGetPosts();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("findTopicByPK", caught);
				Forum.get().status.unsetGetPosts();
			}
		});
	}

	@Override
	public void refreshTopics(long id, String forumName) {
		this.forumId = id;
		this.forumName = forumName;
		resetVPanel();
		vPanel.add(topicTable);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_TOPIC);
		toolbar.setForumId(forumId, forumName);
		resetPostPanel();
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
		topicTable.fillWidth();
		removeAllTopicRows();
		Forum.get().status.setGetTopics();
		forumService.getTopicsByForum(id, new AsyncCallback<List<GWTForumTopic>>() {
			@Override
			public void onSuccess(List<GWTForumTopic> result) {
				toolbar.setNumberOfTopics(result.size());
				for (GWTForumTopic topic : result) {
					topicTable.addRow(topic);
				}
				Forum.get().status.unsetGetTopics();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("getTopicsByForum", caught);
				Forum.get().status.unsetGetTopics();
			}
		});
	}

	@Override
	public void refreshTopics(String uuid) {
		// Not implemented
	}

	@Override
	public void updatePost(GWTForumPost fp) {
		Forum.get().status.setUpdatePost();
		forumService.updatePost(fp, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				refreshPosts(topicId, topicName);
				Forum.get().status.unsetUpdatePost();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("updatePost", caught);
				Forum.get().status.unsetUpdatePost();
			}
		});
	}

	@Override
	public ForumController getController() {
		return this;
	}

	@Override
	public void createForum() {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_CREATE_FORUM);
		forumEditor.reset();
		forumEditor.setAction(ForumEditor.CREATE_FORUM);
		resetPostPanel();
		vPostPanel.add(forumEditor);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}


	@Override
	public void refreshForums() {
		resetVPanel();
		vPanel.add(forumTable);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_FORUM);
		forumTable.fillWidth();
		resetPostPanel();
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
		removeAllForumRows();
		Forum.get().status.setGetForums();
		forumService.getAllForum(new AsyncCallback<List<GWTForum>>() {
			@Override
			public void onSuccess(List<GWTForum> result) {
				for (GWTForum forum : result) {
					forumTable.addRow(forum);
				}
				Forum.get().status.unsetGetForums();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("getAllForum", caught);
				Forum.get().status.unsetGetForums();
			}
		});
	}

	/**
	 * fillWidth
	 */
	public void fillWidth() {
		forumTable.fillWidth();
		topicTable.fillWidth();
	}

	@Override
	public void deleteForum(long forumId) {
		Forum.get().status.setDeleteForum();
		forumService.deleteForum(forumId, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				refreshForums();
				Forum.get().status.unsetDeleteForum();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("deleteForum", caught);
				Forum.get().status.unsetDeleteForum();
			}
		});
	}

	@Override
	public void editForum(GWTForum forum) {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_EDIT_FORUM);
		forumEditor.reset();
		forumEditor.setAction(ForumEditor.EDIT_FORUM);
		forumEditor.setForum(forum);
		resetPostPanel();
		vPostPanel.add(forumEditor);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}

	@Override
	public void editTopic(GWTForumPost fp) {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_EDIT_TOPIC);
		;
		resetPostPanel();
		vPostPanel.add(postEditor);
		postEditor.reset();
		postEditor.setAction(PostEditor.EDIT_TOPIC);
		postEditor.setPost(fp);
		postEditor.setForum(forumId, forumName);
		postEditor.setTopic(topicId, topicName);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}

	@Override
	public void quotePost(GWTForumPost fp) {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(DashboardToolbarForum.MODE_CREATE_POST);
		resetPostPanel();
		vPostPanel.add(postEditor);
		vPostPanel.add(post);
		postEditor.reset();
		postEditor.setPostTitle("Re: " + post.getFirstTopicTitle());
		postEditor.setAction(PostEditor.CREATE_POST);
		postEditor.setForum(forumId, forumName);
		postEditor.setTopic(topicId, topicName);
		postEditor.setMessage("[quote]" + fp.getMessage() + "[/quote]");
		post.actionsVisible(false);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
	}
}