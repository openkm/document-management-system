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

package com.openkm.frontend.client.widget.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidget;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidgetToFire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TemplateWizardPopup
 *
 * @author jllort
 *
 */
public class TemplateWizardPopup extends DialogBox {
	private final OKMPropertyGroupServiceAsync propertyGroupService = GWT.create(OKMPropertyGroupService.class);
	private final OKMDocumentServiceAsync documentService = GWT.create(OKMDocumentService.class);

	private static final int STATUS_NONE = -1;
	private static final int STATUS_PROPERTY_GROUPS = 0;
	private static final int STATUS_FINISH = 1;

	private FiredVerticalPanel vPanelFired;
	private String docPath = "";
	private String destinationPath = "";
	private List<GWTPropertyGroup> groupsList = null;
	private List<GWTFormElement> formElementList = null;
	private Map<String, List<Map<String, String>>> tableProperties = new HashMap<String, List<Map<String, String>>>();
	private int groupIndex = 0;
	private PropertyGroupWidget propertyGroupWidget = null;
	private int status = STATUS_NONE;
	public Button actualButton;
	private boolean open = false;

	/**
	 * TemplateWizardPopup
	 */
	public TemplateWizardPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		actualButton = new Button("");
		vPanelFired = new FiredVerticalPanel();
		vPanelFired.setSize("100%", "20px");
		setText(Main.i18n("template.wizard.creation"));

		actualButton.setStyleName("okm-YesButton");

		super.hide();
		setWidget(vPanelFired);
	}

	/**
	 * Starting wizard
	 *
	 * @param docPath
	 */
	public void start(String docPath, String destinationPath, boolean open) {
		groupsList = new ArrayList<GWTPropertyGroup>();
		formElementList = new ArrayList<GWTFormElement>();
		tableProperties = new HashMap<String, List<Map<String, String>>>();
		vPanelFired.clear();
		actualButton = new Button("");
		actualButton.setEnabled(false);
		this.docPath = docPath;
		this.destinationPath = destinationPath;
		this.open = open;
		status = STATUS_PROPERTY_GROUPS;

		// Wizard
		groupIndex = 0;
		propertyGroupService.getGroups(docPath, new AsyncCallback<List<GWTPropertyGroup>>() {
			@Override
			public void onSuccess(List<GWTPropertyGroup> result) {
				for (GWTPropertyGroup group : result) {
					groupsList.add(group);
				}
				showNextWizard();
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getGroups", caught);
			}
		});
	}

	/**
	 * getProperties()
	 */
	private void getProperties() {
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML("");
		hPanel.add(actualButton);
		hPanel.add(space);
		hPanel.setCellWidth(space, "3px");
		propertyGroupWidget = new PropertyGroupWidget(docPath, groupsList.get(groupIndex),
				new HTML(groupsList.get(groupIndex).getLabel()), vPanelFired, null);
		vPanelFired.clear();
		vPanelFired.add(propertyGroupWidget);
		vPanelFired.add(hPanel);
		HTML space2 = new HTML("");
		vPanelFired.add(space2);
		vPanelFired.setCellVerticalAlignment(propertyGroupWidget, HasAlignment.ALIGN_TOP);
		vPanelFired.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_RIGHT);
		vPanelFired.setCellHeight(space2, "5px");
		propertyGroupWidget.getProperties(false);
	}

	/**
	 * showNextWizard
	 */
	public void showNextWizard() {
		switch (status) {
			case STATUS_PROPERTY_GROUPS:
				if (groupsList != null && groupsList.size() > groupIndex) {
					if (groupsList.size() == groupIndex + 1) {
						actualButton = acceptButton();
					} else {
						actualButton = nextButton();
					}

					getProperties();
					groupIndex++;
				} else {
					documentService.createFromTemplate(docPath, destinationPath, formElementList, tableProperties, new AsyncCallback<GWTDocument>() {
						@Override
						public void onSuccess(GWTDocument result) {
							if (open) {
								CommonUI.openPath(result.getParentPath(), result.getPath());
							} else {
								Main.get().mainPanel.desktop.browser.fileBrowser.mantainSelectedRowByPath(result.getPath());
								Main.get().mainPanel.desktop.browser.fileBrowser.refresh(Main.get().activeFolderTree.getActualPath());
							}

							// Refreshing users repository size
							Main.get().workspaceUserProperties.getUserDocumentsSize();

							// Forward to next status
							status = STATUS_FINISH;
							showNextWizard();
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("createFromTemplate", caught);

							// Forward to next status
							status = STATUS_FINISH;
							showNextWizard();
						}
					});
				}
				break;

			case STATUS_FINISH:
				hide();
				break;
		}
	}

	/**
	 * Accept button 
	 *
	 * @return
	 */
	private Button acceptButton() {
		Button button = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				actualButton.setEnabled(false);
				executeActionButton();
			}
		});

		button.setStyleName("okm-YesButton");
		button.setEnabled(false);
		return button;
	}

	/**
	 * Next button 
	 *
	 * @return
	 */
	private Button nextButton() {
		Button button = new Button(Main.i18n("button.next"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				actualButton.setEnabled(false);
				executeActionButton();
			}
		});

		button.setStyleName("okm-YesButton");
		button.setEnabled(false);
		return button;
	}

	private void executeActionButton() {
		switch (status) {
			case STATUS_PROPERTY_GROUPS:
				if (propertyGroupWidget != null) {
					formElementList.addAll(propertyGroupWidget.updateFormElementsValuesWithNewer());
					showNextWizard();
				}
				break;
		}
	}

	/**
	 * changeView
	 *
	 * Ensures fileupload is hiden and panel is centered
	 */
	public void changeView() {
		Main.get().activeFolderTree.folderSelectPopup.hide();
		center();
	}

	/**
	 * FiredVerticalPanel
	 *
	 * @author jllort
	 *
	 */
	private class FiredVerticalPanel extends Composite implements PropertyGroupWidgetToFire {
		private VerticalPanel vPanel;

		public FiredVerticalPanel() {
			vPanel = new VerticalPanel();
			initWidget(vPanel);
		}

		@Override
		public void finishedGetProperties() {
			if (propertyGroupWidget != null) {
				propertyGroupWidget.edit();
				actualButton.setEnabled(true);
			}
			changeView();
		}

		@Override
		public void finishedSetProperties() {
			showNextWizard();
		}

		@Override
		public void finishedRemoveGroup() {
			// Not implemented
		}

		/**
		 * setCellHorizontalAlignment
		 */
		public void setCellHorizontalAlignment(Widget w, HorizontalAlignmentConstant align) {
			vPanel.setCellHorizontalAlignment(w, align);
		}

		/**
		 * setCellHeight
		 */
		public void setCellHeight(Widget w, String height) {
			vPanel.setCellHeight(w, height);
		}

		/**
		 * setCellVerticalAlignment
		 */
		public void setCellVerticalAlignment(Widget w, VerticalAlignmentConstant align) {
			vPanel.setCellVerticalAlignment(w, align);
		}

		/**
		 * clear
		 */
		public void clear() {
			vPanel.clear();
		}

		/**
		 * add
		 */
		public void add(Widget widget) {
			vPanel.add(widget);
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("template.wizard.creation"));
	}
}