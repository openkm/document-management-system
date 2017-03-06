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
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMForumService;
import com.openkm.extension.frontend.client.service.OKMForumServiceAsync;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.Util;

import java.util.List;

/**
 * TopicManager
 *
 * @author jllort
 */
public class TopicManager extends Composite implements ForumController {
	private final static int IE_SIZE_RECTIFICATION = (Util.getUserAgent().startsWith("ie") ? 1 : 0);
	private final OKMForumServiceAsync forumService = (OKMForumServiceAsync) GWT.create(OKMForumService.class);

	// Toolbar height
	public static final int TOOLBAR_HEADER = 25;

	// Number of columns
	private static final int NUMBER_OF_COLUMNS = 4;

	private VerticalPanel vPanel;
	private TabToolbarForum toolbar;

	// Topic list
	private TopicScrollTable topicTable;
	private FixedWidthFlexTable topicHeaderTable;
	private FixedWidthGrid topicDataTable;

	// Editor & Post
	private ScrollPanel scrollPanelPost;
	private VerticalPanel vPostPanel;
	private PostEditor postEditor;
	private Post post;

	// identifiers
	private long forumId = 0;
	private long topicId = 0;
	private String topicName = "";

	/**
	 * Topic
	 */
	public TopicManager() {
		vPanel = new VerticalPanel();
		toolbar = new TabToolbarForum(this);

		ScrollTableImages scrollTableImages = new ScrollTableImages() {
			public AbstractImagePrototype scrollTableAscending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_asc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_asc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableDescending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_desc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_desc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableFillWidth() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/fill_width.gif");
					}

					public Image createImage() {
						return new Image("img/fill_width.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
					}
				};
			}
		};

		topicHeaderTable = new FixedWidthFlexTable();
		topicDataTable = new FixedWidthGrid();
		topicTable = new TopicScrollTable(topicDataTable, topicHeaderTable, scrollTableImages, this);
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

		// Post Editor
		vPostPanel = new VerticalPanel();
		postEditor = new PostEditor(this);
		postEditor.setStyleName("okm-Mail");
		post = new Post(this);
		scrollPanelPost = new ScrollPanel(vPostPanel);

		vPanel.add(toolbar); // Always visible

		toolbar.setHeight("" + TOOLBAR_HEADER + "px");
		toolbar.setWidth("100%");
		vPanel.setCellHeight(toolbar, "" + TOOLBAR_HEADER + "px");

		vPostPanel.setWidth("100%");
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

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		vPanel.setPixelSize(width, height);
		topicTable.setPixelSize(width - IE_SIZE_RECTIFICATION, height - TOOLBAR_HEADER - IE_SIZE_RECTIFICATION);
		topicTable.fillWidth();
		scrollPanelPost.setPixelSize(width - IE_SIZE_RECTIFICATION, height - (TOOLBAR_HEADER + IE_SIZE_RECTIFICATION));
		postEditor.setEditorSize(width - IE_SIZE_RECTIFICATION);
	}

	/**
	 * Removes all rows except the first
	 */
	private void removeAllRows() {
		// Purge all rows
		while (topicDataTable.getRowCount() > 0) {
			topicDataTable.removeRow(0);
		}

		topicTable.getDataTable().resize(0, NUMBER_OF_COLUMNS);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		topicHeaderTable.setHTML(0, 0, GeneralComunicator.i18nExtension("forum.topics"));
		topicHeaderTable.setHTML(0, 1, GeneralComunicator.i18nExtension("forum.replies"));
		topicHeaderTable.setHTML(0, 2, GeneralComunicator.i18nExtension("forum.views"));
		topicHeaderTable.setHTML(0, 3, GeneralComunicator.i18nExtension("forum.last.post"));
		toolbar.langRefresh();
		postEditor.langRefresh();
		if (hasWidget(topicTable) && forumId != 0) {
			refreshTopics(Forum.get().getUuid());
		}
		if (hasWidget(post) && topicId != 0) {
			refreshPosts(topicId, topicName);
		}
	}

	/**
	 * setForumId
	 *
	 * @param forumId
	 */
	public void setForumId(int forumId) {
		this.forumId = forumId;
	}

	@Override
	public void createNewPost() {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(TabToolbarForum.MODE_CREATE_POST);
		resetPostPanel();
		vPostPanel.add(postEditor);
		vPostPanel.add(post);
		postEditor.reset();
		postEditor.setPostTitle("Re: " + post.getFirstTopicTitle());
		postEditor.setAction(PostEditor.CREATE_POST);
		postEditor.setForum(forumId, "");
		postEditor.setTopic(topicId, topicName);
		GeneralComunicator.disableKeyShorcuts();
		post.actionsVisible(false);
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
	}

	@Override
	public void createNewTopic(long forumId, String forumName) {
		this.forumId = forumId;
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(TabToolbarForum.MODE_CREATE_TOPIC);
		resetPostPanel();
		vPostPanel.add(postEditor);
		postEditor.reset();
		postEditor.setAction(PostEditor.CREATE_TOPIC);
		postEditor.setForum(forumId, "");
		postEditor.setTopic(topicId, topicName);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}

	@Override
	public void newTopicCreated(long forumId, GWTForumTopic topic) {
		this.forumId = forumId;
		this.topicId = topic.getId();
		this.topicName = topic.getTitle();
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(TabToolbarForum.MODE_POST);
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
	public void refreshTopics(long id, String forumName) {
		resetVPanel();
		vPanel.add(topicTable);
		toolbar.switchViewMode(TabToolbarForum.MODE_TOPIC);
		resetPostPanel();
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
		removeAllRows();
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
				GeneralComunicator.showError("getTopicsByUuid", caught);
				Forum.get().status.unsetGetTopics();
			}
		});
	}

	@Override
	public void refreshTopics(String uuid) {
		resetVPanel();
		vPanel.add(topicTable);
		toolbar.switchViewMode(TabToolbarForum.MODE_TOPIC);
		resetPostPanel();
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
		removeAllRows();
		Forum.get().status.setGetTopics();

		forumService.getTopicsByNode(uuid, new AsyncCallback<List<GWTForumTopic>>() {
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
				GeneralComunicator.showError("getTopicsByUuid", caught);
				Forum.get().status.unsetGetTopics();
			}
		});
	}

	@Override
	public void refreshPosts(final long topicId, String topicName) {
		this.topicId = topicId;
		this.topicName = topicName;
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(TabToolbarForum.MODE_POST);
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
	public void goHome() {
		refreshTopics(Forum.get().getUuid());
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
					refreshTopics(Forum.get().getUuid());
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
	public void editPost(GWTForumPost fp) {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(TabToolbarForum.MODE_EDIT_POST);
		resetPostPanel();
		vPostPanel.add(postEditor);
		postEditor.reset();
		postEditor.setAction(PostEditor.EDIT_POST);
		postEditor.setPost(fp);
		postEditor.setForum(forumId, "");
		postEditor.setTopic(topicId, topicName);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}

	@Override
	public ForumController getController() {
		return this;
	}

	@Override
	public void refreshForums() {
		// Nothing to be done here
	}

	@Override
	public void createForum() {
		// Nothing to be done here
	}

	@Override
	public void deleteForum(long forumId) {
		// Nothing to be done here
	}

	@Override
	public void editForum(GWTForum forum) {
		// Nothing to be done here
	}

	@Override
	public void editTopic(GWTForumPost fp) {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(TabToolbarForum.MODE_EDIT_TOPIC);
		resetPostPanel();
		vPostPanel.add(postEditor);
		postEditor.reset();
		postEditor.setAction(PostEditor.EDIT_TOPIC);
		postEditor.setPost(fp);
		postEditor.setForum(forumId, "");
		postEditor.setTopic(topicId, topicName);
		GeneralComunicator.disableKeyShorcuts();
		scrollPanelPost.setStyleName("okm-Mail");
		vPostPanel.setStyleName("okm-Mail");
	}

	@Override
	public void quotePost(GWTForumPost fp) {
		resetVPanel();
		vPanel.add(scrollPanelPost);
		toolbar.switchViewMode(TabToolbarForum.MODE_CREATE_POST);
		resetPostPanel();
		vPostPanel.add(postEditor);
		vPostPanel.add(post);
		postEditor.reset();
		postEditor.setPostTitle("Re: " + post.getFirstTopicTitle());
		postEditor.setAction(PostEditor.CREATE_POST);
		postEditor.setForum(forumId, "");
		postEditor.setTopic(topicId, topicName);
		postEditor.setMessage("[quote]" + fp.getMessage() + "[/quote]");
		GeneralComunicator.disableKeyShorcuts();
		post.actionsVisible(false);
		scrollPanelPost.removeStyleName("okm-Mail");
		vPostPanel.removeStyleName("okm-Mail");
	}
}