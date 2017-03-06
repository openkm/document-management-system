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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.service.OKMForumService;
import com.openkm.extension.frontend.client.service.OKMForumServiceAsync;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * ForumEditor
 *
 * @author jllort
 */
public class ForumEditor extends Composite {
	private final OKMForumServiceAsync forumService = (OKMForumServiceAsync) GWT.create(OKMForumService.class);

	public static final int NONE = 0;
	public static final int CREATE_FORUM = 1;
	public static final int EDIT_FORUM = 2;

	private VerticalPanel vPanel;
	private TextBox title;
	private TextArea textArea;
	private Button create;
	private Button update;
	private Button cancel;
	private int action = NONE;
	private HTML name;
	GWTForum forum;
	private ForumToolBarEditor toolbar;

	/**
	 * ForumEditor
	 */
	public ForumEditor(final ForumController controller) {
		SimplePanel sp = new SimplePanel();
		vPanel = new VerticalPanel();
		sp.add(vPanel);

		// Space
		HTML space = new HTML("");
		vPanel.add(space);

		// Subject
		name = new HTML(GeneralComunicator.i18nExtension("forum.name"));
		title = new TextBox();
		title.setWidth("250px");
		title.setStyleName("okm-Input");
		HorizontalPanel titlePanel = new HorizontalPanel();
		HTML titleLeftSpace = new HTML("&nbsp;");
		titlePanel.add(titleLeftSpace);
		titlePanel.add(name);
		titlePanel.add(new HTML("&nbsp;"));
		titlePanel.add(title);
		titlePanel.setCellWidth(titleLeftSpace, "5px");
		titlePanel.setCellVerticalAlignment(name, HasAlignment.ALIGN_MIDDLE);
		vPanel.add(titlePanel);

		// Space
		HTML space2 = new HTML("");
		vPanel.add(space2);

		// Text Area
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
		create = new Button(GeneralComunicator.i18n("button.create"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (action) {
					case CREATE_FORUM:
						action = NONE;
						GWTForum forum = new GWTForum();
						forum.setName(title.getText());
						forum.setDescription(textArea.getText());
						Forum.get().status.setCreateForum();
						forumService.createForum(forum, new AsyncCallback<GWTForum>() {
							@Override
							public void onSuccess(GWTForum result) {
								controller.refreshTopics(result.getId(), result.getName());
								Forum.get().status.unsetCreateForum();
							}

							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("createForum", caught);
								Forum.get().status.unsetCreateForum();
							}
						});
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}

			;
		});
		create.setStyleName("okm-AddButton");

		// Cancel
		cancel = new Button(GeneralComunicator.i18n("button.cancel"));
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (action) {
					case CREATE_FORUM:
					case EDIT_FORUM:
						controller.refreshForums();
						break;
				}
				GeneralComunicator.enableKeyShorcuts();
			}
		});
		cancel.setStyleName("okm-NoButton");

		update = new Button(GeneralComunicator.i18n("button.update"));
		update.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				action = NONE;
				forum.setName(title.getText());
				forum.setDescription(textArea.getText());
				Forum.get().status.setUpdateForum();
				forumService.updateForum(forum, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						controller.refreshForums();
						Forum.get().status.unsetUpdateForum();
					}

					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("updateForum", caught);
						Forum.get().status.unsetUpdateForum();
					}
				});
				GeneralComunicator.enableKeyShorcuts();
			}
		});
		update.setStyleName("okm-YesButton");

		// Button panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(new HTML("&nbsp;"));
		buttonPanel.add(cancel);
		buttonPanel.add(new HTML("&nbsp;"));
		buttonPanel.add(create);
		buttonPanel.add(update);
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
		name.setHTML(GeneralComunicator.i18nExtension("forum.name"));
		create.setHTML(GeneralComunicator.i18n("button.create"));
		cancel.setHTML(GeneralComunicator.i18n("button.cancel"));
		update.setHTML(GeneralComunicator.i18n("button.update"));
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
			case CREATE_FORUM:
				create.setVisible(true);
				update.setVisible(false);
				break;
			case EDIT_FORUM:
				create.setVisible(false);
				update.setVisible(true);
				break;
		}
	}

	/**
	 * setPost
	 *
	 * @param forum
	 */
	public void setForum(GWTForum forum) {
		this.forum = forum;
		title.setText(forum.getName());
		textArea.setText(forum.getDescription());
	}
}