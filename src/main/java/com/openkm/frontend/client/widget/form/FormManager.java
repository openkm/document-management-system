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

package com.openkm.frontend.client.widget.form;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.bean.form.*;
import com.openkm.frontend.client.constants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.util.*;
import com.openkm.frontend.client.util.validator.ExtendedDefaultValidatorProcessor;
import com.openkm.frontend.client.util.validator.ValidatorBuilder;
import com.openkm.frontend.client.util.validator.ValidatorToFire;
import com.openkm.frontend.client.widget.Clipboard;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.searchin.CalendarWidget;
import com.openkm.frontend.client.widget.searchin.HasPropertyHandler;
import eu.maydu.gwt.validation.client.ValidationProcessor;
import eu.maydu.gwt.validation.client.actions.FocusAction;

import java.util.*;

/**
 * FormManager
 *
 * @author jllort
 */
public class FormManager {
	private final OKMKeyValueServiceAsync keyValueService = GWT.create(OKMKeyValueService.class);
	private final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);
	private final OKMDocumentServiceAsync documentService = GWT.create(OKMDocumentService.class);
	private final OKMFolderServiceAsync folderService = GWT.create(OKMFolderService.class);

	// Boolean contants
	private String BOOLEAN_TRUE = String.valueOf(Boolean.TRUE);

	private List<GWTFormElement> formElementList = new ArrayList<GWTFormElement>();
	public Map<String, GWTPropertyParams> hPropertyParams = new HashMap<String, GWTPropertyParams>();
	private Map<String, Widget> hWidgetProperties = new HashMap<String, Widget>();
	private FlexTable table;
	private FolderSelectPopup folderSelectPopup;
	private ExtendedDefaultValidatorProcessor validationProcessor;
	private boolean drawed = false;
	private boolean readOnly = false;
	private GWTTaskInstance taskInstance;
	private Button submitForm;
	private HasWorkflow workflow;
	private HorizontalPanel submitButtonPanel;
	private boolean isSearchView = false;
	private boolean isMassiveView = false;
	private HasPropertyHandler propertyHandler;
	private List<Button> buttonControlList;
	private Map<String, Object> workflowVarMap = new HashMap<String, Object>();
	private FormManager singleton;
	private ValidatorToFire validatorToFire;
	
	/**
	 * FormManager used in workflow mode
	 */
	public FormManager(HasWorkflow workflow, ValidatorToFire validatorToFire) {
		singleton = this;
		this.workflow = workflow;
		this.validatorToFire = validatorToFire;
		init();
	}

	/**
	 * FormManager used in search mode
	 */
	public FormManager(HasPropertyHandler propertyHandler, ValidatorToFire validatorToFire) {
		singleton = this;
		this.propertyHandler = propertyHandler;
		this.validatorToFire = validatorToFire;
		isSearchView = true;
		init();
	}

	/**
	 * FormManager used in property group mode
	 */
	public FormManager(ValidatorToFire validatorToFire) {
		singleton = this;
		this.validatorToFire = validatorToFire;
		init();
	}

	/**
	 * setIsMassiveUpdate
	 */
	public void setIsMassiveUpdate(HasPropertyHandler propertyHandler) {
		this.propertyHandler = propertyHandler;
		isMassiveView = true;
	}

	/**
	 * init
	 */
	private void init() {
		table = new FlexTable();
		table.setWidth("100%");
		table.setStyleName("okm-NoWrap");
		folderSelectPopup = new FolderSelectPopup();
		folderSelectPopup.setWidth("450px");
		folderSelectPopup.setHeight("440px");
		folderSelectPopup.setStyleName("okm-Popup");
		folderSelectPopup.addStyleName("okm-DisableSelect");
		submitButtonPanel = new HorizontalPanel();
		buttonControlList = new ArrayList<Button>();
	}

	/**
	 * getTable
	 *
	 * @return
	 */
	public FlexTable getTable() {
		return table;
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row     The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(int row, int columns, boolean warp) {
		for (int i = 0; i < columns; i++) {
			table.getCellFormatter().setWordWrap(row, i, false);
		}
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param table   FlexTable The table to format
	 * @param row     The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(FlexTable table, int row, int columns, boolean warp) {
		for (int i = 0; i < columns; i++) {
			table.getCellFormatter().setWordWrap(row, i, false);
		}
	}

	/**
	 * drawFormElement
	 */
	private void drawFormElement(int row, final GWTFormElement gwtFormElement, boolean readOnly, boolean searchView) {
		final String propertyName = gwtFormElement.getName();

		if (gwtFormElement instanceof GWTButton) {
			final GWTButton gWTButton = (GWTButton) gwtFormElement;

			if (submitForm != null) {
				submitForm.setVisible(false); // Always set form hidden because there's new buttons
			}

			Button transButton = new Button(gWTButton.getLabel());
			String style = Character.toUpperCase(gWTButton.getStyle().charAt(0)) + gWTButton.getStyle().substring(1);
			transButton.setStyleName("okm-" + style + "Button");
			HTML space = new HTML("&nbsp;");
			submitButtonPanel.add(transButton);
			submitButtonPanel.add(space);
			submitButtonPanel.setCellWidth(space, "5px");

			// Setting submit button
			transButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (gWTButton.getConfirmation() != null && !gWTButton.getConfirmation().equals("")) {
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_WORKFLOW_ACTION);
						Main.get().confirmPopup.setConfirmationText(gWTButton.getConfirmation());
						ValidationButton validationButton = new ValidationButton(gWTButton, singleton);
						Main.get().confirmPopup.setValue(validationButton);
						Main.get().confirmPopup.center();
					} else {
						if (gWTButton.isValidate()) {
							if (validationProcessor.validate()) {
								if (gWTButton.getTransition().equals("")) {
									workflow.setTaskInstanceValues(taskInstance.getId(), null);
								} else {
									workflow.setTaskInstanceValues(taskInstance.getId(), gWTButton.getTransition());
								}
								disableAllButtonList();
							}
						} else {
							if (gWTButton.getTransition().equals("")) {
								workflow.setTaskInstanceValues(taskInstance.getId(), null);
							} else {
								workflow.setTaskInstanceValues(taskInstance.getId(), gWTButton.getTransition());
							}
							disableAllButtonList();
						}
					}
				}
			});

			// Adding button to control list
			if (!buttonControlList.contains(transButton)) {
				buttonControlList.add(transButton);
			}
		} else if (gwtFormElement instanceof GWTTextArea) {
			HorizontalPanel hPanel = new HorizontalPanel();
			TextArea textArea = new TextArea();
			textArea.setEnabled((!readOnly && !((GWTTextArea) gwtFormElement).isReadonly()) || isSearchView); // read only
			hPanel.add(textArea);
			textArea.setStyleName("okm-TextArea");
			textArea.setText(((GWTTextArea) gwtFormElement).getValue());
			textArea.setSize(gwtFormElement.getWidth(), gwtFormElement.getHeight());
			HTML text = new HTML(); // Create a widget for this property
			text.setHTML(((GWTTextArea) gwtFormElement).getValue().replaceAll("\n", "<br>"));
			hWidgetProperties.put(propertyName, hPanel);
			table.setHTML(row, 0, "<b>" + gwtFormElement.getLabel() + "</b>");
			table.setWidget(row, 1, text);
			table.getCellFormatter().setVerticalAlignment(row, 0, VerticalPanel.ALIGN_TOP);
			table.getCellFormatter().setWidth(row, 1, "100%");

			if (searchView || isMassiveView) {
				final Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon());
				removeImage.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						for (int row = 0; row < table.getRowCount(); row++) {
							if (table.getWidget(row, 2).equals(removeImage)) {
								table.removeRow(row);
								break;
							}
						}

						hWidgetProperties.remove(propertyName);
						hPropertyParams.remove(propertyName);
						formElementList.remove(gwtFormElement);
						propertyHandler.propertyRemoved();
					}
				});

				removeImage.addStyleName("okm-Hyperlink");
				table.setWidget(row, 2, removeImage);
				table.getCellFormatter().setVerticalAlignment(row, 2, HasAlignment.ALIGN_TOP);

				if (propertyHandler != null) {
					textArea.addKeyUpHandler(new KeyUpHandler() {
						@Override
						public void onKeyUp(KeyUpEvent event) {
							propertyHandler.metadataValueChanged();
						}
					});
				}

				setRowWordWarp(row, 3, true);
			} else {
				setRowWordWarp(row, 2, true);
			}
		} else if (gwtFormElement instanceof GWTInput) {
			final HorizontalPanel hPanel = new HorizontalPanel();
			final TextBox textBox = new TextBox(); // Create a widget for this property
			textBox.setEnabled((!readOnly && !((GWTInput) gwtFormElement).isReadonly()) || isSearchView); // read only
			hPanel.add(textBox);
			String value = "";

			if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_TEXT)
					|| ((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_LINK)
					|| ((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_FOLDER)) {
				textBox.setText(((GWTInput) gwtFormElement).getValue());
				value = ((GWTInput) gwtFormElement).getValue();
			} else if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_DATE)) {
				if (((GWTInput) gwtFormElement).getDate() != null) {
					DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
					textBox.setText(dtf.format(((GWTInput) gwtFormElement).getDate()));
					value = dtf.format(((GWTInput) gwtFormElement).getDate());
				}
			}

			textBox.setWidth(gwtFormElement.getWidth());
			textBox.setStyleName("okm-Input");
			hWidgetProperties.put(propertyName, hPanel);
			table.setHTML(row, 0, "<b>" + gwtFormElement.getLabel() + "</b>");
			table.setHTML(row, 1, value);

			if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_DATE)) {
				final PopupPanel calendarPopup = new PopupPanel(true);
				final CalendarWidget calendar = new CalendarWidget();

				calendar.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						calendarPopup.hide();
						DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
						textBox.setText(dtf.format(calendar.getDate()));
						((GWTInput) gwtFormElement).setDate(calendar.getDate());

						if (propertyHandler != null) {
							propertyHandler.metadataValueChanged();
						}
					}
				});

				calendarPopup.add(calendar);
				final Image calendarIcon = new Image(OKMBundleResources.INSTANCE.calendar());

				if (readOnly || ((GWTInput) gwtFormElement).isReadonly()) {                        // read only
					calendarIcon.setResource(OKMBundleResources.INSTANCE.calendarDisabled());
				} else {
					calendarIcon.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							calendarPopup.setPopupPosition(calendarIcon.getAbsoluteLeft(), calendarIcon.getAbsoluteTop() - 2);
							if (calendar.getDate() != null) {
								calendar.setNow((Date) calendar.getDate().clone());
							} else {
								calendar.setNow(null);
							}
							calendarPopup.show();
						}
					});
				}

				calendarIcon.setStyleName("okm-Hyperlink");
				hPanel.add(Util.hSpace("5px"));
				hPanel.add(calendarIcon);
				textBox.setEnabled(false);
			} else if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_LINK)) {
				if (!value.equals("")) {
					HorizontalPanel hLinkPanel = new HorizontalPanel();
					Anchor anchor = new Anchor(value, true);
					anchor.setStyleName("okm-Hyperlink");
					final String url = value;

					anchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Window.open(url, url, "");
						}
					});

					hLinkPanel.add(anchor);
					hLinkPanel.add(Util.hSpace("5px"));
					hLinkPanel.add(new Clipboard(url));
					table.setWidget(row, 1, hLinkPanel);
				} else {
					table.setHTML(row, 1, "");
				}
			} else if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_FOLDER)) {
				if (!value.equals("")) {
					Anchor anchor = new Anchor();
					final GWTFolder folder = ((GWTInput) gwtFormElement).getFolder();

					// remove first ocurrence
					String path = value.substring(value.indexOf("/", 1) + 1);

					// Looks if must change icon on parent if now has no childs and properties with user security
					// atention
					if (folder.isHasChildren()) {
						anchor.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", path, "top"));
					} else {
						anchor.setHTML(Util.imageItemHTML("img/menuitem_empty.gif", path, "top"));
					}

					anchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent arg0) {
							CommonUI.openPath(folder.getPath(), null);
						}
					});

					anchor.setStyleName("okm-KeyMap-ImageHover");
					table.setWidget(row, 1, anchor);
				} else {
					table.setHTML(row, 1, "");
				}

				Image pathExplorer = new Image(OKMBundleResources.INSTANCE.folderExplorer());
				pathExplorer.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						// when any changes is done is fired search.metadataValueChanged();
						folderSelectPopup.show(textBox, propertyHandler);
					}
				});

				pathExplorer.setStyleName("okm-KeyMap-ImageHover");
				hPanel.add(Util.hSpace("5px"));
				hPanel.add(pathExplorer);
				hPanel.setCellVerticalAlignment(pathExplorer, HasAlignment.ALIGN_MIDDLE);
				pathExplorer.setVisible((!readOnly && !((GWTInput) gwtFormElement).isReadonly()) || isSearchView); // read only
				textBox.setEnabled(false);
			}

			table.getCellFormatter().setVerticalAlignment(row, 0, VerticalPanel.ALIGN_TOP);
			table.getCellFormatter().setWidth(row, 1, "100%");

			if (searchView || isMassiveView) {
				if (searchView) {
					// Second date input
					if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_DATE)) {
						final TextBox textBoxTo = new TextBox();
						textBoxTo.setWidth(gwtFormElement.getWidth());
						textBoxTo.setStyleName("okm-Input");
						hPanel.add(new HTML("&nbsp;&harr;&nbsp;"));
						hPanel.add(textBoxTo);

						if (((GWTInput) gwtFormElement).getDateTo() != null) {
							DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
							textBoxTo.setText(dtf.format(((GWTInput) gwtFormElement).getDateTo()));
						}

						final PopupPanel calendarPopup = new PopupPanel(true);
						final CalendarWidget calendar = new CalendarWidget();
						calendar.addChangeHandler(new ChangeHandler() {
							@Override
							public void onChange(ChangeEvent event) {
								calendarPopup.hide();
								DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
								textBoxTo.setText(dtf.format(calendar.getDate()));
								((GWTInput) gwtFormElement).setDateTo(calendar.getDate());

								if (propertyHandler != null) {
									propertyHandler.metadataValueChanged();
								}
							}
						});

						calendarPopup.add(calendar);
						final Image calendarIcon = new Image(OKMBundleResources.INSTANCE.calendar());
						calendarIcon.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								calendarPopup.setPopupPosition(calendarIcon.getAbsoluteLeft(),
										calendarIcon.getAbsoluteTop() - 2);
								calendarPopup.show();
							}
						});

						calendarIcon.setStyleName("okm-Hyperlink");
						hPanel.add(Util.hSpace("5px"));
						hPanel.add(calendarIcon);
						textBoxTo.setEnabled(false);

						// Clean
						final Image cleanIcon = new Image(OKMBundleResources.INSTANCE.cleanIcon());
						cleanIcon.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								TextBox textBox = (TextBox) hPanel.getWidget(0);
								textBox.setText("");
								textBoxTo.setText("");
								((GWTInput) gwtFormElement).setDate(null);
								((GWTInput) gwtFormElement).setDateTo(null);
							}
						});
						cleanIcon.setStyleName("okm-Hyperlink");
						hPanel.add(Util.hSpace("5px"));
						hPanel.add(cleanIcon);
					}
				}

				// Delete
				final Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon());
				removeImage.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						for (int row = 0; row < table.getRowCount(); row++) {
							if (table.getWidget(row, 2).equals(removeImage)) {
								table.removeRow(row);
								break;
							}
						}

						hWidgetProperties.remove(propertyName);
						hPropertyParams.remove(propertyName);
						formElementList.remove(gwtFormElement);
						propertyHandler.propertyRemoved();
					}
				});
				removeImage.addStyleName("okm-Hyperlink");
				table.setWidget(row, 2, removeImage);
				table.getCellFormatter().setVerticalAlignment(row, 2, HasAlignment.ALIGN_TOP);

				if (propertyHandler != null) {
					textBox.addKeyUpHandler(new KeyUpHandler() {
						@Override
						public void onKeyUp(KeyUpEvent event) {
							propertyHandler.metadataValueChanged();
						}
					});
				}

				setRowWordWarp(row, 3, true);
			} else {
				// Clean icon ( case is not readonly )
				final Image cleanIcon = new Image(OKMBundleResources.INSTANCE.cleanIcon());
				cleanIcon.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						TextBox textBox = (TextBox) hPanel.getWidget(0);
						textBox.setText("");
						((GWTInput) gwtFormElement).setDate(null);
						((GWTInput) gwtFormElement).setFolder(new GWTFolder());
					}
				});
				cleanIcon.setStyleName("okm-Hyperlink");
				hPanel.add(Util.hSpace("5px"));
				hPanel.add(cleanIcon);
				cleanIcon.setVisible((!readOnly && !((GWTInput) gwtFormElement).isReadonly())); // read only

				setRowWordWarp(row, 2, true);
			}

		} else if (gwtFormElement instanceof GWTSuggestBox) {
			HorizontalPanel hPanel = new HorizontalPanel();
			final GWTSuggestBox suggestBox = (GWTSuggestBox) gwtFormElement;
			final TextBox textBox = new TextBox(); // Create a widget for this property
			textBox.setWidth(gwtFormElement.getWidth());
			textBox.setStyleName("okm-Input");
			textBox.setReadOnly(true);
			textBox.setEnabled((!readOnly && !suggestBox.isReadonly()) || isSearchView); // read only 
			final HTML hiddenKey = new HTML("");
			hiddenKey.setVisible(false);

			if (suggestBox.getValue() != null) {
				hiddenKey.setHTML(suggestBox.getValue());
			}

			hPanel.add(textBox);
			hPanel.add(hiddenKey);
			final HTML value = new HTML("");
			table.setHTML(row, 0, "<b>" + gwtFormElement.getLabel() + "</b>");
			table.setWidget(row, 1, value);
			table.getCellFormatter().setVerticalAlignment(row, 0, VerticalPanel.ALIGN_TOP);
			table.getCellFormatter().setWidth(row, 1, "100%");

			if (textBox.isEnabled()) {
				final Image databaseRecordImage = new Image(OKMBundleResources.INSTANCE.databaseRecord());
				databaseRecordImage.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						List<String> tables = new ArrayList<String>();
						if (suggestBox.getTable() != null) {
							tables.add(suggestBox.getTable());
						}

						DatabaseRecord databaseRecord = new DatabaseRecord(hiddenKey, textBox);
						// when any changes is done is fired search.metadataValueChanged();
						DatabaseRecordSelectPopup drsPopup = new DatabaseRecordSelectPopup(suggestBox, databaseRecord, propertyHandler);
						drsPopup.setWidth("300px");
						drsPopup.setHeight("220px");
						drsPopup.setStyleName("okm-Popup");
						drsPopup.setPopupPosition(databaseRecordImage.getAbsoluteLeft(),
								databaseRecordImage.getAbsoluteTop() - 2);
						drsPopup.show();
					}
				});
				databaseRecordImage.setStyleName("okm-Hyperlink");
				hPanel.add(new HTML("&nbsp;"));
				hPanel.add(databaseRecordImage);
			}

			hWidgetProperties.put(propertyName, hPanel);
			if (!suggestBox.getValue().equals("")) {
				textBox.setValue(suggestBox.getText());
				value.setHTML(suggestBox.getText());
				hiddenKey.setHTML(suggestBox.getValue());
				
				/*List<String> tables = new ArrayList<String>();
				
				if (suggestBox.getTable() != null) {
					tables.add(suggestBox.getTable());
				}
				
				String formatedQuery = MessageFormat.format(suggestBox.getValueQuery(), suggestBox.getValue());
				keyValueService.getKeyValues(tables, formatedQuery, new AsyncCallback<List<GWTKeyValue>>() {
					@Override
					public void onSuccess(List<GWTKeyValue> result) {
						if (!result.isEmpty()) {
							GWTKeyValue keyValue = result.get(0);
							textBox.setValue(keyValue.getValue());
							value.setHTML(keyValue.getValue());
							hiddenKey.setHTML(keyValue.getKey());
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("getKeyValues", caught);
					}
				}); */
			}

			if (searchView || isMassiveView) {
				final Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon());
				removeImage.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						for (int row = 0; row < table.getRowCount(); row++) {
							if (table.getWidget(row, 2).equals(removeImage)) {
								table.removeRow(row);
								break;
							}
						}

						hWidgetProperties.remove(propertyName);
						hPropertyParams.remove(propertyName);
						formElementList.remove(gwtFormElement);
						propertyHandler.propertyRemoved();
					}
				});
				removeImage.addStyleName("okm-Hyperlink");
				table.setWidget(row, 2, removeImage);
				table.getCellFormatter().setVerticalAlignment(row, 2, HasAlignment.ALIGN_TOP);
				textBox.addKeyUpHandler(Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.keyUpHandler);
				setRowWordWarp(row, 3, true);
			} else {
				setRowWordWarp(row, 2, true);
			}
		} else if (gwtFormElement instanceof GWTCheckBox) {
			CheckBox checkBox = new CheckBox();
			checkBox.setEnabled((!readOnly && !((GWTCheckBox) gwtFormElement).isReadonly()) || isSearchView); // read only
			checkBox.setValue(((GWTCheckBox) gwtFormElement).getValue());
			hWidgetProperties.put(propertyName, checkBox);
			table.setHTML(row, 0, "<b>" + gwtFormElement.getLabel() + "</b>");

			if (checkBox.getValue()) {
				table.setWidget(row, 1, new Image(OKMBundleResources.INSTANCE.yes()));
			} else {
				table.setWidget(row, 1, new Image(OKMBundleResources.INSTANCE.no()));
			}

			table.getCellFormatter().setVerticalAlignment(row, 0, VerticalPanel.ALIGN_TOP);
			table.getCellFormatter().setWidth(row, 1, "100%");

			if (searchView || isMassiveView) {
				final Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon());
				removeImage.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						for (int row = 0; row < table.getRowCount(); row++) {
							if (table.getWidget(row, 2).equals(removeImage)) {
								table.removeRow(row);
								break;
							}
						}

						hWidgetProperties.remove(propertyName);
						hPropertyParams.remove(propertyName);
						formElementList.remove(gwtFormElement);
						propertyHandler.propertyRemoved();
					}
				});
				removeImage.addStyleName("okm-Hyperlink");
				table.setWidget(row, 2, removeImage);
				table.getCellFormatter().setVerticalAlignment(row, 2, HasAlignment.ALIGN_TOP);

				if (propertyHandler != null) {
					checkBox.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							propertyHandler.metadataValueChanged();
						}
					});
				}

				setRowWordWarp(row, 3, true);
			} else {
				setRowWordWarp(row, 2, true);
			}
		} else if (gwtFormElement instanceof GWTSelect) {
			final GWTSelect gwtSelect = (GWTSelect) gwtFormElement;

			if (!gwtSelect.getOptionsData().equals("") && workflowVarMap.keySet().contains(gwtSelect.getOptionsData())) {
				gwtSelect.setOptions(getOptionsFromVariable(workflowVarMap.get(gwtSelect.getOptionsData())));
			}

			if (gwtSelect.getType().equals(GWTSelect.TYPE_SIMPLE)) {
				String selectedLabel = "";
				HorizontalPanel hPanel = new HorizontalPanel();
				ListBox listBox = new ListBox();
				listBox.setEnabled((!readOnly && !gwtSelect.isReadonly()) || isSearchView); // read only
				hPanel.add(listBox);
				listBox.setStyleName("okm-Select");
				listBox.addItem("", ""); // Always we set and empty value

				for (GWTOption option : gwtSelect.getOptions()) {
					listBox.addItem(option.getLabel(), option.getValue());
					if (option.isSelected()) {
						listBox.setItemSelected(listBox.getItemCount() - 1, true);
						selectedLabel = option.getLabel();
					}
				}

				// Mark suggested
				if (!gwtSelect.getSuggestion().equals("")) {
					NodeList<Element> nodeList = listBox.getElement().getElementsByTagName("option");
					int count = 1; // 0 is empty value
					for (GWTOption option : gwtSelect.getOptions()) {
						if (nodeList.getLength() < (count)) {
							break;
						}
						if (option.isSuggested()) {
							nodeList.getItem(count).setClassName("okm-Option-Suggested");
						} else {
							nodeList.getItem(count).setClassName("okm-Option");
						}
						count++;
					}
				}

				hWidgetProperties.put(propertyName, hPanel);

				table.setHTML(row, 0, "<b>" + gwtFormElement.getLabel() + "</b>");
				table.setHTML(row, 1, selectedLabel);
				table.getCellFormatter().setWidth(row, 1, "100%");

				if (searchView || isMassiveView) {
					final Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon());
					removeImage.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							for (int row = 0; row < table.getRowCount(); row++) {
								if (table.getWidget(row, 2).equals(removeImage)) {
									table.removeRow(row);
									break;
								}
							}

							hWidgetProperties.remove(propertyName);
							hPropertyParams.remove(propertyName);
							formElementList.remove(gwtFormElement);
							propertyHandler.propertyRemoved();
						}
					});
					removeImage.addStyleName("okm-Hyperlink");
					table.setWidget(row, 2, removeImage);
					table.getCellFormatter().setVerticalAlignment(row, 2, HasAlignment.ALIGN_TOP);

					if (propertyHandler != null) {
						listBox.addChangeHandler(new ChangeHandler() {
							@Override
							public void onChange(ChangeEvent event) {
								propertyHandler.metadataValueChanged();
							}
						});
					}

					setRowWordWarp(row, 3, true);
				} else {
					setRowWordWarp(row, 2, true);
				}

			} else if (gwtSelect.getType().equals(GWTSelect.TYPE_MULTIPLE)) {
				final HorizontalPanel hPanel = new HorizontalPanel();
				ListBox listMulti = new ListBox();
				listMulti.setEnabled((!readOnly && !gwtSelect.isReadonly()) || isSearchView); // read only
				listMulti.setStyleName("okm-Select");
				listMulti.addItem("", ""); // Always we set and empty value

				// Table for values
				FlexTable tableMulti = new FlexTable();

				Button addButton = new Button(Main.i18n("button.add"), new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(propertyName);
						FlexTable tableMulti = (FlexTable) hPanel.getWidget(0);
						ListBox listMulti = (ListBox) hPanel.getWidget(2);
						Button addButton = (Button) hPanel.getWidget(4);

						if (listMulti.getSelectedIndex() > 0) {
							final HTML htmlValue = new HTML(listMulti.getValue(listMulti.getSelectedIndex()));
							int rowTableMulti = tableMulti.getRowCount();
							Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon());

							removeImage.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									Widget sender = (Widget) event.getSource();
									HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(propertyName);
									FlexTable tableMulti = (FlexTable) hPanel.getWidget(0);
									ListBox listMulti = (ListBox) hPanel.getWidget(2);
									Button addButton = (Button) hPanel.getWidget(4);
									String value = htmlValue.getText();
									String optionLabel = "";

									for (Iterator<GWTOption> itOptions = gwtSelect.getOptions().iterator(); itOptions
											.hasNext(); ) {
										GWTOption option = itOptions.next();
										if (option.getValue().equals(htmlValue.getText())) {
											optionLabel = option.getLabel();
											break;
										}
									}

									listMulti.addItem(optionLabel, value);
									listMulti.setVisible(true);
									addButton.setVisible(true);

									// Looking for row to delete
									for (int i = 0; i < tableMulti.getRowCount(); i++) {
										if (tableMulti.getWidget(i, 1).equals(sender)) {
											tableMulti.removeRow(i);
										}
									}

									if (propertyHandler != null) {
										propertyHandler.metadataValueChanged();
									}
								}
							});
							removeImage.setStyleName("okm-Hyperlink");

							tableMulti.setWidget(rowTableMulti, 0, htmlValue);
							tableMulti.setWidget(rowTableMulti, 1, removeImage);
							tableMulti.setHTML(rowTableMulti, 2, listMulti.getItemText(listMulti.getSelectedIndex()));

							setRowWordWarp(tableMulti, rowTableMulti, 2, true);
							listMulti.removeItem(listMulti.getSelectedIndex());
							htmlValue.setVisible(false);

							if (listMulti.getItemCount() <= 1) {
								listMulti.setVisible(false);
								addButton.setVisible(false);
							}

							if (propertyHandler != null) {
								propertyHandler.metadataValueChanged();
							}
						}
					}
				});

				addButton.setEnabled((!readOnly && !gwtSelect.isReadonly()) || isSearchView); // read only
				addButton.setStyleName("okm-AddButton");

				hPanel.add(tableMulti);
				hPanel.add(new HTML("&nbsp;"));
				hPanel.add(listMulti);
				hPanel.add(new HTML("&nbsp;"));
				hPanel.add(addButton);
				hPanel.setVisible(true);
				listMulti.setVisible(false);
				addButton.setVisible(false);
				hPanel.setCellVerticalAlignment(tableMulti, VerticalPanel.ALIGN_TOP);
				hPanel.setCellVerticalAlignment(listMulti, VerticalPanel.ALIGN_TOP);
				hPanel.setCellVerticalAlignment(addButton, VerticalPanel.ALIGN_TOP);
				hPanel.setHeight("100%");

				table.setHTML(row, 0, "<b>" + gwtFormElement.getLabel() + "</b>");
				table.setWidget(row, 1, hPanel);
				table.getCellFormatter().setVerticalAlignment(row, 0, VerticalPanel.ALIGN_TOP);
				table.getCellFormatter().setVerticalAlignment(row, 1, VerticalPanel.ALIGN_TOP);
				table.getCellFormatter().setWidth(row, 1, "100%");

				for (Iterator<GWTOption> itData = gwtSelect.getOptions().iterator(); itData.hasNext(); ) {
					final GWTOption option = itData.next();

					// Looks if there's some selected value
					if (option.isSelected()) {
						int rowTableMulti = tableMulti.getRowCount();
						HTML htmlValue = new HTML(option.getValue());

						Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon()); // read only for this element goes at edit() logic
						removeImage.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								Widget sender = (Widget) event.getSource();
								HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(propertyName);
								FlexTable tableMulti = (FlexTable) hPanel.getWidget(0);
								ListBox listMulti = (ListBox) hPanel.getWidget(2);
								Button addButton = (Button) hPanel.getWidget(4);

								listMulti.addItem(option.getLabel(), option.getValue());
								listMulti.setVisible(true);
								addButton.setVisible(true);

								// Looking for row to delete
								for (int i = 0; i < tableMulti.getRowCount(); i++) {
									if (tableMulti.getWidget(i, 1).equals(sender)) {
										tableMulti.removeRow(i);
									}
								}

								if (propertyHandler != null) {
									propertyHandler.metadataValueChanged();
								}
							}
						});
						removeImage.setStyleName("okm-Hyperlink");

						tableMulti.setWidget(rowTableMulti, 0, htmlValue);
						tableMulti.setWidget(rowTableMulti, 1, removeImage);
						tableMulti.setHTML(rowTableMulti, 2, option.getLabel());
						setRowWordWarp(tableMulti, rowTableMulti, 2, true);
						htmlValue.setVisible(false);
						removeImage.setVisible(false);
					} else {
						listMulti.addItem(option.getLabel(), option.getValue());
					}
				}

				// Mark suggested
				if (!gwtSelect.getSuggestion().equals("")) {
					NodeList<Element> nodeList = listMulti.getElement().getElementsByTagName("option");
					int count = 1; // 0 is empty value
					for (GWTOption option : gwtSelect.getOptions()) {
						// In list only are shown not selected items
						if (!option.isSelected()) {
							if (nodeList.getLength() < (count)) {
								break;
							}
							if (option.isSuggested()) {
								nodeList.getItem(count).setClassName("okm-Option-Suggested");
							} else {
								nodeList.getItem(count).setClassName("okm-Option");
							}
							count++;
						}
					}
				}

				// Save panel
				hWidgetProperties.put(propertyName, hPanel);

				if (searchView || isMassiveView) {
					final Image removeImage = new Image(OKMBundleResources.INSTANCE.deleteIcon());
					removeImage.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							for (int row = 0; row < table.getRowCount(); row++) {
								if (table.getWidget(row, 2).equals(removeImage)) {
									table.removeRow(row);
									break;
								}
							}

							hWidgetProperties.remove(propertyName);
							hPropertyParams.remove(propertyName);
							formElementList.remove(gwtFormElement);
							propertyHandler.propertyRemoved();
						}
					});
					removeImage.addStyleName("okm-Hyperlink");
					table.setWidget(row, 2, removeImage);
					table.getCellFormatter().setVerticalAlignment(row, 2, HasAlignment.ALIGN_TOP);

					// not implemented
					// textBox.addKeyUpHandler(Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.keyUpHandler);
					setRowWordWarp(row, 3, true);
				} else {
					setRowWordWarp(row, 2, true);
				}
			}
		} else if (gwtFormElement instanceof GWTUpload) {
			final GWTUpload upload = (GWTUpload) gwtFormElement;
			HorizontalPanel hPanel = new HorizontalPanel();
			FileUpload fileUpload = new FileUpload();
			fileUpload.setStyleName("okm-Input");
			fileUpload.getElement().setAttribute("size", "" + upload.getWidth());
			final Anchor documentLink = new Anchor();

			// Setting document link by uuid
			if (upload.getDocumentUuid() != null && !upload.getDocumentUuid().equals("")) {
				repositoryService.getPathByUUID(upload.getDocumentUuid(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						documentService.get(result, new AsyncCallback<GWTDocument>() {
							@Override
							public void onSuccess(GWTDocument result) {
								final String docPath = result.getPath();
								documentLink.setText(result.getName());
								documentLink.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										CommonUI.openPath(Util.getParent(docPath), docPath);
									}
								});
							}

							@Override
							public void onFailure(Throwable caught) {
								Main.get().showError("getDocument", caught);
							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("getPathByUUID", caught);
					}
				});
			}

			documentLink.setStyleName("okm-Hyperlink");
			hPanel.add(documentLink);
			hPanel.add(fileUpload);
			hWidgetProperties.put(propertyName, hPanel);
			table.setHTML(row, 0, "<b>" + gwtFormElement.getLabel() + "</b>");
			table.setWidget(row, 1, new HTML(""));
			table.getCellFormatter().setVerticalAlignment(row, 0, VerticalPanel.ALIGN_TOP);
			table.getCellFormatter().setWidth(row, 1, "100%");
			setRowWordWarp(row, 2, true);

			// If folderPath is null must initialize value
			if (upload.getFolderPath() == null || upload.getFolderPath().equals("") && upload.getFolderUuid() != null
					&& !upload.getFolderUuid().equals("")) {
				repositoryService.getPathByUUID(upload.getFolderUuid(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						upload.setFolderPath(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Main.get().showError("getPathByUUID", caught);
					}
				});
			}
		} else if (gwtFormElement instanceof GWTText) {
			HorizontalPanel hPanel = new HorizontalPanel();
			HTML title = new HTML("&nbsp;" + ((GWTText) gwtFormElement).getLabel() + "&nbsp;");
			title.setStyleName("okm-NoWrap");
			hPanel.add(Util.hSpace("10px"));
			hPanel.add(title);
			hPanel.setCellWidth(title, ((GWTText) gwtFormElement).getWidth());
			hWidgetProperties.put(propertyName, hPanel);
			table.setWidget(row, 0, hPanel);
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
		} else if (gwtFormElement instanceof GWTSeparator) {
			HorizontalPanel hPanel = new HorizontalPanel();
			Image horizontalLine = new Image("img/transparent_pixel.gif");
			horizontalLine.setStyleName("okm-TopPanel-Line-Border");
			horizontalLine.setSize("10px", "2px");
			Image horizontalLine2 = new Image("img/transparent_pixel.gif");
			horizontalLine2.setStyleName("okm-TopPanel-Line-Border");
			horizontalLine2.setSize("100%", "2px");
			HTML title = new HTML("&nbsp;" + ((GWTSeparator) gwtFormElement).getLabel() + "&nbsp;");
			title.setStyleName("okm-NoWrap");
			hPanel.add(horizontalLine);
			hPanel.add(title);
			hPanel.add(horizontalLine2);
			hPanel.setCellVerticalAlignment(horizontalLine, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(horizontalLine2, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellWidth(horizontalLine2, ((GWTSeparator) gwtFormElement).getWidth());
			hWidgetProperties.put(propertyName, hPanel);
			table.setWidget(row, 0, hPanel);
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
		} else if (gwtFormElement instanceof GWTDownload) {
			HorizontalPanel hPanel = new HorizontalPanel();
			hWidgetProperties.put(propertyName, hPanel);
			table.setWidget(row, 0, hPanel);
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
			GWTDownload download = (GWTDownload) gwtFormElement;
			FlexTable downloadTable = new FlexTable();
			HTML description = new HTML("<b>" + gwtFormElement.getLabel() + "</b>");
			downloadTable.setWidget(0, 0, description);
			downloadTable.getFlexCellFormatter().setColSpan(0, 0, 2);

			for (final GWTNode node : download.getNodes()) {
				int downloadTableRow = downloadTable.getRowCount();
				final Anchor anchor = new Anchor("<b>" + node.getLabel() + "</b>", true);

				if (!node.getUuid().equals("")) {
					repositoryService.getPathByUUID(node.getUuid(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							folderService.isValid(result, new AsyncCallback<Boolean>() {
								@Override
								public void onSuccess(Boolean result) {
									final boolean isFolder = result;
									anchor.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											if (isFolder) {
												Util.downloadFileByUUID(node.getUuid(), "export");
											} else {
												Util.downloadFileByUUID(node.getUuid(), "");
											}
										}
									});
								}

								@Override
								public void onFailure(Throwable caught) {
									Main.get().showError("getPathByUUID", caught);
								}
							});
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("getPathByUUID", caught);
						}
					});
				} else if (!node.getPath().equals("")) {
					repositoryService.getUUIDByPath(node.getPath(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							final String uuid = result;
							folderService.isValid(node.getPath(), new AsyncCallback<Boolean>() {
								@Override
								public void onSuccess(Boolean result) {
									final boolean isFolder = result;
									anchor.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											if (isFolder) {
												Util.downloadFileByUUID(uuid, "export");
											} else {
												Util.downloadFileByUUID(uuid, "");
											}
										}
									});
								}

								@Override
								public void onFailure(Throwable caught) {
									Main.get().showError("getPathByUUID", caught);
								}
							});
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("getUUIDByPath", caught);
						}
					});
				}

				anchor.setStyleName("okm-Hyperlink");
				downloadTable.setWidget(downloadTableRow, 0, new HTML("&nbsp;&nbsp;&nbsp;"));
				downloadTable.setWidget(downloadTableRow, 1, anchor);
			}

			hPanel.add(downloadTable);
		} else if (gwtFormElement instanceof GWTPrint) {
			HorizontalPanel hPanel = new HorizontalPanel();
			hWidgetProperties.put(propertyName, hPanel);
			table.setWidget(row, 0, hPanel);
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
			GWTPrint print = (GWTPrint) gwtFormElement;
			FlexTable printTable = new FlexTable();
			HTML description = new HTML("<b>" + gwtFormElement.getLabel() + "</b>");
			printTable.setWidget(0, 0, description);
			printTable.getFlexCellFormatter().setColSpan(0, 0, 2);

			for (final GWTNode node : print.getNodes()) {
				int downloadTableRow = printTable.getRowCount();
				final Button downloadButton = new Button(Main.i18n("button.print"));

				if (!node.getUuid().equals("")) {
					downloadButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Util.print(node.getUuid());
						}
					});
				} else if (!node.getPath().equals("")) {
					repositoryService.getUUIDByPath(node.getPath(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							final String uuid = result;
							downloadButton.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									Util.print(uuid);
								}
							});
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("getUUIDByPath", caught);
						}
					});
				}

				downloadButton.setStyleName("okm-DownloadButton");
				printTable.setWidget(downloadTableRow, 0, new HTML("&nbsp;&nbsp;&nbsp;" + node.getLabel()
						+ "&nbsp;&nbsp;"));
				printTable.setWidget(downloadTableRow, 1, downloadButton);
			}

			hPanel.add(printTable);
		}
	}

	/**
	 * getDrawedFormElement
	 * <p>
	 * Called externally by file browser to draw form element, really used to do not reply draw logic and use actual
	 * Use with caution table elements will be removed after executed this method
	 *
	 * @param gwtFormElement
	 * @return
	 */
	public Widget getDrawFormElement(GWTFormElement gwtFormElement) {
		Widget widget = new HTML(""); // empty widget ( used in case not applicable form elements )
		if (gwtFormElement == null) {  // To show empty value
			return widget;
		}
		int row = table.getRowCount();
		drawFormElement(row, gwtFormElement, false, false);
		if (gwtFormElement instanceof GWTTextArea) {
			widget = table.getWidget(row, 1);
		} else if (gwtFormElement instanceof GWTInput) {
			if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_LINK) ||
					((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_FOLDER)) {
				widget = table.getWidget(row, 1);
			} else {
				widget = new HTML(table.getText(row, 1));
			}
		} else if (gwtFormElement instanceof GWTSuggestBox) {
			widget = new HTML(((GWTSuggestBox) gwtFormElement).getText());
		} else if (gwtFormElement instanceof GWTCheckBox) {
			widget = table.getWidget(row, 1);
		} else if (gwtFormElement instanceof GWTSelect) {
			if (((GWTSelect) gwtFormElement).getType().equals(GWTSelect.TYPE_SIMPLE)) {
				widget = new HTML(table.getText(row, 1));
			} else if (((GWTSelect) gwtFormElement).getType().equals(GWTSelect.TYPE_MULTIPLE)) {
				String selectedValues = "";
				for (GWTOption opt : ((GWTSelect) gwtFormElement).getOptions()) {
					if (opt.isSelected()) {
						if (selectedValues.length() > 0) {
							selectedValues += ", ";
						}
						selectedValues += opt.getValue();
					}
				}
				widget = new HTML(selectedValues);
			}
		} else if (gwtFormElement instanceof GWTUpload) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTText) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTSeparator) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTDownload) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTPrint) {
			// Not aplicable
		}
		table.removeAllRows();
		return widget;
	}

	/**
	 * getDrawEditFormElement
	 * <p>
	 * Called externally by file browser to draw form element, really used to do not reply draw logic and use actual
	 * Use with caution table elements will be removed after executed this method
	 *
	 * @param gwtFormElement
	 * @return
	 */
	public Widget getDrawEditFormElement(GWTFormElement gwtFormElement, HasPropertyHandler propertyHandler) {
		this.propertyHandler = propertyHandler;
		Widget widget = new HTML(""); // empty widget ( used in case not applicable form elements )
		if (gwtFormElement == null) {  // To show empty value
			return widget;
		}
		int row = table.getRowCount();
		isSearchView = true;
		setFormElements(Arrays.asList(gwtFormElement)); // Initilizing form element list ( needed by edit 
		drawFormElement(row, gwtFormElement, false, isSearchView);
		drawed = true;
		edit();
		if (gwtFormElement instanceof GWTTextArea) {
			HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
			widget = hPanel.getWidget(0);
		} else if (gwtFormElement instanceof GWTInput) {
			HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
			if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_TEXT)) {
				widget = hPanel.getWidget(0);
			} else if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_DATE)) {
				hPanel.remove(8); // Removing delete icon
				return hPanel;
			} else if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_FOLDER)) {
				return hPanel;
			} else if (((GWTInput) gwtFormElement).getType().equals(GWTInput.TYPE_LINK)) {
				return hPanel;
			}
		} else if (gwtFormElement instanceof GWTSuggestBox) {
			return table.getWidget(row, 1);
		} else if (gwtFormElement instanceof GWTCheckBox) {
			return table.getWidget(row, 1);
		} else if (gwtFormElement instanceof GWTSelect) {
			if (((GWTSelect) gwtFormElement).getType().equals(GWTSelect.TYPE_SIMPLE)) {
				HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
				return hPanel.getWidget(0);
			} else if (((GWTSelect) gwtFormElement).getType().equals(GWTSelect.TYPE_MULTIPLE)) {
				HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
				return hPanel;
			}
		} else if (gwtFormElement instanceof GWTUpload) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTText) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTSeparator) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTDownload) {
			// Not aplicable
		} else if (gwtFormElement instanceof GWTPrint) {
			// Not aplicable
		}
		table.removeAllRows();
		return widget;
	}

	/**
	 * Edit values
	 */
	public void edit() {
		// Before edit must be always drawed
		if (!drawed) {
			draw(readOnly);
		}

		buildValidators();

		// Always ad submit form at ends
		if (submitForm != null) {
			HTML space = new HTML("&nbsp;");
			submitButtonPanel.add(submitForm);
			submitButtonPanel.add(space);
			submitButtonPanel.setCellWidth(space, "5px");
			int row = table.getRowCount();
			table.setWidget(row, 0, submitButtonPanel);
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
			table.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasAlignment.ALIGN_CENTER);
		}
	}

	/**
	 * buildValidators
	 */
	public void buildValidators() {
		int rows = 0;
		validationProcessor = new ExtendedDefaultValidatorProcessor(validatorToFire);
		FocusAction focusAction = new FocusAction();

		for (Iterator<GWTFormElement> it = formElementList.iterator(); it.hasNext(); ) {
			GWTFormElement formField = it.next();

			if (formField instanceof GWTTextArea) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formField.getName());
				table.setWidget(rows, 1, hPanel);

				for (GWTValidator validator : ((GWTTextArea) formField).getValidators()) {
					TextArea textArea = (TextArea) hPanel.getWidget(0);
					ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "textarea_" + rows,
							validator, textArea);
				}
			} else if (formField instanceof GWTInput) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formField.getName());
				table.setWidget(rows, 1, hPanel);

				for (GWTValidator validator : ((GWTInput) formField).getValidators()) {
					TextBox textBox = (TextBox) hPanel.getWidget(0);
					ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "input_" + rows, validator,
							textBox);
				}
			} else if (formField instanceof GWTSuggestBox) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formField.getName());
				table.setWidget(rows, 1, hPanel);

				for (GWTValidator validator : ((GWTSuggestBox) formField).getValidators()) {
					TextBox textBox = (TextBox) hPanel.getWidget(0);
					ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "suggestbox_" + rows,
							validator, textBox);
				}
			} else if (formField instanceof GWTCheckBox) {
				CheckBox checkBox = (CheckBox) hWidgetProperties.get(formField.getName());
				table.setWidget(rows, 1, checkBox);
			} else if (formField instanceof GWTSelect) {
				GWTSelect gwtSelect = (GWTSelect) formField;

				if (gwtSelect.getType().equals(GWTSelect.TYPE_SIMPLE)) {
					HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formField.getName());
					ListBox listBox = (ListBox) hPanel.getWidget(0);
					table.setWidget(rows, 1, hPanel);

					for (GWTValidator validator : ((GWTSelect) formField).getValidators()) {
						ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "select_" + rows,
								validator, listBox);
					}
				} else if (gwtSelect.getType().equals(GWTSelect.TYPE_MULTIPLE)) {
					HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formField.getName());
					FlexTable tableMulti = (FlexTable) hPanel.getWidget(0);
					ListBox listMulti = (ListBox) hPanel.getWidget(2);
					Button addButton = (Button) hPanel.getWidget(4);

					// Only it there's some element to assign must set it visible.
					if (listMulti.getItemCount() > 1) {
						listMulti.setVisible((!readOnly && !gwtSelect.isReadonly()) || isSearchView); // read only
						addButton.setVisible((!readOnly && !gwtSelect.isReadonly()) || isSearchView); // read only);
					}

					// Enables deleting option
					for (int i = 0; i < tableMulti.getRowCount(); i++) {
						((Image) tableMulti.getWidget(i, 1)).setVisible((!readOnly && !gwtSelect.isReadonly()) || isSearchView); // read only
					}

					table.setWidget(rows, 1, hPanel);

					for (GWTValidator validator : ((GWTSelect) formField).getValidators()) {
						ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "select_" + rows,
								validator, tableMulti);
					}
				}
			} else if (formField instanceof GWTUpload) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formField.getName());
				table.setWidget(rows, 1, hPanel);

				for (GWTValidator validator : ((GWTUpload) formField).getValidators()) {
					FileUpload fileUpload = (FileUpload) hPanel.getWidget(1);
					ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "fileupload_" + rows,
							validator, fileUpload);
				}
			} else if (formField instanceof GWTText) {
				// Nothing to be done here
			} else if (formField instanceof GWTSeparator) {
				// Nothing to be done here
			} else if (formField instanceof GWTDownload) {
				// Nothing to be done here
			} else if (formField instanceof GWTPrint) {
				// Nothing to be done here
			}

			rows++;
		}
	}

	/**
	 * setFormElements
	 *
	 * @param formElementList
	 */
	public void setFormElements(List<GWTFormElement> formElementList) {
		drawed = false;
		hWidgetProperties.clear();
		hPropertyParams.clear();
		this.formElementList = formElementList;

	}

	/**
	 * initButtonControlList
	 */
	private void initButtonControlList() {
		buttonControlList = new ArrayList<Button>(); // Ensure button list is empty
		if (submitForm != null) {
			buttonControlList.add(submitForm);
		}
	}

	/**
	 * disableAllButtonList
	 */
	private void disableAllButtonList() {
		for (Button button : buttonControlList) {
			button.setEnabled(false);
		}
	}

	/**
	 * addPropertyParam
	 */
	public void addPropertyParam(GWTPropertyParams propertyParam) {
		updateFormElementsValuesWithNewer(); // save values
		drawed = false;

		if (!hWidgetProperties.containsKey(propertyParam.getFormElement().getName())) {
			hPropertyParams.put(propertyParam.getFormElement().getName(), propertyParam);
			formElementList.add(propertyParam.getFormElement());
			GWTFormElement formElement = propertyParam.getFormElement();

			if (propertyParam.getValue() != null) {
				if (formElement instanceof GWTInput) {
					GWTInput input = (GWTInput) formElement;

					if (((GWTInput) formElement).getType().equals(GWTInput.TYPE_DATE)) {
						if (!propertyParam.getValue().equals("")) {
							String date[] = propertyParam.getValue().split(",");
							input.setDate(ISO8601.parseBasic(date[0]));

							if (date.length == 2) {
								input.setDateTo(ISO8601.parseBasic(date[1]));
							}
						}
					} else {
						input.setValue(propertyParam.getValue());
					}
				} else if (formElement instanceof GWTTextArea) {
					((GWTTextArea) formElement).setValue(propertyParam.getValue());
				} else if (formElement instanceof GWTSuggestBox) {
					((GWTSuggestBox) formElement).setValue(propertyParam.getValue());
				} else if (formElement instanceof GWTCheckBox) {
					((GWTCheckBox) formElement).setValue(Boolean.parseBoolean(propertyParam.getValue()));
				} else if (formElement instanceof GWTSelect) {
					String value[] = propertyParam.getValue().split(",");
					GWTSelect select = (GWTSelect) formElement;

					for (GWTOption option : select.getOptions()) {
						for (int i = 0; i < value.length; i++) {
							if (option.getValue().equals(value[i])) {
								option.setSelected(true);
							} else {
								option.setSelected(false);
							}
						}
					}
				} else if (formElement instanceof GWTUpload) {
					// Not aplicable to property groups
				} else if (formElement instanceof GWTText) {
					((GWTText) formElement).setLabel(propertyParam.getValue());
				} else if (formElement instanceof GWTSeparator) {
					// Nothing to be done here
				} else if (formElement instanceof GWTDownload) {
					// Nothing to be done here
				} else if (formElement instanceof GWTPrint) {
					// Nothing to be done here
				}
			}
		}
	}

	/**
	 * draw
	 */
	public void draw() {
		draw(false);
	}

	/**
	 * draw
	 */
	public void draw(boolean readOnly) {
		this.readOnly = readOnly;
		table.removeAllRows();
		submitButtonPanel.clear();
		initButtonControlList();
		int rows = 0;

		for (GWTFormElement formElement : formElementList) {
			drawFormElement(rows, formElement, readOnly, isSearchView);
			rows++;
		}

		drawed = true;
	}

	/**
	 * updateFormElements
	 */
	public List<GWTFormElement> getFormElements() {
		return formElementList;
	}

	/**
	 * getPropertyParams
	 */
	public Map<String, GWTPropertyParams> getPropertyParams() {
		for (GWTFormElement formElement : updateFormElementsValuesWithNewer()) {
			String value = "";

			if (formElement instanceof GWTInput) {
				if (((GWTInput) formElement).getType().equals(GWTInput.TYPE_DATE)) {
					GWTInput input = (GWTInput) formElement;
					value = ISO8601.formatBasic(input.getDate());

					if (input.getDateTo() != null) {
						value += "," + ISO8601.formatBasic(input.getDateTo());
					} else {
						value += "," + value;
					}
				} else {
					value = ((GWTInput) formElement).getValue();
				}
			} else if (formElement instanceof GWTTextArea) {
				value = ((GWTTextArea) formElement).getValue();
			} else if (formElement instanceof GWTSuggestBox) {
				value = ((GWTSuggestBox) formElement).getValue();
			} else if (formElement instanceof GWTCheckBox) {
				value = String.valueOf(((GWTCheckBox) formElement).getValue());
			} else if (formElement instanceof GWTSelect) {
				GWTSelect select = (GWTSelect) formElement;

				for (GWTOption option : select.getOptions()) {
					if (option.isSelected()) {
						if (!value.equals("")) {
							value += ",";
						}
						value += option.getValue();
					}
				}
			} else if (formElement instanceof GWTUpload) {
				// Not aplicable to property groups
			} else if (formElement instanceof GWTText) {
				// Nothing to be done here
			} else if (formElement instanceof GWTSeparator) {
				// Nothing to be done here
			} else if (formElement instanceof GWTDownload) {
				// Nothing to be done here
			} else if (formElement instanceof GWTPrint) {
				// Nothing to be done here
			}

			hPropertyParams.get(formElement.getName()).setValue(value);
		}

		return hPropertyParams;
	}

	/**
	 * updateFormElementsWithNewer
	 */
	public List<GWTFormElement> updateFormElementsValuesWithNewer() {
		for (GWTFormElement formElement : formElementList) {
			if (formElement instanceof GWTTextArea) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formElement.getName());
				TextArea textArea = (TextArea) hPanel.getWidget(0);
				((GWTTextArea) formElement).setValue(textArea.getText());
			} else if (formElement instanceof GWTInput) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formElement.getName());
				TextBox textBox = (TextBox) hPanel.getWidget(0);
				((GWTInput) formElement).setValue(textBox.getText());

				// note that date is added by click handler in drawform method
				if (((GWTInput) formElement).getType().equals(GWTInput.TYPE_FOLDER)) {
					// Must be updated folder in GWTInput because must be drawn
					GWTFolder folder = new GWTFolder();
					folder.setPath(textBox.getText());
					((GWTInput) formElement).setFolder(folder);
				}
			} else if (formElement instanceof GWTSuggestBox) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formElement.getName());
				HTML hiddenKey = (HTML) hPanel.getWidget(1);
				GWTSuggestBox suggestBox = (GWTSuggestBox) formElement;
				suggestBox.setValue(hiddenKey.getHTML());
				updateSuggestBoxTextValue(suggestBox);
			} else if (formElement instanceof GWTCheckBox) {
				CheckBox checkbox = (CheckBox) hWidgetProperties.get(formElement.getName());
				((GWTCheckBox) formElement).setValue(checkbox.getValue());
			} else if (formElement instanceof GWTSelect) {
				GWTSelect gwtSelect = (GWTSelect) formElement;

				if (gwtSelect.getType().equals(GWTSelect.TYPE_SIMPLE)) {
					HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formElement.getName());
					ListBox listBox = (ListBox) hPanel.getWidget(0);
					String selectedValue = "";

					if (listBox.getSelectedIndex() > 0) {
						selectedValue = listBox.getValue(listBox.getSelectedIndex());
					}

					for (Iterator<GWTOption> itOptions = gwtSelect.getOptions().iterator(); itOptions.hasNext(); ) {
						GWTOption option = itOptions.next();
						if (option.getValue().equals(selectedValue)) {
							option.setSelected(true);
						} else {
							option.setSelected(false);
						}
					}
				} else if (gwtSelect.getType().equals(GWTSelect.TYPE_MULTIPLE)) {
					HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formElement.getName());
					FlexTable tableMulti = (FlexTable) hPanel.getWidget(0);

					// Disables all options
					for (Iterator<GWTOption> itOptions = gwtSelect.getOptions().iterator(); itOptions.hasNext(); ) {
						itOptions.next().setSelected(false);
					}

					// Enables options
					if (tableMulti.getRowCount() > 0) {
						for (int i = 0; i < tableMulti.getRowCount(); i++) {
							String selectedValue = tableMulti.getText(i, 0);
							for (Iterator<GWTOption> itOptions = gwtSelect.getOptions().iterator(); itOptions.hasNext(); ) {
								GWTOption option = itOptions.next();
								if (option.getValue().equals(selectedValue)) {
									option.setSelected(true);
								}
							}
						}
					}
				}
			} else if (formElement instanceof GWTUpload) {
				// Nothing to be done here, upload files are updated in file upload widget
			} else if (formElement instanceof GWTText) {
				// Nothing to be done here
			} else if (formElement instanceof GWTSeparator) {
				// Nothing to be done here
			} else if (formElement instanceof GWTDownload) {
				// Nothing to be done here
			} else if (formElement instanceof GWTPrint) {
				// Nothing to be done here
			}
		}

		return formElementList;
	}

	/**
	 * hasFileUploadFormElement
	 */
	public boolean hasFileUploadFormElement() {
		boolean found = false;

		for (GWTFormElement formElement : formElementList) {
			if (formElement instanceof GWTUpload) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formElement.getName());
				FileUpload fileUpload = (FileUpload) hPanel.getWidget(1);

				if (!fileUpload.getFilename().equals("")) {
					found = true;
				}

				break;
			}
		}

		return found;
	}

	/**
	 * getFilesToUpload
	 */
	public Collection<FileToUpload> getFilesToUpload(String transition) {
		List<FileToUpload> filesToUpload = new ArrayList<FileToUpload>();
		int rows = 0;

		for (GWTFormElement formElement : formElementList) {
			if (formElement instanceof GWTUpload) {
				HorizontalPanel hPanel = (HorizontalPanel) hWidgetProperties.get(formElement.getName());
				table.setWidget(rows, 1, hPanel);
				FileUpload fileUpload = (FileUpload) hPanel.getWidget(1);

				if (!fileUpload.getFilename().equals("")) {
					hPanel.remove(fileUpload);
					hPanel.add(new HTML(fileUpload.getFilename())); // replace uploadfile widget to text file
					FileToUpload fileToUpload = new FileToUpload();
					GWTUpload upload = (GWTUpload) formElement;

					if (upload.getType().equals(GWTUpload.TYPE_CREATE)) {
						fileToUpload.setAction(UIFileUploadConstants.ACTION_INSERT);
					} else if (upload.getType().equals(GWTUpload.TYPE_UPDATE)) {
						fileToUpload.setAction(UIFileUploadConstants.ACTION_UPDATE);
					}

					fileToUpload.setName(formElement.getName());
					fileToUpload.setFileUpload(fileUpload);
					fileToUpload.setSize(upload.getWidth());
					fileToUpload.setFireEvent(false);
					fileToUpload.setPath(upload.getFolderPath());
					fileToUpload.setDesiredDocumentName(upload.getDocumentName());
					fileToUpload.setWorkflow(workflow);
					fileToUpload.setLastToBeUploaded(false);
					fileToUpload.setEnableAddButton(false);
					fileToUpload.setEnableImport(false);
					fileToUpload.setWorkflowTaskId(taskInstance.getId());
					fileToUpload.setWorkflowTransition(transition);
					filesToUpload.add(fileToUpload);
				}
			}

			rows++;
		}

		// Indicates is the last file to be upload in the cycle
		if (filesToUpload.size() > 0) {
			filesToUpload.get(filesToUpload.size() - 1).setLastToBeUploaded(true);
		}

		return filesToUpload;
	}

	/**
	 * updateFilesToUpload
	 */
	public void updateFilesToUpload(Collection<FileToUpload> filesToUpload) {
		for (FileToUpload fileToUpload : filesToUpload) {
			for (GWTFormElement formElement : formElementList) {
				if (formElement.getName().equals(fileToUpload.getName())) {
					GWTUpload upload = (GWTUpload) formElement;
					upload.setDocumentUuid(fileToUpload.getDocumentUUID());
				}
			}
		}
	}

	/**
	 * loadDataFromPropertyGroupVariables
	 */
	public void loadDataFromPropertyGroupVariables(Map<String, GWTFormElement> map) {
		// Only iterate if really there's some variable to be mapped
		if (!map.isEmpty()) {
			for (GWTFormElement formElement : formElementList) {
				if (map.containsKey(formElement.getName())) {
					if (formElement instanceof GWTTextArea) {
						GWTTextArea textArea = (GWTTextArea) formElement;
						textArea.setValue(getStringValueFromVariable(map.get(formElement.getName())));
					} else if (formElement instanceof GWTInput) {
						GWTInput input = (GWTInput) formElement;
						input.setValue(getStringValueFromVariable(map.get(formElement.getName())));

						if (input.getType().equals(GWTInput.TYPE_DATE)) {
							if (!"".equals(input.getValue())) {
								Date date = ISO8601.parseBasic(input.getValue());

								if (date != null) {
									input.setDate(date);
								} else {
									Log.warn("Input '" + input.getName() + "' value should be in ISO8601 format: "
											+ input.getValue());
								}
							}
						}
					} else if (formElement instanceof GWTSuggestBox) {
						GWTSuggestBox suggestBox = (GWTSuggestBox) formElement;
						suggestBox.setValue(getStringValueFromVariable(map.get(formElement.getName())));
						updateSuggestBoxTextValue(suggestBox);
					} else if (formElement instanceof GWTCheckBox) {
						GWTCheckBox checkBox = (GWTCheckBox) formElement;
						checkBox.setValue(getBooleanValueFromVariable(map.get(formElement.getName())));
					} else if (formElement instanceof GWTSelect) {
						GWTSelect select = (GWTSelect) formElement;
						select.setOptions(getOptionsValueFromVariable(formElement.getName(), select.getOptions()));
					} else if (formElement instanceof GWTUpload) {
						// No aplicable to property groups
					} else if (formElement instanceof GWTText) {
						GWTText text = (GWTText) formElement;
						text.setLabel(getStringValueFromVariable(map.get(formElement.getName())));
					} else if (formElement instanceof GWTSeparator) {
						// Nothing to be done here
					} else if (formElement instanceof GWTDownload) {
						// Nothing to be done here
					} else if (formElement instanceof GWTPrint) {
						// Nothing to be done here
					}
				}
			}
		}
	}

	/**
	 *
	 */
	public void loadDataFromWorkflowVariables(Map<String, Object> map) {
		workflowVarMap = map;
		// Only iterate if really there's some variable to be mapped
		if (!map.isEmpty()) {
			for (GWTFormElement formElement : formElementList) {
				if (formElement instanceof GWTTextArea) {
					GWTTextArea textArea = (GWTTextArea) formElement;

					if (!textArea.getData().equals("") && map.keySet().contains(textArea.getData())) {
						textArea.setValue(getStringValueFromVariable(map.get(textArea.getData())));
					}
				} else if (formElement instanceof GWTInput) {
					GWTInput input = (GWTInput) formElement;

					if (!input.getData().equals("") && map.keySet().contains(input.getData())) {
						Object var = map.get(input.getData());
						input.setValue(getStringValueFromVariable(var));

						if (input.getType().equals(GWTInput.TYPE_DATE)) {
							if (!"".equals(input.getValue())) {
								Date date = ISO8601.parseBasic(input.getValue());

								if (date != null) {
									input.setDate(date);
								} else {
									Log.warn("Input '" + input.getName() + "' value should be in ISO8601 format: "
											+ input.getValue());
								}
							}
						}
					}
				} else if (formElement instanceof GWTSuggestBox) {
					GWTSuggestBox suggestBox = (GWTSuggestBox) formElement;

					if (!suggestBox.getData().equals("") && map.keySet().contains(suggestBox.getData())) {
						suggestBox.setValue(getStringValueFromVariable(map.get(suggestBox.getData())));
						updateSuggestBoxTextValue(suggestBox);
					}
				} else if (formElement instanceof GWTCheckBox) {
					GWTCheckBox checkBox = (GWTCheckBox) formElement;

					if (!checkBox.getData().equals("") && map.keySet().contains(checkBox.getData())) {
						checkBox.setValue(getBooleanValueFromVariable(map.get(checkBox.getData())));
					}
				} else if (formElement instanceof GWTSelect) {
					GWTSelect select = (GWTSelect) formElement;

					if (!select.getData().equals("") && map.keySet().contains(select.getData())) {
						Collection<GWTOption> opts = getOptionsValueFromVariable(map.get(select.getData()), select.getOptions());
						select.setOptions(opts);
					}
				} else if (formElement instanceof GWTUpload) {
					GWTUpload upload = (GWTUpload) formElement;

					if (!upload.getData().equals("") && map.keySet().contains(upload.getData())) {
						GWTUpload uploadData = (GWTUpload) map.get(upload.getData());

						if (!uploadData.getDocumentName().equals("")) {
							upload.setDocumentName(uploadData.getDocumentName());
						}

						if (!uploadData.getDocumentUuid().equals("")) {
							upload.setDocumentUuid(uploadData.getDocumentUuid());
						}

						if (!uploadData.getFolderPath().equals("")) {
							upload.setFolderPath(uploadData.getFolderPath());
						}

						if (!uploadData.getFolderUuid().equals("")) {
							upload.setFolderUuid(uploadData.getFolderUuid());
						}

						if (uploadData.getValidators().size() > 0) {
							upload.setValidators(uploadData.getValidators());
						}
					}
				} else if (formElement instanceof GWTText) {
					GWTText text = (GWTText) formElement;
					if (!text.getData().equals("") && map.keySet().contains(text.getData())) {
						text.setLabel(getStringValueFromVariable(map.get(text.getData())));
					}
				} else if (formElement instanceof GWTSeparator) {
					// Nothing to be done here
				} else if (formElement instanceof GWTDownload) {
					GWTDownload download = (GWTDownload) formElement;
					if (!download.getData().equals("") && map.keySet().contains(download.getData())) {
						download.setNodes(getNodesValueFromVariable(map.get(download.getData())));
					}
				} else if (formElement instanceof GWTPrint) {
					GWTPrint print = (GWTPrint) formElement;
					if (!print.getData().equals("") && map.keySet().contains(print.getData())) {
						print.setNodes(getNodesValueFromVariable(map.get(print.getData())));
					}
				}
			}
		}
	}

	/**
	 * getNodesValueFromVariable
	 */
	private List<GWTNode> getNodesValueFromVariable(Object obj) {
		if (obj instanceof GWTInput) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTTextArea) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTSuggestBox) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTCheckBox) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTSelect) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTUpload) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTText) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTSeparator) {
			return new ArrayList<GWTNode>();
		} else if (obj instanceof GWTDownload) {
			GWTDownload download = (GWTDownload) obj;
			return download.getNodes();
		} else if (obj instanceof GWTPrint) {
			GWTPrint print = (GWTPrint) obj;
			return print.getNodes();
		} else {
			return new ArrayList<GWTNode>();
		}
	}

	/**
	 * getStringValueFromVariable
	 */
	private String getStringValueFromVariable(Object obj) {
		if (obj instanceof GWTInput) {
			return ((GWTInput) obj).getValue();
		} else if (obj instanceof GWTTextArea) {
			return ((GWTTextArea) obj).getValue();
		} else if (obj instanceof GWTSuggestBox) {
			return ((GWTSuggestBox) obj).getValue();
		} else if (obj instanceof GWTCheckBox) {
			return String.valueOf(((GWTCheckBox) obj).getValue());
		} else if (obj instanceof GWTSelect) {
			String values = "";
			GWTSelect select = (GWTSelect) obj;

			for (GWTOption option : select.getOptions()) {
				if (option.isSelected()) {
					if (values.length() > 0) {
						values += "," + option.getValue();
					} else {
						values += option.getValue();
					}
				}
			}

			return values;
		} else if (obj instanceof GWTUpload) {
			return null;
		} else if (obj instanceof GWTText) {
			return ((GWTText) obj).getLabel();
		} else if (obj instanceof GWTSeparator) {
			return null;
		} else if (obj instanceof GWTDownload) {
			return null;
		} else if (obj instanceof GWTPrint) {
			return null;
		} else {
			return null;
		}
	}

	/**
	 * getBooleanValueFromVariable
	 */
	private boolean getBooleanValueFromVariable(Object obj) {
		if (obj instanceof GWTInput) {
			return ((GWTInput) obj).getValue().toLowerCase().equals(BOOLEAN_TRUE);
		} else if (obj instanceof GWTTextArea) {
			return ((GWTTextArea) obj).getValue().toLowerCase().equals(BOOLEAN_TRUE);
		} else if (obj instanceof GWTSuggestBox) {
			return ((GWTSuggestBox) obj).getValue().toLowerCase().equals(BOOLEAN_TRUE);
		} else if (obj instanceof GWTCheckBox) {
			return ((GWTCheckBox) obj).getValue();
		} else if (obj instanceof GWTSelect) {
			String values = "";
			GWTSelect select = (GWTSelect) obj;

			for (GWTOption option : select.getOptions()) {
				if (option.isSelected()) {
					if (values.length() > 0) {
						values += "," + option.getValue();
					} else {
						values += option.getValue();
					}
				}
			}

			return values.toLowerCase().contains(BOOLEAN_TRUE); // test if on chain contains "true"
		} else if (obj instanceof GWTUpload) {
			return false;
		} else if (obj instanceof GWTText) {
			return false;
		} else if (obj instanceof GWTSeparator) {
			return false;
		} else if (obj instanceof GWTDownload) {
			return false;
		} else if (obj instanceof GWTPrint) {
			return false;
		} else {
			return false;
		}
	}

	/**
	 * getOptionsValueFromVariable
	 */
	private Collection<GWTOption> getOptionsValueFromVariable(Object obj, Collection<GWTOption> options) {
		for (GWTOption option : options) {
			if (obj instanceof GWTInput) {
				if (option.getValue().equals(((GWTInput) obj).getValue())) {
					option.setSelected(true);
					return options;
				}
			} else if (obj instanceof GWTTextArea) {
				if (option.getValue().equals(((GWTTextArea) obj).getValue())) {
					option.setSelected(true);
					return options;
				}
			} else if (obj instanceof GWTSuggestBox) {
				if (option.getValue().equals(((GWTSuggestBox) obj).getValue())) {
					option.setSelected(true);
					return options;
				}
			} else if (obj instanceof GWTCheckBox) {
				if (option.getValue().equals(String.valueOf(((GWTCheckBox) obj).getValue()))) {
					option.setSelected(true);
					return options;
				}
			} else if (obj instanceof GWTSelect) {
				// Only doing mapping between values, if not found then is false
				boolean found = false;
				GWTSelect select = (GWTSelect) obj;

				for (GWTOption optionVar : select.getOptions()) {
					if (option.getValue().equals(optionVar.getValue())) {
						found = optionVar.isSelected();
						break;
					}
				}

				option.setSelected(found); // always setting values, if not found
			} else if (obj instanceof GWTUpload) {
				return options;
			} else if (obj instanceof GWTText) {
				return options;
			} else if (obj instanceof GWTSeparator) {
				return options;
			} else if (obj instanceof GWTDownload) {
				return null;
			} else if (obj instanceof GWTPrint) {
				return null;
			} else {
				return options;
			}
		}

		return options;
	}

	/**
	 * getOptionsValueFromVariable
	 */
	private Collection<GWTOption> getOptionsFromVariable(Object obj) {
		if (obj instanceof GWTInput) {
			return null;
		} else if (obj instanceof GWTTextArea) {
			return null;
		} else if (obj instanceof GWTSuggestBox) {
			return null;
		} else if (obj instanceof GWTCheckBox) {
			return null;
		} else if (obj instanceof GWTSelect) {
			GWTSelect select = (GWTSelect) obj;
			return select.getOptions();
		} else if (obj instanceof GWTUpload) {
			return null;
		} else if (obj instanceof GWTText) {
			return null;
		} else if (obj instanceof GWTSeparator) {
			return null;
		} else if (obj instanceof GWTDownload) {
			return null;
		} else if (obj instanceof GWTPrint) {
			return null;
		} else {
			return null;
		}
	}

	/**
	 * Gets a string map values
	 */
	public Map<String, String> getStringMapValues() {
		Map<String, String> values = new HashMap<String, String>();

		for (GWTFormElement formElement : formElementList) {
			if (formElement instanceof GWTTextArea) {
				values.put(formElement.getName(), getStringValueFromVariable(formElement));
			} else if (formElement instanceof GWTInput) {
				if (((GWTInput) formElement).getType().equals(GWTInput.TYPE_DATE)) {
					GWTInput input = (GWTInput) formElement;
					String value = ISO8601.formatBasic(input.getDate());
					if (input.getDateTo() != null) {
						value += "," + ISO8601.formatBasic(input.getDateTo());
					} else {
						value += "," + value;
					}
					values.put(formElement.getName(), value);
				} else {
					values.put(formElement.getName(), getStringValueFromVariable(formElement));
				}
			} else if (formElement instanceof GWTSuggestBox) {
				values.put(formElement.getName(), getStringValueFromVariable(formElement));
			} else if (formElement instanceof GWTCheckBox) {
				values.put(formElement.getName(), getStringValueFromVariable(formElement));
			} else if (formElement instanceof GWTSelect) {
				values.put(formElement.getName(), getStringValueFromVariable(formElement));
			} else if (formElement instanceof GWTUpload) {
				// No aplicable to property groups
			} else if (formElement instanceof GWTText) {
				// Nothing to be done here
			} else if (formElement instanceof GWTSeparator) {
				// Nothing to be done here
			} else if (formElement instanceof GWTDownload) {
				// Nothing to be done here
			} else if (formElement instanceof GWTPrint) {
				// Nothing to be done here
			}
		}

		return values;
	}

	/**
	 * updateSuggestBoxTextValue
	 *
	 * @param suggestBox
	 */
	private void updateSuggestBoxTextValue(final GWTSuggestBox suggestBox) {
		List<String> tables = new ArrayList<String>();
		if (suggestBox.getTable() != null) {
			tables.add(suggestBox.getTable());
		}

		String formatedQuery = MessageFormat.format(suggestBox.getValueQuery(), suggestBox.getValue());
		keyValueService.getKeyValues(tables, formatedQuery, new AsyncCallback<List<GWTKeyValue>>() {
			@Override
			public void onSuccess(List<GWTKeyValue> result) {
				if (!result.isEmpty()) {
					GWTKeyValue keyValue = result.get(0);
					suggestBox.setText(keyValue.getValue());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getKeyValues", caught);
			}
		});
	}

	/**
	 *
	 */
	public void setSubmitFormButton(Button submitForm) {
		this.submitForm = submitForm;
	}

	/**
	 *
	 */
	public void setTaskInstance(GWTTaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	/**
	 *
	 */
	public ExtendedDefaultValidatorProcessor getValidationProcessor() {
		return validationProcessor;
	}

	/**
	 * DatabaseRecord
	 */
	class DatabaseRecord implements HasDatabaseRecord {
		private HTML keyWidget;
		private TextBox valueWidget;

		/**
		 * DatabaseRecord
		 */
		public DatabaseRecord(HTML keyWidget, TextBox valueWidget) {
			this.keyWidget = keyWidget;
			this.valueWidget = valueWidget;
		}

		@Override
		public void setKeyValue(GWTKeyValue keyValue) {
			keyWidget.setHTML(keyValue.getKey());
			valueWidget.setText(keyValue.getValue());
		}
	}

	/**
	 * ButtonValidation
	 */
	public class ValidationButton {
		private GWTButton gWTButton;
		private FormManager formManager;

		/**
		 * ValidationButton
		 */
		public ValidationButton(GWTButton gWTButton, FormManager formManager) {
			this.gWTButton = gWTButton;
			this.formManager = formManager;
		}

		/**
		 *
		 */
		public HasWorkflow getWorkflow() {
			return formManager.workflow;
		}

		/**
		 *
		 */
		public GWTButton getButton() {
			return gWTButton;
		}

		/**
		 *
		 */
		public ValidationProcessor getValidationProcessor() {
			return formManager.validationProcessor;
		}

		/**
		 *
		 */
		public GWTTaskInstance getTaskInstance() {
			return taskInstance;
		}

		/**
		 *
		 */
		public void disableAllButtonList() {
			formManager.disableAllButtonList();
		}
	}
}