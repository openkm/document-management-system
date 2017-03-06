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

package com.openkm.frontend.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTExtendedAttributes;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;

import java.util.List;

/**
 * TemplatePopup
 * s
 * @author jllort
 *
 */
public class TemplatePopup extends DialogBox {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hNamePanel;
	private HorizontalPanel hButtonPanel;
	private HTML nameText;
	private TextBox name;
	private Button cancel;
	private Button create;
	private GWTDocument doc;
	private GroupBoxPanel groupBoxPanel;
	private FlexTable table;
	private CheckBox copyCategories;
	private CheckBox copyKeywords;
	private CheckBox copyNotes;
	private CheckBox copyPropertyGroup;
	private CheckBox copyWiki;
	private String dstFldPath;
	private boolean open = false;
	private Anchor selectAll;
	private Anchor selectNone;

	public TemplatePopup() {
		// Establishes auto-close when click outside
		super(false, true);

		setText(Main.i18n("template.new.document.title"));

		// Name
		hNamePanel = new HorizontalPanel();
		nameText = new HTML(Main.i18n("template.new.document.name"));
		name = new TextBox();
		name.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (name.getText().length() > 0) {
					if (KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
						create.setEnabled(false);
						create();
					} else {
						create.setEnabled(true);
					}
				} else {
					create.setEnabled(false);
				}
			}
		});
		name.setWidth("250px");
		name.setStyleName("okm-Input");

		hNamePanel.add(Util.hSpace("5px"));
		hNamePanel.add(nameText);
		hNamePanel.add(Util.hSpace("5px"));
		hNamePanel.add(name);

		hNamePanel.setCellVerticalAlignment(nameText, HasAlignment.ALIGN_MIDDLE);
		hNamePanel.setCellVerticalAlignment(name, HasAlignment.ALIGN_MIDDLE);

		// Buttons
		cancel = new Button(Main.i18n("button.cancel"));
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancel.setStyleName("okm-NoButton");

		create = new Button(Main.i18n("button.create"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				create.setEnabled(false);
				create();
			}
		});
		create.setStyleName("okm-AddButton");

		hButtonPanel = new HorizontalPanel();
		hButtonPanel.add(cancel);
		hButtonPanel.add(Util.hSpace("5px"));
		hButtonPanel.add(create);

		// copy attributes
		HorizontalPanel hAttributesPanel = new HorizontalPanel();
		hAttributesPanel.setWidth("100%");
		copyCategories = new CheckBox();
		copyKeywords = new CheckBox();
		copyNotes = new CheckBox();
		copyPropertyGroup = new CheckBox();
		copyWiki = new CheckBox();
		table = new FlexTable();
		table.setWidth("100%");
		table.setStyleName("okm-NoWrap");
		// row 1
		table.setHTML(0, 0, Main.i18n("template.categories"));
		table.setWidget(0, 1, copyCategories);
		table.setHTML(0, 2, "&nbsp;");
		table.setHTML(0, 3, Main.i18n("template.notes"));
		table.setWidget(0, 4, copyNotes);
		table.setHTML(0, 5, "&nbsp;");
		table.getFlexCellFormatter().setWidth(0, 5, "100%");
		// row 2
		table.setHTML(1, 0, Main.i18n("template.keywords"));
		table.setWidget(1, 1, copyKeywords);
		table.setHTML(1, 2, "&nbsp;");
		table.setHTML(1, 3, Main.i18n("template.wiki"));
		table.setWidget(1, 4, copyWiki);
		// row 3
		table.setHTML(2, 0, Main.i18n("template.propertygroup"));
		table.setWidget(2, 1, copyPropertyGroup);
		table.setHTML(2, 2, "&nbsp;");
		// row 4
		HorizontalPanel selectPanel = new HorizontalPanel();
		selectAll = new Anchor(Main.i18n("button.all"));
		selectAll.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				copyCategories.setValue(true);
				copyKeywords.setValue(true);
				copyNotes.setValue(true);
				copyPropertyGroup.setValue(true);
				copyWiki.setValue(true);
			}
		});
		selectAll.addStyleName("okm-Hyperlink");
		selectNone = new Anchor(Main.i18n("button.none"));
		selectNone.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				copyCategories.setValue(false);
				copyKeywords.setValue(false);
				copyNotes.setValue(false);
				copyPropertyGroup.setValue(false);
				copyWiki.setValue(false);
			}
		});
		selectNone.addStyleName("okm-Hyperlink");
		selectPanel.add(selectAll);
		selectPanel.add(Util.hSpace("5px"));
		selectPanel.add(selectNone);
		selectPanel.add(Util.hSpace("5px"));
		table.setWidget(3, 0, selectPanel);
		table.getFlexCellFormatter().setColSpan(3, 0, 6);
		table.getFlexCellFormatter().setHorizontalAlignment(3, 0, HasAlignment.ALIGN_RIGHT);

		groupBoxPanel = new GroupBoxPanel();
		groupBoxPanel.setCaption(Main.i18n("template.copy"));
		groupBoxPanel.add(table);

		hAttributesPanel.add(Util.hSpace("5px"));
		hAttributesPanel.add(groupBoxPanel);
		hAttributesPanel.add(Util.hSpace("5px"));

		vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
		vPanel.add(Util.vSpace("5px"));
		vPanel.add(hNamePanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.add(hAttributesPanel);
		vPanel.add(Util.vSpace("5px"));
		vPanel.add(hButtonPanel);
		vPanel.add(Util.vSpace("5px"));

		vPanel.setCellHorizontalAlignment(hNamePanel, HasAlignment.ALIGN_LEFT);
		vPanel.setCellHorizontalAlignment(groupBoxPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hButtonPanel, HasAlignment.ALIGN_CENTER);

		setWidget(vPanel);
	}

	/**
	 * reset
	 */
	public void reset(GWTDocument doc, String dstFldpath, boolean openFldPath) {
		this.doc = doc;
		this.dstFldPath = dstFldpath;
		this.open = openFldPath;
		name.setText(doc.getName());
		copyCategories.setValue(false);
		copyKeywords.setValue(false);
		copyNotes.setValue(false);
		copyPropertyGroup.setValue(false);
		copyWiki.setValue(false);
		create.setEnabled(true);
	}

	/**
	 * create
	 */
	public void create() {
		propertyGroupService.getGroups(doc.getPath(), new AsyncCallback<List<GWTPropertyGroup>>() {
			@Override
			public void onSuccess(List<GWTPropertyGroup> result) {
				// Has property groups and mime type to fill fields
				if ((doc.getMimeType().equals("application/pdf") ||
						doc.getMimeType().equals("text/html") ||
						(doc.getMimeType().equals("application/vnd.oasis.opendocument.text") && doc.getName().endsWith("odt"))) &&
						result.size() > 0) {
					Main.get().templateWizardPopup.start(doc.getPath(), dstFldPath + "/" + name.getText(), open);
					hide();
				} else {
					Main.get().mainPanel.desktop.browser.fileBrowser.status.setFlagCreateFromTemplate();
					GWTExtendedAttributes attributes = new GWTExtendedAttributes();
					attributes.setCategories(copyCategories.getValue());
					attributes.setKeywords(copyKeywords.getValue());
					attributes.setNotes(copyNotes.getValue());
					attributes.setPropertyGroups(copyPropertyGroup.getValue());
					attributes.setWiki(copyWiki.getValue());
					documentService.createFromTemplate(doc.getPath(), dstFldPath, name.getText(), attributes, new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							Main.get().mainPanel.desktop.browser.fileBrowser.status.unsetFlagCreateFromTemplate();
							String dstPath = dstFldPath + "/" + name.getText();
							// If are in same stack view is not needed all path sequence ( create from menu )
							if (open) {
								CommonUI.openPath(Util.getParent(dstPath), dstPath);
							} else {
								Main.get().mainPanel.desktop.browser.fileBrowser.mantainSelectedRowByPath(dstPath);
								Main.get().mainPanel.desktop.browser.fileBrowser.refresh(Main.get().activeFolderTree.getActualPath());
							}

							Main.get().workspaceUserProperties.getUserDocumentsSize();
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().mainPanel.desktop.browser.fileBrowser.status.unsetFlagCreateFromTemplate();
							Main.get().showError("createFromTemplate", caught);
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getAllGroups", caught);
			}
		});
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("template.new.document.title"));
		cancel.setText(Main.i18n("button.cancel"));
		create.setText(Main.i18n("button.create"));
		selectAll.setText(Main.i18n("button.all"));
		selectNone.setText(Main.i18n("button.none"));
		groupBoxPanel.setCaption(Main.i18n("template.copy"));
		nameText.setHTML(Main.i18n("template.new.document.name"));
		// row 1
		table.setHTML(0, 0, Main.i18n("template.categories"));
		table.setHTML(0, 3, Main.i18n("template.notes"));
		// row 2
		table.setHTML(1, 0, Main.i18n("template.keywords"));
		table.setHTML(1, 0, Main.i18n("template.wiki"));
		// row 3
		table.setHTML(2, 0, Main.i18n("template.propertygroup"));
	}
}